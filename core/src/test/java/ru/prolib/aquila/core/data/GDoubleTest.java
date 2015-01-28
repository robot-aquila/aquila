package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-08-22<br>
 * $Id: GDoubleTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GDoubleTest {
	private static GDouble getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getter = new GDouble();
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals(123.456d, getter.get(123.456d), 0.0001d);
		assertEquals(123d, getter.get(123), 0.0001d);
		assertNull(getter.get(this));
		assertNull(getter.get(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GDouble()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/53545).toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}
	
}
