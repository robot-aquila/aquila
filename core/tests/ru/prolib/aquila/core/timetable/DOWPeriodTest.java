package ru.prolib.aquila.core.timetable;


import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class DOWPeriodTest {
	private DOWSpan from, to;
	private DOWPeriod period;
	
	@Before
	public void setUp() throws Exception {
		from = new DOWSpan(DOW.TUESDAY);
		to = new DOWSpan(DOW.FRIDAY);
		period = new DOWPeriod(from, to);
	}
	
	@Test
	public void testContains_DateTime() throws Exception {
		FR fix[] = {
				new FR(new DateTime(2013, 8, 11, 18, 41, 54,   0), false),
				new FR(new DateTime(2013, 8, 12,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 12, 18, 41, 54, 102), false),
				new FR(new DateTime(2013, 8, 12, 23, 59, 59, 999), false),
				new FR(new DateTime(2013, 8, 13,  0,  0,  0,   0), true),
				new FR(new DateTime(2013, 8, 14, 10, 31, 21, 908), true),
				new FR(new DateTime(2013, 8, 16, 10, 31, 21, 908), true),
				new FR(new DateTime(2013, 8, 16, 23, 59, 59, 999), true),
				new FR(new DateTime(2013, 8, 17,  0,  0,  0,   0), false),
				new FR(new DateTime(2013, 8, 17,  0,  0,  0, 112), false),
				new FR(new DateTime(2013, 8, 18, 23, 59, 59, 999), false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix[i];
			assertEquals(msg, fr.expected, period.contains(fr.time));
		}
	}
	
	@Test
	public void testContains_DOWSpan() throws Exception {
		assertFalse(period.contains(new DOWSpan(DOW.MONDAY)));
		assertTrue(period.contains(new DOWSpan(DOW.TUESDAY)));
		assertTrue(period.contains(new DOWSpan(DOW.WEDNESDAY)));
		assertTrue(period.contains(new DOWSpan(DOW.THURSDAY)));
		assertTrue(period.contains(new DOWSpan(DOW.FRIDAY)));
		assertFalse(period.contains(new DOWSpan(DOW.SATURDAY)));
		assertFalse(period.contains(new DOWSpan(DOW.SUNDAY)));
	}
	
	@Test
	public void testNextDate() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 11,  2, 15, 24,   8),
						new DateTime(2013, 8, 13,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 12, 12, 45, 12, 928),
						new DateTime(2013, 8, 13,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 13,  0,  0,  0,   0),
						new DateTime(2013, 8, 14,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 14, 22, 50, 10, 154),
						new DateTime(2013, 8, 15,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 15,  1, 12, 41, 871),
						new DateTime(2013, 8, 16,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 16, 23, 59, 59, 999),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 17,  0,  0,  0,   0),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 18,  9, 45,  0,   1),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 19, 10,  0,  0,   0),
						new DateTime(2013, 8, 20,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 20,  7, 50, 32, 475),
						new DateTime(2013, 8, 21,  0,  0,  0,   0)),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextDate(fr.time));
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
		Variant<DOWSpan> vFr = new Variant<DOWSpan>()
			.add(new DOWSpan(DOW.TUESDAY))
			.add(new DOWSpan(DOW.MONDAY));
		Variant<DOWSpan> vTo = new Variant<DOWSpan>(vFr)
			.add(new DOWSpan(DOW.FRIDAY))
			.add(new DOWSpan(DOW.SUNDAY));
		Variant<?> iterator = vTo;
		int foundCnt = 0;
		DOWPeriod x, found = null;
		do {
			x = new DOWPeriod(vFr.get(), vTo.get());
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
	public void testIsEndDate() throws Exception {
		assertFalse(period.isEndDate(new DateTime(2013, 8, 12, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 13, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 14, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 15, 0, 0, 0, 0)));
		assertTrue(period.isEndDate(new DateTime(2013, 8, 16, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 17, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 18, 0, 0, 0, 0)));
		assertFalse(period.isEndDate(new DateTime(2013, 8, 19, 0, 0, 0, 0)));
	}
	
	@Test
	public void testNextEndDate() throws Exception {
		FR2 fix[] = {
				new FR2(new DateTime(2013, 8, 11,  2, 15, 24,   8),
						new DateTime(2013, 8, 16,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 12,  0,  0,  0,   0),
						new DateTime(2013, 8, 16,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 14,  0,  0,  0,   0),
						new DateTime(2013, 8, 16,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 16,  0,  0,  0,   0),
						new DateTime(2013, 8, 16,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 16, 18, 54, 12, 827),
						new DateTime(2013, 8, 16, 18, 54, 12, 827)),						
				new FR2(new DateTime(2013, 8, 17, 11, 20, 48, 191),
						new DateTime(2013, 8, 23,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 23,  0,  0,  0,   0),
						new DateTime(2013, 8, 23,  0,  0,  0,   0)),
				new FR2(new DateTime(2013, 8, 24,  1,  0,  0,   0),
						new DateTime(2013, 8, 30,  0,  0,  0,   0)),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			FR2 fr = fix[i];
			assertEquals(msg, fr.expected, period.nextEndDate(fr.time));
		}
	}
	
	@Test
	public void testConstruct2Consts() throws Exception {
		period = new DOWPeriod(DOW.SATURDAY, DOW.SUNDAY);
		DOWPeriod expected =
			new DOWPeriod(new DOWSpan(DOW.SATURDAY), new DOWSpan(DOW.SUNDAY));
		assertEquals(expected, period);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsFromLessThanTo() throws Exception {
		new DOWPeriod(DOW.SUNDAY, DOW.FRIDAY);
	}
	
	@Test
	public void testOverlap() throws Exception {
		assertTrue(period.overlap(new DOWPeriod(DOW.MONDAY, DOW.SATURDAY)));
		assertTrue(period.overlap(new DOWPeriod(DOW.MONDAY, DOW.THURSDAY)));
		assertTrue(period.overlap(new DOWPeriod(DOW.THURSDAY, DOW.SATURDAY)));
		assertTrue(period.overlap(new DOWPeriod(DOW.WEDNESDAY, DOW.THURSDAY)));
		assertTrue(period.overlap(new DOWPeriod(DOW.TUESDAY, DOW.SUNDAY)));
		assertTrue(period.overlap(new DOWPeriod(DOW.WEDNESDAY, DOW.FRIDAY)));
		assertFalse(period.overlap(new DOWPeriod(DOW.MONDAY, DOW.MONDAY)));
		assertFalse(period.overlap(new DOWPeriod(DOW.SATURDAY, DOW.SATURDAY)));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("TUESDAY-FRIDAY", period.toString());
		assertEquals("MONDAY", new DOWPeriod(DOW.MONDAY, DOW.MONDAY).toString());
	}

}
