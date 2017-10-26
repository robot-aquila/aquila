package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.utils.Variant;

public class ZTFHoursTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Instant T(String timeString, ZoneId zoneID) {
		return ZonedDateTime.of(LocalDateTime.parse(timeString), zoneID).toInstant();
	}
	
	private ZTFHours H1UTC, H5UTC, H12UTC, H1MSK, H9MSK, H12MSK;
	private ZoneId UTC, MSK;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		H1UTC = new ZTFHours(1);
		H5UTC = new ZTFHours(5);
		H12UTC = new ZTFHours(12);
		H1MSK = new ZTFHours(1, MSK);
		H9MSK = new ZTFHours(9, MSK);
		H12MSK = new ZTFHours(12, MSK);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfLengthGt12() {
		new ZTFHours(13);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfLengthLe0() {
		new ZTFHours(0);
	}
	
	@Test
	public void testCtor1_DefaultZoneIDisUTC() {
		assertEquals(UTC, new ZTFHours(2).getZoneID());
	}
	
	@Test
	public void testIsIntraday() {
		assertTrue(H1UTC.isIntraday());
		assertTrue(H5UTC.isIntraday());
		assertTrue(H12UTC.isIntraday());
		assertTrue(H1MSK.isIntraday());
		assertTrue(H9MSK.isIntraday());
		assertTrue(H12MSK.isIntraday());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(ChronoUnit.HOURS, H1UTC.getUnit());
		assertEquals(ChronoUnit.HOURS, H5UTC.getUnit());
		assertEquals(ChronoUnit.HOURS, H12UTC.getUnit());
		assertEquals(ChronoUnit.HOURS, H1MSK.getUnit());
		assertEquals(ChronoUnit.HOURS, H9MSK.getUnit());
		assertEquals(ChronoUnit.HOURS, H12MSK.getUnit());
	}
	
	@Test
	public void testGetLength() {
		assertEquals( 1, H1UTC.getLength());
		assertEquals( 5, H5UTC.getLength());
		assertEquals(12, H12UTC.getLength());
		assertEquals( 1, H1MSK.getLength());
		assertEquals( 9, H9MSK.getLength());
		assertEquals(12, H12MSK.getLength());
	}
	
	@Test
	public void testGetZoneID() {
		assertEquals(UTC, H1UTC.getZoneID());
		assertEquals(UTC, H5UTC.getZoneID());
		assertEquals(UTC, H12UTC.getZoneID());
		assertEquals(MSK, H1MSK.getZoneID());
		assertEquals(MSK, H9MSK.getZoneID());
		assertEquals(MSK, H12MSK.getZoneID());
	}
	
	@Test
	public void testGetInterval_H1UTC() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-25T00:32:19Z"), T("2017-10-25T00:00:00Z"), T("2017-10-25T01:00:00Z") },
			{ T("2017-10-25T10:45:24Z"), T("2017-10-25T10:00:00Z"), T("2017-10-25T11:00:00Z") },
			{ T("2017-10-25T23:59:31Z"), T("2017-10-25T23:00:00Z"), T("2017-10-26T00:00:00Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H1UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_H5UTC() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-25T00:32:19Z"), T("2017-10-25T00:00:00Z"), T("2017-10-25T05:00:00Z") },
			{ T("2017-10-25T10:45:24Z"), T("2017-10-25T10:00:00Z"), T("2017-10-25T15:00:00Z") },
			{ T("2017-10-25T23:59:31Z"), T("2017-10-25T20:00:00Z"), T("2017-10-26T00:00:00Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H5UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_H12UTC() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-25T00:32:19Z"), T("2017-10-25T00:00:00Z"), T("2017-10-25T12:00:00Z") },
			{ T("2017-10-25T13:45:24Z"), T("2017-10-25T12:00:00Z"), T("2017-10-26T00:00:00Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H12UTC.getInterval(fix[i][0]));
		}		
	}
	
	@Test
	public void testGetInterval_H1MSK() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-25T03:32:19", MSK), T("2017-10-25T03:00:00", MSK), T("2017-10-25T04:00:00", MSK) },
			{ T("2017-10-25T13:45:24", MSK), T("2017-10-25T13:00:00", MSK), T("2017-10-25T14:00:00", MSK) },
			{ T("2017-10-25T23:59:31", MSK), T("2017-10-25T23:00:00", MSK), T("2017-10-26T00:00:00", MSK) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H1MSK.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_H9MSK() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-24T18:15:42", MSK), T("2017-10-24T18:00:00", MSK), T("2017-10-25T00:00:00", MSK) },
			{ T("2017-10-25T00:03:51", MSK), T("2017-10-25T00:00:00", MSK), T("2017-10-25T09:00:00", MSK) },
			{ T("2017-10-25T11:12:23", MSK), T("2017-10-25T09:00:00", MSK), T("2017-10-25T18:00:00", MSK) },
			{ T("2017-10-25T18:59:31", MSK), T("2017-10-25T18:00:00", MSK), T("2017-10-26T00:00:00", MSK) },
			{ T("2017-10-25T23:49:12", MSK), T("2017-10-25T18:00:00", MSK), T("2017-10-26T00:00:00", MSK) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H9MSK.getInterval(fix[i][0]));
		}		
	}
	
	@Test
	public void testGetInterval_H12MSK() {
		Instant fix[][] = {
			// time for, start of  interval, end of interval
			{ T("2017-10-24T02:12:44", MSK), T("2017-10-24T00:00:00", MSK), T("2017-10-24T12:00:00", MSK) },
			{ T("2017-10-24T11:35:05", MSK), T("2017-10-24T00:00:00", MSK), T("2017-10-24T12:00:00", MSK) },
			{ T("2017-10-24T18:24:30", MSK), T("2017-10-24T12:00:00", MSK), T("2017-10-25T00:00:00", MSK) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, H12MSK.getInterval(fix[i][0]));
		}					
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(H1MSK.equals(H1MSK));
		assertFalse(H1MSK.equals(null));
		assertFalse(H1MSK.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vLen = new Variant<>(9, 10);
		Variant<ZoneId> vZone = new Variant<>(vLen, MSK, UTC);
		Variant<?> iterator = vZone;
		int foundCnt = 0;
		ZTFHours x, found = null;
		do {
			x = new ZTFHours(vLen.get(), vZone.get());
			if ( H9MSK.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(9, found.getLength());
		assertEquals(MSK, found.getZoneID());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(179231, 7591).append(1).append(MSK).toHashCode(), H1MSK.hashCode());
		assertEquals(new HashCodeBuilder(179231, 7591).append(9).append(MSK).toHashCode(), H9MSK.hashCode());
		assertEquals(new HashCodeBuilder(179231, 7591).append(12).append(MSK).toHashCode(), H12MSK.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("H1[UTC]", H1UTC.toString());
		assertEquals("H5[UTC]", H5UTC.toString());
		assertEquals("H12[UTC]", H12UTC.toString());
		assertEquals("H1[Europe/Moscow]", H1MSK.toString());
		assertEquals("H9[Europe/Moscow]", H9MSK.toString());
		assertEquals("H12[Europe/Moscow]", H12MSK.toString());
	}
	
	@Test
	public void testIsCompatibleWith() {
		assertTrue(H5UTC.isCompatibleWith(H12UTC));
		assertFalse(H5UTC.isCompatibleWith(H9MSK));
	}
	
	@Test
	public void testToTFrame() {
		assertEquals(new TFHours(5), H5UTC.toTFrame());
		assertEquals(new TFHours(9), H9MSK.toTFrame());
	}

}
