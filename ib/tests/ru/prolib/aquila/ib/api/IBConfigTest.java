package ru.prolib.aquila.ib.api;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.api.IBConfig;

/**
 * 2012-11-25<br>
 * $Id: IBConfigTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBConfigTest {
	private IBConfig config;
	
	@Before
	public void setUp() throws Exception {
		config = new IBConfig("127.0.0.1", 12345, 8567);
	}

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
		assertEquals("127.0.0.1", config.getHost());
		assertEquals(12345, config.getPort());
		assertEquals(8567, config.getClientId());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(config.equals(config));
		assertFalse(config.equals(null));
		assertFalse(config.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vHost = new Variant<String>()
			.add("127.0.0.1")
			.add("192.168.1.1");
		Variant<Integer> vPort = new Variant<Integer>(vHost)
			.add(12345)
			.add(88123);
		Variant<Integer> vId = new Variant<Integer>(vPort)
			.add(8567)
			.add(0);
		Variant<?> iterator = vId;
		int foundCnt = 0;
		IBConfig x, found = null;
		do {
			x = new IBConfig(vHost.get(), vPort.get(), vId.get());
			if ( config.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("127.0.0.1", found.getHost());
		assertEquals(12345, found.getPort());
		assertEquals(8567, found.getClientId());
	}

}
