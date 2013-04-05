package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер целочисленного значения.
 * <p>
 * Если значение типа {@link java.lang.Long}, то возвращается как есть. Если
 * значение имеет тип {@link java.lang.Integer}, то выполняется приведение к
 * типу {@link java.lang.Long}. Если значение типа {@link java.lang.Double},
 * то выполняет приведение к типу {@link java.lang.Long} с потерей точности.
 * В остальных случаях возвращает null.
 * <p>
 * 2012-08-22<br>
 * $Id: GLong.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GLong implements G<Long> {

	/**
	 * Создать геттер.
	 */
	public GLong() {
		super();
	}

	@Override
	public Long get(Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Integer.class ) {
				return ((Integer) value).longValue();
			} else if ( valueClass == Double.class ) {
				return ((Double) value).longValue();
			} else if ( valueClass == Long.class ) {
				return (Long) value;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof GLong;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/54251).toHashCode();
	}

}
