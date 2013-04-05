package ru.prolib.aquila.ta.math;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ValueImpl;

/**
 * 2012-02-07
 * $Id: BollingerBandTest.java 199 2012-02-07 21:10:33Z whirlwind $
 */
public class BollingerBandTest {
	TestValue<Double> central;
	TestValue<Double> stdev;
	BollingerBand value;

	@Before
	public void setUp() throws Exception {
		central = new TestValue<Double>();
		stdev = new TestValue<Double>();
		value = new BollingerBand(central, stdev, 2.0d, "foo");
	}
	
	@Test
	public void testConstruct4() throws Exception {
		assertSame(central, value.getCentralLine());
		assertSame(stdev, value.getStandardDeviation());
		assertEquals(2.0d, value.getFactor(), 0.0001d);
		assertEquals("foo", value.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		value = new BollingerBand(central, stdev, 5.0d);
		assertSame(central, value.getCentralLine());
		assertSame(stdev, value.getStandardDeviation());
		assertEquals(5.0d, value.getFactor(), 0.0001d);
		assertEquals(ValueImpl.DEFAULT_ID, value.getId());
	}
	
	@Test
	public void testUpdate() throws Exception {
		Double fix[][] = {
			// central, stdev, expected
			{ 100d,  35d, 170d },
			{ null,  35d, null },
			{ 100d, null, null },
			{ 234d,  82d, 398d },
		};
		
		for ( int i = 0; i < fix.length; i ++ ) {
			central.addToStackAndUpdate(fix[i][0]);
			stdev.addToStackAndUpdate(fix[i][1]);
			value.update();
			if ( fix[i][2] == null ) {
				assertNull("At #" + i, value.get());
			} else {
				assertEquals("At #" + i, fix[i][2], value.get(), 0.001d);
			}
		}
	}

}
