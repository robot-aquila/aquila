package ru.prolib.aquila.ta.ds.quik;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.rxltdde.Xlt.ITable;
import ru.prolib.aquila.util.FixedMap;

/**
 * Проблема: данные из таблиц QUIK-а могут поступать до того как локальный
 * объект заявки создан. Данные из таблицы заявок могут поступить ранее,
 * чем данные из таблицы стоп-заявок. Это не позволяет разработать систему
 * основанную на сопоставлении входных данных с локальными заявками, так как
 * старт слежения может произойти после того как данные по отслеживаемой
 * заявке уже получены. 
 * 
 * Решение: информация из соответствующих таблиц QUIK-а кешируется сразу
 * при поступлении данных. После кеширования просматривается список
 * наблюдаемых заявок: если есть заявка, то она обновляется. При добавлении
 * заявки в список наблюдения сразу просматривается кеш.
 * 
 */
public class PortfolioOrdersQuik extends Observable
	implements PortfolioOrders,RXltDdeTableHandler
{
	final static Logger logger = LoggerFactory.getLogger(PortfolioOrdersQuik.class);
	
	static final String ACTIVE = "Активна";
	static final String FILLED = "Исполнена";
	static final String KILLED = "Снята";
	
	/**
	 * Идентификатор таблицы Таблица заявок (фьючерсы)
	 * Таблица используется для определения статуса заявок.
	 */
	static final String ordersTopic = "[export]orders";
	
	/**
	 * Идентификатор таблицы Таблица стоп-заявок
	 * Таблица используется для определения статуса заявок.
	 */
	static final String stopOrdersTopic = "[export]stop-orders";

	
	/**
	 * Структура хранения информации о рыночной или лимитной заявке.
	 */
	public static class OrderEntry {
		
		public OrderEntry(int initialStatus) {
			status = initialStatus;
		}
		
		int status; // текущий статус
	}
	
	/**
	 * Структура хранения информации о стоп-заявке.
	 */
	public static class StopOrderEntry {
		
		public StopOrderEntry(int initialStatus, long initialRelatedId) {
			status = initialStatus;
			relatedId = initialRelatedId;
		}
		
		int		status; // текущий статус
		long	relatedId; // номер связанной заявки
	}

	protected final HashMap<Long, Order> watchOrders;
	protected final HashMap<Long, Order> watchStopOrders;
	protected final FixedMap<Long, OrderEntry> cacheOrders;
	protected final FixedMap<Long, StopOrderEntry> cacheStopOrders;
	
	public PortfolioOrdersQuik() {
		this(1024);
	}
	
	public PortfolioOrdersQuik(int maximumCapacity) {
		super();
		watchOrders = new HashMap<Long, Order>();
		watchStopOrders = new HashMap<Long, Order>();
		cacheOrders = new FixedMap<Long, OrderEntry>(maximumCapacity);
		cacheStopOrders = new FixedMap<Long, StopOrderEntry>(maximumCapacity);
	}
	
	/**
	 * Зарегистрировать обработчики таблиц.
	 * 
	 * @param dispatcher
	 */
	public void registerHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.add(ordersTopic, this);
		dispatcher.add(stopOrdersTopic, this);
	}
	
	/**
	 * Удалить обработчики таблиц.
	 * 
	 * @param dispatcher
	 */
	public void unregisterHandler(RXltDdeDispatcher dispatcher) {
		dispatcher.remove(ordersTopic, this);
		dispatcher.remove(stopOrdersTopic, this);
	}
	
	@Override
	public void waitForComplete(Order order, long timeout)
		throws PortfolioTimeoutException,
			   PortfolioException,
			   InterruptedException
	{
		synchronized ( order ) {
			while ( (order.getStatus() == Order.ACTIVE
				  || order.getStatus() == Order.PENDING)
				 	&& timeout > 0 )
			{
				long start = System.currentTimeMillis();
				order.wait(timeout);
				timeout -= (System.currentTimeMillis() - start);
				if ( timeout <= 0 ) {
					throw new PortfolioTimeoutException("Order " + order
							+ " incomplete " + timeout + " ms");
				}
			}
		}
	}
	
	@Override
	public void onTable(ITable table) {
		String topic = table.getTopic();
		if ( topic.equals(ordersTopic) ) {
			// изменение статуса заявки
			// требуются колонки
			// 		Номер,
			// 		Состояние
			long id = 0;
			int status = 0;
			for ( int row = 0; row < table.getRows(); row ++ ) {
				try {
					id = ((Double)table.getCell(row, 0)).longValue();
					status = status2Int((String)table.getCell(row, 1));
					updateOrder(id, status);
				} catch ( OrderException e ) {
					logger.error("Error update order. Ignore row {}", row, e);
				}
			}
			
		} else
		if ( topic.equals(stopOrdersTopic) ) {
			// изменение статуса стоп-заявки
			// требуются колонки
			// 		Номер,
			// 		Состояние,
			// 		Номер заявки
			long id = 0,relId = 0;
			int status = 0;
			for ( int row = 0; row < table.getRows(); row ++ ) {
				try {
					id = ((Double)table.getCell(row, 0)).longValue();
					status = status2Int((String)table.getCell(row, 1));
					relId = ((Double)table.getCell(row, 2)).longValue(); 
					updateStopOrder(id, status, relId);
				} catch ( OrderException e ) {
					logger.error("Error update stop-order. Ignore row {}",
							row, e);
				}
			}
			
		} else {
			logger.warn("Unknown table: {}", topic);
		}
	}
	
	@Override
	public synchronized void startWatch(Order order) throws PortfolioException {
		try {
			if ( order.isStopOrder() ) {
				watchStopOrders.put(order.getId(), order);
				logger.debug("Start watch stop-order: {}", order);
				setChanged();
				notifyObservers(order);
				checkStopOrder(order);
			} else {
				watchOrders.put(order.getId(), order);
				logger.debug("Start watch order: {}", order);
				setChanged();
				notifyObservers(order);
				checkOrder(order);
			}
		} catch ( OrderException e ) {
			throw new PortfolioException(e.getMessage(), e);
		}
	}
	
	protected synchronized void updateOrder(long id, int status)
			throws OrderException
	{
		OrderEntry cache = cacheOrders.get(id);
		if ( cache == null ) {
			logger.debug("New cache entry added for order #{}", id);
			cache = new OrderEntry(status);
			cacheOrders.put(id, cache);
			checkOrder(id);
		} else if ( cache.status != status ) {
			logger.debug("Order #{} status changed", id);
			cache.status = status;
			checkOrder(id);
		}
	}
	
	protected synchronized void updateStopOrder(long id, int status,
			long relatedId) throws OrderException
	{

		StopOrderEntry cache = cacheStopOrders.get(id);
		if ( cache == null ) {
			logger.debug("New cache entry added for stop-order #{}", id);
			cache = new StopOrderEntry(status, relatedId);
			cacheStopOrders.put(id, cache);
			checkStopOrder(id);
		} else if ( cache.status != status) {
			logger.debug("Stop-order #{} status changed", id);
			cache.status = status;
			cache.relatedId = relatedId;
			checkStopOrder(id);
		}
	}
	
	/**
	 * Проверить заявку по экземпляру.
	 *
	 * Сопоставляет данные локального кеша с указанной заявкой.
	 * Исполненные и снятые заявки удаляются из списка наблюдения.
	 * 
	 * @param order
	 * @throws OrderException 
	 */
	protected void checkOrder(Order order) throws OrderException {
		OrderEntry cache = cacheOrders.get(order.getId());
		if ( cache == null ) {
			return;
		}
		// Имеются какие-то данные, значит заявка как минимум активна
		if ( order.getStatus() == Order.PENDING ) {
			order.activate();
		}
		
		switch ( cache.status ) {
		case Order.ACTIVE:
			break;
		case Order.FILLED:
		case Order.KILLED:
			watchOrders.remove(order.getId());
			cacheOrders.remove(order.getId());
			// выполняем после удаления, что бы потенциальное
			// исключение не помешало очистке хранилища
			if ( order.getStatus() == Order.ACTIVE ) {
				if ( cache.status == Order.FILLED ) {
					logger.debug("Order filled: {}", order);
					order.fill();
				} else {
					logger.debug("Order killed: {}", order);
					order.kill();
				}
			}
			break;
		}
	}
	
	/**
	 * Проверить стоп-заявку по экземпляру.
	 * 
	 * Сопоставляет данные локального кеша с указанной стоп-заявкой.
	 * Исполненные и снятые заявки удаляются из списка наблюдения.
	 * 
	 * @param order
	 */
	protected void checkStopOrder(Order order) throws OrderException {
		StopOrderEntry cache = cacheStopOrders.get(order.getId());
		if ( cache == null ) {
			return;
		}
		// Имеются какие-то данные, значит заявка как минимум активна
		if ( order.getStatus() == Order.PENDING ) {
			order.activate();
		}
		
		switch ( cache.status ) {
		case Order.ACTIVE:
			break;
		case Order.FILLED:
		case Order.KILLED:
			watchStopOrders.remove(order.getId());
			cacheStopOrders.remove(order.getId());
			// выполняем после удаления, что бы потенциальное
			// исключение не помешало очистке хранилища
			if ( order.getStatus() == Order.ACTIVE ) {
				if ( cache.status == Order.FILLED ) {
					logger.debug("Stop-order filled: {}", order);
					order = order.fill(cache.relatedId);
					watchOrders.put(cache.relatedId, order);
					// возможно связанная заявка уже изменена
					checkOrder(order);
				} else {
					logger.debug("Stop-order killed: {}", order);
					order.kill();
				}
			}
			break;
		}
	}
	
	/**
	 * Проверить заявку по номеру.
	 * @param id
	 * @throws OrderException
	 */
	protected void checkOrder(long id) throws OrderException {
		Order order = watchOrders.get(id);
		if ( order != null ) {
			checkOrder(order);
		}
	}

	/**
	 * Проверить стоп-заявку по номеру.
	 * @param id
	 * @throws OrderException
	 */
	protected void checkStopOrder(long id) throws OrderException {
		Order order = watchStopOrders.get(id);
		if ( order != null ) {
			checkStopOrder(order);
		}
	}
	
	/**
	 * Конвертировать строковый статус заявки QUIK в константу класса Order. 
	 * @param str
	 * @return
	 * @throws OrderException
	 */
	private final int status2Int(String str) throws OrderException {
		if ( str.equals(ACTIVE) ) {
			return Order.ACTIVE;
		} else
		if ( str.equals(FILLED) ) {
			return Order.FILLED;
		} else
		if ( str.equals(KILLED) ) {
			return Order.KILLED;
		} else {
			throw new OrderException("Unknown status: " + str);
		}
	}

	@Override
	public synchronized Set<Order> getActiveOrders() {
		Set<Order> active = new HashSet<Order>(watchStopOrders.values());
		active.addAll(watchOrders.values());
		return active;
	}
	
}