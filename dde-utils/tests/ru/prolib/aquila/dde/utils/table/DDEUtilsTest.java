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
		assertEquals(new DDETableRange(1, 2, 5, 2),
				utils.parseXltRange("R1C2:R5C2"));
		assertEquals(new DDETableRange(20, 10, 50, 12),
				utils.parseXltRange("R20C10:R50C12"));
	}
	
	@Test (expected=ParseException.class)
	public void testParseXltRange_ThrowsIfStringIncorrect() throws Exception {
		utils.parseXltRange("R1234C3332:zulakdnoas");
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

}
