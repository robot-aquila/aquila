package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;
import java.util.Vector;
import org.joda.time.DateTime;
import org.junit.*;

public class TLEventCacheTest {
	private static DateTime startTime = new DateTime(2014, 1, 27, 19, 40, 0);
	private TLEventCache cache;

	@Before
	public void setUp() throws Exception {
		cache = new TLEventCache(startTime);
	}
	
	@Test
	public void testGetPOA() throws Exception {
		assertEquals(startTime, cache.getPOA());
	}
	
	@Test
	public void testPullStack_NoData() throws Exception {
		assertNull(cache.pullStack());
		assertEquals(startTime, cache.getPOA());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPullStack() throws Exception {
		TLEvent event[] = {
				new TLEvent(startTime.plus(1), null), // #0
				new TLEvent(startTime.plus(5), null), // #1
				new TLEvent(startTime.plus(2), null), // #2
				new TLEvent(startTime.plus(1), null), // #3
				new TLEvent(startTime.plus(2), null), // #4
				new TLEvent(startTime.plus(6), null), // #5
				new TLEvent(startTime.plus(2), null), // #6
				new TLEvent(startTime.plus(5), null), // #7
				new TLEvent(startTime.plus(2), null), // #8
		};
		Vector stackEvents[] = {
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
		DateTime POAs[] = {
				startTime.plus(2),
				startTime.plus(3),
				startTime.plus(6),
				startTime.plus(7),
		};
		for ( TLEvent e : event ) {
			cache.pushEvent(e);
		}
		for ( int i = 0; i < stackEvents.length; i ++ ) {
			String msg = "at #" + i;
			TLEventStack stack = cache.pullStack();
			assertNotNull("Stack " + msg, stack);
			assertEquals("Events " + msg, stackEvents[i], stack.getEvents());
			assertEquals("POA " + msg, POAs[i], cache.getPOA());
		}
		assertNull(cache.pullStack());
	}
	
	@Test (expected=TLOutOfDateException.class)
	public void testPushStack_ThrowsIfOutOfPOA() throws Exception {
		cache.pushEvent(new TLEvent(startTime.minus(1), null));
	}

}
