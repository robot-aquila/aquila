package ru.prolib.aquila.core.BusinessEntities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * Инструмент торговли.
 */
public class SecurityImpl extends EditableImpl implements EditableSecurity {
	private final Terminal terminal;
	private final SecurityDescriptor descr;
	private final EventDispatcher dispatcher;
	private final EventType onChanged;
	private final EventType onTrade;
	private Double minPrice;
	private Double maxPrice;
	private Double stepPrice;
	private Double lastPrice;
	private double stepSize;
	private int lotSize;
	private int decimals;
	private Trade lastTrade = null;
	private String displayName;
	private Double askPrice,bidPrice;
	private Long askSize,bidSize;
	private Double open,close,high,low;
	private SecurityStatus status = SecurityStatus.STOPPED;
	private DecimalFormat priceFormat;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param descr дескриптор инструмента
	 * @param dispatcher диспетчер событий
	 * @param onChanged тип события: при изменении атрибутов инструмента
	 * @param onTrade тип события: новая (анонимная) сделка по инструменту
	 */
	public SecurityImpl(Terminal terminal, SecurityDescriptor descr,
					    EventDispatcher dispatcher,
					    EventType onChanged,
					    EventType onTrade)
	{
		super();
		this.terminal = terminal;
		this.descr = descr;
		this.dispatcher = dispatcher;
		this.onChanged = onChanged;
		this.onTrade = onTrade;
		changePriceFormat();
	}
	
