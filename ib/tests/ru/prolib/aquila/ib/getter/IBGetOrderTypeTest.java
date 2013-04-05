package ru.prolib.aquila.ib.getter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.ib.getter.IBGetOrderType;

import com.ib.client.Order;

/**
 * 2012-12-16<br>
 * $Id: IBGetOrderTypeTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderTypeTest {
	private static Order order;
	private static IBGetOrderType getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		order = new Order();
		order.m_orderType = "hello";
		getter = new IBGetOrderType();
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
		assertTrue(getter.equals(new IBGetOrderType()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121217, 5425)
			.toHashCode(), getter.hashCode());
	}

}
