package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Тест выполняет проверку общих требований к реализациям очереди событий.
 */
public class EventQueue_FunctionalTest {
	
	/**
	 * Активное событие может быть связано с действием.
	 * Активные события используются для эмуляции подачи новых событий в
	 * обработчике события.
	 */
	static class ActiveEvent extends EventImpl {
		private final Runnable action;
		private final String id;
		private final CountDownLatch finished = new CountDownLatch(1);

		public ActiveEvent(EventType type, String id) {
			this(type, id, null);
		}
		
		public ActiveEvent(EventType type, String id, Runnable action) {
			super(type);
			this.action = action;
			this.id = id;
		}
		
		public void activate() {
			if ( finished.getCount() == 0 ) {
				throw new RuntimeException("Already finished: " + id);
			}
			if ( action != null ) {
				action.run();
			}
			finished.countDown();
		}
		
		@Override
		public String toString() {
			return id;
		}
		
	}
	
	/**
	 * Обозреватель аккумулирует все полученные события и вызывает функцию
	 * активации, если это событие активного типа.
	 */
	static class Listener implements EventListener {
		private final List<Event> events = new LinkedList<Event>();

		@Override
		public void onEvent(Event event) {
			events.add(event);
			if ( event instanceof ActiveEvent ) {
				((ActiveEvent) event).activate();
			}
		}
		
	}
	
	/**
	 * Проверить соответствие последовательности поступления событий
	 * последовательности их обработки.
	 * <p>
	 * @param queue
	 * @throws Exception
	 */
	public void testSchedulingSequence(final EventQueue queue) throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final EventDispatcher dispatcher = new EventDispatcherImpl(queue);
		EventType type = dispatcher.createType();
		Listener listener = new Listener();
		
		type.addListener(listener);
		
		final Event eX = new ActiveEvent(type, "eX");
		final ActiveEvent e1 = new ActiveEvent(type, "e1");
		final ActiveEvent e1_a = new ActiveEvent(type, "e1_a");
		final ActiveEvent e2 = new ActiveEvent(type, "e2", new Runnable() {
			@Override
			public void run() {
				dispatcher.dispatch(e1_a);
			}
		});
		final ActiveEvent e3 = new ActiveEvent(type, "e3");
		final ActiveEvent e3_b = new ActiveEvent(type, "e3_b", new Runnable() {
			@Override
			public void run() {
				finished.countDown();
			}
		});
		final ActiveEvent e3_a = new ActiveEvent(type, "e3_a", new Runnable() {
			@Override
			public void run() {
				dispatcher.dispatch(e3_b);
			}
		});
		final ActiveEvent e4 = new ActiveEvent(type, "e4", new Runnable() {
			@Override
			public void run() {
				dispatcher.dispatch(e3_a);
				dispatcher.dispatch(eX);
			}
		});

		
		List<ActiveEvent> fire = new LinkedList<ActiveEvent>();
		fire.add(e1);
		fire.add(e2);
		fire.add(e3); 
		fire.add(e4);
		
		List<Event> expected = new LinkedList<Event>();
		expected.add(e1);
		expected.add(e2);
		expected.add(e1_a);
		expected.add(e3);
		expected.add(e4);
		expected.add(e3_a);
		expected.add(eX);
		expected.add(e3_b);

		for ( int i = 0; i < fire.size(); i ++ ) {
			dispatcher.dispatch(fire.get(i));
			assertTrue(fire.get(i).finished.await(100, TimeUnit.MILLISECONDS));
		}
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		
		assertEquals(expected, listener.events);
	}

}
