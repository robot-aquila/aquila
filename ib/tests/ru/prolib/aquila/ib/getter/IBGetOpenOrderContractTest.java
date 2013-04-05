package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.getter.IBGetOpenOrderContract;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

/**
 * 2012-12-15<br>
 * $Id: IBGetOpenOrderContractTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOpenOrderContractTest {
	private static IMocksControl control;
	private static Contract contract;
	private static IBEventOpenOrder event;
	private static IBGetOpenOrderContract getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		contract = new Contract();
		Order order = new Order();
		EventType type = control.createMock(EventType.class);
		OrderState orderState = control.createMock(OrderState.class);
		event = new IBEventOpenOrder(type, 12345, contract, order, orderState);
		getter = new IBGetOpenOrderContract();
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		assertSame(contract, getter.get(event));
	}
	
	@Test
	public void testGet_IfNotAnEvent() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
		assertNull(getter.get(getter));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOpenOrderContract()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 163533)
			.toHashCode(), getter.hashCode());
	}

}
