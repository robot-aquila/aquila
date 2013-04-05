package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-12-26<br>
 * $Id: PositionTypeTest.java 383 2012-12-26 12:21:37Z whirlwind $
 */
public class PositionTypeTest {
	
	@Test
	public void testConstant() throws Exception {
		assertEquals("Close", PositionType.CLOSE.getCode());
		assertEquals("Close", PositionType.CLOSE.toString());
		assertEquals("Long", PositionType.LONG.getCode());
		assertEquals("Long", PositionType.LONG.toString());
		assertEquals("Short", PositionType.SHORT.getCode());
		assertEquals("Short", PositionType.SHORT.toString());
	}

}
