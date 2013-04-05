package ru.prolib.aquila.core.data;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: DataSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class DataSeriesImplTest {
	private DataSeriesImpl series;

	@Before
	public void setUp() throws Exception {
		series = new DataSeriesImpl("test", 512);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		series = new DataSeriesImpl();
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		series = new DataSeriesImpl("foobar");
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
		Variant<Double[]> vData = new Variant<Double[]>(vLmt)
			.add(new Double[] { 12.34d, 15.03d })
			.add(new Double[] { });
		Variant<?> iterator = vData;
		int foundCnt = 0;
		DataSeriesImpl found = null, x = null;
		do {
			x = new DataSeriesImpl(vId.get(), vLmt.get());
			for ( Double value : vData.get() ) {
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
