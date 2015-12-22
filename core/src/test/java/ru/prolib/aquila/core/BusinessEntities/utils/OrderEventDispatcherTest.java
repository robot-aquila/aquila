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
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals("Order", ed.getId());
				
		EventType type;
		type = dispatcher.OnRegistered();
		assertEquals("Order.Registered", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnRegisterFailed();
		assertEquals("Order.RegisterFailed", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnCancelled();
		assertEquals("Order.Cancelled", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnCancelFailed();
		assertEquals("Order.CancelFailed", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnFilled();
		assertEquals("Order.Filled", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnPartiallyFilled();
		assertEquals("Order.PartiallyFilled", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnChanged();
		assertEquals("Order.Changed", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnDone();
		assertEquals("Order.Done", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnFailed();
		assertEquals("Order.Failed", type.getId());
		assertFalse(type.isOnlySyncMode());

		type = dispatcher.OnTrade();
		assertEquals("Order.Trade", type.getId());
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireTrade() throws Exception {
		dispatcher.OnTrade().addListener(listener);
		queue.enqueue(eq(new OrderTradeEvent(dispatcher.OnTrade(), order, trade)));
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
		queue.enqueue(same(event));
		control.replay();
		
		dispatcher.dispatch(event);
		
		control.verify();
	}

}
