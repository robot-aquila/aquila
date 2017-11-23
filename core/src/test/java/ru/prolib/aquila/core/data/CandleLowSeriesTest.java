package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

public class CandleLowSeriesTest {
	private IMocksControl control;
	private Series<Candle> candlesMock;
	private SeriesImpl<Candle> candles;
	private Instant time1, time2, time3;
	private Candle candle1, candle2, candle3;
	private CandleLowSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(Series.class);
		candles = new SeriesImpl<>("foo");
		time1 = Instant.parse("2013-10-07T11:00:00Z");
		time2 = time1.plusSeconds(5 * 60);
		time3 = time2.plusSeconds(5 * 60);
		candle1 = new CandleBuilder()
				.withTime(time1)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("144440"))
				.withHighPrice(CDecimalBD.of("144440"))
				.withLowPrice(CDecimalBD.of("143130"))
				.withClosePrice(CDecimalBD.of("143210"))
				.withVolume(CDecimalBD.of(39621L))
				.buildCandle();
		candle2 = new CandleBuilder()
				.withTime(time2)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("143230"))
				.withHighPrice(CDecimalBD.of("143390"))
				.withLowPrice(CDecimalBD.of("143100"))
				.withClosePrice(CDecimalBD.of("143290"))
				.withVolume(CDecimalBD.of(12279L))
				.buildCandle();
		candle3 = new CandleBuilder()
				.withTime(time3)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.of("143280"))
				.withHighPrice(CDecimalBD.of("143320"))
				.withLowPrice(CDecimalBD.of("143110"))
				.withClosePrice(CDecimalBD.of("143190"))
				.withVolume(CDecimalBD.of(11990L))
				.buildCandle();
		candles.add(candle1);
		candles.add(candle2);
		candles.add(candle3);
		series = new CandleLowSeries(candles);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo.LOW", series.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(CDecimalBD.of("143110"), series.get());
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(CDecimalBD.of("143130"), series.get(0));
		assertEquals(CDecimalBD.of("143100"), series.get(1));
		assertEquals(CDecimalBD.of("143110"), series.get(2));
	}

	@Test
	public void testGetLength() {
		assertEquals(3, series.getLength());
	}
	
	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(candlesMock.getLID()).andReturn(lidStub);
		control.replay();
		
		series = new CandleLowSeries(candlesMock);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		series = new CandleLowSeries(candlesMock);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		series = new CandleLowSeries(candlesMock);
		series.unlock();
		
		control.verify();
	}

}
