package ru.prolib.aquila.data;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Descriptor of a data segment of symbol at specified date.
 */
public class DatedSymbol {
	private final Symbol symbol;
	private final LocalDate date;
	
	public DatedSymbol(Symbol symbol, LocalDate date) {
		this.symbol = symbol;
		this.date = date;
	}
	
	/**
	 * Get symbol.
	 * <p>
	 * @return symbol
	 */
	public Symbol getSymbol() {
		return symbol;
	}
	
	/**
	 * Get date.
	 * <p>
	 * @return date
	 */
	public LocalDate getDate() {
		return date;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DatedSymbol.class ) {
			return false;
		}
		DatedSymbol o = (DatedSymbol) other;
		return new EqualsBuilder()
			.append(o.symbol, symbol)
			.append(o.date, date)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + symbol + " at " + date + "]";
	}

}
