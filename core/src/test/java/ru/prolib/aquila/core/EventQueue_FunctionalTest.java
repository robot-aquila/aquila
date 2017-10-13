package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
		private final CountDownLatch finished;

		public ActiveEvent(EventType type, String id, CountDownLatch finished) {
			this(type, id, null, finished);
		}
		
		public ActiveEvent(EventType type, String id, Runnable action, CountDownLatch finished) {
			super(type);
			this.action = action;
			this.id = id;
			this.finished = finished;
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
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ActiveEvent.class ) {
				return false;
			}
			ActiveEvent o = (ActiveEvent) other;
			return new EqualsBuilder()
					.append(getType(), o.getType())
					.append(id, o.id)
					.isEquals();
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
	
	static class AEFactory implements EventFactory {
		private final String id;
		private final Runnable r;
		private CountDownLatch finished = new CountDownLatch(1);
		
		AEFactory(String id, Runnable r) {
			this.id = id;
			this.r = r;
		}
		
		AEFactory(String id) {
			this(id, null);
		}

		@Override
		public synchronized Event produceEvent(EventType type) {
			return new ActiveEvent(type, id, r, finished);
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
		
		Runnable
			r_e3_b = new Runnable() {
				@Override public void run() {
					finished.countDown();
				}
			},
			r_e2 = new Runnable() {
				@Override public void run() {
					dispatcher.dispatch(type, new AEFactory("e1_a"));
				}
			},
			r_e3_a = new Runnable() {
				@Override
				public void run() {
					dispatcher.dispatch(type, new AEFactory("e3_b", r_e3_b));
				}
			},
			r_e4 = new Runnable() {
				@Override
				public void run() {
					dispatcher.dispatch(type, new AEFactory("e3_a", r_e3_a));
					dispatcher.dispatch(type, new AEFactory("eX"));
				}
			};
		List<AEFactory> fire = new ArrayList<>();
		fire.add(new AEFactory("e1"));
		fire.add(new AEFactory("e2", r_e2));
		fire.add(new AEFactory("e3"));
		fire.add(new AEFactory("e4", r_e4));
		
		List<Event> expected = new LinkedList<Event>();
		expected.add(new ActiveEvent(type, "e1", null));
		expected.add(new ActiveEvent(type, "e2", null));
		expected.add(new ActiveEvent(type, "e1_a", null));
		expected.add(new ActiveEvent(type, "e3", null));
		expected.add(new ActiveEvent(type, "e4", null));
		expected.add(new ActiveEvent(type, "e3_a", null));
		expected.add(new ActiveEvent(type, "eX", null));
		expected.add(new ActiveEvent(type, "e3_b", null));

		for ( int i = 0; i < fire.size(); i ++ ) {
			dispatcher.dispatch(type, fire.get(i));
			assertTrue(fire.get(i).finished.await(100, TimeUnit.MILLISECONDS));
		}
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		
		assertEquals(expected, listener.events);
	}

}
