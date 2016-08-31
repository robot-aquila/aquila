package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdSetExecutionSpeedTest {
	private CmdSetExecutionSpeed cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdSetExecutionSpeed(5);
	}
	
	@Test
	public void testCtor() {
		assertEquals(5, cmd.getExecutionSpeed());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsIfBadExecutionSpeed() {
		new CmdSetExecutionSpeed(-1);
	}

	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdSetExecutionSpeed(5)));
		assertFalse(cmd.equals(new CmdSetExecutionSpeed(0)));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
