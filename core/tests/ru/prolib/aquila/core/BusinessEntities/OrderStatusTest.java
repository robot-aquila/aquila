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
		assertEquals(0x02, OrderStatus.VERSION);
		assertEquals("Pending", OrderStatus.PENDING.toString());
		assertFalse(OrderStatus.PENDING.isError());
		assertFalse(OrderStatus.PENDING.isFinal());
		assertFalse(OrderStatus.PENDING.isActive());
		assertTrue(OrderStatus.PENDING.canBeUpdated());
		
		assertEquals("Active", OrderStatus.ACTIVE.toString());
		assertFalse(OrderStatus.ACTIVE.isError());
		assertFalse(OrderStatus.ACTIVE.isFinal());
		assertTrue(OrderStatus.ACTIVE.isActive());
		assertTrue(OrderStatus.ACTIVE.canBeUpdated());
		
		assertEquals("Filled", OrderStatus.FILLED.toString());
		assertFalse(OrderStatus.FILLED.isError());
		assertTrue(OrderStatus.FILLED.isFinal());
		assertFalse(OrderStatus.FILLED.isActive());
		assertFalse(OrderStatus.FILLED.canBeUpdated());
		
		assertEquals("Cancelled", OrderStatus.CANCELLED.toString());
		assertFalse(OrderStatus.CANCELLED.isError());
		assertTrue(OrderStatus.CANCELLED.isFinal());
		assertFalse(OrderStatus.CANCELLED.isActive());
		assertFalse(OrderStatus.CANCELLED.canBeUpdated());
		
		assertEquals("Rejected", OrderStatus.REJECTED.toString());
		assertTrue(OrderStatus.REJECTED.isError());
		assertTrue(OrderStatus.REJECTED.isFinal());
		assertFalse(OrderStatus.REJECTED.isActive());
		assertFalse(OrderStatus.REJECTED.canBeUpdated());
		
		assertEquals("Cancelling", OrderStatus.CANCEL_SENT.toString());
		assertFalse(OrderStatus.CANCEL_SENT.isError());
		assertFalse(OrderStatus.CANCEL_SENT.isFinal());
		assertFalse(OrderStatus.CANCEL_SENT.isActive());
		assertTrue(OrderStatus.CANCEL_SENT.canBeUpdated());
		
		assertEquals("Condition", OrderStatus.CONDITION.toString());
		assertFalse(OrderStatus.CONDITION.isError());
		assertFalse(OrderStatus.CONDITION.isFinal());
		assertTrue(OrderStatus.CONDITION.isActive());
		assertTrue(OrderStatus.CONDITION.canBeUpdated());
		
		assertEquals("Sent", OrderStatus.SENT.toString());
		assertFalse(OrderStatus.SENT.isError());
		assertFalse(OrderStatus.SENT.isFinal());
		assertFalse(OrderStatus.SENT.isActive());
		assertTrue(OrderStatus.SENT.canBeUpdated());
		
		assertEquals("Cancel failed", OrderStatus.CANCEL_FAILED.toString());
		assertTrue(OrderStatus.CANCEL_FAILED.isError());
		assertTrue(OrderStatus.CANCEL_FAILED.isFinal());
		assertFalse(OrderStatus.CANCEL_FAILED.isActive());
		assertFalse(OrderStatus.CANCEL_FAILED.canBeUpdated());
	}

}
