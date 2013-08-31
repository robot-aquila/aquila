package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class SecuritiesEventDispatcherTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private Terminal terminal;
	private Security security;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private SecuritiesEventDispatcher dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("RI", "SPBFUT", "USD", SecurityType.FUT);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		queue = control.createMock(EventQueue.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		security = new SecurityImpl(terminal, descr,
				new SecurityEventDispatcher(es, descr));
		dispatcher = new SecuritiesEventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Securities");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
		assertEquals(dispatcher.OnAvailable(), ed.createType("Available"));
		assertEquals(dispatcher.OnTrade(), ed.createType("Trade"));
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		dispatcher.OnAvailable().addListener(listener);
		queue.enqueue(eq(new SecurityEvent(dispatcher.OnAvailable(), security)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireAvailable(security);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new SecurityEvent(dispatcher.OnChanged(), security)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new SecurityEvent(security.OnChanged(), security));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade trade = control.createMock(Trade.class);
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new SecurityTradeEvent(dispatcher.OnTrade(), security,
				trade)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new SecurityTradeEvent(security.OnTrade(), security,
				trade));
		
		control.verify();
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(security);
		
		assertTrue(security.OnChanged().isListener(dispatcher));
		assertTrue(security.OnTrade().isListener(dispatcher));
	}
	
	@Test
	public void testStopRelayFor() throws Exception {
		security.OnChanged().addListener(dispatcher);
		security.OnTrade().addListener(dispatcher);
		
		dispatcher.stopRelayFor(security);
		
		assertFalse(security.OnChanged().isListener(dispatcher));
		assertFalse(security.OnTrade().isListener(dispatcher));
	}

}
