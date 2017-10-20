package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdModeSwitchRunCutoffTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CmdModeSwitchRunCutoff cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdModeSwitchRunCutoff(T("2016-08-25T18:35:55Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.MODE_SWITCH, cmd.getType());
		assertEquals(SchedulerMode.RUN_CUTOFF, cmd.getMode());
		assertEquals(Instant.parse("2016-08-25T18:35:55Z"), cmd.getCutoff());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdModeSwitchRunCutoff(T("2016-08-25T18:35:55Z"))));
		assertFalse(cmd.equals(new CmdModeSwitchRunCutoff(T("2016-08-25T00:00:00Z"))));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
