package ru.prolib.aquila.core;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-04-09
 */
public class EventImplTest {
	private EventType type,type2;
	private EventImpl event;

	@Before
	public void setUp() throws Exception {
		type = new EventTypeImpl("foo");
		type2 = new EventTypeImpl("bar");
		event = new EventImpl(type);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertSame(type, event.getType());
	}
	
	@Test
	public void testIsType() throws Exception {
		assertTrue(event.isType(type));
		assertFalse(event.isType(type2));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("foo.BasicEvent", event.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(event.equals(new EventImpl(type)));
		assertFalse(event.equals(new EventImpl(type2)));
	}

}
