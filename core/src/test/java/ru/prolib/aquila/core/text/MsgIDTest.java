package ru.prolib.aquila.core.text;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class MsgIDTest {
	private MsgID service;

	@Before
	public void setUp() throws Exception {
		service = new MsgID("foo", "bar");
	}
	
	@Test
	public void testGetters() {
		assertEquals("foo", service.getSectionId());
		assertEquals("bar", service.getMessageId());
	}
	
	@Test
	public void testToString() {
		String expected = "foo.bar";
		
		assertEquals(expected, service.toString());
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new MsgID("foo", "bar")));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new MsgID("bar", "bar")));
		assertFalse(service.equals(new MsgID("foo", "foo")));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(722213, 4005)
				.append("foo")
				.append("bar")
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
