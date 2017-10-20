package ru.prolib.aquila.core.data.tseries.filler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;

public class CandleSeriesCandleAggregatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TSeriesImpl<Candle> series;
	private CandleSeriesAggregator<Candle> aggregator;

	@Before
	public void setUp() throws Exception {
		series = new TSeriesImpl<>(TimeFrame.M5);
		aggregator = CandleSeriesCandleAggregator.getInstance();
	}
	
	@Test
	public void testAggregate_FirstCandle() throws Exception {
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(TimeFrame.M1)
				.withTime("2017-05-02T11:36:53Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle());
		
		Candle expected = new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle();
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_AppendToLastCandle() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(TimeFrame.M2)
				.withTime("2017-05-02T11:38:14Z")
				.withOpenPrice(89.75d)
				.withHighPrice(89.75d)
				.withLowPrice(87.43d)
				.withClosePrice(88.92d)
				.withVolume(500L)
				.buildCandle());
		
		Candle expected = new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.75d)
				.withLowPrice(86.12d)
				.withClosePrice(88.92d)
				.withVolume(1500L)
				.buildCandle();
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_PastData() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(TimeFrame.M2)
				.withTime("2017-05-02T11:31:55Z")
				.withOpenPrice(73.12d)
				.withHighPrice(74.05d)
				.withLowPrice(72.14d)
				.withClosePrice(73.52d)
				.withVolume(1500L)
				.buildCandle());
		
		assertEquals(2, series.getLength());
		assertEquals(new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:31:55Z")
				.withOpenPrice(73.12d)
				.withHighPrice(74.05d)
				.withLowPrice(72.14d)
				.withClosePrice(73.52d)
				.withVolume(1500L)
				.buildCandle(), series.get(0));
		assertEquals(new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle(), series.get(1));
	}

	@Test
	public void testAggregate_NewCandle() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(TimeFrame.M2)
				.withTime("2017-05-02T11:44:26Z")
				.withOpenPrice(73.12d)
				.withHighPrice(74.05d)
				.withLowPrice(72.14d)
				.withClosePrice(73.52d)
				.withVolume(1500L)
				.buildCandle());
		
		assertEquals(2, series.getLength());
		assertEquals(new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(86.12d)
				.withHighPrice(89.15d)
				.withLowPrice(86.12d)
				.withClosePrice(87.00d)
				.withVolume(1000L)
				.buildCandle(), series.get(0));
		assertEquals(new CandleBuilder()
				.withTimeFrame(TimeFrame.M5)
				.withTime("2017-05-02T11:40:55Z")
				.withOpenPrice(73.12d)
				.withHighPrice(74.05d)
				.withLowPrice(72.14d)
				.withClosePrice(73.52d)
				.withVolume(1500L)
				.buildCandle(), series.get(1));
	}

	@Test
	public void testEquals() {
		assertTrue(aggregator.equals(CandleSeriesCandleAggregator.getInstance()));
		assertFalse(aggregator.equals(null));
		assertFalse(aggregator.equals(this));
	}
}
