package ru.prolib.aquila.core.data.timeframe;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.TFrame;

/**
 * Hours time frame with time zone.
 */
public class ZTFHours extends AbstractZTFrame {

	/**
	 * Create timeframe of hours in specified time zone.
	 * <p>
	 * @param length - number of hours in period
	 * @param zoneID - time zone ID
	 */
	public ZTFHours(int length, ZoneId zoneID) {
		super(length, ChronoUnit.HOURS, zoneID);
		if ( length <= 0 || length > 12 ) {
			throw new IllegalArgumentException("Invalid length specified: " + length);
		}
	}

	/**
	 * Create timeframe of hours in UTC time zone.
	 * <p>
	 * @param length - length of period in hours (from 1 to 12 inclusive)
	 */
	public ZTFHours(int length) {
		this(length, ZoneId.of("UTC"));
	}
	
	@Override
	public Interval getInterval(Instant instant) {
		LocalDateTime time = LocalDateTime.ofInstant(instant, zoneID);
		long h = ChronoUnit.HOURS.between(LocalTime.MIDNIGHT, time.toLocalTime()) / length * length;
		LocalDateTime f = LocalDateTime.of(time.toLocalDate(), LocalTime.of((int)h, 0));
		LocalDateTime t = f.plusHours(length);
		if ( t.toLocalDate().isAfter(f.toLocalDate()) ) {
			t = LocalDateTime.of(f.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
		}
		return Interval.of(
			ZonedDateTime.of(f, zoneID).toInstant(),
			ZonedDateTime.of(t, zoneID).toInstant());
	}

	@Override
	public boolean isIntraday() {
		return true;
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ZTFHours.class ) {
			return false;
		}
		ZTFHours o = (ZTFHours) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.append(o.zoneID, zoneID)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(179231, 7591)
			.append(length)
			.append(zoneID)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return "H" + length + "[" + zoneID + "]";
	}

	@Override
	public TFrame toTFrame() {
		return new TFHours(length);
	}

}
