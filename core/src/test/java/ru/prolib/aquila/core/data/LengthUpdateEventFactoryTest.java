package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class LengthUpdateEventFactoryTest {
	private EventType type;
	private LengthUpdateEventFactory service;

	@Before
	public void setUp() throws Exception {
		type = new EventTypeImpl();
		service = new LengthUpdateEventFactory(12, 47);
	}
	
	@Test
	public void testProduceEvent() {
		Event actual = service.produceEvent(type);
		
		Event expected = new LengthUpdateEvent(type, 12, 47);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(new LengthUpdateEventFactory(12, 47)));
		assertFalse(service.equals(new LengthUpdateEventFactory( 0, 47)));
		assertFalse(service.equals(new LengthUpdateEventFactory(12,  0)));
		assertFalse(service.equals(new LengthUpdateEventFactory( 0,  0)));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(8765119, 593)
				.append(12)
				.append(47)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
