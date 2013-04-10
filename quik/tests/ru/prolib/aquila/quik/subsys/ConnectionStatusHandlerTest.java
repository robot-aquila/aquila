package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.ConnEvent;
import ru.prolib.aquila.t2q.T2QConnStatus;

public class ConnectionStatusHandlerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private EventType type;
	private ConnectionStatusHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		type = control.createMock(EventType.class);
		handler = new ConnectionStatusHandler(terminal);
		expect(type.asString()).andStubReturn("test");
	}
	
	@Test
	public void testOnEvent_DllConnected() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		
		handler.onEvent(new ConnEvent(type, T2QConnStatus.DLL_CONN));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_DllDisconnected() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		control.replay();
		
		handler.onEvent(new ConnEvent(type, T2QConnStatus.DLL_DISC));
		
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
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		ConnectionStatusHandler x = null, found = null;
		do {
			x = new ConnectionStatusHandler(vTerm.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.terminal);
	}

}
