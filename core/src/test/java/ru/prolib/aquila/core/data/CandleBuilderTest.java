package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

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
		assertSame(builder, builder.withOpenPrice(CDecimalBD.of("215.02")));
		assertSame(builder, builder.withHighPrice(CDecimalBD.of("219.86")));
		assertSame(builder, builder.withLowPrice(CDecimalBD.of("211.73")));
		assertSame(builder, builder.withClosePrice(CDecimalBD.of("216.95")));
		assertSame(builder, builder.withVolume(CDecimalBD.of(1500L)));
		
		Candle actual = builder.buildCandle();
		
		Interval expectedInterval = Interval.of(T("2017-10-19T13:00:00Z"), T("2017-10-19T13:15:00Z"));
		Candle expected = new Candle(expectedInterval,
				CDecimalBD.of("215.02"),
				CDecimalBD.of("219.86"),
				CDecimalBD.of("211.73"),
				CDecimalBD.of("216.95"),
				CDecimalBD.of(1500L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildCandle_AlternativeSetters_String() {
		assertSame(builder, builder.withTime("2017-10-19T13:00:00Z"));
		assertSame(builder, builder.withOpenPrice("215.02"));
		assertSame(builder, builder.withHighPrice("219.86"));
		assertSame(builder, builder.withLowPrice("211.73"));
		assertSame(builder, builder.withClosePrice("216.95"));
		assertSame(builder, builder.withVolume("1500"));
		Candle actual = builder.withTimeFrame(ZTFrame.M15).buildCandle();
		
		Interval expectedInterval = Interval.of(T("2017-10-19T13:00:00Z"), T("2017-10-19T13:15:00Z"));
		Candle expected = new Candle(expectedInterval,
				CDecimalBD.of("215.02"),
				CDecimalBD.of("219.86"),
				CDecimalBD.of("211.73"),
				CDecimalBD.of("216.95"),
				CDecimalBD.of(1500L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildCandle_AlternativeSetters_Long() {
		assertSame(builder, builder.withOpenPrice(215L));
		assertSame(builder, builder.withHighPrice(219L));
		assertSame(builder, builder.withLowPrice(211L));
		assertSame(builder, builder.withClosePrice(216L));
		assertSame(builder, builder.withVolume(1500L));
		Candle actual = builder.withTime("2017-11-17T18:45:00Z")
				.withTimeFrame(ZTFrame.M15)
				.buildCandle();
		
		Candle expected = new CandleBuilder()
				.withTime("2017-11-17T18:45:00Z")
				.withTimeFrame(ZTFrame.M15)
				.withOpenPrice(CDecimalBD.of(215L))
				.withHighPrice(CDecimalBD.of(219L))
				.withLowPrice(CDecimalBD.of(211L))
				.withClosePrice(CDecimalBD.of(216L))
				.withVolume(CDecimalBD.of(1500L))
				.buildCandle();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildCandle6_SLLLLL() {
		Candle actual = builder.withTimeFrame(ZTFrame.M10)
				.buildCandle("2017-11-22T20:44:00Z", 100L, 105L, 98L, 103L, 5000L);
		
		Candle expected = new CandleBuilder()
				.withTime("2017-11-22T20:44:00Z")
				.withTimeFrame(ZTFrame.M10)
				.withOpenPrice(100L)
				.withHighPrice(105L)
				.withLowPrice(98L)
				.withClosePrice(103L)
				.withVolume(5000L)
				.buildCandle();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBuildCandle6_SSSSSL() {
		Candle actual = builder.withTimeFrame(ZTFrame.M1)
				.buildCandle("2017-11-23T04:22:00Z", "10.01", "12.56", "10.00", "11.96", 1500L);
		
		Candle expected = new CandleBuilder()
				.withTime("2017-11-23T04:22:00Z")
				.withTimeFrame(ZTFrame.M1)
				.withOpenPrice("10.01")
				.withHighPrice("12.56")
				.withLowPrice("10.00")
				.withClosePrice("11.96")
				.withVolume(1500L)
				.buildCandle();
		assertEquals(expected, actual);
	}
	
}
