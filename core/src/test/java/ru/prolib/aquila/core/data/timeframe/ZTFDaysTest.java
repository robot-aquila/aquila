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

public class ZTFDaysTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Instant T(String timeString, ZoneId zoneID) {
		return ZonedDateTime.of(LocalDateTime.parse(timeString), zoneID).toInstant();
	}
	
	private ZTFDays D1UTC, D7UTC, D21UTC, D7MSK;
	private ZoneId UTC, MSK;

	@Before
	public void setUp() throws Exception {
		UTC = ZoneId.of("UTC");
		MSK = ZoneId.of("Europe/Moscow");
		D1UTC = new ZTFDays(1);
		D7UTC = new ZTFDays(7);
		D21UTC = new ZTFDays(21);
		D7MSK = new ZTFDays(7, MSK);
	}
	
	@Test
	public void testIsIntraday() {
		assertFalse(D1UTC.isIntraday());
		assertFalse(D7UTC.isIntraday());
		assertFalse(D21UTC.isIntraday());
		assertFalse(D7MSK.isIntraday());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(ChronoUnit.DAYS, D1UTC.getUnit());
		assertEquals(ChronoUnit.DAYS, D7UTC.getUnit());
		assertEquals(ChronoUnit.DAYS, D21UTC.getUnit());
		assertEquals(ChronoUnit.DAYS, D7MSK.getUnit());
	}
	
	@Test
	public void testGetLength() {
		assertEquals( 1, D1UTC.getLength());
		assertEquals( 7, D7UTC.getLength());
		assertEquals(21, D21UTC.getLength());
		assertEquals( 7, D7MSK.getLength());
	}
	
	@Test
	public void testGetInterval_D1UTC() {
		Instant fix[][] = {
			// timestamp, from, to
			{ T("2013-10-06T00:01:14.715Z"), T("2013-10-06T00:00:00.000Z"), T("2013-10-07T00:00:00.000Z") },
			{ T("2013-10-09T00:07:00.000Z"), T("2013-10-09T00:00:00.000Z"), T("2013-10-10T00:00:00.000Z") },
			{ T("2013-01-01T23:51:02.472Z"), T("2013-01-01T00:00:00.000Z"), T("2013-01-02T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, D1UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_D7UTC() {
		Instant fix[][] = {
			// timestamp, from, to
			{ T("2015-01-12T15:48:19.112Z"), T("2015-01-08T00:00:00.000Z"), T("2015-01-15T00:00:00.000Z") },
			{ T("2015-05-19T20:10:24.051Z"), T("2015-05-14T00:00:00.000Z"), T("2015-05-21T00:00:00.000Z") },
			{ T("2015-09-08T22:10:45.972Z"), T("2015-09-03T00:00:00.000Z"), T("2015-09-10T00:00:00.000Z") }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, D7UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_D21UTC() {
		Instant fix[][] = {
			{ T("2017-01-15T00:00:00Z"), T("2017-01-01T00:00:00Z"), T("2017-01-22T00:00:00Z") },
			{ T("2017-01-21T20:59:59Z"), T("2017-01-01T00:00:00Z"), T("2017-01-22T00:00:00Z") },
			{ T("2017-01-21T23:59:59Z"), T("2017-01-01T00:00:00Z"), T("2017-01-22T00:00:00Z") },
			{ T("2017-02-05T00:00:00Z"), T("2017-01-22T00:00:00Z"), T("2017-02-12T00:00:00Z") },
			{ T("2017-12-20T00:00:00Z"), T("2017-12-03T00:00:00Z"), T("2017-12-24T00:00:00Z") },
			{ T("2017-12-28T00:00:00Z"), T("2017-12-24T00:00:00Z"), T("2018-01-01T00:00:00Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, D21UTC.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_D7MSK() {
		Instant fix[][] = {
			{ T("2015-01-12T15:48:19.112", MSK), T("2015-01-08T00:00:00", MSK), T("2015-01-15T00:00:00", MSK) },
			{ T("2015-05-19T20:10:24.051", MSK), T("2015-05-14T00:00:00", MSK), T("2015-05-21T00:00:00", MSK) },
			{ T("2015-09-08T22:10:45.972", MSK), T("2015-09-03T00:00:00", MSK), T("2015-09-10T00:00:00", MSK) }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, D7MSK.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(D7MSK.equals(D7MSK));
		assertFalse(D7MSK.equals(null));
		assertFalse(D7MSK.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vLen = new Variant<>(7, 15);
		Variant<ZoneId> vZone = new Variant<>(vLen, MSK, UTC);
		Variant<?> iterator = vZone;
		int foundCnt = 0;
		ZTFDays x, found = null;
		do {
			x = new ZTFDays(vLen.get(), vZone.get());
			if ( D7MSK.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(7, found.getLength());
		assertEquals(MSK, found.getZoneID());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(9921, 57015).append( 1).append(UTC).toHashCode(), D1UTC.hashCode());
		assertEquals(new HashCodeBuilder(9921, 57015).append( 7).append(UTC).toHashCode(), D7UTC.hashCode());
		assertEquals(new HashCodeBuilder(9921, 57015).append(21).append(UTC).toHashCode(), D21UTC.hashCode());
		assertEquals(new HashCodeBuilder(9921, 57015).append( 7).append(MSK).toHashCode(), D7MSK.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("D1[UTC]", D1UTC.toString());
		assertEquals("D7[UTC]", D7UTC.toString());
		assertEquals("D21[UTC]", D21UTC.toString());
		assertEquals("D7[Europe/Moscow]", D7MSK.toString());
	}
	
	@Test
	public void testIsCompatibleWith() {
		assertTrue(D1UTC.isCompatibleWith(D7UTC));
		assertFalse(D1UTC.isCompatibleWith(D7MSK));
	}
	
	@Test
	public void testToTFrame() {
		assertEquals(new TFDays(1), D1UTC.toTFrame());
		assertEquals(new TFDays(7), D7MSK.toTFrame());
	}

}
