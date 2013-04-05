package ru.prolib.aquila.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.util.Sequence;
import ru.prolib.aquila.util.SequenceLong;

/**
 * Реализация хранилища заявок для тестовых прогонов.
 * Автоматически исполняет заявки на основе данных о торгах.
 * Обрабатывает события от источника данных о торгах и заявок, взятых под
 * наблюдение. Генерирует событие при взятие на контроль новой заявки.
 */
public class TestPortfolioOrders extends Observable
	implements PortfolioOrders,Observer
{
	private static final Logger logger = LoggerFactory.getLogger(TestPortfolioOrders.class);
	private final TestPortfolioOrdersChecker checkerPeriod;
	private final TestPortfolioOrdersChecker checkerMoment;
	private final Sequence<Long> id;
	protected final HashMap<Long, Order> orders;
	protected final HashMap<Long, Order> stopOrders;
	protected MarketData data = null;
	
	public TestPortfolioOrders() {
		this(new TestPortfolioOrdersCheckerPeriod(),
			 new TestPortfolioOrdersCheckerMoment(), new SequenceLong());
		logger.warn("Created own id sequence");
	}
	
	public TestPortfolioOrders(Sequence<Long> id) {
		this(new TestPortfolioOrdersCheckerPeriod(),
			 new TestPortfolioOrdersCheckerMoment(), id);
	}
	
	public TestPortfolioOrders(TestPortfolioOrdersChecker checkerPeriod,
							   TestPortfolioOrdersChecker checkerMoment,
							   Sequence<Long> id)
	{
		super();
		this.id = id;
		this.checkerPeriod = checkerPeriod;
		this.checkerMoment = checkerMoment;
		orders = new HashMap<Long, Order>();
		stopOrders = new HashMap<Long, Order>();
	}
	
	public Sequence<Long> getIdSeq() {
		return id;
	}
	
	public TestPortfolioOrdersChecker getCheckerPeriod() {
		return checkerPeriod;
	}
	
	public TestPortfolioOrdersChecker getCheckerMoment() {
		return checkerMoment;
	}
	
	/**
	 * Начать обслуживание
	 * 
	 * Вызов этого метода определяет источник данных и выполняет подписку
	 * на уведомления от источника данных. Вызов завершается провалом, если
	 * сервис уже начал обслуживание.
	 * 
	 * @param data источник данных
	 * @throws PortfolioException источник данных уже определен
	 */
	public synchronized void startService(MarketData data)
		throws PortfolioException
	{
		if ( this.data != null ) {
			throw new PortfolioException("Service already started");
		}
		this.data = data;
		data.addObserver(this);
	}
	
	/**
	 * Прекратить обслуживание
	 * 
	 * Этот метод отменяет подписку на уведомления источника данных и
	 * сбрасывает его. Если на момент вызова источник не был указан, то ничего
	 * не происходит. 
	 * 
	 * @throws PortfolioException
	 */
	public synchronized void stopService() throws PortfolioException {
		if ( data != null ) {
			data.deleteObserver(this);
			data = null;
		}
	}
	
	/**
	 * Условно-ожидать исполнения заявки
	 * 
	 * Так как тестовый портфель не подразумевает поступление нужных данных
	 * в других потоках, то реальное ожидание здесь смысла не имеет. Заявка
	 * не может быть исполнена по информации из другого потока, так как работа
	 * в режиме тестирования выполняется строго последовательно: получение
	 * порции данных, обработка полученных данных. Для того, что бы обеспечить
	 * наибольшую совместимость с боевыми компонентами, но при этом исключить
	 * многопоточные ошибки в режиме тестирования, данный метод просто проверяет
	 * текущий статус заявки. Если она не исполнена и не снята то без всякого
	 * ожидания будет выброшено исключение {@link PortfolioTimeoutException}.
	 * 
	 * @throws PortfolioTimeoutException заявка не исполнена
	 */
	@Override
	public void waitForComplete(Order order, long timeout)
		throws PortfolioTimeoutException, PortfolioException,
		InterruptedException
	{
		switch ( order.getStatus() ) {
		case Order.FILLED:
		case Order.KILLED:
			return;
		default:
			throw new PortfolioTimeoutException();
		}
	}

	/**
	 * Начать наблюдение за заявкой
	 */
	@Override
	public synchronized void startWatch(Order order) {
		if ( order.isStopOrder() ) {
			stopOrders.put(order.getId(), order);
			logger.debug("Start watch stop-order: {}", order);
		} else {
			orders.put(order.getId(), order);
			logger.debug("Start watch order: {}", order);
		}
		
		order.addObserver(this);
		setChanged();
		notifyObservers(order);
		checkOrder(checkerMoment, order);
	}
	
	/**
	 * Проверить заявку
	 * 
	 * Проверяет заявку на необходимость исполнения или удаления из 
	 * соответствующего списка наблюдения.
	 * 
	 * @param checker контроллер проверки условий
	 * @param order проверяемая заявка
	 */
	protected void checkOrder(TestPortfolioOrdersChecker checker, Order order) {
		int status = order.getStatus();
		switch ( status ) {
		case Order.FILLED:
		case Order.KILLED:
			stopWatch(order);
			break;
		default:
			if ( checker.canFill(order, data) ) {
				fillOrder(order);
			}
		}
	}
	
	/**
	 * Обработать поступление новых данных
	 * 
	 * Данный метод обрабатывает события двух типов: события от источника
	 * данных, свидетельствующее о появлении нового бара, и события об
	 * исполнении или снятии заявки.
	 * 
	 * @param o источник уведомления
	 * @param arg параметр не используется
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( o == data ) {
			List<Order> list = new LinkedList<Order>(stopOrders.values());
			Iterator<Order> i = list.iterator();
			if ( list.size() > 0 ) {
				logger.debug("Total check {} stop-orders", list.size());
			}
			while ( i.hasNext() ) {
				checkOrder(checkerPeriod, i.next());
			}
			
			list = new LinkedList<Order>(orders.values());
			i = list.iterator();
			if ( list.size() > 0 ) {
				logger.debug("Total check {} orders", list.size());
			}
			while ( i.hasNext() ) {
				checkOrder(checkerPeriod, i.next());
			}
		} else if ( o instanceof Order ) {
			stopWatch((Order)o);
		} else {
			logger.warn("Unexpected observable instance: {}", o.getClass());
		}
	}

	@Override
	public synchronized Set<Order> getActiveOrders() {
		Set<Order> active = new HashSet<Order>(stopOrders.values());
		active.addAll(orders.values());
		return active;
	}
	
	/**
	 * Обработать завершенную заявку.
	 * 
	 * Снимает с контроля указанную заявку.
	 * 
	 * @param order
	 */
	protected void stopWatch(Order order) {
		if ( order.isStopOrder() ) {
			stopOrders.remove(order.getId());
			logger.debug("Remove stop-order from watch-list: {}", order);
		} else {
			orders.remove(order.getId());
			logger.debug("Remove order from watch-list: {}", order);
		}
		order.deleteObserver(this);
	}
	
	/**
	 * Исполнить заявку.
	 * 
	 * @param order
	 */
	protected void fillOrder(Order order) {
		try {
			if ( order.isStopOrder() ) {
				logger.debug("Fill stop-order: {}", order);
				startWatch(order.fill(id.next()));
			} else {
				logger.debug("Fill order: {}", order);
				order.fill();
			}
		} catch ( OrderException e ) {
			logger.error("Cannot fill order: {}", order, e);
		}
	}

}
