package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

/**
 * 2012-04-09
 * $Id: EventImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventImplTest {
	private IMocksControl control;
	private EventType type,type2;
	private EventImpl event;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
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
	public void testEquals_TrueDerivedClass() throws Exception {
		DerivedEventImpl event2 = new DerivedEventImpl(type);
		assertTrue(event.equals(event2));
	}
	
	@Test
	public void testToString() throws Exception {
		expect(type.asString()).andReturn("One.Two");
		control.replay();
		assertEquals("One.Two.BasicEvent", event.toString());
		control.verify();
	}
	
	static class DerivedEventImpl extends EventImpl {

		public DerivedEventImpl(EventType type) {
			super(type);
		}
		
	}

}
