package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

public class CandleVolumeSeriesTest {
	private EditableCandleSeries candles;
	private Instant time1, time2, time3;
	private Interval int1, int2, int3;
	private Candle candle1, candle2, candle3;
	private CandleVolumeSeries series;

	@Before
	public void setUp() throws Exception {
		candles = new CandleSeriesImpl(TimeFrame.M5, "foo");
		time1 = Instant.parse("2013-10-07T11:00:00Z");
		time2 = time1.plusSeconds(5 * 60);
		time3 = time2.plusSeconds(5 * 60);
		int1 = TimeFrame.M5.getInterval(time1);
		int2 = TimeFrame.M5.getInterval(time2);
		int3 = TimeFrame.M5.getInterval(time3);
		candle1 = new Candle(int1, 144440d, 144440d, 143130d, 143210d, 39621L);
		candle2 = new Candle(int2, 143230d, 143390d, 143100d, 143290d, 12279L);
		candle3 = new Candle(int3, 143280d, 143320d, 143110d, 143190d, 11990L);
		candles.add(candle1);
		candles.add(candle2);
		candles.add(candle3);
		series = new CandleVolumeSeries(candles);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo.VOLUME", series.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(11990d, series.get(), 0.1d);
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(39621d, series.get(0), 0.1d);
		assertEquals(12279d, series.get(1), 0.1d);
		assertEquals(11990d, series.get(2), 0.1d);
	}

	@Test
	public void testGetLength() {
		assertEquals(3, series.getLength());
	}

}
