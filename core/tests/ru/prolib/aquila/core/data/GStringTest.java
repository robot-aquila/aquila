package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-08-22<br>
 * $Id: GStringTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GStringTest {
	private static GString getter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getter = new GString();
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals("gotcha", getter.get("gotcha"));
		assertNull(getter.get(this));
		assertNull(getter.get(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GString()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() {
		int hashCode = new HashCodeBuilder(20121031, /*0*/80537).toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
