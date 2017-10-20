package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;
import ru.prolib.aquila.dde.DDEService;

/**
 * 2012-08-18<br>
 * $Id: DDEServiceStarterTest.java 517 2013-02-12 00:37:18Z whirlwind $
 */
public class DDEServiceStarterTest {
	private IMocksControl control;
	private DDEServer server;
	private DDEService service;
	private DDEServiceStarter starter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		server = control.createMock(DDEServer.class);
		service = control.createMock(DDEService.class);
		starter = new DDEServiceStarter(server, service);
		expect(service.getName()).andStubReturn("HELLO");
	}
	
	@Test
	public void testConstruct() throws Exception {
		Object fixture[][] = {
				// server, service, exception?
				{ server, service, false },
				{ null,   service, true  },
				{ server, null,    true  },
				{ null,   null,    true  }
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			server = (DDEServer) fixture[i][0];
			service = (DDEService) fixture[i][1];
			boolean exception = false;
			try {
				starter = new DDEServiceStarter(server, service);
			} catch ( NullPointerException e ) {
				exception = true;
			}
			assertEquals(msg, (Boolean) fixture[i][2], exception);
			if ( exception == false ) {
				assertSame(msg, server, starter.getServer());
				assertSame(msg, service, starter.getService());
			}
		}
	}
	
	@Test
	public void testStart_Ok() throws Exception {
		server.registerService(same(service));
		control.replay();
		
		starter.start();
		
		control.verify();
	}
	
	@Test (expected=StarterException.class)
	public void testStart_IfThrownAtRegisterService() throws Exception {
		DDEException expected = new DDEException("Test exception");
		server.registerService(same(service));
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
		server.unregisterService(eq("HELLO"));
		control.replay();
		
		starter.stop();
		
		control.verify();
	}
	
	@Test (expected=StarterException.class)
	public void testStop_IfThrownAtUnregisterService() throws Exception {
		DDEException expected = new DDEException("Test exception");
		server.unregisterService(eq("HELLO"));
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
		Variant<DDEServer> vServer = new Variant<DDEServer>()
			.add(server)
			.add(control.createMock(DDEServer.class));
		Variant<DDEService> vService = new Variant<DDEService>(vServer)
			.add(service)
			.add(control.createMock(DDEService.class));
		int foundCnt = 0;
		DDEServiceStarter found = null, x = null;
		do {
			x = new DDEServiceStarter(vServer.get(), vService.get());
			if ( starter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vService.next() );
		assertEquals(1, foundCnt);
		assertSame(server, found.getServer());
		assertSame(service, found.getService());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(starter.equals(starter));
		assertFalse(starter.equals(null));
		assertFalse(starter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/62813)
			.append(server)
			.append(service)
			.toHashCode();
		assertEquals(hashCode, starter.hashCode());
	}

}
