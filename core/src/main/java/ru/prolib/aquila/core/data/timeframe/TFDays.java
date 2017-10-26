package ru.prolib.aquila.core.data.timeframe;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.ZTFrame;

public class TFDays extends AbstractTFrame {
	
	public TFDays(int length) {
		super(length, ChronoUnit.DAYS);
	}

	@Override
	public boolean isIntraday() {
		return false;
	}

	@Override
	public ZTFrame toZTFrame(ZoneId zoneID) {
		return new ZTFDays(length, zoneID);
	}
	
	@Override
	public String toString() {
		return "D" + length;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1839115, 19013).append(length).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TFDays.class ) {
			return false;
		}
		TFDays o = (TFDays) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.isEquals();
	}

}
