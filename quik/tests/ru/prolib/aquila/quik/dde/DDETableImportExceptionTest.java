package ru.prolib.aquila.quik.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class DDETableImportExceptionTest {
	private DDETableImportException exception;
	private Exception exc1 = new Exception("Test message");

	@Before
	public void setUp() throws Exception {
		exception = new DDETableImportException("foobar", exc1);
	}
	
	@Test
	public void testGetMessage() throws Exception {
		assertEquals("Table [foobar]: Test message", exception.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vTbl = new Variant<String>()
			.add("foobar")
			.add("another");
		Variant<Exception> vExc = new Variant<Exception>(vTbl)
			.add(exc1)
			.add(new Exception("Another error"));
		Variant<?> iterator = vExc;
		int foundCnt = 0;
		DDETableImportException x = null, found = null;
		do {
			x = new DDETableImportException(vTbl.get(), vExc.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getTableName());
		assertSame(exc1, found.getCause());
	}

}
