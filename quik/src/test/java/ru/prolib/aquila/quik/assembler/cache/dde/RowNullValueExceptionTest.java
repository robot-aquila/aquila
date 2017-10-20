package ru.prolib.aquila.quik.assembler.cache.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.quik.assembler.cache.dde.RowNullValueException;

public class RowNullValueExceptionTest {
	private RowNullValueException exception;

	@Before
	public void setUp() throws Exception {
		exception = new RowNullValueException("foobar");
	}
	
	@Test
	public void testGetMessage() throws Exception {
		assertEquals("Unexpected null value: foobar", exception.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(exception.equals(new RowNullValueException("foobar")));
		assertFalse(exception.equals(new RowNullValueException("zulu4")));
	}

}
