package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер дескриптора инструмента сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetSecurityDescriptor.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeSetSecurityDescriptor implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetSecurityDescriptor() {
		super();
	}

	@Override
	public void set(Trade object, Object value) throws ValueException {
		if ( value instanceof SecurityDescriptor ) {
			object.setSecurityDescriptor((SecurityDescriptor) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == TradeSetSecurityDescriptor.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 162723).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
