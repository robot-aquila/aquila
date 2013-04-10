package ru.prolib.aquila.quik.subsys.order;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.quik.api.ApiServiceException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Обработчик заявок.
 * <p>
 * Генерирует строки транзакций в соответствии с протоколом обмена данными
 * TRANS2QUIK. Отслеживает транзакции, связанные с заявками и стоп-заявками.
 * При возникновении ошибок в связи с вышеназванными транзакциями, выставляет
 * для заявки или стоп-заявки (при условии, что этим транзакциям соответствуют
 * ожидающие заявки) статус <b>ошибка регистрации</b> и переводит ее в список
 * зарегистрированных заявок. При этом, в качестве номера заявки используется
 * последовательность отрицательных целочисленных значений, для получения
 * которых используется дополнительный нумератор.
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
		boolean isStop = false;
		OrderType type = order.getType();
		if ( type == OrderType.STOP_LIMIT || type == OrderType.TAKE_PROFIT
			|| type == OrderType.TAKE_PROFIT_AND_STOP_LIMIT )
		{
			isStop = true;
		}
		try {
			send("TRANS_ID="
				+ locator.getTransactionNumerator().incrementAndGet() + "; "
				+ "CLASSCODE=" + order.getSecurity().getClassCode() + "; "
				+ "ACTION=" + (isStop ? "KILL_STOP_ORDER" : "KILL_ORDER") + "; "
				+ (isStop ? "STOP_" : "") + "ORDER_KEY=" + order.getId());
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

}
