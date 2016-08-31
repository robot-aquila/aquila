package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdModeSwitchRunStepTest {
	private CmdModeSwitchRunStep cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdModeSwitchRunStep();
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.MODE_SWITCH, cmd.getType());
		assertEquals(SchedulerMode.RUN_STEP, cmd.getMode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdModeSwitchRunStep()));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
