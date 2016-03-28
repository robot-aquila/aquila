package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class OrderChangeImplTest {
	private Map<Integer, Object> tokens;
	private OrderChangeImpl change;

	@Before
	public void setUp() throws Exception {
		tokens = new HashMap<Integer, Object>();
		change = new OrderChangeImpl(tokens);
	}
	
	@Test
	public void testIsStatusChanged() {
		assertFalse(change.isStatusChanged());
		tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
		assertTrue(change.isStatusChanged());
	}
	
	@Test
	public void testIsFinalized() {
		assertFalse(change.isFinalized());
		tokens.put(OrderField.STATUS, OrderStatus.FILLED);
		assertTrue(change.isFinalized());
	}
	
	@Test
	public void testGetStatus() {
		tokens.put(OrderField.STATUS, OrderStatus.REJECTED);
		assertEquals(OrderStatus.REJECTED, change.getStatus());
	}
	
	@Test
	public void testGetDoneTime() {
		Instant time = Instant.now();
		tokens.put(OrderField.DONE_TIME, time);
		assertEquals(time, change.getDoneTime());
	}
	
	@Test
	public void testGetCurrentVolume() {
		tokens.put(OrderField.CURRENT_VOLUME, 100L);
		assertEquals(100L, change.getCurrentVolume());
	}

	@Test
	public void testGetExecutedValue() {
		tokens.put(OrderField.EXECUTED_VALUE, 286.15d);
		assertEquals(286.15d, change.getExecutedValue(), 0.01d);
	}

}
