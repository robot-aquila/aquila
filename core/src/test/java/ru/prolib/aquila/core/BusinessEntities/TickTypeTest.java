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
		assertEquals("ASK", TickType.ASK.toString());
		assertEquals("BID", TickType.BID.toString());
		assertEquals("TRADE", TickType.TRADE.toString());
	}

}
