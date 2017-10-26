package ru.prolib.aquila.core.data.timeframe;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.ZTFrame;

public class TFMinutes extends AbstractTFrame {
	
	public TFMinutes(int length) {
		super(length, ChronoUnit.MINUTES);
	}

	@Override
	public boolean isIntraday() {
		return true;
	}

	@Override
	public ZTFrame toZTFrame(ZoneId zoneID) {
		return new ZTFMinutes(length, zoneID);
	}
	
	@Override
	public String toString() {
		return "M" + length;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(4856141, 13219).append(length).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TFMinutes.class ) {
			return false;
		}
		TFMinutes o = (TFMinutes) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.isEquals();
	}

}
