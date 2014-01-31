package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;
import java.util.TimerTask;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.*;

/**
 * Базовая реализация терминала.
 * <p>
 * Данный класс выполняет дополнительную обработку событий
 * {@link #OnConnected()} и {@link #OnDisconnected()}. Запрос на генерацию
 * события {@link #OnConnected()} выполняется посредством вызова метода
 * {@link #fireTerminalConnectedEvent()}, но фактически генерируется только
 * один раз и только в том случае, если предыдущее событие терминала было
 * {@link #OnStarted()} или {@link #OnDisconnected()}. Генерация события
 * {@link #OnDisconnected()} выполняется посредством вызова метода
 * {@link #fireTerminalDisconnectedEvent()}, но фактически генерируется один раз
 * в случае, если предыдущее сгенерированное событие терминала было
 * {@link #OnConnected()}. Кроме того, данный класс автоматически выполняет
 * запрос на генерацию события {@link #OnDisconnected()} перед генерацией
 * события останова терминала. При этом так же учитывается вышеописанное
 * условие.
 * <p>
 * Таким образом, каждая специфическая реализация терминала, использующая данный
 * класс в качестве основы, должна обеспечить принципиальную возможность
 * получения информации о наличии соединения с удаленной системой, а о
 * корректном порядке следования соответствующих событий позаботится данный
 * класс. Предсказуемая последовательность событий необходима для потребителей,
 * которые будут использовать данные события как сигналы разрешающие или
 * запрещающие работу с терминалом. 
 */
public class TerminalImpl implements EditableTerminal {
	private static final Logger logger;
	private final EventSystem es;
	private final EditableSecurities securities;
	private final EditablePortfolios portfolios;
	private final StarterQueue starter;
	private final EditableOrders orders;
	private final TerminalEventDispatcher dispatcher;
	private volatile TerminalState state = TerminalState.STOPPED;
	private final TerminalController controller;
	private OrderProcessor orderProcessor;
	private final Scheduler scheduler;
	private final Counter orderNumerator;
	
	static {
		logger = LoggerFactory.getLogger(TerminalImpl.class);
	}
	
	/**
	 * Создать объект (короткий конструктор).
	 * <p>
	 * Автоматически создает экземпляры планировщика задач и контроллера
	 * терминала.
	 * <p>
	 * @param eventSystem фасад подсистемы событий
	 * @param starter пускач
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param dispatcher диспетчер событий
	 */
	public TerminalImpl(EventSystem eventSystem,
						StarterQueue starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						TerminalEventDispatcher dispatcher)
	{
		this(eventSystem, new SchedulerLocal(), starter, securities, portfolios,
				orders, new TerminalController(), dispatcher);
	}
	
	/**
	 * Создать объект.
	 * <p>
	 * @param eventSystem фасад подсистемы событий
	 * @param scheduler планировщик задач
	 * @param starter пускач
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param controller контроллер терминала
	 * @param dispatcher диспетчер событий
	 */
	public TerminalImpl(EventSystem eventSystem,
						Scheduler scheduler,
						StarterQueue starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						TerminalController controller,
						TerminalEventDispatcher dispatcher)
	{
		super();
		this.es = eventSystem;
		this.starter = starter;
		this.securities = securities;
		this.portfolios = portfolios;
		this.orders = orders;
		this.controller = controller;
		this.dispatcher = dispatcher;
		this.scheduler = scheduler;
		orderNumerator = new SimpleCounter();
	}
	
	/**
	 * Получить экземпляр таймера.
	 * <p>
	 * @return таймер
	 */
	final public Scheduler getScheduler() {
		return scheduler;
	}
	
	/**
	 * Получить контроллер терминала.
	 * <p>
	 * @return контроллер терминала
	 */
	final public TerminalController getTerminalController() {
		return controller;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	final public TerminalEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	final public synchronized OrderProcessor getOrderProcessorInstance() {
		return orderProcessor;
	}
	
	@Override
	final public synchronized
		void setOrderProcessorInstance(OrderProcessor processor)
	{
		this.orderProcessor = processor;
	}
	
	@Override
	final public EditableSecurities getSecuritiesInstance() {
		return securities;
	}
	
	@Override
	final public EditablePortfolios getPortfoliosInstance() {
		return portfolios;
	}
	
	@Override
	final public StarterQueue getStarter() {
		return starter;
	}
	
	@Override
	final public EditableOrders getOrdersInstance() {
		return orders;
	}
	
	@Override
	final public List<Security> getSecurities() {
		return securities.getSecurities();
	}

	@Override
	final public Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		return securities.getSecurity(descr);
	}

