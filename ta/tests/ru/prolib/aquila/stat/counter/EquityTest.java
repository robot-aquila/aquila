package ru.prolib.aquila.stat.counter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorImpl;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReaderFake;

public class EquityTest {
	IMocksControl control;
	MarketDataImpl data;
	PortfolioState state;
	ServiceLocator locator;
	Observer observer;
	Equity value;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		state = control.createMock(PortfolioState.class);
		observer = control.createMock(Observer.class);
		data = new MarketDataImpl(new MarketDataReaderFake(1));
		locator = new ServiceLocatorImpl();
		locator.setPortfolioState(state);
		locator.setMarketData(data);
		value = new Equity();
	}
	
	@Test
	public void testGetValue_NullBeforeUpdate() throws Exception {
		assertNull(value.getValue());
	}
	
	@Test
	public void testUpdate_Ok() throws Exception {
		expect(state.getTotalMoney()).andReturn(120d);
		observer.update(same(value), eq(null));
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(120d));
		
		expect(state.getTotalMoney()).andReturn(180d);
		observer.update(same(value), eq(null));
		expectLastCall().andDelegateTo(new TestCounterValue<Double>(180d));
		
		control.replay();
		
		value.startService(locator);
		value.addObserver(observer);
		
		value.update(data, null);
		assertEquals(120d, value.getValue(), 0.01d);
		
		value.update(data, null);
		assertEquals(180d, value.getValue(), 0.01d);
		
		control.verify();
		value.stopService();
		assertEquals(0, data.countObservers());
	}
	
	@Test (expected=CounterServiceAlreadyStartedException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		control.replay();
		
		value.startService(locator);
		
		control.verify();
		value.startService(locator);
	}

}
