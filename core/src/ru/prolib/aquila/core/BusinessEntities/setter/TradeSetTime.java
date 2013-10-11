package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер времени сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetTime.java 303 2012-11-05 15:18:57Z whirlwind $
 */
public class TradeSetTime implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetTime() {
		super();
	}

	@Override
	public void set(Trade object, Object value) throws ValueException {
		if ( value instanceof DateTime ) {
			object.setTime((DateTime) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof TradeSetTime;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 164505).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