	@Override
	final public boolean isSecurityExists(SecurityDescriptor descr) {
		return securities.isSecurityExists(descr);
	}

	@Override
	final public EventType OnSecurityAvailable() {
		return securities.OnSecurityAvailable();
	}

	@Override
	final public List<Portfolio> getPortfolios() {
		return portfolios.getPortfolios();
	}

	@Override
	final public Portfolio getPortfolio(Account account)
			throws PortfolioException
	{
		return portfolios.getPortfolio(account);
	}

	@Override
	final public boolean isPortfolioAvailable(Account account) {
		return portfolios.isPortfolioAvailable(account);
	}

	@Override
	final public EventType OnPortfolioAvailable() {
		return portfolios.OnPortfolioAvailable();
	}

	@Override
	final public synchronized void start() throws StarterException {
		if ( state == TerminalState.STOPPED ) {
			setTerminalState(TerminalState.STARTING);
			logger.debug("Run start sequence");
			controller.runStartSequence(this);
		} else if ( state == TerminalState.STARTING
				 || state == TerminalState.STOPPING )
		{
			logger.warn("start(): terminal in intermediate state: {}", state);
		} else {
			throw new StarterException("Cannot start terminal: " + state);
		}
	}

	@Override
	final public synchronized void stop() throws StarterException {
		if (state == TerminalState.STARTED||state == TerminalState.CONNECTED) {
			setTerminalState(TerminalState.STOPPING);
			logger.debug("Run stop sequence");
			controller.runStopSequence(this);
		} else if ( state == TerminalState.STARTING
				 || state == TerminalState.STOPPING )
		{
			logger.warn("stop(): terminal in intermediate state: {}", state);
		} else {
			throw new StarterException("Cannot stop terminal: " + state);
		}
	}

	@Override
	final public Portfolio getDefaultPortfolio()
		throws PortfolioException
	{
		return portfolios.getDefaultPortfolio();
	}

	@Override
	final public boolean isOrderExists(int id) {
		return orders.isOrderExists(id);
	}

	@Override
	final public List<Order> getOrders() {
		return orders.getOrders();
	}

	@Override
	final public Order getOrder(int id) throws OrderException {
		return orders.getOrder(id);
	}

	@Override
	final public EventType OnOrderAvailable() {
		return orders.OnOrderAvailable();
	}

	@Override
	final public void placeOrder(Order order) throws OrderException {
		synchronized ( order ) {
			OrderStatus status = order.getStatus();
			EditableOrder o = (EditableOrder) order;
			if ( status == OrderStatus.PENDING ) {
				OrderActivator activator = o.getActivator();
				if ( activator != null ) {
					activator.start(o);
					o.setStatus(OrderStatus.CONDITION);
					orders.fireEvents(o);
					return;
				}
			} else if ( status == OrderStatus.CONDITION ) {
				order.getActivator().stop();
			}
			synchronized ( this ) {
				orderProcessor.placeOrder(order);
			}
		}
	}

	@Override
	final public void cancelOrder(Order order) throws OrderException {
		synchronized ( order ) {
			EditableOrder o = (EditableOrder) order;
			OrderStatus status = o.getStatus();
			if ( status == OrderStatus.PENDING ) {
				o.setStatus(OrderStatus.CANCELLED);
				o.setLastChangeTime(getCurrentTime());
				orders.fireEvents(o);
			} else if ( status == OrderStatus.CONDITION ) {
				o.getActivator().stop();
				o.setStatus(OrderStatus.CANCELLED);
				o.setLastChangeTime(getCurrentTime());
				orders.fireEvents(o);
			} else {
				synchronized ( this ) {
					orderProcessor.cancelOrder(order);
				}
			}
		}
	}

	@Override
	final public int getOrdersCount() {
		return orders.getOrdersCount();
	}

	@Override
	final public EventType OnOrderCancelFailed() {
		return orders.OnOrderCancelFailed();
	}

	@Override
	final public EventType OnOrderCancelled() {
		return orders.OnOrderCancelled();
	}

	@Override
	final public EventType OnOrderChanged() {
		return orders.OnOrderChanged();
	}

	@Override
	final public EventType OnOrderDone() {
		return orders.OnOrderDone();
	}

	@Override
	final public EventType OnOrderFailed() {
		return orders.OnOrderFailed();
	}

	@Override
	final public EventType OnOrderFilled() {
		return orders.OnOrderFilled();
	}

	@Override
	final public EventType OnOrderPartiallyFilled() {
		return orders.OnOrderPartiallyFilled();
	}

	@Override
	final public EventType OnOrderRegistered() {
		return orders.OnOrderRegistered();
	}

	@Override
	final public EventType OnOrderRegisterFailed() {
		return orders.OnOrderRegisterFailed();
	}

