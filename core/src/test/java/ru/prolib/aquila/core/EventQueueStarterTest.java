package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-10<br>
 * $Id$
 */
public class EventQueueStarterTest {
	private IMocksControl control;
	private EventQueue queue;
	private EventQueueStarter starter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		starter = new EventQueueStarter(queue, 1000);
	}
	
	@Test
	public void testStart() throws Exception {
		queue.start();
		control.replay();
		starter.start();
		control.verify();
	}
	
	@Test
	public void testStop_Ok() throws Exception {
		queue.stop();
		expect(queue.join(1000)).andReturn(true);
		control.replay();
		starter.stop();
		control.verify();
	}
	
	@Test (expected=StarterException.class)
	public void testStop_ThrowsOnTimeout() throws Exception {
		queue.stop();
		expect(queue.join(1000)).andReturn(false);
		control.replay();
		starter.stop();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(starter.equals(starter));
		assertFalse(starter.equals(this));
		assertFalse(starter.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventQueue> vQue = new Variant<EventQueue>()
			.add(queue)
			.add(control.createMock(EventQueue.class));
		Variant<Long> vTo = new Variant<Long>(vQue)
			.add(1000L)
			.add(2000L);
		Variant<?> iterator = vTo;
		int foundCnt = 0;
		EventQueueStarter x = null, found = null;
		do {
			x = new EventQueueStarter(vQue.get(), vTo.get());
			if ( starter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(queue, found.getEventQueue());
		assertEquals(1000L, found.getTimeout());
	}

}
