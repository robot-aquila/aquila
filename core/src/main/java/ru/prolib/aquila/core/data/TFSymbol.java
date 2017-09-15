package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Key to represent time frame of symbol.
 */
public class TFSymbol {
	private final Symbol symbol;
	private final TimeFrame timeFrame;
	
	public TFSymbol(Symbol symbol, TimeFrame timeFrame) {
		this.symbol = symbol;
		this.timeFrame = timeFrame;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(648271, 715)
				.append(symbol)
				.append(timeFrame)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return symbol.toString() + "[" + timeFrame + "]";
 	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TFSymbol.class ) {
			return false;
		}
		TFSymbol o = (TFSymbol) other;
		return new EqualsBuilder()
				.append(o.symbol, symbol)
				.append(o.timeFrame, timeFrame)
				.isEquals();
	}

}
