package ru.prolib.aquila.ta.indicator;


import org.junit.*;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.Smma;
import static org.junit.Assert.*;

public class SmmaTest {
	private TestValue<Double> source;
	private Smma ma;

	@Before
	public void setUp() throws Exception {
		source = new TestValue<Double>();
		ma = new Smma(source, 3);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals(3, ma.getPeriod());
		assertSame(source, ma.getSource());
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSourceIsNull() throws Exception {
		new Smma(null, 5);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConstruct_ThrowsIfPeriodsLessThan2() throws Exception {
		new Smma(source, 1);
	}

	private void pass(double fixture[], double expected[]) throws Exception {
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(fixture[i]);
			assertEquals("At sequence #" + i, expected[i],
					ma.calculate(), 0.00001d);
		}
	}
	
	@Test
	public void testCalculate1() throws Exception {
		double fixture[] = {5.0d, 1.5d, 8.5d, 7.0d, 3.5d, 1.2d};
		double expected[] = {5.0d, 3.25d, 5.0d, 5.66666d, 4.94444d, 3.69629d};
		pass(fixture, expected);
	}
	
	@Test
	public void testCalculate2() throws Exception {
		double fixture[] = {1.0d, 2.0d, 3.0d, 4.0d, 5.0d, 6.0d};
		double expected[] = {1.0d, 1.5d, 2.0d, 2.66666d, 3.44444d, 4.29629d};
		pass(fixture, expected);
	}

}
