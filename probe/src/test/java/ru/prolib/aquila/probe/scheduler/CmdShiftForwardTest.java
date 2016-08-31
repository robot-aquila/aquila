package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdShiftForwardTest {
	private CmdShiftForward cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdShiftForward(Instant.parse("2016-08-25T18:40:05Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.SHIFT_FORWARD, cmd.getType());
		assertEquals(Instant.parse("2016-08-25T18:40:05Z"), cmd.getTime());
	}

}
