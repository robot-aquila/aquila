package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

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
	private EventSystem es;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		source = new DataSeriesImpl(es);
		ma = new WilderMA(es, "foo", source, 3, 128);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
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
		ma = new WilderMA(es, "bar", source, 5);
		assertEquals("bar", ma.getId());
		assertSame(source, ma.getSource());
		assertEquals(5, ma.getPeriod());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, ma.getStorageLimit());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		ma = new WilderMA(es, source, 14);
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
		ma = new WilderMA(es, source, 3);
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(ma.equals(ma));
		assertFalse(ma.equals(this));
		assertFalse(ma.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<Double> src1 = new Vector<Double>();
		src1.add(80d);
		src1.add(912d);
		src1.add(15d);
		List<Double> src2 = new Vector<Double>();
		src2.add(1d);
		for ( int i = 0; i < src1.size(); i ++ ) {
			source.add(src1.get(i));
		}
		Variant<String> vId = new Variant<String>()
			.add("foo")
			.add("bar");
		Variant<List<Double>> vSrc = new Variant<List<Double>>(vId)
			.add(src1)
			.add(src2);
		Variant<Integer> vPer = new Variant<Integer>(vSrc)
			.add(3)
			.add(15);
		Variant<Integer> vLen = new Variant<Integer>(vPer)
			.add(128)
			.add(16);
		Variant<?> iterator = vLen;
		int foundCnt = 0;
		WilderMA x, found = null;
		do {
			DataSeriesImpl ds = new DataSeriesImpl(es);
			x = new WilderMA(es, vId.get(), ds, vPer.get(), vLen.get());
			for ( int i = 0; i < vSrc.get().size(); i ++ ) {
				ds.add(vSrc.get().get(i));
			}
			if ( ma.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertEquals(ma.getSource(), found.getSource());
		assertEquals(3, found.getPeriod());
		assertEquals(128, found.getStorageLimit());
	}
	
}
