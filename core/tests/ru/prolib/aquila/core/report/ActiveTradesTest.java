package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
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
	
	private Terminal terminal;
	private EventDispatcher dispatcher;
	private SecurityDescriptor descr;
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
		descr = new SecurityDescriptor("Foo", "FooClass", "Bar", SecurityType.UNK);
		
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
	public void testGetReport_NotFound() {
		assertNull(trades.getReport(descr));
	}
	/*
	 * Что то тут не понятно, как оттестировать, 
	 * что действительно оба собития случились
	 */
	@Test
	public void testAddTrade_ReverseType() throws Exception {
		final CountDownLatch finished = new CountDownLatch(2);
		trades.OnReportClosed().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				TradeReportEvent e = (TradeReportEvent) event;
				TradeReport report = e.getTradeReport();
				assertFalse(report.isOpen());
				assertNull(trades.getReport(descr));
				finished.countDown();
			}			
		});
		trades.OnReportOpened().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				TradeReportEvent e = (TradeReportEvent) event;
				TradeReport report = e.getTradeReport();
				assertEquals(report, trades.getReport(descr));				
				finished.countDown();				
			}
			
		});
		Trade trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(100L);
		trade.setVolume(200.00d);		
		
		trades.addTrade(trade);
		
		TradeReport report = trades.getReport(descr);
		assertEquals(PositionType.LONG, report.getType());
		
		trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.SELL);
		trade.setPrice(35.00d);
		trade.setQty(200L);
		trade.setVolume(400.00d);
		
		trades.addTrade(trade);
		
		report = trades.getReport(descr);
		assertEquals(PositionType.SHORT, report.getType());
		assertEquals((Double) 35.00d, report.getAverageOpenPrice());
		assertEquals((Long) 100L, report.getQty());
		assertEquals((Double) 200.00d, report.getOpenVolume());
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testAddTrade_ChangeExisting() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		
		trades.OnReportChanged().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				TradeReportEvent e = (TradeReportEvent) event;
				TradeReport report = e.getTradeReport();
				assertEquals(report, trades.getReport(descr));				
				finished.countDown();
			}
			
		});
		
		Trade trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		trades.addTrade(trade);
		
		trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(44L);
		trade.setVolume(150.00d);
		
		trades.addTrade(trade);
		
		TradeReport report = trades.getReport(descr);
		assertEquals((Double) 35.00d, report.getAverageOpenPrice());
		assertEquals((Long) 146L, report.getQty());
		assertEquals((Double) 500.00d, report.getOpenVolume());
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testGetReports() throws Exception {
		Trade trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		trades.addTrade(trade);
		
		trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(
				new SecurityDescriptor("Bar", "BarClass", "Foo", SecurityType.UNK));
		trade.setDirection(OrderDirection.SELL);
		trade.setPrice(85.00d);
		trade.setQty(44L);
		trade.setVolume(500.00d);
		
		trades.addTrade(trade);
		
		List<TradeReport> reports = trades.getReports();
		assertEquals(2, reports.size());
	}
	
	@Test
	public void testAddTrade_NewReport() throws Exception {
		Trade trade = new Trade(terminal);
		trade.setTime(new Date());
		trade.setSecurityDescriptor(descr);
		trade.setDirection(OrderDirection.BUY);
		trade.setPrice(35.00d);
		trade.setQty(102L);
		trade.setVolume(350.00d);
		
		final CountDownLatch finished = new CountDownLatch(1);
		
		trades.OnReportOpened().addListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				TradeReportEvent e = (TradeReportEvent) event;
				TradeReport report = e.getTradeReport();
				assertEquals(report, trades.getReport(descr));				
				finished.countDown();
			}			
		});
		trades.addTrade(trade);
		
		TradeReport report = trades.getReport(descr);
		assertEquals((Double) 35.00d, report.getAverageOpenPrice());
		assertEquals((Long) 102L, report.getQty());
		assertEquals((Double) 350.00d, report.getOpenVolume());
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testConstructor() {
		assertEquals(dispatcher, trades.getDispatcher());
		assertEquals(onOpened, trades.OnReportOpened());
		assertEquals(onClosed, trades.OnReportClosed());
		assertEquals(onChanged, trades.OnReportChanged());
	}

}
