package ru.prolib.aquila.test;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;


public class TestPortfolioStateTest {
	IMocksControl control;
	Observer observer;
	AssetImpl asset;
	PortfolioOrders orders;
	TestPortfolioState state;
	TestPortfolioStateController ctrl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 5d, 0);
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		orders = control.createMock(PortfolioOrders.class);
		ctrl = control.createMock(TestPortfolioStateController.class);
		state = new TestPortfolioState(asset, ctrl);
	}
	
	@Test
	public void testConstructor2() throws Exception {
		assertSame(asset, state.getAsset());
		assertSame(ctrl, state.getController());
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		Asset asset = control.createMock(Asset.class);
		state = new TestPortfolioState(asset, ctrl);
		asset.addObserver(state);
		orders.addObserver(state);
		control.replay();
		
		state.startService(orders);
		
		control.verify();
		assertSame(orders, state.orders);
	}
	
	@Test (expected=PortfolioException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		state.orders = orders;
		control.replay();
		
		state.startService(orders);
	}
	
	@Test
	public void testStopService_OkNotStarted() throws Exception {
		control.replay();
		
		state.stopService();
		
		control.verify();
	}
	
	@Test
	public void testStopService_OkStarted() throws Exception {
		Asset asset = control.createMock(Asset.class);
		state = new TestPortfolioState(asset, ctrl);
		state.orders = orders;
		asset.deleteObserver(state);
		orders.deleteObserver(state);

		control.replay();
		
		state.stopService();
		
		control.verify();
		assertNull(state.orders);
	}
	
	@Test
	public void testGetMoney() throws Exception {
		expect(ctrl.getMoney()).andReturn(12345.0d);
		control.replay();
		
		assertEquals(12345.0d, state.getMoney(), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		expect(ctrl.getPosition()).andReturn(-10);
		control.replay();
		
		assertEquals(-10, state.getPosition());
		
		control.verify();
	}
	
	@Test
	public void testSetMoney() throws Exception {
		ctrl.setMoney(115.35d);
		control.replay();
		
		state.setMoney(115.35d);
		
		control.verify();
	}
	
	@Test
	public void testSetPosition() throws Exception {
		ctrl.setPosition(10);
		control.replay();
		
		state.setPosition(10);
		
		control.verify();
	}
	
	@Test
	public void testGetInitialMargin() throws Exception {
		expect(ctrl.getInitialMargin()).andReturn(123.45d);
		control.replay();
		
		assertEquals(123.45d, state.getInitialMargin(), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testGetVariationMargin() throws Exception {
		expect(ctrl.getVariationMargin()).andReturn(1000d);
		control.replay();
		
		assertEquals(1000d, state.getVariationMargin(), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testWaitForNeutralPosition_Ok() throws Exception {
		expect(ctrl.getPosition()).andReturn(0);
		control.replay();
		
		state.waitForNeutralPosition(1000);
		
		control.verify();
	}
	
	@Test (expected=PortfolioTimeoutException.class)
	public void testWaitForNeutralPosition_Timeout() throws Exception {
		expect(ctrl.getPosition()).andReturn(-1);
		control.replay();
		
		state.waitForNeutralPosition(1000);
	}
	
	@Test
	public void testUpdate_FromOrdersIgnoreStopOrder() throws Exception {
		TestPortfolioOrders orders = new TestPortfolioOrders();
		state.orders = orders;
		Order order = control.createMock(Order.class);
		expect(order.isMarketOrder()).andReturn(false);
		expect(order.isLimitOrder()).andReturn(false);
		control.replay();

		state.update(orders, order);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_FromOrdersListenLimitOrder() throws Exception {
		TestPortfolioOrders orders = new TestPortfolioOrders();
		state.orders = orders;
		Order order = control.createMock(Order.class);
		expect(order.isMarketOrder()).andReturn(false);
		expect(order.isLimitOrder()).andReturn(true);
		order.addObserver(state);
		control.replay();
		
		state.update(orders, order);
		
		control.verify();
	}

	@Test
	public void testUpdate_FromOrdersListenMarketOrder() throws Exception {
		TestPortfolioOrders orders = new TestPortfolioOrders();
		state.orders = orders;
		Order order = control.createMock(Order.class);
		expect(order.isMarketOrder()).andReturn(true);
		order.addObserver(state);
		control.replay();
		
		state.update(orders, order);
		
		control.verify();
	}

	@Test
	public void testUpdate_MarketBuyOrderFilled() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 1);
		order.activate();
		order.fill();
		ctrl.changePosition(1);
		observer.update(state, null);
		control.replay();
		
		state.addObserver(observer);
		state.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_MarketSellOrderFilled() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.SELL, 5);
		order.activate();
		order.fill();
		ctrl.changePosition(-5);
		observer.update(state, null);
		control.replay();
		
		state.addObserver(observer);
		state.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_LimitBuyOrderFilled() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.BUY, 2, 125d);
		order.activate();
		order.fill();
		ctrl.changePosition(2, 125d);
		observer.update(state, null);
		control.replay();
		
		state.addObserver(observer);
		state.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_LimitSellOrderFilled() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.SELL, 10, 255d);
		order.activate();
		order.fill();
		ctrl.changePosition(-10, 255d);
		observer.update(state, null);
		control.replay();
		
		state.addObserver(observer);
		state.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_AssetEventPriceIgnored() throws Exception {
		control.replay();
		
		state.update(asset, Asset.EVENT_PRICE);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_AssetEventClearingProcessed() throws Exception {
		ctrl.closePeriod();
		ctrl.openPeriod(asset);
		control.replay();
		
		state.update(asset, Asset.EVENT_CLEARING);
		
		control.verify();
	}
	
	@Test
	public void testGetTotalMoney() throws Exception {
		expect(ctrl.getMoney()).andReturn(100.00d);
		expect(ctrl.getInitialMargin()).andReturn(80.00d);
		expect(ctrl.getVariationMargin()).andReturn(50.00d);
		control.replay();
		
		assertEquals(230.00d, state.getTotalMoney(), 0.01d);
		
		control.verify();
	}

}
