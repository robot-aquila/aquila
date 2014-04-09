package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;


public class OrdersEventDispatcherTest {
	@SuppressWarnings("rawtypes")
	private EditableTerminal terminal;
	private EditableOrder order;
	private OrdersEventDispatcher dispatcher;
	private List<Event> eventsActual, eventsExpected;
	private Event e;	
	
	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		terminal = new TerminalImpl("test");
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
		eventType.addListener(new EventListener() {
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
		e = new OrderEvent(dispatcher.OnAvailable(), order);
		eventsExpected.add(e);
		
		dispatcher.fireAvailable(order);
		dispatcher.onEvent(incoming);
		assertTrue(counter2.await(500L, TimeUnit.MILLISECONDS));

		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		e = new OrderEvent(dispatcher.OnAvailable(), order);
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
				new OrderEvent(dispatcher.OnRegistered(), order),
				new OrderEvent(order.OnRegistered(), order));
	}
	
	@Test
	public void testOnEvent_OnRegisterFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnRegisterFailed(),
				new OrderEvent(dispatcher.OnRegisterFailed(), order),
				new OrderEvent(order.OnRegisterFailed(), order));
	}
	
	@Test
	public void testOnEvent_OnCancelled() throws Exception {
		testSynchronousEvent(dispatcher.OnCancelled(),
				new OrderEvent(dispatcher.OnCancelled(), order),
				new OrderEvent(order.OnCancelled(), order));
	}
	
	@Test
	public void testOnEvent_OnCancelFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnCancelFailed(),
				new OrderEvent(dispatcher.OnCancelFailed(), order),
				new OrderEvent(order.OnCancelFailed(), order));
	}

	@Test
	public void testOnEvent_OnFilled() throws Exception {
		testSynchronousEvent(dispatcher.OnFilled(),
				new OrderEvent(dispatcher.OnFilled(), order),
				new OrderEvent(order.OnFilled(), order));
	}

	@Test
	public void testOnEvent_OnPartiallyFilled() throws Exception {
		testSynchronousEvent(dispatcher.OnPartiallyFilled(),
				new OrderEvent(dispatcher.OnPartiallyFilled(), order),
				new OrderEvent(order.OnPartiallyFilled(), order));
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnChanged(),
				new OrderEvent(dispatcher.OnChanged(), order),
				new OrderEvent(order.OnChanged(), order));
	}
	
	@Test
	public void testOnEvent_OnDone() throws Exception {
		testSynchronousEvent(dispatcher.OnDone(),
				new OrderEvent(dispatcher.OnDone(), order),
				new OrderEvent(order.OnDone(), order));
	}
	
	@Test
	public void testOnEvent_OnFailed() throws Exception {
		testSynchronousEvent(dispatcher.OnFailed(),
				new OrderEvent(dispatcher.OnFailed(), order),
				new OrderEvent(order.OnFailed(), order));
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade t = new Trade(terminal);
		testSynchronousEvent(dispatcher.OnTrade(),
				new OrderTradeEvent(dispatcher.OnTrade(), order, t),
				new OrderTradeEvent(order.OnTrade(), order, t));
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
