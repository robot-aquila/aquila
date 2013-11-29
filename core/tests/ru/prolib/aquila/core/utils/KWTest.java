package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import java.util.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.KW;

public class KWTest {

	@Test
	public void testEquals() throws Exception {
		Integer v1 = 300, v2 = 300, v3 = 123;
		KW<Integer> w1 = new KW<Integer>(v1),
			w2 = new KW<Integer>(v2),
			w3 = new KW<Integer>(v3);
		
		assertTrue(v1.equals(v2));
		assertFalse(w1.equals(w2));
		assertEquals(w1.instance(), w2.instance());
		
		assertFalse(v1.equals(v3));
		assertFalse(w1.equals(w3));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		KW<Integer> w1 = new KW<Integer>(1234);
		assertFalse(w1.equals(null));
		assertFalse(w1.equals(this));		
	}
	
	@Test
	public void testHashCode() throws Exception {
		KW<KWTest> w1 = new KW<KWTest>(this), w2 = new KW<KWTest>(this);
		assertNotSame(w1, w2);
		assertEquals(w1.hashCode(), w2.hashCode());
		assertSame(w1.instance(), w2.instance());
		assertEquals(w1.hashCode(),
				new HashCodeBuilder().append(this).toHashCode());
	}
	
	@Test
	public void testForSets() throws Exception {
		Integer v1 = 300, v2 = 300, v3 = 123;
		Set<KW<Integer>> set = new HashSet<KW<Integer>>();
		set.add(new KW<Integer>(v1));
		set.add(new KW<Integer>(v1)); // skip dup
		set.add(new KW<Integer>(v2));
		set.add(new KW<Integer>(v3));
		
		assertEquals(3, set.size());
		assertTrue(set.contains(new KW<Integer>(v1)));
		assertTrue(set.contains(new KW<Integer>(v2)));
		assertTrue(set.contains(new KW<Integer>(v3)));
	}
	
	@Test
	public void testForLists() throws Exception {
		Integer v1 = 300, v2 = 300, v3 = 123;
		List<KW<Integer>> list = new Vector<KW<Integer>>();
		list.add(new KW<Integer>(v1));
		list.add(new KW<Integer>(v2));
		list.add(new KW<Integer>(v3));
		
		assertTrue(list.contains(new KW<Integer>(v1)));
		assertTrue(list.contains(new KW<Integer>(v2)));
		assertTrue(list.contains(new KW<Integer>(v3)));
	}
	
	@Test
	public void testForMaps() throws Exception {
		Integer v1 = 300, v2 = 300, v3 = 123;
		Map<KW<Integer>, String> map = new HashMap<KW<Integer>, String>();
		map.put(new KW<Integer>(v1), "foo");
		map.put(new KW<Integer>(v2), "bar");
		map.put(new KW<Integer>(v3), "buz");
		map.put(new KW<Integer>(v1), "paz");
		
		assertEquals(3, map.size());
		assertEquals("paz", map.get(new KW<Integer>(v1)));
		assertEquals("bar", map.get(new KW<Integer>(v2)));
		assertEquals("buz", map.get(new KW<Integer>(v3)));
	}

}
