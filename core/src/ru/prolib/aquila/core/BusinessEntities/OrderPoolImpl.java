package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.utils.OrderPoolBase;

/**
 * Имплементация пула заявок.
 */
public class OrderPoolImpl implements OrderPool {
	private final Terminal terminal;
	private final OrderPoolBase base;
	private Account account;
	private Security security;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param base основание пула
	 */
	OrderPoolImpl(Terminal terminal, OrderPoolBase base)
	{
		super();
		this.terminal = terminal;
		this.base = base;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public OrderPoolImpl(Terminal terminal) {
		this(terminal, new OrderPoolBase());
	}
	
	@Override
	public void placeOrders() throws OrderException {
		base.placeOrders();
	}
	
	@Override
	public void cancelOrders() {
		base.cancelOrders();
	}
	
	@Override
	public synchronized void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public synchronized void setSecurity(Security security) {
		this.security = security;
	}
	
	@Override
	public synchronized Account getAccount() {
		return account;
	}
	
	@Override
	public synchronized Security getSecurity() {
		return security;
	}
	
	private final void testDefaultAccount() {
		if ( account == null ) {
			throw new IllegalStateException("Default account unspecified");
		}		
	}
	
	private final void testDefaultSecurity() {
		if ( security == null ) {
			throw new IllegalStateException("Default security unspecified");
		}
	}
	
	@Override
	public Order buy(long qty, double price) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price));
	}
	
	@Override
	public Order sell(long qty, double price) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price));
	}
	
	@Override
	public Order buy(long qty, double price, double stop) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order sell(long qty, double price, double stop) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order buy(Security security, long qty, double price) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price));
	}
	
	@Override
	public Order sell(Security security, long qty, double price) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price));
	}
	
	@Override
	public Order buy(Security security, long qty, double price, double stop) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order sell(Security security, long qty, double price, double stop) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order buy(Account account, Security security, long qty,
			double price)
	{
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price));
	}
	
	@Override
	public Order sell(Account account, Security security, long qty,
			double price)
	{
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price));
	}
	
	@Override
	public Order buy(Account account, Security security, long qty,
			double price, double stop)
	{
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order sell(Account account, Security security, long qty,
			double price, double stop)
	{
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty, price, new StopOrderActivator(stop)));
	}
	
	@Override
	public Order add(Order order) {
		return base.add(order);
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public Order buy(long qty) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty));
	}

	@Override
	public Order sell(long qty) {
		testDefaultAccount();
		testDefaultSecurity();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty));
	}

	@Override
	public boolean isPooled(Order order) {
		return base.isPooled(order);
	}

	@Override
	public boolean isPending(Order order) {
		return base.isPending(order);
	}

	@Override
	public boolean isActive(Order order) {
		return base.isActive(order);
	}

	@Override
	public boolean isDone(Order order) {
		return base.isDone(order);
	}

	@Override
	public Order buy(Security security, long qty) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty));
	}

	@Override
	public Order sell(Security security, long qty) {
		testDefaultAccount();
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty));
	}

	@Override
	public Order buy(Account account, Security security, long qty) {
		return base.add(terminal.createOrder(account, Direction.BUY, security,
				qty));
	}

	@Override
	public Order sell(Account account, Security security, long qty) {
		return base.add(terminal.createOrder(account, Direction.SELL, security,
				qty));
	}
	
	/**
	 * Получить основание пула.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return основание
	 */
	OrderPoolBase getPoolBase() {
		return base;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() !=  OrderPoolImpl.class ) {
			return false;
		}
		OrderPoolImpl o = (OrderPoolImpl) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.account, account)
			.append(o.base, base)
			.append(o.security, security)
			.isEquals();
	}

	@Override
	public Order buy(long qty, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder)
			terminal.createOrder(account, Direction.BUY, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(long qty, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder)
			terminal.createOrder(account, Direction.SELL, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(long qty, double price, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder)
			terminal.createOrder(account, Direction.BUY, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(long qty, double price, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder)
			terminal.createOrder(account, Direction.SELL, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(long qty, double price, double stop, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(long qty, double price, double stop, String comment) {
		testDefaultAccount();
		testDefaultSecurity();
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Security security, long qty, String comment) {
		testDefaultAccount();
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Security security, long qty, String comment) {
		testDefaultAccount();
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Account account, Security security, long qty,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Account account, Security security, long qty,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Security security, long qty, double price,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Security security, long qty, double price,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Security security, long qty, double price, double stop,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Security security, long qty, double price, double stop,
			String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Account account, Security security, long qty,
			double price, String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Account account, Security security, long qty,
			double price, String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty, price);
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order buy(Account account, Security security, long qty,
			double price, double stop, String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.BUY, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

	@Override
	public Order sell(Account account, Security security, long qty,
			double price, double stop, String comment)
	{
		EditableOrder o = (EditableOrder) terminal.createOrder(account,
				Direction.SELL, security, qty, price,
				new StopOrderActivator(stop));
		o.setComment(comment);
		return base.add(o);
	}

}
