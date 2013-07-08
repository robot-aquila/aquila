package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер направления сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetDirection.java 303 2012-11-05 15:18:57Z whirlwind $
 */
public class TradeSetDirection implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetDirection() {
		super();
	}

	@Override
	public void set(Trade object, Object value) throws ValueException {
		if ( value instanceof Direction ) {
			object.setDirection((Direction) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof TradeSetDirection;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 163921).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
