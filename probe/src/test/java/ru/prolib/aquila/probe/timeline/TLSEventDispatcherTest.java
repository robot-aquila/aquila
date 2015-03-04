package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import ru.prolib.aquila.core.*;

public class TLSEventDispatcherTest {
	private EventSystem es;
	private TLSEventDispatcher dispatcher;
	private EventListener listener;
	private CountDownLatch counter;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		dispatcher = new TLSEventDispatcher(es);
	}
	
	@After
	public void tearDown() throws Exception {
		if ( es.getEventQueue().started() ) {
			es.getEventQueue().stop();
			es.getEventQueue().join(1000L);
		}
	}
	
	@Test
	public void testEventTypes() throws Exception {
		String did = "Timeline" + TLSEventDispatcher.getLastId();
		
		EventTypeSI type;
		type = (EventTypeSI) dispatcher.OnRun();
		assertEquals(did + ".Run", type.getId());
		
		type = (EventTypeSI) dispatcher.OnPause();
		assertEquals(did + ".Pause", type.getId());
		
		type = (EventTypeSI) dispatcher.OnFinish();
		assertEquals(did + ".Finish", type.getId());
	}
	
	@Test (expected=NoSuchMethodException.class)
	public void testOnStepEventTypeRemoved() throws Exception {
		TLSEventDispatcher.class.getMethod("OnStep");
	}
	
	private void prepareListener(final EventType forType) throws Exception {
		counter = new CountDownLatch(1);
		listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertTrue(event.isType(forType));
				forType.removeListener(listener);
				counter.countDown();
			}
		};
		forType.addListener(listener);
	}
	
	@Test
	public void testFireRun() throws Exception {
		prepareListener(dispatcher.OnRun());
		dispatcher.fireRun();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testFirePause() throws Exception {
		prepareListener(dispatcher.OnPause());
		dispatcher.firePause();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testFireFinish() throws Exception {
		prepareListener(dispatcher.OnFinish());
		dispatcher.fireFinish();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

}
