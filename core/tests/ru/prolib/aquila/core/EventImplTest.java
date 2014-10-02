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
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTypeIsNull() throws Exception {
		new EventImpl(null);
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
	public void testEquals_TrueWithSameObject() throws Exception {
		assertTrue(event.equals(event));
	}

	/**
	 * Тестовый класс для проверки работоспособности сравнения событий.
	 */
	static class MyTestEvent extends EventImpl {

		public MyTestEvent(EventType type) {
			super(type);
		}
		
	}
	
	@Test
	public void testEqusl_TrueWithSameObject_EvenForDerivedClasses() throws Exception {
		MyTestEvent e = new MyTestEvent(type);
		assertTrue(e.equals(e));
	}
	
	@Test
	public void testEquals_TrueWithSameType() throws Exception {
		EventImpl event2 = new EventImpl(type);
		assertTrue(event.equals(event2));
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
		DerivedEventImpl event2 = new DerivedEventImpl(type);
		assertFalse(event.equals(event2));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("foo.BasicEvent", event.toString());
	}
	
	static class DerivedEventImpl extends EventImpl {

		public DerivedEventImpl(EventType type) {
			super(type);
		}
		
	}

}
