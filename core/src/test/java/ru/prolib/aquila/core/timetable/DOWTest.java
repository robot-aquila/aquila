package ru.prolib.aquila.core.timetable;

import static org.junit.Assert.*;

import org.junit.*;


public class DOWTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals(1, DOW.MONDAY.getNumber());
		assertEquals("MONDAY", DOW.MONDAY.toString());
		
		assertEquals(2, DOW.TUESDAY.getNumber());
		assertEquals("TUESDAY", DOW.TUESDAY.toString());
		
		assertEquals(3, DOW.WEDNESDAY.getNumber());
		assertEquals("WEDNESDAY", DOW.WEDNESDAY.toString());
		
		assertEquals(4, DOW.THURSDAY.getNumber());
		assertEquals("THURSDAY", DOW.THURSDAY.toString());
		
		assertEquals(5, DOW.FRIDAY.getNumber());
		assertEquals("FRIDAY", DOW.FRIDAY.toString());
		
		assertEquals(6, DOW.SATURDAY.getNumber());
		assertEquals("SATURDAY", DOW.SATURDAY.toString());
		
		assertEquals(7, DOW.SUNDAY.getNumber());
		assertEquals("SUNDAY", DOW.SUNDAY.toString());
	}

}
