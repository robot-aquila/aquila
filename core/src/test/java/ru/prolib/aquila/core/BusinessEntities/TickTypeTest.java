package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TickTypeTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() {
		assertEquals("Tick", TickType.TICK.toString());
		assertEquals("Ask", TickType.ASK.toString());
		assertEquals("Bid", TickType.BID.toString());
		assertEquals("Trade", TickType.TRADE.toString());
	}

}
