package ru.prolib.aquila.quik.assembler.cache.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.dde.RowDataTypeMismatchException;

public class RowDataTypeMismatchExceptionTest {
	private RowDataTypeMismatchException exception;

	@Before
	public void setUp() throws Exception {
		exception = new RowDataTypeMismatchException("name", "string");
	}
	
	@Test
	public void testGetMessage() throws Exception {
		assertEquals("Data type mismatch. Expected [string] for [name]",
				exception.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vElemId = new Variant<String>()
			.add("name")
			.add("foo");
		Variant<String> vType = new Variant<String>(vElemId)
			.add("string")
			.add("double");
		Variant<?> iterator = vType;
		int foundCnt = 0;
		RowDataTypeMismatchException x = null, found = null;
		do {
			x = new RowDataTypeMismatchException(vElemId.get(), vType.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("name", found.getElementId());
		assertEquals("string", found.getExpectedType());
	}

}
