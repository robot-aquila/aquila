package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-06-01<br>
 * $Id: SecurityEventTest.java 223 2012-07-04 12:26:58Z whirlwind $
 */
public class SecurityEventTest {
	private IMocksControl control;
	private EventType eventType1,eventType2;
	private Security security1,security2;
	private SecurityEvent event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType1 = control.createMock(EventType.class);
		eventType2 = control.createMock(EventType.class);
		security1 = control.createMock(Security.class);
		security2 = control.createMock(Security.class);
		event = new SecurityEvent(eventType1, security1);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType1, event.getType());
		assertSame(security1, event.getSecurity());
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
		SecurityEvent event2 = new SecurityEvent(eventType2, security1);
		SecurityEvent event3 = new SecurityEvent(eventType1, security2);
		SecurityEvent event4 = new SecurityEvent(eventType2, security2);
		SecurityEvent event5 = new SecurityEvent(eventType1, security1);
		
		assertFalse(event.equals(event2));
		assertFalse(event.equals(event3));
		assertFalse(event.equals(event4));
		assertTrue(event.equals(event5));
	}

}
