package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.LocalTimePeriod;

public class LocalTimePeriodTest {
	private LocalTime from, to;
	private ZoneId zone;
	private LocalTimePeriod period;
	
	public static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		from = LocalTime.of(10, 0);
		to = LocalTime.of(14, 0);
		zone = ZoneId.of("Europe/Moscow");
		period = new LocalTimePeriod(from, to, zone);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor3_ThrowsIfTimeToIsEqualsToFrom() {
		new LocalTimePeriod(from, from, zone);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfTimeToIsLessThatFrom() {
		new LocalTimePeriod(to, from, zone);
	}
	
	@Test
	public void testCtor3() {
		assertEquals(from, period.from());
		assertEquals(to, period.to());
		assertEquals(zone, period.zone());
	}
	
	@Test
	public void testCompareStartTo() {
		assertEquals( 0, period.compareStartTo(T("2016-08-18T07:00:00Z"))); 
		assertEquals( 1, period.compareStartTo(T("2016-08-18T06:59:59.999Z")));
		assertEquals(-1, period.compareStartTo(T("2016-08-18T07:00:00.001Z")));
	}
	
	@Test
	public void testCompareEndTo() {
		assertEquals( 0, period.compareEndTo(T("2016-08-18T11:00:00Z"))); 
		assertEquals( 1, period.compareEndTo(T("2016-08-18T10:59:59.999Z")));
		assertEquals(-1, period.compareEndTo(T("2016-08-18T11:00:00.001Z")));
	}
	
	@Test
	public void testContains() {
		assertFalse(period.contains(T("1997-01-01T06:00:00.000Z")));
		assertFalse(period.contains(T("1997-01-01T06:59:59.999Z")));
		assertTrue(period.contains(T("1997-01-01T07:00:00.000Z")));
		assertTrue(period.contains(T("1997-01-01T07:00:00.001Z")));
		assertTrue(period.contains(T("1997-01-01T08:00:00.000Z")));
		assertTrue(period.contains(T("1997-01-01T10:59:59.999Z")));
		assertFalse(period.contains(T("1997-01-01T11:00:00.000Z")));
		assertFalse(period.contains(T("1997-01-01T11:00:00.001Z")));
		assertFalse(period.contains(T("1997-01-01T12:00:00.000Z")));
	}

	@Test
	public void testToString() {
		assertEquals("P[10:00-14:00 Europe/Moscow]", period.toString());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(period.equals(period));
		assertFalse(period.equals(null));
		assertFalse(period.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(period.equals(new LocalTimePeriod(from, to, zone)));
		assertFalse(period.equals(new LocalTimePeriod(LocalTime.of(0, 0), to, zone)));
		assertFalse(period.equals(new LocalTimePeriod(from, LocalTime.of(23, 59), zone)));
		assertFalse(period.equals(new LocalTimePeriod(from, to, ZoneOffset.UTC)));
	}

	@Test
	public void testToZDT() {
		ZonedDateTime expected = ZonedDateTime.of(2016, 9, 15, 15, 30, 45, 0, zone);
		assertEquals(expected, period.toZDT(T("2016-09-15T12:30:45Z")));
	}
	
	@Test
	public void testToZT() {
		LocalTime expected = LocalTime.of(15, 30, 45);
		assertEquals(expected, period.toZT(T("2016-09-15T12:30:45Z")));
	}
	
	@Test
	public void testEndOfDayAllowedAsPeriodEnd_ByInstance() {
		period = new LocalTimePeriod(LocalTime.of(23, 50), LocalTime.MIDNIGHT, zone);
		testEndOfDayAllowedAsPeriodEnd();
	}
	
	@Test
	public void testEndOfDayAllowedAsPeriodEnd_ByValue() {
		period = new LocalTimePeriod(LocalTime.of(23, 50), LocalTime.of(0, 0), zone);
		testEndOfDayAllowedAsPeriodEnd();		
	}
	
	private void testEndOfDayAllowedAsPeriodEnd() {
		Instant dummy = ZonedDateTime.of(2017, 4, 24, 0, 0, 0, 0, zone).toInstant();
		assertEquals( 1, period.compareEndTo(dummy));
		assertEquals( 1, period.compareStartTo(dummy));
		assertFalse(period.contains(dummy));
		
		dummy = ZonedDateTime.of(2017, 4, 24, 23, 50, 0, 0, zone).toInstant();
		assertEquals( 1, period.compareEndTo(dummy));
		assertEquals( 0, period.compareStartTo(dummy));
		assertTrue(period.contains(dummy));
		
		dummy = ZonedDateTime.of(2017, 4, 24, 23, 59, 59, 999, zone).toInstant();
		assertEquals( 1, period.compareEndTo(dummy));
		assertEquals(-1, period.compareStartTo(dummy));
		assertTrue(period.contains(dummy));
		
		dummy = ZonedDateTime.of(2017, 4, 25, 0, 0, 0, 0, zone).toInstant();
		assertEquals( 1, period.compareEndTo(dummy));
		assertEquals( 1, period.compareStartTo(dummy));
		assertFalse(period.contains(dummy));
	}

}
