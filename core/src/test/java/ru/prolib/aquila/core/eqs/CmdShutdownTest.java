package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdShutdownTest {
	private CmdShutdown service;

	@Before
	public void setUp() throws Exception {
		service = new CmdShutdown();
	}

	@Test
	public void testGetters() {
		assertEquals(CmdType.SHUTDOWN, service.getType());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(66641497, service.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdShutdown()));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
