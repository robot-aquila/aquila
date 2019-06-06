package ru.prolib.aquila.core.config;

import static org.junit.Assert.*;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreCli;

public class KVStoreCliTest {
	private KVStoreCli service;

	@Before
	public void setUp() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder()
				.longOpt("help")
				.build());
		options.addOption(Option.builder()
				.longOpt("data-dir")
				.hasArg()
				.build());
		String args[] = {
				"--help",
				"--data-dir", "/foo/bar",
		};
		service = new KVStoreCli(new DefaultParser().parse(options, args));
	}
	
	@Test
	public void testHasKey() {
		assertFalse(service.hasKey("foobar"));
		assertTrue(service.hasKey("help"));
		assertTrue(service.hasKey("data-dir"));
		assertFalse(service.hasKey("cucumber"));
	}

	@Test
	public void testGet() {
		assertNull(service.get("foobar"));
		assertEquals("1", service.get("help"));
		assertEquals("/foo/bar", service.get("data-dir"));
		assertNull(service.get("charlie"));
	}
	
	@Test
	public void testGet_SpecialCase_BooleanOption() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder().longOpt("enable").build());
		String args[] = { "--enable", };
		
		service = new KVStoreCli(new DefaultParser().parse(options, args));
		
		assertEquals("1", service.get("enable"));
	}

}
