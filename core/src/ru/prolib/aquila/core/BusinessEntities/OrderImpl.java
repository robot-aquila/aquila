package ru.prolib.aquila.core.BusinessEntities;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderHandler;

/**
 * Заявка.
 * <p>
 * 2012-09-22<br>
 * $Id: OrderImpl.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderImpl extends EditableImpl implements EditableOrder {
	public static final int VERSION = 0x03;
	public static final Integer STATUS_CHANGED = 0x01;
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
	private OrderDirection direction;
	private Long id;
	private Double price;
	private Long qty;
	private Long qtyRest;
	private OrderStatus status = OrderStatus.PENDING, prevStatus;
	private Long transId;
	private OrderType type;
	private Long linkedOrderId;
	private Double stopLimitPrice,takeProfitPrice;
	private Price offset,spread;
	private Double execVolume = 0.0d;
	private Double avgExecPrice = 0.0d;
	private final List<OrderHandler> eventHandlers;
	private final Terminal terminal;
	private Date time,lastChangeTime;
	private final List<Trade> trades = new Vector<Trade>();
	
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
	 * @param eventHandlers набор генераторов событий 
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
					 List<OrderHandler> eventHandlers,
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
		this.eventHandlers = eventHandlers;
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
	public List<OrderHandler> getEventHandlers() {
		return new LinkedList<OrderHandler>(eventHandlers);
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
	public synchronized Long getId() {
		return id;
	}

	@Override
	public synchronized OrderDirection getDirection() {
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
			setChanged(STATUS_CHANGED);
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
	public synchronized void setId(Long id) {
		if ( id == null ? this.id != null : ! id.equals(this.id) ) {
			this.id = id;
			setChanged();
		}
	}

	@Override
	public synchronized void setDirection(OrderDirection dir) {
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
	public synchronized Long getTransactionId() {
		return transId;
	}

	@Override
	public synchronized void setTransactionId(Long id) {
		if ( id == null ? this.transId != null : ! id.equals(this.transId) ) {
			this.transId = id;
			setChanged();
		}
	}

	@Override
	public synchronized void fireChangedEvent() throws EditableObjectException {
		for ( int i = 0; i < eventHandlers.size(); i ++ ) {
			eventHandlers.get(i).handle(this);
		}
	}

	@Override
	public synchronized Long getLinkedOrderId() {
		return linkedOrderId;
	}

	@Override
	public synchronized Double getStopLimitPrice() {
		return stopLimitPrice;
	}

	@Override
	public synchronized void setLinkedOrderId(Long id) {
		if ( id == null ? linkedOrderId != null : ! id.equals(linkedOrderId) ) {
			linkedOrderId = id;
			setChanged();
		}
	}

	@Override
	public synchronized void setStopLimitPrice(Double price) {
		if ( price == null ? stopLimitPrice != null
						   : ! price.equals(stopLimitPrice) )
		{
			stopLimitPrice = price;
			setChanged();
		}
	}

	@Override
	public synchronized Double getTakeProfitPrice() {
		return takeProfitPrice;
	}

	@Override
	public synchronized Price getOffset() {
		return offset;
	}

	@Override
	public synchronized Price getSpread() {
		return spread;
	}

	@Override
	public synchronized void setTakeProfitPrice(Double price) {
		if ( price == null ? takeProfitPrice != null
						   : ! price.equals(takeProfitPrice) )
		{
			takeProfitPrice = price;
			setChanged();
		}
	}

	@Override
	public synchronized void setOffset(Price value) {
		if ( value == null ? offset != null : ! value.equals(offset) ) {
			offset = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setSpread(Price value) {
		if ( value == null ? spread != null : ! value.equals(spread) ) {
			spread = value;
			setChanged();
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
	public synchronized void addTrade(Trade trade) {
		trades.add(trade);
		Collections.sort(trades);
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

}
