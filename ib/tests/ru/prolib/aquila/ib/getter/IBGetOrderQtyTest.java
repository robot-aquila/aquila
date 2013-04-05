package ru.prolib.aquila.ib.getter;


import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.ib.getter.IBGetOrderQty;

import com.ib.client.Order;

/**
 * 2012-12-16<br>
 * $Id: IBGetOrderQtyTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderQtyTest {
	private static Order order;
	private static IBGetOrderQty getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		order = new Order();
		order.m_totalQuantity = 200;
		getter = new IBGetOrderQty();
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals(new Long(200), (Long) getter.get(order));
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderQty()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121217, 13959)
			.toHashCode(), getter.hashCode());
	}


}
