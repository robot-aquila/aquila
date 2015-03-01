package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PortfoliosEventDispatcherTest {
	private static Account account = new Account("foo", "bar");
	private static SecurityDescriptor descr =
			new SecurityDescriptor("zu", "lu", ISO4217.USD, SecurityType.FUT);
	@SuppressWarnings("rawtypes")
	private EditableTerminal terminal;
	private EditablePortfolio portfolio;
	private EditableSecurity security;
	private EditablePosition position;
	private PortfoliosEventDispatcher dispatcher;
	private List<Event> eventsActual, eventsExpected;
	private Event e;

	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		terminal = new TerminalImpl("test");
		portfolio = terminal.getEditablePortfolio(account);
		security = terminal.getEditableSecurity(descr);
		position = portfolio.getEditablePosition(security);
		dispatcher = new PortfoliosEventDispatcher(terminal.getEventSystem());
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
		dispatcher.OnPortfolioAvailable().addListener(new EventListener() {
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
		e = new PortfolioEvent((EventTypeSI) dispatcher.OnPortfolioAvailable(), portfolio);
		eventsExpected.add(e);
		
		dispatcher.fireAvailable(portfolio);
		dispatcher.onEvent(incoming);
		assertTrue(counter2.await(500L, TimeUnit.MILLISECONDS));

		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testStructure() throws Exception {
		String did = "Portfolios";
		EventDispatcher ed = dispatcher.getEventDispatcher();
		assertEquals(did, ed.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnPortfolioAvailable();
		assertEquals(did + ".Available", type.getId());
		assertFalse(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnPortfolioChanged();
		assertEquals(did + ".Changed", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnPositionAvailable();
		assertEquals(did + ".PositionAvailable", type.getId());
		assertTrue(type.isOnlySyncMode());
		
		type = (EventTypeSI) dispatcher.OnPositionChanged();
		assertEquals(did + ".PositionChanged", type.getId());
		assertTrue(type.isOnlySyncMode());
	}
	
	@Test
	public void testFireAvailable() throws Exception {
		e = new PortfolioEvent((EventTypeSI) dispatcher.OnPortfolioAvailable(), portfolio);
		eventsExpected.add(e);
		final CountDownLatch counter = new CountDownLatch(1);
		dispatcher.OnPortfolioAvailable().addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				eventsActual.add(event);
				counter.countDown();
			}
		});

		dispatcher.fireAvailable(portfolio);
		assertTrue(counter.await(500L, TimeUnit.MILLISECONDS));
		assertEquals(eventsExpected, eventsActual);
	}
	
	@Test
	public void testOnEvent_PositionChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnPositionChanged(),
			new PositionEvent((EventTypeSI) dispatcher.OnPositionChanged(), position),
			new PositionEvent((EventTypeSI) portfolio.OnPositionChanged(), position));
	}
	
	@Test
	public void testOnEvent_PositionAvailable() throws Exception {
		testSynchronousEvent(dispatcher.OnPositionAvailable(),
			new PositionEvent((EventTypeSI) dispatcher.OnPositionAvailable(), position),
			new PositionEvent((EventTypeSI) portfolio.OnPositionAvailable(), position));
	}
	
	@Test
	public void testOnEvent_PortfolioChanged() throws Exception {
		testSynchronousEvent(dispatcher.OnPortfolioChanged(),
			new PortfolioEvent((EventTypeSI) dispatcher.OnPortfolioChanged(), portfolio),
			new PortfolioEvent((EventTypeSI) portfolio.OnChanged(), portfolio));
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(portfolio);
		
		assertTrue(portfolio.OnChanged().isListener(dispatcher));
		assertTrue(portfolio.OnPositionAvailable().isListener(dispatcher));
		assertTrue(portfolio.OnPositionChanged().isListener(dispatcher));
	}

}
