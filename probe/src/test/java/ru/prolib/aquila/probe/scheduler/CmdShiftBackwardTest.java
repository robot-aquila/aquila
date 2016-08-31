package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class CmdShiftBackwardTest {
	private CmdShiftBackward cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdShiftBackward(Instant.parse("2016-08-25T18:41:41Z"));
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.SHIFT_BACKWARD, cmd.getType());
		assertEquals(Instant.parse("2016-08-25T18:41:41Z"), cmd.getTime());
	}

}
