package ru.prolib.aquila.ta.indicator;

import org.junit.*;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.Sma;
import static org.junit.Assert.*;

public class SmaTest {
	private ValueImpl<Double> source;
	private Sma sma;

	@Before
	public void setUp() throws Exception {
		source = new ValueImpl<Double>();
		sma = new Sma(source, 3);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals(3, sma.getPeriod());
		assertSame(source, sma.getSource());
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new Sma(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new Sma(source, 1);
	}

	@Test
	public void testCalculate() throws Exception {
		double fixture[] = {5.0d, 6.0d, 2.0d, 9.0d, 8.0d};
		double expected[] = {5.0d, 5.5d, 4.33333d, 5.66666d, 6.33333d};
		assertNull(sma.calculate());
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(fixture[i]);
			assertEquals("At sequence #" + i, expected[i], sma.calculate(), 0.00001d);
		}
	}

}
