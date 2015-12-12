package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PositionEventDispatcher;

/**
 * Торговая позиция.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionImpl.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class PositionImpl extends EditableImpl implements EditablePosition {
	private final Portfolio portfolio;
	private final Security security;
	private final PositionEventDispatcher dispatcher;
	private long open;
	private long lock;
	private long curr;
	private double variationMargin;
	private Double marketValue = 0.0d, bookValue = 0.0d;
	private PositionType type = PositionType.CLOSE;

	/**
	 * Создать объект позиции.
	 * <p>
	 * @param portfolio портфель, которому принадлежит позиция
	 * @param security инструмент, по которому открыта позиция
	 * @param dispatcher диспетчер событий
	 */
	public PositionImpl(Portfolio portfolio, Security security,
			PositionEventDispatcher dispatcher)
	{
		super();
		this.portfolio = portfolio;
		this.security = security;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public Terminal getTerminal() {
		return portfolio.getTerminal();
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public PositionEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public Symbol getSymbol() {
		return security.getSymbol();
	}
	
	@Override
	public Security getSecurity() {
		return security;
	}
	
	@Override
	public Account getAccount() {
		return portfolio.getAccount();
	}

	@Override
	public EventType OnChanged() {
		return dispatcher.OnChanged();
	}

	@Override
	public void fireChangedEvent() {
		dispatcher.fireChanged(this);
	}

	@Override
	public synchronized double getVarMargin() {
		return variationMargin;
	}

	@Override
	public synchronized long getOpenQty() {
		return open;
	}

	@Override
	public synchronized long getLockQty() {
		return lock;
	}

	@Override
	public synchronized long getCurrQty() {
		return curr;
	}

	@Override
	public synchronized void setVarMargin(double margin) {
		if ( variationMargin != margin ) {
			variationMargin = margin;
			setChanged();
		}
	}

	@Override
	public synchronized void setOpenQty(long value) {
		if ( open != value ) {
			open = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setLockQty(long value) {
		if ( lock != value ) {
			lock = value;
			setChanged();
		}
	}

	@Override
	public synchronized void setCurrQty(long value) {
		if ( curr != value ) {
			curr = value;
			if ( curr > 0 ) {
				type = PositionType.LONG;
			} else if ( curr < 0 ) {
				type = PositionType.SHORT;
			} else {
				type = PositionType.CLOSE;
			}
			setChanged();
		}
	}

	@Override
	public synchronized PositionType getType() {
		return type;
	}

	@Override
	public synchronized Double getMarketValue() {
		return marketValue;
	}

	@Override
	public synchronized void setMarketValue(Double value) {
		if ( value== null ? marketValue != null : !value.equals(marketValue) ) {
			marketValue = value;
			setChanged();
		}
	}

	@Override
	public synchronized Double getBookValue() {
		return bookValue;
	}

	@Override
	public synchronized void setBookValue(Double value) {
		if ( value== null ? bookValue != null : !value.equals(bookValue) ) {
			bookValue = value;
			setChanged();
		}
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionImpl.class ) {
			return false;
		}
		PositionImpl o = (PositionImpl) other;
		return new EqualsBuilder()
			.append(o.curr, curr)
			.append(o.lock, lock)
			.append(o.open, open)
			.append(o.variationMargin, variationMargin)
			.append(o.bookValue, bookValue)
			.append(o.marketValue, marketValue)
			.appendSuper(o.portfolio == portfolio)
			.appendSuper(o.security == security)
			.append(o.isAvailable(), isAvailable())
			.isEquals();
	}

}
