package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import ru.prolib.aquila.core.data.*;

/**
 * 2013-03-04<br>
 * $Id: FMathImplTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class FMathImplTest {
	
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
		};
	private final Date time = new Date();
	private FMath math;
	private EditableDataSeries value;
	private EditableSeries<Candle> candles;
	
	@Before
	public void setUp() throws Exception {
		math = new FMathImpl();
		value = new DataSeriesImpl();
		candles = new SeriesImpl<Candle>();
	}
	
	@Test
	public void testAbs() throws Exception {
		assertEquals(123.45d, math.abs(123.45d), 0.01d);
		assertEquals(123.45d, math.abs(-123.45d), 0.01d);
		assertNull(math.abs(null));
	}
	
	@Test
	public void testMaxVA() throws Exception {
		assertEquals(180.24, math.max(67.4, null, 180.24, null, 159.12), 0.001);
		assertEquals(12.34, math.max(12.34, 12.34, null), 0.001);
		assertNull(math.max((Double) null));
	}
	
	@Test
	public void testMinVA() throws Exception {
		assertEquals(67.4, math.min(67.4, null, 180.24, null, 159.12), 0.001);
		assertEquals(12.34, math.min(12.34, 12.34, null), 0.001);
		assertNull(math.min((Double) null));
	}
	
	@Test
	public void testHasNulls() throws Exception {
		assertFalse(math.hasNulls(value, 200));
		
		assertFalse(math.hasNulls(value, 0, 0));
		assertFalse(math.hasNulls(value, 0));
		value.add(null);
		value.add(12.34d);
		value.add(11.62d);
		assertFalse(math.hasNulls(value, 2, 1));
		assertFalse(math.hasNulls(value, 1));
		assertFalse(math.hasNulls(value, 2, 2));
		assertFalse(math.hasNulls(value, 2));
		assertTrue(math.hasNulls(value, 2, 3));
		assertTrue(math.hasNulls(value, 3));
		assertFalse(math.hasNulls(value, -1, 1));
		assertTrue(math.hasNulls(value, -1, 2));
		
		assertTrue(math.hasNulls(value, 200));
	}

	@Test
	public void testSma() throws Exception {
		assertNull(math.sma(value, 5));
		assertNull(math.sma(value, 0, 5));
		
		value.add(null);
		assertNull(math.sma(value, 5));
		assertNull(math.sma(value, 0, 5));
		
		value.add(10.0d);
		value.add(20.0d);
		assertNull(math.sma(value, 5));
		
		value.add(30.0d);
		value.add(40.0d);
		assertNull(math.sma(value, 5));
		assertNull(math.sma(value, 1, 5));
		
		value.add(50.00d);
		assertEquals(30.00d, math.sma(value, 5), 0.01d);
		assertEquals(30.00d, math.sma(value, 5, 5), 0.01d);
		
		value.add(null);
		assertEquals(30.00d, math.sma(value, -1, 5), 0.01d);
		assertNull(math.sma(value, 5));
		assertNull(math.sma(value, 6, 5));
	}
	
	@Test
	public void testVv_dpo3_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			value.add(fix_vv_dpo3[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			Double dpo = math.vv_dpo(value, i, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVv_dpo3_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			value.add(fr.value);
			Double dpo = math.vv_dpo(value, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVv_dpo20_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			value.add(fix_vv_dpo20[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			Double dpo = math.vv_dpo(value, i, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testVv_dpo20_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			value.add(fr.value);
			Double dpo = math.vv_dpo(value, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo, 0.0001d);
			}
		}
	}
	
	@Test
	public void testTr() throws Exception {
		assertNull(math.tr(candles));
		assertNull(math.tr(candles, 0));
		
		candles.add(null);
		assertNull(math.tr(candles));
		assertNull(math.tr(candles, 0));
		
		// H-L
		candles.add(new Candle(time, 0, 48.70, 47.79, 48.16, 0L));
		assertEquals(0.91, math.tr(candles), 0.01d);
		assertEquals(0.91, math.tr(candles, 1), 0.01d);
		
		// |H-Cp|
		candles.add(new Candle(time, 0, 49.35, 48.86, 49.32, 0L));
		candles.add(new Candle(time, 0, 49.92, 49.50, 49.91, 0L));
		assertEquals(0.6, math.tr(candles), 0.001);
		assertEquals(0.6, math.tr(candles, 3), 0.001);
		
		// |L-Cp|
		candles.add(new Candle(time, 0, 50.19, 49.87, 50.13, 0L));
		candles.add(new Candle(time, 0, 50.12, 49.20, 49.53, 0L));
		assertEquals(0.93, math.tr(candles), 0.001);
		assertEquals(0.93, math.tr(candles, 5), 0.001);
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
			value.add(fix[i][0]);
			Double expect = fix[i][1];
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, math.max(value, i, period));
				assertNull(msg, math.max(value, period));
			} else {
				assertEquals(msg, expect, math.max(value, i, period), 0.01d);
				assertEquals(msg, expect, math.max(value, period), 0.01d);
			}
		}
		// additional tests
		assertEquals(17.76d, math.max(value, -2, period), 0.01d);
		assertEquals(18.54d, math.max(value, -3, period), 0.01d);
		assertEquals(18.54d, math.max(value, -5, period), 0.01d);
		assertEquals(16.12d, math.max(value, -7, period), 0.01d);
	}
	
	@Test
	public void testMaxVA23() throws Exception {
		int period = 4;
		EditableDataSeries value2 = new DataSeriesImpl();
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
			value.add(fix[i][0]);
			value2.add(fix[i][1]);
			String msg = "At #" + i;
			Double expect = fix[i][2];
			assertEquals(msg, expect, math.max(period, value, value2), 0.01d);
			assertEquals(msg, expect, math.max(i, period, value2, value),0.01d);
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
			value.add(fix[i][0]);
			Double expect = fix[i][1];
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, math.min(value, i, period));
				assertNull(msg, math.min(value, period));
			} else {
				assertEquals(msg, expect, math.min(value, i, period), 0.01d);
				assertEquals(msg, expect, math.min(value, period), 0.01d);
			}
		}
		// additional tests
		assertEquals(17.76d, math.min(value, -2, period), 0.01d);
		assertEquals(17.76d, math.min(value, -3, period), 0.01d);
		assertEquals(11.92d, math.min(value, -5, period), 0.01d);
		assertEquals(13.21d, math.min(value, -7, period), 0.01d);
	}
	
	@Test
	public void testMinVA23() throws Exception {
		int period = 4;
		EditableDataSeries value2 = new DataSeriesImpl();
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
			value.add(fix[i][0]);
			value2.add(fix[i][1]);
			String msg = "At #" + i;
			Double expect = fix[i][2];
			assertEquals(msg, expect, math.min(period, value, value2), 0.01d);
			assertEquals(msg, expect, math.min(i, period, value2, value),0.01d);
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
			value.add((Double) fix[i][0]);
			String msg = "At #" + i;
			Boolean expect = (Boolean) fix[i][1];
			assertEquals(msg, expect, math.crossUnderZero(value));
			assertEquals(msg, expect, math.crossUnderZero(value, i));
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
				value.add((Double) fix[i][0]);
				String msg = "At #" + i;
				Boolean expect = (Boolean) fix[i][1];
				assertEquals(msg, expect, math.crossOverZero(value));
				assertEquals(msg, expect, math.crossOverZero(value, i));
			}

	}

}
