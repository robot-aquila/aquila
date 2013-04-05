package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер цены сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetPrice.java 303 2012-11-05 15:18:57Z whirlwind $
 */
public class TradeSetPrice implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetPrice() {
		super();
	}

	@Override
	public void set(Trade object, Object value) {
		if ( value instanceof Double ) {
			object.setPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof TradeSetPrice;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 164801).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
