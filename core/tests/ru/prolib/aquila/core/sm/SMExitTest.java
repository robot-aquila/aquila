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
		exit = new SMExit(state);
	}

	@Test
	public void testGetState() {
		assertSame(state, exit.getState());
	}

}
