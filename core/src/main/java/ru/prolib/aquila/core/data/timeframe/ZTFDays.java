package ru.prolib.aquila.core.data.timeframe;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TFrame;

/**
 * Days time frame with time zone.
 */
public class ZTFDays extends AbstractZTFrame {
	
	public ZTFDays(int length, ZoneId zoneID) {
		super(length, ChronoUnit.DAYS, zoneID);
	}
	
	public ZTFDays(int length) {
		this(length, ZoneId.of("UTC"));
	}

	@Override
	public Interval getInterval(Instant timestamp) {
		LocalDateTime time = LocalDateTime.ofInstant(timestamp, zoneID);
		int d = (time.getDayOfYear() - 1) / length * length;
		LocalDateTime f = LocalDateTime.of(time.getYear(), 1, 1, 0, 0, 0).plusDays(d);
		LocalDateTime t = f.plusDays(length);
		if ( t.getYear() > f.getYear() ) {
			t = LocalDateTime.of(f.getYear() + 1, 1, 1, 0, 0, 0);
		}
		return Interval.of(
				ZonedDateTime.of(f, zoneID).toInstant(),
				ZonedDateTime.of(t, zoneID).toInstant());
	}

	@Override
	public boolean isIntraday() {
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ZTFDays.class ) {
			return false;
		}
		ZTFDays o = (ZTFDays) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.append(o.zoneID, zoneID)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9921, 57015).append(length).append(zoneID).toHashCode();
	}
	
	@Override
	public String toString() {
		return "D" + length + "[" + zoneID + "]";
	}

	@Override
	public TFrame toTFrame() {
		return new TFDays(length);
	}

}
