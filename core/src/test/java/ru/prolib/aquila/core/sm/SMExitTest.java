package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SMExitTest {
	private SMState state;
	private SMExit exit;

	@Before
	public void setUp() throws Exception {
		state = new SMState();
		exit = new SMExit(state, "foobar");
	}

	@Test
	public void testGetState() throws Exception {
		assertSame(state, exit.getState());
	}
	
	@Test
	public void testGetId() throws Exception {
		assertEquals("foobar", exit.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("foobar", exit.toString());
	}

}
