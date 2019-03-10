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
	
}
