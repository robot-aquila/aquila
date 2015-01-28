package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;

/**
 * 2012-04-25<br>
 * $Id: GCandleTimeTest.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public class GCandleIntervalTest {
	private GCandleInterval getter;
	
	@Before
	public void setUp() throws Exception {
		getter = new GCandleInterval();
	}
	
	@Test
	public void testGet() throws Exception {
		DateTime from = new DateTime(2013, 10, 6, 16, 10, 0);
		Interval interval = new Interval(from, Minutes.minutes(5));
		Candle candle = new Candle(interval, 0, 0, 0, 0, 0);
		assertEquals(interval, getter.get(candle));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GCandleInterval()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}

}
