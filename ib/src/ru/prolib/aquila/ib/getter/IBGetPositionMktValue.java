package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Геттер рыночной цены позиции.
 * <p>
 * 2012-12-30<br>
 * $Id: IBGetPositionMktValue.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetPositionMktValue extends IBGetPositionAttr<Double> {
	
	/**
	 * Конструктор.
	 */
	public IBGetPositionMktValue() {
		super();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 223901)
			.append(IBGetPositionMktValue.class)
			.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetPositionMktValue.class;
	}

	@Override
	protected Double getEventAttr(IBEventUpdatePortfolio event) {
		return event.getMarketValue();
	}

}
