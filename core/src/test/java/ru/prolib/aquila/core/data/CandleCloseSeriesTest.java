package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

public class CandleCloseSeriesTest {
	private IMocksControl control;
	private Series<Candle> candlesMock;
	private SeriesImpl<Candle> candles;
	private Instant time1, time2, time3;
	private Candle candle1, candle2, candle3;
	private CandleCloseSeries series;

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
				.withOpenPrice(144440L)
				.withHighPrice(144440L)
				.withLowPrice(143130L)
				.withClosePrice(143210L)
				.withVolume(39621L)
				.buildCandle();
		candle2 = new CandleBuilder()
				.withTime(time2)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(143230L)
				.withHighPrice(143390L)
				.withLowPrice(143100L)
				.withClosePrice(143290L)
				.withVolume(12279L)
				.buildCandle();
		candle3 = new CandleBuilder()
				.withTime(time3)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(143280L)
				.withHighPrice(143320L)
				.withLowPrice(143110L)
				.withClosePrice(143190L)
				.withVolume(11990L)
				.buildCandle();
		candles.add(candle1);
		candles.add(candle2);
		candles.add(candle3);
		series = new CandleCloseSeries(candles);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo.CLOSE", series.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(CDecimalBD.of(143190L), series.get());
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(CDecimalBD.of(143210L), series.get(0));
		assertEquals(CDecimalBD.of(143290L), series.get(1));
		assertEquals(CDecimalBD.of(143190L), series.get(2));
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
		
		series = new CandleCloseSeries(candlesMock);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		series = new CandleCloseSeries(candlesMock);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		series = new CandleCloseSeries(candlesMock);
		series.unlock();
		
		control.verify();
	}	

}
