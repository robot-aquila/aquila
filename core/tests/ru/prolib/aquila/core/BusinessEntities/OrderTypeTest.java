package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.*;


/**
 * 2012-10-14<br>
 * $Id: OrderTypeTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderTypeTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("Limit", OrderType.LIMIT.toString());
		assertEquals("Market", OrderType.MARKET.toString());
		assertEquals("StopLimit", OrderType.STOP_LIMIT.toString());
		assertEquals("TakeProfit", OrderType.TAKE_PROFIT.toString());
		assertEquals("TakeProfit&StopLimit", OrderType.TPSL.toString());
		assertSame(OrderType.TPSL, OrderType.TAKE_PROFIT_AND_STOP_LIMIT);
		assertEquals("Other", OrderType.OTHER.toString());
	}

}
