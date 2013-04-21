package ru.prolib.aquila.dde.utils.table;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class NotAllRequiredFieldsExceptionTest {
	private NotAllRequiredFieldsException exception;

	@Before
	public void setUp() throws Exception {
		exception = new NotAllRequiredFieldsException("foo", "bar");
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vTable = new Variant<String>()
			.add("foo")
			.add("bar");
		Variant<String> vField = new Variant<String>(vTable)
			.add("bar")
			.add("foo");
		Variant<?> iterator = vField;
		int foundCnt = 0;
		NotAllRequiredFieldsException x = null, found = null;
		do {
			x = new NotAllRequiredFieldsException(vTable.get(), vField.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getTableName());
		assertEquals("bar", found.getFieldName());
	}

}
