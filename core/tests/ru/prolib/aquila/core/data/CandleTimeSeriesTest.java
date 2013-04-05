package ru.prolib.aquila.core.data;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: CandleTimeSeriesTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleTimeSeriesTest {
	private Series<Candle> candles;
	private CandleTimeSeries series;

	@Before
	public void setUp() throws Exception {
		candles = new SeriesImpl<Candle>();
		series = new CandleTimeSeries("foo", candles);
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertEquals("foo", series.getId());
		assertSame(candles, series.getCandles());
		assertEquals(new GCandleTime(), series.getGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		SeriesImpl<Candle> candles2 = new SeriesImpl<Candle>();
		candles2.add(new Candle(new Date(), 10d, 20l));
		Variant<String> vId = new Variant<String>()
			.add("bar")
			.add("foo");
		Variant<Series<Candle>> vCandles =  new Variant<Series<Candle>>(vId)
			.add(candles)
			.add(candles2);
		Variant<?> iterator = vCandles;
		int foundCnt = 0;
		CandleTimeSeries x = null, found = null;
		do {
			x = new CandleTimeSeries(vId.get(), vCandles.get());
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertSame(candles, found.getCandles());
	}

}
