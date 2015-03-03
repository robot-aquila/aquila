package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-11<br>
 * $Id: CandleDataSeriesTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleDataSeriesTest {
	private EventSystem es;
	private Series<Candle> candles;
	private GCandlePart<Double> getter;
	private CandleDataSeries series;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		candles = new SeriesImpl<Candle>(es);
		getter = new GCandleOpen();
		series = new CandleDataSeries(es, "foo", candles, getter);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertEquals("foo", series.getId());
		assertSame(candles, series.getCandles());
		assertSame(getter, series.getGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		SeriesImpl<Candle> candles2 = new SeriesImpl<Candle>(es);
		candles2.add(new Candle(new Interval(new DateTime(),Minutes.minutes(1)),
				10d, 20l));
		Variant<String> vId = new Variant<String>()
			.add("bar")
			.add("foo");
		Variant<Series<Candle>> vCandles =  new Variant<Series<Candle>>(vId)
			.add(candles)
			.add(candles2);
		Variant<GCandlePart<Double>> vGtr =
				new Variant<GCandlePart<Double>>(vCandles)
			.add(getter)
			.add(new GCandleClose());
		Variant<?> iterator = vGtr;
		int foundCnt = 0;
		CandleDataSeries x = null, found = null;
		do {
			x = new CandleDataSeries(es, vId.get(), vCandles.get(), vGtr.get());
			if ( series.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertSame(candles, found.getCandles());
		assertSame(getter, found.getGetter());
	}

}
