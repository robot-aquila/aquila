package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.stat.PositionChange;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TradeEvent;
import ru.prolib.aquila.stat.TradeReport;

public class LastTradeYieldRatioTest {
	IMocksControl control;
	TrackingTrades tracking;
	ServiceLocator locator;
	LastTradeYieldRatio value;
	Observer observer;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		tracking = control.createMock(TrackingTrades.class);
		locator = new ServiceLocatorImpl();
		locator.setTrackingTrades(tracking);
		value = new LastTradeYieldRatio();
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		tracking.addObserver(value);
		control.replay();
		
		value.startService(locator);
		
		control.verify();
	}
	
	@Test (expected=CounterServiceAlreadyStartedException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		tracking.addObserver(value);
		control.replay();
		
		value.startService(locator);

		control.verify();
		value.startService(locator);
	}
	
	@Test
	public void testStopService_OkStarted() throws Exception {
		tracking.addObserver(value); // from startService
		tracking.deleteObserver(value);
		control.replay();
		
		value.startService(locator);
		value.stopService();
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testStopService_OkNotStarted() throws Exception {
		control.replay();
		
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testUpdate_IgnoreIfNotTradeClosed() throws Exception {
		control.replay();
		
		value.addObserver(observer);
		value.update(null, this);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_OkLongProfit() throws Exception {
		TradeReport trade = new TradeReport();
		trade.addChange(new PositionChange(0, 1, 200.10d));
		trade.addChange(new PositionChange(1, 5, 205.15d));
		trade.addChange(new PositionChange(2, -6, 208.01d));
		observer.update(value, null);
		// 1248.06 / 1225.85 - 1 = 0.018118041
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(1.8118041d, 0.0001d));
		control.replay();
		
		value.addObserver(observer);
		value.update(null, TradeEvent.tradeClosed(trade));
		
		control.verify();
		assertEquals(1.8118041d, value.getValue(), 0.00001d);
	}
	
	@Test
	public void testUpdate_OkLongLoss() throws Exception {
		TradeReport trade = new TradeReport();
		trade.addChange(new PositionChange(0, 1, 205d));
		trade.addChange(new PositionChange(1, -1, 200d));
		observer.update(value, null);
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(-2.4390244d, 0.001d));
		control.replay();
		
		value.addObserver(observer);
		value.update(null, TradeEvent.tradeClosed(trade));
		
		control.verify();
		assertEquals(-2.4390244d, value.getValue(), 0.00001d);
	}
	
	@Test
	public void testUpdate_OkShortProfit() throws Exception {
		TradeReport trade = new TradeReport();
		trade.addChange(new PositionChange(0, -1, 200d));
		trade.addChange(new PositionChange(2, 1, 180d));
		observer.update(value, null);
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(11.111d, 0.001d));
		control.replay();
		
		value.addObserver(observer);
		value.update(null, TradeEvent.tradeClosed(trade));
		
		control.verify();
		assertEquals(11.111d, value.getValue(), 0.001d);
	}
	
	@Test
	public void testUpdate_OkShortLoss() throws Exception {
		TradeReport trade = new TradeReport();
		trade.addChange(new PositionChange(0, -1, 180d));
		trade.addChange(new PositionChange(2, 1, 200d));
		observer.update(value, null);
		expectLastCall()
			.andDelegateTo(new TestCounterValue<Double>(-10d, 0.100d));
		control.replay();
		
		value.addObserver(observer);
		value.update(null, TradeEvent.tradeClosed(trade));
		
		control.verify();
		assertEquals(-10d, value.getValue(), 0.0001d);
	}

}
