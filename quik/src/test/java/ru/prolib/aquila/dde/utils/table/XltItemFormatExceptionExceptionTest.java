package ru.prolib.aquila.dde.utils.table;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class XltItemFormatExceptionExceptionTest {
	private XltItemFormatException exception;

	@Before
	public void setUp() throws Exception {
		exception = new XltItemFormatException("table", "zulu45");
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
			.add("table")
			.add("bable");
		Variant<String> vItem = new Variant<String>(vTable)
			.add("zulu45")
			.add("R1C1:R1C5");
		Variant<?> iterator = vItem;
		int foundCnt = 0;
		XltItemFormatException x = null, found = null;
		do {
			x = new XltItemFormatException(vTable.get(), vItem.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("table", found.getTableName());
		assertEquals("zulu45", found.getItem());
	}

}
