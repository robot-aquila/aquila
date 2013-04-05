package ru.prolib.aquila.ta.ds.quik;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Observer;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.rxltdde.Xlt.Table;
import ru.prolib.aquila.ta.ds.quik.PortfolioOrdersQuik.OrderEntry;
import ru.prolib.aquila.ta.ds.quik.PortfolioOrdersQuik.StopOrderEntry;
import ru.prolib.aquila.util.LateNotify;
import ru.prolib.aquila.util.LateNotifyAction;

public class PortfolioOrdersQuikTest {
	IMocksControl control;
	PortfolioOrdersQuik orders;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orders = new PortfolioOrdersQuik();
	}
	
	@Test
	public void testStartWatch_StopOrder() throws Exception {
		Observer observer = control.createMock(Observer.class);
		OrderImpl order = new OrderImpl(12345L, Order.SELL, 10, 23.19d, 23.10d);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", Order.class)
			.createMock(control);
		observer.update(orders, order);
		orders.checkStopOrder(same(order));
		control.replay();
		
		orders.addObserver(observer);
		orders.startWatch(order);
		
		control.verify();
		assertSame(order, orders.watchStopOrders.get(12345L));
	}
	
	@Test
	public void testStartWatch_Order() throws Exception {
		Observer observer = control.createMock(Observer.class);
		OrderImpl order = new OrderImpl(12345L, Order.BUY, 10, 100.00d);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", Order.class)
			.createMock(control);
		observer.update(orders, order);
		orders.checkOrder(same(order));
		control.replay();
		
		orders.addObserver(observer);
		orders.startWatch(order);
		
		control.verify();
		assertSame(order, orders.watchOrders.get(12345L));
	}
	
	@Test
	public void testOnTable_OrdersTable() throws Exception {
		Object[] cells = {
			// Номер, Состояние
			new Double(100.00d), "Активна",
			new Double(101.00d), "Снята",
			new Double(102.00d), "Исполнена",
			new Double(103.00d), "Активна",
		};
		Table table = new Table(cells, "[export]orders", "R1C1:R1C1", 2);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("updateOrder", long.class, int.class)
			.createMock();
		orders.updateOrder(100L, Order.ACTIVE);
		orders.updateOrder(101L, Order.KILLED);
		orders.updateOrder(102L, Order.FILLED);
		orders.updateOrder(103L, Order.ACTIVE);
		replay(orders);
		
		orders.onTable(table);

		verify(orders);
	}
	
	@Test
	public void testOnTable_StopOrdersTable() throws Exception {
		Object[] cells = {
			// Номер, Состояние, Номер заявки
			new Double(4001.00d), "Активна",   new Double(  0.00d),
			new Double(4002.00d), "Исполнена", new Double(100.00d),
			new Double(4003.00d), "Активна",   new Double(  0.00d),
			new Double(4004.00d), "Снята",     new Double(  0.00d),
		};
		Table table = new Table(cells, "[export]stop-orders", "R1C1:R1C1", 3);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("updateStopOrder",long.class,int.class,long.class)
			.createMock();
		orders.updateStopOrder(4001L, Order.ACTIVE, 0L);
		orders.updateStopOrder(4002L, Order.FILLED, 100L);
		orders.updateStopOrder(4003L, Order.ACTIVE, 0L);
		orders.updateStopOrder(4004L, Order.KILLED, 0L);
		replay(orders);
		
		orders.onTable(table);
		
		verify(orders);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация о заявке уже закеширована и новый статус отличен от кеша
	 * @throws Exception
	 */
	@Test
	public void testUpdateOrder_CacheHit() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", long.class)
			.createMock();
		OrderEntry cache = new OrderEntry(Order.PENDING);
		orders.cacheOrders.put(12345L, cache);
		orders.checkOrder(12345L);
		replay(orders);
		
		orders.updateOrder(12345L, Order.FILLED);
		
		verify(orders);
		assertSame(cache, orders.cacheOrders.get(12345L));
		assertEquals(Order.FILLED, cache.status);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация о заявке еще не закеширована
	 * @throws Exception
	 */
	@Test
	public void testUpdateOrder_CacheMiss() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", long.class)
			.createMock();
		orders.checkOrder(54321L);
		replay(orders);
		
		orders.updateOrder(54321L, Order.ACTIVE);
		
		verify(orders);
		OrderEntry cache = orders.cacheOrders.get(54321L);
		assertNotNull(cache);
		assertEquals(Order.ACTIVE, cache.status);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация о заявке закеширована, но не изменена
	 * @throws Exception
	 */
	@Test
	public void testUpdateOrder_StatusNotChanged() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", long.class)
			.createMock();
		OrderEntry cache = new OrderEntry(Order.ACTIVE);
		orders.cacheOrders.put(12345L, cache);
		replay(orders);
		
		orders.updateOrder(12345L, Order.ACTIVE);
		
		verify(orders);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация стоп-заявки уже закеширована и статус изменен
	 * @throws Exception
	 */
	@Test
	public void testUpdateStopOrder_CacheHit() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", long.class)
			.createMock();
		StopOrderEntry cache = new StopOrderEntry(Order.ACTIVE, 0L);
		orders.cacheStopOrders.put(876L, cache);
		orders.checkStopOrder(876L);
		replay(orders);
		
		orders.updateStopOrder(876L, Order.FILLED, 123L);
		
		verify(orders);
		assertSame(cache, orders.cacheStopOrders.get(876L));
		assertEquals(Order.FILLED, cache.status);
		assertEquals(123L, cache.relatedId);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация стоп-заявки не закеширована
	 * @throws Exception
	 */
	@Test
	public void testUpdateStopOrder_CacheMiss() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", long.class)
			.createMock();
		orders.checkStopOrder(555L);
		replay(orders);
		
		orders.updateStopOrder(555L, Order.KILLED, 111L);
		
		verify(orders);
		StopOrderEntry cache = orders.cacheStopOrders.get(555L);
		assertNotNull(cache);
		assertEquals(Order.KILLED, cache.status);
		assertEquals(111L, cache.relatedId);
	}
	
	/**
	 * Тестируются ситуации:
	 * - информация закеширована, но статус не изменен
	 * @throws Exception
	 */
	@Test
	public void testUpdateStopOrder_StatusNotChanged() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", long.class)
			.createMock();
		StopOrderEntry cache = new StopOrderEntry(Order.FILLED, 321L);
		orders.cacheStopOrders.put(876L, cache);
		replay(orders);
		
		orders.updateStopOrder(876L, Order.FILLED, 123L);
		
		verify(orders);
		assertSame(cache, orders.cacheStopOrders.get(876L));
		assertEquals(Order.FILLED, cache.status);
		assertEquals(321L, cache.relatedId);
	}
	
	/**
	 * Тестируются ситуации:
	 * - нет информации о заявке
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder1_CacheMiss() throws Exception {
		Order order = new OrderImpl(890L, Order.BUY, 100, 100.00d);
		orders = new PortfolioOrdersQuik();
		orders.checkOrder(order);
		
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	/**
	 * Тестируются ситуации:
	 * - при наличии информации в кеше заявка активируется
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder1_ActivatePendingOrder() throws Exception {
		Order order = new OrderImpl(890L, Order.BUY, 100, 100.00d);
		OrderEntry cache = new OrderEntry(Order.ACTIVE);
		orders = new PortfolioOrdersQuik();
		orders.cacheOrders.put(890L, cache);
		
		orders.checkOrder(order);

		assertEquals(Order.ACTIVE, order.getStatus());
	}
	
	/**
	 * Тестируются ситуации:
	 * - заявка исполнена
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder1_Fill() throws Exception {
		Order order = new OrderImpl(890L, Order.BUY, 100, 100.00d);
		OrderEntry cache = new OrderEntry(Order.FILLED);
		orders = new PortfolioOrdersQuik();
		orders.cacheOrders.put(890L, cache);
		
		orders.checkOrder(order);

		assertEquals(Order.FILLED, order.getStatus());
		assertNull(orders.cacheOrders.get(890L));
		assertNull(orders.watchOrders.get(890L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - заявка снята
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder1_Kill() throws Exception {
		Order order = new OrderImpl(890L, Order.BUY, 100, 100.00d);
		OrderEntry cache = new OrderEntry(Order.KILLED);
		orders = new PortfolioOrdersQuik();
		orders.cacheOrders.put(890L, cache);
		
		orders.checkOrder(order);

		assertEquals(Order.KILLED, order.getStatus());
		assertNull(orders.cacheOrders.get(890L));
		assertNull(orders.watchOrders.get(890L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - нет информации о стоп-заявке
	 * @throws Exception
	 */
	@Test
	public void testCheckStopOrde1r_CacheMiss() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.SELL, 10,  90.00d, 80.00d);
		orders = new PortfolioOrdersQuik();
		
		orders.checkStopOrder(order);
		
		assertEquals(Order.PENDING, order.getStatus());
	}
	
	/**
	 * Тестируются ситуации:
	 * - при наличии информации в кеше стоп-заявка активируется
	 * @throws Exception
	 */
	@Test
	public void testCheckStopOrder1_ActivatePendingOrder() throws Exception {
		OrderImpl order = new OrderImpl(222L, Order.BUY, 20,  190.00d, 200.00d);
		StopOrderEntry cache = new StopOrderEntry(Order.ACTIVE, 0L);
		orders = new PortfolioOrdersQuik();
		orders.cacheStopOrders.put(222L, cache);
		
		orders.checkStopOrder(order);
	
		assertEquals(Order.ACTIVE, order.getStatus());
	}
	
	/**
	 * Тестируются ситуации:
	 * - стоп-заявка активируется
	 * @throws Exception
	 */
	@Test
	public void testCheckStopOrder1_Fill() throws Exception {
		OrderImpl order = new OrderImpl(111L, Order.SELL, 10, 110.00d, 100.00d);
		StopOrderEntry cache = new StopOrderEntry(Order.FILLED, 112L);
		orders = new PortfolioOrdersQuik();
		orders.cacheStopOrders.put(111L, cache);
		
		orders.checkStopOrder(order);
		
		assertEquals(Order.FILLED, order.getStatus());
		assertNull(orders.cacheStopOrders.get(111L));
		assertNull(orders.watchStopOrders.get(111L));
		
		Order relatedOrder = order.getRelatedOrder();
		assertNotNull(relatedOrder);
		assertSame(relatedOrder, orders.watchOrders.get(112L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - стоп-заявка снимается
	 * @throws Exception
	 */
	@Test
	public void testCheckStopOrder1_Kill() throws Exception {
		OrderImpl order = new OrderImpl(222L, Order.BUY, 1, 255000d, 255005d);
		StopOrderEntry cache = new StopOrderEntry(Order.KILLED, 111L);
		orders = new PortfolioOrdersQuik();
		orders.cacheStopOrders.put(222L, cache);
		
		orders.checkStopOrder(order);
		
		assertEquals(Order.KILLED, order.getStatus());
		assertNull(orders.cacheStopOrders.get(222L));
		assertNull(orders.watchStopOrders.get(222L));
		assertNull(order.getRelatedOrder());
		assertNull(orders.cacheOrders.get(111L));
	}
	
	@Test
	public void testCheckOrder2_WatchListMiss() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", Order.class)
			.createMock();
		replay(orders);
		
		orders.checkOrder(123L);
		
		verify(orders);
	}
	
	@Test
	public void testCheckOrder2_WatchListHit() throws Exception {
		OrderImpl order = new OrderImpl(123L, Order.BUY, 10, 100d);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkOrder", Order.class)
			.createMock();
		orders.watchOrders.put(123L, order);
		orders.checkOrder(same(order));
		replay(orders);
		
		orders.checkOrder(123L);
		
		verify(orders);
	}
	
	@Test
	public void testCheckStopOrder2_WatchListMiss() throws Exception {
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", Order.class)
			.createMock();
		replay(orders);
		
		orders.checkStopOrder(111l);
		
		verify(orders);
	}
	
	@Test
	public void testCheckStopOrder2_WatchListHit() throws Exception {
		OrderImpl order = new OrderImpl(222L, Order.SELL, 1, 1100.00d, 1090.00d);
		orders = createMockBuilder(PortfolioOrdersQuik.class)
			.withConstructor()
			.addMockedMethod("checkStopOrder", Order.class)
			.createMock();
		orders.watchStopOrders.put(222L, order);
		orders.checkStopOrder(order);
		replay(orders);
		
		orders.checkStopOrder(222L);
		
		verify(orders);
	}
	
	@Test
	public void testRegisterHandler() throws Exception {
		RXltDdeDispatcher d = createMockBuilder(RXltDdeDispatcher.class)
			.withConstructor()
			.addMockedMethod("add")
			.createMock();
		d.add("[export]orders", orders);
		d.add("[export]stop-orders", orders);
		replay(d);
		
		orders.registerHandler(d);
		
		verify(d);
	}
	
	@Test
	public void testUnregisterHandler() throws Exception {
		RXltDdeDispatcher d = createMockBuilder(RXltDdeDispatcher.class)
			.withConstructor()
			.addMockedMethod("remove")
			.createMock();
		d.remove("[export]orders", orders);
		d.remove("[export]stop-orders", orders);
		replay(d);
		
		orders.unregisterHandler(d);
		
		verify(d);
	}
	
	@Test
	public void testWaitForComplete2_Ok() throws Exception {
		final Order order = new OrderImpl(123L, Order.BUY, 1, 100.00d);
		order.activate();
		new LateNotify(order, 6, 100, new LateNotifyAction(){
			
				@Override
				public void execute() throws Exception {
					order.kill();
				}
			
			}).start();
		
		orders.waitForComplete(order, 700);
		
		assertEquals(Order.KILLED, order.getStatus());
	}
	
	@Test
	public void testWaitForComplete2_Timeout() throws Exception {
		final Order order = new OrderImpl(123L, Order.BUY, 1, 100.00d);
		order.activate();
		new LateNotify(order, 100, 5).start();
		
		try {
			orders.waitForComplete(order, 700);
			fail("Expected exception: " + PortfolioTimeoutException.class);
		} catch ( PortfolioTimeoutException e ) {
			
		}
		
		assertEquals(Order.ACTIVE, order.getStatus());
	}
	
	@Test
	public void testGetActiveOrders() throws Exception {
		Order stop1 = new OrderImpl(1L, Order.SELL, 1, 100.00d, 95.00d);
		Order stop2 = new OrderImpl(2L, Order.BUY, 10, 110.00d, 112.00d);
		Order order1 = new OrderImpl(1L, Order.BUY, 1, 154.00d);
		Order order2 = new OrderImpl(2L, Order.SELL,1, 130.00d);
		orders.startWatch(stop1);
		orders.startWatch(stop2);
		orders.startWatch(order1);
		orders.startWatch(order2);
		
		Set<Order> active = orders.getActiveOrders();
		assertNotNull(active);
		assertEquals(4, active.size());
		assertTrue(active.contains(stop1));
		assertTrue(active.contains(stop2));
		assertTrue(active.contains(order1));
		assertTrue(active.contains(order2));
	}

}
