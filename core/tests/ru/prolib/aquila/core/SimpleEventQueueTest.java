package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-03-10<br>
 * $Id: SimpleEventQueueTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class SimpleEventQueueTest {
	private IMocksControl control;
	private SimpleEventQueue queue;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = new SimpleEventQueue();
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals("default", queue.getId());
	}
	
	@Test
	public void testStart() throws Exception {
		queue.start();
	}
	
	@Test
	public void testStop() throws Exception {
		queue.stop();
	}
	
	@Test
	public void testEnqueueL() throws Exception {
		Event event = control.createMock(Event.class);
		List<EventListener> list = new Vector<EventListener>();
		list.add(control.createMock(EventListener.class));
		list.get(0).onEvent(same(event));
		list.add(control.createMock(EventListener.class));
		list.get(1).onEvent(same(event));
		list.add(control.createMock(EventListener.class));
		list.get(2).onEvent(same(event));
		control.replay();
		
		queue.enqueue(event, list);
		
		control.verify();
	}
	
	@Test
	public void testEnqueueD() throws Exception {
		EventType type = control.createMock(EventType.class);
		Event event = new EventImpl(type);
		List<EventListener> list = new Vector<EventListener>();
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		expect(dispatcher.getListeners(same(type))).andReturn(list);
		list.add(control.createMock(EventListener.class));
		list.get(0).onEvent(same(event));
		list.add(control.createMock(EventListener.class));
		list.get(1).onEvent(same(event));
		list.add(control.createMock(EventListener.class));
		list.get(2).onEvent(same(event));
		control.replay();
		
		queue.enqueue(event, dispatcher);
		
		control.verify();
	}
	
	@Test
	public void testStarted() throws Exception {
		assertTrue(queue.started());
	}
	
	@Test
	public void testIsDispatchThread() throws Exception {
		assertTrue(queue.isDispatchThread());
	}
	
	@Test
	public void testJoin1() throws Exception {
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testJoin0() throws Exception {
		queue.join();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(queue.equals(queue));
		assertTrue(queue.equals(new SimpleEventQueue()));
		assertFalse(queue.equals(null));
		assertFalse(queue.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		queue = new SimpleEventQueue("test");
		Variant<String> vId = new Variant<String>()
			.add("test")
			.add("best");
		Variant<?> iterator = vId;
		int foundCnt = 0;
		SimpleEventQueue x = null, found = null;
		do {
			x = new SimpleEventQueue(vId.get());
			if ( queue.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("test", found.getId());
	}

}
