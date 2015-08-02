package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

public class IndicatorEventDispatcherTest {
	private EventSystem es;
	private EditableSeries<Double> series;
	private IndicatorEventDispatcher dispatcher;
	private List<Event> actualEvents;
	private EventListener eventListener;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl("test");
		es.getEventQueue().start();
		series = new SeriesFactoryImpl(es).createDouble("foo");
		dispatcher = new IndicatorEventDispatcher(es, "zulu24");
		
		actualEvents = new Vector<Event>();
		actualEvents.clear();
		eventListener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				actualEvents.add(event);
			}
		};
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventDispatcher d = dispatcher.getEventDispatcher();
		assertEquals("zulu24", d.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnStarted();
		assertEquals("zulu24.Started", type.getId());

		type = (EventTypeSI) dispatcher.OnStopped();
		assertEquals("zulu24.Stopped", type.getId());

		type = (EventTypeSI) dispatcher.OnAdded();
		assertEquals("zulu24.Add", type.getId());
		
		type = (EventTypeSI) dispatcher.OnUpdated();
		assertEquals("zulu24.Upd", type.getId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		String did = EventDispatcherImpl.AUTO_ID_PREFIX + EventDispatcherImpl.getAutoId();
		dispatcher = new IndicatorEventDispatcher(es);
		EventDispatcher d = dispatcher.getEventDispatcher();
		assertEquals(did, d.getId());
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnStarted();
		assertEquals(did + ".Started", type.getId());

		type = (EventTypeSI) dispatcher.OnStopped();
		assertEquals(did + ".Stopped", type.getId());

		type = (EventTypeSI) dispatcher.OnAdded();
		assertEquals(did + ".Add", type.getId());
		
		type = (EventTypeSI) dispatcher.OnUpdated();
		assertEquals(did + ".Upd", type.getId());
	}
	
	@Test
	public void testFireStarted() throws Exception {
		dispatcher.OnStarted().addSyncListener(eventListener);
		
		dispatcher.fireStarted();
		
		List<Event> expected = new Vector<Event>();
		expected.add(new EventImpl((EventTypeSI) dispatcher.OnStarted()));
		assertEquals(expected, actualEvents);
	}
	
	@Test
	public void testFireStopped() throws Exception {
		dispatcher.OnStopped().addSyncListener(eventListener);
		
		dispatcher.fireStopped();
		
		List<Event> expected = new Vector<Event>();
		expected.add(new EventImpl((EventTypeSI) dispatcher.OnStopped()));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFireAdded() throws Exception {
		dispatcher.OnAdded().addSyncListener(eventListener);
		
		dispatcher.fireAdded(429d, 128);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) dispatcher.OnAdded(), 429d, 128));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFireUpdated2() throws Exception {
		dispatcher.OnUpdated().addSyncListener(eventListener);
		
		dispatcher.fireUpdated(631d, 183);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) dispatcher.OnUpdated(), 631d, 183));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFireUpdated3() throws Exception {
		dispatcher.OnUpdated().addSyncListener(eventListener);
		
		dispatcher.fireUpdated(112d, 613d, 1912);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) dispatcher.OnUpdated(), 112d, 613d, 1912));
		assertEquals(expected, actualEvents);
	}
	
	@Test
	public void testStartRelayFor() throws Exception {
		dispatcher.startRelayFor(series);
		
		assertTrue(series.OnAdded().isListener(dispatcher));
		assertTrue(series.OnUpdated().isListener(dispatcher));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStartRelayFor_ThrowsIfStarted() throws Exception {
		dispatcher.startRelayFor(series);
		dispatcher.startRelayFor(series);
	}
	
	@Test
	public void testStopRelay() throws Exception {
		dispatcher.startRelayFor(series);
		
		dispatcher.stopRelay();
		
		assertFalse(series.OnAdded().isListener(dispatcher));
		assertFalse(series.OnUpdated().isListener(dispatcher));
	}
	
	@Test
	public void testStopRelay_SkipsIfNotStarted() throws Exception {
		dispatcher.stopRelay();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testOnEvent_RelayAddedEvent() throws Exception {
		dispatcher.OnAdded().addSyncListener(eventListener);
		dispatcher.startRelayFor(series);
		
		series.add(815.32d);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) dispatcher.OnAdded(), 815.32d, 0));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testOnEvent_RelayUpdatedEvent() throws Exception {
		series.add(81.32d);
		dispatcher.OnUpdated().addSyncListener(eventListener);
		dispatcher.startRelayFor(series);
		
		series.set(21.24d);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) dispatcher.OnUpdated(), 81.32d, 21.24d, 0));
		assertEquals(expected, actualEvents);
	}

}
