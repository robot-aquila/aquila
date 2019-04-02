package ru.prolib.aquila.ui;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventFactory;

public class SwingEventTest {
	private EventQueueImpl queue;
	private EventTypeImpl type;
	private SwingEvent service;
	private EventListener listenerStub1, listenerStub2;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl();
		type = new EventTypeImpl();
		listenerStub1 = new EventListenerStub();
		listenerStub2 = new EventListenerStub();
		service = new SwingEvent(listenerStub1);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.shutdown();
		service = null;
	}

	@Test
	public void testOnEvent() throws Exception {
		Event expected = new EventImpl(type);
		CountDownLatch finished = new CountDownLatch(1);
		service = new SwingEvent(new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( SwingUtilities.isEventDispatchThread()
				  && event.equals(expected) )
				{
					finished.countDown();
				}
			}
		});
		type.addListener(service);
		
		queue.enqueue(type, SimpleEventFactory.getInstance());
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new SwingEvent(listenerStub1)));
		assertFalse(service.equals(new SwingEvent(listenerStub2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
