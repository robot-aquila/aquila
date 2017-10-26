package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Duration;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleCloseTSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private Interval interval1, interval2, interval3;
	private TSeriesImpl<Candle> candles;
	private TSeries<Candle> candlesMock;
	private CandleCloseTSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(TSeries.class);
		Duration duration = Duration.ofMinutes(5);
		interval1 = Interval.of(T("2017-09-01T06:20:00Z"), duration);
		interval2 = Interval.of(T("2017-09-01T06:25:00Z"), duration);
		interval3 = Interval.of(T("2017-09-01T06:30:00Z"), duration);
		candles = new TSeriesImpl<>(ZTFrame.M5);
		series = new CandleCloseTSeries("CLOSE", candles);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("CLOSE", series.getId());
		assertSame(candles, series.getCandleSeries());
	}
	
	@Test
	public void testCtor1() {
		series = new CandleCloseTSeries(candles);
		assertEquals(TSeries.DEFAULT_ID, series.getId());
		assertSame(candles, series.getCandleSeries());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertNull(series.get());
		
		candles.set(interval1.getStart(), new Candle(interval1, 100d, 110d, 95d, 98d, 1000L));
		
		assertEquals(98d, series.get(), 0.01d);
		
		candles.set(interval2.getStart(), new Candle(interval2, 98d, 99d, 92d, 95d, 2000L));

		assertEquals(95d, series.get(), 0.01d);
	}
	
	@Test
	public void testGet1_I() throws Exception {
		candles.set(interval1.getStart(), new Candle(interval1, 100d, 110d,  95d,  98d, 1000L));
		candles.set(interval2.getStart(), new Candle(interval2,  98d,  99d,  92d,  95d, 2000L));
		candles.set(interval3.getStart(), new Candle(interval3,  95d, 100d,  91d, 100d, 3000L));
		
		assertEquals( 98d, series.get(0), 0.01d);
		assertEquals( 95d, series.get(1), 0.01d);
		assertEquals(100d, series.get(2), 0.01d);
		
		assertEquals( 95d, series.get(-1), 0.01d);
		assertEquals( 98d, series.get(-2), 0.01d);
	}
	
	@Test
	public void testGetLength() {
		assertEquals(0, series.getLength());
		
		candles.set(interval1.getStart(), new Candle(interval1, 100d, 110d,  95d,  98d, 1000L));

		assertEquals(1, series.getLength());

		candles.set(interval2.getStart(), new Candle(interval2,  98d,  99d,  92d,  95d, 2000L));

		assertEquals(2, series.getLength());
		
		candles.set(interval3.getStart(), new Candle(interval3,  95d, 100d,  91d, 100d, 3000L));

		assertEquals(3, series.getLength());
	}

	@Test
	public void testGetLID() {
		assertSame(candles.getLID(), series.getLID());
	}
	
	@Test
	public void testLock() {
		series = new CandleCloseTSeries(candlesMock);
		candlesMock.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		series = new CandleCloseTSeries(candlesMock);
		candlesMock.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGet1_T() {
		candles.set(interval1.getStart(), new Candle(interval1, 100d, 110d,  95d,  98d, 1000L));
		candles.set(interval2.getStart(), new Candle(interval2,  98d,  99d,  92d,  95d, 2000L));
		candles.set(interval3.getStart(), new Candle(interval3,  95d, 100d,  91d, 100d, 3000L));

		assertNull(series.get(T("2017-09-01T06:17:32Z")));
		assertEquals( 98d, series.get(T("2017-09-01T06:20:00.000Z")), 0.01);
		assertEquals( 98d, series.get(T("2017-09-01T06:23:15.000Z")), 0.01);
		assertEquals( 98d, series.get(T("2017-09-01T06:24:59.999Z")), 0.01);
		assertEquals( 95d, series.get(T("2017-09-01T06:25:00.000Z")), 0.01);
		assertEquals( 95d, series.get(T("2017-09-01T06:28:15.123Z")), 0.01);
		assertEquals( 95d, series.get(T("2017-09-01T06:29:59.999Z")), 0.01);
		assertEquals(100d, series.get(T("2017-09-01T06:30:00.000Z")), 0.01);
		assertEquals(100d, series.get(T("2017-09-01T06:32:19.034Z")), 0.01);
		assertEquals(100d, series.get(T("2017-09-01T06:34:59.999Z")), 0.01);
		assertNull(series.get(T("2017-09-01T06:39:00.000Z")));
	}

	@Test
	public void testGetTimeFrame() {
		assertEquals(ZTFrame.M5, series.getTimeFrame());
	}
	
	@Test
	public void testToIndex() {
		series = new CandleCloseTSeries(candlesMock);
		expect(candlesMock.toIndex(T("2017-09-01T07:13:00Z"))).andReturn(450);
		control.replay();
		
		assertEquals(450, series.toIndex(T("2017-09-01T07:13:00Z")));
		
		control.verify();
	}

}
