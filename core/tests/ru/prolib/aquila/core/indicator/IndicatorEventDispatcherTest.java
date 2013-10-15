package ru.prolib.aquila.core.indicator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

public class IndicatorEventDispatcherTest {
	private EventSystem es;
	private EditableDataSeries series;
	private IndicatorEventDispatcher dispatcher;
	private List<Event> actualEvents;
	private EventListener eventListener;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl(new SimpleEventQueue("test"));
		series = new SeriesFactoryImpl().createDouble("foo");
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
	
	@Test
	public void testConstruct2() throws Exception {
		EventDispatcher d = es.createEventDispatcher("zulu24");
		assertEquals(d, dispatcher.getEventDispatcher());
		assertEquals(d.createType("Started"), dispatcher.OnStarted());
		assertEquals(d.createType("Stopped"), dispatcher.OnStopped());
		assertEquals(d.createType("Add"), dispatcher.OnAdded());
		assertEquals(d.createType("Upd"), dispatcher.OnUpdated());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		String expectedId =
			EventDispatcherImpl.AUTO_ID_PREFIX + EventDispatcherImpl.getAutoId();
		dispatcher = new IndicatorEventDispatcher(es);
		EventDispatcher d = es.createEventDispatcher(expectedId);
		assertEquals(d, dispatcher.getEventDispatcher());
		assertEquals(d.createType("Started"), dispatcher.OnStarted());
		assertEquals(d.createType("Stopped"), dispatcher.OnStopped());
		assertEquals(d.createType("Add"), dispatcher.OnAdded());
		assertEquals(d.createType("Upd"), dispatcher.OnUpdated());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		String expectedId =
			EventDispatcherImpl.AUTO_ID_PREFIX + EventDispatcherImpl.getAutoId();
		dispatcher = new IndicatorEventDispatcher();
		es = new EventSystemImpl(new SimpleEventQueue());
		EventDispatcher d = es.createEventDispatcher(expectedId);
		assertEquals(d, dispatcher.getEventDispatcher());
		assertEquals(d.createType("Started"), dispatcher.OnStarted());
		assertEquals(d.createType("Stopped"), dispatcher.OnStopped());
		assertEquals(d.createType("Add"), dispatcher.OnAdded());
		assertEquals(d.createType("Upd"), dispatcher.OnUpdated());
	}
	
	@Test
	public void testFireStarted() throws Exception {
		dispatcher.OnStarted().addListener(eventListener);
		
		dispatcher.fireStarted();
		
		List<Event> expected = new Vector<Event>();
		expected.add(new EventImpl(dispatcher.OnStarted()));
		assertEquals(expected, actualEvents);
	}
	
	@Test
	public void testFireStopped() throws Exception {
		dispatcher.OnStopped().addListener(eventListener);
		
		dispatcher.fireStopped();
		
		List<Event> expected = new Vector<Event>();
		expected.add(new EventImpl(dispatcher.OnStopped()));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFireAdded() throws Exception {
		dispatcher.OnAdded().addListener(eventListener);
		
		dispatcher.fireAdded(429d, 128);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent(dispatcher.OnAdded(), 429d, 128));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFireUpdated2() throws Exception {
		dispatcher.OnUpdated().addListener(eventListener);
		
		dispatcher.fireUpdated(631d, 183);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent(dispatcher.OnUpdated(), 631d, 183));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFireUpdated3() throws Exception {
		dispatcher.OnUpdated().addListener(eventListener);
		
		dispatcher.fireUpdated(112d, 613d, 1912);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent(dispatcher.OnUpdated(), 112d, 613d, 1912));
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
		dispatcher.OnAdded().addListener(eventListener);
		dispatcher.startRelayFor(series);
		
		series.add(815.32d);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent(dispatcher.OnAdded(), 815.32d, 0));
		assertEquals(expected, actualEvents);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testOnEvent_RelayUpdatedEvent() throws Exception {
		series.add(81.32d);
		dispatcher.OnUpdated().addListener(eventListener);
		dispatcher.startRelayFor(series);
		
		series.set(21.24d);
		
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent(dispatcher.OnUpdated(), 81.32d, 21.24d, 0));
		assertEquals(expected, actualEvents);
	}

}
