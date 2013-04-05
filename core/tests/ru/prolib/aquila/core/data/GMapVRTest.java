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
 * $Id: GMapVRTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapVRTest {
	private static Map<Validator, String> map;
	private static GMapVR<String> getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		map = new HashMap<Validator, String>();
		map.put(new ValidatorEq("one"), "zippo");
		map.put(new ValidatorEq("two"), "charlie");
		getter = new GMapVR<String>(map);
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals("zippo", getter.get("one"));
		assertEquals("charlie", getter.get("two"));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		// Аналогичная карта
		Map<Validator, String> map2 = new HashMap<Validator, String>();
		map2.put(new ValidatorEq("one"), "zippo");
		map2.put(new ValidatorEq("two"), "charlie");
		assertTrue(getter.equals(new GMapVR<String>(map2)));
		map2.put(new ValidatorEq("ups"), "delta");
		assertFalse(getter.equals(new GMapVR<String>(map2)));
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GMapVR<String>(map)));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/24319)
			.append(map)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
