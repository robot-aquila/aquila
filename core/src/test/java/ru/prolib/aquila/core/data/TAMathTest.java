package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

/**
 * 2013-03-04<br>
 * $Id: FMathImplTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TAMathTest {
	
	/**
	 * Запись фикстуры для проверки расчета вещественного значения.
	 */
	public static class FR {
		private final CDecimal value;
		private final CDecimal expected;
		
		public FR(String value, String expected) {
			super();
			if ( value == null ) {
				this.value = null;
			} else {
				this.value = CDecimalBD.of(value);
			}
			if ( expected == null ) {
				this.expected = null;
			} else {
				this.expected = CDecimalBD.of(expected);
			}
		}
	}

	public final static FR
		/**
		 * Фикстура теста VectorVest DPO(3)
		 */
		fix_vv_dpo3[] = {
			// SPY (S & P 500 SPDR) daily from 1997-06-06
			new FR("86.3800",  null),	// 1997-06-06
			new FR("86.8100",  null),	// 1997-06-09
			new FR("87.0800",  null),	// 1997-06-10
			new FR("87.2800",  null),	// 1997-06-11
			new FR("88.9700",  null),	// 1997-06-12
			new FR("89.7200",  "2.9633"),	// 1997-06-13
			new FR("89.7500",  "2.6933"),	// 1997-06-16
			new FR("89.6300",  "1.8533"),	// 1997-06-17
			new FR("89.3100",  "0.6533"),	// 1997-06-18
			new FR("90.2300",  "0.7500"),	// 1997-06-19
			new FR("89.5800", "-0.1200"),	// 1997-06-20
			new FR("87.4100", "-2.1533"),	// 1997-06-23
			new FR("89.6300", "-0.0933"),	// 1997-06-24
			new FR("89.0000", "-0.7067"),	// 1997-06-25
			new FR("88.5600", "-0.5133"),	// 1997-06-26
			new FR("88.9100",  "0.0367"),	// 1997-06-27
			new FR("88.3100", "-0.3700"),	// 1997-06-30
			new FR("89.3400",  "0.2767"),	// 1997-07-01
			new FR("90.8100",  "1.9867"),	// 1997-07-02
			new FR("92.0600",  "3.4667"),	// 1997-07-03
			new FR("91.1300",  "2.2767"),	// 1997-07-07
			new FR("92.0800",  "2.5933"),	// 1997-07-08
			new FR("91.0600",  "0.3233"),	// 1997-07-09
		},
		/**
		 * Фикстура теста VectorVest DPO(20)
		 */
		fix_vv_dpo20[] = {
			// SPY (S & P 500 SPDR) minutes from 2013-09-03 09:30:00
			new FR("165.3869",  null),	// 2013-09-03 09:30:00
			new FR("165.4000",  null),	// 2013-09-03 09:31:00
			new FR("165.4600",  null),	// 2013-09-03 09:32:00
			new FR("165.5100",  null),	// 2013-09-03 09:33:00
			new FR("165.4400",  null),	// 2013-09-03 09:34:00
			new FR("165.5500",  null),	// 2013-09-03 09:35:00
			new FR("165.4800",  null),	// 2013-09-03 09:36:00
			new FR("165.5061",  null),	// 2013-09-03 09:37:00
			new FR("165.4500",  null),	// 2013-09-03 09:38:00
			new FR("165.4300",  null),	// 2013-09-03 09:39:00
			new FR("165.4500",  null),	// 2013-09-03 09:40:00
			new FR("165.3701",  null),	// 2013-09-03 09:41:00
			new FR("165.3800",  null),	// 2013-09-03 09:42:00
			new FR("165.3600",  null),	// 2013-09-03 09:43:00
			new FR("165.3400",  null),	// 2013-09-03 09:44:00
			new FR("165.3650",  null),	// 2013-09-03 09:45:00
			new FR("165.3200",  null),	// 2013-09-03 09:46:00
			new FR("165.3000",  null),	// 2013-09-03 09:47:00
			new FR("165.3300",  null),	// 2013-09-03 09:48:00
			new FR("165.3100",  null),	// 2013-09-03 09:49:00
			new FR("165.3500",  null),	// 2013-09-03 09:50:00
			new FR("165.2900",  null),	// 2013-09-03 09:51:00
			new FR("165.3500",  null),	// 2013-09-03 09:52:00
			new FR("165.3050",  null),	// 2013-09-03 09:53:00
			new FR("165.2200",  null),	// 2013-09-03 09:54:00
			new FR("165.3700",  null),	// 2013-09-03 09:55:00
			new FR("165.3650",  null),	// 2013-09-03 09:56:00
			new FR("165.3500",  null),	// 2013-09-03 09:57:00
			new FR("165.1900",  null),	// 2013-09-03 09:58:00
			new FR("165.2600",  null),	// 2013-09-03 09:59:00
			new FR("165.1600", "-0.2469"),	// 2013-09-03 10:00:00
			new FR("165.0950", "-0.3101"),	// 2013-09-03 10:01:00
			new FR("165.1600", "-0.2396"),	// 2013-09-03 10:02:00
			new FR("165.2300", "-0.1641"),	// 2013-09-03 10:03:00
			new FR("165.2300", "-0.1538"),	// 2013-09-03 10:04:00
			new FR("165.1800", "-0.1928"),	// 2013-09-03 10:05:00
			new FR("165.2700", "-0.0938"),	// 2013-09-03 10:06:00
			new FR("165.2400", "-0.1181"),	// 2013-09-03 10:07:00
			new FR("165.2100", "-0.1403"),	// 2013-09-03 10:08:00
			new FR("165.2300", "-0.1073"),	// 2013-09-03 10:09:00
			new FR("165.3800",  "0.0512"),	// 2013-09-03 10:10:00
			new FR("165.3400",  "0.0257"),	// 2013-09-03 10:11:00
			new FR("165.4300",  "0.1295"),	// 2013-09-03 10:12:00
			new FR("165.3700",  "0.0805"),	// 2013-09-03 10:13:00
			new FR("165.3300",  "0.0470"),	// 2013-09-03 10:14:00
			new FR("165.3000",  "0.0225"),	// 2013-09-03 10:15:00
			new FR("165.1700", "-0.0983"),	// 2013-09-03 10:16:00
			new FR("165.1800", "-0.0858"),	// 2013-09-03 10:17:00
			new FR("165.1200", "-0.1428"),	// 2013-09-03 10:18:00
			new FR("165.1200", "-0.1368"),	// 2013-09-03 10:19:00
			new FR("165.0500", "-0.2028"),	// 2013-09-03 10:20:00
			new FR("165.0800", "-0.1743"),	// 2013-09-03 10:21:00
			new FR("165.0350", "-0.2218"),	// 2013-09-03 10:22:00
			new FR("164.9900", "-0.2708"),	// 2013-09-03 10:23:00
			new FR("165.0401", "-0.2239"),	// 2013-09-03 10:24:00
			new FR("165.1000", "-0.1695"),	// 2013-09-03 10:25:00
			new FR("165.1500", "-0.1160"),	// 2013-09-03 10:26:00
			new FR("165.0700", "-0.1863"),	// 2013-09-03 10:27:00
			new FR("165.0940", "-0.1538"),	// 2013-09-03 10:28:00
			new FR("165.1300", "-0.1143"),	// 2013-09-03 10:29:00
			new FR("165.0800", "-0.1573"),	// 2013-09-03 10:30:00
			new FR("165.1500", "-0.0818"),	// 2013-09-03 10:31:00
			new FR("165.2200", "-0.0110"),	// 2013-09-03 10:32:00
			new FR("165.1900", "-0.0348"),	// 2013-09-03 10:33:00
			new FR("165.1900", "-0.0228"),	// 2013-09-03 10:34:00
			new FR("165.1700", "-0.0333"),	// 2013-09-03 10:35:00
			new FR("165.1600", "-0.0393"),	// 2013-09-03 10:36:00
			new FR("165.2345",  "0.0412"),	// 2013-09-03 10:37:00
			new FR("165.2050",  "0.0202"),	// 2013-09-03 10:38:00
		},
		/**
		 * Фикстура теста Quik EMA(5)
		 */
		fix_qema5[] = {
			// RIU5, 2015-07-31, m15, close
			new FR("85990.000000", null), //09:15
			new FR("85190.000000", null), //10:00
			new FR("85290.000000", null), //10:15
			new FR("84980.000000", null), //10:30
			new FR("85260.000000", "85339.506173"), //10:45
			new FR("85120.000000", "85266.337449"),
			new FR("84730.000000", "85087.558299"),
			new FR("84890.000000", "85021.705533"),
			new FR("84900.000000", "84981.137022"),
			new FR("85120.000000", "85027.424681"),
			new FR("85150.000000", "85068.283121"),
			new FR("84950.000000", "85028.855414"),
			new FR("85010.000000", "85022.570276"),
			new FR("85150.000000", "85065.046851"),//13:00
			new FR("85150.000000", "85093.364567"),//13:15
			new FR("84940.000000", "85042.243045"),
			new FR("84900.000000", "84994.828697"), // x!
			new FR("84440.000000", "84809.885798"),
			new FR("84200.000000", "84606.590532"),
			new FR("84230.000000", "84481.060355"),
			new FR("84180.000000", "84380.706903"),
			new FR("84430.000000", "84397.137935"),
			new FR("84230.000000", "84341.425290"),
			new FR("84850.000000", "84510.950193"),
			new FR("84740.000000", "84587.300129"),
			new FR("85230.000000", "84801.533419"),
			new FR("85770.000000", "85124.355613"),
			new FR("85480.000000", "85242.903742"),
			new FR("86130.000000", "85538.602495"),
			new FR("85720.000000", "85599.068330"),
			new FR("85840.000000", "85679.378887"), // x!
			new FR("85830.000000", "85729.585925"), // x!
			new FR("85690.000000", "85716.390617"),//17:45, x! + 0.000001
			new FR("85670.000000", "85700.927078"),//18:00, x! + 0.000001
			new FR("85850.000000", "85750.618052"),//18:15
		};
	
	
	private TAMath service;
	private EditableSeries<CDecimal> series1, series2;
	private EditableSeries<Candle> candles;
	
	@Before
	public void setUp() throws Exception {
		service = TAMath.getInstance();
		series1 = new SeriesImpl<>();
		series2 = new SeriesImpl<>();
		candles = new SeriesImpl<Candle>();
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public void testAbs() throws Exception {
		CDecimal expected = CDecimalBD.of("123.45");
		assertEquals(expected, service.abs(CDecimalBD.of("123.45")));
		assertEquals(expected, service.abs(CDecimalBD.of("-123.45")));
		assertNull(service.abs(null));
	}
	
	@Test
	public void testMaxVA() throws Exception {
		CDecimal expected = CDecimalBD.of("180.24");
		CDecimal actual = service.max(CDecimalBD.of("67.4"),
				null,
				CDecimalBD.of("180.24"),
				null,
				CDecimalBD.of("159.12"));
		assertEquals(expected, actual);
		
		expected = CDecimalBD.of("12.34");
		actual = service.max(CDecimalBD.of("12.34"), CDecimalBD.of("12.34"), null);
		assertEquals(expected, actual);
		
		assertNull(service.max((CDecimal) null));
	}
	
	@Test
	public void testMinVA() throws Exception {
		CDecimal expected = CDecimalBD.of("67.4");
		CDecimal actual = service.min(
				CDecimalBD.of("67.4"),
				null,
				CDecimalBD.of("180.24"),
				null,
				CDecimalBD.of("159.12"));
		assertEquals(expected, actual);
		
		expected = CDecimalBD.of("12.34");
		actual = service.min(CDecimalBD.of("12.34"), CDecimalBD.of("12.34"), null);
		assertEquals(expected, actual);
		
		assertNull(service.min((CDecimal) null));
	}
	
	@Test
	public void testHasNulls() throws Exception {
		assertFalse(service.hasNulls(series1, 200));
		
		assertFalse(service.hasNulls(series1, 0, 0));
		assertFalse(service.hasNulls(series1, 0));
		series1.add(null);
		series1.add(CDecimalBD.of("12.34"));
		series1.add(CDecimalBD.of("11.62"));
		assertFalse(service.hasNulls(series1, 2, 1));
		assertFalse(service.hasNulls(series1, 1));
		assertFalse(service.hasNulls(series1, 2, 2));
		assertFalse(service.hasNulls(series1, 2));
		assertTrue(service.hasNulls(series1, 2, 3));
		assertTrue(service.hasNulls(series1, 3));
		assertFalse(service.hasNulls(series1, -1, 1));
		assertTrue(service.hasNulls(series1, -1, 2));
		
		assertTrue(service.hasNulls(series1, 200));
	}

	@Test
	public void testSma() throws Exception {
		assertNull(service.sma(series1, 5));
		assertNull(service.sma(series1, 0, 5));
		
		series1.add(null);
		assertNull(service.sma(series1, 5));
		assertNull(service.sma(series1, 0, 5));
		
		series1.add(CDecimalBD.of("10.00"));
		series1.add(CDecimalBD.of("20.00"));
		assertNull(service.sma(series1, 5));
		
		series1.add(CDecimalBD.of("30.00"));
		series1.add(CDecimalBD.of("40.00"));
		assertNull(service.sma(series1, 5));
		assertNull(service.sma(series1, 1, 5));
		
		series1.add(CDecimalBD.of("50.00"));
		assertEquals(CDecimalBD.of("30.00"), service.sma(series1, 5));
		assertEquals(CDecimalBD.of("30.00"), service.sma(series1, 5, 5));
		
		series1.add(null);
		assertEquals(CDecimalBD.of("30.00"), service.sma(series1, -1, 5));
		assertNull(service.sma(series1, 5));
		assertNull(service.sma(series1, 6, 5));
	}
	
	@Test
	public void testVvdpo3_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			series1.add(fix_vv_dpo3[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			CDecimal dpo = service.vvdpo(series1, i, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo);
			}
		}
	}
	
	@Test
	public void testVvdpo3_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo3.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo3[i];
			series1.add(fr.value);
			CDecimal dpo = service.vvdpo(series1, 3);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo);
			}
		}
	}
	
	@Test
	public void testVvdpo20_3args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			series1.add(fix_vv_dpo20[i].value);
		}
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			CDecimal dpo = service.vvdpo(series1, i, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo);
			}
		}
	}
	
	@Test
	public void testVvdpo20_2args() throws Exception {
		for ( int i = 0; i < fix_vv_dpo20.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_vv_dpo20[i];
			series1.add(fr.value);
			CDecimal dpo = service.vvdpo(series1, 20);
			if ( fr.expected == null ) {
				assertNull(msg, dpo);
			} else {
				assertEquals(msg, fr.expected, dpo);
			}
		}
	}
	
	@Test
	public void testTr() throws Exception {
		assertNull(service.tr(candles));
		assertNull(service.tr(candles, 0));
		
		candles.add(null);
		assertNull(service.tr(candles));
		assertNull(service.tr(candles, 0));

		Instant time = Instant.parse("2013-10-11T11:09:43Z");
		
		// H-L
		candles.add(new CandleBuilder()
				.withTime(time)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.ZERO)
				.withHighPrice(CDecimalBD.of("48.70"))
				.withLowPrice(CDecimalBD.of("47.79"))
				.withClosePrice(CDecimalBD.of("48.16"))
				.withVolume(CDecimalBD.ZERO)
				.buildCandle());
		assertEquals(CDecimalBD.of("0.91"), service.tr(candles));
		assertEquals(CDecimalBD.of("0.91"), service.tr(candles, 1));
		
		// |H-Cp|
		candles.add(new CandleBuilder()
				.withTime(time.plusSeconds(5 * 60))
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.ZERO)
				.withHighPrice(CDecimalBD.of("49.35"))
				.withLowPrice(CDecimalBD.of("48.86"))
				.withClosePrice(CDecimalBD.of("49.32"))
				.withVolume(CDecimalBD.ZERO)
				.buildCandle());
		candles.add(new CandleBuilder()
				.withTime(time.plusSeconds(10 * 60))
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.ZERO)
				.withHighPrice(CDecimalBD.of("49.92"))
				.withLowPrice(CDecimalBD.of("49.50"))
				.withClosePrice(CDecimalBD.of("49.91"))
				.withVolume(CDecimalBD.ZERO)
				.buildCandle());
		assertEquals(CDecimalBD.of("0.60"), service.tr(candles));
		assertEquals(CDecimalBD.of("0.60"), service.tr(candles, 3));
		
		// |L-Cp|
		candles.add(new CandleBuilder()
				.withTime(time.plusSeconds(15 * 60))
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.ZERO)
				.withHighPrice(CDecimalBD.of("50.19"))
				.withLowPrice(CDecimalBD.of("49.87"))
				.withClosePrice(CDecimalBD.of("50.13"))
				.withVolume(CDecimalBD.ZERO)
				.buildCandle());
		candles.add(new CandleBuilder()
				.withTime(time.plusSeconds(20 * 60))
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(CDecimalBD.ZERO)
				.withHighPrice(CDecimalBD.of("50.12"))
				.withLowPrice(CDecimalBD.of("49.20"))
				.withClosePrice(CDecimalBD.of("49.53"))
				.withVolume(CDecimalBD.ZERO)
				.buildCandle());
		assertEquals(CDecimalBD.of("0.93"), service.tr(candles));
		assertEquals(CDecimalBD.of("0.93"), service.tr(candles, 5));
	}
	
	@Test
	public void testMax23() throws Exception {
		int period = 3;
		FR fix[] = {
				// value, max
				new FR("19.29", "19.29"),
				new FR("15.44", "19.29"),
				new FR("11.86", "19.29"),
				new FR("21.15", "21.15"),
				new FR(null, 	"21.15"),
				new FR("16.12", "21.15"),
				new FR("13.21", "16.12"),
				new FR("11.92", "16.12"),
				new FR("18.54", "18.54"),
				new FR("17.76", "18.54"),
				new FR(null, 	"18.54"),
				new FR(null, 	"17.76"),
				new FR(null, 	null),
				new FR("1.15",  "1.15"),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series1.add(fix[i].value);
			CDecimal expect = fix[i].expected;
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, service.max(series1, i, period));
				assertNull(msg, service.max(series1, period));
			} else {
				assertEquals(msg, expect, service.max(series1, i, period));
				assertEquals(msg, expect, service.max(series1, period));
			}
		}
		// additional tests
		assertEquals(CDecimalBD.of("17.76"), service.max(series1, -2, period));
		assertEquals(CDecimalBD.of("18.54"), service.max(series1, -3, period));
		assertEquals(CDecimalBD.of("18.54"), service.max(series1, -5, period));
		assertEquals(CDecimalBD.of("16.12"), service.max(series1, -7, period));
	}
	
	@Test
	public void testMaxVA23() throws Exception {
		int period = 4;
		EditableSeries<CDecimal> value2 = new SeriesImpl<>();
		CDecimal fix[][] = {
				// value, value2, max
				{ CDecimalBD.of("19.29"), null,   				  CDecimalBD.of("19.29") },
				{ CDecimalBD.of("11.19"), CDecimalBD.of("21.15"), CDecimalBD.of("21.15") },
				{ null,   				  null,   				  CDecimalBD.of("21.15") },
				{ CDecimalBD.of("23.74"), CDecimalBD.of("23.70"), CDecimalBD.of("23.74") },
				{ CDecimalBD.of("13.17"), CDecimalBD.of( "2.20"), CDecimalBD.of("23.74") },
				{ CDecimalBD.of("23.17"), CDecimalBD.of("16.20"), CDecimalBD.of("23.74") },
				{ CDecimalBD.of("18.25"), CDecimalBD.of("16.21"), CDecimalBD.of("23.74") },
				{ CDecimalBD.of("15.12"), CDecimalBD.of( "6.18"), CDecimalBD.of("23.17") },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series1.add(fix[i][0]);
			value2.add(fix[i][1]);
			String msg = "At #" + i;
			CDecimal expect = fix[i][2];
			assertEquals(msg, expect, service.max(period, series1, value2));
			assertEquals(msg, expect, service.max(i, period, value2, series1));
		}
	}
	
	@Test
	public void testMaxL3_SII() throws Exception {
		int period = 3;
		Long fix[][] = {
				// value, expected max
				{  19L,   19L },
				{  15L,   19L },
				{  11L,   19L },
				{  21L,   21L },
				{ null,   21L },
				{  16L,   21L },
				{  13L,   16L },
				{  11L,   16L },
				{  18L,   18L },
				{  17L,   18L },
				{ null,   18L },
				{ null,   17L },
				{ null,  null },
				{   1L,    1L },
		};
		SeriesImpl<Long> series = new SeriesImpl<>();
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
		}
		
		for ( int i = 0; i < fix.length; i ++ ) {
			Long expected = fix[i][1];
			String msg = "At #" + i;
			if ( expected == null ) {
				assertNull(msg, service.maxL(series, i, period));
			} else {
				assertEquals(msg, expected, service.maxL(series, i, period));
			}
		}
		
		// Negative index test
		assertNull(service.maxL(series, -1, period));
		assertEquals((Long) 17L, service.maxL(series, -2, period));
		assertEquals((Long) 18L, service.maxL(series, -3, period));
		assertEquals((Long) 18L, service.maxL(series, -4, period));
		assertEquals((Long) 18L, service.maxL(series, -5, period));
		assertEquals((Long) 16L, service.maxL(series, -6, period));
	}
	
	@Test
	public void testMaxL2_SI() throws Exception {
		int period = 3;
		Long fix[][] = {
				// value, expected max
				{  19L,   19L },
				{  15L,   19L },
				{  11L,   19L },
				{  21L,   21L },
				{ null,   21L },
				{  16L,   21L },
				{  13L,   16L },
				{  11L,   16L },
				{  18L,   18L },
				{  17L,   18L },
				{ null,   18L },
				{ null,   17L },
				{ null,  null },
				{   1L,    1L },
		};
		SeriesImpl<Long> series = new SeriesImpl<>();
		for ( int i = 0; i < fix.length; i ++ ) {
			series.add(fix[i][0]);
			Long expected = fix[i][1];
			String msg = "At #" + i;
			if ( expected == null ) {
				assertNull(msg, service.maxL(series, period));
			} else {
				assertEquals(msg, expected, service.maxL(series, period));
			}
		}
	}
	
	@Test
	public void testMin23() throws Exception {
		int period = 3;
		FR fix[] = {
				// value, min
				new FR("19.29", "19.29"),
				new FR("15.44", "15.44"),
				new FR("11.86", "11.86"),
				new FR("21.15", "11.86"),
				new FR(null,    "11.86"),
				new FR("16.12", "16.12"),
				new FR("13.21", "13.21"),
				new FR("11.92", "11.92"),
				new FR("18.54", "11.92"),
				new FR("17.76", "11.92"),
				new FR(null,    "17.76"),
				new FR(null,    "17.76"),
				new FR(null,    null),
				new FR("1.15",  "1.15"),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series1.add(fix[i].value);
			CDecimal expect = fix[i].expected;
			String msg = "At #" + i;
			if ( expect == null ) {
				assertNull(msg, service.min(series1, i, period));
				assertNull(msg, service.min(series1, period));
			} else {
				assertEquals(msg, expect, service.min(series1, i, period));
				assertEquals(msg, expect, service.min(series1, period));
			}
		}
		// additional tests
		assertEquals(CDecimalBD.of("17.76"), service.min(series1, -2, period));
		assertEquals(CDecimalBD.of("17.76"), service.min(series1, -3, period));
		assertEquals(CDecimalBD.of("11.92"), service.min(series1, -5, period));
		assertEquals(CDecimalBD.of("13.21"), service.min(series1, -7, period));
	}
	
	@Test
	public void testMinVA23() throws Exception {
		int period = 4;
		EditableSeries<CDecimal> value2 = new SeriesImpl<>();
		String fix[][] = {
				// value, value2, min
				{ "19.29", null,    "19.29" },
				{ "11.19", "21.15", "11.19" },
				{ null,    null,    "11.19" },
				{ "23.74", "23.70", "11.19" },
				{ "13.17",  "2.20",  "2.20" },
				{ "23.17", "16.20",  "2.20" },
				{ "18.25", "16.21",  "2.20" },
				{ "15.12",  "6.18",  "2.20" },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			if ( fix[i][0] == null ) {
				series1.add(null);
			} else {
				series1.add(CDecimalBD.of(fix[i][0]));
			}
			if ( fix[i][1] == null ) {
				value2.add(null);
			} else {
				value2.add(CDecimalBD.of(fix[i][1]));
			}
			String msg = "At #" + i;
			CDecimal expect = CDecimalBD.of(fix[i][2]);
			assertEquals(msg, expect, service.min(period, series1, value2));
			assertEquals(msg, expect, service.min(i, period, value2, series1));
		}
	}
	
	@Test
	public void testCrossUnderZero() throws Exception {
		Object fix[][] = {
			// value, cross?
			{ CDecimalBD.of("19.29"), false },
			{ CDecimalBD.of("20.15"), false },
			{ CDecimalBD.of("-1.12"), true  },
			{ CDecimalBD.of("-5.29"), false },
			{ CDecimalBD.of("10.74"), false },
			{ null,   false },
			{  CDecimalBD.of("1.15"), false },
			{ null,   false },
			{ null,   false },
			{ CDecimalBD.of("-5.33"), false },
			{  CDecimalBD.of("5.33"), false },
			{ CDecimalBD.of("-5.33"), true  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			series1.add((CDecimal) fix[i][0]);
			String msg = "At #" + i;
			Boolean expect = (Boolean) fix[i][1];
			assertEquals(msg, expect, service.crossUnderZero(series1));
			assertEquals(msg, expect, service.crossUnderZero(series1, i));
		}
	}

	@Test
	public void testCrossOverZero() throws Exception {
		Object fix[][] = {
				// value, cross?
				{ CDecimalBD.of("-9.29"), false },
				{ CDecimalBD.of("-0.15"), false },
				{  CDecimalBD.of("1.12"), true  },
				{  CDecimalBD.of("5.29"), false },
				{ CDecimalBD.of("10.74"), false },
				{ null,   false },
				{  CDecimalBD.of("1.15"), false },
				{ null,   false },
				{ null,   false },
				{  CDecimalBD.of("5.33"), false },
				{ CDecimalBD.of("-5.33"), false },
				{  CDecimalBD.of("5.33"), true  },
			};
			for ( int i = 0; i < fix.length; i ++ ) {
				series1.add((CDecimal) fix[i][0]);
				String msg = "At #" + i;
				Boolean expect = (Boolean) fix[i][1];
				assertEquals(msg, expect, service.crossOverZero(series1));
				assertEquals(msg, expect, service.crossOverZero(series1, i));
			}

	}
	
	@Test
	public void testQema() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			series1.add(fix_qema5[i].value);
		}
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			String msg = "At #" + i;
			FR fr = fix_qema5[i];
			CDecimal value = service.qema(series1, i, 5);
			if ( fr.expected == null ) {
				assertNull(msg, value);
			} else {
				assertNotNull(msg, value);
				assertEquals(msg, fr.expected, value);
			}
		}
	}
	
	@Test
	public void testQema_RevOrder() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			series1.add(fix_qema5[i].value);
		}
		for ( int i = 0; i < fix_qema5.length - 1; i ++ ) {
			int index = i - fix_qema5.length + 1;
			String msg = "At #" + index;
			FR fr = fix_qema5[i];
			CDecimal value = service.qema(series1, index, 5);
			if ( fr.expected == null ) {
				assertNull(msg, value);
			} else {
				assertNotNull(msg, value);
				assertEquals(msg, fr.expected, value);
			}
		}
	}
	
	@Test
	public void testQema_NullIfCannotObtainStartValue() throws Exception {
		series1.add(CDecimalBD.of("40.27"));
		series1.add(null);
		series1.add(CDecimalBD.of("40.92"));
		series1.add(CDecimalBD.of("44.33"));
		series1.add(null);
		series1.add(null);
		series1.add(CDecimalBD.of("45.02"));
		series1.add(CDecimalBD.of("48.13"));
		for ( int i = 0; i < series1.getLength(); i ++ ) {
			String msg = "At #" + i;
			assertNull(msg, service.qema(series1, i, 3));
		}
	}
	
	@Test
	public void testQema_WithNullsButOk() throws Exception {
		FR fixture[] = {
			//  value, expected MA
			new FR("40.27",          null),
			new FR(null,          	 null ),
			new FR("40.92",	         null), //  40.9200
			new FR("44.33",          null), // (40.9200 * 2 + 2 * 44.33) / 4 = 42.625
			new FR("53.50",          null), // (42.6250 * 2 + 2 * 53.50) / 4 = 48.0625
			new FR("52.13", "50.096250000"), // (48.0625 * 2 + 2 * 52.13) / 4 = 50.09625
			new FR("45.02", "47.558125000"), // (50.09625 * 2 + 2 * 45.02) / 4 = 47.558125
			new FR(null, 	"47.558125000"),
			new FR(null, 	"47.558125000"),
			new FR("48.13", "47.844062500"), // (47.558125 * 2 + 2 * 48.13) / 4 = 47.8440625
			new FR("51.14", "49.492031250"), // (47.8440625 * 2 + 2 * 51.14) / 4 = 49.49203125
			new FR("52.18", "50.836015625"), // (49.49203125 * 2 + 2 * 52.18) / 4 = 50.836015625
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			CDecimal x = fixture[i].value;
			series1.add(x == null ? null : x.withScale(9));
		}
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			CDecimal expected = fixture[i].expected;
			CDecimal actual = service.qema(series1, i, 3);
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertNotNull(msg + " expected: " + expected + " but null", actual);
				assertEquals(msg, expected, actual);
			}
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testQema_ThrowsIfPeriodTooLow() throws Exception {
		service.qema(series1, 0, 1);
	}
	
	/**
	 * Фикстура теста Quik ATR(5)
	 */
	static String fix_qatr5[][] = {
		// RIU5, 2015-08-06, h1
		// open, high,low,close, expected ATR
		{"82960","82960","82960","82960", null},
		{"82840","83700","82840","83380", null},
		{"83390","83490","83110","83310", null},
		{"83280","83310","83140","83220", null},
		{"83200","83200","82610","82660", "404.000000"},
		{"82640","83090","82480","82880", "445.200000"},
		{"82890","83140","82830","83040", "418.160000"},
		{"82970","82990","82550","82720", "432.528000"},
		{"82730","82950","82660","82790", "404.022400"},
		{"82760","82860","82610","82700", "373.217920"},
		{"82690","82920","82620","82770", "358.574336"},
		{"82780","82850","82640","82670", "328.859469"},
		{"82660","82740","82530","82700", "305.087575"},
		{"82680","82830","82590","82710", "292.070060"},
		{"82720","82780","82610","82700", "267.656048"},
		{"82690","82760","82080","82190", "350.124838"},
		{"82190","82250","81680","81860", "394.099870"}, //x! -0.000001
		{"81830","81970","81630","81770", "383.279896"}, //x! -0.000001
		{"81790","82120","81760","82090", "378.623917"},
		{"82060","82260","81830","82050", "388.899134"},
		{"82020","82190","81770","81810", "395.119307"},
	};
	
	private void fillCandlesQatr5(int scale) throws Exception {
		String fixture[][] = fix_qatr5;
		for ( int i = 0; i < fixture.length; i ++ ) {
			candles.add(new CandleBuilder()
					.withTime(Instant.now())
					.withTimeFrame(ZTFrame.M5)
					.withOpenPrice(CDecimalBD.of(fixture[i][0]).withScale(scale))
					.withHighPrice(CDecimalBD.of(fixture[i][1]).withScale(scale))
					.withLowPrice(CDecimalBD.of(fixture[i][2]).withScale(scale))
					.withClosePrice(CDecimalBD.of(fixture[i][3]).withScale(scale))
					.withVolume(CDecimalBD.of(1L))
					.buildCandle());
		}
	}
	
	@Test
	public void testQatr() throws Exception {
		fillCandlesQatr5(6);
		String fixture[][] = fix_qatr5;
		CDecimal expected = null, actual = null;
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			if ( fixture[i][4] == null ) {
				expected = null;
			} else {
				expected = CDecimalBD.of(fixture[i][4]);
			}
			actual = service.qatr(candles, i, 5);
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertNotNull(msg + " expected: " + expected + " but null", actual);
				assertEquals(msg, expected, actual);
			}
		}
		assertEquals(expected, service.qatr(candles, 5));
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testDelta1_ThrowsIfOutOfRange() throws Exception {
		series1.add(CDecimalBD.of("112.34"));
		series1.add(CDecimalBD.of("113.05"));
		series1.add(CDecimalBD.of("111.26"));
		
		service.delta(series1, -3);
	}
	
	@Test
	public void testDelta1() throws Exception {
		series1.add(CDecimalBD.of("112.34"));
		series1.add(CDecimalBD.of("113.05"));
		series1.add(CDecimalBD.of("111.26"));
		series1.add(null);
		series1.add(CDecimalBD.of("124.15"));
		series1.add(CDecimalBD.of("125.01"));
		
		assertEquals(CDecimalBD.ZERO, service.delta(series1, 0));
		assertEquals(CDecimalBD.of("0.71"), service.delta(series1, 1));
		assertEquals(CDecimalBD.of("-1.79"), service.delta(series1, 2));
		assertNull(service.delta(series1, 3));
		assertEquals(CDecimalBD.ZERO, service.delta(series1, 4));
		assertEquals(CDecimalBD.of("0.86"), service.delta(series1, 5));
		
		assertEquals(CDecimalBD.ZERO, service.delta(series1, -1));
		assertNull(service.delta(series1,  -2));
		assertEquals(CDecimalBD.of("-1.79"), service.delta(series1, -3));
		assertEquals(CDecimalBD.of("0.71"), service.delta(series1, -4));
		assertEquals(CDecimalBD.ZERO, service.delta(series1, -5));
	}
	
	@Test (expected=ValueException.class)
	public void testAmean1_ThrowsIfEmptySeries() throws Exception {
		service.amean(series1, 4);
	}
	
	@Test (expected=ValueException.class)
	public void testAmean1_ThrowsIfNotEnoughData() throws Exception {
		series1.add(null);
		series1.add(null);
		series1.add(null);
		
		service.amean(series1, 4);
	}
	
	@Test
	public void testAmean2() throws Exception {
		series1.add(CDecimalBD.of("15.34"));
		series1.add(null);
		series1.add(CDecimalBD.of("30.29"));
		series1.add(CDecimalBD.of("10.12"));
		series1.add(null);
		
		assertEquals(CDecimalBD.of("18.5833"), service.amean(series1, 4));
	}
	
	@Test (expected=ValueException.class)
	public void testCovariance3_ThrowsIfEmptySeries() throws Exception {
		service.covariance(series1, series2, 4);
	}
	
	@Test (expected=ValueException.class)
	public void testCovariance3_ThrowsIfDifferentLength() throws Exception {
		series1.add(CDecimalBD.of("2.34"));
		series1.add(CDecimalBD.of("8.01"));
		series2.add(CDecimalBD.of("9.46"));
		
		service.covariance(series1, series2, 4);
	}
	
	@Test (expected=ValueException.class)
	public void testCovariance2_ThrowsIfNotEnoughData() throws Exception {
		series1.add(null);
		series1.add(null);
		series1.add(null);
		series2.add(null);
		series2.add(null);
		series2.add(null);
		
		service.covariance(series1, series2, 4);
	}
	
	@Test
	public void testCovariance3() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(CDecimalBD.of(78L));
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("4.8333"), service.covariance(series1, series2, 4));
	}
	
	@Test
	public void testCovariance3_NullValuesAtSamePos() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(null);
		series1.add(null);
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(null);
		series2.add(null);
		series2.add(CDecimalBD.of(78L));
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("6.6875"), service.covariance(series1, series2, 4));
	}
	
	@Test
	public void testCovariance3_NullValueInTheFirstSeries() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(null);
		series1.add(CDecimalBD.of(4L));
		series1.add(null);
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(CDecimalBD.of(78L));
		series2.add(CDecimalBD.of(82L));

		assertEquals(CDecimalBD.of("3.3750"), service.covariance(series1, series2, 4));
	}
	
	@Test
	public void testCovariance2_NullValueInTheSecondSeries() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(null);
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(null);
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("0.8750"), service.covariance(series1, series2, 4));
	}
	
	@Test
	public void testCovariance3_NullValuesAtDifferentPositions() throws Exception {
		series1.add(null);
		series1.add(CDecimalBD.of(5L));
		series1.add(CDecimalBD.of(4L));
		series1.add(null);
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(null);
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(null);
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("2.5000"), service.covariance(series1, series2, 4));		
	}
	
	@Test
	public void testCovariance3_TestCase1() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(null);
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(CDecimalBD.of(78L));
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("5.08"), service.covariance(series1, series2, 2));
	}
	
	@Test (expected=ValueException.class)
	public void testVariance2_ThrowsIfEmptySeries() throws Exception {
		service.variance(series1, 4);
	}

	@Test (expected=ValueException.class)
	public void testVariance2_ThrowsIfNotEnoughData() throws Exception {
		series1.add(null);
		series1.add(null);
		series1.add(null);
		
		service.variance(series1, 4);
	}

	@Test
	public void testVariance2() throws Exception {
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(CDecimalBD.of(78L));
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("34.33"), service.variance(series2, 2));
	}

	@Test
	public void testVariance2_NullValues() throws Exception {
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(null);
		series2.add(CDecimalBD.of(83L));
		series2.add(null);
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("26.25"), service.variance(series2, 2));
	}
	
	@Test
	public void testCorrelation3() throws Exception {
		series1.add(CDecimalBD.of("3.0000"));
		series1.add(CDecimalBD.of("5.0000"));
		series1.add(CDecimalBD.of("4.0000"));
		series1.add(CDecimalBD.of("4.0000"));
		series1.add(CDecimalBD.of("2.0000"));
		series1.add(CDecimalBD.of("3.0000"));
		series2.add(CDecimalBD.of("86.0000"));
		series2.add(CDecimalBD.of("95.0000"));
		series2.add(CDecimalBD.of("92.0000"));
		series2.add(CDecimalBD.of("83.0000"));
		series2.add(CDecimalBD.of("78.0000"));
		series2.add(CDecimalBD.of("82.0000"));
		
		assertEquals(CDecimalBD.of("0.862"), service.correlation(series1, series2, 3));
	}
	
	@Test (expected=ValueException.class)
	public void testCorrelation3_ThrowsIfEmptySeries() throws ValueException {
		service.correlation(series1, series2, 3);
	}
	
	@Test (expected=ValueException.class)
	public void testCorrelation3_ThrowsIfNotEnoughData() throws ValueException {
		series1.add(null);
		series1.add(null);
		series2.add(null);
		series2.add(null);
		
		service.correlation(series1, series2, 3);
	}
	
	@Test (expected=ValueException.class)
	public void testCorrelation3_ThrowsIfDifferentLength() throws Exception {
		series1.add(CDecimalBD.of("2.34"));
		series1.add(CDecimalBD.of("8.01"));
		series2.add(CDecimalBD.of("9.46"));
		
		service.correlation(series1, series2, 3);
	}
	
	@Test
	public void testCorrelation3_NullValuesAtSamePos() throws Exception {
		series1.add(CDecimalBD.of("3"));
		series1.add(CDecimalBD.of("5"));
		series1.add(null);
		series1.add(null);
		series1.add(CDecimalBD.of("2"));
		series1.add(CDecimalBD.of("3"));
		series2.add(CDecimalBD.of("86"));
		series2.add(CDecimalBD.of("95"));
		series2.add(null);
		series2.add(null);
		series2.add(CDecimalBD.of("78"));
		series2.add(CDecimalBD.of("82"));
		
		assertEquals(CDecimalBD.of("0.9741"), service.correlation(series1, series2, 4));
	}
	
	@Test
	public void testCorellation3_NullValueInTheFirstSeries() throws Exception {
		series1.add(CDecimalBD.of("3"));
		series1.add(CDecimalBD.of("5"));
		series1.add(null);
		series1.add(CDecimalBD.of("4"));
		series1.add(CDecimalBD.of("2"));
		series1.add(CDecimalBD.of("3"));
		series2.add(CDecimalBD.of("86"));
		series2.add(CDecimalBD.of("95"));
		series2.add(CDecimalBD.of("92"));
		series2.add(CDecimalBD.of("83"));
		series2.add(CDecimalBD.of("78"));
		series2.add(CDecimalBD.of("82"));

		assertEquals(CDecimalBD.of("0.8730"), service.correlation(series1, series2, 4));
	}
	
	@Test
	public void testCorrelation3_NullValueInTheSecondSeries() throws Exception {
		series1.add(CDecimalBD.of(3L));
		series1.add(CDecimalBD.of(5L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(4L));
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(CDecimalBD.of(95L));
		series2.add(CDecimalBD.of(92L));
		series2.add(null);
		series2.add(null);
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of("0.9370"), service.correlation(series1, series2, 4));
	}
	
	@Test
	public void testCorellation3_NullValuesAtDifferentPositions() throws Exception {
		series1.add(null);
		series1.add(CDecimalBD.of(5L));
		series1.add(CDecimalBD.of(4L));
		series1.add(null);
		series1.add(CDecimalBD.of(2L));
		series1.add(CDecimalBD.of(3L));
		series2.add(CDecimalBD.of(86L));
		series2.add(null);
		series2.add(CDecimalBD.of(92L));
		series2.add(CDecimalBD.of(83L));
		series2.add(null);
		series2.add(CDecimalBD.of(82L));
		
		assertEquals(CDecimalBD.of(1L), service.correlation(series1, series2, 0));		
	}
	
	/**
	 * Запись фикстуры для проверки пересечений.
	 */
	static class FR2 {
		private final CDecimal x, y;
		private final boolean expected;
		
		FR2(String x, String y, boolean expected) {
			if ( x == null ) {
				this.x = null;
			} else {
				this.x = CDecimalBD.of(x);
			}
			if ( y == null ) {
				this.y = null;
			} else {
				this.y = CDecimalBD.of(y);
			}
			this.expected = expected;
		}
		
		FR2(Long x, Long y, boolean expected) {
			if ( x == null ) {
				this.x = null;
			} else {
				this.x = CDecimalBD.of(x);
			}
			if ( y == null ) {
				this.y = null;
			} else {
				this.y = CDecimalBD.of(y);
			}
			this.expected = expected;
		}
		
	}
	
	@Test
	public void testCrossUnder() throws Exception {
		FR2 fix[] = {
			new FR2(45L, 20L, false),
			new FR2(15L, 22L, true),
			new FR2(25L, 23L, false),
			new FR2(null, 30L, false),
			new FR2(24L, 31L, false),
			new FR2(20L, 24L, false),
			new FR2(45L, 20L, false),
			new FR2(15L, null, false),
			new FR2(12L, 15L, false),
			new FR2((Long)null, null, false),
			new FR2((Long)null, null, false),
			new FR2(null, 10L, false),
			new FR2(null, 12L, false),
			new FR2(12L, null, false),
			new FR2(10L, null, false),
			new FR2(10L, 20L, false),
			new FR2( 9L, 22L, false),
			new FR2(20L, 10L, false),
			new FR2(10L, 20L, true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			FR2 f = fix[i];
			series1.add(f.x);
			series2.add(f.y);
			assertEquals("At #" + i, f.expected, service.crossUnder(series1, series2, i));
		}
	}
	
	@Test
	public void testCrossOver() throws Exception {
		FR2 fix[] = {
			new FR2(20L, 45L, false),
			new FR2(22L, 15L, true),
			new FR2(23L, 25L, false),
			new FR2(30L, null, false),
			new FR2(31L, 24L, false),
			new FR2(24L, 20L, false),
			new FR2(20L, 45L, false),
			new FR2(null, 15L, false),
			new FR2(15L, 12L, false),
			new FR2((Long)null, null, false),
			new FR2((Long)null, null, false),
			new FR2(10L, null, false),
			new FR2(12L, null, false),
			new FR2(null, 12L, false),
			new FR2(null, 10L, false),
			new FR2(20L, 10L, false),
			new FR2(22L,  9L, false),
			new FR2(10L, 20L, false),
			new FR2(20L, 10L, true),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			FR2 f = fix[i];
			series1.add(f.x);
			series2.add(f.y);
			assertEquals("At #" + i, f.expected, service.crossOver(series1, series2, i));
		}
	}
	
}
