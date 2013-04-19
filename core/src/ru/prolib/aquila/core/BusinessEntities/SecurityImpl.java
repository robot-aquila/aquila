package ru.prolib.aquila.core.BusinessEntities;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * Реализация модифицируемого торгового инструмента.
 * <p>
 * 2012-05-30<br>
 * $Id: SecurityImpl.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class SecurityImpl extends EditableImpl implements EditableSecurity {
	private final Terminal terminal;
	private final SecurityDescriptor descr;
	private final EventDispatcher dispatcher;
	private final EventType etChanged;
	private final EventType etNewTrade;
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
	 * Создать объект инструмента.
	 * <p>
	 * @param terminal
	 * @param descr
	 * @param eventDispatcher
	 * @param eventTypeChanged
	 * @param eventTypeNewTrade
	 * @throws NullPointerException
	 */
	public SecurityImpl(Terminal terminal, SecurityDescriptor descr,
					    EventDispatcher eventDispatcher,
					    EventType eventTypeChanged,
					    EventType eventTypeNewTrade)
	{
		super();
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		this.terminal = terminal;
		if ( ! descr.isValid() ) {
			throw new IllegalArgumentException("Invalid security descriptor: "
					+ descr);
		}
		this.descr = descr;
		if ( eventDispatcher == null ) {
			throw new NullPointerException("Event dispatcher cannot be null");
		}
		this.dispatcher = eventDispatcher;
		if ( eventTypeChanged == null ) {
			throw new NullPointerException("OnChanged type cannot be null");
		}
		this.etChanged = eventTypeChanged;
		if ( eventTypeNewTrade == null ) {
			throw new NullPointerException("OnNewTrade type cannot be null");
		}
		this.etNewTrade = eventTypeNewTrade;
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
		return etChanged;
	}

	@Override
	public synchronized EventType OnTrade() {
		return etNewTrade;
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
		dispatcher.dispatch(new SecurityTradeEvent(etNewTrade, this, trade));
	}
	
	@Override
	public void fireChangedEvent() throws EditableObjectException {
		dispatcher.dispatch(new SecurityEvent(etChanged, this));
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

}
