package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-08-03<br>
 * $Id: PositionEventTest.java 250 2012-08-06 03:14:33Z whirlwind $
 */
public class PositionEventTest {
	private IMocksControl control;
	private EventType eventType1, eventType2;
	private Position position1, position2;
	private Instant time1, time2;
	private Set<Integer> tokens1, tokens2;
	private PositionEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType1 = control.createMock(EventType.class);
		eventType2 = control.createMock(EventType.class);
		position1 = control.createMock(Position.class);
		position2 = control.createMock(Position.class);
		time1 = Instant.parse("2017-08-04T17:40:00Z");
		tokens1 = new HashSet<>();
		tokens1.add(PositionField.CURRENT_VOLUME);
		tokens2 = new HashSet<>();
		tokens2.add(PositionField.PROFIT_AND_LOSS);
		event = new PositionEvent(eventType1, position1, time1);
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
		PositionEvent event2 = new PositionEvent(eventType2, position1, time1);
		PositionEvent event3 = new PositionEvent(eventType1, position2, time1);
		PositionEvent event4 = new PositionEvent(eventType2, position2, time1);
		PositionEvent event5 = new PositionEvent(eventType1, position1, time1);
		
		assertFalse(event.equals(new PositionEvent(eventType1, position1, time2)));
		assertFalse(event.equals(event2));
		assertFalse(event.equals(event3));
		assertFalse(event.equals(event4));
		assertTrue(event.equals(event5));
	}

}
