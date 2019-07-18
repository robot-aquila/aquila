package ru.prolib.aquila.core.config;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreHash;

public class KVStoreHashTest {
	private Map<String, String> data;
	private KVStoreHash service;

	@Before
	public void setUp() throws Exception {
		data = new HashMap<>();
		service = new KVStoreHash(data);
	}
	
	@Test
	public void testHasKey() {
		data.put("foo", "bar");
		data.put("zulu24", "charlie");
		
		assertFalse(service.hasKey("zoo"));
		assertTrue(service.hasKey("foo"));
		assertTrue(service.hasKey("zulu24"));
		assertFalse(service.hasKey("bazooka"));
	}

	@Test
	public void testGet() {
		data.put("foo", "bar");
		data.put("zulu24", "charlie");
		
		assertNull(service.get("zoo"));
		assertEquals("bar", service.get("foo"));
		assertEquals("charlie", service.get("zulu24"));
		assertNull(service.get("bazooka"));
	}
	
	@Test
	public void testAdd() throws Exception {
		assertSame(service, service.add("foo", "bar"));
		assertSame(service, service.add("key", "val"));

		Map<String, String> expected = new HashMap<>();
		expected.put("foo", "bar");
		expected.put("key", "val");
		assertEquals(expected, data);
	}
	
	@Test (expected=ConfigException.class)
	public void testAdd_ThrowsIfAlreadyExists() throws Exception {
		data.put("key", "value");
		
		service.add("key", "value");
	}

}
