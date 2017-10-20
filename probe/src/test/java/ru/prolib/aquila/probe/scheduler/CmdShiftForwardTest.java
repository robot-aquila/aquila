package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdShiftForwardTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CmdShiftForward cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdShiftForward(T("2016-08-25T18:40:05Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.SHIFT_FORWARD, cmd.getType());
		assertEquals(T("2016-08-25T18:40:05Z"), cmd.getTime());
	}
	
	@Test
	public void testEquals() {
		assertTrue(cmd.equals(cmd));
		assertTrue(cmd.equals(new CmdShiftForward(T("2016-08-25T18:40:05Z"))));
		assertFalse(cmd.equals(new CmdShiftForward(T("2016-08-25T18:40:00Z"))));
		assertFalse(cmd.equals(null));
		assertFalse(cmd.equals(this));
	}

}
