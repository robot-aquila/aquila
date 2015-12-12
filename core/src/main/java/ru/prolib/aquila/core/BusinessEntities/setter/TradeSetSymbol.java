package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер дескриптора инструмента сделки.
 */
public class TradeSetSymbol implements S<Trade> {
	
	/**
	 * Создать сеттер.
	 */
	public TradeSetSymbol() {
		super();
	}

	@Override
	public void set(Trade object, Object value) throws ValueException {
		if ( value instanceof Symbol ) {
			object.setSymbol((Symbol) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == TradeSetSymbol.class;
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
