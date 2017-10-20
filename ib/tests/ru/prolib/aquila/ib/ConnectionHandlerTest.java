package ru.prolib.aquila.ib;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Timer;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.api.*;

public class ConnectionHandlerTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private IBClient client;
	private EventType onStarted, onStopped, onDisconnected;
	private Timer timer;
	private IBConfig config;
	private ConnectionHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		client = control.createMock(IBClient.class);
		onStarted = control.createMock(EventType.class);
		onStopped = control.createMock(EventType.class);
		onDisconnected = control.createMock(EventType.class);
		timer = control.createMock(Timer.class);
		config = new IBConfig("localhost", 4001, 0);
		handler = new ConnectionHandler(terminal, config, timer);
		
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconnected);
		expect(terminal.getClient()).andStubReturn(client);
	}
	
	@Test
	public void testStart() throws Exception {
		onStarted.addListener(handler);
		onStopped.addListener(handler);
		onDisconnected.addListener(handler);
		control.replay();
		
		handler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		onStarted.removeListener(handler);
		onStopped.removeListener(handler);
		onDisconnected.removeListener(handler);
		client.disconnect();
		control.replay();
		
		handler.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStarted() throws Exception {
		timer.schedule(eq(new DoReconnect(terminal, config)),eq(0L),eq(5000L));
		control.replay();
		
		handler.onEvent(new EventImpl(onStarted));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnDisconnected_NotStarted() throws Exception {
		expect(terminal.started()).andReturn(false);
		control.replay();
		
		handler.onEvent(new EventImpl(onDisconnected));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnDisconnected_Started() throws Exception {
		expect(terminal.started()).andReturn(true);
		timer.schedule(eq(new DoReconnect(terminal, config)),eq(0L),eq(5000L));
		control.replay();
		
		handler.onEvent(new EventImpl(onDisconnected));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnStopped() throws Exception {
		client.disconnect();
		control.replay();
		
		handler.onEvent(new EventImpl(onStopped));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		IBTerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = (IBEditableTerminal) tb.createTerminal("foo");
		IBEditableTerminal t2 = (IBEditableTerminal) tb.createTerminal("foo");
		handler = new ConnectionHandler(t1, config);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<IBConfig> vConf = new Variant<IBConfig>(vTerm)
			.add(config)
			.add(new IBConfig("127.0.0.1", 800, 1));
		Variant<?> iterator = vConf;
		int foundCnt = 0;
		ConnectionHandler x, found = null;
		do {
			x = new ConnectionHandler(vTerm.get(), vConf.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertEquals(config, found.getConfig());
	}

}
