package ru.prolib.aquila.core.indicator.function;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

public class QuikEMAFunctionTest {
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
	
	private EditableDataSeries source, own;
	private QuikEMAFunction fn;
	private EventSystem es;
	
	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		source = new DataSeriesImpl(es);
		own = new DataSeriesImpl(es);
		fn = new QuikEMAFunction(3);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testCalculate() throws Exception {
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #";
			source.add(fixture[i][0]);
			Double expected = fixture[i][1];
			Double actual = fn.calculate(source, own, i);
			if ( expected == null ) {
				assertNull(msg, actual);
			} else {
				assertEquals(msg, expected, actual, 0.000001d);
			}
			own.add(expected);
		}
	}
	
	@Test
	public void testGetDefaultId() throws Exception {
		assertEquals("QuikEMA(3)", fn.getDefaultId());
		fn.setPeriod(10);
		assertEquals("QuikEMA(10)", fn.getDefaultId());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(fn.equals(fn));
		assertFalse(fn.equals(null));
		assertFalse(fn.equals(this));
		assertTrue(fn.equals(new QuikEMAFunction(3)));
	}

}
