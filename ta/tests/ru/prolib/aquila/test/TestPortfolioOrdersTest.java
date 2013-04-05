package ru.prolib.aquila.test;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReader;
import ru.prolib.aquila.util.Sequence;
import ru.prolib.aquila.util.SequenceLong;

public class TestPortfolioOrdersTest {
	IMocksControl control;
	TestPortfolioOrdersChecker checkerPeriod;
	TestPortfolioOrdersChecker checkerMoment;
	Sequence<Long> id;
	MarketDataReader dataReader;
	MarketDataImpl data;
	TestPortfolioOrders orders;
	Observer observer;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		checkerPeriod = control.createMock(TestPortfolioOrdersChecker.class);
		checkerMoment = control.createMock(TestPortfolioOrdersChecker.class);
		id = control.createMock(Sequence.class);
		dataReader = control.createMock(MarketDataReader.class);
		data = new MarketDataImpl(dataReader);
		observer = control.createMock(Observer.class);
		orders = new TestPortfolioOrders(checkerPeriod, checkerMoment, id);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		orders = new TestPortfolioOrders();
		TestPortfolioOrdersCheckerPeriod checker1 =
			(TestPortfolioOrdersCheckerPeriod) orders.getCheckerPeriod();
		assertNotNull(checker1);
		TestPortfolioOrdersCheckerMoment checker2 =
			(TestPortfolioOrdersCheckerMoment) orders.getCheckerMoment();
		assertNotNull(checker2);
		SequenceLong id = (SequenceLong) orders.getIdSeq();
		assertNotNull(id);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		orders = new TestPortfolioOrders(id);
		TestPortfolioOrdersCheckerPeriod checker1 =
			(TestPortfolioOrdersCheckerPeriod) orders.getCheckerPeriod();
		assertNotNull(checker1);
		TestPortfolioOrdersCheckerMoment checker2 =
			(TestPortfolioOrdersCheckerMoment) orders.getCheckerMoment();
		assertNotNull(checker2);
		assertSame(id, orders.getIdSeq());
	}

	@Test
	public void testConstruct2() throws Exception {
		assertSame(id, orders.getIdSeq());
		assertSame(checkerPeriod, orders.getCheckerPeriod());
		assertSame(checkerMoment, orders.getCheckerMoment());
	}

	@Test
	public void testStartService_Ok() throws Exception {
		data.addObserver(orders);
		control.replay();
		
		orders.startService(data);
		
		control.verify();
	}
	
	@Test (expected=PortfolioException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		orders.startService(data);
		orders.startService(data);
	}
	
	@Test
	public void testStopService_Ok() throws Exception {
		data.addObserver(orders); // from startService
		data.deleteObserver(orders);
		control.replay();
		
		orders.startService(data);
		orders.stopService();
		
		control.verify();
	}
	
	@Test
	public void testWaitForComplete_Filled() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1);
		order.activate();
		order.fill();
		
		orders.waitForComplete(order, 1000);
	}

	@Test
	public void testWaitForComplete_Killed() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1);
		order.activate();
		order.kill();
		
		orders.waitForComplete(order, 1000);
	}

	@Test (expected=PortfolioTimeoutException.class)
	public void testWaitForComplete_Timeout() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1);
		order.activate();
		
		orders.waitForComplete(order, 1000);
	}

	/**
	 * Тестируются ситуации:
	 * - стоп-заявка добавляется в список стоп-заявок
	 * - начинается наблюдение за стоп-заявкой
	 * - наблюдатели уведомляются о появлении новой стоп-заявки
	 * - стоп-заявка передается на проверку исполнения 
	 * @throws Exception
	 */
	@Test
	public void testStartWatch_StopOrder1() throws Exception {
		orders = createMockBuilder(TestPortfolioOrders.class)
			.withConstructor(checkerPeriod, checkerMoment, id)
			.addMockedMethod("checkOrder")
			.createMock(control);
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1, 100.00d, 110.00d);
		observer.update(orders, order);
		orders.checkOrder(checkerMoment, order);
		control.replay();
		
		orders.addObserver(observer);
		orders.startWatch(order);
		
		control.verify();
		assertEquals(1, order.countObservers());
		assertSame(order, orders.stopOrders.get(1L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - заявка добавляется в список заявок
	 * - начинается наблюдение за заявкой
	 * - наблюдатели уведомляются о появлении новой заявки
	 * - заявка передается на проверку исполнения
	 * @throws Exception
	 */
	@Test
	public void testStartWatch_Order1() throws Exception {
		orders = createMockBuilder(TestPortfolioOrders.class)
			.withConstructor(checkerPeriod, checkerMoment, id)
			.addMockedMethod("checkOrder")
			.createMock(control);
		Order order = control.createMock(Order.class);
		expect(order.isStopOrder()).andReturn(false);
		expect(order.getId()).andReturn(123L);
		order.addObserver(orders);
		observer.update(orders, order);
		orders.checkOrder(checkerMoment, order);
		control.replay();
		
		orders.addObserver(observer);
		orders.startWatch(order);
		
		control.verify();
		assertSame(order, orders.orders.get(123L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - исполненная стоп-заявка удаляется из списка стоп-заявок
	 * - список лимитных/рыночных заявок не модифицируется
	 * - отписка от наблюдения за стоп-заявкой
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder_StopOrderFilled() throws Exception {
		Order order = control.createMock(Order.class);
		Order stopOrder = control.createMock(Order.class);
		expect(stopOrder.getStatus()).andReturn(Order.FILLED);
		expect(stopOrder.isStopOrder()).andReturn(true);
		expect(stopOrder.getId()).andReturn(123L);
		stopOrder.deleteObserver(orders);
		orders.orders.put(123L, order);
		orders.stopOrders.put(123L, stopOrder);
		control.replay();
		
		orders.checkOrder(checkerPeriod, stopOrder);
		
		control.verify();
		assertNull(orders.stopOrders.get(123L));
		assertSame(order, orders.orders.get(123L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - отмененная заявка удаляется из списка заявок
	 * - список стоп-заявок не модифицируется
	 * - отписка от наблюдения за заявкой
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder_OrderKilled() throws Exception {
		Order stopOrder = control.createMock(Order.class);
		Order order = control.createMock(Order.class);
		expect(order.getStatus()).andReturn(Order.KILLED);
		expect(order.isStopOrder()).andReturn(false);
		expect(order.getId()).andReturn(123L);
		order.deleteObserver(orders);
		orders.orders.put(123L, order);
		orders.stopOrders.put(123L, stopOrder);
		control.replay();
		
		orders.checkOrder(checkerPeriod, order);
		
		control.verify();
		assertNull(orders.orders.get(123L));
		assertSame(stopOrder, orders.stopOrders.get(123L));
	}
	
	/**
	 * Тестируются ситуации:
	 * - через чекер определяется необходимость исполнения заявки
	 * - стоп-заявка исполняется, номер заявки берется из последовательности
	 * - новая заявка добавляется на контроль
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder_FillStopOrder() throws Exception {
		Order order = control.createMock(Order.class);
		Order stopOrder = control.createMock(Order.class);
		orders = createMockBuilder(TestPortfolioOrders.class)
			.withConstructor(checkerPeriod, checkerMoment, id)
			.addMockedMethod("startWatch")
			.createMock(control);
		orders.data = data;
		expect(stopOrder.getStatus()).andReturn(Order.PENDING);
		expect(checkerPeriod.canFill(stopOrder, data)).andReturn(true);
		expect(stopOrder.isStopOrder()).andReturn(true);
		expect(id.next()).andReturn(123L);
		expect(stopOrder.fill(123L)).andReturn(order);
		orders.startWatch(order);
		control.replay();
		
		orders.checkOrder(checkerPeriod, stopOrder);
		
		control.verify();
	}

	/**
	 * Тестируются ситуации:
	 * - через чекер определяется необходимость исполнения заявки
	 * - заявка исполняется
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder_FillOrder() throws Exception {
		Order order = control.createMock(Order.class);
		orders.data = data;
		expect(order.getStatus()).andReturn(Order.ACTIVE);
		expect(checkerPeriod.canFill(order, data)).andReturn(true);
		expect(order.isStopOrder()).andReturn(false);
		order.fill();
		control.replay();
		
		orders.checkOrder(checkerPeriod, order);
		
		control.verify();
	}
	
	/**
	 * Тестируются ситуации:
	 * - пропуск, т.к. заявка не удовлетворяет условиям исполнения
	 * @throws Exception
	 */
	@Test
	public void testCheckOrder_Skip() throws Exception {
		Order order = control.createMock(Order.class);
		orders.data = data;
		expect(order.getStatus()).andReturn(Order.PENDING);
		expect(checkerPeriod.canFill(order, data)).andReturn(false);
		control.replay();
		
		orders.checkOrder(checkerPeriod, order);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ForOrder() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1);
		orders = createMockBuilder(TestPortfolioOrders.class)
			.withConstructor(checkerPeriod, checkerMoment, id)
			.addMockedMethod("stopWatch")
			.createMock(control);
		orders.stopWatch(order);
		control.replay();
		
		orders.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ForMarketData() throws Exception {
		Order stop1 = control.createMock(Order.class);
		Order stop2 = control.createMock(Order.class);
		Order stop3 = control.createMock(Order.class);
		Order order1 = control.createMock(Order.class);
		Order order2 = control.createMock(Order.class);
		Order order3 = control.createMock(Order.class);
		orders = createMockBuilder(TestPortfolioOrders.class)
			.withConstructor(checkerPeriod, checkerMoment, id)
			.addMockedMethod("checkOrder")
			.createMock(control);
		orders.data = data;
		orders.stopOrders.put(1L, stop1);
		orders.stopOrders.put(2L, stop2);
		orders.stopOrders.put(3L, stop3);
		orders.orders.put(1L, order1);
		orders.orders.put(2L, order2);
		orders.orders.put(3L, order3);
		orders.checkOrder(checkerPeriod, stop1);
		orders.checkOrder(checkerPeriod, stop2);
		orders.checkOrder(checkerPeriod, stop3);
		orders.checkOrder(checkerPeriod, order1);
		orders.checkOrder(checkerPeriod, order2);
		orders.checkOrder(checkerPeriod, order3);
		control.replay();
		
		orders.update(data, null);
		
		control.verify();
	}
	
	@Test
	public void testGetActiveOrders() throws Exception {
		Order stop1 = new OrderImpl(1L, Order.SELL, 1, 100.00d, 95.00d);
		Order stop2 = new OrderImpl(2L, Order.BUY, 10, 110.00d, 112.00d);
		Order order1 = new OrderImpl(1L, Order.BUY, 1, 154.00d);
		Order order2 = new OrderImpl(2L, Order.SELL,1, 130.00d);
		orders.stopOrders.put(1L, stop1);
		orders.stopOrders.put(2L, stop2);
		orders.orders.put(1L, order1);
		orders.orders.put(2L, order2);
		
		Set<Order> active = orders.getActiveOrders();
		assertNotNull(active);
		assertEquals(4, active.size());
		assertTrue(active.contains(stop1));
		assertTrue(active.contains(stop2));
		assertTrue(active.contains(order1));
		assertTrue(active.contains(order2));
	}
	
}
