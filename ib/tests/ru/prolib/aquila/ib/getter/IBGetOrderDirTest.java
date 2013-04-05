package ru.prolib.aquila.ib.getter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.ib.getter.IBGetOrderDir;

import com.ib.client.Order;

/**
 * 2012-12-15<br>
 * $Id: IBGetOrderDirTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderDirTest {
	private static Order order;
	private static IBGetOrderDir getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		order = new Order();
		order.m_action = "hello";
		getter = new IBGetOrderDir();
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals("hello", getter.get(order));
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderDir()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 200323)
			.toHashCode(), getter.hashCode());
	}

}
