package ru.prolib.aquila.probe.scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdCloseTest {
	private CmdClose cmd;

	@Before
	public void setUp() throws Exception {
		cmd = new CmdClose();
	}

	@Test
	public void testCtor() {
		assertEquals(CmdType.CLOSE, cmd.getType());
	}

}
