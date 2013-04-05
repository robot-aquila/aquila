package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-09-22<br>
 * $Id: OrderStatusTest.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class OrderStatusTest {
	
	@Test
	public void testConstants() {
		assertEquals(0x01, OrderStatus.VERSION);
		assertEquals("Pending", OrderStatus.PENDING.toString());
		assertEquals("Active", OrderStatus.ACTIVE.toString());
		assertEquals("Filled", OrderStatus.FILLED.toString());
		assertEquals("Cancelled", OrderStatus.CANCELLED.toString());
		assertEquals("Failed", OrderStatus.FAILED.toString());
	}

}
