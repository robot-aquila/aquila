package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер вещественного значения.
 * <p>
 * Если значение имеет тип {@link java.lang.Double}, то возвращается как есть.
 * Если значение типа {@link java.lang.Integer}, то выполняет приведение к типу
 * {@link java.lang.Double}. В остальных случаях возвращает null.
 * <p>
 * 2012-08-22<br>
 * $Id: GDouble.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GDouble implements G<Double> {

	/**
	 * Создать геттер.
	 */
	public GDouble() {
		super();
	}

	@Override
	public Double get(Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Double.class ) {
				return (Double) value;
			} else if ( valueClass == Integer.class ) {
				return ((Integer) value).doubleValue();
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof GDouble;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/53545).toHashCode();
	}

}
