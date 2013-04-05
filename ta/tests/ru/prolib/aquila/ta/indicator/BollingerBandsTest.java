package ru.prolib.aquila.ta.indicator;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.ta.*;

/**
 * 2012-02-07
 * $Id: BollingerBandsTest.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class BollingerBandsTest {
	
	@Test
	public void testConstruct() throws Exception {
		ValueImpl<Double> central = new ValueImpl<Double>();
		ValueImpl<Double> upper = new ValueImpl<Double>();
		ValueImpl<Double> lower = new ValueImpl<Double>();
		BollingerBands bb = new BollingerBands(central, upper, lower);
		assertSame(central, bb.getCentralLine());
		assertSame(upper, bb.getUpperBand());
		assertSame(lower, bb.getLowerBand());
	}

}
