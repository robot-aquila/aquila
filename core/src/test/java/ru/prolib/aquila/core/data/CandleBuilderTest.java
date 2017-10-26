package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

public class CandleBuilderTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CandleBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new CandleBuilder();
	}
	
	@Test
	public void testBuildCandle() {
		assertSame(builder, builder.withTimeFrame(ZTFrame.M15));
		assertSame(builder, builder.withTime(T("2017-10-19T13:00:00Z")));
		assertSame(builder, builder.withOpenPrice(215.02d));
		assertSame(builder, builder.withHighPrice(219.86d));
		assertSame(builder, builder.withLowPrice(211.73d));
		assertSame(builder, builder.withClosePrice(216.95d));
		assertSame(builder, builder.withVolume(1500L));
		
		Candle actual = builder.buildCandle();
		
		Interval expectedInterval = Interval.of(T("2017-10-19T13:00:00Z"), T("2017-10-19T13:15:00Z"));
		Candle expected = new Candle(expectedInterval, 215.02, 219.86d, 211.73d, 216.95d, 1500L);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildCandle_WithTimeString() {
		assertSame(builder, builder.withTime("2017-10-19T13:00:00Z"));
		builder.withTimeFrame(ZTFrame.M15)
			.withOpenPrice(215.02d)
			.withHighPrice(219.86d)
			.withLowPrice(211.73d)
			.withClosePrice(216.95d)
			.withVolume(1500L);

		Candle actual = builder.buildCandle();
		
		Interval expectedInterval = Interval.of(T("2017-10-19T13:00:00Z"), T("2017-10-19T13:15:00Z"));
		Candle expected = new Candle(expectedInterval, 215.02, 219.86d, 211.73d, 216.95d, 1500L);
		assertEquals(expected, actual);
	}
	
}
