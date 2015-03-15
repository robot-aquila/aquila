package ru.prolib.aquila.t2q.jqt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.JQTrans.JQTransServer;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QException;

/**
 * 2013-01-31<br>
 * $Id: JQTServiceTest.java 531 2013-02-19 15:26:34Z whirlwind $
 */
public class JQTServiceTest {
	private static IMocksControl control;
	private static JQTHandler handler;
	private static JQTransServer server;
	private static JQTService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handler = control.createMock(JQTHandler.class);
		server = control.createMock(JQTransServer.class);
		service = new JQTService(handler, server);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(handler, service.getHandler());
		assertSame(server, service.getServer());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<JQTHandler> vHandler = new Variant<JQTHandler>()
			.add(control.createMock(JQTHandler.class))
			.add(handler);
		Variant<JQTransServer> vServer = new Variant<JQTransServer>(vHandler)
			.add(server)
			.add(control.createMock(JQTransServer.class));
		Variant<?> iterator = vServer;
		int foundCnt = 0;
		JQTService found = null, x = null;
		do {
			x = new JQTService(vHandler.get(), vServer.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(handler, found.getHandler());
		assertSame(server, found.getServer());
	}
	
	@Test
	public void testConnect_Ok() throws Exception {
		server.connect(eq("zulu15"));
		control.replay();
		service.connect("zulu15");
		control.verify();
	}
	
	@Test
	public void testConnect_IfServerThrows() throws Exception {
		Exception expected = new Exception("Test exception");
		server.connect(eq("zulu15"));
		expectLastCall().andThrow(expected);
		control.replay();
		try {
			service.connect("zulu15");
			fail("Expected exception: " + T2QException.class.getSimpleName());
		} catch ( T2QException e ) {
			assertSame(expected, e.getCause());
		}
		control.verify();
	}
	
	@Test
	public void testDisconnect_Ok() throws Exception {
		server.disconnect();
		control.replay();
		
		service.disconnect();
		
		control.verify();
	}
	
	@Test
	public void testDisconnect_IfServerThrows() throws Exception {
		Exception expected = new Exception("Test exception");
		server.disconnect();
		expectLastCall().andThrow(expected);
		control.replay();
		service.disconnect();
		control.verify();
	}
	
	@Test
	public void testSend_Ok() throws Exception {
		server.send("ZULU;KAPPA");
		control.replay();
		
		service.send("ZULU;KAPPA");
		
		control.verify();
	}

	@Test
	public void testSend_IfServerThrows() throws Exception {
		Exception expected = new Exception("Test exception");
		server.send("ZULU;KAPPA54");
		expectLastCall().andThrow(expected);
		control.replay();
		try {
			service.send("ZULU;KAPPA54");
			fail("Expected exception: " + T2QException.class.getSimpleName());
		} catch ( T2QException e ) {
			assertSame(expected, e.getCause());
		}
		control.verify();
	}
	
}
