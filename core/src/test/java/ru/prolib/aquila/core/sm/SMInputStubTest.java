package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(64876529, 905)
				.append(SMExit.STUB)
				.build();
		
		assertEquals(expected, input.hashCode());
	}
	
	@Test
	public void testEquals() {
		assertTrue(input.equals(input));
		assertTrue(input.equals(new SMInputStub(SMExit.STUB)));
		assertFalse(input.equals(null));
		assertFalse(input.equals(this));
		assertFalse(input.equals(new SMInputStub(new SMExit(SMStateHandler.FINAL, "zulu"))));
	}

}
