package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.data.*;

/**
 * 2013-03-11<br>
 * $Id: QUIK_EMATest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class QUIK_EMATest {
	private static Double fixture[][] = {
		// value, expected ma
		{  28.17d,  28.170000d },
		{  40.92d,  34.545000d },
		{  42.07d,  38.307500d },
		{  44.33d,  41.318750d },
		{  53.50d,  47.409375d },
		{  51.20d,  49.304688d },
		{  45.02d,  47.162344d },
		{    null,        null },
		{    null,        null },
		{ 218.85d, 218.850000d },
		{ 223.80d, 221.325000d },
		{ 228.46d, 224.892500d },
	};
	
	private DataSeriesImpl source;
	private QUIK_EMA ma;

	@Before
	public void setUp() throws Exception {
		source = new DataSeriesImpl();
		ma = new QUIK_EMA("foo", source, 3, 128);
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertEquals("foo", ma.getId());
		assertSame(source, ma.getSource());
		assertEquals(3, ma.getPeriod());
		assertEquals(128, ma.getStorageLimit());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		ma = new QUIK_EMA("bar", source, 5);
		assertEquals("bar", ma.getId());
		assertSame(source, ma.getSource());
		assertEquals(5, ma.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, ma.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		ma = new QUIK_EMA(source, 14);
		assertEquals("QUIK_EMA(14)", ma.getId());
		assertSame(source, ma.getSource());
		assertEquals(14, ma.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, ma.getStorageLimit());
	}
	
	@Test
	public void testCalculation_StepByStep() throws Exception {
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(fixture[i][0]);
			Double expected = fixture[i][1];
			String msg = "At #" + i;
			if ( expected == null ) {
				assertNull(msg, ma.get());
			} else {
				Double actual = ma.get();
				assertNotNull(msg, actual);
				assertEquals(msg, fixture[i][1], actual, 0.000001d);
			}
		}
	}
	
	@Test
	public void testCalculation_Late() throws Exception {
		for ( int i = 0; i < 5; i ++ ) {
			source.add(fixture[i][0]);
		}
		ma = new QUIK_EMA(source, 3);
		for ( int i = 0; i < 5; i ++ ) {
			Double expected = fixture[i][1];
			String msg = "At #" + i;
			if ( expected == null ) {
				assertNull(msg, ma.get(i));
			} else {
				Double actual = ma.get(i);
				assertNotNull(msg, actual);
				assertEquals(msg, fixture[i][1], actual, 0.000001d);
			}
		}
	}

}
