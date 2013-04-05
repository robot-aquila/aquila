package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.*;
import ru.prolib.aquila.stat.*;
import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ds.*;

public class LastTradeCloseTest {
	IMocksControl control;
	ServiceLocator locator;
	TrackingTrades tracking;
	MarketDataImpl data;
	Observer observer;
	LastTradeClose value;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = new ServiceLocatorImpl();
		tracking = control.createMock(TrackingTrades.class);
		observer = control.createMock(Observer.class);
		data = new MarketDataImpl(new MarketDataReaderFake(1));
		locator.setTrackingTrades(tracking);
		locator.setMarketData(data);
		value = new LastTradeClose();
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
	public void testStopService_OkNotStarted() throws Exception {
		control.replay();
		
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testStopService_OkStarted() throws Exception {
		tracking.addObserver(value);
		tracking.deleteObserver(value);
		control.replay();
		
		value.startService(locator);
		value.stopService();
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testUpdate_IgnoreIfNotTradeClosed() throws Exception {
		control.replay();
		
		value.update(data, this);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		Date last = new Date();
		TestValue<Date> time = new TestValue<Date>(MarketData.TIME, last);
		data.addValue(time);
		TradeReport trade = new TradeReport();
		trade.addChange(new PositionChange(-1, 1, 15d));
		trade.addChange(new PositionChange(0, 1, 15d));
		
		tracking.addObserver(value); // from startService
		observer.update(value, null);
		expectLastCall().andDelegateTo(new TestCounterValue<Date>(last));
		control.replay();
		
		value.addObserver(observer);
		value.startService(locator);
		value.update(null, TradeEvent.tradeClosed(trade));
		
		control.verify();
	}
	
}
