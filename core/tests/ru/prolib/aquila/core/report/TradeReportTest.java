package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * $Id$
 */
public class TradeReportTest {

	private static IMocksControl control;
	
	private Terminal terminal;
	private SecurityDescriptor descr;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		terminal = control.createMock(Terminal.class);
		descr = new SecurityDescriptor("Foo", "FooClass", "Bar", SecurityType.UNK);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetUncoveredQty() throws Exception {
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		Trade trade = createTrade(
				descr, OrderDirection.BUY, new Date(), 120L, 3.00d, 350.00d);
		report.addTrade(trade);
		
		trade = createTrade(
				descr, OrderDirection.SELL, new Date(), 70L, 3.00d, 350.00d);
		report.addTrade(trade);
		assertEquals((Long) 50L, report.getUncoveredQty());
	}
	
	
	@Test(expected=TradeReportException.class)
	public void testAddTrade_ThrowsIfInvalidSecurity() throws TradeReportException {
		Date date = new Date();
		Trade trade = createTrade(
				new SecurityDescriptor("Bar", "BarClass", "Foo", SecurityType.UNK),
				OrderDirection.BUY, date, 102L, 35.00d, 350.00d);
		
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		report.addTrade(trade);		
	}
	
	@Test(expected=TradeReportException.class)
	public void testAddTrade_LongThrowsIfQtyTooBig() throws TradeReportException {
		Date date = new Date();
		Trade trade = createTrade(descr, OrderDirection.BUY, date, 102L, 35.00d, 350.00d);
		
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		report.addTrade(trade);		
		
		trade = createTrade(descr, OrderDirection.SELL, date, 103L, 38.00d, 200.00d);
		
		report.addTrade(trade);
	}
	
	@Test
	public void testAddTrade_CloseShortTrade() throws TradeReportException {
		testCloseTrade(PositionType.SHORT);
	}
	
	@Test
	public void testAddTrade_CloseLongTrade() throws TradeReportException {
		testCloseTrade(PositionType.LONG);
	}
	
	@Test
	public void testAddTrade_OpenLongTrade() throws TradeReportException {
		testOpenTrade(PositionType.LONG);
	}
	
	@Test
	public void testAddTrade_OpenShortTrade() throws TradeReportException {
		testOpenTrade(PositionType.SHORT);
	}

	@Test
	public void testConstructor() {
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		assertFalse(report.isOpen());
		assertEquals(PositionType.LONG, report.getType());
		assertEquals(descr, report.getSecurity());
		assertEquals((Long) 0L, report.getQty());
		assertEquals((Double) 0.0d, report.getAverageClosePrice());
		assertEquals((Double) 0.0d, report.getAverageOpenPrice());
		assertEquals((Double) 0.0d, report.getOpenVolume());
		assertEquals((Double) 0.0d, report.getCloseVolume());
	}
	
	private void testOpenTrade(PositionType type) throws TradeReportException {
		OrderDirection dir = (type == PositionType.LONG) ?
				OrderDirection.BUY : OrderDirection.SELL;
		
		Date date = new Date();
		Trade trade = createTrade(descr, dir, date, 102L, 35.12d, 350.00d);
		
		TradeReport report = new TradeReport(type, descr);		
		report.addTrade(trade);
		
		assertEquals(date, report.getOpenTime());
		assertNull(report.getCloseTime());
		assertEquals((Long) 102L, report.getQty());
		assertEquals((Double) 35.12d, report.getAverageOpenPrice());
		assertEquals((Double) 0.00d, report.getAverageClosePrice());
		assertEquals((Double) 350.00d, report.getOpenVolume());
		assertEquals((Double) 0.00d, report.getCloseVolume());
		assertTrue(report.isOpen());
		
		trade = createTrade(descr, dir, new Date(), 102L, 30.12d, 340.00d);
		
		report.addTrade(trade);
		
		assertEquals(date, report.getOpenTime());
		assertNull(report.getCloseTime());
		assertEquals((Long) 204L, report.getQty());
		assertEquals((Double) 32.62d, report.getAverageOpenPrice());
		assertEquals((Double) 0.00d, report.getAverageClosePrice());
		assertEquals((Double) 690.00d, report.getOpenVolume());
		assertEquals((Double) 0.00d, report.getCloseVolume());
		assertTrue(report.isOpen());
	}
	
	private void testCloseTrade(PositionType type) throws TradeReportException {
		
		OrderDirection openDir = (type == PositionType.LONG) ?
				OrderDirection.BUY : OrderDirection.SELL;
		OrderDirection closeDir = (type == PositionType.LONG) ?
				OrderDirection.SELL : OrderDirection.BUY;
		
		Date date = new Date();
		
		Trade trade = createTrade(descr, openDir, date, 102L, 35.00d, 130.00d);
		
		TradeReport report = new TradeReport(type, descr);
		report.addTrade(trade);		
		
		trade = createTrade(descr, closeDir, date, 51L, 38.00d, 200.00d);
		report.addTrade(trade);
		
		assertNull(report.getCloseTime());
		assertEquals((Long) 102L, report.getQty());
		assertEquals((Double) 38.00d, report.getAverageClosePrice());
		assertEquals((Double) 200.00d, report.getCloseVolume());
		
		Date date2 = new Date();
		trade = createTrade(descr, closeDir, date2, 51L, 39.00d, 150.00d);
		report.addTrade(trade);
		
		assertFalse(report.isOpen());
		assertEquals(date2, report.getCloseTime());
		assertEquals((Long) 102L, report.getQty());
		assertEquals((Double) 350.00d, report.getCloseVolume());
		assertEquals((Double) 38.50d, report.getAverageClosePrice());
	}
	
	private Trade createTrade(SecurityDescriptor descr, OrderDirection dir,Date date, 
			Long qty, Double price, Double volume)
	{
		Trade trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(dir);
		trade.setPrice(price);
		trade.setQty(qty);
		trade.setVolume(volume);
		return trade;
	}

}
