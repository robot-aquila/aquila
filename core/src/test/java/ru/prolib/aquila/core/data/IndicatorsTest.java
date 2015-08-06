package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.joda.time.*;
import org.junit.*;

/**
 * 2013-03-04<br>
 * $Id: FMathImplTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class IndicatorsTest {
	
	/**
	 * Запись фикстуры для проверки расчета вещественного значения.
	 */
	public static class FR {
		private final Double value;
		private final Double expected;
		
		public FR(Double value, Double expected) {
			super();
			this.value = value;
			this.expected = expected;
		}
	}

	public final static FR
		/**
		 * Фикстура теста VectorVest DPO(3)
		 */
		fix_vv_dpo3[] = {
			// SPY (S & P 500 SPDR) daily from 1997-06-06
			new FR(86.3800d,  null),	// 1997-06-06
			new FR(86.8100d,  null),	// 1997-06-09
			new FR(87.0800d,  null),	// 1997-06-10
			new FR(87.2800d,  null),	// 1997-06-11
			new FR(88.9700d,  null),	// 1997-06-12
			new FR(89.7200d,  2.9633d),	// 1997-06-13
			new FR(89.7500d,  2.6933d),	// 1997-06-16
			new FR(89.6300d,  1.8533d),	// 1997-06-17
			new FR(89.3100d,  0.6533d),	// 1997-06-18
			new FR(90.2300d,  0.7500d),	// 1997-06-19
			new FR(89.5800d, -0.1200d),	// 1997-06-20
			new FR(87.4100d, -2.1533d),	// 1997-06-23
			new FR(89.6300d, -0.0933d),	// 1997-06-24
			new FR(89.0000d, -0.7067d),	// 1997-06-25
			new FR(88.5600d, -0.5133d),	// 1997-06-26
			new FR(88.9100d,  0.0367d),	// 1997-06-27
			new FR(88.3100d, -0.3700d),	// 1997-06-30
			new FR(89.3400d,  0.2767d),	// 1997-07-01
			new FR(90.8100d,  1.9867d),	// 1997-07-02
			new FR(92.0600d,  3.4667d),	// 1997-07-03
			new FR(91.1300d,  2.2767d),	// 1997-07-07
			new FR(92.0800d,  2.5933d),	// 1997-07-08
			new FR(91.0600d,  0.3233d),	// 1997-07-09
		},
		/**
		 * Фикстура теста VectorVest DPO(20)
		 */
		fix_vv_dpo20[] = {
			// SPY (S & P 500 SPDR) minutes from 2013-09-03 09:30:00
			new FR(165.3869d,  null),	// 2013-09-03 09:30:00
			new FR(165.4000d,  null),	// 2013-09-03 09:31:00
			new FR(165.4600d,  null),	// 2013-09-03 09:32:00
			new FR(165.5100d,  null),	// 2013-09-03 09:33:00
			new FR(165.4400d,  null),	// 2013-09-03 09:34:00
			new FR(165.5500d,  null),	// 2013-09-03 09:35:00
			new FR(165.4800d,  null),	// 2013-09-03 09:36:00
			new FR(165.5061d,  null),	// 2013-09-03 09:37:00
			new FR(165.4500d,  null),	// 2013-09-03 09:38:00
			new FR(165.4300d,  null),	// 2013-09-03 09:39:00
			new FR(165.4500d,  null),	// 2013-09-03 09:40:00
			new FR(165.3701d,  null),	// 2013-09-03 09:41:00
			new FR(165.3800d,  null),	// 2013-09-03 09:42:00
			new FR(165.3600d,  null),	// 2013-09-03 09:43:00
			new FR(165.3400d,  null),	// 2013-09-03 09:44:00
			new FR(165.3650d,  null),	// 2013-09-03 09:45:00
			new FR(165.3200d,  null),	// 2013-09-03 09:46:00
			new FR(165.3000d,  null),	// 2013-09-03 09:47:00
			new FR(165.3300d,  null),	// 2013-09-03 09:48:00
			new FR(165.3100d,  null),	// 2013-09-03 09:49:00
			new FR(165.3500d,  null),	// 2013-09-03 09:50:00
			new FR(165.2900d,  null),	// 2013-09-03 09:51:00
			new FR(165.3500d,  null),	// 2013-09-03 09:52:00
			new FR(165.3050d,  null),	// 2013-09-03 09:53:00
			new FR(165.2200d,  null),	// 2013-09-03 09:54:00
			new FR(165.3700d,  null),	// 2013-09-03 09:55:00
			new FR(165.3650d,  null),	// 2013-09-03 09:56:00
			new FR(165.3500d,  null),	// 2013-09-03 09:57:00
			new FR(165.1900d,  null),	// 2013-09-03 09:58:00
			new FR(165.2600d,  null),	// 2013-09-03 09:59:00
			new FR(165.1600d, -0.2469d),	// 2013-09-03 10:00:00
			new FR(165.0950d, -0.3101d),	// 2013-09-03 10:01:00
			new FR(165.1600d, -0.2396d),	// 2013-09-03 10:02:00
			new FR(165.2300d, -0.1641d),	// 2013-09-03 10:03:00
			new FR(165.2300d, -0.1538d),	// 2013-09-03 10:04:00
			new FR(165.1800d, -0.1928d),	// 2013-09-03 10:05:00
			new FR(165.2700d, -0.0938d),	// 2013-09-03 10:06:00
			new FR(165.2400d, -0.1181d),	// 2013-09-03 10:07:00
			new FR(165.2100d, -0.1403d),	// 2013-09-03 10:08:00
			new FR(165.2300d, -0.1073d),	// 2013-09-03 10:09:00
			new FR(165.3800d,  0.0512d),	// 2013-09-03 10:10:00
			new FR(165.3400d,  0.0257d),	// 2013-09-03 10:11:00
			new FR(165.4300d,  0.1295d),	// 2013-09-03 10:12:00
			new FR(165.3700d,  0.0805d),	// 2013-09-03 10:13:00
			new FR(165.3300d,  0.0470d),	// 2013-09-03 10:14:00
			new FR(165.3000d,  0.0225d),	// 2013-09-03 10:15:00
			new FR(165.1700d, -0.0983d),	// 2013-09-03 10:16:00
			new FR(165.1800d, -0.0858d),	// 2013-09-03 10:17:00
			new FR(165.1200d, -0.1428d),	// 2013-09-03 10:18:00
			new FR(165.1200d, -0.1368d),	// 2013-09-03 10:19:00
			new FR(165.0500d, -0.2028d),	// 2013-09-03 10:20:00
			new FR(165.0800d, -0.1743d),	// 2013-09-03 10:21:00
			new FR(165.0350d, -0.2218d),	// 2013-09-03 10:22:00
			new FR(164.9900d, -0.2708d),	// 2013-09-03 10:23:00
			new FR(165.0401d, -0.2239d),	// 2013-09-03 10:24:00
			new FR(165.1000d, -0.1695d),	// 2013-09-03 10:25:00
			new FR(165.1500d, -0.1160d),	// 2013-09-03 10:26:00
			new FR(165.0700d, -0.1863d),	// 2013-09-03 10:27:00
			new FR(165.0940d, -0.1538d),	// 2013-09-03 10:28:00
			new FR(165.1300d, -0.1143d),	// 2013-09-03 10:29:00
			new FR(165.0800d, -0.1573d),	// 2013-09-03 10:30:00
			new FR(165.1500d, -0.0818d),	// 2013-09-03 10:31:00
			new FR(165.2200d, -0.0110d),	// 2013-09-03 10:32:00
			new FR(165.1900d, -0.0348d),	// 2013-09-03 10:33:00
			new FR(165.1900d, -0.0228d),	// 2013-09-03 10:34:00
			new FR(165.1700d, -0.0333d),	// 2013-09-03 10:35:00
			new FR(165.1600d, -0.0393d),	// 2013-09-03 10:36:00
			new FR(165.2345d,  0.0412d),	// 2013-09-03 10:37:00
			new FR(165.2050d,  0.0202d),	// 2013-09-03 10:38:00
		},
		/**
		 * Фикстура теста Quik EMA(5)
		 */
		fix_qema5[] = {
			// RIU5, 2015-07-31, m15, close
			new FR(85990.000000, null), //09:15
			new FR(85190.000000, null), //10:00
			new FR(85290.000000, null), //10:15
			new FR(84980.000000, null), //10:30
			new FR(85260.000000, 85339.506173), //10:45
			new FR(85120.000000, 85266.337449),
			new FR(84730.000000, 85087.558299),
			new FR(84890.000000, 85021.705533),
			new FR(84900.000000, 84981.137022),
			new FR(85120.000000, 85027.424681),
			new FR(85150.000000, 85068.283121),
			new FR(84950.000000, 85028.855414),
			new FR(85010.000000, 85022.570276),
			new FR(85150.000000, 85065.046851),//13:00
			new FR(85150.000000, 85093.364567),//13:15
			new FR(84940.000000, 85042.243045),
			new FR(84900.000000, 84994.828696),
			new FR(84440.000000, 84809.885798),
			new FR(84200.000000, 84606.590532),
			new FR(84230.000000, 84481.060355),
			new FR(84180.000000, 84380.706903),
			new FR(84430.000000, 84397.137935),
			new FR(84230.000000, 84341.425290),
			new FR(84850.000000, 84510.950193),
			new FR(84740.000000, 84587.300129),
			new FR(85230.000000, 84801.533419),
			new FR(85770.000000, 85124.355613),
			new FR(85480.000000, 85242.903742),
			new FR(86130.000000, 85538.602495),
			new FR(85720.000000, 85599.068330),
			new FR(85840.000000, 85679.378886),
			new FR(85830.000000, 85729.585924),
			new FR(85690.000000, 85716.390616),//17:45
			new FR(85670.000000, 85700.927077),//18:00
			new FR(85850.000000, 85750.618052),//18:15
		};
	
	
	private Indicators indicators;
	private EditableSeries<Double> series;
	private EditableSeries<Candle> candles;
	
	@Before
	public void setUp() throws Exception {
		indicators = new Indicators();
		series = new SeriesImpl<Double>();
		candles = new SeriesImpl<Candle>();
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public void testAbs() throws Exception {
		assertEquals(123.45d, indicators.abs(123.45d), 0.01d);
		assertEquals(123.45d, indicators.abs(-123.45d), 0.01d);
		assertNull(indicators.abs(null));
	}
	
	@Test
	public void testMaxVA() throws Exception {
		assertEquals(180.24, indicators.max(67.4, null, 180.24, null, 159.12), 0.001);
		assertEquals(12.34, indicators.max(12.34, 12.34, null), 0.001);
		assertNull(indicators.max((Double) null));
	}
	
	@Test
	public void testMinVA() throws Exception {
		assertEquals(67.4, indicators.min(67.4, null, 180.24, null, 159.12), 0.001);
		assertEquals(12.34, indicators.min(12.34, 12.34, null), 0.001);
		assertNull(indicators.min((Double) null));
	}
	
	@Test
	public void testHasNulls() throws Exception {
		assertFalse(indicators.hasNulls(series, 200));
		
		assertFalse(indicators.hasNulls(series, 0, 0));
		assertFalse(indicators.hasNulls(series, 0));
		series.add(null);
		series.add(12.34d);
		series.add(11.62d);
		assertFalse(indicators.hasNulls(series, 2, 1));
		assertFalse(indicators.hasNulls(series, 1));
		assertFalse(indicators.hasNulls(series, 2, 2));
		assertFalse(indicators.hasNulls(series, 2));
		assertTrue(indicators.hasNulls(series, 2, 3));
		assertTrue(indicators.hasNulls(series, 3));
		assertFalse(indicators.hasNulls(series, -1, 1));
		assertTrue(indicators.hasNulls(series, -1, 2));
		
		assertTrue(indicators.hasNulls(series, 200));
	}

	@Test
	public void testSma() throws Exception {
		assertNull(indicators.sma(series, 5));
		assertNull(indicators.sma(series, 0, 5));
		
		series.add(null);
		assertNull(indicators.sma(series, 5));
		assertNull(indicators.sma(series, 0, 5));
		
		series.add(10.0d);
		series.add(20.0d);
		assertNull(indicators.sma(series, 5));
		
		series.add(30.0d);
		series.add(40.0d);
		assertNull(indicators.sma(series, 5));
		assertNull(indicators.sma(series, 1, 5));
		
		series.add(50.00d);
		assertEquals(30.00d, indicators.sma(series, 5), 0.01d);
		assertEquals(30.00d, indicators.sma(series, 5, 5), 0.01d);
		
		series.add(null);
		assertEquals(30.00d, indicators.sma(series, -1, 5), 0.01d);
		assertNull(indicators.sma(series, 5));
		assertNull(indicators.sma(series, 6, 5));
	}
	
	@Test
	public void testVvdpo3_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			series.add(fix_vv_dpo3[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			Double dpo = indicators.vvdpo(series, i, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVvdpo3_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			series.add(fr.value);
			Double dpo = indicators.vvdpo(series, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVvdpo20_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			series.add(fix_vv_dpo20[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			Double dpo = indicators.vvdpo(series, i, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVvdpo20_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			series.add(fr.value);
			Double dpo = indicators.vvdpo(series, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testTr() throws Exception {
		assertNull(indicators.tr(candles));
		assertNull(indicators.tr(candles, 0));
		
		candles.add(null);
		assertNull(indicators.tr(candles));
		assertNull(indicators.tr(candles, 0));

		DateTime time = new DateTime(2013, 10, 11, 11, 9, 43);
		
		// H-L
		candles.add(new Candle(TimeFrame.M5.getInterval(time),
				0, 48.70, 47.79, 48.16, 0L));
		assertEquals(0.91, indicators.tr(candles), 0.01d);
		assertEquals(0.91, indicators.tr(candles, 1), 0.01d);
		
		// |H-Cp|
		candles.add(new Candle(TimeFrame.M5.getInterval(time.plusMinutes(5)),
				0, 49.35, 48.86, 49.32, 0L));
		candles.add(new Candle(TimeFrame.M5.getInterval(time.plusMinutes(10)),
				0, 49.92, 49.50, 49.91, 0L));
		assertEquals(0.6, indicators.tr(candles), 0.001);
		assertEquals(0.6, indicators.tr(candles, 3), 0.001);
		
		// |L-Cp|
		candles.add(new Candle(TimeFrame.M5.getInterval(time.plusMinutes(15)),
				0, 50.19, 49.87, 50.13, 0L));
		candles.add(new Candle(TimeFrame.M5.getInterval(time.plusMinutes(20)),
				0, 50.12, 49.20, 49.53, 0L));
		assertEquals(0.93, indicators.tr(candles), 0.001);
		assertEquals(0.93, indicators.tr(candles, 5), 0.001);
	}
	
	@Test
	public void testMax23() throws Exception {
		int period = 3;
		Double fix[][] = {
				// value, max
				{ 19.29d, 19.29d },
				{ 15.44d, 19.29d },
				{ 11.86d, 19.29d },
				{ 21.15d, 21.15d },
				{ null,   21.15d },
				{ 16.12d, 21.15d },
				{ 13.21d, 16.12d },
				{ 11.92d, 16.12d },
				{ 18.54d, 18.54d },
				{ 17.76d, 18.54d },
				{ null,   18.54d },
				{ null,   17.76d },
				{ null,   null   },
				{  1.15d,  1.15d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
			Double expect = fix[i][1];
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, indicators.max(series, i, period));
				assertNull(msg, indicators.max(series, period));
			} else {
				assertEquals(msg, expect, indicators.max(series, i, period), 0.01d);
				assertEquals(msg, expect, indicators.max(series, period), 0.01d);
			}
		}
		// additional tests
		assertEquals(17.76d, indicators.max(series, -2, period), 0.01d);
		assertEquals(18.54d, indicators.max(series, -3, period), 0.01d);
		assertEquals(18.54d, indicators.max(series, -5, period), 0.01d);
		assertEquals(16.12d, indicators.max(series, -7, period), 0.01d);
	}
	
	@Test
	public void testMaxVA23() throws Exception {
		int period = 4;
		EditableSeries<Double> value2 = new SeriesImpl<Double>();
		Double fix[][] = {
				// value, value2, max
				{ 19.29d, null,   19.29d },
				{ 11.19d, 21.15d, 21.15d },
				{ null,   null,   21.15d },
				{ 23.74d, 23.70d, 23.74d },
				{ 13.17d,  2.20d, 23.74d },
				{ 23.17d, 16.20d, 23.74d },
				{ 18.25d, 16.21d, 23.74d },
				{ 15.12d,  6.18d, 23.17d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
			value2.add(fix[i][1]);
			String msg = "At #" + i;
			Double expect = fix[i][2];
			assertEquals(msg, expect, indicators.max(period, series, value2), 0.01d);
			assertEquals(msg, expect, indicators.max(i, period, value2, series),0.01d);
		}
	}
	
	@Test
	public void testMin23() throws Exception {
		int period = 3;
		Double fix[][] = {
				// value, min
				{ 19.29d, 19.29d },
				{ 15.44d, 15.44d },
				{ 11.86d, 11.86d },
				{ 21.15d, 11.86d },
				{ null,   11.86d },
				{ 16.12d, 16.12d },
				{ 13.21d, 13.21d },
				{ 11.92d, 11.92d },
				{ 18.54d, 11.92d },
				{ 17.76d, 11.92d },
				{ null,   17.76d },
				{ null,   17.76d },
				{ null,   null   },
				{  1.15d,  1.15d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
			Double expect = fix[i][1];
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, indicators.min(series, i, period));
				assertNull(msg, indicators.min(series, period));
			} else {
				assertEquals(msg, expect, indicators.min(series, i, period), 0.01d);
				assertEquals(msg, expect, indicators.min(series, period), 0.01d);
			}
		}
		// additional tests
		assertEquals(17.76d, indicators.min(series, -2, period), 0.01d);
		assertEquals(17.76d, indicators.min(series, -3, period), 0.01d);
		assertEquals(11.92d, indicators.min(series, -5, period), 0.01d);
		assertEquals(13.21d, indicators.min(series, -7, period), 0.01d);
	}
	
	@Test
	public void testMinVA23() throws Exception {
		int period = 4;
		EditableSeries<Double> value2 = new SeriesImpl<Double>();
		Double fix[][] = {
				// value, value2, min
				{ 19.29d, null,   19.29d },
				{ 11.19d, 21.15d, 11.19d },
				{ null,   null,   11.19d },
				{ 23.74d, 23.70d, 11.19d },
				{ 13.17d,  2.20d,  2.20d },
				{ 23.17d, 16.20d,  2.20d },
				{ 18.25d, 16.21d,  2.20d },
				{ 15.12d,  6.18d,  2.20d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
			value2.add(fix[i][1]);
			String msg = "At #" + i;
			Double expect = fix[i][2];
			assertEquals(msg, expect, indicators.min(period, series, value2), 0.01d);
			assertEquals(msg, expect, indicators.min(i, period, value2, series),0.01d);
		}
	}
	
	@Test
	public void testCrossUnderZero() throws Exception {
		Object fix[][] = {
			// value, cross?
			{ 19.29d, false },
			{ 20.15d, false },
			{ -1.12d, true  },
			{ -5.29d, false },
			{ 10.74d, false },
			{ null,   false },
			{  1.15d, false },
			{ null,   false },
			{ null,   false },
			{ -5.33d, false },
			{  5.33d, false },
			{ -5.33d, true  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add((Double) fix[i][0]);
			String msg = "At #" + i;
			Boolean expect = (Boolean) fix[i][1];
			assertEquals(msg, expect, indicators.crossUnderZero(series));
			assertEquals(msg, expect, indicators.crossUnderZero(series, i));
		}
	}

	@Test
	public void testCrossOverZero() throws Exception {
		Object fix[][] = {
				// value, cross?
				{ -9.29d, false },
				{ -0.15d, false },
				{  1.12d, true  },
				{  5.29d, false },
				{ 10.74d, false },
				{ null,   false },
				{  1.15d, false },
				{ null,   false },
				{ null,   false },
				{  5.33d, false },
				{ -5.33d, false },
				{  5.33d, true  },
			};
			for ( int i = 0; i < fix.length; i ++ ) {
				series.add((Double) fix[i][0]);
				String msg = "At #" + i;
				Boolean expect = (Boolean) fix[i][1];
				assertEquals(msg, expect, indicators.crossOverZero(series));
				assertEquals(msg, expect, indicators.crossOverZero(series, i));
			}

	}
	
	@Test
	public void testQema() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			series.add(fix_qema5[i].value);
		}
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_qema5[i];
			Double value = indicators.qema(series, i, 5);
			if ( fr.expected == null ) {
				assertNull(msg, value);
			} else {
				assertNotNull(msg, value);
				assertEquals(msg, fr.expected, value, 0.000001d);
			}
		}
	}
	
	@Test
	public void testQema_RevOrder() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			series.add(fix_qema5[i].value);
		}
		for ( int i = 0; i < fix_qema5.length - 1; i ++ ) {
			int index = i - fix_qema5.length + 1;
			String msg = "At #" + index;
			FR fr = fix_qema5[i];
			Double value = indicators.qema(series, index, 5);
			if ( fr.expected == null ) {
				assertNull(msg, value);
			} else {
				assertNotNull(msg, value);
				assertEquals(msg, fr.expected, value, 0.000001d);
			}
		}
	}
	
	@Test
	public void testQema_NullIfCannotObtainStartValue() throws Exception {
		series.add(40.27d);
		series.add(null);
		series.add(40.92d);
		series.add(44.33d);
		series.add(null);
		series.add(null);
		series.add(45.02d);
		series.add(48.13d);
		for ( int i = 0; i < series.getLength(); i ++ ) {
			String msg = "At #" + i;
			assertNull(msg, indicators.qema(series, i, 3));
		}
	}
	
	@Test
	public void testQema_WithNullsButOk() throws Exception {
		Double fixture[][] = {
			//  value, expected MA
			{  40.27d,          null },
			{    null,          null },
			{  40.92d,	        null }, //  40.9200
			{  44.33d,          null }, // (40.9200 * 2 + 2 * 44.33) / 4 = 42.625
			{  53.50d,          null }, // (42.6250 * 2 + 2 * 53.50) / 4 = 48.0625
			{  52.13d, 50.096250000d }, // (48.0625 * 2 + 2 * 52.13) / 4 = 50.09625
			{  45.02d, 47.558125000d }, // (50.09625 * 2 + 2 * 45.02) / 4 = 47.558125
			{    null, 47.558125000d },
			{    null, 47.558125000d },
			{  48.13d, 47.844062500d }, // (47.558125 * 2 + 2 * 48.13) / 4 = 47.8440625
			{  51.14d, 49.492031250d }, // (47.8440625 * 2 + 2 * 51.14) / 4 = 49.49203125
			{  52.18d, 50.836015625d }, // (49.49203125 * 2 + 2 * 52.18) / 4 = 50.836015625
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			series.add(fixture[i][0]);
		}
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			Double expected = fixture[i][1];
			Double actual = indicators.qema(series, i, 3);
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertNotNull(msg + " expected: " + expected + " but null", actual);
				assertEquals(msg, expected, actual, 0.0001d);
			}
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testQema_ThrowsIfPeriodTooLow() throws Exception {
		indicators.qema(series, 0, 1);
	}
	
	/**
	 * Фикстура теста Quik ATR(5)
	 */
	static Double fix_qatr5[][] = {
		// RIU5, 2015-08-06, h1
		// open, high,low,close, expected ATR
		{82960d,82960d,82960d,82960d, null},
		{82840d,83700d,82840d,83380d, null},
		{83390d,83490d,83110d,83310d, null},
		{83280d,83310d,83140d,83220d, null},
		{83200d,83200d,82610d,82660d, 404.000000d},
		{82640d,83090d,82480d,82880d, 445.200000d},
		{82890d,83140d,82830d,83040d, 418.160000d},
		{82970d,82990d,82550d,82720d, 432.528000d},
		{82730d,82950d,82660d,82790d, 404.022400d},
		{82760d,82860d,82610d,82700d, 373.217920d},
		{82690d,82920d,82620d,82770d, 358.574336d},
		{82780d,82850d,82640d,82670d, 328.859469d},
		{82660d,82740d,82530d,82700d, 305.087575d},
		{82680d,82830d,82590d,82710d, 292.070060d},
		{82720d,82780d,82610d,82700d, 267.656048d},
		{82690d,82760d,82080d,82190d, 350.124838d},
		{82190d,82250d,81680d,81860d, 394.099871d},
		{81830d,81970d,81630d,81770d, 383.279897d},
		{81790d,82120d,81760d,82090d, 378.623917d},
		{82060d,82260d,81830d,82050d, 388.899134d},
		{82020d,82190d,81770d,81810d, 395.119307d},
	};
	
	private void fillCandlesQatr5() throws Exception {
		Double fixture[][] = fix_qatr5;
		Interval interval = new Interval(0, 0);
		for ( int i = 0; i < fixture.length; i ++ ) {
			candles.add(new Candle(interval, fixture[i][0], fixture[i][1],
					fixture[i][2], fixture[i][3], 0l));
		}
	}
	
	@Test
	public void testQatr() throws Exception {
		fillCandlesQatr5();
		Double fixture[][] = fix_qatr5;
		Double expected = null, actual = null;
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			expected = fixture[i][4];
			actual = indicators.qatr(candles, i, 5);
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertNotNull(msg + " expected: " + expected + " but null", actual);
				assertEquals(msg, expected, actual, 0.000001d);
			}
		}
		assertEquals(expected, indicators.qatr(candles, 5), 0.000001d);
	}

}
