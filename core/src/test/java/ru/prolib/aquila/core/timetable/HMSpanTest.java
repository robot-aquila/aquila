package ru.prolib.aquila.core.timetable;


import static org.junit.Assert.*;

import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class HMSpanTest {
	private HMSpan span;

	@Before
	public void setUp() throws Exception {
		span = new HMSpan(19, 45);
	}
	
	@Test
	public void testLess() throws Exception {
		FR fix[] = {
				new FR(new DateTime(1998, 1, 15, 19, 44, 59, 999), false),
				new FR(new DateTime(2013, 8, 10, 19, 44, 59, 999), false),
				new FR(new DateTime(2013, 8, 10, 19, 45,  0,   0), false),
				new FR(new DateTime(2013, 8, 10, 19, 45,  0,   1), true),
				new FR(new DateTime(1998, 1, 15, 23, 50,  1,   0), true),
				new FR(new DateTime(1992, 8, 14, 19, 47,  1,   0), true),
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
				new FR(new DateTime(1998, 1, 15, 19, 44, 59, 999), false),
				new FR(new DateTime(2013, 8, 10, 19, 44, 59, 999), false),
				new FR(new DateTime(2013, 8, 10, 19, 45,  0,   0), true),
				new FR(new DateTime(2013, 8, 10, 19, 45,  0,   1), true),
				new FR(new DateTime(1998, 1, 15, 23, 50,  1,   0), true),
				new FR(new DateTime(1992, 8, 14, 19, 47,  1,   0), true),
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
				new FR(new DateTime(2014, 8, 10, 19, 10, 15,   0), true),
				new FR(new DateTime(2013, 8, 10, 19, 44,  0,   0), true),
				new FR(new DateTime(2013, 8, 10, 19, 44, 59, 999), true),
				new FR(new DateTime(2013, 8, 10, 19, 45,  0,   0), false),
				new FR(new DateTime(2058, 8, 10, 19, 45,  0,   1), false),
				
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
				new FR(new DateTime(2013, 8, 14, 10, 41, 02, 125), true),
				new FR(new DateTime(2013, 8, 14, 19, 44, 59, 999), true),
				new FR(new DateTime(2013, 8, 14, 19, 45,  0,   0), true),
				new FR(new DateTime(2013, 8, 14, 19, 45,  0,   1), false),
				new FR(new DateTime(2013, 8, 14, 23, 19, 24, 831), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, span.greaterOrEquals(fr.time));
		}
	}
	
	@Test
	public void testAlign() throws Exception {
		DateTime initial = new DateTime(2013, 8, 10, 12, 28, 49, 180);
		DateTime expected = new DateTime(2013, 8, 10, 19, 45, 0, 0); 
		assertEquals(expected, span.align(initial));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(span.equals(span));
		assertFalse(span.equals(null));
		assertFalse(span.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vHr = new Variant<Integer>()
			.add(19)
			.add(1);
		Variant<Integer> vMin = new Variant<Integer>(vHr)
			.add(45)
			.add(18);
		Variant<?> iterator = vMin;
		int foundCnt = 0;
		HMSpan x, found = null;
		do {
			x = new HMSpan(vHr.get(), vMin.get());
			if ( span.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(19, found.getHour());
		assertEquals(45, found.getMinute());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsHourLessThan0() throws Exception {
		new HMSpan(-1, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsHourGreaterThan23() throws Exception {
		new HMSpan(24, 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsMinuteLessThan0() throws Exception {
		new HMSpan(0, -1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsMinuteGreaterThan59() throws Exception {
		new HMSpan(0, 60);
	}
	
	@Test
	public void testToMinutes() throws Exception {
		assertEquals(19 * 60 + 45, span.toMinutes());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		span = new HMSpan(19 * 60 + 45);
		assertEquals(19, span.getHour());
		assertEquals(45, span.getMinute());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("19:45", span.toString());
		assertEquals("08:01", new HMSpan(8, 1).toString());
	}

}
