package ru.prolib.aquila.ib.subsys.api;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.ib.api.IBConfig;

/**
 * 2012-11-25<br>
 * $Id: IBConfigTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBConfigTest {
	private IBConfig config;

	@Test
	public void testConstruct0() throws Exception {
		config = new IBConfig();
		assertNull(config.getHost());
		assertEquals(4001, config.getPort());
		assertEquals(0, config.getClientId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		config = new IBConfig(12345);
		assertNull(config.getHost());
		assertEquals(12345, config.getPort());
		assertEquals(0, config.getClientId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		config = new IBConfig("127.0.0.1", 12345);
		assertEquals("127.0.0.1", config.getHost());
		assertEquals(12345, config.getPort());
		assertEquals(0, config.getClientId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		config = new IBConfig("127.0.0.1", 12345, 8567);
		assertEquals("127.0.0.1", config.getHost());
		assertEquals(12345, config.getPort());
		assertEquals(8567, config.getClientId());
	}

}
