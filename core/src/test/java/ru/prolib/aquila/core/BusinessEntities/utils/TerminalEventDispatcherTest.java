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
		EventDispatcher ed = es.createEventDispatcher("Terminal");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnConnected(), ed.createType("Connected"));
		assertEquals(dispatcher.OnDisconnected(), ed.createType("Disconnected"));
		assertEquals(dispatcher.OnPanic(), ed.createType("Panic"));
		assertEquals(dispatcher.OnRequestSecurityError(),
				ed.createType("RequestSecurityError"));
		assertEquals(dispatcher.OnStarted(), ed.createType("Started"));
		assertEquals(dispatcher.OnStopped(), ed.createType("Stopped"));
	}
	
	@Test
	public void testFireConnected() throws Exception {
		dispatcher.OnConnected().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnConnected())),
				same(dispatcher.getEventDispatcher()));
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
		queue.enqueue(eq(new EventImpl(dispatcher.OnDisconnected())), 
				same(dispatcher.getEventDispatcher()));
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
		queue.enqueue(eq(new EventImpl(dispatcher.OnStarted())), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireStarted();
		
		control.verify();
	}
	
	@Test
	public void testFireStopped() throws Exception {
		dispatcher.OnStopped().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnStopped())), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireStopped();
		
		control.verify();
	}
	
	@Test
	public void testFirePanic2() throws Exception {
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent(dispatcher.OnPanic(), 80, "test")), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.firePanic(80, "test");
		
		control.verify();
	}

	@Test
	public void testFirePanic3() throws Exception {
		Object args[] = { "foo", 23 };
		dispatcher.OnPanic().addListener(listener);
		queue.enqueue(eq(new PanicEvent(dispatcher.OnPanic(), 13, "foo", args)), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.firePanic(13, "foo", args);
		
		control.verify();
	}

	@Test
	public void testRequestSecurityError() throws Exception {
		EventType type = dispatcher.OnRequestSecurityError(); 
		type.addListener(listener);
		queue.enqueue(eq(new RequestSecurityEvent(type, descr, 500, "test")), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireSecurityRequestError(descr, 500, "test");
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe() throws Exception {
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
