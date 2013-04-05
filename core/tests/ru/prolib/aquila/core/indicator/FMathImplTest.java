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
	public void testDpo() throws Exception {
		assertNull(math.dpo(value, 3));
		assertNull(math.dpo(value, 0, 3));
		
		value.add(null);
		assertNull(math.dpo(value, 3));
		assertNull(math.dpo(value, 0, 3));
		
		value.add(10.0);
		assertNull(math.dpo(value, 3));
		assertNull(math.dpo(value, 1, 3));
		
		value.add(20.0);
		value.add(30.0);
		assertEquals(5.0, math.dpo(value, 3), 0.01);
		assertEquals(5.0, math.dpo(value, 3, 3), 0.01);
		
		value.add(35.0);
		assertEquals(2.5, math.dpo(value, 3), 0.01);
		assertEquals(2.5, math.dpo(value, 4, 3), 0.01);
		
		value.add(30.0);
		assertEquals(-2.5, math.dpo(value, 3), 0.01);
		assertEquals(-2.5, math.dpo(value, 5, 3), 0.01);
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
