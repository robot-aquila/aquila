package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.DataProvider;

/**
 * Terminal model implementation.
 */
public class TerminalImpl implements EditableTerminal {
	private final LID lid;
	private final Lock lock;
	private final EventQueue queue;
	private final EventDispatcher dispatcher;
	private final Map<Symbol, EditableSecurity> securities;
	private final Map<Account, EditablePortfolio> portfolios;
	private EditablePortfolio defaultPortfolio;
	private final Map<Long, EditableOrder> orders;
	private final Scheduler scheduler;
	private final String terminalID;
	private final DataProvider dataProvider;
	private final ObjectFactory objectFactory;
	private final EventType onOrderAvailable, onOrderCancelFailed,
		onOrderCancelled, onOrderExecution, onOrderDone, onOrderFailed,
		onOrderFilled, onOrderPartiallyFilled, onOrderRegistered,
		onOrderRegisterFailed, onOrderUpdate, onOrderArchived,
		onPortfolioAvailable,
		onPortfolioUpdate, onPositionAvailable, onPositionChange,
		onPositionCurrentPriceChange, onPositionUpdate, onSecurityAvailable,
		onSecuritySessionUpdate, onSecurityUpdate, onTerminalReady,
		onTerminalUnready, onSecurityMarketDepthUpdate, onSecurityBestAsk,
		onSecurityBestBid, onSecurityLastTrade, onSecurityClose,
		onOrderClose, onPositionClose, onPortfolioClose;
	private boolean closed = false;
	private boolean started = false;
	
	private static String getID(TerminalImpl terminal, String suffix) {
		return String.format("%s.%s", terminal.terminalID, suffix);
	}
	
