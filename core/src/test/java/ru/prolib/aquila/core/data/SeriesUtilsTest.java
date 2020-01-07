package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;

public class SeriesUtilsTest {
	private SeriesUtils service;

	@Before
	public void setUp() throws Exception {
		service = new SeriesUtils();
	}

	@Test
	public void testCopy_Candles_ItToTSeries() throws Exception {
		ZTFrame tf = ZTFrame.M5;
		List<Candle> data = new ArrayList<>();
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T00:55:00Z", "100", "115", "100", "110", 5L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:00:00Z", "110", "112",  "98",  "99", 1L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:05:00Z",  "99", "105",  "99", "107", 3L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:10:00Z", "108", "119", "104", "115", 7L));
		TSeriesImpl<Candle> target = new TSeriesImpl<>(tf);
		
		try ( CloseableIteratorStub<Candle> it = new CloseableIteratorStub<Candle>(data) ) {
			service.copy(it, target);
		};
		
		assertEquals(data.size(), target.getLength());
		assertEquals(data.get(0), target.get(0));
		assertEquals(data.get(1), target.get(1));
		assertEquals(data.get(2), target.get(2));
		assertEquals(data.get(3), target.get(3));
	}
	
	@Test
	public void testCopy_Candles_TSeriesToTSeries() throws Exception {
		ZTFrame tf = ZTFrame.M5;
		List<Candle> data = new ArrayList<>();
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T00:55:00Z", "100", "115", "100", "110", 5L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:00:00Z", "110", "112",  "98",  "99", 1L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:05:00Z",  "99", "105",  "99", "107", 3L));
		data.add(new CandleBuilder(tf).buildCandle("2020-01-07T01:10:00Z", "108", "119", "104", "115", 7L));
		TSeriesImpl<Candle> source = new TSeriesImpl<>(tf), target = new TSeriesImpl<>(tf);
		for ( Candle x : data ) {
			source.set(x.getStartTime(), x);
		}
		source.set(Instant.parse("2020-01-07T00:50:00Z"), null);
		source.set(Instant.parse("2020-01-07T01:15:00Z"), null);
		
		service.copy(source,  target);
		
		assertEquals(data.size(), target.getLength());
		assertEquals(data.get(0), target.get(0));
		assertEquals(data.get(1), target.get(1));
		assertEquals(data.get(2), target.get(2));
		assertEquals(data.get(3), target.get(3));
		assertNotEquals(source.getLength(), target.getLength());
	}

}