	@Override
	final public EventType OnSecurityChanged() {
		return securities.OnSecurityChanged();
	}

	@Override
	final public EventType OnSecurityTrade() {
		return securities.OnSecurityTrade();
	}

	@Override
	final public EventType OnPortfolioChanged() {
		return portfolios.OnPortfolioChanged();
	}

	@Override
	final public EventType OnPositionAvailable() {
		return portfolios.OnPositionAvailable();
	}

	@Override
	final public EventType OnPositionChanged() {
		return portfolios.OnPositionChanged();
	}

	@Override
	final public int getSecuritiesCount() {
		return securities.getSecuritiesCount();
	}

	@Override
	final public int getPortfoliosCount() {
		return portfolios.getPortfoliosCount();
	}

	@Override
	final public void fireEvents(EditableOrder order) {
		orders.fireEvents(order);
	}

	@Override
	final public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		return orders.getEditableOrder(id);
	}

	@Override
	final public void registerOrder(int id, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		orders.registerOrder(id, order);
	}

	@Override
	final public void purgeOrder(int id) {
		orders.purgeOrder(id);
	}

	@Override
	final public void fireEvents(EditablePortfolio portfolio) {
		portfolios.fireEvents(portfolio);
	}

	@Override
	final public EditablePortfolio getEditablePortfolio(Account account) {
		return portfolios.getEditablePortfolio(this, account);
	}

	@Override
	final public void setDefaultPortfolio(EditablePortfolio portfolio) {
		portfolios.setDefaultPortfolio(portfolio);
	}

	@Override
	final public EditableSecurity getEditableSecurity(SecurityDescriptor descr)
	{
		return securities.getEditableSecurity(this, descr);
	}

	@Override
	final public void fireEvents(EditableSecurity security) {
		securities.fireEvents(security);
	}

	@Override
	final public EventType OnConnected() {
		return dispatcher.OnConnected();
	}

	@Override
	final public EventType OnDisconnected() {
		return dispatcher.OnDisconnected();
	}

	@Override
	final public synchronized void fireTerminalConnectedEvent() {
		if ( state == TerminalState.STARTED ) {
			state = TerminalState.CONNECTED;
			dispatcher.fireConnected();
			logger.info("Terminal connected");
		} else {
			logger.debug("Skip connected event request cuz {}", state);
		}
	}

	@Override
	final public synchronized void fireTerminalDisconnectedEvent() {
		if ( state == TerminalState.CONNECTED
		  || state == TerminalState.STOPPING )
		{
			if ( state == TerminalState.CONNECTED ) {
				setTerminalState(TerminalState.STARTED);
			}
			dispatcher.fireDisconnected();
			logger.info("Terminal disconnected");
		} else {
			logger.debug("Skip disconnected event request cuz {}", state);
		}
	}
	
	@Override
	final public void fireTerminalStartedEvent() {
		dispatcher.fireStarted();
	}
	
	@Override
	final public void fireTerminalStoppedEvent() {
		dispatcher.fireStopped();
	}

	@Override
	final public EventType OnStarted() {
		return dispatcher.OnStarted();
	}

	@Override
	final public EventType OnStopped() {
		return dispatcher.OnStopped();
	}

	@Override
	final public EventType OnPanic() {
		return dispatcher.OnPanic();
	}

	@Override
	final public void firePanicEvent(int code, String msgId) {
		firePanicEvent(code, msgId, new Object[] { });
	}

	@Override
	final public synchronized void
		firePanicEvent(int code, String msgId, Object[] args)
	{
		if ( started() ) {
			logger.error("PANIC[" + code + "]: " + msgId, args);
			dispatcher.firePanic(code, msgId, args);
			try {
				stop();
			} catch ( StarterException e ) {
				logger.error("Unexpected exception (ignore): ", e);
			}
		}
	}

	@Override
	final public synchronized boolean stopped() {
		return state == TerminalState.STOPPED;
	}

	@Override
	final public synchronized boolean connected() {
		return state == TerminalState.CONNECTED;
	}
	
	@Override
	final public synchronized boolean started() {
		return state == TerminalState.CONNECTED
			|| state == TerminalState.STARTED;
	}

	@Override
	final public synchronized TerminalState getTerminalState() {
		return state;
	}

	@Override
	final public synchronized void setTerminalState(TerminalState state) {
		logger.debug("Change terminal state to {}", state);
		this.state = state;
	}

	@Override
	final public DateTime getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	@Override
	final public EventSystem getEventSystem() {
		return es;
	}

	@Override
	final public EditableOrder createOrder(EditableTerminal terminal) {
		return orders.createOrder(terminal);
	}

	@Override
	final public EditableOrder createOrder() {
		return orders.createOrder(this);
	}

	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TerminalImpl.class ) {
			return false;
		}
		return fieldsEquals(other);
	}
	
	/**
	 * Служебный метод сравнения полей экземпляра.
	 * <p>
	 * @param other объект для сравнения
	 * @return результат сравнения
	 */
	protected boolean fieldsEquals(Object other) {
		TerminalImpl o = (TerminalImpl) other;
		return new EqualsBuilder()
			.append(o.controller, controller)
			.append(o.es, es)
			.append(o.orderProcessor, orderProcessor)
			.append(o.orders, orders)
			.append(o.portfolios, portfolios)
			.append(o.securities, securities)
			.append(o.starter, starter)
			.append(o.state, state)
			.append(o.scheduler, scheduler)
			.isEquals();
	}

	@Override
	final public EventType OnOrderTrade() {
		return orders.OnOrderTrade();
	}

	@Override
	final public Counter getOrderNumerator() {
		return orderNumerator;
	}

	@Override
	final public Order createOrder(Account account, Direction dir,
			Security security, long qty, double price)
	{
		return createOrder(account, dir, security, qty, price, null);
	}

	@Override
	final public Order createOrder(Account account, Direction dir,
			Security security, long qty)
	{
		return createOrder(account, dir, security, qty, null);
	}
	
	@Override
	final public Order createOrder(Account account, Direction dir,
		Security security, long qty, double price, OrderActivator activator)
	{
		EditableOrder order = createOrder();
		order.setTime(getCurrentTime());
		order.setType(OrderType.LIMIT);
		order.setAccount(account);
		order.setDirection(dir);
		order.setSecurityDescriptor(security.getDescriptor());
		order.setQty(qty);
		order.setQtyRest(qty);
		order.setPrice(price);
		if ( activator != null ) {
			order.setActivator(activator);
		}
		order.resetChanges();
		try {
			orders.registerOrder(orderNumerator.incrementAndGet(), order);
		} catch ( OrderException e ) {
			throw new IllegalStateException("Corrupted internal state: ", e);
		}
		orders.fireEvents(order);
		return order;
	}

	@Override
	final public Order createOrder(Account account, Direction dir,
		Security security, long qty, OrderActivator activator)
	{
		EditableOrder order = createOrder();
		order.setTime(getCurrentTime());
		order.setType(OrderType.MARKET);
		order.setAccount(account);
		order.setDirection(dir);
		order.setSecurityDescriptor(security.getDescriptor());
		order.setQty(qty);
		order.setQtyRest(qty);
		if ( activator != null ) {
			order.setActivator(activator);
		}
		order.resetChanges();
		try {
			orders.registerOrder(orderNumerator.incrementAndGet(), order);
		} catch ( OrderException e ) {
			throw new IllegalStateException("Corrupted internal state: ", e);
		}
		orders.fireEvents(order);
		return order;
	}

	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		
	}

	@Override
	final public EventType OnRequestSecurityError() {
		return dispatcher.OnRequestSecurityError();
	}

	@Override
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		Object args[] = { descr, errorCode, errorMsg };
		logger.error("TODO: fire request {} error: [{}] {}", args);
		dispatcher.fireSecurityRequestError(descr, errorCode, errorMsg);
	}

	@Override
	final public void schedule(TimerTask task, DateTime time) {
		scheduler.schedule(task, time);
	}

	@Override
	final public void schedule(TimerTask task, DateTime firstTime, long period)
	{
		scheduler.schedule(task, firstTime, period);
	}

	@Override
	final public void schedule(TimerTask task, long delay) {
		scheduler.schedule(task, delay);
	}

	@Override
	final public void schedule(TimerTask task, long delay, long period) {
		scheduler.schedule(task, delay, period);
	}

	@Override
	final public void scheduleAtFixedRate(TimerTask task, DateTime firstTime,
			long period)
	{
		scheduler.scheduleAtFixedRate(task, firstTime, period);		
	}

	@Override
	final public
		void scheduleAtFixedRate(TimerTask task, long delay, long period)
	{
		scheduler.scheduleAtFixedRate(task, delay, period);
	}

	@Override
	final public boolean cancel(TimerTask task) {
		return scheduler.cancel(task);
	}

	@Override
	public EditablePortfolio
		getEditablePortfolio(EditableTerminal terminal, Account account)
	{
		return portfolios.getEditablePortfolio(terminal, account);
	}

	@Override
	public EditableSecurity
		getEditableSecurity(EditableTerminal terminal, SecurityDescriptor descr)
	{
		return securities.getEditableSecurity(terminal, descr);
	}

}
