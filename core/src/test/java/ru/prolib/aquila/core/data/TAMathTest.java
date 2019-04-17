package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.threeten.extra.Interval;

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
			new FR("86.38",  null),	// 1997-06-06
			new FR("86.81",  null),	// 1997-06-09
			new FR("87.08",  null),	// 1997-06-10
			new FR("87.28",  null),	// 1997-06-11
			new FR("88.97",  null),	// 1997-06-12
			new FR("89.72",  "2.963333333"),	// 1997-06-13
			new FR("89.75",  "2.693333333"),	// 1997-06-16
			new FR("89.63",  "1.853333333"),	// 1997-06-17
			new FR("89.31",  "0.653333333"),	// 1997-06-18
			new FR("90.23",  "0.750000000"),	// 1997-06-19
			new FR("89.58", "-0.120000000"),	// 1997-06-20
			new FR("87.41", "-2.153333333"),	// 1997-06-23
			new FR("89.63", "-0.093333333"),	// 1997-06-24
			new FR("89.00", "-0.706666667"),	// 1997-06-25
			new FR("88.56", "-0.513333333"),	// 1997-06-26
			new FR("88.91",  "0.036666667"),	// 1997-06-27
			new FR("88.31", "-0.370000000"),	// 1997-06-30
			new FR("89.34",  "0.276666667"),	// 1997-07-01
			new FR("90.81",  "1.986666667"),	// 1997-07-02
			new FR("92.06",  "3.466666667"),	// 1997-07-03
			new FR("91.13",  "2.276666667"),	// 1997-07-07
			new FR("92.08",  "2.593333333"),	// 1997-07-08
			new FR("91.06",  "0.323333333"),	// 1997-07-09
		},
		/**
		 * Фикстура теста VectorVest DPO(20)
		 */
		fix_vv_dpo20[] = {
			// SPY (S & P 500 SPDR) minutes from 2013-09-03 09:30:00, actually differs
			new FR("165.38",  null),	// 2013-09-03 09:30:00
			new FR("165.40",  null),	// 2013-09-03 09:31:00
			new FR("165.46",  null),	// 2013-09-03 09:32:00
			new FR("165.51",  null),	// 2013-09-03 09:33:00
			new FR("165.44",  null),	// 2013-09-03 09:34:00
			new FR("165.55",  null),	// 2013-09-03 09:35:00
			new FR("165.48",  null),	// 2013-09-03 09:36:00
			new FR("165.50",  null),	// 2013-09-03 09:37:00
			new FR("165.45",  null),	// 2013-09-03 09:38:00
			new FR("165.43",  null),	// 2013-09-03 09:39:00
			new FR("165.45",  null),	// 2013-09-03 09:40:00
			new FR("165.37",  null),	// 2013-09-03 09:41:00
			new FR("165.38",  null),	// 2013-09-03 09:42:00
			new FR("165.36",  null),	// 2013-09-03 09:43:00
			new FR("165.34",  null),	// 2013-09-03 09:44:00
			new FR("165.36",  null),	// 2013-09-03 09:45:00
			new FR("165.32",  null),	// 2013-09-03 09:46:00
			new FR("165.30",  null),	// 2013-09-03 09:47:00
			new FR("165.33",  null),	// 2013-09-03 09:48:00
			new FR("165.31",  null),	// 2013-09-03 09:49:00
			new FR("165.35",  null),	// 2013-09-03 09:50:00
			new FR("165.29",  null),	// 2013-09-03 09:51:00
			new FR("165.35",  null),	// 2013-09-03 09:52:00
			new FR("165.30",  null),	// 2013-09-03 09:53:00
			new FR("165.22",  null),	// 2013-09-03 09:54:00
			new FR("165.37",  null),	// 2013-09-03 09:55:00
			new FR("165.36",  null),	// 2013-09-03 09:56:00
			new FR("165.35",  null),	// 2013-09-03 09:57:00
			new FR("165.19",  null),	// 2013-09-03 09:58:00
			new FR("165.26",  null),	// 2013-09-03 09:59:00
			new FR("165.16", "-0.246000000"),	// 2013-09-03 10:00:00
			new FR("165.09", "-0.314500000"),	// 2013-09-03 10:01:00
			new FR("165.16", "-0.239000000"),	// 2013-09-03 10:02:00
			new FR("165.23", "-0.163500000"),	// 2013-09-03 10:03:00
			new FR("165.23", "-0.153000000"),	// 2013-09-03 10:04:00
			new FR("165.18", "-0.192000000"),	// 2013-09-03 10:05:00
			new FR("165.27", "-0.093000000"),	// 2013-09-03 10:06:00
			new FR("165.24", "-0.117000000"),	// 2013-09-03 10:07:00
			new FR("165.21", "-0.139500000"),	// 2013-09-03 10:08:00
			new FR("165.23", "-0.106500000"),	// 2013-09-03 10:09:00
			new FR("165.38",  "0.052000000"),	// 2013-09-03 10:10:00
			new FR("165.34",  "0.026500000"),	// 2013-09-03 10:11:00
			new FR("165.43",  "0.130500000"),	// 2013-09-03 10:12:00
			new FR("165.37",  "0.081500000"),	// 2013-09-03 10:13:00
			new FR("165.33",  "0.048000000"),	// 2013-09-03 10:14:00
			new FR("165.30",  "0.023500000"),	// 2013-09-03 10:15:00
			new FR("165.17", "-0.097500000"),	// 2013-09-03 10:16:00
			new FR("165.18", "-0.085000000"),	// 2013-09-03 10:17:00
			new FR("165.12", "-0.142000000"),	// 2013-09-03 10:18:00
			new FR("165.12", "-0.136000000"),	// 2013-09-03 10:19:00
			new FR("165.05", "-0.202000000"),	// 2013-09-03 10:20:00
			new FR("165.08", "-0.173500000"),	// 2013-09-03 10:21:00
			new FR("165.03", "-0.226000000"),	// 2013-09-03 10:22:00
			new FR("164.99", "-0.270000000"),	// 2013-09-03 10:23:00
			new FR("165.04", "-0.223500000"),	// 2013-09-03 10:24:00
			new FR("165.10", "-0.169000000"),	// 2013-09-03 10:25:00
			new FR("165.15", "-0.115500000"),	// 2013-09-03 10:26:00
			new FR("165.07", "-0.186000000"),	// 2013-09-03 10:27:00
			new FR("165.09", "-0.157500000"),	// 2013-09-03 10:28:00
			new FR("165.13", "-0.114000000"),	// 2013-09-03 10:29:00
			new FR("165.08", "-0.157000000"),	// 2013-09-03 10:30:00
			new FR("165.15", "-0.081500000"),	// 2013-09-03 10:31:00
			new FR("165.22", "-0.011000000"),	// 2013-09-03 10:32:00
			new FR("165.19", "-0.034500000"),	// 2013-09-03 10:33:00
			new FR("165.19", "-0.022500000"),	// 2013-09-03 10:34:00
			new FR("165.17", "-0.033000000"),	// 2013-09-03 10:35:00
			new FR("165.16", "-0.039000000"),	// 2013-09-03 10:36:00
			new FR("165.23",  "0.037000000"),	// 2013-09-03 10:37:00
			new FR("165.20",  "0.015500000"),	// 2013-09-03 10:38:00
		},
		/**
		 * Фикстура теста Quik EMA(5)
		 */
		fix_qema5[] = {
			// RIU5, 2015-07-31, m15, close
			new FR("85990", null), //09:15
			new FR("85190", null), //10:00
			new FR("85290", null), //10:15
			new FR("84980", null), //10:30
			new FR("85260", "85339.506173"), //10:45
			new FR("85120", "85266.337449"),
			new FR("84730", "85087.558299"),
			new FR("84890", "85021.705533"),
			new FR("84900", "84981.137022"),
			new FR("85120", "85027.424681"),
			new FR("85150", "85068.283121"),
			new FR("84950", "85028.855414"),
			new FR("85010", "85022.570276"),
			new FR("85150", "85065.046851"),//13:00
			new FR("85150", "85093.364567"),//13:15
			new FR("84940", "85042.243045"),
			new FR("84900", "84994.828697"),
			new FR("84440", "84809.885798"),
			new FR("84200", "84606.590532"),
			new FR("84230", "84481.060355"),
			new FR("84180", "84380.706903"),
			new FR("84430", "84397.137935"),
			new FR("84230", "84341.425290"),
			new FR("84850", "84510.950194"),
			new FR("84740", "84587.300129"),
			new FR("85230", "84801.533419"),
			new FR("85770", "85124.355613"),
			new FR("85480", "85242.903742"),
			new FR("86130", "85538.602495"),
			new FR("85720", "85599.068330"),
			new FR("85840", "85679.378887"),
			new FR("85830", "85729.585924"),
			new FR("85690", "85716.390616"),//17:45
			new FR("85670", "85700.927078"),//18:00
			new FR("85850", "85750.618052"),//18:15
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
		assertEquals(CDecimalBD.of("30.000000000"), service.sma(series1, 5));
		assertEquals(CDecimalBD.of("30.000000000"), service.sma(series1, 5, 5));
		
		series1.add(null);
		assertEquals(CDecimalBD.of("30.000000000"), service.sma(series1, -1, 5));
		assertNull(service.sma(series1, 5));
		assertNull(service.sma(series1, 6, 5));
	}
	
	@Test
	public void testSma_TestOfPrecision() throws Exception {
		List<CDecimal> expected = new ArrayList<>();
		// SiM8, M5, Exponential MA(close, 5), 2018-02-14 18:00
		series1.add(of(58540L));	expected.add(null);
		series1.add(of(58340L));	expected.add(null);
		series1.add(of(58343L));	expected.add(null);
		series1.add(of(58324L));	expected.add(null);
		series1.add(of(58337L));	expected.add(of("58376.800000")); // 18:20
		series1.add(of(58332L));	expected.add(of("58335.200000"));
		series1.add(of(58207L));	expected.add(of("58308.600000")); // 18:30
		series1.add(of(58138L));	expected.add(of("58267.600000"));
		series1.add(of(58113L));	expected.add(of("58225.400000")); // 18:40
		series1.add(of(58089L));	expected.add(of("58175.800000")); // 19:05
		series1.add(of(58040L));	expected.add(of("58117.400000"));
		series1.add(of(58042L));	expected.add(of("58084.400000")); // 19:15
		series1.add(of(57970L));	expected.add(of("58050.800000"));
		series1.add(of(57984L));	expected.add(of("58025.000000")); // 19:25
		series1.add(of(58032L));	expected.add(of("58013.600000"));
		
		for ( int i = 0; i < expected.size(); i ++ ) {
			String msg = "At #" + i;
			CDecimal actualValue = service.sma(series1, i, 5);
			if ( actualValue != null ) {
				actualValue = actualValue.withScale(6, RoundingMode.HALF_UP);
			}
			CDecimal expectedValue = expected.get(i);
			assertEquals(msg, expectedValue, actualValue);
		}
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
				assertEquals(msg, fr.expected, value.withScale(6));
			}
		}
	}
	
	@Test
	public void testQema_TestOfPrecision() throws Exception {
		List<CDecimal> expected = new ArrayList<>();
		// SiM8, M5, Exponential MA(close, 5), 2018-02-14 18:00
		series1.add(of(58540L));	expected.add(null);
		series1.add(of(58340L));	expected.add(null);
		series1.add(of(58343L));	expected.add(null);
		series1.add(of(58324L));	expected.add(null);
		series1.add(of(58337L));	expected.add(of("58375.395062")); // 18:20
		series1.add(of(58332L));	expected.add(of("58360.930041"));
		series1.add(of(58207L));	expected.add(of("58309.620027")); // 18:30
		series1.add(of(58138L));	expected.add(of("58252.413352"));
		series1.add(of(58113L));	expected.add(of("58205.942234")); // 18:40
		series1.add(of(58089L));	expected.add(of("58166.961490")); // 19:05
		series1.add(of(58040L));	expected.add(of("58124.640993"));
		series1.add(of(58042L));	expected.add(of("58097.093995")); // 19:15
		series1.add(of(57970L));	expected.add(of("58054.729330"));
		series1.add(of(57984L));	expected.add(of("58031.152887")); // 19:25
		series1.add(of(58032L));	expected.add(of("58031.435258"));
		
		for ( int i = 0; i < expected.size(); i ++ ) {
			String msg = "At #" + i;
			CDecimal actualValue = service.qema(series1, i, 5);
			if ( actualValue != null ) {
				actualValue = actualValue.withScale(6, RoundingMode.HALF_UP);
			}
			CDecimal expectedValue = expected.get(i);
			assertEquals(msg, expectedValue, actualValue);
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
				assertEquals(msg, fr.expected, value.withScale(6));
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
			series1.add(fixture[i].value);
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
		{"82190","82250","81680","81860", "394.099871"}, //x! -0.000001 <- WTF?
		{"81830","81970","81630","81770", "383.279897"}, //x! -0.000001
		{"81790","82120","81760","82090", "378.623917"},
		{"82060","82260","81830","82050", "388.899134"},
		{"82020","82190","81770","81810", "395.119307"},
	};
	
	private void fillCandlesQatr5() throws Exception {
		String fixture[][] = fix_qatr5;
		for ( int i = 0; i < fixture.length; i ++ ) {
			candles.add(new CandleBuilder()
					.withTime(Instant.now())
					.withTimeFrame(ZTFrame.M5)
					.withOpenPrice(of(fixture[i][0]))
					.withHighPrice(of(fixture[i][1]))
					.withLowPrice(of(fixture[i][2]))
					.withClosePrice(of(fixture[i][3]))
					.withVolume(of(1L))
					.buildCandle());
		}
	}
	
	@Test
	public void testQatr() throws Exception {
		fillCandlesQatr5();
		String fixture[][] = fix_qatr5;
		CDecimal expected = null, actual = null;
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			if ( fixture[i][4] == null ) {
				expected = null;
			} else {
				expected = of(fixture[i][4]);
			}
			actual = service.qatr(candles, i, 5);
			if ( actual != null ) {
				actual = actual.withScale(6);
			}
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertNotNull(msg + " expected: " + expected + " but null", actual);
				assertEquals(msg, expected, actual);
			}
		}
		assertEquals(expected, service.qatr(candles, 5).withScale(6));
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
	public void testCrossUnder_ForkCase() throws Exception {
		// ___ series1
		//  \_ series2
		
		series1.add(of(20L)); series1.add(of(20L)); series1.add(of(20L));
		series2.add(of(20L)); series2.add(of(20L)); series2.add(of(10L));
		
		assertFalse(service.crossUnder(series1, series2, 1));
		assertFalse(service.crossUnder(series2, series1, 1));
		assertFalse(service.crossOver(series1, series2, 1));
		assertFalse(service.crossOver(series2, series1, 1));
		
		assertFalse(service.crossUnder(series1, series2, 2));
		assertTrue(service.crossUnder(series2, series1, 2));
		assertTrue(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
	}
	
	@Test
	public void testCrossUnder_JoinCase() throws Exception {
		// _   series1
		// _\_ series2
		
		series1.add(of(20L)); series1.add(of(10L)); series1.add(of(10L));
		series2.add(of(10L)); series2.add(of(10L)); series2.add(of(10L));
		
		assertTrue(service.crossUnder(series1, series2, 1));
		assertFalse(service.crossUnder(series2, series1, 1));
		assertFalse(service.crossOver(series1, series2, 1));
		assertTrue(service.crossOver(series2, series1, 1));
		
		assertFalse(service.crossUnder(series1, series2, 2));
		assertFalse(service.crossUnder(series2, series1, 2));
		assertFalse(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
	}
	
	@Test
	public void testCrossUnder_SkipDouble() throws Exception {
		// _    series1  
		// _\__ series2  
		//   \_ series1
		
		series1.add(of(30L)); series1.add(of(20L)); series1.add(of(10L));
		series2.add(of(20L)); series2.add(of(20L)); series2.add(of(20L));
		
		assertTrue(service.crossUnder(series1, series2, 1));
		assertFalse(service.crossUnder(series2, series1, 1));
		assertFalse(service.crossOver(series1, series2, 1));
		assertTrue(service.crossOver(series2, series1, 1));
		
		assertFalse(service.crossUnder(series1, series2, 2));
		assertFalse(service.crossUnder(series2, series1, 2));
		assertFalse(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
	}
	
	@Test
	public void testCrossUnder_Bounce() throws Exception {
		// _  _ series1
		// _\/_ series2

		series1.add(of(30L)); series1.add(of(20L)); series1.add(of(30L));
		series2.add(of(20L)); series2.add(of(20L)); series2.add(of(20L));
		
		assertTrue(service.crossUnder(series1, series2, 1));
		assertFalse(service.crossUnder(series2, series1, 1));
		assertFalse(service.crossOver(series1, series2, 1));
		assertTrue(service.crossOver(series2, series1, 1));
		
		assertFalse(service.crossUnder(series1, series2, 2));
		assertTrue(service.crossUnder(series2, series1, 2));
		assertTrue(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
	}
	
	@Test
	public void testCrossOver_ForkCase() throws Exception {
		//   _ series1
		// _/_ series2
		
		series1.add(of(10L)); series1.add(of(20L)); series1.add(of(20L));
		series2.add(of(10L)); series2.add(of(10L)); series2.add(of(10L));
		
		assertTrue(service.crossOver(series1, series2, 1));
		assertFalse(service.crossOver(series2, series1, 1));
		assertFalse(service.crossUnder(series1, series2, 1));
		assertTrue(service.crossUnder(series2, series1, 1));
		
		assertFalse(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
		assertFalse(service.crossUnder(series1, series2, 2));
		assertFalse(service.crossUnder(series2, series1, 2));
	}
	
	@Test
	public void testCrossOver_JoinCase() throws Exception {
		// ___ series2
		// _/  series1

		series1.add(of(10L)); series1.add(of(20L)); series1.add(of(20L));
		series2.add(of(20L)); series2.add(of(20L)); series2.add(of(20L));
		
		assertTrue(service.crossOver(series1, series2, 1));
		assertFalse(service.crossOver(series2, series1, 1));
		assertFalse(service.crossUnder(series1, series2, 1));
		assertTrue(service.crossUnder(series2, series1, 1));
		
		assertFalse(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
		assertFalse(service.crossUnder(series1, series2, 2));
		assertFalse(service.crossUnder(series2, series1, 2));
	}
	
	@Test
	public void testCrossOver_SkipDouble() throws Exception {
		//    _ series1  
		// __/_ series2  
		// _/   series1

		series1.add(of(10L)); series1.add(of(20L)); series1.add(of(30L));
		series2.add(of(20L)); series2.add(of(20L)); series2.add(of(20L));
		
		assertTrue(service.crossOver(series1, series2, 1));
		assertFalse(service.crossOver(series2, series1, 1));
		assertFalse(service.crossUnder(series1, series2, 1));
		assertTrue(service.crossUnder(series2, series1, 1));
		
		assertFalse(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
		assertFalse(service.crossUnder(series1, series2, 2));
		assertFalse(service.crossUnder(series2, series1, 2));
	}
	
	@Test
	public void testCrossOver_Bounce() throws Exception {
		// _  _ series1
		// _/\_ series2
		
		series1.add(of(20L)); series1.add(of(20L)); series1.add(of(20L));
		series2.add(of(10L)); series2.add(of(20L)); series2.add(of(10L));
		
		assertFalse(service.crossOver(series1, series2, 1));
		assertTrue(service.crossOver(series2, series1, 1));
		assertTrue(service.crossUnder(series1, series2, 1));
		assertFalse(service.crossUnder(series2, series1, 1));
		
		assertTrue(service.crossOver(series1, series2, 2));
		assertFalse(service.crossOver(series2, series1, 2));
		assertFalse(service.crossUnder(series1, series2, 2));
		assertTrue(service.crossUnder(series2, series1, 2));
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
	
	static class FR2x {
		private final CDecimal x, y;
		private final int expected;
		
		public FR2x(Long x, Long y, int expected) {
			this.x = x == null ? null : CDecimalBD.of(x);
			this.y = y == null ? null : CDecimalBD.of(y);
			this.expected = expected;
		}
		
	}
	
	@Test
	public void testCross() throws Exception {
		Long nul = null;
		FR2x fix[] = {
			new FR2x(20L, nul,  0),
			new FR2x(10L, 10L,  0),
			new FR2x(nul, 20L,  0),
			new FR2x(nul, nul,  0),
			new FR2x(10L, 10L,  0),
			new FR2x(10L, 10L,  0),
			new FR2x(10L, 20L, -1), // #6 
			new FR2x(10L, 20L,  0),
			new FR2x(20L, 20L,  1),
			new FR2x(20L, 20L,  0),
			new FR2x(30L, 25L,  1),
			new FR2x(25L, 25L, -1),
			new FR2x(15L, 25L,  0),
		};
		Instant start_time = Instant.EPOCH;
		ZTFrame tf = ZTFrame.M5;
		TSeriesImpl<CDecimal> xs = new TSeriesImpl<>(tf), ys = new TSeriesImpl<>(tf);
		Interval cur_int = tf.getInterval(start_time);
		for ( FR2x fr : fix ) {
			Instant cur_time = cur_int.getStart();
			xs.set(cur_time, fr.x);
			ys.set(cur_time, fr.y);
			cur_int = tf.getInterval(cur_int.getEnd());
		}
		
		cur_int = tf.getInterval(start_time);
		for ( int i = 0; i < fix.length; i ++ ) {
			Instant cur_time = cur_int.getEnd();
			FR2x fr = fix[i];
			String msg = "At#" + i;
			switch ( fr.expected ) {
			case -1:
				//System.out.println(new StringBuilder()
				//		.append("crossUnder: ")
				//		.append("xs[").append(i).append("]=").append(xs.get(i))
				//		.append("ys[").append(i).append("]=").append(ys.get(i))
				//		.append(" is ").append(service.crossUnder(xs, ys, i))
				//		.toString());
				assertFalse(msg, service.crossOver(xs, ys, i));
				assertTrue( msg, service.crossUnder(xs, ys, i));
				break;
			case  1:
				assertTrue( msg, service.crossOver(xs, ys,  i));
				assertFalse(msg, service.crossUnder(xs, ys, i));
				break;
			case  0:
			default:
				assertFalse(msg, service.crossOver(xs, ys, i));
				assertFalse(msg, service.crossUnder(xs, ys, i));
				break;
			}
			assertEquals(msg, fr.expected, service.cross(xs, ys, cur_time));
			cur_int = tf.getInterval(cur_int.getEnd());
		}
	}
	
}