	private EventType newEventType(String suffix) {
		return new EventTypeImpl(getID(this, suffix));
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param params - basic terminal constructor parameters
	 */
	public TerminalImpl(TerminalParams params) {
		super();
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.terminalID = params.getTerminalID();
		this.queue = params.getEventQueue();
		this.dispatcher = params.getEventDispatcher();
		this.scheduler = params.getScheduler();
		this.objectFactory = params.getObjectFactory();
		this.dataProvider = params.getDataProvider();
		this.securities = new HashMap<Symbol, EditableSecurity>();
		this.portfolios = new HashMap<Account, EditablePortfolio>();
		this.orders = new HashMap<Long, EditableOrder>();
		onOrderAvailable = newEventType("ORDER_AVAILABLE");
		onOrderCancelFailed = newEventType("ORDER_CANCEL_FAILED");
		onOrderCancelled = newEventType("ORDER_CANCELLED");
		onOrderExecution = newEventType("ORDER_DEAL");
		onOrderDone = newEventType("ORDER_DONE");
		onOrderFailed = newEventType("ORDER_FAILED");
		onOrderFilled = newEventType("ORDER_FILLED");
		onOrderPartiallyFilled = newEventType("ORDER_PARTIALLY_FILLED");
		onOrderRegistered = newEventType("ORDER_REGISTERED");
		onOrderRegisterFailed = newEventType("ORDER_REGISTER_FAILED");
		onOrderUpdate = newEventType("ORDER_UPDATE");
		onOrderArchived = newEventType("ORDER_ARCHIVED");
		onPortfolioAvailable = newEventType("PORTFOLIO_AVAILABLE");
		onPortfolioUpdate = newEventType("PORTFOLIO_UPDATE");
		onPositionAvailable = newEventType("POSITION_AVAILABLE");
		onPositionChange = newEventType("POSITION_CHANGE");
		onPositionCurrentPriceChange = newEventType("POSITION_CURRENT_PRICE_CHANGE");
		onPositionUpdate = newEventType("POSITION_UPDATE");
		onSecurityAvailable = newEventType("SECURITY_AVAILABLE");
		onSecuritySessionUpdate = newEventType("SECURITY_SESSION_UPDATE");
		onSecurityUpdate = newEventType("SECURITY_UPDATE");
		onSecurityMarketDepthUpdate = newEventType("SECURITY_MARKET_DEPTH_UPDATE");
		onSecurityBestAsk = newEventType("SECURITY_BEST_ASK");
		onSecurityBestBid = newEventType("SECURITY_BEST_BID");
		onSecurityLastTrade = newEventType("SECURITY_LAST_TRADE");
		onTerminalReady = newEventType("TERMINAL_READY");
		onTerminalUnready = newEventType("TERMINAL_UNREADY");
		onSecurityClose = newEventType("SECURITY_CLOSE");
		onOrderClose = newEventType("ORDER_CLOSE");
		onPositionClose = newEventType("POSITION_CLOSE");
		onPortfolioClose = newEventType("PORTFOLIO_CLOSE");
	}
	
	@Override
	public LID getLID() {
		return lid;
	}
	
	@Override
	public EventQueue getEventQueue() {
		return queue;
	}
	
	@Override
	public String getTerminalID() {
		return terminalID;
	}
		
	@Override
	public Set<Security> getSecurities() {
		lock.lock();
		try {
			return new HashSet<Security>(securities.values());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Security getSecurity(Symbol symbol) throws SecurityException {
		lock.lock();
		try {
			EditableSecurity security = securities.get(symbol);
			if ( security == null ) {
				throw new SecurityNotExistsException(symbol);
			}
			return security;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isSecurityExists(Symbol symbol) {
		lock.lock();
		try {
			return securities.containsKey(symbol);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public int getSecurityCount() {
		lock.lock();
		try {
			return securities.size();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public EditableSecurity getEditableSecurity(Symbol symbol) {
		lock.lock();
		try {
			if ( closed ) {
				throw new IllegalStateException();
			}
			EditableSecurity security = securities.get(symbol);
			if ( security == null ) {
				security = objectFactory.createSecurity(this, symbol);
				securities.put(symbol, security);
				security.onAvailable().addAlternateType(onSecurityAvailable);
				security.onSessionUpdate().addAlternateType(onSecuritySessionUpdate);
				security.onUpdate().addAlternateType(onSecurityUpdate);
				security.onBestAsk().addAlternateType(onSecurityBestAsk);
				security.onBestBid().addAlternateType(onSecurityBestBid);
				security.onLastTrade().addAlternateType(onSecurityLastTrade);
				security.onMarketDepthUpdate().addAlternateType(onSecurityMarketDepthUpdate);
				security.onClose().addAlternateType(onSecurityClose);
			}
			return security;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Portfolio> getPortfolios() {
		lock.lock();
		try {
			return new HashSet<Portfolio>(portfolios.values());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Portfolio getPortfolio(Account account) throws PortfolioException {
		lock.lock();
		try {
			EditablePortfolio portfolio = portfolios.get(account);
			if ( portfolio == null ) {
				throw new PortfolioNotExistsException(account);
			}
			return portfolio;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isPortfolioExists(Account account) {
		lock.lock();
		try {
			return portfolios.containsKey(account);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public EditablePortfolio getEditablePortfolio(Account account) {
		EditablePortfolio portfolio = null;
		lock.lock();
		try {
			if ( closed ) {
				throw new IllegalStateException();
			}
			portfolio = portfolios.get(account);
			if ( portfolio == null ) {
				portfolio = objectFactory.createPortfolio(this, account);
				portfolios.put(account, portfolio);
				portfolio.onAvailable().addAlternateType(onPortfolioAvailable);
				portfolio.onPositionAvailable().addAlternateType(onPositionAvailable);
				portfolio.onPositionChange().addAlternateType(onPositionChange);
				portfolio.onPositionCurrentPriceChange().addAlternateType(onPositionCurrentPriceChange);
				portfolio.onPositionUpdate().addAlternateType(onPositionUpdate);
				portfolio.onUpdate().addAlternateType(onPortfolioUpdate);
				portfolio.onClose().addAlternateType(onPortfolioClose);
				portfolio.onPositionClose().addAlternateType(onPositionClose);
			}
			if ( defaultPortfolio == null ) {
				defaultPortfolio = portfolio;
			}
		} finally {
			lock.unlock();
		}
		return portfolio;
	}
	
	@Override
	public int getPortfolioCount() {
		lock.lock();
		try {
			return portfolios.size();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Portfolio getDefaultPortfolio() throws PortfolioException {
		lock.lock();
		try {
			if ( defaultPortfolio == null ) {
				throw new PortfolioNotExistsException();
			}
			return defaultPortfolio;			
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		lock.lock();
		try {
			defaultPortfolio = portfolio;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isOrderExists(long id) {
		lock.lock();
		try {
			return orders.containsKey(id);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Order> getOrders() {
		lock.lock();
		try {
			return new HashSet<Order>(orders.values());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Order getOrder(long id) throws OrderException {
		return getEditableOrder(id);
	}
	
	@Override
	public EditableOrder getEditableOrder(long id) throws OrderNotExistsException {
		lock.lock();
		try {
			EditableOrder order = orders.get(id);
			if ( order == null ) {
				throw new OrderNotExistsException(id);
			}
			return order;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Create strict lock on terminal and portfolio.
	 * <p>
	 * @param account - portfolio to lock
	 * @return lockable object
	 */
	protected Lockable createLock(Account account) {
		try {
			return new Multilock(this, getPortfolio(account));
		} catch ( PortfolioException e ) {
			throw new IllegalStateException("Error accessing portfolio", e);
		}
	}
	
	@Override
	public EditableOrder createOrder(long id, Account account, Symbol symbol) {
		lock.lock();
		try {
			if ( closed ) {
				throw new IllegalStateException();
			}
			if ( orders.containsKey(id) ) {
				throw new IllegalArgumentException("Order already exists: " + id);
			}
			EditableOrder order = objectFactory.createOrder(this, account, symbol, id);
			order.onAvailable().addAlternateType(onOrderAvailable);
			order.onCancelFailed().addAlternateType(onOrderCancelFailed);
			order.onCancelled().addAlternateType(onOrderCancelled);
			order.onDone().addAlternateType(onOrderDone);
			order.onExecution().addAlternateType(onOrderExecution);
			order.onFailed().addAlternateType(onOrderFailed);
			order.onFilled().addAlternateType(onOrderFilled);
			order.onPartiallyFilled().addAlternateType(onOrderPartiallyFilled);
			order.onRegistered().addAlternateType(onOrderRegistered);
			order.onRegisterFailed().addAlternateType(onOrderRegisterFailed);
			order.onUpdate().addAlternateType(onOrderUpdate);
			order.onArchived().addAlternateType(onOrderArchived);
			order.onClose().addAlternateType(onOrderClose);
			orders.put(id, order);
			return order;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public EditableOrder createOrder(Account account, Symbol symbol) {
		return createOrder(dataProvider.getNextOrderID(), account, symbol);
	}
	
	@Override
	public int getOrderCount() {
		lock.lock();
		try {
			return orders.size();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void placeOrder(Order order) throws OrderException {
		if ( isClosed() ) {
			throw new IllegalStateException();
		}
		dataProvider.registerNewOrder(toEditable(order));
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		if ( isClosed() ) {
			throw new IllegalStateException();
		}
		dataProvider.cancelOrder(toEditable(order));
	}
	
	@Override
	public Order createOrder(Account account, Symbol symbol, OrderAction action,
			CDecimal qty, CDecimal price)
	{
		return createOrder(
				account,
				symbol,
				action,
				OrderType.LMT,
				qty,
				price,
				null,
				OrderStatus.PENDING,
				getCurrentTime(),
				false
			);
	}

	@Override
	public Order createOrder(Account account, Symbol symbol, OrderAction action, CDecimal qty) {
		return createOrder(
				account,
				symbol,
				action,
				OrderType.MKT,
				qty,
				null,
				null,
				OrderStatus.PENDING,
				getCurrentTime(),
				false
			);
	}
	
	@Override
	public Order createOrder(Account account, Symbol symbol, OrderType type,
			OrderAction action, CDecimal qty, CDecimal price, String comment)
	{
		return createOrder(
				account,
				symbol,
				action,
				type,
				qty,
				price,
				comment,
				OrderStatus.PENDING,
				getCurrentTime(),
				false
			);
	}

	@Override
	public SubscrHandler subscribe(Symbol symbol, MDLevel level) {
		return dataProvider.subscribe(symbol, level, this);
	}
	
	@Override
	public SubscrHandler subscribe(Account account) {
		return dataProvider.subscribe(account, this);
	}
	
	@Override
	public Instant getCurrentTime() {
		return scheduler.getCurrentTime();
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant time) {
		return scheduler.schedule(task, time);
	}

	@Override
	public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
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
	public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime, long period) {
		return scheduler.scheduleAtFixedRate(task, firstTime, period);		
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay, long period) {
		return scheduler.scheduleAtFixedRate(task, delay, period);
	}
	
	@Override
	public TaskHandler schedule(SPRunnable task) {
		return scheduler.schedule(task);
	}
	
	/**
	 * Get scheduler.
	 * <p>
	 * @return scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	@Override
	public void close() {
		List<EditableOrder> orderToClose = new LinkedList<>();
		List<EditableSecurity> securityToClose = new LinkedList<>();
		List<EditablePortfolio> portfolioToClose = new LinkedList<>();
		stop();
		lock.lock();
		try {
			if ( closed ) {
				return;
			}
			scheduler.close();
			for ( EditableOrder order : orders.values() ) {
				orderToClose.add(order);
			}
			orders.clear();
			for ( EditableSecurity security : securities.values() ) {
				securityToClose.add(security);
			}
			securities.clear();
			for ( EditablePortfolio portfolio : portfolios.values() ) {
				portfolioToClose.add(portfolio);
			}
			portfolios.clear();
			onTerminalReady.removeAlternatesAndListeners();
			onTerminalUnready.removeAlternatesAndListeners();
			onOrderAvailable.removeAlternatesAndListeners();
			onOrderCancelFailed.removeAlternatesAndListeners();
			onOrderCancelled.removeAlternatesAndListeners();
			onOrderExecution.removeAlternatesAndListeners();
			onOrderDone.removeAlternatesAndListeners();
			onOrderFailed.removeAlternatesAndListeners();
			onOrderFilled.removeAlternatesAndListeners();
			onOrderPartiallyFilled.removeAlternatesAndListeners();
			onOrderRegistered.removeAlternatesAndListeners();
			onOrderRegisterFailed.removeAlternatesAndListeners();
			onOrderUpdate.removeAlternatesAndListeners();
			onOrderArchived.removeAlternatesAndListeners();
			onPortfolioAvailable.removeAlternatesAndListeners();
			onPortfolioUpdate.removeAlternatesAndListeners();
			onPositionAvailable.removeAlternatesAndListeners();
			onPositionChange.removeAlternatesAndListeners();
			onPositionCurrentPriceChange.removeAlternatesAndListeners();
			onPositionUpdate.removeAlternatesAndListeners();
			onSecurityAvailable.removeAlternatesAndListeners();
			onSecuritySessionUpdate.removeAlternatesAndListeners();
			onSecurityUpdate.removeAlternatesAndListeners();
			onSecurityMarketDepthUpdate.removeAlternatesAndListeners();
			onSecurityBestAsk.removeAlternatesAndListeners();
			onSecurityBestBid.removeAlternatesAndListeners();
			onSecurityLastTrade.removeAlternatesAndListeners();
		} finally {
			closed = true;
			lock.unlock();
		}
		dataProvider.close();
		for ( EditableOrder order : orderToClose ) {
			order.close();
		}
		for ( EditablePortfolio portfolio : portfolioToClose ) {
			portfolio.close();
		}
		for ( EditableSecurity security : securityToClose ) {
			security.close();			
		}
	}
	
	@Override
	public EventType onTerminalReady() {
		return onTerminalReady;
	}

	@Override
	public EventType onTerminalUnready() {
		return onTerminalUnready;
	}

	@Override
	public EventType onOrderAvailable() {
		return onOrderAvailable;
	}

	@Override
	public EventType onOrderCancelFailed() {
		return onOrderCancelFailed;
	}

	@Override
	public EventType onOrderCancelled() {
		return onOrderCancelled;
	}

	@Override
	public EventType onOrderUpdate() {
		return onOrderUpdate;
	}

	@Override
	public EventType onOrderDone() {
		return onOrderDone;
	}

	@Override
	public EventType onOrderFailed() {
		return onOrderFailed;
	}

	@Override
	public EventType onOrderFilled() {
		return onOrderFilled;
	}

	@Override
	public EventType onOrderPartiallyFilled() {
		return onOrderPartiallyFilled;
	}

	@Override
	public EventType onOrderRegistered() {
		return onOrderRegistered;
	}

	@Override
	public EventType onOrderRegisterFailed() {
		return onOrderRegisterFailed;
	}

	@Override
	public EventType onOrderExecution() {
		return onOrderExecution;
	}
	
	@Override
	public EventType onOrderArchived() {
		return onOrderArchived;
	}

	@Override
	public EventType onPortfolioAvailable() {
		return onPortfolioAvailable;
	}

	@Override
	public EventType onPortfolioUpdate() {
		return onPortfolioUpdate;
	}

	@Override
	public EventType onPositionAvailable() {
		return onPositionAvailable;
	}

	@Override
	public EventType onPositionUpdate() {
		return onPositionUpdate;
	}

	@Override
	public EventType onPositionChange() {
		return onPositionChange;
	}

	@Override
	public EventType onPositionCurrentPriceChange() {
		return onPositionCurrentPriceChange;
	}

	@Override
	public EventType onSecurityAvailable() {
		return onSecurityAvailable;
	}

	@Override
	public EventType onSecurityUpdate() {
		return onSecurityUpdate;
	}

	@Override
	public EventType onSecuritySessionUpdate() {
		return onSecuritySessionUpdate;
	}
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	
	@Override
	public boolean isClosed() {
		lock.lock();
		try {
			return closed;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void start() {
		lock.lock();
		try {
			if ( closed || started ) {
				throw new IllegalStateException();
			}
			started = true;
		} finally {
			lock.unlock();
		}
		// It's OK to call start or/and stop consecutively.
		// Just one first call will delegate to data provider.
		// To call start or/and stop in proper sequence the
		// onTerminalReady/onTerminalUnready should be analyzed.
		dataProvider.subscribeRemoteObjects(this);
		dispatcher.dispatch(onTerminalReady, new TerminalEventFactory(this));
	}

	@Override
	public void stop() {
		lock.lock();
		try {
			if ( ! started ) {
				return;
			}
			started = false;
		} finally {
			lock.unlock();
		}
		dataProvider.unsubscribeRemoteObjects(this);
		dispatcher.dispatch(onTerminalUnready, new TerminalEventFactory(this));
	}
	
	static class TerminalEventFactory implements EventFactory {
		private final Terminal terminal;
		
		TerminalEventFactory(Terminal terminal) {
			this.terminal = terminal;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new TerminalEvent(type, terminal);
		}
		
	}
	
	// TODO: This method may be moved to public interface
	private EditableOrder createOrder(Account account, Symbol symbol,
			OrderAction action, OrderType type, CDecimal volume, CDecimal price,
			String comment, OrderStatus initial_status, Instant creation_time,
			boolean suppress_events)
	{
		EditableOrder order = createOrder(account, symbol);
		order.lock();
		try {
			if ( suppress_events ) order.suppressEvents();
			order.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.TYPE, type)
				.withToken(OrderField.ACTION, action)
				.withToken(OrderField.INITIAL_VOLUME, volume)
				.withToken(OrderField.CURRENT_VOLUME, volume)
				.withToken(OrderField.PRICE, price)
				.withToken(OrderField.COMMENT, comment)
				.withToken(OrderField.TIME, creation_time)
				.withToken(OrderField.STATUS, initial_status)
				.buildUpdate());
			if ( suppress_events ) order.purgeEvents();
		} finally {
			order.unlock();
		}
		return order;
	}
	
	private EditableOrder toEditable(Order order) throws OrderException {
		EditableOrder dummy = orders.get(order.getID());
		if ( order != dummy ) {
			throw new OrderOwnershipException();
		}
		return dummy;
	}

	@Override
	public EventType onSecurityMarketDepthUpdate() {
		return onSecurityMarketDepthUpdate;
	}

	@Override
	public EventType onSecurityBestBid() {
		return onSecurityBestBid;
	}

	@Override
	public EventType onSecurityBestAsk() {
		return onSecurityBestAsk;
	}

	@Override
	public EventType onSecurityLastTrade() {
		return onSecurityLastTrade;
	}
	
	@Override
	public void archiveOrders() {
		List<EditableOrder> list = new LinkedList<>();
		lock.lock();
		try {
			Iterator<Map.Entry<Long, EditableOrder>> it = orders.entrySet().iterator();
			orders.entrySet().size();
			while ( it.hasNext() ){
				Map.Entry<Long, EditableOrder> pair = it.next();
				EditableOrder order = pair.getValue();
				if ( order.getStatus().isFinal() ) {
					list.add(order);
					it.remove();
				}
			}
		} finally {
			lock.unlock();
		}
		for ( EditableOrder order : list ) {
			order.fireArchived();
			order.close();
		}
	}

	@Override
	public EventType onSecurityClose() {
		return onSecurityClose;
	}

	@Override
	public EventType onOrderClose() {
		return onOrderClose;
	}

	@Override
	public EventType onPortfolioClose() {
		return onPortfolioClose;
	}

	@Override
	public EventType onPositionClose() {
		return onPositionClose;
	}

	@Override
	public void suppressEvents() {
		dispatcher.suppressEvents();
	}

	@Override
	public void restoreEvents() {
		dispatcher.restoreEvents();
	}
	
	@Override
	public void purgeEvents() {
		dispatcher.purgeEvents();
	}

}
