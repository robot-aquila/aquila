package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Test;


public class OrderActionTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("BUY", OrderAction.BUY.toString());
		assertEquals("COVER", OrderAction.COVER.toString());
		assertEquals("SELL", OrderAction.SELL.toString());
		assertEquals("SELL_SHORT", OrderAction.SELL_SHORT.toString());
	}
	
	@Test
	public void testIsBuy() {
		assertTrue(OrderAction.BUY.isBuy());
		assertTrue(OrderAction.COVER.isBuy());
		assertFalse(OrderAction.SELL.isBuy());
		assertFalse(OrderAction.SELL_SHORT.isBuy());
	}

}
