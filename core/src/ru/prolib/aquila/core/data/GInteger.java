package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер целочисленного значения.
 * <p>
 * Если значение типа {@link java.lang.Integer}, то возвращается
 * как есть. Если значение типа {@link java.lang.Double}, то выполняет
 * приведение к типу {@link java.lang.Integer}. При этом, дробная часть
 * отбрасывается (то есть конвертация с потерей точности). В остальных случаях
 * возвращает null.
 * <p>
 * 2012-08-22<br>
 * $Id: GInteger.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GInteger implements G<Integer> {

	/**
	 * Создать геттер.
	 */
	public GInteger() {
		super();
	}

	@Override
	public Integer get(Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Double.class ) {
				return ((Double) value).intValue();
			} else if ( valueClass == Integer.class ) {
				return (Integer) value;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof GInteger;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/54031).toHashCode();
	}

}
