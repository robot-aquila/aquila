package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

/**
 * 2012-04-20<br>
 * $Id: EventQueueImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImplTest {
	private EventSystem eSys;
	private EventQueueImpl queue;
	private EventDispatcher dispatcher;
	private EventType type1,type2,type3;
	private EventListenerStub listener1, listener2, listener3;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl("EVNT");
		eSys = new EventSystemImpl(queue);
		dispatcher = eSys.createEventDispatcher();
		type1 = dispatcher.createType();
		type2 = dispatcher.createType();
		type3 = new EventTypeImpl("foo");
		listener1 = new EventListenerStub();
		listener2 = new EventListenerStub();
		listener3 = new EventListenerStub();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	@Test
	public void testStarted() throws Exception {
		assertTrue(queue.started());
	}
	
	@Test
	public void testStartStop_SequentiallyOk() throws Exception {
		queue.start();
		queue.stop();
		assertTrue(queue.join(1000));
		queue.stop();
		queue.start();
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueue_ThrowsIfEventIsNull() throws Exception {
		queue.start();
		queue.enqueue(null);
	}
	
	@Test
	public void testEnqueue_AddListenerAfterEnqueue() throws Exception {
		// Добавление наблюдателя после помещения события в очередь
		// учитывается при непосредственно отправке события.
		final CountDownLatch finished = new CountDownLatch(3);
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		type1 = dispatcher.createType();
		type2 = dispatcher.createType();
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type2.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( event.isType(type2) ) {
					type1.addListener(this);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type2));
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	private static int counter = 0; 
	@Test
	public void testEnqueueL_FromQueueThread() throws Exception {
		// Тест трансляции события из потока диспетчеризации.
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				counter ++;
				if ( counter <= 10 ) {
					queue.enqueue(new EventImpl(type1));
				} else {
					queue.stop();
				}
			}
		});
		counter = 0;
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(queue.join(1000));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testJoin1_ThrowsIfTimeoutLessOrEqThanZero() throws Exception {
		queue.join(0);
	}
	
	@Test
	public void testJoin1_TrueIfFinished() throws Exception {
		final Event event = new EventImpl(type1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					Thread.sleep(40);
					queue.stop();
				} catch ( Exception e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
			}
		});
		queue.start();
		queue.enqueue(event);
		assertTrue(queue.join(100));
		assertFalse(queue.started());
	}
		
	@Test
	public void testJoin1_IgnoreInQueueThread() throws Exception {
		final CountDownLatch exit = new CountDownLatch(1);
		final Event event = new EventImpl(type1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.join(1000);
				} catch ( InterruptedException e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
				exit.countDown();
			}
		});
		queue.start();
		queue.enqueue(event);
		assertTrue(exit.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	@Test
	public void testJoin0_ReturnIfFinished() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.stop();
				} catch ( Exception e ) {
					fail("Unhandled exception: " + e);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.join();
		assertFalse(queue.started());
	}
	
	@Test
	public void testJoin0_IgnoreInQueueThread() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					queue.join();
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					fail("Unhandled exception: " + e);
				}
				finished.countDown();
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(50, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testJoin0_ReturnIfQueueStopped() throws Exception {
		long start = System.currentTimeMillis();
		queue.join();
		assertTrue(System.currentTimeMillis() - start <= 10);
	}
	
	@Test
	public void testJoin0_Ok() throws Exception {
		final CountDownLatch started = new CountDownLatch(1);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				try {
					started.await();
					Thread.sleep(50);
					queue.stop();
				} catch ( Exception e ) {
					Thread.currentThread().interrupt();
					fail("Unhandled exception: " + e);
				}
			}
		});
		queue.start();
		queue.enqueue(new EventImpl(type1));
		started.countDown();
		queue.join();
		assertTrue(queue.started());
	}
	
	@Test
	public void testFunctionalTest() throws Exception {
		new EventQueue_FunctionalTest().testSchedulingSequence(queue);
	}
	
	@Test
	public void testFunctionalTest_MixedSyncAndAsyncEvents() throws Exception {
		// Тест последовательности трансляции событий для различных способов
		// доставки. Синхронные получатели должны получать события в первую
		// очередь. В том числе, если новые события возникают в результате
		// обработки предыдущих. Асинхронные получатели получают события только
		// после доставки всех событий синхронным наблюдателям.
		final List<String> actual = new LinkedList<String>(),
				expected = new LinkedList<String>();
		expected.add("T1L2s");
		expected.add("T3L1s");
		expected.add("T1L1");
		expected.add("T2L1s");
		expected.add("T4L1");
		final EventType type3 = dispatcher.createSyncType(),
				type4 = dispatcher.createType();
		final CountDownLatch finished = new CountDownLatch(1);
		type1.addListener(new EventListener() { 				// #3
			@Override public void onEvent(Event event) {
				actual.add("T1L1");
				dispatcher.dispatch(new EventImpl(type2));
			}
		});
		type1.addSyncListener(new EventListener() {  			// #1
			@Override public void onEvent(Event event) {
				actual.add("T1L2s");
				dispatcher.dispatch(new EventImpl(type3));
			}
		});
		type2.addSyncListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add("T2L1s");
				dispatcher.dispatch(new EventImpl(type4));		// #4 
			}
		});
		type3.addSyncListener(new EventListener() {
			@Override public void onEvent(Event event) {		// #2
				actual.add("T3L1s");
			}
		});
		type4.addListener(new EventListener() {					// #5
			@Override public void onEvent(Event event) {
				actual.add("T4L1");
				finished.countDown();
			}
		});
		
		queue.start();
		queue.enqueue(new EventImpl(type1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
		queue.join(100);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEnqueue2_DispatchForAlternates() throws Exception {
		type1.addListener(listener1);
		type2.addListener(listener2);
		type3.addListener(listener3);
		type1.addAlternateType(type2);
		type1.addAlternateType(type3);
		
		queue.start();
		queue.enqueue(type1, new SimpleEventFactory());
		queue.stop();
		assertTrue(queue.join(1000));

		assertEquals(1, listener1.getEventCount());
		assertEquals(1, listener2.getEventCount());
		assertEquals(1, listener3.getEventCount());
	}
	
	@Test
	public void testEnqueue2_DispatchForAllAlternatesOfAlternates()
			throws Exception
	{
		type1.addListener(listener1);
		type2.addListener(listener2);
		type3.addListener(listener3);
		type1.addAlternateType(type2);
		type2.addAlternateType(type3);
		
		queue.start();
		queue.enqueue(type1, new SimpleEventFactory());
		queue.stop();
		assertTrue(queue.join(1000));
		
		assertEquals(1, listener1.getEventCount());
		assertEquals(1, listener2.getEventCount());
		assertEquals(1, listener3.getEventCount());
	}
	
	@Test
	public void testEnqueue2_CircularReferencesAreFine() throws Exception {
		type1.addListener(listener1);
		type2.addListener(listener2);
		type1.addAlternateType(type2);
		type2.addAlternateType(type1);
		
		queue.start();
		queue.enqueue(type1, new SimpleEventFactory());
		queue.stop();
		assertTrue(queue.join(1000));
		
		assertEquals(1, listener1.getEventCount());
		assertEquals(1, listener2.getEventCount());
	}
	
}
