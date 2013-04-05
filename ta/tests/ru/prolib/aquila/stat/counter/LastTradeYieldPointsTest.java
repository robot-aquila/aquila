package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;
import org.apache.log4j.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.stat.*;

/**
 * 2012-02-05
 * $Id: LastTradeYieldPointsTest.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class LastTradeYieldPointsTest {
	IMocksControl control;
	Observer observer;
	TrackingTradesImpl tracking;
	TrackingTrades mockTracking;
	TradeReport trade;
	LastTradeYieldPoints value;
	ServiceLocator locator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		mockTracking = control.createMock(TrackingTrades.class);
		tracking = new TrackingTradesImpl();
		trade = new TradeReport();
		locator = new ServiceLocatorImpl();
		locator.setTrackingTrades(mockTracking);
		value = new LastTradeYieldPoints();
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		mockTracking.addObserver(value);
		control.replay();
		
		value.startService(locator);
		
		control.verify();
	}
	
	@Test (expected=CounterServiceAlreadyStartedException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		mockTracking.addObserver(value);
		control.replay();
		
		value.startService(locator);
		
		control.verify();
		value.startService(locator);
	}
	
	@Test (expected=CounterException.class)
	public void testStartService_ThrowsIfLocatorError() throws Exception {
		locator = control.createMock(ServiceLocator.class);
		expect(locator.getTrackingTrades())
			.andThrow(new ServiceLocatorException("foobar"));
		control.replay();
		
		value.startService(locator);
	}

	@Test
	public void testStopService_OkIfStarted() throws Exception {
		mockTracking.addObserver(value);
		mockTracking.deleteObserver(value);
		control.replay();
		
		value.startService(locator);
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testStopService_OkIfNotStarted() throws Exception {
		mockTracking.addObserver(value);
		mockTracking.deleteObserver(value);
		control.replay();
		
		value.startService(locator);
		value.stopService();
		value.stopService();
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testUpdate_IgnoreIfNotTrackingEvent() throws Exception {
		value.addObserver(observer);
		control.replay();
		
		value.update(tracking, this);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_IgnoreIfNotTrackingClosedEvent() throws Exception {
		TradeEvent e = TradeEvent.newTrade(trade);
		value.addObserver(observer);
		control.replay();
		
		value.update(tracking, e);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_OkLongProfit() throws Exception {
		trade.addChange(new PositionChange(1, 10, 125.13d));
		trade.addChange(new PositionChange(2, -5, 130.88d));
		trade.addChange(new PositionChange(3, -5, 135.12d));
		TradeEvent e = TradeEvent.tradeClosed(trade);
		observer.update(same(value), eq(null));
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(78.7d, 0.01d));
		control.replay();
		
		value.addObserver(observer);
		value.update(tracking, e);
		
		control.verify();
		assertEquals(78.7d, value.getValue(), 0.001d);
	}
	
	@Test
	public void testUpdate_OkLongLoss() throws Exception {
		trade.addChange(new PositionChange(1,  5, 140d));
		trade.addChange(new PositionChange(2,  5, 135d));
		trade.addChange(new PositionChange(3, -5, 125d));
		trade.addChange(new PositionChange(4, -5, 120d));
		TradeEvent e = TradeEvent.tradeClosed(trade);
		observer.update(same(value), eq(null));
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(-150d, 0.01d));
		control.replay();
		
		value.addObserver(observer);
		value.update(tracking, e);
		
		control.verify();
		assertEquals(-150d, value.getValue(), 0.001d);
	}
	
	@Test
	public void testUpdate_ShortProfit() throws Exception {
		trade.addChange(new PositionChange(1, -2, 121.34d));
		trade.addChange(new PositionChange(2, -1, 121.15d));
		trade.addChange(new PositionChange(3,  3, 119.84d));
		TradeEvent e = TradeEvent.tradeClosed(trade);
		observer.update(same(value), eq(null));
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(4.31d, 0.01d));
		control.replay();
		
		value.addObserver(observer);
		value.update(tracking, e);
		
		control.verify();
		assertEquals(4.31d, value.getValue(), 0.01d);
	}
	
	@Test
	public void testUpdate_ShortLoss() throws Exception {
		trade.addChange(new PositionChange(100, -1, 253.19d));
		trade.addChange(new PositionChange(115,  1, 256.22d));
		TradeEvent e = TradeEvent.tradeClosed(trade);
		observer.update(same(value), eq(null));
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(-3.03d, 0.01d));
		control.replay();
		
		value.addObserver(observer);
		value.update(tracking, e);
		
		control.verify();
		assertEquals(-3.03d, value.getValue(), 0.01d);
	}
	

}
