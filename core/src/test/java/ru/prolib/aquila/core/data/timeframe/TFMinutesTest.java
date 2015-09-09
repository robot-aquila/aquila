package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.TimeUnit;

public class TFMinutesTest {
	private TFMinutes m1, m5, m7, m15, m241;

	@Before
	public void setUp() throws Exception {
		m1 = new TFMinutes(1);
		m5 = new TFMinutes(5);
		m7 = new TFMinutes(7); // нестандарт
		m15 = new TFMinutes(15);
		m241 = new TFMinutes(241); // нестандарт
	}
	
	@Test
	public void testIsIntraday() throws Exception {
		assertTrue(m1.isIntraday());
		assertTrue(m5.isIntraday());
		assertTrue(m7.isIntraday());
		assertTrue(m15.isIntraday());
		assertTrue(m241.isIntraday());
	}
	
	@Test
	public void testGetUnit() throws Exception {
		assertEquals(TimeUnit.MINUTE, m1.getUnit());
		assertEquals(TimeUnit.MINUTE, m5.getUnit());
		assertEquals(TimeUnit.MINUTE, m7.getUnit());
		assertEquals(TimeUnit.MINUTE, m15.getUnit());
		assertEquals(TimeUnit.MINUTE, m241.getUnit());
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(1, m1.getLength());
		assertEquals(5, m5.getLength());
		assertEquals(7, m7.getLength());
		assertEquals(15, m15.getLength());
		assertEquals(241, m241.getLength());
	}
	
	@Test
	public void testGetInterval_m1() throws Exception {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6, 20, 22,  5,  15),
				new DateTime(2013, 10, 6, 20, 22,  0,   0),
				new DateTime(2013, 10, 6, 20, 23,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 57, 19, 213),
				new DateTime(2013, 10, 6, 23, 57,  0,   0),
				new DateTime(2013, 10, 6, 23, 58,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 59, 34, 117),
				new DateTime(2013, 10, 6, 23, 59,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, m1.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m5() throws Exception {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6, 20, 22,  5,  15),
				new DateTime(2013, 10, 6, 20, 20,  0,   0),
				new DateTime(2013, 10, 6, 20, 25,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 57, 19, 213),
				new DateTime(2013, 10, 6, 23, 55,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 59, 34, 117),
				new DateTime(2013, 10, 6, 23, 55,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, m5.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m7() throws Exception {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6,  0,  1, 14, 715),
				new DateTime(2013, 10, 6,  0,  0,  0,   0),
				new DateTime(2013, 10, 6,  0,  7,  0,   0) },
			{	new DateTime(2013, 10, 6,  0,  7,  0,   0),
				new DateTime(2013, 10, 6,  0,  7,  0,   0),
				new DateTime(2013, 10, 6,  0, 14,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 51,  2, 472),
				new DateTime(2013, 10, 6, 23, 48,  0,   0),
				new DateTime(2013, 10, 6, 23, 55,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 55, 13,  24),
				new DateTime(2013, 10, 6, 23, 55,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, m7.getInterval(fix[i][0]));
		}
	}

	@Test
	public void testGetInterval_m15() throws Exception {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6,  0,  1, 14, 715),
				new DateTime(2013, 10, 6,  0,  0,  0,   0),
				new DateTime(2013, 10, 6,  0, 15,  0,   0) },
			{	new DateTime(2013, 10, 6,  0,  7,  0,   0),
				new DateTime(2013, 10, 6,  0,  0,  0,   0),
				new DateTime(2013, 10, 6,  0, 15,  0,   0) },
			{	new DateTime(2013, 10, 6, 23, 51,  2, 472),
				new DateTime(2013, 10, 6, 23, 45,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, m15.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m241() throws Exception {
		DateTime fix[][] = {
			// timestamp, from, to
			{	new DateTime(2013, 10, 6,  0,  1, 14, 715),
				new DateTime(2013, 10, 6,  0,  0,  0,   0),
				new DateTime(2013, 10, 6,  4,  1,  0,   0) },
			{	new DateTime(2013, 10, 6, 15, 27, 13,  28),
				new DateTime(2013, 10, 6, 12,  3,  0,   0),
				new DateTime(2013, 10, 6, 16,  4,  0,   0) }, 
			{	new DateTime(2013, 10, 6, 23, 15,  8, 182),
				new DateTime(2013, 10, 6, 20,  5,  0,   0),
				new DateTime(2013, 10, 7,  0,  0,  0,   0) },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Interval expected = new Interval(fix[i][1], fix[i][2]);
			assertEquals(msg, expected, m241.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(m1.equals(m1));
		assertTrue(m1.equals(new TFMinutes(1)));
		assertFalse(m1.equals(m5));
		assertFalse(m1.equals(null));
		assertFalse(m1.equals(this));
	}

}
