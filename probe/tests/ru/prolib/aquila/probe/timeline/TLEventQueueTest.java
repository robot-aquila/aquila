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
		queue.shiftToNextStack();
		queue.pullStack(); // move AP to +6 ms
		
		assertEquals(new Interval(startTime.plus(6), endTime), queue.getInterval());
		
		queue.pushEvent(sharedEvents[9]); // to last ms of interval
		queue.shiftToNextStack();
		queue.pullStack(); // move to end of interval
		
		assertEquals(new Interval(endTime, endTime), queue.getInterval());
	}
	
	@Test
	public void testGetPOA() throws Exception {
		assertEquals(startTime, queue.getPOA());
	}
	
	@Test
	public void testPullStack_NullIfNoDataAtPOA() throws Exception {
		assertNull(queue.pullStack());
		assertEquals(startTime.plus(1), queue.getPOA());
		assertNull(queue.pullStack());
		assertEquals(startTime.plus(2), queue.getPOA());
		assertNull(queue.pullStack());
		assertEquals(startTime.plus(3), queue.getPOA());
	}
	
	@Test
	public void testPullStack_IgnoresEndOfInterval() throws Exception {
		int protector = 0;
		while ( queue.getPOA().compareTo(endTime.plus(100)) < 0 ) {
			assertNull(queue.pullStack());
			if ( ++protector > 1000000 ) fail("Enabled protection");
		}
	}
	
	@Test
	public void testPullStack() throws Exception {
		Vector<TLEvent> expected = new Vector<TLEvent>();
		expected.add(new TLEvent(startTime, null));
		expected.add(new TLEvent(startTime, null));
		expected.add(new TLEvent(startTime, null));
		
		queue.pushEvent(new TLEvent(startTime.plus(240), null));
		queue.pushEvent(new TLEvent(startTime.plus(240), null));
		queue.pushEvent(new TLEvent(startTime.plus(529), null));
		queue.pushEvent(new TLEvent(startTime.plus(419), null));
		queue.pushEvent(expected.get(0));
		queue.pushEvent(expected.get(1));
		queue.pushEvent(expected.get(2));
		queue.pushEvent(new TLEvent(startTime.plus(986), null));
		
		TLEventStack stack = queue.pullStack();
		assertNotNull(stack);
		assertEquals(startTime.plus(1), queue.getPOA());
		assertEquals(expected, stack.getEvents());
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
		assertTrue(queue.shiftToNextStack());
		assertNotNull(queue.pullStack());
		assertFalse(queue.finished());
		queue.pushEvent(new TLEvent(endTime.minus(200), null));
		assertTrue(queue.shiftToNextStack());
		assertNotNull(queue.pullStack());
		assertFalse(queue.finished());
		assertFalse(queue.shiftToNextStack());
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
	
	@Test
	public void testShiftToNextStack() throws Exception {
		long OFF1=510, OFF2 = 1690, OFF3 = 24596, OFF4 = 51001;
		queue.pushEvent(new TLEvent(startTime.plus(OFF1), null));
		queue.pushEvent(new TLEvent(startTime.plus(OFF2), null));
		queue.pushEvent(new TLEvent(startTime.plus(OFF3), null));
		queue.pushEvent(new TLEvent(startTime.plus(OFF4), null));
		
		assertEquals(startTime, queue.getPOA());
		assertTrue(queue.shiftToNextStack());
		assertEquals(startTime.plus(OFF1), queue.getPOA());
		queue.pullStack(); // Нужно извлечь стек, инача дальше не пойдет
		assertEquals(startTime.plus(OFF1 + 1), queue.getPOA());
		assertTrue(queue.shiftToNextStack());
		assertEquals(startTime.plus(OFF2), queue.getPOA());
		queue.pullStack();
		assertTrue(queue.shiftToNextStack());
		assertEquals(startTime.plus(OFF3), queue.getPOA());
		queue.pullStack();
		assertTrue(queue.shiftToNextStack());
		assertEquals(startTime.plus(OFF4), queue.getPOA());
		queue.pullStack();
		assertFalse(queue.shiftToNextStack());
		assertEquals(endTime, queue.getPOA());
		assertTrue(queue.finished());
	}
	
}
