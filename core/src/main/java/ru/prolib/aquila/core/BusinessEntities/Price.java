package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Цена.
 * <p>
 * 2012-10-24<br>
 * $Id: Price.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class Price {
	private final PriceUnit unit;
	private final Double value;
	
	/**
	 * Создать цену.
	 * <p>
	 * @param unit единицы выражения цены
	 * @param value значение
	 */
	public Price(PriceUnit unit, Double value) {
		super();
		this.unit = unit;
		this.value = value;
	}
	
	/**
	 * Получить значение цены.
	 * <p>
	 * @return значение цены
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * Получить единицу цены.
	 * <p>
	 * @return единица цены
	 */
	public PriceUnit getUnit() {
		return unit;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null || other.getClass() != Price.class ) {
			return false;
		}
		Price o = (Price) other;
		return new EqualsBuilder()
			.append(unit, o.unit)
			.append(value, o.value)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121231, 170523)
			.append(Price.class)
			.append(unit)
			.append(value)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return value + " " + unit;
	}

}
