package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Геттер балансовой стоимости позиции.
 * <p>
 * 2012-12-30<br>
 * $Id: IBGetPositionBalanceCost.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetPositionBalanceCost extends IBGetPositionAttr<Double> {
	
	/**
	 * Конструктор.
	 */
	public IBGetPositionBalanceCost() {
		super();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 224831)
			.append(IBGetPositionBalanceCost.class)
			.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBGetPositionBalanceCost.class;
	}

	@Override
	protected Double getEventAttr(IBEventUpdatePortfolio event) {
		return event.getAverageCost() * event.getPosition();
	}

}
