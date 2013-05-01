package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * $Id$
 */
public class ActiveTradesTest {

	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	
	private static OrderDirection BUY = OrderDirection.BUY;
	private static OrderDirection SELL = OrderDirection.SELL;
	private static PositionType LONG = PositionType.LONG;
	private static PositionType SHORT = PositionType.SHORT;
	
	private Terminal terminal;
	private EventDispatcher dispatcher;
	
	private SecurityDescriptor descr1;
	private SecurityDescriptor descr2;
	private Date date1;
	private Date date2;
	
	private EventType onChanged;
	private EventType onOpened;
	private EventType onClosed;
	
	private ActiveTrades trades;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();		
		
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		dispatcher = eventSystem.createEventDispatcher();
		terminal = control.createMock(Terminal.class);
		onChanged = new EventTypeImpl(dispatcher);
		onOpened = new EventTypeImpl(dispatcher);
		onClosed = new EventTypeImpl(dispatcher);
		
		descr1 = new SecurityDescriptor("Foo", "FooClass", "Bar", SecurityType.UNK);
		descr2 = new SecurityDescriptor("Bar", "BarClass", "Foo", SecurityType.UNK);
		
		date1 = new Date();
		date2 = new Date(System.currentTimeMillis()-3600000);
		
		trades = new ActiveTrades(dispatcher, onOpened, onClosed, onChanged);
		queue.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testCloseAndOpenEvents_WhenTradeReversed() throws Exception {
		final CountDownLatch finished = new CountDownLatch(2);
		final TradeReportEvent et1 = new TradeReportEvent(
				onClosed, createCloseReportEtalon());
		final TradeReportEvent et2 = new TradeReportEvent(
				onOpened, createReversedReportEtalon());
		
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		
		trades.OnReportClosed().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				assertEquals(et1, event);
				finished.countDown();
				
			}			
		});
		trades.OnReportOpened().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				assertEquals(et2, event);
				finished.countDown();
				
			}			
		});
		trades.OnReportChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				fail("OnTradeReportChanged event fired");
				
			}			
		});
		trades.addTrade(createTrade(descr1, SELL, date2, 200L, 1.00d, 4.00d));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testChangeTradeEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final TradeReportEvent et = new TradeReportEvent(
				onChanged, createAddToExistsReportEtalon());
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		EventListener listener = (new EventListener() {

			@Override
			public void onEvent(Event event) {
				assertEquals(et, event);
				finished.countDown();
				
			}			
		});		
		trades.OnReportChanged().addListener(listener);
		trades.OnReportClosed().addListener(listener);
		trades.OnReportOpened().addListener(listener);
		
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testCloseTradeEvent_WhenTradeClosed() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final TradeReportEvent et = new TradeReportEvent(
				onClosed, createCloseReportEtalon());
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		
		EventListener listener = (new EventListener() {

			@Override
			public void onEvent(Event event) {
				assertEquals(et, event);
				finished.countDown();
			}			
		});		
		trades.OnReportClosed().addListener(listener);
		trades.OnReportChanged().addListener(listener);
		trades.OnReportOpened().addListener(listener);
		
		trades.addTrade(createTrade(descr1, SELL, date2, 100L));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOpenTradeEvent_OpenNewTrade() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		
		final TradeReportEvent et = new TradeReportEvent(
				onOpened, createOpenTradeTestEtalon(descr1));
		EventListener listener = (new EventListener() {

			@Override
			public void onEvent(Event event) {
				assertEquals(et, event);
				finished.countDown();
			}			
		});
		trades.OnReportOpened().addListener(listener);
		trades.OnReportChanged().addListener(listener);
		trades.OnReportClosed().addListener(listener);
		
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		
	}
	
	@Test
	public void testAddTrade_OpenTradesForSeveralDescriptors() throws Exception {
		TradeReport et1 = createOpenTradeTestEtalon(descr1);
		TradeReport et2 = createOpenTradeTestEtalon(descr2);
		
		Vector<Trade> tr = new Vector<Trade>();
		tr.add(createTrade(descr1, BUY, date1, 100L));
		tr.add(createTrade(descr2, BUY, date1, 100L));
		for(int i = 0; i <tr.size(); i++) {
			trades.addTrade(tr.get(i));
		}
		assertEquals(2, trades.getReports().size());
		assertEquals(et1, trades.getReport(descr1));
		assertEquals(et2, trades.getReport(descr2));
	}

	@Test
	public void testAddTrade_CreateNew() throws Exception {
		TradeReport et = createOpenTradeTestEtalon(descr1);
		
		Trade trade = createTrade(descr1, BUY, date1, 100L);
		assertNull(trades.getReport(descr1));
		trades.addTrade(trade);
		
		assertEquals(1, trades.getReports().size());
		assertEquals(et, trades.getReport(descr1));
	}
	
	@Test
	public void testAddTrade_ReportAlreadyExists() throws Exception {
		TradeReport et = createAddToExistsReportEtalon();
		
		Vector<Trade> tr = new Vector<Trade>();
		tr.add(createTrade(descr1, BUY, date1, 100L));
		tr.add(createTrade(descr1, BUY, date1, 100L));
		for(int i = 0; i <tr.size(); i++) {
			trades.addTrade(tr.get(i));
		}
		assertEquals(1, trades.getReports().size());
		assertEquals(et, trades.getReport(descr1));
	}
	
	@Test
	public void testAddTrade_CloseReport() throws Exception {
		TradeReport et = createCloseReportEtalon();
		
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		
		TradeReport report = trades.getReport(descr1);
		trades.addTrade(createTrade(descr1, SELL, date2, 100L));
		
		assertEquals(0, trades.getReports().size());		
		assertEquals(et, report);		
	}
	
	@Test
	public void testAddTrade_RevertReport() throws Exception {
		TradeReport et1 = createCloseReportEtalon();
		TradeReport et2 = createReversedReportEtalon();
		
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		
		TradeReport closedReport = trades.getReport(descr1);
		
		trades.addTrade(createTrade(descr1, SELL, date2, 200L, 1.00d, 4.00d));
		
		assertEquals(et1, closedReport);
		assertEquals(1, trades.getReports().size());
		assertEquals(et2, trades.getReport(descr1));
	}
	
	@Test
	public void testGetReport_NotFound() {
		assertNull(trades.getReport(descr1));
	}
	
	@Test
	public void testGetReport_Found() throws TradeReportException {
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		assertEquals(createOpenTradeTestEtalon(descr1),trades.getReport(descr1));
	}
	
	@Test
	public void testGetTrades() throws Exception {
		trades.addTrade(createTrade(descr1, BUY, date1, 100L));
		List<TradeReport> reps = trades.getReports();
		assertEquals(1, reps.size());
		assertEquals(createOpenTradeTestEtalon(descr1),reps.get(0));		
	}
	
	@Test
	public void testConstructor() {
		assertEquals(dispatcher, trades.getDispatcher());
		assertEquals(onOpened, trades.OnReportOpened());
		assertEquals(onClosed, trades.OnReportClosed());
		assertEquals(onChanged, trades.OnReportChanged());
	}
	
	/**
	 * Возвращает эталон репорта с установленными:
	 * type: SHORT
	 * descr: descr1
	 * openTime: date2
	 * closeTime: null
	 * openQty: 100
	 * closeQty: 0
	 * openPrice: 100.00
	 * closePrice: 0.00
	 * openVolume: 2.00
	 * closeVolume: 0.00
	 * @return
	 */
	private TradeReport createReversedReportEtalon() {
		return new TradeReport(SHORT, descr1, date2, null, 100L, 
				0L, 100.00d, 0.00d, 2.00d, 0.00d);
	}
	
	/**
	 * Возвращает эталон репорта с установленными:
	 * type: LONG
	 * descr: descr1
	 * openTime: date1
	 * closeTime: date2
	 * openQty: 100
	 * closeQty: 100
	 * openPrice: 100.00
	 * closePrice: 100.00
	 * openVolume: 2.00
	 * closeVolume: 2.00
	 * @return
	 */
	private TradeReport createCloseReportEtalon() {
		return new TradeReport(LONG, descr1, date1, date2, 100L, 
				100L, 100.00d, 100.00d, 2.00d, 2.00d);
	}
	
	/**
	 * Возвращает эталон репорта с установленными:
	 * type: LONG
	 * descr: descr1
	 * openTime: date1
	 * closeTime: null
	 * openQty: 200
	 * closeQty: 0
	 * openPrice: 200.00
	 * closePrice: 0.00
	 * openVolume: 4.00
	 * closeVolume: 0.00
	 * @return
	 */
	private TradeReport createAddToExistsReportEtalon() {
		return new TradeReport(LONG, descr1, date1, null, 200L, 
				0L, 200.00d, 0.00d, 4.00d, 0.00d);
	}

	/**
	 * Возвращает эталон репорта с установленными:
	 * type: LONG
	 * descr: descr - передается аргументом
	 * openTime: date1
	 * closeTime: null
	 * openQty: 100
	 * closeQty: 0
	 * openPrice: 100.00
	 * closePrice: 0.00
	 * openVolume: 2.00
	 * closeVolume: 0.00
	 * 
	 * @param descr SecurityDescriptor
	 * @return
	 */
	private TradeReport createOpenTradeTestEtalon(SecurityDescriptor descr) 
	{
		return new TradeReport(LONG, descr, date1, null, 100L, 
				0L, 100.00d, 0.00d, 2.00d, 0.00d);
	}
	
	/**
	 * Возвращает Trade
	 * 
	 * @param descr 
	 * @param dir
	 * @param qty
	 * @return
	 */
	private Trade createTrade(
			SecurityDescriptor descr, OrderDirection dir, Date date, Long qty) 
	{
		return createTrade(descr, dir, date, qty, 1.00d, 2.00d);
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
