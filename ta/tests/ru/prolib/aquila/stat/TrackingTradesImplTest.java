package ru.prolib.aquila.stat;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.OrderImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioOrders;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.test.TestPortfolioOrders;

/**
 * 2012-02-02
 * $Id: TrackingTradesImplTest.java 196 2012-02-02 20:24:38Z whirlwind $
 */
public class TrackingTradesImplTest {
	IMocksControl control;
	PortfolioOrders orders;
	MarketData data;
	ServiceLocator locator;
	TrackingTradesImpl tracking;
	TestPortfolioOrders testOrders;
	Observer observer;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		orders = control.createMock(PortfolioOrders.class);
		data = control.createMock(MarketData.class);
		observer = control.createMock(Observer.class);
		locator = new ServiceLocatorImpl();
		locator.setPortfolioOrders(orders);
		locator.setMarketData(data);
		tracking = new TrackingTradesImpl();
		testOrders = new TestPortfolioOrders();
	}
	
	private OrderImpl createOrderB(int qty, double price)
		throws OrderException
	{
		return createOrderB(qty, price, null);
	}
	
	private OrderImpl createOrderB(int qty, double price, String comment)
		throws OrderException
	{
		OrderImpl order = new OrderImpl(1L, Order.BUY, qty, price, comment);
		order.fill();
		return order;
	}
	
	private OrderImpl createOrderS(int qty, double price) throws OrderException {
		return createOrderS(qty, price, null);
	}
	
	private OrderImpl createOrderS(int qty, double price, String comment)
		throws OrderException
	{
		OrderImpl order = new OrderImpl(1L, Order.SELL, qty, price, comment);
		order.fill();
		return order;
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		orders.addObserver(tracking);
		control.replay();
		
		tracking.startService(locator);
		
		control.verify();
	}
	
	@Test (expected=TrackingServiceAlreadyStartedException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		orders.addObserver(tracking);
		control.replay();
		
		tracking.startService(locator);
		
		control.verify();
		tracking.startService(locator);
	}
	
	@Test (expected=TrackingException.class)
	public void testStartService_ThrowsIfGetMarketDataFailed()
		throws Exception
	{
		locator = control.createMock(ServiceLocator.class);
		expect(locator.getMarketData()).andThrow(new ServiceLocatorException());
		control.replay();
		
		tracking.startService(locator);
	}
	
	@Test (expected=TrackingException.class)
	public void testStartService_ThrowsIfGetPortfolioOrdersFailed()
		throws Exception
	{
		locator = control.createMock(ServiceLocator.class);
		expect(locator.getMarketData()).andReturn(data);
		expect(locator.getPortfolioOrders())
			.andThrow(new ServiceLocatorException());
		control.replay();
		
		tracking.startService(locator);
	}
	
	@Test
	public void testStopService_OkIfNotStarted() throws Exception {
		control.replay();
		
		tracking.stopService();
		
		control.verify();
	}
	
	@Test
	public void testStopService_Ok() throws Exception {
		orders.addObserver(tracking);
		orders.deleteObserver(tracking);
		control.replay();
		
		tracking.startService(locator);
		tracking.stopService();
		
		control.verify();
	}
	
	@Test
	public void testUpdate_StartObserveLimitOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(order.isLimitOrder()).andReturn(true);
		order.addObserver(tracking);
		control.replay();
		
		tracking.update(testOrders, order);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_IgnoreNonLimitOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(order.isLimitOrder()).andReturn(false);
		control.replay();
		
		tracking.update(testOrders, order);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_KilledOrder() throws Exception {
		OrderImpl order = new OrderImpl(1L, Order.SELL, 10);
		order.kill();
		control.replay();
		
		tracking.update(order, null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_NewTrade() throws Exception {
		TradeReport expected = new TradeReport();
		expected.addChange(new PositionChange(88, 10, 800d));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex()).andReturn(88);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.newTrade(expected)));
		control.replay();
		
		tracking.startService(locator);
		tracking.addObserver(observer);
		tracking.update(createOrderB(10, 800d), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_LongClosed() throws Exception {
		TradeReport expected = new TradeReport();
		expected.addChange(new PositionChange(88, 10, 800d, "zulu4"));
		expected.addChange(new PositionChange(90, -10, 810d, "yep"));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex()).andReturn(88);
		expect(data.getLastBarIndex()).andReturn(90);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeClosed(expected)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderB(10, 800d, "zulu4"), null);
		tracking.addObserver(observer);
		tracking.update(createOrderS(10, 810, "yep"), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_LongChange() throws Exception {
		TradeReport expected = new TradeReport();
		expected.addChange(new PositionChange(88, 12, 800d, "zulu4"));
		expected.addChange(new PositionChange(95, -5, 820d));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex()).andReturn(88);
		expect(data.getLastBarIndex()).andReturn(95);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeChange(expected)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderB(12, 800d, "zulu4"), null);
		tracking.addObserver(observer);
		tracking.update(createOrderS(5, 820d), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_LongSwap() throws Exception {
		TradeReport exp1 = new TradeReport();
		exp1.addChange(new PositionChange(88, 18, 800d, "zulu4"));
		exp1.addChange(new PositionChange(95,  5, 820d));
		exp1.addChange(new PositionChange(99,-23, 810d, "baka"));
		TradeReport exp2 = new TradeReport();
		exp2.addChange(new PositionChange(99, -7, 810d, "baka"));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex())
			.andReturn(88)
			.andReturn(95)
			.andReturn(99);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeClosed(exp1)));

		expect(data.getLastBarIndex()).andReturn(99);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.newTrade(exp2)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderB(18, 800d, "zulu4"), null);
		tracking.update(createOrderB(5, 820d), null);
		tracking.addObserver(observer);
		tracking.update(createOrderS(30, 810d, "baka"), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ShortClosed() throws Exception {
		TradeReport expected = new TradeReport();
		expected.addChange(new PositionChange(88, -1, 800d));
		expected.addChange(new PositionChange(90,  1, 810d));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex()).andReturn(88);
		expect(data.getLastBarIndex()).andReturn(90);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeClosed(expected)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderS(1, 800d), null);
		tracking.addObserver(observer);
		tracking.update(createOrderB(1, 810), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ShortChange() throws Exception {
		TradeReport expected = new TradeReport();
		expected.addChange(new PositionChange(88, -10, 800d));
		expected.addChange(new PositionChange(95,  -5, 820d));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex()).andReturn(88);
		expect(data.getLastBarIndex()).andReturn(95);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeChange(expected)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderS(10, 800d), null);
		tracking.addObserver(observer);
		tracking.update(createOrderS(5, 820d), null);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ShortSwap() throws Exception {
		TradeReport exp1 = new TradeReport();
		exp1.addChange(new PositionChange(88, -11, 500d, "alpha"));
		exp1.addChange(new PositionChange(95,   5, 520d, "beta"));
		exp1.addChange(new PositionChange(99,   6, 510d, "charlie"));
		TradeReport exp2 = new TradeReport();
		exp2.addChange(new PositionChange(99,  24, 510d, "charlie"));
		
		orders.addObserver(tracking);
		expect(data.getLastBarIndex())
			.andReturn(88)
			.andReturn(95)
			.andReturn(99);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.tradeClosed(exp1)));

		expect(data.getLastBarIndex()).andReturn(99);
		observer.update(same(tracking), isA(TradeEvent.class));
		expectLastCall()
			.andDelegateTo(new TestTradeEvent(TradeEvent.newTrade(exp2)));
		control.replay();
		
		tracking.startService(locator);
		tracking.update(createOrderS(11, 500d, "alpha"), null);
		tracking.update(createOrderB(5, 520d, "beta"), null);
		tracking.addObserver(observer);
		tracking.update(createOrderB(30, 510d, "charlie"), null);
		
		control.verify();
	}
	
	/**
	 * Валидатор события трейда.
	 */
	public static class TestTradeEvent implements Observer {
		private final TradeEvent expected;
		
		public TestTradeEvent(TradeEvent expected) {
			super();
			this.expected = expected;
		}

		@Override
		public void update(Observable o, Object arg) {
			TradeEvent actual = (TradeEvent) arg;
			assertEquals(expected.getEventId(), actual.getEventId());
			assertEquals(expected.getTradeReport(), actual.getTradeReport());
		}
		
	}

}
