package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер количества сделки.
 * <p>
 * 2012-11-05<br>
 * $Id: TradeSetQty.java 303 2012-11-05 15:18:57Z whirlwind $
 */
public class TradeSetQty implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetQty() {
		super();
	}

	@Override
	public void set(Trade object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setQty((Long) value);
			} else if ( valueClass == Integer.class ) {
				object.setQty(((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				object.setQty(((Double) value).longValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof TradeSetQty;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 171123).toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
