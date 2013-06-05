package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class PortfolioTradesHelperTest {
	private static Date time = new Date();
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private Security security;
	private Position position;
	private Terminal terminal;
	private PortfolioTradesHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("A", "B", "C", SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		security = control.createMock(Security.class);
		position = control.createMock(Position.class);
		terminal = control.createMock(Terminal.class);
		helper = new PortfolioTradesHelper();
		
		expect(position.getSecurity()).andStubReturn(security);
		expect(position.getTerminal()).andStubReturn(terminal);
		expect(security.getDescriptor()).andStubReturn(descr);
	}
	
	@Test
	public void testCreateInitialTrade_SkipIfZero() throws Exception {
		expect(position.getCurrQty()).andReturn(0L);
		control.replay();
		
		assertNull(helper.createInitialTrade(position));
		
		control.verify();
	}
	
	@Test
	public void testCreateInitialTrade_ZeroPriceIfNoSuitablePrice()
		throws Exception
	{
		expect(position.getCurrQty()).andReturn(1L);
		expect(security.getMostAccuratePrice()).andReturn(null);
		expect(terminal.getCurrentTime()).andReturn(time);
		expect(security.getMostAccurateVolume(0.0d, 1L)).andReturn(0.0d);
		control.replay();
		
		Trade expected = new Trade(terminal);
		expected.setTime(time);
		expected.setDirection(OrderDirection.BUY);
		expected.setPrice(0.0d);
		expected.setQty(1L);
		expected.setSecurityDescriptor(descr);
		expected.setVolume(0.0d);
		
		assertEquals(expected, helper.createInitialTrade(position));
		
		control.verify();
	}
	
	@Test
	public void testCreateInitialTrade_Long() throws Exception {
		expect(position.getCurrQty()).andReturn(10L);
		expect(security.getMostAccuratePrice()).andReturn(20.0d);
		expect(terminal.getCurrentTime()).andReturn(time);
		expect(security.getMostAccurateVolume(20.0d, 10L)).andReturn(200.0d);
		control.replay();
		
		Trade expected = new Trade(terminal);
		expected.setTime(time);
		expected.setDirection(OrderDirection.BUY);
		expected.setPrice(20.0d);
		expected.setQty(10L);
		expected.setSecurityDescriptor(descr);
		expected.setVolume(200.0d);
		
		assertEquals(expected, helper.createInitialTrade(position));
		
		control.verify();
	}

	@Test
	public void testCreateInitialTrade_Short() throws Exception {
		expect(position.getCurrQty()).andReturn(-5L);
		expect(security.getMostAccuratePrice()).andReturn(10.0d);
		expect(terminal.getCurrentTime()).andReturn(time);
		expect(security.getMostAccurateVolume(10.0d, 5L)).andReturn(50.0d);
		control.replay();
		
		Trade expected = new Trade(terminal);
		expected.setTime(time);
		expected.setDirection(OrderDirection.SELL);
		expected.setPrice(10.0d);
		expected.setQty(5L);
		expected.setSecurityDescriptor(descr);
		expected.setVolume(50.0d);
		
		assertEquals(expected, helper.createInitialTrade(position));
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(helper.equals(helper));
		assertFalse(helper.equals(null));
		assertFalse(helper.equals(this));
		assertTrue(helper.equals(new PortfolioTradesHelper()));
	}

}
