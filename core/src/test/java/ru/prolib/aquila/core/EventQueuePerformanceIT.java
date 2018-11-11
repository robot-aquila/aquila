package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.eque.DispatchingType;
import ru.prolib.aquila.core.eque.EventQueueStats;

public class EventQueuePerformanceIT {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}
	
	private EventType typeSpecial;
	private List<EventType> types;
	private List<EventListener> listeners;

	@Before
	public void setUp() throws Exception {
		typeSpecial = new EventTypeImpl("SPEC");
		types = new ArrayList<>();
		for ( int i = 0; i < 100; i ++ ) {
			types.add(new EventTypeImpl("TYPE" + i));
		}
		listeners = new ArrayList<>();
		for ( int i = 0; i < 100; i ++ ) {
			listeners.add(new EventListenerStub());
		}
		for ( int i = 0; i < 50; i ++ ) {
			types.get(i).addAlternateType(types.get(50 + i));
		}
		for ( int i = 0; i < 50; i ++ ) {
			types.get(50 + i).addListener(listeners.get(i));
		}
	}
	
	@After
	public void tearDown() throws Exception {
		for ( EventType type : types ) {
			type.removeListeners();
			type.removeAlternates();
		}
		typeSpecial.removeListeners();
		typeSpecial.removeAlternates();
		types.clear();
		listeners.clear();
	}
	
	private void runTest(EventQueueImpl queue, long MAX_SENT_EVENTS) throws Exception {
		long MAX_TIME = 30000; // seconds
		float MAX_RATIO = 2;
		long st = System.currentTimeMillis(), est = 0;
		EventFactory factory = SimpleEventFactory.getInstance();
		CountDownLatch finished = new CountDownLatch(1);
		boolean proceed = true;
		EventQueueStats stats = queue.getStats();
		while ( proceed ) {
			for ( EventType type : types ) {
				queue.enqueue(type, factory);
				est = System.currentTimeMillis() - st;
				long tes = stats.getTotalEventsSent();
				long ted = stats.getTotalEventsDispatched();
				if ( ted != 0 && tes > MAX_SENT_EVENTS ) {
					float ratio = (float) tes / (float) ted;
					if ( ratio > MAX_RATIO ) {
						proceed = false;
						System.out.println("@" + est + " BREAK by tes/ted ratio "
								+ ratio + ", tes=" + tes + " ted=" + ted);
						break;
					}
				}
				if ( est >= MAX_TIME ) {
					System.out.println("@" + est + " BREAK by time: " + est + " ms.");
					proceed = false;
					break;
				}				
			}
		}
		typeSpecial.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				finished.countDown();
			}
		});
		queue.enqueue(typeSpecial, factory);
		assertTrue(finished.await(MAX_TIME * 5, TimeUnit.MILLISECONDS));
	}
	
	private EventQueueStats runTest(DispatchingType dispatchingType, int pass, long MAX_SENT_EVENTS)
		throws Exception
	{
		EventQueueImpl queue = new EventQueueImpl(dispatchingType);
		System.out.println("---------------------------------");
		System.out.println("Starting test: " + dispatchingType + " (pass " + pass + ")");
		long st = System.currentTimeMillis();
		runTest(queue, MAX_SENT_EVENTS);
		long dt = System.currentTimeMillis() - st;
		queue.shutdown();
		EventQueueStats stats = queue.getStats();
		System.out.println("Test finished: " + dispatchingType + " (" + dt + " ms used)");
		System.out.println("       Events sent: " + stats.getTotalEventsSent());
		System.out.println(" Events dispatched: " + stats.getTotalEventsDispatched());
		System.out.println("  Dispatching time: " + stats.getDispatchingTime());
		System.out.println("Building list time: " + stats.getBuildingTaskListTime());
		System.out.println("   Delivering time: " + stats.getDeliveryTime());
		return stats;
	}

	@Test
	public void testX() throws Exception {
		Thread.sleep(10000);
		long MAX_SENT_EVENTS = 2000000;
		for ( int i = 0; i < 1; i ++ ) {
			runTest(DispatchingType.OLD_ORIGINAL, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.OLD_COMPL_FUTURES, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_QUEUE_4WORKERS, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_QUEUE_6WORKERS, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.OLD_RIGHT_HERE, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_RIGHT_HERE_NO_TIME_STATS, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_RIGHT_HERE_V3, i, MAX_SENT_EVENTS);
		}
	}
	
	@Test
	public void testX2() throws Exception {
		Thread.sleep(10000);
		long MAX_SENT_EVENTS = 10000000;
		for ( int i = 0; i < 3; i ++ ) {
			runTest(DispatchingType.OLD_RIGHT_HERE, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_RIGHT_HERE_NO_TIME_STATS, i, MAX_SENT_EVENTS);
			runTest(DispatchingType.NEW_RIGHT_HERE_V3, i, MAX_SENT_EVENTS);
		}
	}

}
