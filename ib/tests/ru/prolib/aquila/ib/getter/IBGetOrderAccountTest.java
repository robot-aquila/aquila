package ru.prolib.aquila.ib.getter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.ib.getter.IBGetOrderAccount;

import com.ib.client.Order;

/**
 * 2012-12-15<br>
 * $Id: IBGetOrderAccountTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderAccountTest {
	private static Order order;
	private static IBGetOrderAccount getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		order = new Order();
		order.m_account = "TEST";
		getter = new IBGetOrderAccount();
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals("TEST", getter.get(order));
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(this));
		assertNull(getter.get(null));
		assertNull(getter.get(getter));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderAccount()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 122703)
			.toHashCode(), getter.hashCode());
	}

}
