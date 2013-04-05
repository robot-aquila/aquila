package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: CandleSeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleSeriesImplTest {
	private CandleSeriesImpl series;
	private Date time1 = new Date(), time2 = new Date(), time3 = new Date();
	private Candle[] fixture = {
			new Candle(time1, 1.1d, 2.2d, 3.3d, 4.4d, 100l),
			new Candle(time2, 2.1d, 3.2d, 4.3d, 5.4d, 200l),
			new Candle(time3, 3.1d, 4.2d, 5.3d, 6.4d, 300l),
	};

	@Before
	public void setUp() throws Exception {
		series = new CandleSeriesImpl("foo", 512);
		for ( Candle candle : fixture ) {
			series.add(candle);
		}
	}
	
	@Test
	public void testConstruct0() throws Exception {
		series = new CandleSeriesImpl();
		assertEquals(Series.DEFAULT_ID, series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		series = new CandleSeriesImpl("zulu24");
		assertEquals("zulu24", series.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, series.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		series = new CandleSeriesImpl("zulu24", 128);
		assertEquals("zulu24", series.getId());
		assertEquals(128, series.getStorageLimit());
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
			.add("foo")
			.add("bar");
		Variant<Integer> vLmt = new Variant<Integer>(vId)
			.add(512)
			.add(128);
		Variant<Candle[]> vData = new Variant<Candle[]>(vLmt)
			.add(fixture)
			.add(new Candle[] { });
		Variant<?> iterator = vData;
		int foundCnt = 0;
		CandleSeriesImpl x = null, found = null;
		do {
			x = new CandleSeriesImpl(vId.get(), vLmt.get());
			for ( Candle candle : vData.get() ) {
				x.add(candle);
			}
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertEquals(512, found.getStorageLimit());
		// check series data
		for ( int i = 0; i < fixture.length; i ++ ) {
			assertEquals(fixture[i], found.get(i));
		}
	}
	
	@Test
	public void testGetOpen() throws Exception {
		DataSeries proxy = series.getOpen();
		assertSame(proxy, series.getOpen());
		assertNotNull(proxy);
		assertEquals(1.1d, proxy.get(0), 0.01d);
		assertEquals(2.1d, proxy.get(1), 0.01d);
		assertEquals(3.1d, proxy.get(2), 0.01d);
	}
	
	@Test
	public void testGetHigh() throws Exception {
		DataSeries proxy = series.getHigh();
		assertSame(proxy, series.getHigh());
		assertNotNull(proxy);
		assertEquals(2.2d, proxy.get(0), 0.01d);
		assertEquals(3.2d, proxy.get(1), 0.01d);
		assertEquals(4.2d, proxy.get(2), 0.01d);
	}

	@Test
	public void testGetLow() throws Exception {
		DataSeries proxy = series.getLow();
		assertSame(proxy, series.getLow());
		assertNotNull(proxy);
		assertEquals(3.3d, proxy.get(0), 0.01d);
		assertEquals(4.3d, proxy.get(1), 0.01d);
		assertEquals(5.3d, proxy.get(2), 0.01d);
	}
	
	@Test
	public void testGetClose() throws Exception {
		DataSeries proxy = series.getClose();
		assertSame(proxy, series.getClose());
		assertNotNull(proxy);
		assertEquals(4.4d, proxy.get(0), 0.01d);
		assertEquals(5.4d, proxy.get(1), 0.01d);
		assertEquals(6.4d, proxy.get(2), 0.01d);
	}

	@Test
	public void testGetVolume() throws Exception {
		DataSeries proxy = series.getVolume();
		assertSame(proxy, series.getVolume());
		assertNotNull(proxy);
		assertEquals(100d, proxy.get(0), 0.01d);
		assertEquals(200d, proxy.get(1), 0.01d);
		assertEquals(300d, proxy.get(2), 0.01d);
	}
	
	@Test
	public void testGetTime() throws Exception {
		TimeSeries proxy = series.getTime();
		assertSame(proxy, series.getTime());
		assertNotNull(proxy);
		assertSame(time1, proxy.get(0));
		assertSame(time2, proxy.get(1));
		assertSame(time3, proxy.get(2));
	}

}
