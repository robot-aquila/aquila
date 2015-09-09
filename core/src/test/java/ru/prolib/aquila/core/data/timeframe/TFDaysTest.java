package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.TimeUnit;

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
		assertEquals(TimeUnit.DAY, d1.getUnit());
		assertEquals(TimeUnit.DAY, d7.getUnit());
		assertEquals(TimeUnit.DAY, d21.getUnit());
	}
	
	@Test
	public void testGetLength() {
		assertEquals(1, d1.getLength());
		assertEquals(7, d7.getLength());
		assertEquals(21, d21.getLength());
	}
	
	@Test
	public void testGetInterval_d1() {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6,  0,  1, 14, 715),
				new DateTime(2013, 10, 6,  0,  0,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
			{	new DateTime(2013, 10, 9,  0,  7,  0,   0),
				new DateTime(2013, 10, 9,  0,  0,  0,   0),
				new DateTime(2013, 10,10,  0,  0,  0,   0) },
			{	new DateTime(2013,  1, 1, 23, 51,  2, 472),
				new DateTime(2013,  1, 1,  0,  0,  0,   0),
				new DateTime(2013,  1, 2,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, d1.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_d7() {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2015, 1, 12, 15, 48, 19, 112),
				new DateTime(2015, 1,  8,  0,  0,  0,   0),
				new DateTime(2015, 1, 15,  0,  0,  0,   0) },
			{	new DateTime(2015, 5, 19, 20, 10, 24,  51),
				new DateTime(2015, 5, 14,  0,  0,  0,   0),
				new DateTime(2015, 5, 21,  0,  0,  0,   0) },
			{	new DateTime(2015, 9,  8, 22, 10, 45, 972),
				new DateTime(2015, 9,  3,  0,  0,  0,   0),
				new DateTime(2015, 9, 10,  0,  0,  0,   0) }
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
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
