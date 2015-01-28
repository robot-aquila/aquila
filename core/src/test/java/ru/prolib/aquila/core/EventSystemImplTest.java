package ru.prolib.aquila.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * 2012-04-22<br>
 * $Id: EventSystemImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventSystemImplTest {
	private IMocksControl control;
	private EventQueue queue;
	private EventSystemImpl eventSystem;
	private EventType[] types;
	private Map<EventType, Event> state;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		eventSystem = new EventSystemImpl(queue);
		types = new EventType[2];
		types[0] = control.createMock(EventType.class);
		types[1] = control.createMock(EventType.class);
		state = new LinkedHashMap<EventType, Event>();
		state.put(types[0], null);
		state.put(types[1], null);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		eventSystem = new EventSystemImpl();
		EventQueueImpl q = (EventQueueImpl) eventSystem.getEventQueue();
		assertNotNull(q);
	}
	
	@Test
	public void testConstruct1_Ok() throws Exception {
		assertSame(queue, eventSystem.getEventQueue());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct1_ThrowsIfQueueIsNull() throws Exception {
		new EventSystemImpl((EventQueue)null);
	}
	
	@Test
	public void testConstruct1Str() throws Exception {
		eventSystem = new EventSystemImpl("zulu24");
		assertEquals("zulu24", eventSystem.getEventQueue().getId());
	}
	
	@Test
	public void testCreateEventDispatcher0() throws Exception {
		EventDispatcherImpl d = (EventDispatcherImpl)
			eventSystem.createEventDispatcher();
		assertNotNull(d);
		assertSame(queue, d.getEventQueue());
	}
	
	@Test
	public void testCreateEventDispatcher1() throws Exception {
		EventDispatcherImpl d = (EventDispatcherImpl)
		eventSystem.createEventDispatcher("Zulu");
		assertNotNull(d);
		assertSame(queue, d.getEventQueue());
		assertEquals("Zulu", d.getId());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(eventSystem.equals(eventSystem));
		assertTrue(eventSystem.equals(new EventSystemImpl(queue)));
		assertFalse(eventSystem.equals(null));
		assertFalse(eventSystem.equals(this));
		assertFalse(eventSystem.equals(new EventSystemImpl(control
				.createMock(EventQueue.class))));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 60523)
			.append(queue)
			.toHashCode();
		assertEquals(hashCode, eventSystem.hashCode());
	}
	
}
