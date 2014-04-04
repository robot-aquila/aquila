package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import ru.prolib.aquila.core.*;

public class TLSimulationEventDispatcherTest {
	private EventSystem es;
	private TLSimulationEventDispatcher dispatcher;
	private EventListener listener;
	private CountDownLatch counter;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		dispatcher = new TLSimulationEventDispatcher(es);
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
		String id = "Timeline" + TLSimulationEventDispatcher.getLastId() + ".";
		assertEquals(id + "Running", dispatcher.OnRunning().getId());
		assertEquals(id + "Paused", dispatcher.OnPaused().getId());
		assertEquals(id + "Finished", dispatcher.OnFinished().getId());
		assertEquals(id + "Stepping", dispatcher.OnStepping().getId());
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
	public void testFireRunning() throws Exception {
		prepareListener(dispatcher.OnRunning());
		dispatcher.fireRunning();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testFirePaused() throws Exception {
		prepareListener(dispatcher.OnPaused());
		dispatcher.firePaused();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testFireFinished() throws Exception {
		prepareListener(dispatcher.OnFinished());
		dispatcher.fireFinished();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testFireStepping() throws Exception {
		prepareListener(dispatcher.OnStepping());
		dispatcher.fireStepping();
		
		assertTrue(counter.await(1L, TimeUnit.SECONDS));
	}

}
