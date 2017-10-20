package ru.prolib.aquila.core.data.row;


import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Map;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class SimpleRowTest {
	private static String[] headers = { "foo", "bar", "buz" };
	private static Object[] values = { 1, true, "jubba" };
	private Map<String, Object> map;
	private SimpleRow row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		map = new Hashtable<String, Object>();
		for ( int i = 0; i < headers.length; i ++ ) {
			map.put(headers[i], values[i]);
		}
		row = new SimpleRow(map);
	}
	
	@Test
	public void testMakeMap() throws Exception {
		assertEquals(map, SimpleRow.makeMap(headers, values));
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals(1, row.get("foo"));
		assertTrue((Boolean) row.get("bar"));
		assertEquals("jubba", row.get("buz"));
	}
	
	@Test
	public void testConstruct2() throws Exception {
		row = new SimpleRow(headers, values);
		assertEquals(1, row.get("foo"));
		assertTrue((Boolean) row.get("bar"));
		assertEquals("jubba", row.get("buz"));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Map<String, Object> map2 = new Hashtable<String, Object>();
		map2.put("foo",	0);
		map2.put("zulu", 1);
		Variant<Map<String, ?>> vMap = new Variant<Map<String, ?>>()
			.add(map)
			.add(map2);
		Variant<?> iterator = vMap;
		int foundCnt = 0;
		SimpleRow x = null, found = null;
		do {
			x = new SimpleRow(vMap.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		for ( int i = 0; i < headers.length; i ++ ) {
			assertEquals(values[i], found.get(headers[i]));
		}
	}
	
	@Test
	public void testGetRowCopy() throws Exception {
		Row copy = row.getRowCopy();
		assertNotNull(copy);
		assertNotSame(row, copy);
		assertEquals(row, copy);
		// changes in source hash not affected to copy
		map.put("foo", 256);
		map.remove("bar");
		assertEquals(256, row.get("foo"));
		assertNull(row.get("bar"));
		assertEquals(1, copy.get("foo"));
		assertTrue((Boolean) copy.get("bar"));
	}

}
