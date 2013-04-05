package ru.prolib.aquila.test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedHashSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ChaosTheory.AssetsImpl;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.PropsImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReaderFake;
import ru.prolib.aquila.util.Sequence;

public class TestPortfolioTest {
	IMocksControl control;
	Asset asset;
	Sequence<Long> id;
	PortfolioOrders orders;
	PortfolioState state;
	TestPortfolio port;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		asset = control.createMock(Asset.class);
		id = control.createMock(Sequence.class);
		orders = control.createMock(PortfolioOrders.class);
		state = control.createMock(PortfolioState.class);
		port = new TestPortfolio(asset, id, orders, state);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(asset, port.getAsset());
		assertSame(id, port.getIdSeq());
		assertSame(orders, port.getPortfolioOrders());
		assertSame(state, port.getPortfolioState());
	}
	
	@Test
	public void testGetMoney_DelegateToState() throws Exception {
		expect(state.getMoney()).andReturn(123.45d);
		control.replay();
		
		assertEquals(123.45d, port.getMoney(), 0.01d);
		
		control.verify();
	}
	
	@Test
	public void testGetPosition_DelegateToState() throws Exception {
		expect(state.getPosition()).andReturn(12);
		control.replay();
		
		assertEquals(12, port.getPosition());
		
		control.verify();
	}
	
	@Test
	public void testKill_Ok() throws Exception {
		Order order = control.createMock(Order.class);
		order.kill();
		control.replay();
		
		port.kill(order);
		
		control.verify();
	}
	
	@Test (expected=PortfolioException.class)
	public void testKill_Throws() throws Exception {
		Order order = control.createMock(Order.class);
		order.kill();
		expectLastCall().andThrow(new OrderException("test error"));
		control.replay();
		
		port.kill(order);
	}
	
	@Test
	public void testStopBuy3_Ok() throws Exception {
		Order expected = new OrderImpl(123L, Order.BUY, 10, 100.05d, 101.00d);
		expect(id.next()).andReturn(123L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.stopBuy(10, 100.05d, 101.00d);
		
		control.verify();
		assertEquals(expected, result);
	}
	
	@Test
	public void testStopBuy4_Ok() throws Exception {
		Order expected = new OrderImpl(1L, Order.BUY, 10, 10d, 11d, "test");
		expect(id.next()).andReturn(1L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.stopBuy(10, 10d, 11d, "test");
		
		control.verify();
		assertEquals(expected, result);
	}
	
	@Test
	public void testStopSell3_Ok() throws Exception {
		Order expected = new OrderImpl(5L, Order.SELL, 5, 5d, 4d);
		expect(id.next()).andReturn(5L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.stopSell(5, 5d, 4d);
		
		control.verify();
		assertEquals(expected, result);
	}

	@Test
	public void testStopSell4_Ok() throws Exception {
		Order expected = new OrderImpl(15L, Order.SELL, 5, 5d, 4d, "zulu");
		expect(id.next()).andReturn(15L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.stopSell(5, 5d, 4d, "zulu");
		
		control.verify();
		assertEquals(expected, result);
	}
	
	@Test
	public void testKillAll0_Ok() throws Exception {
		LinkedHashSet<Order> active = new LinkedHashSet<Order>();
		expect(orders.getActiveOrders()).andReturn(active);
		Order order1 = control.createMock(Order.class);
		order1.kill();
		active.add(order1);
		Order order2 = control.createMock(Order.class);
		order2.kill();
		active.add(order2);
		control.replay();
		
		port.killAll();
		
		control.verify();
	}
	
	@Test
	public void testKillAll1_Ok() throws Exception {
		LinkedHashSet<Order> active = new LinkedHashSet<Order>();
		expect(orders.getActiveOrders()).andReturn(active);
		Order order1 = control.createMock(Order.class);
		expect(order1.getType()).andReturn(Order.BUY);
		expect(order1.getStatus()).andReturn(Order.ACTIVE);
		order1.kill();
		active.add(order1);
		
		Order order2 = control.createMock(Order.class);
		expect(order2.getType()).andReturn(Order.SELL);
		active.add(order2);
		
		Order order3 = control.createMock(Order.class);
		expect(order3.getType()).andReturn(Order.BUY);
		expect(order3.getStatus()).andReturn(Order.PENDING);
		order3.activate();
		order3.kill();
		active.add(order3);
		control.replay();
		
		port.killAll(Order.BUY);
		
		control.verify();
	}
	
	@Test
	public void testLimitBuy2_Ok() throws Exception {
		Order expected = new OrderImpl(43L, Order.BUY, 5, 100.35d);
		expect(id.next()).andReturn(43L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.limitBuy(5, 100.35d);
		
		control.verify();
		assertEquals(expected, result);
	}
	
	@Test
	public void testLimitBuy3_Ok() throws Exception {
		Order expected = new OrderImpl(43L, Order.BUY, 5, 100.35d, "baka");
		expect(id.next()).andReturn(43L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.limitBuy(5, 100.35d, "baka");
		
		control.verify();
		assertEquals(expected, result);
	}
	
	@Test
	public void testLimitSell2_Ok() throws Exception {
		Order expected = new OrderImpl(44L, Order.SELL, 5, 100.35d);
		expect(id.next()).andReturn(44L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.limitSell(5, 100.35d);
		
		control.verify();
		assertEquals(expected, result);
	}

	@Test
	public void testLimitSell3_Ok() throws Exception {
		Order expected = new OrderImpl(44L, Order.SELL, 5, 100.35d, "zaka");
		expect(id.next()).andReturn(44L);
		orders.startWatch(eq(expected));
		control.replay();
		
		Order result = port.limitSell(5, 100.35d, "zaka");
		
		control.verify();
		assertEquals(expected, result);
	}

	@Test
	public void testWaitForComplete_DelegateToOrders() throws Exception {
		Order order = control.createMock(Order.class);
		orders.waitForComplete(order, 1000L);
		control.replay();
		
		port.waitForComplete(order, 1000L);
		
		control.verify();
	}
	
	@Test
	public void testWaitForNeutralPosition_DelegateToState() throws Exception {
		state.waitForNeutralPosition(800L);
		control.replay();
		
		port.waitForNeutralPosition(800L);
		
		control.verify();
	}
	
	@Test
	public void testComplex() throws Exception {
		ServiceLocator locator = new ServiceLocatorImpl();
		Props props = new PropsImpl();
		props.setString("Asset", "RIH2");
		locator.setProperties(props);
		
		AssetImpl asset = new AssetImpl("RIH2", "SPBFUT", 5, 0);
		asset.updateInitialMarginMoney(10000d);
		asset.updateEstimatedPrice(110d);
		asset.updatePriceStepMoney(3.22d);
		asset.updatePrice(112d);
		AssetsImpl assets = new AssetsImpl();
		assets.add(asset);
		locator.setAssets(assets);
		

		Double hi[] = { 150d, 155d, 155d, 140d };
		Double lo[] = { 130d, 130d, 125d, 130d };
		Double cl[] = { 145d, 135d, 130d, 135d };
		MarketDataImpl data = new MarketDataImpl(new MarketDataReaderFake(1));
		data.addValue(new TestValue<Double>(MarketData.HIGH, hi));
		data.addValue(new TestValue<Double>(MarketData.LOW, lo));
		data.addValue(new TestValue<Double>(MarketData.CLOSE, cl));
		locator.setMarketData(data);
		
		HierarchicalStreamReader reader = control.createMock(HierarchicalStreamReader.class);

		port = (TestPortfolio) new TestPortfolioBuilder().create(locator, reader);
		TestPortfolioState state =
			(TestPortfolioState)locator.getPortfolioState();
		state.getController().openPeriod(asset);
		Order order = port.stopBuy(2, 151d, 126d);
		assertNotNull(order);
		assertEquals(Order.PENDING, order.getStatus());
		assertEquals(0.0d, state.getMoney(), 0.01d);
		
		data.update();
		assertEquals(Order.PENDING, order.getStatus());
		
		data.update();
		assertEquals(Order.FILLED, order.getStatus());
		order = order.getRelatedOrder();
		assertEquals(Order.ACTIVE, order.getStatus());
		
		data.update();
		assertEquals(Order.FILLED, order.getStatus());
	}

}
