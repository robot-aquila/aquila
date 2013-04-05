package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.data.GConst;

/**
 * 2012-09-07<br>
 * $Id: GConstTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GConstTest {
	private static GConst<Integer> getter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		getter = new GConst<Integer>(12345);
	}
	
	@Test
	public void testGet() throws Exception {
		assertEquals((Integer) 12345, getter.get(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(new GConst<Integer>(12345)));
		assertFalse(getter.equals(new GConst<Integer>(123)));
		assertFalse(getter.equals(new GConst<String>("zulu24")));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/51607)
			.append(12345)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
