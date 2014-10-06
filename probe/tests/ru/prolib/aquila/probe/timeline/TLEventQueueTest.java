package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;
import java.util.Vector;
import org.joda.time.*;
import org.junit.*;

public class TLEventQueueTest {
	private static DateTime startTime = new DateTime(2014, 1, 27, 19, 40, 0),
							  endTime = new DateTime(2014, 1, 27, 19, 50, 0);
	private static Interval interval = new Interval(startTime, endTime);
	private static TLEvent sharedEvents[] = {
		new TLEvent(startTime.plus(1), null), // #0
		new TLEvent(startTime.plus(5), null), // #1
		new TLEvent(startTime.plus(2), null), // #2
		new TLEvent(startTime.plus(1), null), // #3
		new TLEvent(startTime.plus(2), null), // #4
		new TLEvent(startTime.plus(6), null), // #5
		new TLEvent(startTime.plus(2), null), // #6
		new TLEvent(startTime.plus(5), null), // #7
		new TLEvent(startTime.plus(2), null), // #8
		new TLEvent(endTime.minus(1),  null), // #9
	};
	private TLEventQueue queue;

	@Before
	public void setUp() throws Exception {
		queue = new TLEventQueue(interval);
	}
	
	@Test
	public void testGetInterval() throws Exception {
		assertEquals(interval, queue.getInterval());
	}
	
	@Test
	public void testGetInterval_ReturnsCurrentActiveInterval() throws Exception {
		queue.pushEvent(sharedEvents[1]); // +5 ms
		queue.pullStack(); // move AP to +6 ms
		
		assertEquals(new Interval(startTime.plus(6), endTime), queue.getInterval());
		
		queue.pushEvent(sharedEvents[9]); // to last ms of interval
		queue.pullStack(); // move to end of interval
		
		assertEquals(new Interval(endTime, endTime), queue.getInterval());
	}
	
	@Test
	public void testGetPOA() throws Exception {
		assertEquals(startTime, queue.getPOA());
	}
	
	@Test
	public void testPullStack_NoData() throws Exception {
		assertNull(queue.pullStack());
		assertEquals(endTime, queue.getPOA());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPullStack() throws Exception {
		TLEvent event[] = sharedEvents;
		Vector stackEvents[] = {
				new Vector(),
				new Vector(),
				new Vector(),
				new Vector(),
				new Vector(),
		};
		stackEvents[0].add(event[0]);
		stackEvents[0].add(event[3]);
		stackEvents[1].add(event[2]);
		stackEvents[1].add(event[4]);
		stackEvents[1].add(event[6]);
		stackEvents[1].add(event[8]);
		stackEvents[2].add(event[1]);
		stackEvents[2].add(event[7]);
		stackEvents[3].add(event[5]);
		stackEvents[4].add(event[9]);
		DateTime POAs[] = {
				startTime.plus(2),
				startTime.plus(3),
				startTime.plus(6),
				startTime.plus(7),
				endTime,
		};
		for ( TLEvent e : event ) {
			queue.pushEvent(e);
		}
		assertEquals(POAs.length, queue.size());
		for ( int i = 0; i < stackEvents.length; i ++ ) {
			String msg = "at #" + i;
			TLEventStack stack = queue.pullStack();
			assertNotNull("Stack " + msg, stack);
			assertEquals("Events " + msg, stackEvents[i], stack.getEvents());
			assertEquals("POA " + msg, POAs[i], queue.getPOA());
		}
		assertNull("Expected end of queue", queue.pullStack());
		assertEquals(endTime, queue.getPOA());
	}
	
	@Test (expected=TLOutOfIntervalException.class)
	public void testPushEvent_ThrowsBeforePOA() throws Exception {
		queue.pushEvent(new TLEvent(startTime.minus(1), null));
		assertEquals(0,queue.size());
	}
	
	@Test (expected=TLOutOfIntervalException.class)
	public void testPushEvent_ThrowsAfterEnfOfInterval() throws Exception {
		queue.pushEvent(new TLEvent(endTime, null));
		queue.pushEvent(new TLEvent(endTime.plus(1), null));
		assertEquals(0,queue.size());
	}
	
	@Test
	public void testIsFinished1() throws Exception {
		// Конец РП=2014-01-27T19:50:00.000 НЕ входит в РП!!!
		// Первое событие датированное последней ms РП
		// переводит очередь в состояние завершенности.
		assertFalse(queue.finished());
		queue.pushEvent(new TLEvent(startTime.plus(1), null));
		queue.pullStack();
		assertFalse(queue.finished());
		queue.pushEvent(new TLEvent(endTime.minus(1), null));
		queue.pullStack();
		assertTrue(queue.finished());
		assertEquals(new Interval(endTime, endTime), queue.getInterval());
	}

	@Test
	public void testClear() throws Exception {
		for ( TLEvent e : sharedEvents ) {
			queue.pushEvent(e);
		}
		queue.clear();
		
		assertEquals(0, queue.size());
	}
	
}
