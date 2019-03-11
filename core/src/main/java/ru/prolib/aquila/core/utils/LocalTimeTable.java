package ru.prolib.aquila.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.threeten.extra.Interval;

public class LocalTimeTable {
	private final ZoneId zoneID;
	private final List<LocalTimePeriod> periods;
	
	LocalTimeTable(ZoneId zoneID, List<LocalTimePeriod> periods) {
		this.zoneID = zoneID;
		this.periods = periods;
	}
	
	public LocalTimeTable(ZoneId zoneID) {
		this(zoneID, new ArrayList<>());
	}
	
	public ZoneId getZoneID() {
		return zoneID;
	}
	
	public List<LocalTimePeriod> getPeriods() {
		return new ArrayList<>(periods);
	}
	
	public LocalTimeTable addPeriod(LocalTime from, LocalTime to) {
		LocalTimePeriod np = new LocalTimePeriod(from, to, zoneID);
		for ( LocalTimePeriod p : periods ) {
			if ( p.overlaps(np) ) {
				throw new IllegalArgumentException("Overlapping periods");
			}
		}
		periods.add(np);
		Collections.sort(periods);
		return this;
	}

	/**
	 * Get active or coming period relative to the time specified.
	 * <p>
	 * This method is intended to determine period to which the time belongs.
	 * If the time is not inside any period then next period relative to
	 * the time will be determined. This is useful to test for example is it
	 * time to trade and when the trading should be finished and all positions
	 * terminated.
	 * <p>
	 * @param time - time to determine period
	 * @return period
	 */
	public Interval getActiveOrComing(Instant time) {
		if ( periods.size() == 0 ) {
			throw new IllegalStateException("No periods defined");
		}
		ZonedDateTime zdt = time.atZone(zoneID);
		LocalTime lt = zdt.toLocalTime();
		LocalDate ld = zdt.toLocalDate();
		LocalTimePeriod found = null;
		for ( LocalTimePeriod p : periods ) {
			if ( p.from().compareTo(lt) >= 0 || p.contains(time) ) {
				found = p;
				break;
			}
		}
		if ( found == null ) {
			found = periods.get(0);
			ld = ld.plusDays(1);
		}
		return Interval.of(
				ZonedDateTime.of(ld, found.from(), zoneID).toInstant(),
				ZonedDateTime.of(ld, found.to(), zoneID).toInstant()
			);
	}
	
	/**
	 * Get next period relative to the time specified.
	 * <p>
	 * This method is intended to determine the next coming period.
	 * This is useful for example to get a time of next trading period start.
	 * <p>
	 * @param time - time to determine period
	 * @return period
	 */
	public Interval getComing(Instant time) {
		Interval x = getActiveOrComing(time);
		if ( time.compareTo(x.getStart()) >= 0 ) {
			x = getActiveOrComing(x.getEnd());
		}
		return x;
	}
	
}
