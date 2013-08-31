package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Vector;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;


public class OrdersEventDispatcherTest {
	private IMocksControl control;
	private Terminal terminal;
	private OrderImpl order;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private OrdersEventDispatcher dispatcher;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		es = new EventSystemImpl(queue);
		terminal = control.createMock(Terminal.class);
		order = new OrderImpl(new OrderEventDispatcher(es),
				new Vector<OrderStateHandler>(), terminal);
		listener = control.createMock(EventListener.class);
		dispatcher = new OrdersEventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Orders");
		assertEquals(dispatcher.getEventDispatcher(), ed);
		assertEquals(dispatcher.OnAvailable(), ed.createType("Available"));
		assertEquals(dispatcher.OnRegistered(), ed.createType("Registered"));
		assertEquals(dispatcher.OnRegisterFailed(), ed.createType("RegisterFailed"));
		assertEquals(dispatcher.OnCancelled(), ed.createType("Cancelled"));
		assertEquals(dispatcher.OnCancelFailed(), ed.createType("CancelFailed"));
		assertEquals(dispatcher.OnFilled(), ed.createType("Filled"));
		assertEquals(dispatcher.OnPartiallyFilled(), ed.createType("PartiallyFilled"));
		assertEquals(dispatcher.OnChanged(), ed.createType("Changed"));
		assertEquals(dispatcher.OnDone(), ed.createType("Done"));
		assertEquals(dispatcher.OnFailed(), ed.createType("Failed"));
		assertEquals(dispatcher.OnTrade(), ed.createType("Trade"));
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		dispatcher.OnAvailable().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnAvailable(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireAvailable(order);
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnRegistered() throws Exception {
		dispatcher.OnRegistered().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnRegistered(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnRegistered(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnRegisterFailed() throws Exception {
		dispatcher.OnRegisterFailed().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnRegisterFailed(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnRegisterFailed(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnCancelled() throws Exception {
		dispatcher.OnCancelled().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnCancelled(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnCancelled(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnCancelFailed() throws Exception {
		dispatcher.OnCancelFailed().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnCancelFailed(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnCancelFailed(), order));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnFilled() throws Exception {
		dispatcher.OnFilled().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnFilled(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnFilled(), order));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnPartiallyFilled() throws Exception {
		dispatcher.OnPartiallyFilled().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnPartiallyFilled(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnPartiallyFilled(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		dispatcher.OnChanged().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnChanged(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnChanged(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnDone() throws Exception {
		dispatcher.OnDone().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnDone(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnDone(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnFailed() throws Exception {
		dispatcher.OnFailed().addListener(listener);
		queue.enqueue(eq(new OrderEvent(dispatcher.OnFailed(), order)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderEvent(order.OnFailed(), order));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade trd = control.createMock(Trade.class);
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new OrderTradeEvent(dispatcher.OnTrade(), order, trd)),
				same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.onEvent(new OrderTradeEvent(order.OnTrade(), order, trd));
		
		control.verify();
	}

	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(order);
		
		assertTrue(order.OnCancelFailed().isListener(dispatcher));
		assertTrue(order.OnCancelled().isListener(dispatcher));
		assertTrue(order.OnChanged().isListener(dispatcher));
		assertTrue(order.OnDone().isListener(dispatcher));
		assertTrue(order.OnFailed().isListener(dispatcher));
		assertTrue(order.OnFilled().isListener(dispatcher));
		assertTrue(order.OnPartiallyFilled().isListener(dispatcher));
		assertTrue(order.OnRegistered().isListener(dispatcher));
		assertTrue(order.OnRegisterFailed().isListener(dispatcher));
		assertTrue(order.OnTrade().isListener(dispatcher));
	}

	@Test
	public void testStopRelayFor() throws Exception {
		order.OnCancelFailed().addListener(dispatcher);
		order.OnCancelled().addListener(dispatcher);
		order.OnChanged().addListener(dispatcher);
		order.OnDone().addListener(dispatcher);
		order.OnFailed().addListener(dispatcher);
		order.OnFilled().addListener(dispatcher);
		order.OnPartiallyFilled().addListener(dispatcher);
		order.OnRegistered().addListener(dispatcher);
		order.OnRegisterFailed().addListener(dispatcher);
		order.OnTrade().addListener(dispatcher);
		
		dispatcher.stopRelayFor(order);
		
		assertFalse(order.OnCancelFailed().isListener(dispatcher));
		assertFalse(order.OnCancelled().isListener(dispatcher));
		assertFalse(order.OnChanged().isListener(dispatcher));
		assertFalse(order.OnDone().isListener(dispatcher));
		assertFalse(order.OnFailed().isListener(dispatcher));
		assertFalse(order.OnFilled().isListener(dispatcher));
		assertFalse(order.OnPartiallyFilled().isListener(dispatcher));
		assertFalse(order.OnRegistered().isListener(dispatcher));
		assertFalse(order.OnRegisterFailed().isListener(dispatcher));
		assertFalse(order.OnTrade().isListener(dispatcher));
	}

}
