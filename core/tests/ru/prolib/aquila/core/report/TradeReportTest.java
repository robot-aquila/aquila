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
	
	
	@Test(expected=TradeReportException.class)
	public void testAddTrade_ThrowsIfInvalidSecurity() throws TradeReportException {
		Date date = new Date();
		Trade trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(
				new SecurityDescriptor("Bar", "BarClass", "Foo", SecurityType.UNK));
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		report.addTrade(trade);		
	}
	
	@Test(expected=TradeReportException.class)
	public void testAddTrade_LongThrowsIfQtyTooBig() throws TradeReportException {
		Date date = new Date();
		Trade trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		report.addTrade(trade);		
		
		trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.SELL);
		trade.setPrice(38.00d);
		trade.setQty(103L);
		trade.setVolume(200.00d);
		
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
		Trade trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(dir);
		trade.setPrice(35.12d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
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
		
		trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(dir);
		trade.setPrice(30.12d);
		trade.setQty(102L);
		trade.setVolume(340.00d);
		
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
		Trade trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(openDir);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		TradeReport report = new TradeReport(type, descr);
		report.addTrade(trade);		
		
		trade = new Trade(terminal);
		trade.setTime(date);
		trade.setSecurityDescriptor(descr);
		trade.setDirection(closeDir);
		trade.setPrice(38.00d);
		trade.setQty(51L);
		trade.setVolume(200.00d);
		
		report.addTrade(trade);
		
		assertNull(report.getCloseTime());
		assertEquals((Long) 51L, report.getQty());
		assertEquals((Double) 38.00d, report.getAverageClosePrice());
		assertEquals((Double) 200.00d, report.getCloseVolume());
		
		Date date2 = new Date();
		trade = new Trade(terminal);
		trade.setDirection(closeDir);
		trade.setSecurityDescriptor(descr);
		trade.setTime(date2);
		trade.setQty(51L);
		trade.setPrice(39.00d);
		trade.setVolume(150.00d);
		
		report.addTrade(trade);
		
		assertFalse(report.isOpen());
		assertEquals(date2, report.getCloseTime());
		assertEquals((Long) 0L, report.getQty());
		assertEquals((Double) 350.00d, report.getCloseVolume());
		assertEquals((Double) 38.50d, report.getAverageClosePrice());
	}

}
