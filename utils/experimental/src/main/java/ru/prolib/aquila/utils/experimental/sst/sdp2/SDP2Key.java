package ru.prolib.aquila.utils.experimental.sst.sdp2;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.TimeFrame;

public class SDP2Key {
	private final TimeFrame timeFrame;
	private final Symbol symbol;
	
	public SDP2Key(TimeFrame timeFrame, Symbol symbol) {
		if ( timeFrame == null ) {
			throw new NullPointerException("Timeframe cannot be null");
		}
		this.timeFrame = timeFrame;
		this.symbol = symbol;
	}

	public SDP2Key(TimeFrame timeFrame) {
		this(timeFrame, null);
	}
	
	public TimeFrame getTimeFrame() {
		return timeFrame;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SDP2Key.class ) {
			return false;
		}
		SDP2Key o = (SDP2Key) other;
		return new EqualsBuilder()
				.append(timeFrame, o.timeFrame)
				.append(symbol,  o.symbol)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1919, 95).append(timeFrame).append(symbol).toHashCode();
	}

}
