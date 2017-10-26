package ru.prolib.aquila.core.data.timeframe;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.ZTFrame;

public class TFHours extends AbstractTFrame {
	
	public TFHours(int length) {
		super(length, ChronoUnit.HOURS);
	}

	@Override
	public boolean isIntraday() {
		return true;
	}

	@Override
	public ZTFrame toZTFrame(ZoneId zoneID) {
		return new ZTFHours(length, zoneID);
	}
	
	@Override
	public String toString() {
		return "H" + length;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(571921, 5527).append(length).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TFHours.class ) {
			return false;
		}
		TFHours o = (TFHours) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.isEquals();
	}

}
