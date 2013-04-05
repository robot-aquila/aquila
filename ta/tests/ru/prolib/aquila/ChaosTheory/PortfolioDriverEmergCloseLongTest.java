package ru.prolib.aquila.ChaosTheory;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class PortfolioDriverEmergCloseLongTest {
	AssetImpl asset;
	Portfolio port;
	PortfolioDriverEmergCloseLong close;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 5, 0);
		port = createMock(Portfolio.class);
		close = new PortfolioDriverEmergCloseLong(port, asset, 1000);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(asset, close.getAsset());
		assertSame(port, close.getPortfolio());
		assertEquals(1000L, close.getTimeout());
	}
	
	@Test
	public void testTryClose_PositionLessThanZero() throws Exception {
		expect(port.getPosition()).andReturn(-10);
		replay(port);
		
		assertTrue(close.tryClose(12345, "gamma"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_PositionEqualsZero() throws Exception {
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(65432, "gamma"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_OkSuccess() throws Exception {
		expect(port.getPosition()).andReturn(10);
		port.killAll(Order.BUY);
		asset.updatePrice(100.00d);
		OrderImpl order = new OrderImpl(123L, Order.SELL, 10, 90.00d);
		expect(port.limitSell(10, 90.00d, "charlie")).andReturn(order);
		port.waitForComplete(order, 1000);
		port.waitForNeutralPosition(1000);
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(2, "charlie"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_ExceptionOnWaitOrder() throws Exception {
		expect(port.getPosition()).andReturn(10);
		port.killAll(Order.BUY);
		asset.updatePrice(200.00d);
		OrderImpl order = new OrderImpl(123L, Order.SELL, 10, 190.00d);
		expect(port.limitSell(10, 190.00d, "mu")).andReturn(order);
		port.waitForComplete(order, 1000);
		expectLastCall().andThrow(new PortfolioTimeoutException("Ya ya"));
		port.kill(order);
		expect(port.getPosition()).andReturn(5);
		replay(port);
		
		assertFalse(close.tryClose(2, "mu"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_ExceptionOnWaitNeutralPosition() throws Exception {
		expect(port.getPosition()).andReturn(1);
		port.killAll(Order.BUY);
		asset.updatePrice(250.00d);
		OrderImpl order = new OrderImpl(123L, Order.SELL, 10, 210.00d);
		expect(port.limitSell(1, 210.00d, "alpha")).andReturn(order);
		port.waitForComplete(order, 1000);
		port.waitForNeutralPosition(1000);
		expectLastCall().andThrow(new PortfolioTimeoutException("Ya ya"));
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(8, "alpha"));
		
		verify(port);
	}

}
