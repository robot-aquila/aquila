package ru.prolib.aquila.core.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.threeten.extra.Interval;

/**
 * An intraday time period.
 */
public class LocalTimePeriod implements Comparable<LocalTimePeriod> {
	/**
	 * End of day marker.
	 */
	public static final LocalTime EOD = LocalTime.MIDNIGHT;
	private final LocalTime from, to;
	private final ZoneId zone;
	
	/**
	 * Create time period.
	 * <p>
	 * @param from - time from (inclusive)
	 * @param to - time to (exclusive). Must be greater that time from.
	 * Use {@link #EOD} instance to specify period up to end of day.
	 * @param zone - time zone ID
	 */
	public LocalTimePeriod(LocalTime from, LocalTime to, ZoneId zone) {
		int x = to.compareTo(from);
		if ( x == 0 || x < 0 && ! to.equals(EOD) ) {
			throw new IllegalArgumentException();
		}
		this.from = from;
		this.to = to;
		this.zone = zone;
	}
	
	public boolean contains(Instant time) {
		return compareStartTo(time) <= 0 && compareEndTo(time) > 0;
	}
	
	public int compareStartTo(Instant time) {
		return from.compareTo(toZT(time));
	}
	
	public int compareEndTo(Instant time) {
		return to.equals(EOD) ? 1 : to.compareTo(toZT(time));
	}
	
	public LocalTime from() {
		return from;
	}
	
	public LocalTime to() {
		return to;
	}
	
	public ZoneId zone() {
		return zone;
	}
	
	@Override
	public String toString() {
		return "P[" + from + "-" + to + " " + zone + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != LocalTimePeriod.class ) {
			return false;
		}
		LocalTimePeriod o = (LocalTimePeriod) other;
		return new EqualsBuilder()
			.append(from, o.from)
			.append(to, o.to)
			.append(zone, o.zone)
			.isEquals();
	}
	
	/**
	 * Convert an instant to a zoned date time according to the period's selected time zone.
	 * <p>
	 * @param time - the time
	 * @return the zoned date time
	 */
	public ZonedDateTime toZDT(Instant time) {
		return ZonedDateTime.ofInstant(time, zone);
	}
	
	/**
	 * Extract a daytime part of an instant according to the period's selected time zone.
	 * <p>
	 * @param time - the time
	 * @return the local time according to selected time zone
	 */
	public LocalTime toZT(Instant time) {
		return toZDT(time).toLocalTime();
	}
	
	public boolean overlappedBy(Interval target) {
		Duration target_duration = target.toDuration();
		if ( target_duration.compareTo(Duration.ofDays(1)) >= 0 ) {
			return true;
		}
		ZonedDateTime zdt_target_start = toZDT(target.getStart());
		ZonedDateTime zdt_target_end = toZDT(target.getEnd());
		LocalDate ld_target_start = zdt_target_start.toLocalDate();
		LocalDate ld_target_end = zdt_target_end.toLocalDate();
		if ( Interval.of(
				ZonedDateTime.of(ld_target_start, from, zone).toInstant(),
				ZonedDateTime.of(ld_target_start, to, zone).toInstant()
			).overlaps(target) )
		{
			return true;
		}
		if ( ! ld_target_start.equals(ld_target_end) ) {
			return Interval.of(
					ZonedDateTime.of(ld_target_end, from, zone).toInstant(),
					ZonedDateTime.of(ld_target_end, to, zone).toInstant()
				).overlaps(target);
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(LocalTimePeriod o) {
		if ( ! zone.equals(o.zone) ) {
			throw new IllegalArgumentException();
		}
		int r = from.compareTo(o.from);
		if ( r != 0 ) {
			return r;
		}
		return to.compareTo(o.to);
	}
	
	public boolean overlaps(LocalTimePeriod other) {
		if ( from.compareTo(other.to) >= 0
		  || to.compareTo(other.from) <= 0 )
		{
			return false;
		}
		return true;
	}

}
