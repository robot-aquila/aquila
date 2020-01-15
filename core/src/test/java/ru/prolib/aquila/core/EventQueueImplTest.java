package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.concurrent.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import ru.prolib.aquila.core.eque.DispatchingType;

/**
 * 2012-04-20<br>
 * $Id: EventQueueImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImplTest {
	private static EventQueue_FunctionalTest functionalTest; 
	private static EventQueueFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
		functionalTest = new EventQueue_FunctionalTest();
		factory = new EventQueueFactory();
	}

	private EventSystem eSys;
	private EventQueue queue;
	private EventDispatcher dispatcher;
	private EventType type1,type2,type3;
	
	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault("EVNT");
		eSys = new EventSystemImpl(queue);
		dispatcher = eSys.createEventDispatcher();
		type1 = dispatcher.createType();
		type2 = dispatcher.createType();
		type3 = new EventTypeImpl("foo");
	}
	
	@After
	public void tearDown() throws Exception {
		if ( queue != null ) {
			queue.shutdown();
			queue = null;
		}
	}
	
	@Test
	public void testEnqueue2_AddListenerAfterEnqueue() throws Exception {
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
		queue.enqueue(type2, SimpleEventFactory.getInstance());
		queue.enqueue(type1, SimpleEventFactory.getInstance());
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		assertEquals(2, queue.getStats().getTotalEventsSent());
	}
	
	@Test
	public void testEnqueue2_FromQueueThread() throws Exception {
		// Тест трансляции события из потока диспетчеризации.
		final CountDownLatch finished = new CountDownLatch(11);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type1.addListener(new EventListener() {
			int counter = 0;
			@Override
			public void onEvent(Event event) {
				counter ++;
				if ( counter <= 10 ) {
					queue.enqueue(type1, SimpleEventFactory.getInstance());
				}
			}
		});
		queue.enqueue(type1, SimpleEventFactory.getInstance());
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(11, queue.getStats().getTotalEventsSent());
	}

	@Test
	public void testFunctionalTest_Default() throws Exception {
		functionalTest.testSchedulingSequence(queue);
		functionalTest.testSchedulingSequence2(queue);
	}
	
	@Test
	public void testFunctionalTest_TypeOldOriginal() throws Exception {
		queue = factory.createLegacy(DispatchingType.OLD_ORIGINAL);
		functionalTest.runAllTests(queue);
	}
	
	@Test
	public void testFunctionalTest_TypeOldComplFutures() throws Exception {
		queue = factory.createLegacy(DispatchingType.OLD_COMPL_FUTURES);
		functionalTest.runAllTests(queue);
	}
	
	@Test
	public void testFunctionalTest_TypeOldRightHere() throws Exception {
		queue = factory.createLegacy(DispatchingType.OLD_RIGHT_HERE);
		functionalTest.runAllTests(queue);
	}
	
	@Test
	public void testFunctionalTest_TypeNewQueue4Workers() throws Exception {
		queue = factory.createLegacy(DispatchingType.NEW_QUEUE_4WORKERS);
		functionalTest.runAllTests(queue);
	}

	@Test
	public void testFunctionalTest_TypeNewQueue6Workers() throws Exception {
		queue = factory.createLegacy(DispatchingType.NEW_QUEUE_6WORKERS);
		functionalTest.runAllTests(queue);
	}
	
	@Test
	public void testFunctionalTest_TypeNewRightHereNoStats() throws Exception {
		queue = factory.createLegacy(DispatchingType.NEW_RIGHT_HERE_NO_TIME_STATS);
		functionalTest.runAllTests(queue);
		//functionalTest.testFlushIndicator(queue); // don't test, it doesn't works
	}
	
	private EventQueue createV4Queue() {
		return factory.createV4("V4", EventQueue_FunctionalTest.DEFAULT_TIMEOUT_SECS * 1000L, false);
	}
	
	@Test
	public void testFunctionalTest_V4() throws Exception {
		queue = createV4Queue();
		functionalTest.runAllTests(queue);
	}
	
	@Test
	public void testFunctionalTest_V4_TestFlushIndicator() throws Exception {
		queue = createV4Queue();
		functionalTest.testFlushIndicator(queue);
	}

	@Test
	public void testEnqueue2_DispatchForAlternates() throws Exception {
		final CountDownLatch finished = new CountDownLatch(3);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type2.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type3.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type1.addAlternateType(type2);
		type1.addAlternateType(type3);
		
		queue.enqueue(type1, new SimpleEventFactory());

		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(1, queue.getStats().getTotalEventsSent());
	}
	
	@Test
	public void testEnqueue2_DispatchForAllAlternatesOfAlternates()
			throws Exception
	{
		final CountDownLatch finished = new CountDownLatch(3);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type2.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type3.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type1.addAlternateType(type2);
		type2.addAlternateType(type3);
		
		queue.enqueue(type1, new SimpleEventFactory());

		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(1, queue.getStats().getTotalEventsSent());
	}
	
	@Test
	public void testEnqueue2_CircularReferencesAreOK() throws Exception {
		final CountDownLatch finished = new CountDownLatch(2);
		type1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type2.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		type1.addAlternateType(type2);
		type2.addAlternateType(type1);
		
		queue.enqueue(type1, new SimpleEventFactory());

		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(1, queue.getStats().getTotalEventsSent());
	}
	
}
