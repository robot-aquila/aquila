package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.QUIKClient;

public class ConnectionHandlerTest {
	private IMocksControl control;
	private QUIKTerminal terminal;
	private QUIKClient client;
	private EventTypeSI onStarted, onStopped, onDisconnected;
	private QUIKConfigImpl config;
	private ConnectionHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		client = control.createMock(QUIKClient.class);
		onStarted = control.createMock(EventTypeSI.class);
		onStopped = control.createMock(EventTypeSI.class);
		onDisconnected = control.createMock(EventTypeSI.class);
		config = new QUIKConfigImpl();
		config.quikPath = "C:/work/quik";
		handler = new ConnectionHandler(terminal, config);
		
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
		Runnable task = new DoReconnect(terminal, config);
		TaskHandler th = control.createMock(TaskHandler.class);
		
		expect(terminal.schedule(eq(task),eq(0L),eq(5000L))).andReturn(th);
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
		Runnable task = new DoReconnect(terminal, config);
		TaskHandler th = control.createMock(TaskHandler.class);
		
		expect(terminal.started()).andReturn(true);
		expect(terminal.schedule(eq(task),eq(0L),eq(5000L))).andReturn(th);
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
		Variant<QUIKTerminal> vTerm = new Variant<QUIKTerminal>()
			.add(terminal)
			.add(control.createMock(QUIKTerminal.class));
		Variant<QUIKConfig> vConf = new Variant<QUIKConfig>(vTerm)
			.add(config)
			.add(new QUIKConfigImpl());
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
		assertSame(terminal, found.getTerminal());
		assertEquals(config, found.getConfig());
	}

}