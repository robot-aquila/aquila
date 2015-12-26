package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SimpleEventFactoryTest {
	private EventType type1, type2;
	private SimpleEventFactory factory;

	@Before
	public void setUp() throws Exception {
		type1 = new EventTypeImpl("foo");
		type2 = new EventTypeImpl("bar");
		factory = new SimpleEventFactory();
	}

	@Test
	public void testProduceEvent() {
		Event event1 = factory.produceEvent(type1);
		Event event2 = factory.produceEvent(type2);
		
		assertTrue(event1 instanceof EventImpl);
		assertTrue(event1.isType(type1));
		assertTrue(event2 instanceof EventImpl);
		assertTrue(event2.isType(type2));
	}

}
