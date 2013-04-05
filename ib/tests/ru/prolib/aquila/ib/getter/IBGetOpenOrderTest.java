package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.getter.IBGetOpenOrder;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

/**
 * 2012-12-15<br>
 * $Id: IBGetOpenOrderTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOpenOrderTest {
	private static IMocksControl control;
	private static Order order;
	private static IBEventOpenOrder event;
	private static IBGetOpenOrder getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		order = new Order();
		Contract contract = new Contract();
		EventType type = control.createMock(EventType.class);
		OrderState orderState = control.createMock(OrderState.class);
		event = new IBEventOpenOrder(type, 12345, contract, order, orderState);
		getter = new IBGetOpenOrder();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		assertSame(order, getter.get(event));
	}
	
	@Test
	public void testGet_NullIfNotAnEvent() throws Exception {
		assertNull(getter.get(this));
		assertNull(getter.get(this));
		assertNull(getter.get(getter));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOpenOrder()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 134615)
			.toHashCode(), getter.hashCode());
	}

}
