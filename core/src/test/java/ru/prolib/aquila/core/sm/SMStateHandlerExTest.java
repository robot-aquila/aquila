package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SMStateHandlerExTest {
	private SMStateHandlerEx service;

	@Before
	public void setUp() throws Exception {
		service = new SMStateHandlerEx();
	}

	@Test
	public void testCtor() {
		assertSame(service, service.getEnterAction());
		List<SMExit> expected_exits = service.getExits();
		assertEquals(2, expected_exits.size());
		assertTrue(expected_exits.contains(service.getExit("ERROR")));
		assertTrue(expected_exits.contains(service.getExit("INTERRUPT")));
		List<SMInput> expected_inputs = service.getInputs();
		assertEquals(1, expected_inputs.size());
		assertEquals(new SMInput(service, new OnInterruptAction(service)), expected_inputs.get(0));
		assertEquals(service.getInterrupt(), expected_inputs.get(0));
	}
	
	@Test
	public void testOnInterrupt() {
		assertSame(service.getExit("INTERRUPT"), service.onInterrupt(null));
	}

}
