package ru.prolib.aquila.dde.utils.table;


import static org.junit.Assert.*;
import java.text.ParseException;
import java.util.*;

import org.junit.*;
import ru.prolib.aquila.dde.*;

public class DDEUtilsTest {
	private static DDEUtils utils;
	private DDETable table;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		utils = new DDEUtils();
	}
	
	@Test
	public void testParseXltRange() throws Exception {
		table = new DDETableImpl(new Object[] {1}, "foo", "R1C2:R5C2", 1);
		DDETableRange expected = new DDETableRange(1, 2, 5, 2); 
		assertEquals(expected, utils.parseXltRange(table));
		
		table = new DDETableImpl(new Object[] {1}, "foo", "R20C10:R50C12", 1);
		expected = new DDETableRange(20, 10, 50, 12);
		assertEquals(expected, utils.parseXltRange(table));
	}
	
	@Test
	public void testParseXltRange_ThrowsIfStringIncorrect() throws Exception {
		table = new DDETableImpl(new Object[] {1}, "foo", "R1234C33:zulu", 1);
		try {
			utils.parseXltRange(table);
			fail("Expected: " + XltItemFormatException.class.getSimpleName());
		} catch ( XltItemFormatException e ) {
			assertEquals(new XltItemFormatException("foo", "R1234C33:zulu"), e);
		}
	}
	
	@Test
	public void testMakeHeadersMap2() throws Exception {
		table = new DDETableImpl(new Object[] { "one", "two", "three", 1, 2, 3},
				"foobar", "R1C1:R10C1", 3);
		Map<String, Integer> expected = new Hashtable<String, Integer>();
		expected.put("one", 5 + 0);
		expected.put("two", 5 + 1);
		expected.put("three", 5 + 2);
		assertEquals(expected, utils.makeHeadersMap(table, 5));
	}
	
	@Test
	public void testMakeHeadersMap1() throws Exception {
		table = new DDETableImpl(new Object[] { "one", "two", "three", 1, 2, 3},
				"foobar", "R1C1:R10C1", 3);
		Map<String, Integer> expected = new Hashtable<String, Integer>();
		expected.put("one", 0);
		expected.put("two", 1);
		expected.put("three", 2);
		assertEquals(expected, utils.makeHeadersMap(table));
	}
	
	@Test
	public void testMakeHeadersMap2Req() throws Exception {
		Object cells[] = { "one", "two", "three", "four" };
		table = new DDETableImpl(cells, "foobar", "R1C1:R1C4", 4);
		String required[] = { "two", "four" };
		Map<String, Integer> expected = new Hashtable<String, Integer>();
		expected.put("two", 1);
		expected.put("four", 3);
		assertEquals(expected, utils.makeHeadersMap(table, required));
	}
	
	@Test
	public void testMakeHeadersMap2Req_ThrowsIfNoHeader() throws Exception {
		Object cells[] = { "one", "two", "three", "four" };
		table = new DDETableImpl(cells, "foobar", "R1C1:R1C4", 4);
		String required[] = { "two", "five" };
		
		try {
			utils.makeHeadersMap(table, required);
			fail("Expected: "
					+ NotAllRequiredFieldsException.class.getSimpleName());
		} catch ( NotAllRequiredFieldsException e ) {
			assertEquals(new NotAllRequiredFieldsException("foobar","five"), e);
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(utils.equals(utils));
		assertFalse(utils.equals(null));
		assertFalse(utils.equals(this));
		assertTrue(utils.equals(new DDEUtils()));
	}

}