	/**
	 * Получить используемый диспетчер событий
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public String getCode() {
		return descr.getCode();
	}

	@Override
	public String getClassCode() {
		return descr.getClassCode();
	}

	@Override
	public synchronized int getLotSize() {
		return lotSize;
	}

	@Override
	public synchronized void setLotSize(int value) {
		if ( value != lotSize ) {
			lotSize = value;
			setChanged();
		}
	}

	@Override
	public synchronized Double getMaxPrice() {
		return maxPrice;
	}

	@Override
	public synchronized void setMaxPrice(Double value) {
		if ( (maxPrice == null && value != null)
		  || (maxPrice != null && ! maxPrice.equals(value)) )
		{
			maxPrice = value;
			setChanged();			
		}
	}

	@Override
	public synchronized Double getMinPrice() {
		return minPrice;
	}
	
	@Override
	public synchronized void setMinPrice(Double value) {
		if ( (minPrice == null && value != null)
		  || (minPrice != null && ! minPrice.equals(value)) )
		{
			minPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized Double getMinStepPrice() {
		return stepPrice;
	}
	
	@Override
	public synchronized void setMinStepPrice(Double value) {
		if ( (stepPrice == null && value != null)
		  || (stepPrice != null && ! stepPrice.equals(value)) )
		{
			stepPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized double getMinStepSize() {
		return stepSize;
	}
	
	@Override
	public synchronized void setMinStepSize(double value) {
		if ( value != stepSize ) {
			stepSize = value;
			setChanged();
		}
	}

	@Override
	public synchronized int getPrecision() {
		return decimals;
	}
	
	@Override
	public synchronized void setPrecision(int value) {
		if ( value != decimals ) {
			decimals = value;
			setChanged();
			changePriceFormat();
		}
	}

	@Override
	public synchronized EventType OnChanged() {
		return onChanged;
	}

	@Override
	public synchronized EventType OnTrade() {
		return onTrade;
	}

	@Override
	public String shrinkPrice(double price) {
		double mPrice = Math.round(price / stepSize) * stepSize;
		if ( decimals == 0 ) {
			mPrice = Math.round(mPrice);
		} else {
			double mul = Math.pow(10, decimals);
			mPrice = Math.round(mPrice * mul) / mul;
		}
		return priceFormat.format(mPrice);
	}
	
	@Override
	public void fireTradeEvent(Trade trade) {
		synchronized ( this ) {
			lastTrade = trade;
		}
		dispatcher.dispatch(new SecurityTradeEvent(onTrade, this, trade));
	}
	
	@Override
	public void fireChangedEvent() {
		dispatcher.dispatch(new SecurityEvent(onChanged, this));
	}

	@Override
	public synchronized Trade getLastTrade() {
		return lastTrade;
	}

	@Override
	public SecurityDescriptor getDescriptor() {
		return descr;
	}

	@Override
	public synchronized Double getLastPrice() {
		return lastPrice;
	}

	@Override
	public synchronized void setLastPrice(Double value) {
		if ( (lastPrice == null && value != null)
		  || (lastPrice != null && ! lastPrice.equals(value)) )
		{
			lastPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized String getDisplayName() {
		return displayName;
	}

	@Override
	public synchronized Double getAskPrice() {
		return askPrice;
	}

	@Override
	public synchronized Long getAskSize() {
		return askSize;
	}

	@Override
	public synchronized Double getBidPrice() {
		return bidPrice;
	}

	@Override
	public synchronized Long getBidSize() {
		return bidSize;
	}

	@Override
	public synchronized void setDisplayName(String value) {
		if ( (displayName == null && value != null)
		  || (displayName != null && ! displayName.equals(value)) )
		{
			displayName = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setAskPrice(Double value) {
		if ( (askPrice == null && value != null)
		  || (askPrice != null && ! askPrice.equals(value)) )
		{
			askPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setAskSize(Long value) {
		if ( (askSize == null && value != null)
		  || (askSize != null && ! askSize.equals(value)) )
		{
			askSize = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setBidPrice(Double value) {
		if ( (bidPrice == null && value != null)
		  || (bidPrice != null && ! bidPrice.equals(value)) )
		{
			bidPrice = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setBidSize(Long value) {
		if ( (bidSize == null && value != null)
		  || (bidSize != null && ! bidSize.equals(value)) )
		{
			bidSize = value;
			setChanged();
		}
	}

	@Override
	public synchronized Double getOpenPrice() {
		return open;
	}

	@Override
	public synchronized Double getClosePrice() {
		return close;
	}

	@Override
	public synchronized Double getHighPrice() {
		return high;
	}

	@Override
	public synchronized Double getLowPrice() {
		return low;
	}

	@Override
	public synchronized SecurityStatus getStatus() {
		return status;
	}

	@Override
	public synchronized void setOpenPrice(Double value) {
		if ( value == null ? open != null : ! value.equals(open) ) {
			open = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setClosePrice(Double value) {
		if ( value == null ? close != null : ! value.equals(close) ) {
			close = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setHighPrice(Double value) {
		if ( value == null ? high != null : ! value.equals(high) ) {
			high = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setLowPrice(Double value) {
		if ( value == null ? low != null : ! value.equals(low) ) {
			low = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setStatus(SecurityStatus value) {
		if ( value == null ? status != null : ! value.equals(status) ) {
			status = value;
			setChanged();
		}
	}
	
	private synchronized void changePriceFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		String format = "";
		if ( decimals > 0 ) {
			format = "0." + StringUtils.repeat('0', decimals);
		} else {
			format = "0";
		}
		priceFormat = new DecimalFormat(format, symbols);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityImpl.class ) {
			return false;
		}
		SecurityImpl o = (SecurityImpl) other;
		return new EqualsBuilder()
			.append(o.decimals, decimals)
			.append(o.lotSize, lotSize)
			.append(o.stepSize, stepSize)
			.append(o.askPrice, askPrice)
			.append(o.askSize, askSize)
			.append(o.bidPrice, bidPrice)
			.append(o.bidSize, bidSize)
			.append(o.close, close)
			.append(o.descr, descr)
			.append(o.dispatcher, dispatcher)
			.append(o.displayName, displayName)
			.append(o.high, high)
			.append(o.lastPrice, lastPrice)
			.append(o.lastTrade, lastTrade)
			.append(o.low, low)
			.append(o.maxPrice, maxPrice)
			.append(o.minPrice, minPrice)
			.append(o.onChanged, onChanged)
			.append(o.onTrade, onTrade)
			.append(o.open, open)
			.append(o.status, status)
			.append(o.stepPrice, stepPrice)
			.append(o.terminal, terminal)
			.append(o.isAvailable(), isAvailable())
			.isEquals();
	}

	@Override
	public synchronized Double getMostAccuratePrice() {
		if ( lastPrice != null ) {
			return lastPrice;
		}
		if ( bidPrice != null && askPrice != null ) {
			return (bidPrice + askPrice) / 2;
		}
		if ( open != null ) {
			return open;
		}
		if ( close != null ) {
			return close;
		}
		if ( high != null && low != null ) {
			return (high + low) / 2;
		}
		if ( maxPrice != null && minPrice != null ) {
			return (maxPrice + minPrice) / 2;
		}
		return null;
	}

	@Override
	public synchronized Double getMostAccurateVolume(Double price, Long qty) {
		if ( stepPrice != null ) {
			return price * stepPrice / stepSize * (double) qty;
		} else {
			return price * (double) qty;
		}
	}

}
