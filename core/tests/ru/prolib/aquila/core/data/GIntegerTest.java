package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-08-22<br>
 * $Id: GIntegerTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GIntegerTest {
	private static GInteger getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getter = new GInteger();
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals((Integer)1, getter.get(1));
		assertEquals((Integer)123, getter.get(123.456d));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GInteger()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/54031).toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
