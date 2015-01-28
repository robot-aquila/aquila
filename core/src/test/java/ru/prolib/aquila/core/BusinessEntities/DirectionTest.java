package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Test;


public class DirectionTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("Buy", Direction.BUY.toString());
		assertEquals("Sell", Direction.SELL.toString());
	}

}
