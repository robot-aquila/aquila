package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.utils.Variant;

/**
 * $Id$
 */
public class TradeReportTest {

	private static IMocksControl control;
	
	private static PositionType LONG = PositionType.LONG;
	private static PositionType SHORT = PositionType.SHORT;
	private static OrderDirection BUY = OrderDirection.BUY;
	private static OrderDirection SELL = OrderDirection.SELL;
	
	private static SecurityDescriptor descr1 = new SecurityDescriptor(
			"Foo", "FooClass", "Bar", SecurityType.UNK);
	private static SecurityDescriptor descr2 = new SecurityDescriptor(
			"Bar", "BarClass", "Foo", SecurityType.UNK);
	
	private Terminal terminal;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		terminal = control.createMock(Terminal.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructor() {
		TradeReport report = new TradeReport(LONG, descr1);
		assertFalse(report.isOpen());
		assertEquals(LONG, report.getType());
		assertEquals(descr1, report.getSecurity());
		assertEquals((Long) 0L, report.getQty());
		assertEquals((Double) 0.0d, report.getAverageClosePrice());
		assertEquals((Double) 0.0d, report.getAverageOpenPrice());
		assertEquals((Double) 0.0d, report.getOpenVolume());
		assertEquals((Double) 0.0d, report.getCloseVolume());
	}
	
	@Test
	public void testGetUncoveredQty() {
		TradeReport r = new TradeReport(SHORT, descr1, new Date(), new Date(), 
				100L, 54L, 100.00d, 75.00d, 100.00d, 75.00d);
		assertEquals((Long) 46L, r.getUncoveredQty());
	}
	
	@Test
	public void testIsOpen() {
		TradeReport r = new TradeReport(SHORT, descr1, new Date(), new Date(), 
				100L, 50L, 100.00d, 75.00d, 100.00d, 75.00d);
		assertTrue(r.isOpen());
		
		r = new TradeReport(SHORT, descr1, new Date(), new Date(), 
				100L, 100L, 100.00d, 75.00d, 100.00d, 75.00d);
		assertFalse(r.isOpen());
		
		r = new TradeReport(SHORT, descr1, new Date(), new Date(),
				100L, 200L, 100.00d, 150.00d, 100.00d, 75.00d);
		assertFalse(r.isOpen());
	}
	
	@Test
	public void testEquals() {
		Date openDate = new Date();
		Date closeDate = new Date();
		
		TradeReport eta = new TradeReport(SHORT, descr1, openDate, closeDate, 
				100L, 50L, 100.00d, 150.00d, 102.00d, 76.00d);
		
		Variant<PositionType> type = new Variant<PositionType>()
				.add(SHORT)
				.add(LONG);
		Variant<SecurityDescriptor> descr = new Variant<SecurityDescriptor>(type)
				.add(descr1)
				.add(descr2);
		Variant<Date> oDate = new Variant<Date>(descr)
				.add(openDate)
				.add(new Date());
		Variant<Date> cDate = new Variant<Date>(oDate)
				.add(closeDate)
				.add(new Date());
		Variant<Long> oQty = new Variant<Long>(cDate)
				.add(100L)
				.add(450L);
		Variant<Long> cQty = new Variant<Long>(oQty)
				.add(50L)
				.add(345L);
		Variant<Double> oPrice = new Variant<Double>(cQty)
				.add(100.00d)
				.add(250.00d);
		Variant<Double> cPrice = new Variant<Double>(oPrice)
				.add(150.00d)
				.add(36.00d);
		Variant<Double> oVol = new Variant<Double>(cPrice)
				.add(102.00d)
				.add(26.00d);
		Variant<Double> cVol = new Variant<Double>(oVol)
				.add(76.00d)
				.add(98.00d);
		Variant<?> iterator = cVol;
		int foundCnt = 0;
		TradeReport x = null, found = null;
		do {
			x = new TradeReport(type.get(), descr.get(), oDate.get(), cDate.get(), oQty.get(),
					cQty.get(), oPrice.get(), cPrice.get(), oVol.get(), cVol.get());
			if(eta.equals(x)) {
				foundCnt++;
				found = x;
			}
		} while(iterator.next());
		
		//100L, 50L, 100.00d, 150.00d, 102.00d, 76.00d
		assertEquals(1, foundCnt);
		assertSame(SHORT, found.getType());
		assertSame(descr1, found.getSecurity());
		assertSame(openDate, found.getOpenTime());
		assertSame(closeDate, found.getCloseTime());
		assertEquals((Long) 100L, found.getQty());
		assertEquals((Long) 50L, found.getUncoveredQty());
		assertEquals((Double) 1.00d, found.getAverageOpenPrice());
		assertEquals((Double) 3.00d, found.getAverageClosePrice());
		assertEquals((Double) 102.00d, found.getOpenVolume());
		assertEquals((Double) 76.00d, found.getCloseVolume());
	}
	
	@Test
	public void testAddTrade_ShortAddBuy() throws Exception {
		Date openDate = new Date();
		Date closeDate = new Date();
		
		TradeReport expected = new TradeReport(SHORT, descr1, openDate, null, 
				100L, 50L, 100.00d, 75.00d, 100.00d, 75.00d);
		
		TradeReport report = new TradeReport(SHORT, descr1, openDate, null, 
				100L, 0L, 100.00d, 0.00d, 100.00d, 0.00d);
		Trade trade = createTrade(descr1, BUY, closeDate, 50L, 1.50d, 75.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		
		expected = new TradeReport(SHORT, descr1, openDate, closeDate, 
				100L, 100L, 100.00d, 150.00d, 100.00d, 150.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		assertFalse(report.isOpen());
	}
	
	@Test
	public void testAddTrade_ShortAddSell() throws Exception {
		Date openDate = new Date();
		TradeReport expected = new TradeReport(SHORT, descr1, openDate, null, 
				100L, 0L, 100.00d, 0.00d, 120.00d, 0.00d);
		TradeReport report = new TradeReport(SHORT, descr1);
		
		Trade trade = createTrade(descr1, SELL, openDate, 100L, 1.00d, 120.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		
		expected = new TradeReport(SHORT, descr1, openDate, null, 
				150L, 0L, 150.00d, 0.00d, 180.00d, 0.00d);
		
		trade = createTrade(descr1, SELL, openDate, 50L, 1.00d, 60.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
	}
	
	@Test
	public void testAddTrade_LongAddBuy() throws Exception {
		Date openDate = new Date();
		
		TradeReport expected = new TradeReport(LONG, descr1, openDate, null, 
				100L, 0L, 100.00d, 0.00d, 120.00d, 0.00d);
		TradeReport report = new TradeReport(LONG, descr1);
		
		Trade trade = createTrade(descr1, BUY, openDate, 100L, 1.00d, 120.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		
		expected = new TradeReport(LONG, descr1, openDate, null, 
				150L, 0L, 150.00d, 0.00d, 180.00d, 0.00d);
		
		trade = createTrade(descr1, BUY, openDate, 50L, 1.00d, 60.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
	}
	
	@Test
	public void testAddTrade_LongAddSell() throws Exception {
		Date openDate = new Date();
		Date closeDate = new Date();
		
		TradeReport expected = new TradeReport(LONG, descr1, openDate, null, 
				100L, 50L, 100.00d, 75.00d, 100.00d, 75.00d);
		
		TradeReport report = new TradeReport(LONG, descr1, openDate, null, 
				100L, 0L, 100.00d, 0.00d, 100.00d, 0.00d);
		Trade trade = createTrade(descr1, SELL, closeDate, 50L, 1.50d, 75.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		
		expected = new TradeReport(LONG, descr1, openDate, closeDate, 
				100L, 100L, 100.00d, 150.00d, 100.00d, 150.00d);
		report.addTrade(trade);
		assertEquals(expected, report);
		assertFalse(report.isOpen());
	}
	
	@Test
	public void testAddTrade_ThrowsIfCanAppendFalse() {
		Vector<TradeReport> reports = new Vector<TradeReport>();
		reports.add( halfClosedReport(LONG) );
		reports.add( halfClosedReport(SHORT) );
		reports.add( halfClosedReport(LONG) );
		reports.add( halfClosedReport(SHORT) );
		reports.add( new TradeReport(LONG, descr1) );
		reports.add( new TradeReport(SHORT, descr1) );
		
		Vector<Trade> trades = new Vector<Trade>();
		trades.add(createTrade(SELL, 60L));
		trades.add(createTrade(BUY, 60L));
		trades.add(createTrade(SELL, 50L));
		trades.add(createTrade(BUY, 50L));
		trades.add(createTrade(BUY, 50L));
		trades.add(createTrade(SELL, 50L));
		
		Vector<Boolean> except = new Vector<Boolean>();
		except.add(true);
		except.add(true);
		except.add(false);
		except.add(false);
		except.add(false);
		except.add(false);
		
		for(int i = 0; i < reports.size(); i++) {
			try {
				reports.get(i).addTrade(trades.get(i));
				if(except.get(i)) {
					fail("Expected: "+TradeReportException.class.getSimpleName());
				}
			}catch (Exception e) {
				if(!except.get(i)) {
					fail("testAddTrade_ThrowsIfCanAppendFalse failed. Unexpected exception.");
				}
				IsInstanceOf.instanceOf(TradeReportException.class).matches(e);
			}
		}	
	}

	@Test(expected=TradeReportException.class)
	public void testAddTrade_ThrowsIfInvalidDescriptor() throws Exception {
		TradeReport report = new TradeReport(LONG, descr1);
		Trade trade = new Trade(terminal);
		trade.setSecurityDescriptor(descr2);
		report.addTrade(trade);
	}
	/**
	 * возвращает на половину закрытый репорт указанного типа
	 * @param type тип репорта
	 * @return
	 */
	private TradeReport halfClosedReport(PositionType type) {
		return new TradeReport(type, descr1, new Date(), new Date(),
				100L, 50L, 1.00d, 1.00d, 100.00d, 50.00d);
	}
	
	/**
	 * Возвращает трейд по двум параметрам - направлению и количеству
	 * @param dir направление трейда
	 * @param qty количество
	 * @return
	 */
	private Trade createTrade(OrderDirection dir, Long qty) {
		return createTrade(descr1, dir, new Date(), qty, 1.00d, 60.00d);
	}
	
	
	/**
	 * возвращает Trade
	 * 
	 * @param descr
	 * @param dir
	 * @param date
	 * @param qty
	 * @param price
	 * @param volume
	 * @return
	 */
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
