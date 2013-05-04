package ru.prolib.aquila.quik.subsys.order;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.quik.api.ApiServiceException;
import ru.prolib.aquila.quik.dde.OrderCache;
import ru.prolib.aquila.quik.dde.TradeCache;
import ru.prolib.aquila.quik.dde.TradesCache;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Обработчик заявок.
 * <p>
 * Генерирует строки транзакций в соответствии с протоколом обмена данными
 * TRANS2QUIK. Выставляет транзакции, связанные с заявками и стоп-заявками.
 * <p>
 * 2013-01-24<br>
 * $Id: QUIKOrderProcessor.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class QUIKOrderProcessor implements OrderProcessor {
	private final QUIKServiceLocator locator;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 */
	public QUIKOrderProcessor(QUIKServiceLocator locator) {
		super();
		this.locator = locator;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return  locator;
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		if ( order.getStatus() != OrderStatus.ACTIVE ) {
			return;
		}
		OrderType type = order.getType();
		long orderId = order.getId();
		EditableTerminal terminal = locator.getTerminal();
		EditableOrders orders = null;
		String trspec = null;
		if ( type == OrderType.STOP_LIMIT
			|| type == OrderType.TAKE_PROFIT
			|| type == OrderType.TAKE_PROFIT_AND_STOP_LIMIT )
		{
			orders = terminal.getStopOrdersInstance();
			trspec = "ACTION=KILL_STOP_ORDER; STOP_ORDER_KEY=" + orderId;
		} else {
			orders = terminal.getOrdersInstance();
			trspec = "ACTION=KILL_ORDER; ORDER_KEY=" + orderId;
		}
		long transId = locator.getTransactionNumerator().incrementAndGet();
		locator.getApi().OnTransReply(transId)
			.addListener(new CancelOrderHandler(locator, orders, orderId));
		try {
			send("TRANS_ID=" + transId + "; "
				+ "CLASSCODE=" + order.getSecurity().getClassCode() + "; "
				+ trspec);
		} catch ( SecurityException e ) {
			throw new OrderException(e);
		}
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
		if ( order.getStatus() != OrderStatus.PENDING ) {
			throw new OrderException("Order not pending: " + order.getStatus());
		}
		OrderType type = order.getType();
		Long qty = order.getQty();
		EditableTerminal terminal = locator.getTerminal();
		EditableOrders orders;
		String str;
		if ( type == OrderType.MARKET ) {
			orders = terminal.getOrdersInstance();
			str = newOrderTrPrefix(order)
				+ "ACTION=NEW_ORDER; "
				+ "TYPE=M; PRICE=0; QUANTITY=" + qty;

		} else if ( type == OrderType.LIMIT ) {
			orders = terminal.getOrdersInstance();
			str = newOrderTrPrefix(order)
				+ "ACTION=NEW_ORDER; "
				+ "TYPE=L; "
				+ "PRICE=" + formatPrice(order, order.getPrice()) + "; "
				+ "QUANTITY=" + qty;
			
		} else if ( type == OrderType.STOP_LIMIT ) {
			orders = terminal.getStopOrdersInstance();
			str = newOrderTrPrefix(order)
				+ "ACTION=NEW_STOP_ORDER; "
				+ "STOPPRICE=" +
					formatPrice(order, order.getStopLimitPrice()) + "; "
				+ "PRICE=" + formatPrice(order, order.getPrice()) + "; "
				+ "QUANTITY=" + qty;
		} else {
			throw new QUIKOrderTypeUnsupportedException(type);
		}
		locator.getApi().OnTransReply(order.getTransactionId())
			.addListener(new PlaceOrderHandler(locator, orders));
		send(str);
	}
	
	/**
	 * Форматировать цену в соответствии с параметрами инструмента заявки.
	 * <p> 
	 * @param order заявка
	 * @param price значение цены
	 * @return строковое представление цены
	 * @throws OrderException
	 */
	private String formatPrice(Order order,double price) throws OrderException {
		try {
			return order.getSecurity().shrinkPrice(price);
		} catch ( SecurityException e ) {
			throw new OrderException(e);
		}
	}
	
	/**
	 * Сформировать префикс транзакции новой заявки.
	 * <p>
	 * Префикс содержит общие для всех заявок атрибуты транзакции.
	 * <p>
	 * @param order заявка
	 * @return префикс транзакции
	 */
	private String newOrderTrPrefix(Order order) {
		Account account = order.getAccount();
		SecurityDescriptor secDescr = order.getSecurityDescriptor();
		return "TRANS_ID=" + order.getTransactionId() + "; "
			//+ "FIRM_ID=" + account.getCode() + "; "
			+ "CLIENT_CODE=" + account.getSubCode() + "; "
			+ "ACCOUNT=" + (account.getSubCode2() == null
				? account.getSubCode() : account.getSubCode2()) + "; "
			+ "CLASSCODE=" + secDescr.getClassCode() + "; "
			+ "SECCODE=" + secDescr.getCode() + "; "
			+ "OPERATION=" + (order.getDirection() == OrderDirection.BUY
					? "B" : "S") + "; ";
	}
	
	/**
	 * Отправить транзакцию в QUIK-терминал.
	 * <p>
	 * @param spec спецификация транзакции
	 * @throws OrderException нефатальное (non panic) исключение
	 */
	private void send(String spec) throws OrderException {
		try {
			locator.getApi().send(spec);
		} catch ( ApiServiceException e ) {
			// А это рядовая ошибка. Скорее всего связана с отсутствием
			// подключения к QUIK терминалу. Проверять надо, прежде чем
			// транзакции отправлять.
			throw new OrderException(e);
		}
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == QUIKOrderProcessor.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		QUIKOrderProcessor o = (QUIKOrderProcessor) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.isEquals();
	}
	
	/*
	public void adjustOrders() {
		EditableOrders orders = locator.getTerminal().getOrdersInstance();
		for ( Order o : orders.getOrders() ) {
			EditableOrder order = (EditableOrder) o;
			if ( order.getStatus() != OrderStatus.ACTIVE ) {
				continue;
			}
			OrderCache orderCache = locator.getDdeCache()
				.getOrdersCache()
				.get(order.getId());
			for ( Trade trade : getUnaccountedTrades(order) ) {
				order.addTrade(trade);
				order.fireChangedEvent();
				order.resetChanges();
				order.fireTradeEvent(trade);
			}
			if ( order.getQtyRest() <= 0 ) {
				order.setStatus(OrderStatus.FILLED);
				order.fireChangedEvent();
				order.resetChanges();
			} else if ( orderCache.getStatus() == OrderStatus.CANCELLED
					&& order.getQtyRest() == orderCache.getQtyRest() )
			{
				// Проверить совпадение исполненного кол-ва по сделкам
				// (то есть теперь по заявке) со значением в кэше заявки.
				// Есоли совпадают, значит учтены все сделки. Если нет, значит
				// какие то сделки по заявке еще не закешированы и нужно ждать
				// следующих обновлений.
				order.setStatus(OrderStatus.CANCELLED);
				order.fireChangedEvent();
				order.resetChanges();
			}
		}
		for ( OrderCache o : locator.getDdeCache().getOrdersCache().getAll() ) {
			if ( orders.isOrderExists(o.getId()) ) {
				continue;
			}
			
			
		}
	}
	
	private List<Trade> getUnaccountedTrades(Order order) {
		List<Trade> newTrades = new Vector<Trade>();
		for ( TradeCache cache : locator.getDdeCache().getTradesCache()
				.getAllByOrderId(order.getId()) )
		{
			if ( ! order.hasTrade(cache.getId()) ) {
				Trade trade = new Trade(order.getTerminal());
				trade.setDirection(order.getDirection());
				trade.setId(cache.getId());
				trade.setOrderId(cache.getOrderId());
				trade.setPrice(cache.getPrice());
				trade.setQty(cache.getQty());
				trade.setSecurityDescriptor(order.getSecurityDescriptor());
				trade.setTime(cache.getTime());
				trade.setVolume(cache.getVolume());
				newTrades.add(trade);
			}
		}
		Collections.sort(newTrades);
		return newTrades;
	}
	*/

}
