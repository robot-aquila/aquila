package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.*;

/**
 * Типовая реализация терминала.
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
final public class TerminalImpl implements EditableTerminal {
	private static Logger logger;
	private volatile TerminalState state = TerminalState.STOPPED;
	private EventSystem es;
	private Securities securities;
	private Portfolios portfolios;
	private Orders orders;
	private StarterQueue starter;
	private Scheduler scheduler;
	private TerminalEventDispatcher dispatcher;
	private TerminalController controller;
	private OrderProcessor orderProcessor;
	
	static {
		logger = LoggerFactory.getLogger(TerminalImpl.class);
	}
	
	/**
	 * Конструктор (полный).
	 * <p>
	 * Данный конструктор позволяет определить все связанные объекты.
	 * Основное назначение этого конструктора - обеспечить возможность
	 * тестирования класса терминала. В большинстве случаев, пользователям
	 * класса нет необходимости использовать данный конструктор для
	 * инстанцирования терминала. Большая часть передаваемых объектов фактически
	 * является частью терминала и не имеют смысла в отрыве от него. В
	 * большинстве случаев, достаточно воспользоваться конструктором с более
	 * короткой сигнатурой.    
	 * <p>
	 * Аргументы отсортированны в порядке связанности. Первые фактически
	 * являются частью данного класса и были выделенны исключительно в целях
	 * облегчения дизайна. Ближе к концу аргументы менее связаны. Для них чаще
	 * возникает необходимость передачи извне.
	 * <p>
	 * @param controller контроллер терминала
	 * @param dispatcher диспетчер событий
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param starter пускач
	 * @param scheduler планировщик задач
	 * @param eventSystem фасад подсистемы событий
	 */
	public TerminalImpl(TerminalController controller,
			TerminalEventDispatcher dispatcher,
			Securities securities,
			Portfolios portfolios,
			Orders orders,
			StarterQueue starter,
			Scheduler scheduler,
			EventSystem eventSystem)
	{
		super();
		this.controller = controller;
		this.dispatcher = dispatcher;
		this.securities = securities;
		this.portfolios = portfolios;
		this.orders = orders;
		this.starter = starter;
		this.scheduler = scheduler;
		this.es = eventSystem;
	}
	
	@Override
	public synchronized OrderProcessor getOrderProcessor() {
		return orderProcessor;
	}
	
	@Override
	public synchronized void setOrderProcessor(OrderProcessor processor) {
		this.orderProcessor = processor;
	}
	
	@Override
	public StarterQueue getStarter() {
		return starter;
	}
	
	@Override
	public List<Security> getSecurities() {
		return securities.getSecurities();
	}

	@Override
	public Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		return securities.getSecurity(descr);
	}

	@Override
	public boolean isSecurityExists(SecurityDescriptor descr) {
		return securities.isSecurityExists(descr);
	}

	@Override
	public EventType OnSecurityAvailable() {
		return securities.OnSecurityAvailable();
	}

	@Override
	public List<Portfolio> getPortfolios() {
		return portfolios.getPortfolios();
	}

	@Override
	public Portfolio getPortfolio(Account account)
			throws PortfolioException
	{
		return portfolios.getPortfolio(account);
	}

	@Override
	public boolean isPortfolioAvailable(Account account) {
		return portfolios.isPortfolioAvailable(account);
	}

	@Override
	public EventType OnPortfolioAvailable() {
		return portfolios.OnPortfolioAvailable();
	}

	@Override
	public synchronized void start() throws StarterException {
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
	public synchronized void stop() throws StarterException {
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
	public Portfolio getDefaultPortfolio()
		throws PortfolioException
	{
		return portfolios.getDefaultPortfolio();
	}

	@Override
	public boolean isOrderExists(int id) {
		return orders.isOrderExists(id);
	}

	@Override
	public List<Order> getOrders() {
		return orders.getOrders();
	}

	@Override
	public Order getOrder(int id) throws OrderException {
		return orders.getOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return orders.OnOrderAvailable();
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
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
			orderProcessor.placeOrder(this, order);
		}
	}

	@Override
	public synchronized void cancelOrder(Order order) throws OrderException {
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
					orderProcessor.cancelOrder(this, order);
				}
			}
		}
	}

	@Override
	public int getOrdersCount() {
		return orders.getOrdersCount();
	}

	@Override
	public EventType OnOrderCancelFailed() {
		return orders.OnOrderCancelFailed();
	}

	@Override
	public EventType OnOrderCancelled() {
		return orders.OnOrderCancelled();
	}

	@Override
	public EventType OnOrderChanged() {
		return orders.OnOrderChanged();
	}

	@Override
	public EventType OnOrderDone() {
		return orders.OnOrderDone();
	}

	@Override
	public EventType OnOrderFailed() {
		return orders.OnOrderFailed();
	}

	@Override
	public EventType OnOrderFilled() {
		return orders.OnOrderFilled();
	}

	@Override
	public EventType OnOrderPartiallyFilled() {
		return orders.OnOrderPartiallyFilled();
	}

	@Override
	public EventType OnOrderRegistered() {
		return orders.OnOrderRegistered();
	}

	@Override
	public EventType OnOrderRegisterFailed() {
		return orders.OnOrderRegisterFailed();
	}

	@Override
	public EventType OnSecurityChanged() {
		return securities.OnSecurityChanged();
	}

	@Override
	public EventType OnSecurityTrade() {
		return securities.OnSecurityTrade();
	}

	@Override
	public EventType OnPortfolioChanged() {
		return portfolios.OnPortfolioChanged();
	}

	@Override
	public EventType OnPositionAvailable() {
		return portfolios.OnPositionAvailable();
	}

	@Override
	public EventType OnPositionChanged() {
		return portfolios.OnPositionChanged();
	}

	@Override
	public int getSecuritiesCount() {
		return securities.getSecuritiesCount();
	}

	@Override
	public int getPortfoliosCount() {
		return portfolios.getPortfoliosCount();
	}

	@Override
	public void fireEvents(EditableOrder order) {
		orders.fireEvents(order);
	}

	@Override
	public EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		return orders.getEditableOrder(id);
	}

	@Override
	public void purgeOrder(int id) {
		orders.purgeOrder(id);
	}

	@Override
	public void fireEvents(EditablePortfolio portfolio) {
		portfolios.fireEvents(portfolio);
	}

	@Override
	public EditablePortfolio getEditablePortfolio(Account account) {
		return portfolios.getEditablePortfolio(this, account);
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		portfolios.setDefaultPortfolio(portfolio);
	}

	@Override
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr) {
		return securities.getEditableSecurity(this, descr);
	}

	@Override
	public void fireEvents(EditableSecurity security) {
		securities.fireEvents(security);
	}

	@Override
	public EventType OnConnected() {
		return dispatcher.OnConnected();
	}

	@Override
	public EventType OnDisconnected() {
		return dispatcher.OnDisconnected();
	}

	@Override
	public void fireTerminalConnectedEvent() {
		markTerminalConnected();
	}
	
	@Override
	public synchronized void markTerminalConnected() {
		if ( state == TerminalState.STARTED ) {
			state = TerminalState.CONNECTED;
			dispatcher.fireConnected(this);
			logger.info("Terminal connected");
		} else {
			logger.debug("Skip connected event request cuz {}", state);
		}		
	}

	@Override
	public void fireTerminalDisconnectedEvent() {
		markTerminalDisconnected();
	}
	
	@Override
	public synchronized void markTerminalDisconnected() {
		if ( state == TerminalState.CONNECTED
		  || state == TerminalState.STOPPING )
		{
			if ( state == TerminalState.CONNECTED ) {
				setTerminalState(TerminalState.STARTED);
			}
			dispatcher.fireDisconnected(this);
			logger.info("Terminal disconnected");
		} else {
			logger.debug("Skip disconnected event request cuz {}", state);
		}		
	}
	
	@Override
	public void fireTerminalStartedEvent() {
		dispatcher.fireStarted();
	}
	
	@Override
	public void fireTerminalStoppedEvent() {
		dispatcher.fireStopped();
	}

	@Override
	public EventType OnStarted() {
		return dispatcher.OnStarted();
	}

	@Override
	public EventType OnStopped() {
		return dispatcher.OnStopped();
	}

	@Override
	public EventType OnPanic() {
		return dispatcher.OnPanic();
	}

	@Override
	public void firePanicEvent(int code, String msgId) {
		firePanicEvent(code, msgId, new Object[] { });
	}

	@Override
	public void firePanicEvent(int code, String msgId, Object[] args) {
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
	public synchronized boolean stopped() {
		return state == TerminalState.STOPPED;
	}

	@Override
	public synchronized boolean connected() {
		return state == TerminalState.CONNECTED;
	}
	
	@Override
	public synchronized boolean started() {
		return state == TerminalState.CONNECTED
			|| state == TerminalState.STARTED;
	}

	@Override
	public synchronized TerminalState getTerminalState() {
		return state;
	}

	@Override
	public synchronized void setTerminalState(TerminalState state) {
		logger.debug("Change terminal state to {}", state);
		this.state = state;
	}

	@Override
	public DateTime getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	@Override
	public EventSystem getEventSystem() {
		return es;
	}

	@Override
	public EditableOrder createOrder() {
		return orders.createOrder(this);
	}

	@Override
	public EventType OnOrderTrade() {
		return orders.OnOrderTrade();
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price)
	{
		return createOrder(account, dir, security, qty, price, null);
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty)
	{
		return createOrder(account, dir, security, qty, null);
	}
	
	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price, OrderActivator activator)
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
		orders.fireEvents(order);
		return order;
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, OrderActivator activator)
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
		orders.fireEvents(order);
		return order;
	}

	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		
	}

	@Override
	public EventType OnRequestSecurityError() {
		return dispatcher.OnRequestSecurityError();
	}

	@Override
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		dispatcher.fireSecurityRequestError(descr, errorCode, errorMsg);
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime time) {
		return scheduler.schedule(task, time);
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime firstTime,
			long period)
	{
		return scheduler.schedule(task, firstTime, period);
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		return scheduler.schedule(task, delay);
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		return scheduler.schedule(task, delay, period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, DateTime firstTime,
			long period)
	{
		return scheduler.scheduleAtFixedRate(task, firstTime, period);		
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay,
			long period)
	{
		return scheduler.scheduleAtFixedRate(task, delay, period);
	}

	@Override
	public void cancel(Runnable task) {
		scheduler.cancel(task);
	}
	
	@Override
	public boolean scheduled(Runnable task) {
		return scheduler.scheduled(task);
	}

	@Override
	public TaskHandler getTaskHandler(Runnable task) {
		return scheduler.getTaskHandler(task);
	}

	@Override
	@Deprecated
	public void setStarter(StarterQueue starter) {
		this.starter = starter;
	}

	/**
	 * Установить планировщик задач.
	 * <P>
	 * @param scheduler планировщик
	 */
	@Deprecated
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Получить планировщик задач.
	 * <p>
	 * @return планировщик
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	/** 
	 * Получить контроллер терминала.
	 * <p>
	 * @return контроллер терминала
	 */
	public TerminalController getTerminalController() {
		return controller;
	}
	
	/**
	 * Получить диспетчер событий терминала.
	 * <p>
	 * @return диспетчер событий
	 */
	public TerminalEventDispatcher getTerminalEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить хранилище инструментов.
	 * <p>
	 * @return хранилище инструментов
	 */
	public Securities getSecurityStorage() {
		return securities;
	}
	
	/**
	 * Получить хранилище портфелей.
	 * <p>
	 * @return хранилище портфелей
	 */
	public Portfolios getPortfolioStorage() {
		return portfolios;
	}
	
	/**
	 * Получить хранилище заявок.
	 * <p>
	 * @return хранилище заявок
	 */
	public Orders getOrderStorage() {
		return orders;
	}

	@Override
	public EventType OnReady() {
		return dispatcher.OnReady();
	}

	@Override
	public EventType OnUnready() {
		return dispatcher.OnUnready();
	}

	@Override
	public void fireTerminalReady() {
		logger.debug("Terminal marked as ready");
		dispatcher.fireReady();
	}

	@Override
	public void fireTerminalUnready() {
		logger.debug("Terminal marked as unready");
		dispatcher.fireUnready();
	}

	@Override
	public Counter getOrderIdSequence() {
		return orders.getIdSequence();
	}

}
