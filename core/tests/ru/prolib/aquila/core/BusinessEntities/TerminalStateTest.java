package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2013-02-10<br>
 * $Id$
 */
public class TerminalStateTest {
	
	@Test
	public void testConstants() {
		assertEquals("STOPPED", TerminalState.STOPPED.toString());
		assertEquals("STARTING", TerminalState.STARTING.toString());
		assertEquals("STARTED", TerminalState.STARTED.toString());
		assertEquals("CONNECTED", TerminalState.CONNECTED.toString());
		assertEquals("STOPPING", TerminalState.STOPPING.toString());
	}

}
