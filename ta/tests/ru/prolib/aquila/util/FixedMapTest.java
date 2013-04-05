package ru.prolib.aquila.util;


import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.util.FixedMap;

/**
 * 2012-05-14<br>
 * $Id: FixedMapTest.java 216 2012-05-14 16:13:13Z whirlwind $
 */
public class FixedMapTest {
	FixedMap<Long, Integer> cache;

	@Before
	public void setUp() throws Exception {
		cache = new FixedMap<Long, Integer>(3);
	}
	
	@Test
	public void testLimitedSize() throws Exception {
		assertEquals(0, cache.size());
		cache.put(12345L, 100);
		assertEquals(1, cache.size());
		assertEquals(100, (int)cache.get(12345L));
		
		cache.put(10000L, 200);
		assertEquals(2, cache.size());
		assertEquals(100, (int)cache.get(12345L));
		assertEquals(200, (int)cache.get(10000L));
		
		cache.put(50000L, 300);
		assertEquals(3, cache.size());
		assertEquals(100, (int)cache.get(12345L));
		assertEquals(200, (int)cache.get(10000L));
		assertEquals(300, (int)cache.get(50000L));
		
		cache.put(12345L, 111); // replace
		assertEquals(3, cache.size());
		assertEquals(111, (int)cache.get(12345L));
		assertEquals(200, (int)cache.get(10000L));
		assertEquals(300, (int)cache.get(50000L));
		
		cache.put(88888L, 222); // new key-value
		assertEquals(3, cache.size());
		assertEquals(200, (int)cache.get(10000L));
		assertEquals(300, (int)cache.get(50000L));
		assertEquals(222, (int)cache.get(88888L));
		assertNull(cache.get(12345L));
	}

}
