package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class OrdersEventDispatcherTest {
	private EditableTerminal terminal;
	private EditableOrder order;
	private OrdersEventDispatcher dispatcher;
	private List<Event> eventsActual, eventsExpected;
	private Event e;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		terminal = new TerminalBuilder().buildTerminal();
		order = terminal.createOrder();
		dispatcher = new OrdersEventDispatcher(terminal.getEventSystem());
		terminal.getEventSystem().getEventQueue().start();
		eventsActual = new Vector<Event>();
		eventsExpected = new Vector<Event>();
		e = null;
	}
	
	@After
	public void tearDown() throws Exception {
		terminal.getEventSystem().getEventQueue().stop();
		terminal.getEventSystem().getEventQueue().join(5000L);
	}
	
	/**
	 * Тестировать синхронное событие.
	 * <p>
	 * @param eventType тип события, которое должно быть синхронным
	 * @param expected ожидаемое итоговое событие (результат ретрансляции)
	 * @param incoming исходное событие (основание ретрансляции)
	 * @throws Exception
	 */
	private final void testSynchronousEvent(EventType eventType,
			Event expected, Event incoming) throws Exception
	{
		final CountDownLatch counter1 = new CountDownLatch(1),
				counter2 = new CountDownLatch(1);
		// Используем этот тип события для симуляции асинхронного события 
		dispatcher.OnAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				try {
					// Приостановить обработку очереди минимум на пол секунды
					assertTrue(counter1.await(500L, TimeUnit.MILLISECONDS));
					eventsActual.add(event);
					counter2.countDown();
				} catch ( InterruptedException e ) { }
			}
		});
		// Навешиваем обозревателя на тестируемый синхронный тип событий
		eventType.addSyncListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter1.countDown(); // разблокировать асинхронную очередь
			}
		});
		// Ожидаемое событие (например, об изменении позиции) будет
		// ретранслировано непосредственно в момент поступления исходного
		// события. Для диспетчеризации этого события будет использована
		// отдельная очередь событий.
		eventsExpected.add(expected);
		// Асинхронное событие окажется на втором месте, так как очередь будет
		// на время заморожена обработчиком этого события.
		e = new OrderEvent((EventTypeSI) dispatcher.OnAvailable(), order);
		eventsExpected.add(e);
		
		dispatcher.fireAvailable(order);
		dispatcher.onEvent(incoming);
		assertTrue(counter2.await(500L, TimeUnit.MILLISECONDS));

		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testStructure() throws Exception {
		EventDispatcher d = dispatcher.getEventDispatcher();
		assertEquals("Orders", d.getId());
		
		assertEquals("Orders.Available", dispatcher.OnAvailable().getId());
		assertFalse(dispatcher.OnAvailable().isOnlySyncMode());
		
		assertEquals("Orders.Registered", dispatcher.OnRegistered().getId());
		assertFalse(dispatcher.OnRegistered().isOnlySyncMode());
		
		assertEquals("Orders.RegisterFailed",
				dispatcher.OnRegisterFailed().getId());
		assertFalse(dispatcher.OnRegisterFailed().isOnlySyncMode());
		
		assertEquals("Orders.Cancelled", dispatcher.OnCancelled().getId());
		assertFalse(dispatcher.OnCancelled().isOnlySyncMode());

		assertEquals("Orders.CancelFailed", dispatcher.OnCancelFailed().getId());
		assertFalse(dispatcher.OnCancelFailed().isOnlySyncMode());
		
		assertEquals("Orders.Filled", dispatcher.OnFilled().getId());
		assertFalse(dispatcher.OnFilled().isOnlySyncMode());
		
		assertEquals("Orders.PartiallyFilled",
				dispatcher.OnPartiallyFilled().getId());
		assertFalse(dispatcher.OnPartiallyFilled().isOnlySyncMode());
		
		assertEquals("Orders.Changed", dispatcher.OnChanged().getId());
		assertFalse(dispatcher.OnChanged().isOnlySyncMode());

		assertEquals("Orders.Done", dispatcher.OnDone().getId());
		assertFalse(dispatcher.OnDone().isOnlySyncMode());
		
		assertEquals("Orders.Failed", dispatcher.OnFailed().getId());
		assertFalse(dispatcher.OnFailed().isOnlySyncMode());
		
		assertEquals("Orders.Trade", dispatcher.OnTrade().getId());
		assertFalse(dispatcher.OnTrade().isOnlySyncMode());
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		e = new OrderEvent((EventTypeSI) dispatcher.OnAvailable(), order);
		eventsExpected.add(e);
		final CountDownLatch counter = new CountDownLatch(1);
		dispatcher.OnAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter.countDown();
			}
		});
		
		dispatcher.fireAvailable(order);
		assertTrue(counter.await(500L, TimeUnit.MILLISECONDS));
		assertEquals(eventsExpected, eventsActual);
	}

	@Test
	public void testOnEvent_OnRegistered() throws Exception {
		testSynchronousEvent(dispatcher.OnRegistered(),
			new OrderEvent((EventTypeSI) dispatcher.OnRegistered(), order),
			new OrderEvent((EventTypeSI) order.OnRegistered(), order));
	}
	
	@Test
	public void testOnEvent_OnRegisterFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnRegisterFailed(),
			new OrderEvent((EventTypeSI) dispatcher.OnRegisterFailed(), order),
			new OrderEvent((EventTypeSI) order.OnRegisterFailed(), order));
	}
	
	@Test
	public void testOnEvent_OnCancelled() throws Exception {
		testSynchronousEvent(dispatcher.OnCancelled(),
			new OrderEvent((EventTypeSI) dispatcher.OnCancelled(), order),
			new OrderEvent((EventTypeSI) order.OnCancelled(), order));
	}
	
	@Test
	public void testOnEvent_OnCancelFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnCancelFailed(),
			new OrderEvent((EventTypeSI) dispatcher.OnCancelFailed(), order),
			new OrderEvent((EventTypeSI) order.OnCancelFailed(), order));
	}

	@Test
	public void testOnEvent_OnFilled() throws Exception {
		testSynchronousEvent(dispatcher.OnFilled(),
			new OrderEvent((EventTypeSI) dispatcher.OnFilled(), order),
			new OrderEvent((EventTypeSI) order.OnFilled(), order));
	}

	@Test
	public void testOnEvent_OnPartiallyFilled() throws Exception {
		testSynchronousEvent(dispatcher.OnPartiallyFilled(),
			new OrderEvent((EventTypeSI) dispatcher.OnPartiallyFilled(), order),
			new OrderEvent((EventTypeSI) order.OnPartiallyFilled(), order));
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnChanged(),
			new OrderEvent((EventTypeSI) dispatcher.OnChanged(), order),
			new OrderEvent((EventTypeSI) order.OnChanged(), order));
	}
	
	@Test
	public void testOnEvent_OnDone() throws Exception {
		testSynchronousEvent(dispatcher.OnDone(),
			new OrderEvent((EventTypeSI) dispatcher.OnDone(), order),
			new OrderEvent((EventTypeSI) order.OnDone(), order));
	}
	
	@Test
	public void testOnEvent_OnFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnFailed(),
			new OrderEvent((EventTypeSI) dispatcher.OnFailed(), order),
			new OrderEvent((EventTypeSI) order.OnFailed(), order));
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade t = new Trade(terminal);
		testSynchronousEvent(dispatcher.OnTrade(),
			new OrderTradeEvent((EventTypeSI) dispatcher.OnTrade(), order, t),
			new OrderTradeEvent((EventTypeSI) order.OnTrade(), order, t));
	}

	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(order);
		
		assertTrue(order.OnCancelFailed().isSyncListener(dispatcher));
		assertTrue(order.OnCancelled().isSyncListener(dispatcher));
		assertTrue(order.OnChanged().isSyncListener(dispatcher));
		assertTrue(order.OnDone().isSyncListener(dispatcher));
		assertTrue(order.OnFailed().isSyncListener(dispatcher));
		assertTrue(order.OnFilled().isSyncListener(dispatcher));
		assertTrue(order.OnPartiallyFilled().isSyncListener(dispatcher));
		assertTrue(order.OnRegistered().isSyncListener(dispatcher));
		assertTrue(order.OnRegisterFailed().isSyncListener(dispatcher));
		assertTrue(order.OnTrade().isSyncListener(dispatcher));
	}

	@Test
	public void testStopRelayFor() throws Exception {
		order.OnCancelFailed().addSyncListener(dispatcher);
		order.OnCancelled().addSyncListener(dispatcher);
		order.OnChanged().addSyncListener(dispatcher);
		order.OnDone().addSyncListener(dispatcher);
		order.OnFailed().addSyncListener(dispatcher);
		order.OnFilled().addSyncListener(dispatcher);
		order.OnPartiallyFilled().addSyncListener(dispatcher);
		order.OnRegistered().addSyncListener(dispatcher);
		order.OnRegisterFailed().addSyncListener(dispatcher);
		order.OnTrade().addSyncListener(dispatcher);
		
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
