package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;

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
	private final Starter starter;
	private final EditableOrders orders;
	private final EventDispatcher dispatcher;
	private final EventType onConnected,onDisconnected,onStarted,
		onStopped,onPanic,onReqSecurityError;
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
	 * @param onConnected тип события при подключении
	 * @param onDisconnected тип события при отключении
	 * @param onStarted тип события при старте терминала
	 * @param onStopped тип события при останове терминала
	 * @param onPanic тип события при паническом состоянии терминала
	 * @param onReqSecurityError тип события по запросу инструмента
	 */
	public TerminalImpl(EventSystem eventSystem,
						Starter starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						EventDispatcher dispatcher,
						EventType onConnected,
						EventType onDisconnected,
						EventType onStarted,
						EventType onStopped, EventType onPanic,
						EventType onReqSecurityError)
	{
		this(eventSystem, new SchedulerLocal(), starter,
				securities, portfolios, orders, new TerminalController(),
				dispatcher, onConnected, onDisconnected,
				onStarted, onStopped, onPanic, onReqSecurityError);
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
	 * @param onConnected тип события при подключении
	 * @param onDisconnected тип события при отключении
	 * @param onStarted тип события при старте терминала
	 * @param onStopped тип события при останове терминала
	 * @param onPanic тип события при паническом состоянии терминала
	 * @param onReqSecurityError тип события по запросу инструмента
	 */
	public TerminalImpl(EventSystem eventSystem,
						Scheduler scheduler,
						Starter starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						TerminalController controller,
						EventDispatcher dispatcher,
						EventType onConnected,
						EventType onDisconnected,
						EventType onStarted,
						EventType onStopped,
						EventType onPanic,
						EventType onReqSecurityError)
	{
		super();
		this.es = eventSystem;
		this.starter = starter;
		this.securities = securities;
		this.portfolios = portfolios;
		this.orders = orders;
		this.controller = controller;
		this.dispatcher = dispatcher;
		this.onConnected = onConnected;
		this.onDisconnected = onDisconnected;
		this.onStarted = onStarted;
		this.onStopped = onStopped;
		this.onPanic = onPanic;
		this.onReqSecurityError = onReqSecurityError;
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
	final public EventDispatcher getEventDispatcher() {
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
	final public Starter getStarter() {
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
		orderProcessor.placeOrder(order);
	}

	@Override
	final public void cancelOrder(Order order) throws OrderException {
		orderProcessor.cancelOrder(order);
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
	final public void fireOrderAvailableEvent(Order order) {
		orders.fireOrderAvailableEvent(order);
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
	final public void firePortfolioAvailableEvent(Portfolio portfolio) {
		portfolios.firePortfolioAvailableEvent(portfolio);
	}

	@Override
	final public EditablePortfolio getEditablePortfolio(Account account)
			throws PortfolioException
	{
		return portfolios.getEditablePortfolio(account);
	}

	@Override
	final public EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
			throws PortfolioException
	{
		return portfolios.createPortfolio(terminal, account);
	}
	
	@Override
	final public EditablePortfolio createPortfolio(Account account)
		throws PortfolioException
	{
		return portfolios.createPortfolio(this, account);
	}

	@Override
	final public void setDefaultPortfolio(EditablePortfolio portfolio) {
		portfolios.setDefaultPortfolio(portfolio);
	}

	@Override
	final public EditableSecurity
		getEditableSecurity(SecurityDescriptor descr)
			throws SecurityNotExistsException
	{
		return securities.getEditableSecurity(descr);
	}

	@Override
	final public void fireSecurityAvailableEvent(Security security) {
		securities.fireSecurityAvailableEvent(security);
	}

	@Override
	final public EventType OnConnected() {
		return onConnected;
	}

	@Override
	final public EventType OnDisconnected() {
		return onDisconnected;
	}

	@Override
	final public synchronized void fireTerminalConnectedEvent() {
		if ( state == TerminalState.STARTED ) {
			state = TerminalState.CONNECTED;
			dispatcher.dispatch(new EventImpl(onConnected));
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
			dispatcher.dispatch(new EventImpl(onDisconnected));
			logger.info("Terminal disconnected");
		} else {
			logger.debug("Skip disconnected event request cuz {}", state);
		}
	}
	
	@Override
	final public void fireTerminalStartedEvent() {
		dispatcher.dispatch(new EventImpl(onStarted));
	}
	
	@Override
	final public void fireTerminalStoppedEvent() {
		dispatcher.dispatch(new EventImpl(onStopped));
	}

	@Override
	final public EventType OnStarted() {
		return onStarted;
	}

	@Override
	final public EventType OnStopped() {
		return onStopped;
	}

	@Override
	final public EventType OnPanic() {
		return onPanic;
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
			dispatcher.dispatch(new PanicEvent(onPanic, code, msgId, args));
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
	final public Date getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	@Override
	final public EventSystem getEventSystem() {
		return es;
	}

	@Override
	final public EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return securities.createSecurity(terminal, descr);
	}

	@Override
	final public EditableSecurity
		createSecurity(SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return securities.createSecurity(this, descr);
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
			.append(o.dispatcher, dispatcher)
			.append(o.es, es)
			.append(o.onConnected, onConnected)
			.append(o.onDisconnected, onDisconnected)
			.append(o.onPanic, onPanic)
			.append(o.onStarted, onStarted)
			.append(o.onStopped, onStopped)
			.append(o.orderProcessor, orderProcessor)
			.append(o.orders, orders)
			.append(o.portfolios, portfolios)
			.append(o.securities, securities)
			.append(o.starter, starter)
			.append(o.state, state)
			.append(o.scheduler, scheduler)
			.append(o.onReqSecurityError, onReqSecurityError)
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
		EditableOrder order = createOrder();
		order.setTime(getCurrentTime());
		order.setType(OrderType.LIMIT);
		order.setAccount(account);
		order.setDirection(dir);
		order.setSecurityDescriptor(security.getDescriptor());
		order.setQty(qty);
		order.setQtyRest(qty);
		order.setPrice(price);
		order.setAvailable(true);
		order.resetChanges();
		try {
			orders.registerOrder(orderNumerator.incrementAndGet(), order);
		} catch ( OrderException e ) {
			throw new IllegalStateException("Corrupted internal state: ", e);
		}
		orders.fireOrderAvailableEvent(order);
		return order;
	}

	@Override
	final public Order createOrder(Account account, Direction dir,
			Security security, long qty)
	{
		EditableOrder order = createOrder();
		order.setTime(getCurrentTime());
		order.setType(OrderType.MARKET);
		order.setAccount(account);
		order.setDirection(dir);
		order.setSecurityDescriptor(security.getDescriptor());
		order.setQty(qty);
		order.setQtyRest(qty);
		order.setAvailable(true);
		order.resetChanges();
		try {
			orders.registerOrder(orderNumerator.incrementAndGet(), order);
		} catch ( OrderException e ) {
			throw new IllegalStateException("Corrupted internal state: ", e);
		}
		orders.fireOrderAvailableEvent(order);
		return order;
	}

	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		
	}

	@Override
	final public EventType OnRequestSecurityError() {
		return onReqSecurityError;
	}

	@Override
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		Object args[] = { descr, errorCode, errorMsg };
		logger.error("TODO: fire request {} error: [{}] {}", args);
	}

	@Override
	final public void schedule(TimerTask task, Date time) {
		scheduler.schedule(task, time);
	}

	@Override
	final public void schedule(TimerTask task, Date firstTime, long period) {
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
	final public
		void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)
	{
		scheduler.scheduleAtFixedRate(task, firstTime, period);		
	}

	@Override
	final public
		void scheduleAtFixedRate(TimerTask task, long delay, long period)
	{
		scheduler.scheduleAtFixedRate(task, delay, period);
	}

}
