package ru.prolib.aquila.core.data.tseries;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

import java.time.Instant;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

public class CandleVolumeTSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private Candle candle1, candle2, candle3;
	private TSeriesImpl<Candle> candles;
	private TSeries<Candle> candlesMock;
	private CandleVolumeTSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(TSeries.class);
		candle1 = new CandleBuilder()
				.withTime("2017-09-01T06:20:00.000Z")
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(0L)
				.withHighPrice(0L)
				.withLowPrice(0L)
				.withClosePrice(0L)
				.withVolume(1000L)
				.buildCandle();
		candle2 = new CandleBuilder()
				.withTime("2017-09-01T06:25:00.000Z")
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(0L)
				.withHighPrice(0L)
				.withLowPrice(0L)
				.withClosePrice(0L)
				.withVolume(2000L)
				.buildCandle();
		candle3 = new CandleBuilder()
				.withTime("2017-09-01T06:30:00.000Z")
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(0L)
				.withHighPrice(0L)
				.withLowPrice(0L)
				.withClosePrice(0L)
				.withVolume(3000L)
				.buildCandle();		
		candles = new TSeriesImpl<>(ZTFrame.M5);
		series = new CandleVolumeTSeries("VOLUME", candles);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("VOLUME", series.getId());
		assertSame(candles, series.getCandleSeries());
	}
	
	@Test
	public void testCtor1() {
		series = new CandleVolumeTSeries(candles);
		assertEquals(TSeries.DEFAULT_ID, series.getId());
		assertSame(candles, series.getCandleSeries());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertNull(series.get());
		
		candles.set(candle1.getStartTime(), candle1);
		
		assertEquals(CDecimalBD.of(1000L), series.get());
		
		candles.set(candle2.getStartTime(), candle2);

		assertEquals(CDecimalBD.of(2000L), series.get());
	}
	
	@Test
	public void testGet1_I() throws Exception {
		candles.set(candle1.getStartTime(), candle1);
		candles.set(candle2.getStartTime(), candle2);
		candles.set(candle3.getStartTime(), candle3);
		
		assertEquals(CDecimalBD.of(1000L), series.get(0));
		assertEquals(CDecimalBD.of(2000L), series.get(1));
		assertEquals(CDecimalBD.of(3000L), series.get(2));
		
		assertEquals(CDecimalBD.of(2000L), series.get(-1));
		assertEquals(CDecimalBD.of(1000L), series.get(-2));
	}
	
	@Test
	public void testGetLength() {
		assertEquals(0, series.getLength());
		
		candles.set(candle1.getStartTime(), candle1);

		assertEquals(1, series.getLength());

		candles.set(candle2.getStartTime(), candle2);

		assertEquals(2, series.getLength());
		
		candles.set(candle3.getStartTime(), candle3);

		assertEquals(3, series.getLength());
	}

	@Test
	public void testGetLID() {
		assertSame(candles.getLID(), series.getLID());
	}
	
	@Test
	public void testLock() {
		series = new CandleVolumeTSeries(candlesMock);
		candlesMock.lock();
		control.replay();
		
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		series = new CandleVolumeTSeries(candlesMock);
		candlesMock.unlock();
		control.replay();
		
		series.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGet1_T() {
		candles.set(candle1.getStartTime(), candle1);
		candles.set(candle2.getStartTime(), candle2);
		candles.set(candle3.getStartTime(), candle3);

		assertNull(series.get(T("2017-09-01T06:17:32Z")));
		assertEquals(CDecimalBD.of(1000L), series.get(T("2017-09-01T06:20:00.000Z")));
		assertEquals(CDecimalBD.of(1000L), series.get(T("2017-09-01T06:23:15.000Z")));
		assertEquals(CDecimalBD.of(1000L), series.get(T("2017-09-01T06:24:59.999Z")));
		assertEquals(CDecimalBD.of(2000L), series.get(T("2017-09-01T06:25:00.000Z")));
		assertEquals(CDecimalBD.of(2000L), series.get(T("2017-09-01T06:28:15.123Z")));
		assertEquals(CDecimalBD.of(2000L), series.get(T("2017-09-01T06:29:59.999Z")));
		assertEquals(CDecimalBD.of(3000L), series.get(T("2017-09-01T06:30:00.000Z")));
		assertEquals(CDecimalBD.of(3000L), series.get(T("2017-09-01T06:32:19.034Z")));
		assertEquals(CDecimalBD.of(3000L), series.get(T("2017-09-01T06:34:59.999Z")));
		assertNull(series.get(T("2017-09-01T06:39:00.000Z")));
	}

	@Test
	public void testGetTimeFrame() {
		assertEquals(ZTFrame.M5, series.getTimeFrame());
	}
	
	@Test
	public void testToIndex() {
		series = new CandleVolumeTSeries(candlesMock);
		expect(candlesMock.toIndex(T("2017-09-01T07:13:00Z"))).andReturn(450);
		control.replay();
		
		assertEquals(450, series.toIndex(T("2017-09-01T07:13:00Z")));
		
		control.verify();
	}

}
