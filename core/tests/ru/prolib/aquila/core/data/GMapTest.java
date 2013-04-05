package ru.prolib.aquila.core.data;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * 2012-10-30<br>
 * $Id: GMapTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapTest {
	private static Map<Validator, G<String>> map; 
	private static GMap<String> getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		map = new HashMap<Validator, G<String>>();
		map.put(new ValidatorEq("one"), new GConst<String>("gamma"));
		map.put(new ValidatorEq("two"), new GConst<String>("zulu"));
		getter = new GMap<String>(map);
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals("gamma", getter.get("one"));
		assertEquals("zulu", getter.get("two"));
		assertNull(getter.get("three"));
	}
	
	@Test
	public void testEquals() throws Exception {
		// Аналогичная карта
		Map<Validator, G<String>> map2 = new HashMap<Validator, G<String>>();
		map2.put(new ValidatorEq("two"), new GConst<String>("zulu"));
		map2.put(new ValidatorEq("one"), new GConst<String>("gamma"));
		assertTrue(getter.equals(new GMap<String>(map2)));
		map2.put(new ValidatorEq("boo"), new GConst<String>("sigma"));
		assertFalse(getter.equals(new GMap<String>(map2)));
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/54447)
			.append(map)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
