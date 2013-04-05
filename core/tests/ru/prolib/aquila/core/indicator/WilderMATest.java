package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.data.*;

/**
 * 2013-03-12<br>
 * $Id: WilderMATest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class WilderMATest {
	private static Double fixture[][] = {
		// ( Previous Wilder MA * ( n - 1 ) + DataSeries Value ) / n
		// value, expected ma
		{  28.17d,  28.170000d },
		{  40.92d,  32.420000d },
		{  42.07d,  35.636666d },
		{  44.33d,  38.534444d },
		{  53.50d,  43.522962d },
		{  51.20d,  46.081975d },
		{  45.02d,  45.727983d },
		{    null,        null },
		{    null,        null },
		{ 218.85d, 218.850000d },
		{ 223.80d, 220.500000d },
		{ 228.46d, 223.153333d },
	};

	private DataSeriesImpl source;
	private WilderMA ma;

	@Before
	public void setUp() throws Exception {
		source = new DataSeriesImpl();
		ma = new WilderMA("foo", source, 3, 128);
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
		ma = new WilderMA("bar", source, 5);
		assertEquals("bar", ma.getId());
		assertSame(source, ma.getSource());
		assertEquals(5, ma.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, ma.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		ma = new WilderMA(source, 14);
		assertEquals("WilderMA(14)", ma.getId());
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
		ma = new WilderMA(source, 3);
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
