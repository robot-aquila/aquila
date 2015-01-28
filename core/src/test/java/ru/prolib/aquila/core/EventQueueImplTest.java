package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-20<br>
 * $Id: EventQueueImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImplTest {
	private static EventSystem eSys;
	private static EventQueueImpl queue;
	private static IMocksControl control;
	private static EventType type1,type2;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		queue = new EventQueueImpl("EVNT");
		eSys = new EventSystemImpl(queue);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStart_ThrowsIfAlreadyStarted() throws Exception {
		queue.start();
		queue.start();
	}

	@Test
	public void testStarted() throws Exception {
		assertFalse(queue.started());
		queue.start();
		assertTrue(queue.started());
		queue.stop();
		assertTrue(queue.join(1000));
		assertFalse(queue.started());
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
	
	@Test (expected=IllegalStateException.class)
	public void testEnqueue_ThrowsIfNotStarted() throws Exception {
		queue.enqueue(new EventImpl(type1), new LinkedList<EventListener>());
	}
	
	@Test
	public void testEnqueueL_Ok() throws Exception {
		final CountDownLatch exit = new CountDownLatch(1);
		final Event event1 = new EventImpl(type1);
		final Event event2 = new EventImpl(type2);
		final List<EventListener> listeners1 = new LinkedList<EventListener>();
		final List<EventListener> listeners2 = new LinkedList<EventListener>();
		listeners1.add(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertSame(event1, event);
				queue.enqueue(event2, listeners2);
			}
		});
		listeners2.add(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertSame(event2, event);
				exit.countDown();
				queue.stop();
			}
		});
		queue.start();
		queue.enqueue(event1, listeners1);
		assertTrue(exit.await(50, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueueL_ThrowsIfEventIsNull() throws Exception {
		queue.start();
		queue.enqueue(null, new LinkedList<EventListener>());
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueueL_ThrowsIfListenersIsNull() throws Exception {
		queue.start();
		queue.enqueue(new EventImpl(type1), (List<EventListener>) null);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testEnqueueL_ThrowsIfNotStarted() throws Exception {
		queue.enqueue(new EventImpl(type1), new LinkedList<EventListener>());
	}
	
	@Test
	public void testEnqueueD_AddListenerAfterEnqueue() throws Exception {
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
		queue.enqueue(new EventImpl(type2), dispatcher);
		queue.enqueue(new EventImpl(type1), dispatcher);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueueD_ThrowsIfEventIsNull() throws Exception {
		queue.start();
		queue.enqueue(null, eSys.createEventDispatcher());
	}
	
	@Test (expected=NullPointerException.class)
	public void testEnqueueD_ThrowsIfDispatcherIsNull() throws Exception {
		queue.start();
		queue.enqueue(new EventImpl(type1), (EventDispatcher) null);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testEnqueueD_ThrowsIfNotStarted() throws Exception {
		queue.enqueue(new EventImpl(type1), eSys.createEventDispatcher());
	}
	
	private static int counter = 0; 
	@Test
	public void testEnqueueL_FromQueueThread() throws Exception {
		// Тест трансляции события из потока диспетчеризации.
		final List<EventListener> listeners = new Vector<EventListener>();
		listeners.add(new EventListener() {
			@Override
			public void onEvent(Event event) {
				counter ++;
				if ( counter <= 10 ) {
					queue.enqueue(new EventImpl(type1), listeners);
				} else {
					queue.stop();
				}
			}
		});
		counter = 0;
		queue.start();
		queue.enqueue(new EventImpl(type1), listeners);
		assertTrue(queue.join(1000));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testJoin1_ThrowsIfTimeoutLessOrEqThanZero() throws Exception {
		queue.join(0);
	}
	
	@Test
	public void testJoin1_TrueIfFinished() throws Exception {
		final Event event = new EventImpl(type1);
		final List<EventListener> listeners = new LinkedList<EventListener>();
		listeners.add(new EventListener() {
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
		queue.enqueue(event, listeners);
		assertTrue(queue.join(100));
		assertFalse(queue.started());
	}
	
	@Test
	public void testJoin1_FalseIfNotFinished() throws Exception {
		queue.start();
		long start = System.currentTimeMillis();
		assertFalse(queue.join(50));
		assertTrue(System.currentTimeMillis() - start >= 50);
		assertTrue(queue.started());
		queue.stop();
	}
	
	@Test
	public void testJoin1_IgnoreInQueueThread() throws Exception {
		final CountDownLatch exit = new CountDownLatch(1);
		final Event event = new EventImpl(type1);
		final List<EventListener> listeners = new LinkedList<EventListener>();
		listeners.add(new EventListener() {
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
		queue.enqueue(event, listeners);
		assertTrue(exit.await(100, TimeUnit.MILLISECONDS));
		queue.stop();
	}
	
	@Test
	public void testJoin1_IgnoreIfQueueStopped() throws Exception {
		long start = System.currentTimeMillis();
		assertTrue(queue.join(1000));
		assertTrue(System.currentTimeMillis() - start <= 10);
	}
	
	@Test
	public void testJoin0_ReturnIfFinished() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final List<EventListener> listeners = new Vector<EventListener>();
		listeners.add(new EventListener() {
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
		queue.enqueue(new EventImpl(type1), listeners);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		queue.join();
		assertFalse(queue.started());
	}
	
	@Test
	public void testJoin0_IgnoreInQueueThread() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final List<EventListener> listeners = new LinkedList<EventListener>();
		listeners.add(new EventListener() {
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
		queue.enqueue(new EventImpl(type1), listeners);
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
		final List<EventListener> listeners = new Vector<EventListener>();
		listeners.add(new EventListener() {
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
		queue.enqueue(new EventImpl(type1), listeners);
		started.countDown();
		queue.join();
		assertFalse(queue.started());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(queue.equals(queue));
		assertFalse(queue.equals(null));
		assertFalse(queue.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("EVNT")
			.add("Another");
		Variant<?> iterator = vId;
		int foundCnt = 0;
		EventQueueImpl x = null, found = null;
		do {
			x = new EventQueueImpl(vId.get());
			if ( queue.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("EVNT", found.getId());
	}
	
	@Test
	public void testFunctionalTest() throws Exception {
		new EventQueue_FunctionalTest().testSchedulingSequence(queue);
	}
	
}
