package ru.prolib.aquila.core.data.row;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.*;

/**
 * 2013-02-15<br>
 * $Id$
 */
public class ListOfMapsTest {
	private List<Map<String, Object>> map1, map2;
	private ListOfMaps set1, set2;
	
	@Before
	public void setUp() throws Exception {
		map1 = new Vector<Map<String, Object>>();
		map1.add(new HashMap<String, Object>());
		map1.get(0).put("foo", 345);
		map1.get(0).put("bar", 890);
		map1.add(new HashMap<String, Object>());
		map1.get(1).put("foo", 725);
		map1.add(new HashMap<String, Object>());
		map1.get(2).put("foo", 210);
		map1.get(2).put("bar", 212);
		set1 = new ListOfMaps(map1);
		
		map2 = new Vector<Map<String, Object>>();
		map2.add(new HashMap<String, Object>());
		map2.get(0).put("foo", 625);
		map2.add(new HashMap<String, Object>());
		map2.get(1).put("foo", 112);
		map2.get(1).put("bar", 725);
		set2 = new ListOfMaps(map2);
	}
	
	@Test
	public void testUsage() throws Exception {
		assertTrue(set1.next());
		assertEquals(345, set1.get("foo"));
		assertEquals(890, set1.get("bar"));
		
		assertTrue(set1.next());
		assertEquals(725, set1.get("foo"));
		assertNull(set1.get("bar"));
		
		assertTrue(set1.next());
		assertEquals(210, set1.get("foo"));
		assertEquals(212, set1.get("bar"));
		
		assertFalse(set1.next());
		assertFalse(set1.next());
		set1.reset();
		
		assertTrue(set1.next());
		assertEquals(345, set1.get("foo"));
		assertEquals(890, set1.get("bar"));
		
		set1.close(); // same as reset
		assertTrue(set1.next());
		assertEquals(345, set1.get("foo"));
		assertEquals(890, set1.get("bar"));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(set1.equals(set1));
		assertFalse(set1.equals(null));
		assertFalse(set1.equals(this));
		assertTrue(set1.equals(new ListOfMaps(map1)));
		assertFalse(set1.equals(set2));
	}
	
	@Test
	public void testEquals_IteratorAffected() throws Exception {
		set2 = new ListOfMaps(map1);
		
		assertTrue(set1.equals(set2));
		set2.next();
		assertFalse(set1.equals(set2));
		set1.next();
		assertTrue(set1.equals(set2));
		set2.reset();
		assertFalse(set1.equals(set2));
	}
	
}
