package ru.prolib.aquila.core.report.trades;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class OrderPoolTradeSelectorTest {
	private IMocksControl control;
	private Trade trade;
	private Order order;
	private OrderPool orders;
	private OrderPoolTradeSelector selector;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		trade = control.createMock(Trade.class);
		order = control.createMock(Order.class);
		orders = control.createMock(OrderPool.class);
		selector = new OrderPoolTradeSelector(orders);
	}
	
	@Test
	public void testMustBeAdded() throws Exception {
		expect(orders.isPooled(same(order))).andReturn(true);
		expect(orders.isPooled(same(order))).andReturn(false);
		control.replay();
		
		assertTrue(selector.mustBeAdded(trade, order));
		assertFalse(selector.mustBeAdded(trade, order));
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		OrderPool orders2 = control.createMock(OrderPool.class);
		assertTrue(selector.equals(selector));
		assertTrue(selector.equals(new OrderPoolTradeSelector(orders)));
		assertFalse(selector.equals(null));
		assertFalse(selector.equals(this));
		assertFalse(selector.equals(new OrderPoolTradeSelector(orders2)));
	}
	
	@Test
	public void testGetOrderPool() throws Exception {
		assertSame(orders, selector.getOrderPool());
	}

}
