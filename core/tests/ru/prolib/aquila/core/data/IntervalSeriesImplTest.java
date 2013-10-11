package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: TimeSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class IntervalSeriesImplTest {
	private Interval interval1, interval2, interval3;
	private IntervalSeriesImpl series;

	@Before
	public void setUp() throws Exception {
		Minutes period = Minutes.minutes(5);
		interval1 = new Interval(new DateTime(2010, 1, 1, 8, 30, 0), period);
		interval2 = new Interval(interval1.getEnd(), period);
		interval3 = new Interval(interval2.getEnd(), period);
		series = new IntervalSeriesImpl("test", 512);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		series = new IntervalSeriesImpl();
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		series = new IntervalSeriesImpl("foobar");
		assertEquals("foobar", series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("test", series.getId());
		assertEquals(512, series.getStorageLimit());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("test")
			.add("foobar");
		Variant<Integer> vLmt = new Variant<Integer>(vId)
			.add(512)
			.add(1024);
		Variant<Interval[]> vInt = new Variant<Interval[]>(vLmt)
			.add(new Interval[] { interval1, interval2 })
			.add(new Interval[] { interval3 });
		series.add(interval1);
		series.add(interval2);
		Variant<?> iterator = vInt;
		int foundCnt = 0;
		IntervalSeriesImpl found = null, x = null;
		do {
			x = new IntervalSeriesImpl(vId.get(), vLmt.get());
			for ( Interval i : vInt.get() ) x.add(i);
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("test", found.getId());
		assertEquals(512, found.getStorageLimit());
		// check the data
		assertEquals(2, found.getLength());
		assertEquals(interval1, found.get(0));
		assertEquals(interval2, found.get(1));
	}

}
