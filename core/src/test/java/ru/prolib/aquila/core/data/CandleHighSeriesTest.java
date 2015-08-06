package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

public class CandleHighSeriesTest {
	private EditableCandleSeries candles;
	private DateTime time1, time2, time3;
	private Interval int1, int2, int3;
	private Candle candle1, candle2, candle3;
	private CandleHighSeries series;

	@Before
	public void setUp() throws Exception {
		candles = new CandleSeriesImpl(TimeFrame.M5, "foo");
		time1 = new DateTime(2013, 10, 7, 11, 0, 0);
		time2 = time1.plusMinutes(5);
		time3 = time2.plusMinutes(5);
		int1 = TimeFrame.M5.getInterval(time1);
		int2 = TimeFrame.M5.getInterval(time2);
		int3 = TimeFrame.M5.getInterval(time3);
		candle1 = new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L);
		candle2 = new Candle(int2, 143230d, 143390d, 143100d, 143290d, 12279L);
		candle3 = new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L);
		candles.add(candle1);
		candles.add(candle2);
		candles.add(candle3);
		series = new CandleHighSeries(candles);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo.HIGH", series.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(143320d, series.get(), 0.1d);
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(144440d, series.get(0), 0.1d);
		assertEquals(143390d, series.get(1), 0.1d);
		assertEquals(143320d, series.get(2), 0.1d);
	}

	@Test
	public void testGetLength() {
		assertEquals(3, series.getLength());
	}
	
}

