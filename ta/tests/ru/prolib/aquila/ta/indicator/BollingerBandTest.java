package ru.prolib.aquila.ta.indicator;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ta.*;

/**
 * 2012-02-07
 * $Id: BollingerBandTest.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class BollingerBandTest {
	private ValueImpl<Double> central;
	private ValueImpl<Double> stdev;
	private BollingerBand bb;

	@Before
	public void setUp() throws Exception {
		central = new ValueImpl<Double>();
		stdev = new ValueImpl<Double>();
		bb = new BollingerBand(central, stdev, 2.0d);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(central, bb.getFirstSource());
		assertSame(stdev, bb.getSecondSource());
		assertEquals(2.0d, bb.getWidth(), 0.0001d);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfFirstSourceIsNull() throws Exception {
		new BollingerBand(null, stdev, 2.0d);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfSecondSourceIsNull() throws Exception {
		new BollingerBand(central, null, 2.0d);
	}
	
	@Test
	public void testCalculate() throws Exception {
		Double fix[][] = {
			// central, stdev, expected
			{ 100d,  35d, 170d },
			{ null,  35d, null },
			{ 100d, null, null },
			{ 234d,  82d, 398d },
		};
		
		for ( int i = 0; i < fix.length; i ++ ) {
			central.add(fix[i][0]);
			stdev.add(fix[i][1]);
			String msg = "At sequence #" + i;
			if ( fix[i][2] == null ) {
				assertNull(msg, bb.calculate());
			} else {
				assertEquals(msg, fix[i][2], bb.calculate(), 0.001d);
			}
		}
	}

}
