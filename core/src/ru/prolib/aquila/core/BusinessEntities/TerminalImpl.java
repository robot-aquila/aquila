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
 * <p>
 * @param <T> - тип объекта связывания терминала со спецификой реализации.
 * см. {@link EditableTerminal}.
 */
public class TerminalImpl<T> implements EditableTerminal<T> {
	private static Logger logger;
	private volatile TerminalState state = TerminalState.STOPPED;
	private EventSystem es;
	private Securities securities;
	private Portfolios portfolios;
	private Orders orders;
	private StarterQueue starter;
	private Scheduler scheduler;
	private Counter orderNumerator;	
	private TerminalEventDispatcher dispatcher;
	private TerminalController controller;
	private OrderProcessor orderProcessor;
	private T serviceLocator;
	
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
	 * @param numerator нумератор заявок
	 * @param starter пускач
	 * @param scheduler планировщик задач
	 * @param eventSystem фасад подсистемы событий
	 */
	public TerminalImpl(TerminalController controller,
			TerminalEventDispatcher dispatcher,
			Securities securities,
			Portfolios portfolios,
			Orders orders,
			Counter numerator,
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
		this.orderNumerator = numerator;
		this.starter = starter;
		this.scheduler = scheduler;
		this.es = eventSystem;
	}
	
	/**
	 * Конструктор (ультракороткий).
	 * <p>
	 * Данный конструктор предназначен для создания экземпляра терминала со
	 * всеми связанными объектами по-умолчанию. В качестве предустановленного
	 * объекта используется только система событий. Это может быть полезно
	 * например когда необходимо присоединить терминал к существующей очереди
	 * событий или к специфической реализации событий (например синхронные).
	 * <p>
	 * @param es фасад системы событий
	 */
	public TerminalImpl(EventSystem es) {
		this(new TerminalController(), new TerminalEventDispatcher(es),
			new Securities(new SecuritiesEventDispatcher(es)),
			new Portfolios(new PortfoliosEventDispatcher(es)),
			new Orders(new OrdersEventDispatcher(es)),
			new SimpleCounter(),
			new StarterQueue(),
			new SchedulerLocal(),
			es);
	}
	
	/**
	 * Конструктор (ультракороткий).
	 * <p>
	 * Данный конструктор предназначен для создания экземпляра терминала со
	 * всеми связанными объектами по-умолчанию. Для организации цикла обмена
	 * сообщениями создается стандартная очередь событий типа
	 * {@link EventQueueImpl}.  
	 * <p>
	 * @param queueId идентификатор очереди событий
	 */
	public TerminalImpl(String queueId) {
		this(new EventSystemImpl(new EventQueueImpl(queueId)));
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
	public synchronized List<Security> getSecurities() {
		return securities.getSecurities();
	}

	@Override
	public synchronized Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		return securities.getSecurity(descr);
	}

	@Override
	public synchronized boolean isSecurityExists(SecurityDescriptor descr) {
		return securities.isSecurityExists(descr);
	}

	@Override
	public EventType OnSecurityAvailable() {
		return securities.OnSecurityAvailable();
	}

	@Override
	public synchronized List<Portfolio> getPortfolios() {
		return portfolios.getPortfolios();
	}

	@Override
	public synchronized Portfolio getPortfolio(Account account)
			throws PortfolioException
	{
		return portfolios.getPortfolio(account);
	}

