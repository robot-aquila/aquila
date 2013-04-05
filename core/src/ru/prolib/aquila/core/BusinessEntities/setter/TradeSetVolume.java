package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер объема сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetVolume.java 303 2012-11-05 15:18:57Z whirlwind $
 */
public class TradeSetVolume implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetVolume() {
		super();
	}

	@Override
	public void set(Trade object, Object value) {
		if ( value instanceof Double ) {
			object.setVolume((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof TradeSetVolume;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 165517).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
