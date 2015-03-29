package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalReadyIfConnected;

public class TerminalReadyIfConnectedTest {
	private IMocksControl control;
	private EditableTerminal<?> terminal;
	private EventTypeSI onConnected, onDisconnected;
	private TerminalReadyIfConnected handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		onConnected = new EventTypeImpl();
		onDisconnected = new EventTypeImpl();
		handler = new TerminalReadyIfConnected(terminal);
		expect(terminal.OnConnected()).andStubReturn(onConnected);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconnected);
	}
	
	@Test
	public void testStart() throws Exception {
		control.replay();
		
		handler.start();
		
		control.verify();
		assertTrue(onConnected.isSyncListener(handler));
		assertTrue(onDisconnected.isSyncListener(handler));
	}
	
	@Test
	public void testStop() throws Exception {
		onConnected.addSyncListener(handler);
		onDisconnected.addSyncListener(handler);
		control.replay();
		
		handler.stop();
		
		control.verify();
		assertFalse(onConnected.isListener(handler));
		assertFalse(onDisconnected.isListener(handler));
	}
	
	@Test
	public void testOnEvent_OnConnected() throws Exception {
		terminal.fireTerminalReady();
		control.replay();
		
		handler.onEvent(new EventImpl(onConnected));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnDisconnected() throws Exception {
		terminal.fireTerminalUnready();
		control.replay();
		
		handler.onEvent(new EventImpl(onDisconnected));
		
		control.verify();
	}

}
