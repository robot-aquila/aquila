package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.utils.Variant;

public class ZTFMinutesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Instant T(String timeString, ZoneId zoneID) {
		return ZonedDateTime.of(LocalDateTime.parse(timeString), zoneID).toInstant();
	}
	
	private ZTFMinutes M1UTC, M5UTC, M7UTC, M15UTC, M241UTC, M21MSK;
	private ZoneId UTC, MSK;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		M1UTC = new ZTFMinutes(1);
		M5UTC = new ZTFMinutes(5);
		M7UTC = new ZTFMinutes(7); // нестандарт
		M15UTC = new ZTFMinutes(15);
		M241UTC = new ZTFMinutes(241); // нестандарт
		M21MSK = new ZTFMinutes(21, MSK);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfLengthGt1440() {
		new ZTFMinutes(1441);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfLengthLe0() {
		new ZTFMinutes(0);
	}
	
	@Test
	public void testCtor1_DefaultZoneIDisUTC() {
		assertEquals(UTC, new ZTFMinutes(2).getZoneID());
	}
	
	@Test
	public void testIsIntraday() throws Exception {
		assertTrue(M1UTC.isIntraday());
		assertTrue(M5UTC.isIntraday());
		assertTrue(M7UTC.isIntraday());
		assertTrue(M15UTC.isIntraday());
		assertTrue(M241UTC.isIntraday());
		assertTrue(M21MSK.isIntraday());
	}
	
	@Test
	public void testGetUnit() throws Exception {
		assertEquals(ChronoUnit.MINUTES, M1UTC.getUnit());
		assertEquals(ChronoUnit.MINUTES, M5UTC.getUnit());
		assertEquals(ChronoUnit.MINUTES, M7UTC.getUnit());
		assertEquals(ChronoUnit.MINUTES, M15UTC.getUnit());
		assertEquals(ChronoUnit.MINUTES, M241UTC.getUnit());
		assertEquals(ChronoUnit.MINUTES, M21MSK.getUnit());
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(  1, M1UTC.getLength());
		assertEquals(  5, M5UTC.getLength());
		assertEquals(  7, M7UTC.getLength());
		assertEquals( 15, M15UTC.getLength());
		assertEquals(241, M241UTC.getLength());
		assertEquals( 21, M21MSK.getLength());
	}
	
	@Test
	public void testGetZoneID() {
		assertEquals(UTC, M1UTC.getZoneID());
		assertEquals(UTC, M5UTC.getZoneID());
		assertEquals(UTC, M7UTC.getZoneID());
		assertEquals(UTC, M15UTC.getZoneID());
		assertEquals(UTC, M241UTC.getZoneID());
		assertEquals(MSK, M21MSK.getZoneID());
	}
	
	@Test
	public void testGetInterval_M1UTC() throws Exception {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T20:22:05.015Z"),
				Instant.parse("2013-10-06T20:22:00.000Z"),
				Instant.parse("2013-10-06T20:23:00.000Z") },
			{	Instant.parse("2013-10-06T23:57:19.213Z"),
				Instant.parse("2013-10-06T23:57:00.000Z"),
				Instant.parse("2013-10-06T23:58:00.000Z") },
			{	Instant.parse("2013-10-06T23:59:34.117Z"),
				Instant.parse("2013-10-06T23:59:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M1UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_M5UTC() throws Exception {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T20:22:05.015Z"),
				Instant.parse("2013-10-06T20:20:00.000Z"),
				Instant.parse("2013-10-06T20:25:00.000Z") },
			{	Instant.parse("2013-10-06T23:57:19.213Z"),
				Instant.parse("2013-10-06T23:55:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
			{	Instant.parse("2013-10-06T23:59:34.117Z"),
				Instant.parse("2013-10-06T23:55:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M5UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_M7UTC() throws Exception {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T00:01:14.715Z"),
				Instant.parse("2013-10-06T00:00:00.000Z"),
				Instant.parse("2013-10-06T00:07:00.000Z") },
			{	Instant.parse("2013-10-06T00:07:00.000Z"),
				Instant.parse("2013-10-06T00:07:00.000Z"),
				Instant.parse("2013-10-06T00:14:00.000Z") },
			{	Instant.parse("2013-10-06T23:51:02.472Z"),
				Instant.parse("2013-10-06T23:48:00.000Z"),
				Instant.parse("2013-10-06T23:55:00.000Z") },
			{	Instant.parse("2013-10-06T23:55:13.024Z"),
				Instant.parse("2013-10-06T23:55:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M7UTC.getInterval(fix[i][0]));
		}
	}

	@Test
	public void testGetInterval_M15UTC() throws Exception {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T00:01:14.715Z"),
				Instant.parse("2013-10-06T00:00:00.000Z"),
				Instant.parse("2013-10-06T00:15:00.000Z") },
			{	Instant.parse("2013-10-06T00:07:00.000Z"),
				Instant.parse("2013-10-06T00:00:00.000Z"),
				Instant.parse("2013-10-06T00:15:00.000Z") },
			{	Instant.parse("2013-10-06T23:51:02.472Z"),
				Instant.parse("2013-10-06T23:45:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M15UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_M241UTC() throws Exception {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T00:01:14.715Z"),
				Instant.parse("2013-10-06T00:00:00.000Z"),
				Instant.parse("2013-10-06T04:01:00.000Z") },
			{	Instant.parse("2013-10-06T15:27:13.028Z"),
				Instant.parse("2013-10-06T12:03:00.000Z"),
				Instant.parse("2013-10-06T16:04:00.000Z") }, 
			{	Instant.parse("2013-10-06T23:15:08.182Z"),
				Instant.parse("2013-10-06T20:05:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M241UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_M21MSK() {
		Instant fix[][] = {
			// 19:47=19*60+47=1187; /21=56*21=1176; 19ч=1140, 36м 
			{ T("2017-10-25T19:47:05", MSK), T("2017-10-25T19:36:00", MSK), T("2017-10-25T19:57:00", MSK) },
			// 23:59=23*60+59=1439; /21=68*21=1428; 23ч=1380, 48м
			{ T("2017-10-25T23:59:13", MSK), T("2017-10-25T23:48:00", MSK), T("2017-10-26T00:00:00", MSK) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, M21MSK.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(M21MSK.equals(M21MSK));
		assertFalse(M21MSK.equals(this));
		assertFalse(M21MSK.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vLen = new Variant<>(21, 15);
		Variant<ZoneId> vZone = new Variant<>(vLen, MSK, UTC);
		Variant<?> iterator = vZone;
		int foundCnt = 0;
		ZTFMinutes x, found = null;
		do {
			x = new ZTFMinutes(vLen.get(), vZone.get());
			if ( M21MSK.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(21, found.getLength());
		assertEquals(MSK, found.getZoneID());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(859, 175).append(1).append(UTC).toHashCode(), M1UTC.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(5).append(UTC).toHashCode(), M5UTC.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(7).append(UTC).toHashCode(), M7UTC.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(15).append(UTC).toHashCode(), M15UTC.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(241).append(UTC).toHashCode(), M241UTC.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(21).append(MSK).toHashCode(), M21MSK.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("M1[UTC]", M1UTC.toString());
		assertEquals("M5[UTC]", M5UTC.toString());
		assertEquals("M7[UTC]", M7UTC.toString());
		assertEquals("M15[UTC]", M15UTC.toString());
		assertEquals("M241[UTC]", M241UTC.toString());
		assertEquals("M21[Europe/Moscow]", M21MSK.toString());
	}
	
	@Test
	public void testIsCompatibleWith() {
		assertTrue(M5UTC.isCompatibleWith(M15UTC));
		assertFalse(M5UTC.isCompatibleWith(M21MSK));
	}
	
	@Test
	public void testToTFrame() {
		assertEquals(new TFMinutes(15), M15UTC.toTFrame());
		assertEquals(new TFMinutes(241), M241UTC.toTFrame());
	}
	
}
