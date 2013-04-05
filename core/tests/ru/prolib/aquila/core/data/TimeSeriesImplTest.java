package ru.prolib.aquila.core.data;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: TimeSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class TimeSeriesImplTest {
	private TimeSeriesImpl series;

	@Before
	public void setUp() throws Exception {
		series = new TimeSeriesImpl("test", 512);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		series = new TimeSeriesImpl();
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		series = new TimeSeriesImpl("foobar");
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
		Variant<Date[]> vData = new Variant<Date[]>(vLmt)
			.add(new Date[] { new Date(), new Date() })
			.add(new Date[] { });
		Variant<?> iterator = vData;
		int foundCnt = 0;
		TimeSeriesImpl found = null, x = null;
		do {
			x = new TimeSeriesImpl(vId.get(), vLmt.get());
			for ( Date value : vData.get() ) {
				x.add(value);
			}
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("test", found.getId());
		assertEquals(512, found.getStorageLimit());
		// check the data
		assertEquals(0, found.getLength());
	}

}
