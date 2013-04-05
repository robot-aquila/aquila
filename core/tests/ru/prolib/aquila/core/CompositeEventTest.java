package ru.prolib.aquila.core;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

/**
 * 2012-04-22<br>
 * $Id: CompositeEventTest.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class CompositeEventTest {
	private IMocksControl control;
	private LinkedHashMap<EventType, Event> state;
	private EventType type,type2;
	private CompositeEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		state = new LinkedHashMap<EventType, Event>();
		state.put(type, null);
		state.put(type2, null);
		event = new CompositeEvent(type, state);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type, event.getType());
		assertSame(state, event.getState());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruc_ThrowsIfTypeIsNull() throws Exception {
		new CompositeEvent(null, state);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruc_ThrowsIfStateIsNull() throws Exception {
		new CompositeEvent(type, null);
	}
	
	@Test
	public void testIsType() throws Exception {
		assertTrue(event.isType(type));
		assertFalse(event.isType(type2));
	}
	
	@Test
	public void testEquals_TrueWithSameObject() throws Exception {
		assertTrue(event.equals(event));
	}
	
	@Test
	public void testEquals_TrueWithSameType() throws Exception {
		LinkedHashMap<EventType, Event> state2 =
			new LinkedHashMap<EventType, Event>();
		state2.put(type, null);
		state2.put(type2, null);
		CompositeEvent event2 = new CompositeEvent(type, state2);
		assertTrue(event.equals(event2));
	}
	
	@Test
	public void testEquals_FalseWithDifferentState() throws Exception {
		LinkedHashMap<EventType, Event> state2 =
			new LinkedHashMap<EventType, Event>();
		CompositeEvent event2 = new CompositeEvent(type, state2);
		assertFalse(event.equals(event2));
	}
	
	@Test
	public void testEquals_FalseWithNull() throws Exception {
		assertFalse(event.equals(null));
	}
	
	@Test
	public void testEquals_FalseWithAnotherClass() throws Exception {
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals_FalseWithAnotherType() throws Exception {
		EventImpl event2 = new EventImpl(type2);
		assertFalse(event.equals(event2));
	}
	
	@Test
	public void testEquals_FalseDerivedClass() throws Exception {
		DerivedEvent event2 = new DerivedEvent(type, state);
		assertFalse(event.equals(event2));
	}
	
	static class DerivedEvent extends CompositeEvent {

		public DerivedEvent(EventType type, LinkedHashMap<EventType,
				Event> state)
		{
			super(type, state);
		}
		
	}

}
