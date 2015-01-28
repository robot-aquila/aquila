package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * 2012-10-30<br>
 * $Id: GMapTGTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapTGTest {
	private static G<Integer> key;
	private static Map<Object, G<String>> map;
	private static GMapTG<String> getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		map = new HashMap<Object, G<String>>();
		map.put(100, new GConst<String>("zippo"));
		map.put(500, new GConst<String>("charlie"));
		key = new GCond<Integer>(new ValidatorEq("ABC"),
				new GConst<Integer>(100), new GConst<Integer>(500));
		getter = new GMapTG<String>(key, map);
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals("zippo", getter.get("ABC"));
		assertEquals("charlie", getter.get("UNK"));
		assertEquals("charlie", getter.get(null));
		assertEquals("charlie", getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Map<Object, G<String>> map2 = new HashMap<Object, G<String>>();
		map2.put(500, new GConst<String>("charlie"));
		map2.put(100, new GConst<String>("zippo"));
		G<Integer> key2 = new GCond<Integer>(new ValidatorEq("ABC"),
				new GConst<Integer>(100), new GConst<Integer>(500));
		Map<Object, G<String>> map3 = new HashMap<Object, G<String>>();
		G<Integer> key3 = new GConst<Integer>(1);
		
		assertTrue(getter.equals(new GMapTG<String>(key2, map2)));
		assertFalse(getter.equals(new GMapTG<String>(key2, map3)));
		assertFalse(getter.equals(new GMapTG<String>(key3, map2)));
		assertFalse(getter.equals(new GMapTG<String>(key3, map3)));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/54747)
			.append(key)
			.append(map)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
