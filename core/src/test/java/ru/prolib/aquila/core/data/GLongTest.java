package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-09-03<br>
 * $Id: GLongTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GLongTest {
	private static GLong getter;
	
	@BeforeClass
	public static void setUpbeforeClass() throws Exception {
		getter = new GLong();
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals((Long)123L, getter.get(123L));
		assertEquals((Long)123L, getter.get(123));
		assertEquals((Long)123L, getter.get(123.456d));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GLong()));
		assertFalse(getter.equals(new GInteger()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/54251).toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
