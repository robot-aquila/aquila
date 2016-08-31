package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdModeSwitchRunTest {
	private CmdModeSwitchRun cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdModeSwitchRun();
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.MODE_SWITCH, cmd.getType());
		assertEquals(SchedulerMode.RUN, cmd.getMode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdModeSwitchRun()));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
