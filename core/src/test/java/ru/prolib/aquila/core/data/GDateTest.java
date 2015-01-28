package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-11-10<br>
 * $Id: GDateTest.java 313 2012-11-10 21:36:00Z whirlwind $
 */
public class GDateTest {
	private static GDate getter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getter = new GDate();
	}
	
	@Test
	public void testGet() throws Exception {
		Date d = new Date();
		assertSame(d, getter.get(d));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new GDate()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121111, 202623).toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
