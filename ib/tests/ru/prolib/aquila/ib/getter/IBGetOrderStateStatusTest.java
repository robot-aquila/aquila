package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ib.getter.IBGetOrderStateStatus;

import com.ib.client.OrderState;


public class IBGetOrderStateStatusTest {
	private static IMocksControl control;
	private static OrderState orderState;
	private static IBGetOrderStateStatus getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		orderState = control.createMock(OrderState.class);
		getter = new IBGetOrderStateStatus();
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		orderState.m_status = "hello";
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals("hello", getter.get(orderState));
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderStateStatus()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121217, 20359)
			.toHashCode(), getter.hashCode());
	}

}
