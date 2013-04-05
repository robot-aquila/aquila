package ru.prolib.aquila.ChaosTheory;

import static org.junit.Assert.*;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.util.Variant;
import static org.easymock.EasyMock.*;

public class OrderImplTest {
	IMocksControl control;
	OrderImpl order;
	Observer observer;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		order = null;
	}
	
	@Test
	public void testConstructor_StopOrderWoComment() throws Exception {
		order = new OrderImpl(54321, Order.SELL, 100, 95.30d, 98.55d);
		assertTrue(order.isStopOrder());
		assertFalse(order.isLimitOrder());
		assertFalse(order.isMarketOrder());
		assertEquals(54321, order.getId());
		assertTrue(order.isSell());
		assertFalse(order.isBuy());
		assertEquals(100, order.getQty());
		assertEquals(95.30d, order.getStopPrice(), 0.01d);
		assertEquals(98.55d, order.getPrice(), 0.01d);
		assertNull(order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testConstructor_StopOrderWithComment() throws Exception {
		order = new OrderImpl(8888, Order.BUY, 10, 295.30d, 298.55d, "foobar");
		assertTrue(order.isStopOrder());
		assertFalse(order.isLimitOrder());
		assertFalse(order.isMarketOrder());
		assertEquals(8888, order.getId());
		assertFalse(order.isSell());
		assertTrue(order.isBuy());
		assertEquals(10, order.getQty());
		assertEquals(295.30d, order.getStopPrice(), 0.01d);
		assertEquals(298.55d, order.getPrice(), 0.01d);
		assertEquals("foobar", order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testConstructor_MarketOrderWoComment() throws Exception {
		order = new OrderImpl(123, Order.SELL, 15);
		assertFalse(order.isStopOrder());
		assertFalse(order.isLimitOrder());
		assertTrue(order.isMarketOrder());
		assertEquals(123, order.getId());
		assertTrue(order.isSell());
		assertFalse(order.isBuy());
		assertEquals(15, order.getQty());
		assertNull(order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testConstructor_MarketOrderWithComment() throws Exception {
		order = new OrderImpl(321, Order.BUY, 22, "Zulu4");
		assertFalse(order.isStopOrder());
		assertFalse(order.isLimitOrder());
		assertTrue(order.isMarketOrder());
		assertEquals(321, order.getId());
		assertFalse(order.isSell());
		assertTrue(order.isBuy());
		assertEquals(22, order.getQty());
		assertEquals("Zulu4", order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testConstructor_LimitOrderWoComment() throws Exception {
		order = new OrderImpl(789, Order.SELL, 1560, 22.95d);
		assertFalse(order.isStopOrder());
		assertTrue(order.isLimitOrder());
		assertFalse(order.isMarketOrder());
		assertEquals(789, order.getId());
		assertTrue(order.isSell());
		assertFalse(order.isBuy());
		assertEquals(1560, order.getQty());
		assertEquals(22.95d, order.getPrice(), 0.01d);
		assertNull(order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testConstructor_LimitOrderWithComment() throws Exception {
		order = new OrderImpl(111, Order.BUY, 60, 122.22d, "zelot");
		assertFalse(order.isStopOrder());
		assertTrue(order.isLimitOrder());
		assertFalse(order.isMarketOrder());
		assertEquals(111, order.getId());
		assertFalse(order.isSell());
		assertTrue(order.isBuy());
		assertEquals(60, order.getQty());
		assertEquals(122.22d, order.getPrice(), 0.01d);
		assertEquals("zelot", order.getComment());
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	@Test
	public void testGetPrice_ThrowsIfMarketOrder() throws Exception {
		order = new OrderImpl(567, Order.SELL, 120);
		try {
			order.getPrice();
			fail("Expected exception: " + OrderException.class.getName());
		} catch ( OrderException e ) {
			assertEquals("This order has no price", e.getMessage());
		}
	}
	
	@Test
	public void testGetStopPrice_ThrowsIfMarketOrLimitOrder() throws Exception {
		order = new OrderImpl(789, Order.BUY, 80, 190.35d);
		try {
			order.getStopPrice();
			fail("Expected exception: " + OrderException.class.getName());
		} catch ( OrderException e ) {
			assertEquals("This order has no stop-price", e.getMessage());
		}
	}
	
	@Test
	public void testGetRelatedOrder_ThrowsIfNotStopOrder() throws Exception {
		order = new OrderImpl(1233, Order.SELL, 100);
		try {
			order.getRelatedOrder();
			fail("Expected exception: " + OrderException.class.getName());
		} catch ( OrderException e ) {
			assertEquals("This works only for stop-orders", e.getMessage());
		}
	}
	
	@Test
	public void testGetRelatedOrder_NullIfNotFilled() throws Exception {
		order = new OrderImpl(1111, Order.BUY, 10, 25.10d, 30.10d);
		assertNull(order.getRelatedOrder());
	}
	
	@Test
	public void testActivate_Ok() throws Exception {
		order = new OrderImpl(2323, Order.SELL, 1, 100.00d);
		order.addObserver(observer);
		assertEquals(Order.PENDING, order.getStatus());
		control.replay();
		
		order.activate();
		
		control.verify();
		assertEquals(1, order.countObservers());
		assertEquals(Order.ACTIVE, order.getStatus());
	}
	
	@Test
	public void testActivate_OkIfActive() throws Exception {
		order = new OrderImpl(2323, Order.SELL, 1, 100.00d);
		order.activate();
		control.replay();
		
		order.addObserver(observer);
		order.activate();
		
		control.verify();
		assertEquals(1, order.countObservers());
		assertEquals(Order.ACTIVE, order.getStatus());
	}
	
	@Test
	public void testFill0_Ok() throws Exception {
		order = new OrderImpl(6789, Order.BUY, 1, 200.0d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		order.activate();
		order.fill();
		
		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after fill
		assertEquals(Order.FILLED, order.getStatus());
	}
	
	@Test
	public void testFill0_OkIfNotActive() throws Exception {
		order = new OrderImpl(5678, Order.SELL, 1, 20d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		order.fill();

		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after fill
		assertEquals(Order.FILLED, order.getStatus());
	}
	
	@Test (expected=OrderException.class)
	public void testFill0_ThrowsIfStopOrder() throws Exception {
		order = new OrderImpl(111, Order.BUY, 1, 10d, 15d);
		order.activate();
		order.fill();
	}
	
	@Test
	public void testFill1_Ok() throws Exception {
		order = new OrderImpl(12345, Order.SELL, 100, 10.00d, 13.00d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		order.activate();
		Order limitOrder = order.fill(12346);
		
		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after fill
		assertNotNull(limitOrder);
		assertSame(limitOrder, order.getRelatedOrder());
		assertEquals(Order.SELL, limitOrder.getType());
		assertEquals(12346, limitOrder.getId());
		assertEquals(100, limitOrder.getQty());
		assertEquals(13.00, limitOrder.getPrice(), 0.01d);
		assertEquals(Order.ACTIVE, limitOrder.getStatus());
	}
	
	@Test
	public void testFill1_OkIfNotActive() throws Exception {
		order = new OrderImpl(12345, Order.SELL, 100, 10.00d, 13.00d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		Order limitOrder = order.fill(12346);

		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after fill
		assertNotNull(limitOrder);
		assertSame(limitOrder, order.getRelatedOrder());
		assertEquals(Order.SELL, limitOrder.getType());
		assertEquals(12346, limitOrder.getId());
		assertEquals(100, limitOrder.getQty());
		assertEquals(13.00, limitOrder.getPrice(), 0.01d);
		assertEquals(Order.ACTIVE, limitOrder.getStatus());
	}
	
	@Test (expected=OrderException.class)
	public void testFill1_ThrowsIfNotStopOrder() throws Exception {
		order = new OrderImpl(12345, Order.SELL, 100, 10.00d);
		order.activate();
		order.fill(12346);
	}
	
	@Test
	public void testKill_Ok() throws Exception {
		order = new OrderImpl(2222, Order.BUY, 20, 10.00d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		order.activate();
		order.kill();
		
		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after kill
		assertEquals(Order.KILLED, order.getStatus());
	}
	
	@Test
	public void testKill_OkIfNotActive() throws Exception {
		order = new OrderImpl(2222, Order.BUY, 20, 10.00d);
		observer.update(order, null);
		control.replay();
		
		order.addObserver(observer);
		order.kill();
		
		control.verify();
		assertEquals(0, order.countObservers()); // clear observers after kill
		assertEquals(Order.KILLED, order.getStatus());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> id = new Variant<Long>(new Long[] {1L, 2L});
		Variant<Integer> qty = new Variant<Integer>(new Integer[]{100, 200}, id);
		Variant<Integer> status = new Variant<Integer>(new Integer[]{Order.PENDING, Order.ACTIVE, Order.FILLED, Order.KILLED}, qty);
		Variant<Integer> type = new Variant<Integer>(new Integer[]{Order.BUY, Order.SELL}, status);
		Variant<Double> price = new Variant<Double>(new Double[]{10.0d, 90.0d, null}, type);
		Variant<Double> stopPrice = new Variant<Double>(new Double[]{null, null, 120.0d}, price); 
		Variant<Long> related = new Variant<Long>(new Long[]{123L, 0L}, stopPrice);
		Variant<String> msg = new Variant<String>(new String[]{null, "test comment"}, related);

		Order expected = new OrderImpl(1L, Order.SELL, 200,
									   120.0d, 90.0d, "test comment");
		expected.activate();
		expected.fill(123L);
		Variant<?> root = msg;
		Order found = null;
		int numfound = 0;
		do {
			Order current = new OrderImpl(id.get(), type.get(), qty.get(),
								       stopPrice.get(), price.get(), msg.get());
			switch ( status.get() ) {
			case Order.ACTIVE:
			case Order.FILLED:
			case Order.KILLED:
				current.activate();
				switch ( status.get() ) {
				case Order.KILLED:
					current.kill();
					break;
				case Order.FILLED:
					if ( price.get() == null || stopPrice.get() == null ) {
						current.fill();
					} else {
						current.fill(related.get());
					}
					break;
				}
				break;
			}
			if ( current.equals(expected) ) {
				found = current;
				numfound ++;
			}
		} while ( root.next() );
		assertEquals(1, numfound);
		assertEquals(1L, found.getId());
		assertEquals(Order.SELL, found.getType());
		assertEquals(200, found.getQty());
		assertEquals(120.0d, found.getStopPrice(), 0.001d);
		assertEquals(90.0d, found.getPrice(), 0.001d);
		assertEquals("test comment", found.getComment());
		assertEquals(Order.FILLED, found.getStatus());
		assertEquals(123L, found.getRelatedOrder().getId());
	}

}
