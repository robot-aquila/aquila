package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolverStd;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-12-14<br>
 * $Id: OrderResolverStdTest.java 338 2012-12-15 10:20:43Z whirlwind $
 */
public class OrderResolverStdTest {
	private static IMocksControl control;
	private static EditableOrders orders;
	private static OrderFactory factory;
	private static EditableOrder order;
	private static OrderResolverStd resolver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		orders = control.createMock(EditableOrders.class);
		factory = control.createMock(OrderFactory.class);
		order = control.createMock(EditableOrder.class);
		resolver = new OrderResolverStd(orders, factory);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testResolveOrder_PendingOrder() throws Exception {
		expect(orders.isPendingOrder(100L)).andReturn(true);
		expect(orders.movePendingOrder(100L, 200L)).andReturn(order);
		control.replay();
		
		assertSame(order, resolver.resolveOrder(200L, 100L));
		
		control.verify();
	}
	
	@Test
	public void testResolveOrder_NotPendingAndNewOrder() throws Exception {
		expect(orders.isPendingOrder(100L)).andReturn(false);
		expect(orders.isOrderExists(200L)).andReturn(false);
		expect(factory.createOrder()).andReturn(order);
		orders.registerOrder(eq(200L), same(order));
		control.replay();
		
		assertSame(order, resolver.resolveOrder(200L, 100L));
		
		control.verify();
	}

	@Test
	public void testResolveOrder_NotPendingAndExistingOrder() throws Exception {
		expect(orders.isPendingOrder(100L)).andReturn(false);
		expect(orders.isOrderExists(200L)).andReturn(true);
		expect(orders.getEditableOrder(200L)).andReturn(order);
		control.replay();
		
		assertSame(order, resolver.resolveOrder(200L, 100L));
		
		control.verify();
	}
	
	@Test
	public void testResolveOrder_NullTransIdAndNewOrder() throws Exception {
		expect(orders.isOrderExists(200L)).andReturn(false);
		expect(factory.createOrder()).andReturn(order);
		orders.registerOrder(eq(200L), same(order));
		control.replay();
		
		assertEquals(order, resolver.resolveOrder(200L, null));
		
		control.verify();
	}

	@Test
	public void testResolveOrder_NullTransIdAndExistingOrder()
			throws Exception
	{
		expect(orders.isOrderExists(eq(200L))).andReturn(true);
		expect(orders.getEditableOrder(eq(200L))).andReturn(order);
		control.replay();
		assertEquals(order, resolver.resolveOrder(200L, null));
		control.verify();
	}

	@Test
	public void testEquals() throws Exception {
		Variant<EditableOrders> vOrds = new Variant<EditableOrders>()
			.add(orders)
			.add(control.createMock(EditableOrders.class));
		Variant<OrderFactory> vFact = new Variant<OrderFactory>(vOrds)
			.add(factory)
			.add(control.createMock(OrderFactory.class));
		Variant<?> iterator = vFact;
		int foundCnt = 0;
		OrderResolverStd x = null, found = null;
		do {
			x = new OrderResolverStd(vOrds.get(), vFact.get());
			if ( resolver.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(orders, found.getOrders());
		assertSame(factory, found.getOrderFactory());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(resolver.equals(resolver));
		assertFalse(resolver.equals(null));
		assertFalse(resolver.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121215, 3611)
			.append(orders)
			.append(factory)
			.toHashCode();
		assertEquals(hashCode, resolver.hashCode());
	}

}
