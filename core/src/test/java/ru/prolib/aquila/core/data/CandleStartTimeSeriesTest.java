package ru.prolib.aquila.core.data;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.concurrency.LID;

import java.time.Instant;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * Created by TiM on 04.05.2017.
 */
public class CandleStartTimeSeriesTest {
	private IMocksControl control;
	private Series<Candle> candlesMock;
    private SeriesImpl<Candle> candles;
    private Instant time1, time2, time3;
    private Interval int1, int2, int3;
    private Candle candle1, candle2, candle3;
    private CandleStartTimeSeries series;

    @SuppressWarnings("unchecked")
	@Before
    public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(Series.class);
        candles = new SeriesImpl<Candle>("CANDLES");
        time1 = Instant.parse("2017-05-04T11:00:00Z");
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
        series = new CandleStartTimeSeries(candles);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("CANDLES.START_TIME", series.getId());
    }

    @Test
    public void testGet1() throws Exception {
        assertEquals(time3, series.get());
    }

    @Test
    public void testGet2() throws Exception {
        assertEquals(time1, series.get(0));
        assertEquals(time2, series.get(1));
        assertEquals(time3, series.get(2));
    }

    @Test
    public void testGetLength() throws Exception {
        assertEquals(3, series.getLength());
    }

	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(candlesMock.getLID()).andReturn(lidStub);
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		assertSame(lidStub, series.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		series.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		series = new CandleStartTimeSeries(candlesMock);
		series.unlock();
		
		control.verify();
	}	

}