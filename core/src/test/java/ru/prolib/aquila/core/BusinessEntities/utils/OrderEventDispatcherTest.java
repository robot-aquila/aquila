package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class OrderEventDispatcherTest {
	private IMocksControl control;
	private Order order;
	private Trade trade;
	private EventSystem es;
	private EventListener listener;
	private EventQueue queue;
	private OrderEventDispatcher dispatcher;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		order = control.createMock(Order.class);
		trade = control.createMock(Trade.class);
		listener = control.createMock(EventListener.class);
		es = new EventSystemImpl(queue);
		dispatcher = new OrderEventDispatcher(es);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher ed = es.createEventDispatcher("Order");
		assertEquals(dispatcher.getEventDispatcher(), ed);
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
	public void testFireTrade() throws Exception {
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new OrderTradeEvent(dispatcher.OnTrade(),
				order, trade)), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.fireTrade(order, trade);
		
		control.verify();
	}
	
	@Test
	public void testRemoveListeners() throws Exception {
		dispatcher.OnRegistered().addListener(listener);
		dispatcher.OnRegisterFailed().addListener(listener);
		dispatcher.OnCancelled().addListener(listener);
		dispatcher.OnCancelFailed().addListener(listener);
		dispatcher.OnFilled().addListener(listener);
		dispatcher.OnPartiallyFilled().addListener(listener);
		dispatcher.OnChanged().addListener(listener);
		dispatcher.OnDone().addListener(listener);
		dispatcher.OnFailed().addListener(listener);
		dispatcher.OnTrade().addListener(listener);
		
		dispatcher.removeListeners();
		
		assertFalse(dispatcher.OnRegistered().isListener(listener));
		assertFalse(dispatcher.OnRegisterFailed().isListener(listener));
		assertFalse(dispatcher.OnCancelled().isListener(listener));
		assertFalse(dispatcher.OnCancelFailed().isListener(listener));
		assertFalse(dispatcher.OnFilled().isListener(listener));
		assertFalse(dispatcher.OnPartiallyFilled().isListener(listener));
		assertFalse(dispatcher.OnChanged().isListener(listener));
		assertFalse(dispatcher.OnDone().isListener(listener));
		assertFalse(dispatcher.OnFailed().isListener(listener));
		assertFalse(dispatcher.OnTrade().isListener(listener));
	}
	
	@Test
	public void testDispatch() throws Exception {
		EventType testType = dispatcher.getEventDispatcher().createType("test");
		testType.addListener(listener);
		Event event = new EventImpl(testType);
		queue.enqueue(same(event), same(dispatcher.getEventDispatcher()));
		control.replay();
		
		dispatcher.dispatch(event);
		
		control.verify();
	}

}
