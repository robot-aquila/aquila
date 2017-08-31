package ru.prolib.aquila.core.data.timeframe;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import org.threeten.extra.Interval;

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
		assertEquals(TimeUnit.MINUTES, m1.getUnit());
		assertEquals(TimeUnit.MINUTES, m5.getUnit());
		assertEquals(TimeUnit.MINUTES, m7.getUnit());
		assertEquals(TimeUnit.MINUTES, m15.getUnit());
		assertEquals(TimeUnit.MINUTES, m241.getUnit());
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
			assertEquals(msg, expected, m1.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m5() throws Exception {
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
			assertEquals(msg, expected, m5.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m7() throws Exception {
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
			assertEquals(msg, expected, m7.getInterval(fix[i][0]));
		}
	}

	@Test
	public void testGetInterval_m15() throws Exception {
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
			assertEquals(msg, expected, m15.getInterval(fix[i][0]));
		}
	}
	
	@Test
	public void testGetInterval_m241() throws Exception {
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
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(859, 175).append(1).toHashCode(), m1.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(5).toHashCode(), m5.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(7).toHashCode(), m7.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(15).toHashCode(), m15.hashCode());
		assertEquals(new HashCodeBuilder(859, 175).append(241).toHashCode(), m241.hashCode());
	}

}
