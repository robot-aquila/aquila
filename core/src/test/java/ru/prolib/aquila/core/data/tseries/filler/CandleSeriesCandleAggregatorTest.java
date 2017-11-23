package ru.prolib.aquila.core.data.tseries.filler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleSeriesCandleAggregatorTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TSeriesImpl<Candle> series;
	private CandleSeriesAggregator<Candle> aggregator;

	@Before
	public void setUp() throws Exception {
		series = new TSeriesImpl<>(ZTFrame.M5);
		aggregator = CandleSeriesCandleAggregator.getInstance();
	}
	
	@Test
	public void testAggregate_FirstCandle() throws Exception {
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-05-02T11:36:53Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle());
		
		Candle expected = new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle();
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_AppendToLastCandle() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(ZTFrame.M2)
				.withTime("2017-05-02T11:38:14Z")
				.withOpenPrice(CDecimalBD.of("89.75"))
				.withHighPrice(CDecimalBD.of("89.75"))
				.withLowPrice(CDecimalBD.of("87.43"))
				.withClosePrice(CDecimalBD.of("88.92"))
				.withVolume(CDecimalBD.of(500L))
				.buildCandle());
		
		Candle expected = new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.75"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("88.92"))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle();
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate_PastData() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(ZTFrame.M2)
				.withTime("2017-05-02T11:31:55Z")
				.withOpenPrice(CDecimalBD.of("73.12"))
				.withHighPrice(CDecimalBD.of("74.05"))
				.withLowPrice(CDecimalBD.of("72.14"))
				.withClosePrice(CDecimalBD.of("73.52"))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle());
		
		assertEquals(2, series.getLength());
		assertEquals(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:31:55Z")
				.withOpenPrice(CDecimalBD.of("73.12"))
				.withHighPrice(CDecimalBD.of("74.05"))
				.withLowPrice(CDecimalBD.of("72.14"))
				.withClosePrice(CDecimalBD.of("73.52"))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle(), series.get(0));
		assertEquals(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle(), series.get(1));
	}

	@Test
	public void testAggregate_NewCandle() throws Exception {
		series.set(T("2017-05-02T11:35:00Z"), new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle());
		aggregator.aggregate(series, new CandleBuilder()
				.withTimeFrame(ZTFrame.M2)
				.withTime("2017-05-02T11:44:26Z")
				.withOpenPrice(CDecimalBD.of("73.12"))
				.withHighPrice(CDecimalBD.of("74.05"))
				.withLowPrice(CDecimalBD.of("72.14"))
				.withClosePrice(CDecimalBD.of("73.52"))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle());
		
		assertEquals(2, series.getLength());
		assertEquals(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:35:00Z")
				.withOpenPrice(CDecimalBD.of("86.12"))
				.withHighPrice(CDecimalBD.of("89.15"))
				.withLowPrice(CDecimalBD.of("86.12"))
				.withClosePrice(CDecimalBD.of("87.00"))
				.withVolume(CDecimalBD.of(1000L))
				.buildCandle(), series.get(0));
		assertEquals(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-05-02T11:40:55Z")
				.withOpenPrice(CDecimalBD.of("73.12"))
				.withHighPrice(CDecimalBD.of("74.05"))
				.withLowPrice(CDecimalBD.of("72.14"))
				.withClosePrice(CDecimalBD.of("73.52"))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle(), series.get(1));
	}

	@Test
	public void testEquals() {
		assertTrue(aggregator.equals(CandleSeriesCandleAggregator.getInstance()));
		assertFalse(aggregator.equals(null));
		assertFalse(aggregator.equals(this));
	}
}
