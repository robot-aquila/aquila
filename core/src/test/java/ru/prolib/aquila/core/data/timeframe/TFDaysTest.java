package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

public class TFDaysTest {
	private TFDays d1, d7, d21;

	@Before
	public void setUp() throws Exception {
		d1 = new TFDays(1);
		d7 = new TFDays(7);
		d21 = new TFDays(21);
	}
	
	@Test
	public void testIsIntraday() {
		assertFalse(d1.isIntraday());
		assertFalse(d7.isIntraday());
		assertFalse(d21.isIntraday());
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(TimeUnit.DAYS, d1.getUnit());
		assertEquals(TimeUnit.DAYS, d7.getUnit());
		assertEquals(TimeUnit.DAYS, d21.getUnit());
	}
	
	@Test
	public void testGetLength() {
		assertEquals(1, d1.getLength());
		assertEquals(7, d7.getLength());
		assertEquals(21, d21.getLength());
	}
	
	@Test
	public void testGetInterval_d1() {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2013-10-06T00:01:14.715Z"),
				Instant.parse("2013-10-06T00:00:00.000Z"),
				Instant.parse("2013-10-07T00:00:00.000Z") },
			{	Instant.parse("2013-10-09T00:07:00.000Z"),
				Instant.parse("2013-10-09T00:00:00.000Z"),
				Instant.parse("2013-10-10T00:00:00.000Z") },
			{	Instant.parse("2013-01-01T23:51:02.472Z"),
				Instant.parse("2013-01-01T00:00:00.000Z"),
				Instant.parse("2013-01-02T00:00:00.000Z") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, d1.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_d7() {
		Instant fix[][] = {
			// timestamp, from, to
			{	Instant.parse("2015-01-12T15:48:19.112Z"),
				Instant.parse("2015-01-08T00:00:00.000Z"),
				Instant.parse("2015-01-15T00:00:00.000Z") },
			{	Instant.parse("2015-05-19T20:10:24.051Z"),
				Instant.parse("2015-05-14T00:00:00.000Z"),
				Instant.parse("2015-05-21T00:00:00.000Z") },
			{	Instant.parse("2015-09-08T22:10:45.972Z"),
				Instant.parse("2015-09-03T00:00:00.000Z"),
				Instant.parse("2015-09-10T00:00:00.000Z") }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = Interval.of(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, d7.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testEquals() {
		assertTrue(d1.equals(d1));
		assertTrue(d1.equals(new TFDays(1)));
		assertFalse(d1.equals(d7));
		assertFalse(d1.equals(null));
	}

}
