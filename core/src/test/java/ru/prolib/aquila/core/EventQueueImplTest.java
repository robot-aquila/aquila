package ru.prolib.aquila.core;

import static org.junit.Assert.*;

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
	}
	
	@After
	public void tearDown() throws Exception {
		
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
	}

	@Test
	public void testFunctionalTest() throws Exception {
		new EventQueue_FunctionalTest().testSchedulingSequence(queue);
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
	}
	
}
