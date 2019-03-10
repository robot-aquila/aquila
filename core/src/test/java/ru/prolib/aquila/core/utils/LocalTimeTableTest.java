package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

public class LocalTimeTableTest {
	private static ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	
	static Instant ZT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(ZONE_ID).toInstant();
	}
	
	private LocalTimeTable service;

	@Before
	public void setUp() throws Exception {
		service = new LocalTimeTable(ZONE_ID);
	}
	
	@Test
	public void testAddPeriod() {
		// should be sorted after all
		assertSame(service, service.addPeriod(LocalTime.of(10, 30), LocalTime.of(14,  0)));
		assertSame(service, service.addPeriod(LocalTime.of(19,  0), LocalTime.of(23, 50)));
		assertSame(service, service.addPeriod(LocalTime.of(14,  5), LocalTime.of(18, 45)));

		
		List<LocalTimePeriod> expected = new ArrayList<>();
		expected.add(new LocalTimePeriod(LocalTime.of(10, 30), LocalTime.of(14,  0), ZONE_ID));
		expected.add(new LocalTimePeriod(LocalTime.of(14,  5), LocalTime.of(18, 45), ZONE_ID));
		expected.add(new LocalTimePeriod(LocalTime.of(19,  0), LocalTime.of(23, 50), ZONE_ID));
		assertEquals(expected, service.getPeriods());
		assertEquals(ZONE_ID, service.getZoneID());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetActiveOrComing_ThrowsIfNoPeriods() {
		service.getActiveOrComing(ZT("2019-03-10T14:00:00"));
	}
	
	@Test
	public void testGetActiveOrComing() {
		Interval expected;
		service.addPeriod(LocalTime.of(10, 30), LocalTime.of(14,  0))
			.addPeriod(LocalTime.of(19,  0), LocalTime.of(23, 50))
			.addPeriod(LocalTime.of(14,  5), LocalTime.of(18, 45));
		
		expected = Interval.of(ZT("2019-03-10T10:30:00"), ZT("2019-03-10T14:00:00"));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T05:13:19")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T10:30:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T12:50:05")));
		
		expected = Interval.of(ZT("2019-03-10T14:05:00"), ZT("2019-03-10T18:45:00"));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T14:00:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T14:05:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T18:30:00")));
		
		expected = Interval.of(ZT("2019-03-10T19:00:00"), ZT("2019-03-10T23:50:00"));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T18:45:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T18:50:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T19:00:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T20:00:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T23:45:00")));
		
		expected = Interval.of(ZT("2019-03-11T10:30:00"), ZT("2019-03-11T14:00:00"));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T23:50:00")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-10T23:59:59.999")));
		assertEquals(expected, service.getActiveOrComing(ZT("2019-03-11T01:59:59.999")));
	}

}
