package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Геттер Profit&Loss позиции.
 * <p>
 * 2012-12-31<br>
 * $Id: IBGetPositionVarMargin.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetPositionVarMargin extends IBGetPositionAttr<Double> {
	
	/**
	 * Конструктор.
	 */
	public IBGetPositionVarMargin() {
		super();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 105947)
			.append(IBGetPositionVarMargin.class)
			.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetPositionVarMargin.class;
	}

	@Override
	protected Double getEventAttr(IBEventUpdatePortfolio event) {
		return event.getMarketValue() -
			(event.getAverageCost() * event.getPosition());
	}

}
