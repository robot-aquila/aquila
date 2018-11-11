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
			synchronized ( this ) {
				events.add(event);
			}
			if ( event instanceof ActiveEvent ) {
				((ActiveEvent) event).activate();
			}
		}
		
		public synchronized List<Event> getEvents() {
			return events;
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
	
	public void runAllTests(final EventQueue queue) throws Exception {
		testSchedulingSequence(queue);
		testSchedulingSequence2(queue);
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
		
		assertEquals(expected, listener.getEvents());
	}
	
	/**
	 * Проверить соответствие последовательности поступающих событий разных
	 * типов последовательности их обработки. С учетом возможной задержки
	 * в процессе отправки уведомления, события одного типа, отправленные ранее,
	 * должны доставляться не позже, чем события другого типа, отправленные
	 * позже.
	 * <p>
	 * @param queue
	 * @throws Exception
	 */
	public void testSchedulingSequence2(final EventQueue queue) throws Exception {
		final CountDownLatch finished = new CountDownLatch(6);
		final EventDispatcher dispatcher = new EventDispatcherImpl(queue);
		final EventType type1 = dispatcher.createType(),
				type2 = dispatcher.createType(),
				type3 = dispatcher.createType();
		final List<String> cs = new ArrayList<>();
		// Обработчик события первого типа - самый медленный.
		EventListener listener1 = new EventListener() {
			int c = 0;
			@Override
			public synchronized void onEvent(Event event) {
				assertTrue(event.isType(type1));
				try {
					Thread.sleep(200);
					c ++;
				} catch ( InterruptedException e ) { }
				synchronized ( cs ) {
					cs.add("type1#" + c);
				}
				finished.countDown();
			}
		};
		// Обработчик событий второго типа - медленный, но быстрее первого
		EventListener listener2 = new EventListener() {
			int c = 0;
			@Override
			public synchronized void onEvent(Event event) {
				assertTrue(event.isType(type2));
				try {
					Thread.sleep(50);
					c ++;
				} catch ( InterruptedException e ) { }
				synchronized ( cs ) {
					cs.add("type2#" + c);
				}
				finished.countDown();
			}
		};
		// Обработчик событий третьего типа - без задержек
		EventListener listener3 = new EventListener() {
			int c = 0;
			@Override
			public synchronized void onEvent(Event event) {
				assertTrue(event.isType(type3));
				c ++;
				synchronized ( cs ) {
					cs.add("type3#" + c);
				}
				finished.countDown();
			}
		};
		type1.addListener(listener1);
		type2.addListener(listener2);
		type3.addListener(listener3);
		
		EventFactory factory = SimpleEventFactory.getInstance();
		dispatcher.dispatch(type1, factory);
		dispatcher.dispatch(type2, factory);
		dispatcher.dispatch(type3, factory);
		dispatcher.dispatch(type1, factory);
		dispatcher.dispatch(type2, factory);
		dispatcher.dispatch(type3, factory);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		
		List<String> expected = new ArrayList<>();
		expected.add("type1#1");
		expected.add("type2#1");
		expected.add("type3#1");
		expected.add("type1#2");
		expected.add("type2#2");
		expected.add("type3#2");
		assertEquals(expected, cs);
	}

}