	@Override
	public synchronized boolean isPortfolioAvailable(Account account) {
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
	public synchronized Portfolio getDefaultPortfolio()
		throws PortfolioException
	{
		return portfolios.getDefaultPortfolio();
	}

	@Override
	public synchronized boolean isOrderExists(int id) {
		return orders.isOrderExists(id);
	}

	@Override
	public synchronized List<Order> getOrders() {
		return orders.getOrders();
	}

	@Override
	public synchronized Order getOrder(int id) throws OrderException {
		return orders.getOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return orders.OnOrderAvailable();
	}

	@Override
	public synchronized void placeOrder(Order order) throws OrderException {
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
					orderProcessor.cancelOrder(order);
				}
			}
		}
	}

	@Override
	public synchronized int getOrdersCount() {
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
	public synchronized int getSecuritiesCount() {
		return securities.getSecuritiesCount();
	}

	@Override
	public synchronized int getPortfoliosCount() {
		return portfolios.getPortfoliosCount();
	}

	@Override
	public synchronized void fireEvents(EditableOrder order) {
		orders.fireEvents(order);
	}

	@Override
	public synchronized EditableOrder getEditableOrder(int id)
		throws OrderNotExistsException
	{
		return orders.getEditableOrder(id);
	}

	@Override
	public synchronized void registerOrder(int id, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		orders.registerOrder(id, order);
	}

	@Override
	public synchronized void purgeOrder(int id) {
		orders.purgeOrder(id);
	}

	@Override
	public synchronized void fireEvents(EditablePortfolio portfolio) {
		portfolios.fireEvents(portfolio);
	}

	@Override
	public synchronized EditablePortfolio
		getEditablePortfolio(Account account)
	{
		return portfolios.getEditablePortfolio(this, account);
	}

	@Override
	public synchronized void
		setDefaultPortfolio(EditablePortfolio portfolio)
	{
		portfolios.setDefaultPortfolio(portfolio);
	}

	@Override
	public synchronized EditableSecurity
		getEditableSecurity(SecurityDescriptor descr)
	{
		return securities.getEditableSecurity(this, descr);
	}

	@Override
	public synchronized void fireEvents(EditableSecurity security) {
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
	public synchronized void fireTerminalConnectedEvent() {
		if ( state == TerminalState.STARTED ) {
			state = TerminalState.CONNECTED;
			dispatcher.fireConnected();
			logger.info("Terminal connected");
		} else {
			logger.debug("Skip connected event request cuz {}", state);
		}
	}

	@Override
	public synchronized void fireTerminalDisconnectedEvent() {
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
	public synchronized void
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
	public synchronized DateTime getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	@Override
	public EventSystem getEventSystem() {
		return es;
	}

	@Override
	public synchronized EditableOrder createOrder() {
		return orders.createOrder(this);
	}

	@Override
	public EventType OnOrderTrade() {
		return orders.OnOrderTrade();
	}

	@Override
	public synchronized Counter getOrderNumerator() {
		return orderNumerator;
	}

	@Override
	public synchronized Order createOrder(Account account, Direction dir,
			Security security, long qty, double price)
	{
		return createOrder(account, dir, security, qty, price, null);
	}

	@Override
	public synchronized Order createOrder(Account account, Direction dir,
			Security security, long qty)
	{
		return createOrder(account, dir, security, qty, null);
	}
	
	@Override
	public synchronized Order createOrder(Account account, Direction dir,
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
	public synchronized Order createOrder(Account account, Direction dir,
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
	public EventType OnRequestSecurityError() {
		return dispatcher.OnRequestSecurityError();
	}

	@Override
	public synchronized
		void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		Object args[] = { descr, errorCode, errorMsg };
		logger.error("TODO: fire request {} error: [{}] {}", args);
		dispatcher.fireSecurityRequestError(descr, errorCode, errorMsg);
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, DateTime time) {
		return scheduler.schedule(task, time);
	}

	@Override
	public synchronized TaskHandler
		schedule(Runnable task, DateTime firstTime, long period)
	{
		return scheduler.schedule(task, firstTime, period);
	}

	@Override
	public synchronized TaskHandler schedule(Runnable task, long delay) {
		return scheduler.schedule(task, delay);
	}

	@Override
	public synchronized
		TaskHandler schedule(Runnable task, long delay, long period)
	{
		return scheduler.schedule(task, delay, period);
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, DateTime firstTime, long period)
	{
		return scheduler.scheduleAtFixedRate(task, firstTime, period);		
	}

	@Override
	public synchronized TaskHandler
		scheduleAtFixedRate(Runnable task, long delay, long period)
	{
		return scheduler.scheduleAtFixedRate(task, delay, period);
	}

	@Override
	public synchronized void cancel(Runnable task) {
		scheduler.cancel(task);
	}
	
	@Override
	public synchronized boolean scheduled(Runnable task) {
		return scheduler.scheduled(task);
	}

	@Override
	public synchronized TaskHandler getTaskHandler(Runnable task) {
		return scheduler.getTaskHandler(task);
	}

	@Override
	public synchronized T getServiceLocator() {
		return serviceLocator;
	}

	@Override
	public synchronized void setServiceLocator(T locator) {
		serviceLocator = locator;
	}

	@Override
	public synchronized void setStarter(StarterQueue starter) {
		this.starter = starter;
	}

	/**
	 * Установить планировщик задач.
	 * <P>
	 * @param scheduler планировщик
	 */
	public synchronized void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Получить планировщик задач.
	 * <p>
	 * @return планировщик
	 */
	public synchronized Scheduler getScheduler() {
		return scheduler;
	}
	
	/** 
	 * Получить контроллер терминала.
	 * <p>
	 * @return контроллер терминала
	 */
	public synchronized TerminalController getTerminalController() {
		return controller;
	}
	
	/**
	 * Получить диспетчер событий терминала.
	 * <p>
	 * @return диспетчер событий
	 */
	public synchronized TerminalEventDispatcher getTerminalEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить хранилище инструментов.
	 * <p>
	 * @return хранилище инструментов
	 */
	public synchronized Securities getSecurityStorage() {
		return securities;
	}
	
	/**
	 * Получить хранилище портфелей.
	 * <p>
	 * @return хранилище портфелей
	 */
	public synchronized Portfolios getPortfolioStorage() {
		return portfolios;
	}
	
	/**
	 * Получить хранилище заявок.
	 * <p>
	 * @return хранилище заявок
	 */
	public synchronized Orders getOrderStorage() {
		return orders;
	}

}
