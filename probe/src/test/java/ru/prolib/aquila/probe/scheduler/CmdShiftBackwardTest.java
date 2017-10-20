package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdShiftBackwardTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CmdShiftBackward cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdShiftBackward(T("2016-08-25T18:41:41Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.SHIFT_BACKWARD, cmd.getType());
		assertEquals(T("2016-08-25T18:41:41Z"), cmd.getTime());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdShiftBackward(T("2016-08-25T18:41:41Z"))));
		assertFalse(cmd.equals(new CmdShiftBackward(T("2016-08-25T00:00:00Z"))));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
