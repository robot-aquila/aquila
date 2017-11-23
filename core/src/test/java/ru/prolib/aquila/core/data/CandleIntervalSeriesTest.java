package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.*;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

public class CandleIntervalSeriesTest {
	private IMocksControl control;
	private Series<Candle> candlesMock;
	private SeriesImpl<Candle> candles;
	private Instant time1, time2, time3;
	private Interval int1, int2, int3;
	private Candle candle1, candle2, candle3;
	private CandleIntervalSeries series;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(Series.class);
		candles = new SeriesImpl<>("foo");
		time1 = Instant.parse("2013-10-07T11:00:00Z");
		time2 = time1.plusSeconds(5 * 60);
		time3 = time2.plusSeconds(5 * 60);
		int1 = ZTFrame.M5.getInterval(time1);
		int2 = ZTFrame.M5.getInterval(time2);
		int3 = ZTFrame.M5.getInterval(time3);
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
		series = new CandleIntervalSeries(candles);
	}
	
	@Test
	public void testGetId() {
		assertEquals("foo.INTERVAL", series.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		assertEquals(int3, series.get());
	}
	
	@Test
	public void testGet1() throws Exception {
		assertEquals(int1, series.get(0));
		assertEquals(int2, series.get(1));
		assertEquals(int3, series.get(2));
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
		
		series = new CandleIntervalSeries(candlesMock);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		series = new CandleIntervalSeries(candlesMock);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		series = new CandleIntervalSeries(candlesMock);
		series.unlock();
		
		control.verify();
	}

}
