package ru.prolib.aquila.utils.experimental.experiment.moex;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class LocalTimePeriod {
	private final LocalTime from, to;
	private final ZoneId zone;
	
	/**
	 * Create time period.
	 * <p>
	 * @param from - time from (inclusive)
	 * @param to - time to (exclusive, must be greater that time from)
	 * @param zone - time zone ID
	 */
	public LocalTimePeriod(LocalTime from, LocalTime to, ZoneId zone) {
		if ( to.compareTo(from) <= 0 ) {
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
		return to.compareTo(toZT(time));
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
	
	public LocalTime toZT(Instant time) {
		return ZonedDateTime.ofInstant(time, zone).toLocalTime();
	}

}
