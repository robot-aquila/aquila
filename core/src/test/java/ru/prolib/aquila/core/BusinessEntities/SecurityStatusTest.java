package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 2012-12-28<br>
 * $Id: SecurityStatusTest.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecurityStatusTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("Trading", SecurityStatus.TRADING.toString());
		assertEquals("Trading", SecurityStatus.TRADING.getCode());
		assertEquals("Stopped", SecurityStatus.STOPPED.toString());
		assertEquals("Stopped", SecurityStatus.STOPPED.getCode());
	}

}
