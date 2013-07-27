package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Заявка.
 */
public class OrderImpl extends EditableImpl implements EditableOrder {
	public static final int VERSION = 0x05;
	private final EventDispatcher dispatcher;
	private final EventType onRegister;
	private final EventType onRegisterFailed;
	private final EventType onCancelled;
	private final EventType onCancelFailed;
	private final EventType onFilled;
	private final EventType onPartiallyFilled;
	private final EventType onChanged;
	private final EventType onDone;
	private final EventType onFailed;
	private final EventType onTrade;
	private Account account;
	private SecurityDescriptor descr;
	private Direction direction;
	private Integer id;
	private Double price;
	private Long qty;
	private Long qtyRest;
	private OrderStatus status = OrderStatus.PENDING, prevStatus;
	private OrderType type;
	private Double execVolume = 0.0d;
	private Double avgExecPrice = null;
	private final List<OrderStateHandler> stateHandlers;
	private final Terminal terminal;
	private Date time,lastChangeTime;
	private final LinkedList<Trade> trades = new LinkedList<Trade>();
	private final OrderSystemInfo systemInfo = new OrderSystemInfo();
	private OrderActivator activator;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onRegister тип события
	 * @param onRegisterFailed тип события
	 * @param onCancelled тип события
	 * @param onCancelFailed тип события
	 * @param onFilled тип события
	 * @param onPartiallyFilled тип события
	 * @param onChanged тип события
	 * @param onDone тип события
	 * @param onFailed тип события
	 * @param onTrade тип события
	 * @param stateHandlers набор генераторов событий 
	 * @param terminal терминал заявки
	 */
	public OrderImpl(EventDispatcher dispatcher,
					 EventType onRegister,
					 EventType onRegisterFailed,
					 EventType onCancelled,
					 EventType onCancelFailed,
					 EventType onFilled,
					 EventType onPartiallyFilled,
					 EventType onChanged,
					 EventType onDone,
					 EventType onFailed,
					 EventType onTrade,
					 List<OrderStateHandler> stateHandlers,
					 Terminal terminal)
	{
		super();
		this.dispatcher = dispatcher;
		this.onRegister = onRegister;
		this.onRegisterFailed = onRegisterFailed;
		this.onCancelled = onCancelled;
		this.onCancelFailed = onCancelFailed;
		this.onFilled = onFilled;
		this.onPartiallyFilled = onPartiallyFilled;
		this.onChanged = onChanged;
		this.onDone = onDone;
		this.onFailed = onFailed;
		this.onTrade = onTrade;
		this.stateHandlers = stateHandlers;
		this.terminal = terminal;
	}
	
	/**
	 * Получить терминал заявки.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить копию списка генераторов событий.
	 * <p>
	 * @return список генераторов событий
	 */
	public List<OrderStateHandler> getStateHandlers() {
		return new LinkedList<OrderStateHandler>(stateHandlers);
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
	public EventType OnRegistered() {
		return onRegister;
	}

	@Override
	public EventType OnRegisterFailed() {
		return onRegisterFailed;
	}

	@Override
	public EventType OnCancelled() {
		return onCancelled;
	}

	@Override
	public EventType OnCancelFailed() {
		return onCancelFailed;
	}

	@Override
	public EventType OnFilled() {
		return onFilled;
	}

	@Override
	public EventType OnPartiallyFilled() {
		return onPartiallyFilled;
	}

	@Override
	public EventType OnChanged() {
		return onChanged;
	}

	@Override
	public EventType OnDone() {
		return onDone;
	}
	
	@Override
	public EventType OnFailed() {
		return onFailed;
	}

	@Override
	public synchronized Integer getId() {
		return id;
	}

	@Override
	public synchronized Direction getDirection() {
		return direction;
	}

	@Override
	public synchronized OrderType getType() {
		return type;
	}

	@Override
	public Portfolio getPortfolio() throws PortfolioException {
		return terminal.getPortfolio(getAccount());
	}

	@Override
	public synchronized Account getAccount() {
		return account;
	}

	@Override
	public Security getSecurity() throws SecurityException {
		return terminal.getSecurity(getSecurityDescriptor());
	}

	@Override
	public synchronized OrderStatus getStatus() {
		return status;
	}

	@Override
	public synchronized Long getQty() {
		return qty;
	}

	@Override
	public synchronized Long getQtyRest() {
		return qtyRest;
	}

	@Override
	public synchronized Double getPrice() {
		return price;
	}

	@Override
	public synchronized void setStatus(OrderStatus status) {
		if ( status != this.status ) {
			this.prevStatus = this.status;
			this.status = status;
			setChanged(EditableOrder.STATUS_CHANGED);
		}
	}

	@Override
	public synchronized void setQtyRest(Long qty) {
		if ( qty == null ? this.qtyRest != null : ! qty.equals(this.qtyRest) ) {
			this.qtyRest = qty;
			setChanged();
		}
	}

	@Override
	public synchronized void setId(Integer id) {
		if ( id == null ? this.id != null : ! id.equals(this.id) ) {
			this.id = id;
			setChanged();
		}
	}

	@Override
	public synchronized void setDirection(Direction dir) {
		if ( dir != this.direction ) {
			this.direction = dir;
			setChanged();
		}
	}

	@Override
	public synchronized void setType(OrderType type) {
		if ( type != this.type ) {
			this.type = type;
			setChanged();
		}
	}

	@Override
	public synchronized void setAccount(Account account) {
		if ( account == null ? this.account != null
				: ! account.equals(this.account) )
		{
			this.account = account;
			setChanged();
		}
	}

	@Override
	public synchronized void setSecurityDescriptor(SecurityDescriptor descr) {
		if ( descr == null ? this.descr != null : ! descr.equals(this.descr) ) {
			this.descr = descr;
			setChanged();
		}
	}

	@Override
	public synchronized void setQty(Long qty) {
		if ( qty == null ? this.qty != null : ! qty.equals(this.qty) ) {
			this.qty = qty;
			setChanged();
		}
	}

	@Override
	public synchronized void setPrice(Double price) {
		if ( price == null ? this.price != null : ! price.equals(this.price) ) {
			this.price = price;
			setChanged();
		}
	}

	@Override
	public synchronized void fireChangedEvent() {
		for ( int i = 0; i < stateHandlers.size(); i ++ ) {
			stateHandlers.get(i).handle(this);
		}
	}

	@Override
	public synchronized SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}

