package ru.prolib.aquila.core.timetable;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.*;

public class DOWSpanTest {
	private DOWSpan span;
	
	static class FR2 {
		private final DOW dow;
		private final DateTime time, expected;
		FR2(DOW dow, DateTime time, DateTime expected) {
			this.dow = dow;
			this.time = time;
			this.expected = expected;
		}
	}

	@Before
	public void setUp() throws Exception {
		span = new DOWSpan(DOW.WEDNESDAY);
	}
	
	@Test
	public void testLess() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 11, 14, 31, 53,  19), true),
				new FR(new DateTime(2013, 8, 12,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 13, 18, 24, 15,  48), false),
				new FR(new DateTime(2013, 8, 14,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 14, 12, 45, 19, 292), false),
				new FR(new DateTime(2013, 8, 14, 23, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 15,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 16,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 17,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 18,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 19,  0,  0,  0,   0), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, span.less(fr.time));
		}
	}
	
	@Test
	public void testLessOrEquals() throws Exception {
		FR fix[] = {
			new FR(new DateTime(2013, 8, 11, 14, 31, 53, 19), true),
			new FR(new DateTime(2013, 8, 12, 0, 0, 0, 0), false),
			new FR(new DateTime(2013, 8, 14, 0, 0, 0, 0), true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, span.lessOrEquals(fr.time));
		}
	}
	
	@Test
	public void testGreater() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 11, 14, 31, 53, 19), false),
				new FR(new DateTime(2013, 8, 12, 0, 0, 0, 0), true),
				new FR(new DateTime(2013, 8, 13, 0, 0, 0, 0), true),
				new FR(new DateTime(2013, 8, 14, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 14, 23, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 15, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 16, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 17, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 18, 0, 0, 0, 0), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, span.greater(fr.time));
		}
	}
	
	@Test
	public void testGreaterOrEquals() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 11, 14, 31, 53, 19), false),
				new FR(new DateTime(2013, 8, 12, 0, 0, 0, 0), true),
				new FR(new DateTime(2013, 8, 13, 0, 0, 0, 0), true),
				new FR(new DateTime(2013, 8, 14, 0, 0, 0, 0), true),
				new FR(new DateTime(2013, 8, 14, 23, 59, 59, 999), true),
				new FR(new DateTime(2013, 8, 15, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 16, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 17, 0, 0, 0, 0), false),
				new FR(new DateTime(2013, 8, 18, 0, 0, 0, 0), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, span.greaterOrEquals(fr.time));
		}
	}
	
	@Test
	public void testAlign() throws Exception {
		FR2 fix[] = {
			new FR2(DOW.WEDNESDAY, new DateTime(2013, 8, 11, 12, 28, 49, 180),
					   new DateTime(2013, 8, 14, 12, 28, 49, 180)),
			new FR2(DOW.WEDNESDAY, new DateTime(2013, 8,  6, 12, 28, 49, 180),
					   new DateTime(2013, 8,  7, 12, 28, 49, 180)),
			new FR2(DOW.MONDAY, new DateTime(2013, 8,  5, 12, 28, 49, 180),
					   new DateTime(2013, 8,  5, 12, 28, 49, 180)),
			new FR2(DOW.MONDAY, new DateTime(2013, 8,  4, 12, 28, 49, 180),
					   new DateTime(2013, 8,  5, 12, 28, 49, 180)),
			new FR2(DOW.MONDAY, new DateTime(2013, 8,  5, 12, 28, 49, 180),
					   new DateTime(2013, 8,  5, 12, 28, 49, 180)),
			new FR2(DOW.MONDAY, new DateTime(2013, 8,  6, 12, 28, 49, 180),
					   new DateTime(2013, 8, 12, 12, 28, 49, 180)),
			new FR2(DOW.MONDAY, new DateTime(2013, 8, 10, 12, 28, 49, 180),
					   new DateTime(2013, 8, 12, 12, 28, 49, 180)),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			span = new DOWSpan(fr.dow);
			assertEquals(msg, fr.expected, span.align(fr.time));
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(span.equals(span));
		assertFalse(span.equals(null));
		assertFalse(span.equals(this));
		assertTrue(span.equals(new DOWSpan(DOW.WEDNESDAY)));
		assertFalse(span.equals(new DOWSpan(DOW.MONDAY)));
	}
	
	@Test
	public void testToString() {
		assertEquals("WEDNESDAY", span.toString());
	}

}
