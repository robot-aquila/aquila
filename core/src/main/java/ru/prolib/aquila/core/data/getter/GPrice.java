package ru.prolib.aquila.core.data.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер цены.
 * <p>
 * Использует два подчиненных геттера для получения величины и единицы
 * выражения цены. Если один из геттеров возвращает null, то итоговое
 * значение цены так же равно null. 
 * <p>
 * 2012-10-25<br>
 * $Id: GPrice.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GPrice implements G<Price> {
	private final G<Double> gValue;
	private final G<PriceUnit> gUnit;
	
	/**
	 * Создать геттер.
	 * <p>
	 * @param gValue геттер значения цены
	 * @param gUnit геттер единицы цены
	 */
	public GPrice(G<Double> gValue, G<PriceUnit> gUnit) {
		super();
		this.gValue = gValue;
		this.gUnit = gUnit;
	}
	
	/**
	 * Получить геттер величины цены.
	 * <p>
	 * @return геттер величины
	 */
	public G<Double> getValueGetter() {
		return gValue;
	}
	
	/**
	 * Получить геттер единицы выражения цены.
	 * <p>
	 * @return геттер единицы выражения цены
	 */
	public G<PriceUnit> getUnitGetter() {
		return gUnit;
	}

	@Override
	public Price get(Object object) throws ValueException {
		Double value = (Double) gValue.get(object);
		PriceUnit unit = (PriceUnit) gUnit.get(object);
		if ( value != null && unit != null ) {
			return new Price(unit, value);
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GPrice ) {
			GPrice o = (GPrice) other;
			return new EqualsBuilder()
				.append(gValue, o.gValue)
				.append(gUnit, o.gUnit)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/75701)
			.append(gValue)
			.append(gUnit)
			.toHashCode();
	}

}
