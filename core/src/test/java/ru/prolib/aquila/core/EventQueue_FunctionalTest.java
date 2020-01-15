package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тест выполняет проверку общих требований к реализациям очереди событий.
 */
public class EventQueue_FunctionalTest {
	public static final long DEFAULT_TIMEOUT_SECS = 5L;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EventQueue_FunctionalTest.class);
	}
	
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
	
	static class FIT {
		
		static class ListenerBlocker implements EventListener {
			final CountDownLatch blocker, proceed;
			final long timeout_seconds;
			
			ListenerBlocker(CountDownLatch blocker, CountDownLatch proceed, long timeout_seconds) {
				this.blocker = blocker;
				this.proceed = proceed;
				this.timeout_seconds = timeout_seconds;
			}
	
			@Override
			public void onEvent(Event event) {
				event.getType().removeListener(this);
				try {
					assertTrue(blocker.await(timeout_seconds, TimeUnit.SECONDS));
					proceed.countDown();
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
			
		}

		static class ListenerProducer implements EventListener {
			final EventQueue queue;
			final EventType type;
			final EventFactory factory;
			
			ListenerProducer(EventQueue queue, EventType type_to_send) {
				this.queue = queue;
				this.type = type_to_send;
				this.factory = SimpleEventFactory.getInstance();
			}

			@Override
			public void onEvent(Event event) {
				queue.enqueue(type, factory);
			}
			
		}
		
		static class ListenerFinisher implements EventListener {
			final CountDownLatch finished;
			
			ListenerFinisher(CountDownLatch finished) {
				this.finished = finished;
			}

			@Override
			public void onEvent(Event event) {
				event.getType().removeListener(this);
				finished.countDown();
			}
			
		}
	
		abstract static class AbstractThread extends Thread {
			final EventQueue queue;
			final CountDownLatch started, start_race, phase1_finished, finished;
			final EventType phase1_type, phase2_type;
			final int phase1_count, phase2_count;
			final EventFactory factory;
			final long timeout_seconds;
			
			AbstractThread(EventQueue queue,
					CountDownLatch started,
					CountDownLatch start_race,
					CountDownLatch phase1_finished,
					CountDownLatch finished,
					int phase1_count,
					EventType phase1_type,
					int phase2_count,
					EventType phase2_type,
					long timeout_seconds)
			{
				this.queue = queue;
				this.started = started;
				this.start_race = start_race;
				this.phase1_finished = phase1_finished;
				this.finished = finished;
				this.phase1_count = phase1_count;
				this.phase2_count = phase2_count;
				this.phase1_type = phase1_type;
				this.phase2_type = phase2_type;
				this.factory = SimpleEventFactory.getInstance();
				this.timeout_seconds = timeout_seconds;
			}
			
			@Override
			public void run() {
				try {
					started.countDown();
					assertTrue(start_race.await(timeout_seconds, TimeUnit.SECONDS));
					for ( int i = 0; i < phase1_count; i ++ ) {
						queue.enqueue(phase1_type, factory);
					}
					phase1_finished.countDown();
					if ( afterPhase1() ) {
						for ( int i = 0; i < phase2_count; i ++ ) {
							queue.enqueue(phase2_type, factory);
						}
						finished.countDown();
					}
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
			
			/**
			 * Called after phase 1 finished.
			 * <p>
			 * @return true if should proceed, false - break the test
			 */
			abstract protected boolean afterPhase1();
			
		}
		
		static class SlaveThread extends AbstractThread {
			final CountDownLatch phase2_start;
			
			SlaveThread(EventQueue queue,
					CountDownLatch started,
					CountDownLatch start_race,
					CountDownLatch phase1_finished,
					CountDownLatch finished,
					int phase1_count,
					EventType phase1_type,
					int phase2_count,
					EventType phase2_type,
					long timeout_seconds,
					CountDownLatch phase2_start)
			{
				super(queue, started, start_race, phase1_finished, finished, phase1_count, phase1_type,
						phase2_count, phase2_type, timeout_seconds);
				this.phase2_start = phase2_start;
			}
			
			@Override
			protected boolean afterPhase1() {
				try {
					assertTrue(phase2_start.await(timeout_seconds, TimeUnit.SECONDS));
					return true;
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
					return false;
				}
			}
			
		}
		
		static class MasterThread extends AbstractThread {

			MasterThread(EventQueue queue,
					CountDownLatch started,
					CountDownLatch start_race,
					CountDownLatch phase1_finished,
					CountDownLatch finished,
					int phase1_count,
					EventType phase1_type,
					int phase2_count,
					EventType phase2_type,
					long timeout_seconds)
			{
				super(queue, started, start_race, phase1_finished, finished, phase1_count, phase1_type,
						phase2_count, phase2_type, timeout_seconds);
			}

			@Override
			protected boolean afterPhase1() {
				try {
					FlushIndicator indicator = queue.newFlushIndicator();
					indicator.start();
					indicator.waitForFlushing(timeout_seconds, TimeUnit.SECONDS);
					return true;
				} catch ( Exception e ) {
					logger.error("Unexpected exception: ", e);
					return false;
				}
			}
			
		}
		
	}
		
	public void testFlushIndicator(EventQueue queue) throws Exception {
		// 1. Заблокировать обработку постановом на ожидание в обработчике первого события
		// 2. Фаза 1: Три потока заполняют очередь событиями первого типа, обработка которых приведет к генерации
		// новых событий второго типа
		// 3. Один поток из трех запускает индикатор и ставит на ожидание. Тут проблема - как своевременно поставить,
		// что бы конкуренты не начали забивать очередь событиями второй фазы? Единственный вариант - подождать
		// некоторое время после завершения фазы 1, что бы мастер-поток успел встать на ожидание индикатора. Для
		// слейвов использовать доп сигнал, который включать из блокиратора при съеме блокировки.
		// 4. Оставшиеся два потока ждут сигнала на продолжение
		// 5. Все три потока после сигнала готовы заполнять очередь событиями третьего типа
		// 6. Обработчик первого события получает сигнал и снимает блокировку с очереди
		// 7. После завершения работы все три потока сигнализируют
		// 8. В результате работы должна образоваться последовательность событий, в начале которой
		// сдержатся события первого и второго типов, а в конце - события третьего типа
		long timeout_seconds = DEFAULT_TIMEOUT_SECS;
		int num_threads = 3;
		int phase1_count = 50;
		int phase2_count = 50;
		final EventDispatcher dispatcher = new EventDispatcherImpl(queue);
		final EventType type0 = dispatcher.createType(),
				type1 = dispatcher.createType(),
				type2 = dispatcher.createType(),
				type3 = dispatcher.createType();
		EventListenerStub listenerStub = new EventListenerStub();
		CountDownLatch started = new CountDownLatch(num_threads); // Индикатор старта всех конкурентов
		CountDownLatch start_race = new CountDownLatch(1); // Это сигнал на старт потоков
		CountDownLatch blocker = new CountDownLatch(1); // Этот сигнал блокирует поток диспетчера событий
		CountDownLatch phase1_finished = new CountDownLatch(num_threads);
		CountDownLatch phase2_start = new CountDownLatch(1); // Это сигнал уведомляет о начале второй фазы
		CountDownLatch finished = new CountDownLatch(num_threads); // Это сигнал о завершении потоков
		type0.addListener(new FIT.ListenerBlocker(blocker, phase2_start, timeout_seconds));
		type1.addListener(new FIT.ListenerProducer(queue, type2));
		type1.addListener(listenerStub);
		type2.addListener(listenerStub);
		type3.addListener(listenerStub);
		List<Thread> threads = new ArrayList<>();
		for ( int i = 0; i < num_threads; i ++ ) {
			Thread thread = null;
			if ( i == 0 ) {
				thread = new FIT.MasterThread(queue, started, start_race, phase1_finished, finished,
						phase1_count, type1, phase2_count, type3, timeout_seconds);
				thread.setName("MASTER");
			} else {
				thread = new FIT.SlaveThread(queue, started, start_race, phase1_finished, finished,
						phase1_count, type1, phase2_count, type3, timeout_seconds, phase2_start);
				thread.setName("SLAVE#" + i);
			}
			thread.setDaemon(true);
			threads.add(thread);
		}
		Collections.shuffle(threads);
		for ( Thread thread : threads ) thread.start();
		
		//Thread.sleep(timeout_seconds * 1000L);
		assertTrue(started.await(timeout_seconds, TimeUnit.SECONDS));
		queue.enqueue(type0, SimpleEventFactory.getInstance()); // block queue
		start_race.countDown();
		assertTrue(phase1_finished.await(timeout_seconds, TimeUnit.SECONDS));
		Thread.sleep(200L); // ensure a flush indicator is cocked
		blocker.countDown(); // unblock queue
		assertTrue(finished.await(timeout_seconds, TimeUnit.SECONDS));
		FlushIndicator all_flush = queue.newFlushIndicator();
		all_flush.start();
		all_flush.waitForFlushing(timeout_seconds, TimeUnit.SECONDS);
		
		// head is type1 and type2 events
		// head length is num_threads * phase1_count * 2
		int head_length = num_threads * phase1_count * 2;
		// tail is type3 only events
		// tail length is num_threads * phase2_count
		int tail_length = num_threads * phase2_count;
		int total_length = head_length + tail_length;
		//logger.debug("Total events: {}", total_length);
		assertEquals(total_length, listenerStub.getEventCount());
		for ( int i = 0; i < total_length; i ++ ) {
			Event event = listenerStub.getEvent(i);
			if ( i < head_length ) {
				assertTrue("At[H]#" + i, event.isType(type1) || event.isType(type2));
			} else {
				assertTrue("At[T]#" + i, event.isType(type3));
			}
		}
	}

}
