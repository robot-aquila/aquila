package ru.prolib.aquila.core.timetable;


import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class HMPeriodTest {
	private HMSpan from, to;
	private HMPeriod period;
	
	static class FR3 {
		private final int from_h, from_m, to_h, to_m;
		private final boolean expected;
		FR3(int from_h, int from_m, int to_h, int to_m, boolean expected) {
			this.from_h = from_h;
			this.from_m = from_m;
			this.to_h = to_h;
			this.to_m = to_m;
			this.expected = expected;
		}
	}
	
	static class FR4 {
		private final HMSpan span;
		private boolean expected;
		FR4(HMSpan span, boolean expected) {
			this.span = span;
			this.expected = expected;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		from = new HMSpan(10, 30);
		to = new HMSpan(14, 0);
		period = new HMPeriod(from, to);
	}
	
	@Test
	public void testContains_DateTime() throws Exception {
		FR fix[] = {
			new FR(new DateTime(2013, 8, 10,  9, 59, 59, 999), false),
			new FR(new DateTime(2013, 8, 10, 10, 30,  0,   0), true),
			new FR(new DateTime(2013, 8, 10, 12, 18, 42, 115), true),
			new FR(new DateTime(2013, 8, 10, 13, 59, 59, 999), true),
			new FR(new DateTime(2013, 8, 10, 14,  0,  0,   0), false),
			new FR(new DateTime(2013, 8, 10, 19,  3, 52, 992), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.time));
		}
	}
	
	@Test
	public void testContains_HMSpan() throws Exception {
		FR4 fix[] = {
				new FR4(new HMSpan( 0,  0), false),
				new FR4(new HMSpan(10, 30), true),
				new FR4(new HMSpan(12, 15), true),
				new FR4(new HMSpan(13, 59), true),
				new FR4(new HMSpan(14,  0), false),
				new FR4(new HMSpan(23, 28), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR4 fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.span));
		}
	}
	
	@Test
	public void testNextStartTime() throws Exception {
		FR2 fix[] = {
			new FR2(new DateTime(2013, 8, 10,  0,  0,  0,   0),
					new DateTime(2013, 8, 10, 10, 30,  0,   0)),
			new FR2(new DateTime(2013, 8, 10,  9, 50, 13, 181),
					new DateTime(2013, 8, 10, 10, 30,  0,   0)),
			new FR2(new DateTime(2013, 8, 10, 10, 30,  0,   0), null),
			new FR2(new DateTime(2013, 8, 10, 10, 30,  0,   1), null),
			new FR2(new DateTime(2013, 8, 10, 19,  3, 52, 992), null),
			new FR2(new DateTime(2013, 7, 31, 23, 59,  0,   1), null),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextStartTime(fr.time));
		}
	}
	
	@Test
	public void testNextEndTime() throws Exception {
		FR2 fix[] = {
			new FR2(new DateTime(2013, 8, 10,  8,  0,  0,   0),
					new DateTime(2013, 8, 10, 14,  0,  0,   0)),
			new FR2(new DateTime(2013, 8, 10, 12,  1,  0, 999),
					new DateTime(2013, 8, 10, 14,  0,  0,   0)),
			new FR2(new DateTime(2013, 8, 10, 14,  0,  0,   0), null),
			new FR2(new DateTime(2013, 8, 10, 14,  0,  0,   1), null),
			new FR2(new DateTime(2013, 8, 10, 23, 18, 54,  15), null),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextEndTime(fr.time));
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(period.equals(period));
		assertFalse(period.equals(null));
		assertFalse(period.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<HMSpan> vFr = new Variant<HMSpan>()
			.add(new HMSpan(10, 30))
			.add(new HMSpan( 8, 42));
		Variant<HMSpan> vTo = new Variant<HMSpan>(vFr)
			.add(new HMSpan(14, 0))
			.add(new HMSpan(17, 15));
		Variant<?> iterator = vTo;
		int foundCnt = 0;
		HMPeriod x, found = null;
		do {
			x = new HMPeriod(vFr.get(), vTo.get());
			if ( period.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(from, found.getPeriodFrom());
		assertEquals(to, found.getPeriodTo());
	}
	
	@Test
	public void testOverlap() throws Exception {
		FR3 fix[] = {
				new FR3( 0,  0,  8,  0, false),
				new FR3( 0,  0, 10, 30, false),
				new FR3( 0,  0, 11,  0, true),
				new FR3( 0,  0, 15,  0, true),
				new FR3(12,  0, 15,  0, true),
				new FR3(15,  0, 18,  0, false),
		};
		HMPeriod test;
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR3 fr = fix[i];
			test = new HMPeriod(new HMSpan(fr.from_h, fr.from_m),
					new HMSpan(fr.to_h, fr.to_m));
			assertEquals(msg, fr.expected, period.overlap(test));
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfSpansEquals() throws Exception {
		new HMPeriod(new HMSpan(13, 40), new HMSpan(13, 40));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfFromLessThanTo() throws Exception {
		new HMPeriod(new HMSpan(13, 40), new HMSpan(8, 30));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("10:30-14:00", period.toString());
	}
	
	@Test
	public void testParse() throws Exception {
		assertEquals(period, HMPeriod.parse("10:30-14:00"));
	}

}
