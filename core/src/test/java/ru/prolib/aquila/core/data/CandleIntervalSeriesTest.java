package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class CandleIntervalSeriesTest {
	private EventSystem es;
	private Series<Candle> candles;
	private CandleIntervalSeries series;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		candles = new SeriesImpl<Candle>(es);
		series = new CandleIntervalSeries(es, "foo", candles);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertEquals("foo", series.getId());
		assertSame(candles, series.getCandles());
		assertEquals(new GCandleInterval(), series.getGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(series.equals(series));
		assertFalse(series.equals(null));
		assertFalse(series.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Interval interval = new Interval(new DateTime(), Minutes.minutes(5));
		SeriesImpl<Candle> candles2 = new SeriesImpl<Candle>(es);
		candles2.add(new Candle(interval, 10d, 20l));
		Variant<String> vId = new Variant<String>()
			.add("bar")
			.add("foo");
		Variant<Series<Candle>> vCandles =  new Variant<Series<Candle>>(vId)
			.add(candles)
			.add(candles2);
		Variant<?> iterator = vCandles;
		int foundCnt = 0;
		CandleIntervalSeries x = null, found = null;
		do {
			x = new CandleIntervalSeries(es, vId.get(), vCandles.get());
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
