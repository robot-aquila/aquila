package ru.prolib.aquila.ta;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012/02/09
 * $Id: PivotTypeTest.java 200 2012-02-11 14:03:38Z whirlwind $
 */
public class PivotTypeTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals(PivotType.MAX, PivotType.MAX);
		assertFalse(PivotType.MAX.equals(PivotType.MIN));
	}

}
