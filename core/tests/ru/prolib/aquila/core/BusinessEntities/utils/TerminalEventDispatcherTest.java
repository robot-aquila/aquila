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
		control.replay();
		
		dispatcher.fireConnected();
		
		control.verify();
	}
	
	@Test
	public void testFireDisconnected() throws Exception {
		dispatcher.OnDisconnected().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnDisconnected())), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireDisconnected();
		
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
		dispatcher.OnRequestSecurityError().addListener(listener);
		queue.enqueue(eq(new EventImpl(dispatcher.OnRequestSecurityError())), 
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireSecurityRequestError(descr, 500, "test");
		
		control.verify();
	}

}
