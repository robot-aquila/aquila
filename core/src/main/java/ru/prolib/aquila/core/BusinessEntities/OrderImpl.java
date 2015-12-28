package ru.prolib.aquila.core.BusinessEntities;

import java.time.LocalDateTime;
import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Заявка.
 */
public class OrderImpl extends EditableImpl implements EditableOrder {
	public static final int VERSION = 0x06;
	private Account account;
	private Symbol symbol;
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
	private LocalDateTime time,lastChangeTime;
	private final LinkedList<Trade> trades = new LinkedList<Trade>();
	private final OrderSystemInfo systemInfo = new OrderSystemInfo();
	private OrderActivator activator;
	private String comment = "";
	private final OrderEventDispatcher dispatcher;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param stateHandlers набор генераторов событий 
	 * @param terminal терминал заявки
	 */
	public OrderImpl(OrderEventDispatcher dispatcher,
			List<OrderStateHandler> stateHandlers, Terminal terminal)
	{
		super();
		this.dispatcher = dispatcher;
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
	public OrderEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public EventType OnRegistered() {
		return dispatcher.OnRegistered();
	}

	@Override
	public EventType OnRegisterFailed() {
		return dispatcher.OnRegisterFailed();
	}

	@Override
	public EventType OnCancelled() {
		return dispatcher.OnCancelled();
	}

	@Override
	public EventType OnCancelFailed() {
		return dispatcher.OnCancelFailed();
	}

	@Override
	public EventType OnFilled() {
		return dispatcher.OnFilled();
	}

	@Override
	public EventType OnPartiallyFilled() {
		return dispatcher.OnPartiallyFilled();
	}

	@Override
	public EventType OnChanged() {
		return dispatcher.OnChanged();
	}

	@Override
	public EventType OnDone() {
		return dispatcher.OnDone();
	}
	
	@Override
	public EventType OnFailed() {
		return dispatcher.OnFailed();
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
		return terminal.getSecurity(getSymbol());
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
	public synchronized void setSymbol(Symbol symbol) {
		if ( symbol == null ? this.symbol != null : ! symbol.equals(this.symbol) ) {
			this.symbol = symbol;
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
	public synchronized Symbol getSymbol() {
		return symbol;
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
	public synchronized LocalDateTime getTime() {
		return time;
	}

	@Override
	public synchronized LocalDateTime getLastChangeTime() {
		return lastChangeTime;
	}

	@Override
	public synchronized void setTime(LocalDateTime value) {
		if ( value == null ? time != null : ! value.equals(time) ) {
			time = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setLastChangeTime(LocalDateTime value) {
		if ( value == null ? lastChangeTime != null
				: ! value.equals(lastChangeTime) )
		{
			lastChangeTime = value;
			setChanged();
		}
	}

	@Override
	public EventType OnTrade() {
		return dispatcher.OnTrade();
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
		dispatcher.fireTrade(this, trade);
	}

	@Override
	public void clearAllEventListeners() {
		dispatcher.removeListeners();
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
	public synchronized LocalDateTime getLastTradeTime() {
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
			.appendSuper(o.terminal == terminal)
			.append(o.account, account)
			.append(o.symbol, symbol)
			.append(o.direction, direction)
			.append(o.id, id)
			.append(o.lastChangeTime, lastChangeTime)
			.append(o.price, price)
			.append(o.qty, qty)
			.append(o.status, status)
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
	
	/**
	 * Каждая заявка уникальна и ситуации когда у двух разных заявок может
	 * быть одинаковый хэш-код возникать не должны. В противном случае,
	 * алгоритмы оперирующие наборами заявок не будут работать корректно.
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public synchronized void setComment(String value) {
		if ( value == null ? comment != null : ! value.equals(comment) ) {
			comment = value;
			setChanged();
		}
	}
	
	@Override
	public synchronized String getComment() {
		return comment;
	}

}