	@Override
	public synchronized Double getExecutedVolume() {
		return execVolume;
	}
	
	@Override
	public synchronized Double getAvgExecutedPrice() {
		return avgExecPrice;
	}

	@Override
	public synchronized void setExecutedVolume(Double value) {
		if ( value == null ? execVolume != null : ! value.equals(execVolume) ) {
			execVolume = value;
			setChanged();
		}
	}
	
	@Override
	public synchronized void setAvgExecutedPrice(Double value) {
		if (value == null ? avgExecPrice != null :!value.equals(avgExecPrice)) {
			avgExecPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized OrderStatus getPreviousStatus() {
		return prevStatus;
	}

	@Override
	public synchronized Date getTime() {
		return time;
	}

	@Override
	public synchronized Date getLastChangeTime() {
		return lastChangeTime;
	}

	@Override
	public synchronized void setTime(Date value) {
		if ( value == null ? time != null : ! value.equals(time) ) {
			time = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setLastChangeTime(Date value) {
		if ( value == null ? lastChangeTime != null
				: ! value.equals(lastChangeTime) )
		{
			lastChangeTime = value;
			setChanged();
		}
	}

	@Override
	public EventType OnTrade() {
		return onTrade;
	}

	@Override
	public synchronized List<Trade> getTrades() {
		return trades;
	}

	@Override
	public synchronized void addTrade(Trade newTrade) {
		trades.add(newTrade);
		Collections.sort(trades);
		int execQty = 0;
		double sumByPrice = 0.0d;
		double sumByVolume = 0.0d;
		for ( Trade trade : trades ) {
			synchronized ( trade ) {
				execQty += trade.getQty();
				sumByPrice += trade.getPrice() * trade.getQty();
				sumByVolume += trade.getVolume();
			}
		}
		setQtyRest(qty - execQty);
		setAvgExecutedPrice(sumByPrice / execQty);
		setExecutedVolume(sumByVolume);
	}

	@Override
	public void fireTradeEvent(Trade trade) {
		dispatcher.dispatch(new OrderTradeEvent(onTrade, this, trade));
	}

	@Override
	public void clearAllEventListeners() {
		dispatcher.close();
	}

	@Override
	public synchronized boolean hasTrade(long tradeId) {
		for ( Trade trade : trades ) {
			if ( tradeId == trade.getId() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized Date getLastTradeTime() {
		return trades.size() == 0 ? null : trades.getLast().getTime();
	}

	@Override
	public synchronized Trade getLastTrade() {
		return trades.size() == 0 ? null : trades.getLast();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderImpl.class ) {
			return false;
		}
		OrderImpl o = (OrderImpl) other;
		return new EqualsBuilder()
			.append(o.account, account)
			.append(o.descr, descr)
			.append(o.direction, direction)
			.append(o.dispatcher, dispatcher)
			.append(o.stateHandlers, stateHandlers)
			.append(o.id, id)
			.append(o.lastChangeTime, lastChangeTime)
			.append(o.onCancelFailed, onCancelFailed)
			.append(o.onCancelled, onCancelled)
			.append(o.onChanged, onChanged)
			.append(o.onDone, onDone)
			.append(o.onFailed, onFailed)
			.append(o.onFilled, onFilled)
			.append(o.onPartiallyFilled, onPartiallyFilled)
			.append(o.onRegister, onRegister)
			.append(o.onRegisterFailed, onRegisterFailed)
			.append(o.onTrade, onTrade)
			.append(o.price, price)
			.append(o.qty, qty)
			.append(o.status, status)
			.append(o.terminal, terminal)
			.append(o.time, time)
			.append(o.trades, trades)
			.append(o.type, type)
			.append(o.isAvailable(), isAvailable())
			.append(o.avgExecPrice, avgExecPrice)
			.append(o.execVolume, execVolume)
			.append(o.qtyRest, qtyRest)
			.append(o.systemInfo, systemInfo)
			.append(o.activator, activator)
			.isEquals();
	}
	
	@Override
	public OrderSystemInfo getSystemInfo() {
		return systemInfo;
	}

	@Override
	public synchronized OrderActivator getActivator() {
		return activator;
	}

	@Override
	public synchronized void setActivator(OrderActivator value) {
		if ( value == null ? activator != null : ! value.equals(activator) ) {
			this.activator = value;
			setChanged();
		}
	}

}
