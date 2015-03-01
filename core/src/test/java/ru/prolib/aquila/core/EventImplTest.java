package ru.prolib.aquila.core;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-04-09
 */
public class EventImplTest {
	private EventTypeSI type,type2;
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
		assertSame(type, event.getTypeSI());
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

}
