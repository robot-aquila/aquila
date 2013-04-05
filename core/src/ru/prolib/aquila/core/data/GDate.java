package ru.prolib.aquila.core.data;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Геттер даты.
 * <p>
 * Если значение типа {@link java.util.Date Date}, то возвращается как есть.
 * В противном случае возвращает null.
 * <p>
 * 2012-11-10<br>
 * $Id: GDate.java 542 2013-02-23 04:15:34Z whirlwind $
 */
@Deprecated
public class GDate implements G<Date> {
	
	/**
	 * Создать геттер.
	 */
	public GDate() {
		super();
	}

	@Override
	public Date get(Object source) {
		if ( source != null && source.getClass() == Date.class ) {
			return (Date) source;
		} else {
			return null;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121111, 202623).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GDate;
	}

}
