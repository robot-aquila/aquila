package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PositionsEventDispatcherTest {
	private static Account account = new Account("foo", "bar");
	private static Symbol symbol = new Symbol("zu", "lu", ISO4217.USD, SymbolType.FUTURE);
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private EditableSecurity security;
	private EditablePosition position;
	private PositionsEventDispatcher dispatcher;
	private List<Event> eventsActual, eventsExpected;
	private Event e;

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder().buildTerminal();
		portfolio = terminal.getEditablePortfolio(account);
		security = terminal.getEditableSecurity(symbol);
		position = portfolio.getEditablePosition(security);
		dispatcher = new PositionsEventDispatcher(terminal.getEventSystem(), account);
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
		e = new PositionEvent(dispatcher.OnAvailable(), position);
		eventsExpected.add(e);
		
		dispatcher.fireAvailable(position);
		dispatcher.onEvent(incoming);
		assertTrue(counter2.await(500L, TimeUnit.MILLISECONDS));

		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testStructure() throws Exception {
		String did = "Positions[foo#bar]";
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals(did, ed.getId());
		
		EventType type;
		type = dispatcher.OnAvailable();
		assertEquals(did + ".Available", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = dispatcher.OnChanged();
		assertEquals(did + ".Changed", type.getId());
		assertFalse(type.isOnlySyncMode());
	}

	
	@Test
	public void testFireAvailable() throws Exception {
		e = new PositionEvent(dispatcher.OnAvailable(), position);
		eventsExpected.add(e);
		final CountDownLatch counter = new CountDownLatch(1);
		dispatcher.OnAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter.countDown();
			}
		});

		dispatcher.fireAvailable(position);
		assertTrue(counter.await(500L, TimeUnit.MILLISECONDS));
		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnChanged(),
			new PositionEvent(dispatcher.OnChanged(), position),
			new PositionEvent(position.OnChanged(), position));
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(position);
		
		assertTrue(position.OnChanged().isSyncListener(dispatcher));
	}
	
}
