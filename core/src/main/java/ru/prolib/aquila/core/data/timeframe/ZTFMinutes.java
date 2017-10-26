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
 * Minutes time frame with time zone.
 */
public class ZTFMinutes extends AbstractZTFrame {	
	/**
	 * Create minutes timeframe in specified time zone.
	 * <p>
	 * @param length - number of minutes in interval
	 * @param zoneID - time zone
	 */
	public ZTFMinutes(int length, ZoneId zoneID) {
		super(length, ChronoUnit.MINUTES, zoneID);
		if ( length <= 0 || length > 1440 ) {
			throw new IllegalArgumentException("Invalid length specified: " + length);
		}
	}
	
	/**
	 * Create minute timeframe in UTC time zone.
	 * <p>
	 * @param length - number of minutes in interval
	 */
	public ZTFMinutes(int length) {
		this(length, ZoneId.of("UTC"));
	}

	@Override
	public Interval getInterval(Instant timestamp) {
		LocalDateTime time = LocalDateTime.ofInstant(timestamp, zoneID);
		long secondOfDay = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT,
				time.toLocalTime()) / length * length * 60;
		LocalDateTime from = LocalDateTime.of(time.toLocalDate(),
				LocalTime.ofSecondOfDay(secondOfDay));
		LocalDateTime to = from.plusMinutes(length);
		if ( to.toLocalDate().isAfter(from.toLocalDate()) ) {
			// Конец периода указывает на дату следующего дня.
			// В таком случае конец интервала выравнивается по началу след. дня.
			to = LocalDateTime.of(from.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
		}
		return Interval.of(ZonedDateTime.of(from, zoneID).toInstant(),
				ZonedDateTime.of(to, zoneID).toInstant());
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
		if ( other == null || other.getClass() != ZTFMinutes.class ) {
			return false;
		}
		ZTFMinutes o = (ZTFMinutes) other;
		return new EqualsBuilder()
				.append(o.length, length)
				.append(o.zoneID, zoneID)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(859, 175)
				.append(length)
				.append(zoneID)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return "M" + length + "[" + zoneID + "]";
	}

	@Override
	public TFrame toTFrame() {
		return new TFMinutes(length);
	}

}
