package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactory;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * Конструктор локальных заявок.
 * <p>
 * Данный класс создает заявки стандартных типов, предназначенные для отправки
 * через платежный терминал. Все созданные заявки регистрируются как ожидаемые
 * и могут быть получены из хранилища заявок по номеру транзакции. 
 * <p>
 * 2012-12-10<br>
 * $Id: OrderBuilderImpl.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class OrderBuilderImpl implements OrderBuilder {
	private final OrderFactory factory;
	private final EditableTerminal terminal;
	private final Counter transId;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param factory фабрика экземпляров заявок
	 * @param terminal терминал
	 * @param transId нумератор транзакций
	 */
	public OrderBuilderImpl(OrderFactory factory,
			EditableTerminal terminal, Counter transId)
	{
		super();
		this.factory = factory;
		this.terminal = terminal;
		this.transId = transId;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Данный конструктор предусматривает создание дефолтного нумератора
	 * транзакций типа {@link ru.prolib.aquila.core.utils.SimpleCounter
	 * SimpleCounter}.
	 * <p>
	 * @param factory фабрика экземпляров заявок
	 * @param terminal терминал
	 */
	public OrderBuilderImpl(OrderFactory factory, EditableTerminal terminal) {
		this(factory, terminal, new SimpleCounter(0));
	}
	
	/**
	 * Получить нумератор транзакций.
	 * <p>
	 * @return нумератор транзакций
	 */
	public Counter getTransIdCounter() {
		return transId;
	}
	
	/**
	 * Получить фабрику заявок.
	 * <p>
	 * @return фабрика заявок
	 */
	public OrderFactory getOrderFactory() {
		return factory;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить текущий номер транзакции.
	 * <p>
	 * @return номер транзакции
	 */
	public synchronized long getTransId() {
		return transId.get();
	}

	@Override
	public synchronized Order
			createMarketOrderB(Account account, Security sec, long qty)
					throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.BUY);
		fillMarketOrder(order, account, sec, qty);
		return order;
	}

	@Override
	public synchronized Order
			createMarketOrderS(Account account, Security sec, long qty)
					throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.SELL);
		fillMarketOrder(order, account, sec, qty);
		return order;
	}
	
	@Override
	public synchronized Order createLimitOrderB(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.BUY);
		fillLimitOrder(order, account, sec, price, qty);
		return order;
	}

	@Override
	public synchronized Order createLimitOrderS(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.SELL);
		fillLimitOrder(order, account, sec, price, qty);
		return order;
	}

	@Override
	public synchronized Order createStopLimitB(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.BUY);
		fillStopLimit(order, account, sec, stopPrice, price, qty);
		return order;
	}

	@Override
	public synchronized Order createStopLimitS(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		EditableOrder order = factory.createOrder();
		order.setDirection(OrderDirection.SELL);
		fillStopLimit(order, account, sec, stopPrice, price, qty);
		return order;
	}
	
	private void fillStopLimit(EditableOrder order, Account account,
			Security sec, double stopPrice, double price, long qty)
		throws OrderException
	{
		fillCommonOrder(order, account, sec, qty);
		order.setType(OrderType.STOP_LIMIT);
		order.setPrice(price);
		order.setStopLimitPrice(stopPrice);
		terminal.registerPendingStopOrder(order);
	}
	
	private void fillLimitOrder(EditableOrder order, Account account,
			Security sec, double price, long qty) throws OrderException
	{
		fillCommonOrder(order, account, sec, qty);
		order.setType(OrderType.LIMIT);
		order.setQtyRest(qty);
		order.setPrice(price);
		terminal.registerPendingOrder(order);
	}
	
	private void fillMarketOrder(EditableOrder order, Account account,
			Security sec, long qty) throws OrderException
	{
		fillCommonOrder(order, account, sec, qty);
		order.setType(OrderType.MARKET);
		order.setQtyRest(qty);
		terminal.registerPendingOrder(order);
	}
	
	private void fillCommonOrder(EditableOrder order, Account account,
			Security sec, long qty) throws OrderException
	{
		order.setAccount(account);
		order.setTransactionId((long) transId.incrementAndGet());
		order.setQty(qty);
		order.setSecurityDescriptor(sec.getDescriptor());
		order.setStatus(OrderStatus.PENDING);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderBuilderImpl.class ?
				fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		OrderBuilderImpl o = (OrderBuilderImpl) other;
		return new EqualsBuilder()
			.append(factory, o.factory)
			.append(terminal, o.terminal)
			.append(transId, o.transId)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121211, 43719)
			.append(factory)
			.append(terminal)
			.append(transId)
			.toHashCode();
	}

}
