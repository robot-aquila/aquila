package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;

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
 * <p>
 * 2012-06-02<br>
 * $Id: TerminalImpl.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class TerminalImpl implements EditableTerminal {
	private static final Logger logger;
	private final EventSystem es;
	private final EditableSecurities securities;
	private final EditablePortfolios portfolios;
	private final Starter starter;
	private final EditableOrders orders;
	private final EditableOrders stopOrders;
	private final EventDispatcher dispatcher;
	private final EventType onConnected,onDisconnected,onStarted,
		onStopped,onPanic;
	private volatile TerminalState state = TerminalState.STOPPED;
	private final TerminalController controller;
	private OrderProcessor orderProcessor;
	private final Timer timer;
	
	static {
		logger = LoggerFactory.getLogger(TerminalImpl.class);
	}
	
	/**
	 * Создать объект.
	 * <p>
	 * @param eventSystem фасад подсистемы событий
	 * @param starter пускач
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param stopOrders набор стоп-заявок
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события при подключении
	 * @param onDisconnected тип события при отключении
	 * @param onStarted тип события при старте терминала
	 * @param onStopped тип события при останове терминала
	 * @param onPanic тип события при паническом состоянии терминала
	 */
	public TerminalImpl(EventSystem eventSystem,
						Starter starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						EditableOrders stopOrders,
						EventDispatcher dispatcher,
						EventType onConnected,
						EventType onDisconnected,
						EventType onStarted,
						EventType onStopped, EventType onPanic)
	{
		this(eventSystem, new TimerLocal(), starter,
				securities, portfolios, orders, stopOrders,
				new TerminalController(),
				dispatcher, onConnected, onDisconnected,
				onStarted, onStopped, onPanic);
	}
	
	/**
	 * Создать объект.
	 * <p>
	 * @param eventSystem фасад подсистемы событий
	 * @param timer таймер
	 * @param starter пускач
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param stopOrders набор стоп-заявок
	 * @param controller контроллер терминала
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события при подключении
	 * @param onDisconnected тип события при отключении
	 * @param onStarted тип события при старте терминала
	 * @param onStopped тип события при останове терминала
	 * @param onPanic тип события при паническом состоянии терминала
	 */
	public TerminalImpl(EventSystem eventSystem,
						Timer timer,
						Starter starter,
						EditableSecurities securities,
						EditablePortfolios portfolios,
						EditableOrders orders,
						EditableOrders stopOrders,
						TerminalController controller,
						EventDispatcher dispatcher,
						EventType onConnected,
						EventType onDisconnected,
						EventType onStarted,
						EventType onStopped,
						EventType onPanic)
	{
		super();
		if ( eventSystem == null ) {
			throw new NullPointerException("Event system cannot be null");
		}
		this.es = eventSystem;
		if ( starter == null ) {
			throw new NullPointerException("Starter cannot be null");
		}
		this.starter = starter;
		if ( securities == null ) {
			throw new NullPointerException("Securities cannot be null");
		}
		this.securities = securities;
		if ( portfolios == null ) {
			throw new NullPointerException("Portfolios cannot be null");
		}
		this.portfolios = portfolios;
		if ( orders == null ) {
			throw new NullPointerException("Orders cannot be null");
		}
		this.orders = orders;
		if ( stopOrders == null ) {
			throw new NullPointerException("Stop orders cannot be null");
		}
		this.stopOrders = stopOrders;
		if ( controller == null ) {
			throw new NullPointerException("Controller cannot be null");
		}
		this.controller = controller;
		if ( dispatcher == null ) {
			throw new NullPointerException("Event dispatcher cannot be null");
		}
		this.dispatcher = dispatcher;
		if ( onConnected == null || onDisconnected == null || onStarted == null
				|| onStopped == null || onPanic == null )
		{
			throw new NullPointerException("Event type cannot be null");
		}
		this.onConnected = onConnected;
		this.onDisconnected = onDisconnected;
		this.onStarted = onStarted;
		this.onStopped = onStopped;
		this.onPanic = onPanic;
		if ( timer == null ) {
			throw new NullPointerException("Timer cannot be null");
		}
		this.timer = timer;
	}
	
	/**
	 * Получить экземпляр таймера.
	 * <p>
	 * @return таймер
	 */
	public Timer getTimer() {
		return timer;
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
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	public synchronized OrderProcessor getOrderProcessorInstance() {
		return orderProcessor;
	}
	
	@Override
	public synchronized
		void setOrderProcessorInstance(OrderProcessor processor)
	{
		this.orderProcessor = processor;
	}
	
	@Override
	public EditableSecurities getSecuritiesInstance() {
		return securities;
	}
	
	@Override
	public EditablePortfolios getPortfoliosInstance() {
		return portfolios;
	}
	
	@Override
	public Starter getStarter() {
		return starter;
	}
	
	@Override
	public EditableOrders getOrdersInstance() {
		return orders;
	}
	
	@Override
	public EditableOrders getStopOrdersInstance() {
		return stopOrders;
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
	public Portfolio getDefaultPortfolio() throws PortfolioException {
		return portfolios.getDefaultPortfolio();
	}

	@Override
	public boolean isOrderExists(long id) {
		return orders.isOrderExists(id);
	}

	@Override
	public List<Order> getOrders() {
		return orders.getOrders();
	}

	@Override
	public Order getOrder(long id) throws OrderException {
		return orders.getOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return orders.OnOrderAvailable();
	}

	@Override
	public boolean isStopOrderExists(long id) {
		return stopOrders.isOrderExists(id);
	}

	@Override
	public List<Order> getStopOrders() {
		return stopOrders.getOrders();
	}

	@Override
	public Order getStopOrder(long id) throws OrderException {
		return stopOrders.getOrder(id);
	}

	@Override
	public EventType OnStopOrderAvailable() {
		return stopOrders.OnOrderAvailable();
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
		orderProcessor.placeOrder(order);
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		orderProcessor.cancelOrder(order);
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
	public int getStopOrdersCount() {
		return stopOrders.getOrdersCount();
	}

	@Override
	public EventType OnStopOrderChanged() {
		return stopOrders.OnOrderChanged();
	}

	@Override
	public EventType OnStopOrderCancelFailed() {
		return stopOrders.OnOrderCancelFailed();
	}

	@Override
	public EventType OnStopOrderCancelled() {
		return stopOrders.OnOrderCancelled();
	}

	@Override
	public EventType OnStopOrderDone() {
		return stopOrders.OnOrderDone();
	}

	@Override
	public EventType OnStopOrderFailed() {
		return stopOrders.OnOrderFailed();
	}

	@Override
	public EventType OnStopOrderRegistered() {
		return stopOrders.OnOrderRegistered();
	}

	@Override
	public EventType OnStopOrderRegisterFailed() {
		return stopOrders.OnOrderRegisterFailed();
	}

	@Override
	public void fireOrderAvailableEvent(Order order) {
		orders.fireOrderAvailableEvent(order);
	}

	@Override
	public EditableOrder getEditableOrder(long id)
		throws OrderNotExistsException
	{
		return orders.getEditableOrder(id);
	}

	@Override
	public void registerOrder(long id, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		orders.registerOrder(id, order);
	}

	@Override
	public void purgeOrder(long id) {
		orders.purgeOrder(id);
	}

	@Override
	public boolean isPendingOrder(long transId) {
		return orders.isPendingOrder(transId);
	}

	@Override
	public void registerPendingOrder(long transId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		orders.registerPendingOrder(transId, order);
	}

	@Override
	public void purgePendingOrder(long transId) {
		orders.purgePendingOrder(transId);
	}

	@Override
	public EditableOrder getPendingOrder(long transId)
		throws OrderNotExistsException
	{
		return orders.getPendingOrder(transId);
	}

	@Override
	public void fireStopOrderAvailableEvent(Order order) {
		stopOrders.fireOrderAvailableEvent(order);
	}

	@Override
	public EditableOrder getEditableStopOrder(long id)
		throws OrderNotExistsException
	{
		return stopOrders.getEditableOrder(id);
	}

	@Override
	public void registerStopOrder(long id, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		stopOrders.registerOrder(id, order);
	}

	@Override
	public void purgeStopOrder(long id) {
		stopOrders.purgeOrder(id);
	}

	@Override
	public boolean isPendingStopOrder(long transId) {
		return stopOrders.isPendingOrder(transId);
	}

	@Override
	public void registerPendingStopOrder(long transId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		stopOrders.registerPendingOrder(transId, order);
	}

	@Override
	public void purgePendingStopOrder(long transId) {
		stopOrders.purgePendingOrder(transId);
	}

	@Override
	public EditableOrder getPendingStopOrder(long transId)
		throws OrderNotExistsException
	{
		return stopOrders.getPendingOrder(transId);
	}

	@Override
	public void firePortfolioAvailableEvent(Portfolio portfolio) {
		portfolios.firePortfolioAvailableEvent(portfolio);
	}

	@Override
	public EditablePortfolio getEditablePortfolio(Account account)
		throws PortfolioException
	{
		return portfolios.getEditablePortfolio(account);
	}

	@Override
	public EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
			throws PortfolioException
	{
		return portfolios.createPortfolio(terminal, account);
	}
	
	@Override
	public EditablePortfolio createPortfolio(Account account)
		throws PortfolioException
	{
		return portfolios.createPortfolio(this, account);
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		portfolios.setDefaultPortfolio(portfolio);
	}

	@Override
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr) throws SecurityNotExistsException {
		return securities.getEditableSecurity(descr);
	}

	@Override
	public void fireSecurityAvailableEvent(Security security) {
		securities.fireSecurityAvailableEvent(security);
	}

	@Override
	public EventType OnConnected() {
		return onConnected;
	}

	@Override
	public EventType OnDisconnected() {
		return onDisconnected;
	}

	@Override
	public synchronized void fireTerminalConnectedEvent() {
		if ( state == TerminalState.STARTED ) {
			state = TerminalState.CONNECTED;
			dispatcher.dispatch(new EventImpl(onConnected));
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
			dispatcher.dispatch(new EventImpl(onDisconnected));
			logger.info("Terminal disconnected");
		} else {
			logger.debug("Skip disconnected event request cuz {}", state);
		}
	}
	
	@Override
	public void fireTerminalStartedEvent() {
		dispatcher.dispatch(new EventImpl(onStarted));
	}
	
	@Override
	public void fireTerminalStoppedEvent() {
		dispatcher.dispatch(new EventImpl(onStopped));
	}

	@Override
	public EditableOrder movePendingOrder(long transId, long orderId)
			throws OrderException
	{
		return orders.movePendingOrder(transId, orderId);
	}

	@Override
	public EditableOrder movePendingStopOrder(long transId, long orderId)
			throws OrderException
	{
		return stopOrders.movePendingOrder(transId,orderId);
	}

	@Override
	public EventType OnStarted() {
		return onStarted;
	}

	@Override
	public EventType OnStopped() {
		return onStopped;
	}

	@Override
	public EventType OnPanic() {
		return onPanic;
	}

	@Override
	public void firePanicEvent(int code, String msgId) {
		firePanicEvent(code, msgId, new Object[] { });
	}

	@Override
	public void firePanicEvent(int code, String msgId, Object[] args) {
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
	public EventType OnStopOrderFilled() {
		return stopOrders.OnOrderFilled();
	}

	@Override
	public synchronized Date getCurrentTime() {
		return timer.getCurrentTime();
	}

	@Override
	public EventSystem getEventSystem() {
		return es;
	}

	@Override
	public EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return securities.createSecurity(terminal, descr);
	}

	@Override
	public EditableSecurity createSecurity(SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return securities.createSecurity(this, descr);
	}

	@Override
	public boolean hasPendingOrders() {
		return orders.hasPendingOrders();
	}

	@Override
	public EditableOrder createOrder(EditableTerminal terminal) {
		return orders.createOrder(terminal);
	}

	@Override
	public boolean hasPendingStopOrders() {
		return stopOrders.hasPendingOrders();
	}

	@Override
	public EditableOrder createStopOrder(EditableTerminal terminal) {
		return stopOrders.createOrder(terminal);
	}

	@Override
	public EditableOrder createOrder() {
		return orders.createOrder(this);
	}

	@Override
	public EditableOrder createStopOrder() {
		return stopOrders.createOrder(this);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TerminalImpl.class ) {
			return false;
		}
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
			.append(o.stopOrders, stopOrders)
			.append(o.timer, timer)
			.isEquals();
	}
	
	@Override
	public Order createMarketOrderB(Account account, Security sec, long qty)
		throws OrderException
	{
		EditableOrder order = orders.createOrder(this);
		order.setDirection(OrderDirection.BUY);
		fillMarketOrder(order, account, sec, qty);
		return order;
	}

	@Override
	public Order createMarketOrderS(Account account, Security sec, long qty)
		throws OrderException
	{
		EditableOrder order = orders.createOrder(this);
		order.setDirection(OrderDirection.SELL);
		fillMarketOrder(order, account, sec, qty);
		return order;
	}
	
	@Override
	public Order createLimitOrderB(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		EditableOrder order = orders.createOrder(this);
		order.setDirection(OrderDirection.BUY);
		fillLimitOrder(order, account, sec, price, qty);
		return order;
	}

	@Override
	public Order createLimitOrderS(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		EditableOrder order = orders.createOrder(this);
		order.setDirection(OrderDirection.SELL);
		fillLimitOrder(order, account, sec, price, qty);
		return order;
	}

	@Override
	public Order createStopLimitB(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		EditableOrder order = stopOrders.createOrder(this);
		order.setDirection(OrderDirection.BUY);
		fillStopLimit(order, account, sec, stopPrice, price, qty);
		return order;
	}

	@Override
	public Order createStopLimitS(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		EditableOrder order = stopOrders.createOrder(this);
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
	}
	
	private void fillLimitOrder(EditableOrder order, Account account,
			Security sec, double price, long qty) throws OrderException
	{
		fillCommonOrder(order, account, sec, qty);
		order.setType(OrderType.LIMIT);
		order.setQtyRest(qty);
		order.setPrice(price);
	}
	
	private void fillMarketOrder(EditableOrder order, Account account,
			Security sec, long qty) throws OrderException
	{
		fillCommonOrder(order, account, sec, qty);
		order.setType(OrderType.MARKET);
		order.setQtyRest(qty);
	}
	
	private void fillCommonOrder(EditableOrder order, Account account,
			Security sec, long qty) throws OrderException
	{
		order.setTime(getCurrentTime());
		order.setAccount(account);
		order.setQty(qty);
		order.setSecurityDescriptor(sec.getDescriptor());
		order.setStatus(OrderStatus.PENDING);
	}

	@Override
	public EventType OnOrderTrade() {
		return orders.OnOrderTrade();
	}

}
