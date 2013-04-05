package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * 2012-10-24<br>
 * $Id: PriceUnitTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class PriceUnitTest {
	
	@Test
	public void testConstant() throws Exception {
		assertEquals("PU", PriceUnit.MONEY.toString());
		assertEquals("%", PriceUnit.PERCENT.toString());
		assertEquals("Pts.", PriceUnit.POINT.toString());
	}

}
