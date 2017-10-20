package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;

/**
 * 2013-01-30<br>
 * $Id: DDEServerStarterTest.java 517 2013-02-12 00:37:18Z whirlwind $
 */
public class DDEServerStarterTest {
	private IMocksControl control;
	private DDEServer server;
	private DDEServerStarter starter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		server = control.createMock(DDEServer.class);
		starter = new DDEServerStarter(server);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(server, starter.getServer());
	}
	
	@Test
	public void testStart_Ok() throws Exception {
		server.start();
		control.replay();
		
		starter.start();
		
		control.verify();
	}
	
	@Test (expected=StarterException.class)
	public void testStart_IfThrownAtStart() throws Exception {
		DDEException expected = new DDEException("Test exception");
		server.start();
		expectLastCall().andThrow(expected);
		control.replay();
		try {
			starter.start();
		} catch ( StarterException e ) {
			assertSame(expected, e.getCause());
			control.verify();
			throw e;
		}
	}
	
	@Test
	public void testStop_Ok() throws Exception {
		server.stop();
		server.join();
		control.replay();
		
		starter.stop();
		
		control.verify();
	}
	
	@Test (expected=StarterException.class)
	public void testStop_IfThrownAtStop() throws Exception {
		DDEException expected = new DDEException("Test exception");
		server.stop();
		expectLastCall().andThrow(expected);
		control.replay();
		try {
			starter.stop();
		} catch ( StarterException e ) {
			assertSame(expected, e.getCause());
			control.verify();
			throw e;
		}
	}

	@Test (expected=StarterException.class)
	public void testStop_IfThrownAtJoin() throws Exception {
		DDEException expected = new DDEException("Test exception");
		server.stop();
		server.join();
		expectLastCall().andThrow(expected);
		control.replay();
		try {
			starter.stop();
		} catch ( StarterException e ) {
			assertSame(expected, e.getCause());
			control.verify();
			throw e;
		}
	}

	@Test
	public void testEquals() throws Exception {
		DDEServer server2 = control.createMock(DDEServer.class);
		assertTrue(starter.equals(starter));
		assertTrue(starter.equals(new DDEServerStarter(server)));
		assertFalse(starter.equals(null));
		assertFalse(starter.equals(this));
		assertFalse(starter.equals(new DDEServerStarter(server2)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130131, 204231)
			.append(server)
			.toHashCode(), starter.hashCode());
	}

}
