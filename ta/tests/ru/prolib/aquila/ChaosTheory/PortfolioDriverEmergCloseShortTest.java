package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class PortfolioDriverEmergCloseShortTest {
	AssetImpl asset;
	Portfolio port;
	PortfolioDriverEmergCloseShort close;
	
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
		close = new PortfolioDriverEmergCloseShort(port, asset, 1000);
	}
	
	@Test
	public void testAccessors() {
		assertSame(asset, close.getAsset());
		assertSame(port, close.getPortfolio());
		assertEquals(1000, close.getTimeout());
	}
	
	@Test
	public void testTryClose_PositionGreaterThanZero() throws Exception {
		expect(port.getPosition()).andReturn(10);
		replay(port);
		
		assertTrue(close.tryClose(12345, "foobar"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_PositionEqualsZero() throws Exception {
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(65432, "foobar"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_OkSuccess() throws Exception {
		expect(port.getPosition()).andReturn(-10);
		port.killAll(Order.SELL);
		asset.updatePrice(100.00d);
		OrderImpl order = new OrderImpl(123L, Order.BUY, 10, 120.00d);
		expect(port.limitBuy(10, 120.00d, "foobar")).andReturn(order);
		port.waitForComplete(order, 1000);
		port.waitForNeutralPosition(1000);
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(4, "foobar"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_ExceptionOnWaitOrder() throws Exception {
		expect(port.getPosition()).andReturn(-10);
		port.killAll(Order.SELL);
		asset.updatePrice(200.00d);
		OrderImpl order = new OrderImpl(123L, Order.BUY, 10, 210.00d);
		expect(port.limitBuy(10, 210.00d, "delta")).andReturn(order);
		port.waitForComplete(order, 1000);
		expectLastCall().andThrow(new PortfolioTimeoutException("Ya ya"));
		port.kill(order);
		expect(port.getPosition()).andReturn(0);
		replay(port);
		
		assertTrue(close.tryClose(2, "delta"));
		
		verify(port);
	}
	
	@Test
	public void testTryClose_ExceptionOnWaitNeutralPosition() throws Exception {
		expect(port.getPosition()).andReturn(-1);
		port.killAll(Order.SELL);
		asset.updatePrice(250.00d);
		OrderImpl order = new OrderImpl(123L, Order.BUY, 10, 300.00d);
		expect(port.limitBuy(1, 300.00d, "beta")).andReturn(order);
		port.waitForComplete(order, 1000);
		port.waitForNeutralPosition(1000);
		expectLastCall().andThrow(new PortfolioTimeoutException("Ya ya"));
		expect(port.getPosition()).andReturn(-10);
		replay(port);
		
		assertFalse(close.tryClose(10, "beta"));
		
		verify(port);
	}
}
