package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;

/**
 * 2012-08-03<br>
 * $Id: PositionEventTest.java 250 2012-08-06 03:14:33Z whirlwind $
 */
public class PositionEventTest {
	private IMocksControl control;
	private EventType eventType1,eventType2;
	private Position position1,position2;
	private PositionEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType1 = control.createMock(EventType.class);
		eventType2 = control.createMock(EventType.class);
		position1 = control.createMock(Position.class);
		position2 = control.createMock(Position.class);
		event = new PositionEvent(eventType1, position1);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType1, event.getType());
		assertSame(position1, event.getPosition());
	}
	
	@Test
	public void testEquals_FalseIfNull() throws Exception {
		assertFalse(event.equals(null));
	}
	
	@Test
	public void testEquals_TrueIfSameObject() throws Exception {
		assertTrue(event.equals(event));
	}
	
	@Test
	public void testEquals_FalseIfDifferentClass() throws Exception {
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals_Ok() throws Exception {
		PositionEvent event2 = new PositionEvent(eventType2, position1);
		PositionEvent event3 = new PositionEvent(eventType1, position2);
		PositionEvent event4 = new PositionEvent(eventType2, position2);
		PositionEvent event5 = new PositionEvent(eventType1, position1);
		
		assertFalse(event.equals(event2));
		assertFalse(event.equals(event3));
		assertFalse(event.equals(event4));
		assertTrue(event.equals(event5));
	}

}