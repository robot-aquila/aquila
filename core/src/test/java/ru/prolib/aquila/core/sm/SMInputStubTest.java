package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SMInputStubTest {
	private SMInputStub input;

	@Before
	public void setUp() throws Exception {
		input = new SMInputStub(SMExit.STUB);
	}

	@Test
	public void testInput() throws Exception {
		assertSame(SMExit.STUB, input.input(null));
	}

}
