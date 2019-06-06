package ru.prolib.aquila.core.config;

import static org.junit.Assert.*;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreIni;

public class KVStoreIniTest {
	private KVStoreIni service;

	@Before
	public void setUp() throws Exception {
		Section section = new Ini().add("foobar");
		section.add("help", null);
		section.add("data-dir", "/foo/bar");
		service = new KVStoreIni(section);
	}
	
	@Test
	public void testHasKey() {
		assertFalse(service.hasKey("foobar"));
		assertTrue(service.hasKey("help"));
		assertTrue(service.hasKey("data-dir"));
		assertFalse(service.hasKey("zulu50"));
	}

	@Test
	public void testGet() {
		assertNull(service.get("foobar"));
		assertNull(service.get("help"));
		assertEquals("/foo/bar", service.get("data-dir"));
		assertNull(service.get("zulu50"));
	}

}
