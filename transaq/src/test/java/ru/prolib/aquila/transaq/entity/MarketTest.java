package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class MarketTest {
	private Market service;

	@Before
	public void setUp() throws Exception {
		service= new Market(123, "foobar");
	}

	@Test
	public void testCtor2() {
		assertEquals(123, service.getID());
		assertEquals("foobar", service.getName());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new Market(123, "foobar")));
		assertFalse(service.equals(new Market(321, "foobar")));
		assertFalse(service.equals(new Market(123, "barfoo")));
		assertFalse(service.equals(new Market(321, "barfoo")));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1435547, 19)
				.append(123)
				.append("foobar")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = "Market[id=123,name=foobar]";
		
		assertEquals(expected, service.toString());
	}

}
