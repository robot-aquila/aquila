package ru.prolib.aquila.dde;

import static org.junit.Assert.*;

import org.junit.*;

public class DDETableImplTest {
	private Object[] array;
	private DDETableImpl table;
	
	@Before
	public void setUp() throws Exception {
		array = new Object[24];
		array[3 * 0 + 1] = new String("zulu4");
		array[3 * 2 + 0] = new Integer(54321);
		array[3 * 4 + 0] = new Boolean(true);
		array[3 * 7 + 2] = new Float(123.45f);
		table = new DDETableImpl(array, "foo", "bar", 3);
	}

	@Test
	public void testGetCell_Ok() throws Exception {
		assertNull(table.getCell(0, 0));
		assertEquals(new String("zulu4"), table.getCell(0, 1));
		assertEquals(new Integer(54321), table.getCell(2, 0));
		assertEquals(new Boolean(true), table.getCell(4, 0));
		assertEquals(new Float(123.45f), table.getCell(7, 2));
	}
	
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetCell_ThrowsOutOfRange() throws Exception {
		table.getCell(300, 10);
	}

	@Test
	public void testGetCols() {
		assertEquals(3, table.getCols());
	}

	@Test
	public void testGetItem() {
		assertEquals("bar", table.getItem());
	}

	@Test
	public void testGetRows() {
		assertEquals(8, table.getRows());
	}

	@Test
	public void testGetTopic() {
		assertEquals("foo", table.getTopic());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		table = new DDETableImpl();
		assertEquals(0, table.getCols());
		assertEquals(0, table.getRows());
		assertEquals("", table.getTopic());
		assertEquals("", table.getItem());
	}

}
