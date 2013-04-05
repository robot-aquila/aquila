package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.*;


public class PointsToPriceTest {
	TestCounter<Double> points;
	IMocksControl control;
	Asset asset;
	Portfolio port;
	ServiceLocator locator;
	PointsToPrice value;
	Observer observer;
	
	@Before
	public void setUp() throws Exception {
		points = new TestCounter<Double>();
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		asset = control.createMock(Asset.class);
		port = control.createMock(Portfolio.class);
		locator = new ServiceLocatorImpl();
		locator.setPortfolio(port);
		value = new PointsToPrice(points);
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartService_Ok() throws Exception {
		Counter<Double> points = control.createMock(Counter.class);
		value = new PointsToPrice(points);
		
		expect(port.getAsset()).andReturn(asset);
		points.addObserver(value);
		control.replay();
		
		value.startService(locator);
		
		control.verify();
	}
	
	@Test (expected=CounterServiceAlreadyStartedException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		expect(port.getAsset()).andReturn(asset);
		control.replay();
		
		value.startService(locator);
		
		control.verify();
		value.startService(locator);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStopService() throws Exception {
		Counter<Double> points = control.createMock(Counter.class);
		value = new PointsToPrice(points);
		
		expect(port.getAsset()).andReturn(asset);
		points.addObserver(value);
		points.deleteObserver(value);
		control.replay();
		
		value.startService(locator);
		value.stopService();
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		expect(port.getAsset()).andReturn(asset); // from startService
		expect(asset.priceToMoney(100.5d)).andReturn(10d);
		observer.update(value, null);
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(10d, 0.1d));
		control.replay();
		
		value.addObserver(observer);
		value.startService(locator);
		points.setValueAndNotifyObservers(100.5d);
		
		control.verify();
	}

}
