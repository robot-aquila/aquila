package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер строки.
 * <p>
 * Если значение типа {@link java.lang.String String}, то возвращается как есть.
 * В противном случае возвращается null. 
 * <p>
 * 2012-08-22<br>
 * $Id: GString.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GString implements G<String> {

	/**
	 * Создать геттер.
	 * <p>
	 */
	public GString() {
		super();
	}

	@Override
	public String get(Object value) {
		return (String) (value instanceof String ? value : null);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof GString;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/80537).toHashCode();
	}

}
