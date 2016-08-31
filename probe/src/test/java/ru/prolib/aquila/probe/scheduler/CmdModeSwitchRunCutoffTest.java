package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdModeSwitchRunCutoffTest {
	private CmdModeSwitchRunCutoff cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdModeSwitchRunCutoff(Instant.parse("2016-08-25T18:35:55Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.MODE_SWITCH, cmd.getType());
		assertEquals(SchedulerMode.RUN_CUTOFF, cmd.getMode());
		assertEquals(Instant.parse("2016-08-25T18:35:55Z"), cmd.getCutoff());
	}

}
