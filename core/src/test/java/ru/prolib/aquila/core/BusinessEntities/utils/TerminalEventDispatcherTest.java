package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class TerminalEventDispatcherTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private Terminal terminal;
	private TerminalObserver observer1, observer2, observer3;
	private TerminalEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RI", "SPBFUT", "USD", SecurityType.FUT);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		listener = control.createMock(EventListener.class);
		terminal = control.createMock(Terminal.class);
		observer1 = control.createMock(TerminalObserver.class);
		observer2 = control.createMock(TerminalObserver.class);
		observer3 = control.createMock(TerminalObserver.class);
		es = new EventSystemImpl(queue);
		dispatcher = new TerminalEventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("Terminal", ed.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnConnected();
		assertEquals("Terminal.Connected", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnDisconnected();
		assertEquals("Terminal.Disconnected", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnPanic();
		assertEquals("Terminal.Panic", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnRequestSecurityError();
		assertEquals("Terminal.RequestSecurityError", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnStarted();
		assertEquals("Terminal.Started", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = (EventTypeSI) dispatcher.OnStopped();
		assertEquals("Terminal.Stopped", type.getId());
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireConnected() throws Exception {
		dispatcher.OnConnected().addListener(listener);
		queue.enqueue(eq(new EventImpl((EventTypeSI) dispatcher.OnConnected())));
		observer1.OnTerminalReady(terminal);
		observer2.OnTerminalReady(terminal);
		observer3.OnTerminalReady(terminal);
		control.replay();
		dispatcher.subscribe(observer1);
		dispatcher.subscribe(observer2);
		dispatcher.subscribe(observer3);
		
		dispatcher.fireConnected(terminal);
		
		control.verify();
	}
	
	@Test
	public void testFireDisconnected() throws Exception {
		dispatcher.OnDisconnected().addListener(listener);
		queue.enqueue(eq(new EventImpl((EventTypeSI) dispatcher.OnDisconnected())));
		observer1.OnTerminalUnready(terminal);
		observer2.OnTerminalUnready(terminal);
		observer3.OnTerminalUnready(terminal);
		control.replay();
		dispatcher.subscribe(observer1);
		dispatcher.subscribe(observer2);
		dispatcher.subscribe(observer3);
			
		dispatcher.fireDisconnected(terminal);
		
		control.verify();
	}
	
	@Test
	public void testFireStarted() throws Exception {
		dispatcher.OnStarted().addListener(listener);
		queue.enqueue(eq(new EventImpl((EventTypeSI) dispatcher.OnStarted())));
		control.replay();
		
		dispatcher.fireStarted();
		
		control.verify();
	}
	
	@Test
	public void testFireStopped() throws Exception {
		dispatcher.OnStopped().addListener(listener);
		queue.enqueue(eq(new EventImpl((EventTypeSI) dispatcher.OnStopped())));
		control.replay();
		
		dispatcher.fireStopped();
		
		control.verify();
	}
	
	@Test
	public void testFirePanic2() throws Exception {
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent((EventTypeSI) dispatcher.OnPanic(), 80, "test")));
		control.replay();
		
		dispatcher.firePanic(80, "test");
		
		control.verify();
	}

	@Test
	public void testFirePanic3() throws Exception {
		Object args[] = { "foo", 23 };
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent((EventTypeSI) dispatcher.OnPanic(), 13, "foo", args)));
		control.replay();
		
		dispatcher.firePanic(13, "foo", args);
		
		control.verify();
	}

	@Test
	public void testRequestSecurityError() throws Exception {
		EventTypeSI type = (EventTypeSI) dispatcher.OnRequestSecurityError(); 
		type.addListener(listener);
		queue.enqueue(eq(new RequestSecurityEvent(type, descr, 500, "test")));
		control.replay();
		
		dispatcher.fireSecurityRequestError(descr, 500, "test");
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe() throws Exception {
		queue.enqueue(new EventImpl((EventTypeSI) dispatcher.OnDisconnected()));
		observer3.OnTerminalUnready(terminal);
		control.replay();
		dispatcher.subscribe(observer1);
		dispatcher.subscribe(observer2);
		dispatcher.subscribe(observer3);
		dispatcher.unsubscribe(observer1);
		dispatcher.unsubscribe(observer2);
			
		dispatcher.fireDisconnected(terminal);
		
		control.verify();
	}

}
