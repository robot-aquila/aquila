package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.data.DataProviderStub;

/**
 * 2012-08-16<br>
 * $Id: TerminalImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class TerminalImplTest {
	
	static class DataProviderStubX extends DataProviderStub {
		private long nextOrderID = 1000L;

		@Override
		public long getNextOrderID() {
			return nextOrderID ++;
		}
		
	}
	
	private static Account account1 = new Account("P001");
	private static Account account2 = new Account("P002");
	private static Account account3 = new Account("P003");
	private static Symbol symbol1 = new Symbol("SBER");
	private static Symbol symbol2 = new Symbol("GAZP");
	private static Symbol symbol3 = new Symbol("LKOH");
	private IMocksControl control;
	private Scheduler schedulerMock;
	private DataProvider dataProviderMock;
	private DataProviderStubX dataProviderStub;
	private TerminalImpl terminal, terminalWithMocks;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		schedulerMock = control.createMock(Scheduler.class);
		dataProviderMock = control.createMock(DataProvider.class);
		dataProviderStub = new DataProviderStubX();
		TerminalParams params = new TerminalParams();
		params.setTerminalID("DummyTerminal");
		params.setDataProvider(dataProviderStub);
		terminal = new TerminalImpl(params);
		params = new TerminalParams();
		params.setDataProvider(dataProviderMock);
		params.setScheduler(schedulerMock);
		terminalWithMocks = new TerminalImpl(params);
	}
	
	@After
	public void tearDown() throws Exception {
		control.resetToNice();
		terminal.close();
		terminalWithMocks.close();
	}
	
	@Test
	public void testCtor() {
		assertNotNull(terminal.getScheduler());
		assertSame(dataProviderStub, terminal.getDataProvider());
		assertEquals("DummyTerminal", terminal.getTerminalID());
		String prefix = "DummyTerminal.";
		assertEquals(prefix + "ORDER_AVAILABLE", terminal.onOrderAvailable().getId());
		assertEquals(prefix + "ORDER_CANCEL_FAILED", terminal.onOrderCancelFailed().getId());
		assertEquals(prefix + "ORDER_CANCELLED", terminal.onOrderCancelled().getId());
		assertEquals(prefix + "ORDER_DEAL", terminal.onOrderDeal().getId());
		assertEquals(prefix + "ORDER_DONE", terminal.onOrderDone().getId());
		assertEquals(prefix + "ORDER_FAILED", terminal.onOrderFailed().getId());
		assertEquals(prefix + "ORDER_FILLED", terminal.onOrderFilled().getId());
		assertEquals(prefix + "ORDER_PARTIALLY_FILLED", terminal.onOrderPartiallyFilled().getId());
		assertEquals(prefix + "ORDER_REGISTERED", terminal.onOrderRegistered().getId());
		assertEquals(prefix + "ORDER_REGISTER_FAILED", terminal.onOrderRegisterFailed().getId());
		assertEquals(prefix + "ORDER_UPDATE", terminal.onOrderUpdate().getId());
		assertEquals(prefix + "PORTFOLIO_AVAILABLE", terminal.onPortfolioAvailable().getId());
		assertEquals(prefix + "PORTFOLIO_UPDATE", terminal.onPortfolioUpdate().getId());
		assertEquals(prefix + "POSITION_AVAILABLE", terminal.onPositionAvailable().getId());
		assertEquals(prefix + "POSITION_CHANGE", terminal.onPositionChange().getId());
		assertEquals(prefix + "POSITION_CURRENT_PRICE_CHANGE", terminal.onPositionCurrentPriceChange().getId());
		assertEquals(prefix + "POSITION_UPDATE", terminal.onPositionUpdate().getId());
		assertEquals(prefix + "SECURITY_AVAILABLE", terminal.onSecurityAvailable().getId());
		assertEquals(prefix + "SECURITY_SESSION_UPDATE", terminal.onSecuritySessionUpdate().getId());
		assertEquals(prefix + "SECURITY_UPDATE", terminal.onSecurityUpdate().getId());
		assertEquals(prefix + "SECURITY_MARKET_DEPTH_UPDATE", terminal.onSecurityMarketDepthUpdate().getId());
		assertEquals(prefix + "SECURITY_BEST_ASK", terminal.onSecurityBestAsk().getId());
		assertEquals(prefix + "SECURITY_BEST_BID", terminal.onSecurityBestBid().getId());
		assertEquals(prefix + "SECURITY_LAST_TRADE", terminal.onSecurityLastTrade().getId());
		assertEquals(prefix + "TERMINAL_READY", terminal.onTerminalReady().getId());
		assertEquals(prefix + "TERMINAL_UNREADY", terminal.onTerminalUnready().getId());
	}

	@Test
	public void testGetEditableSecurity() throws Exception {
		EditableSecurity actual = terminal.getEditableSecurity(symbol1);
		
		assertEquals(symbol1, actual.getSymbol());
		assertSame(actual, terminal.getEditableSecurity(symbol1));
		assertTrue(actual.onAvailable().isAlternateType(terminal.onSecurityAvailable()));
		assertTrue(actual.onSessionUpdate().isAlternateType(terminal.onSecuritySessionUpdate()));
		assertTrue(actual.onUpdate().isAlternateType(terminal.onSecurityUpdate()));
		assertTrue(actual.onBestAsk().isAlternateType(terminal.onSecurityBestAsk()));
		assertTrue(actual.onBestBid().isAlternateType(terminal.onSecurityBestBid()));
		assertTrue(actual.onLastTrade().isAlternateType(terminal.onSecurityLastTrade()));
		assertTrue(actual.onMarketDepthUpdate().isAlternateType(terminal.onSecurityMarketDepthUpdate()));
	}
	
	@Test
	public void testGetEditableSecurity_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.getEditableSecurity(symbol1);
	}

	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity_ThrowsIfNotExists() throws Exception {
		terminal.getSecurity(symbol1);
	}

	@Test
	public void testGetSecurity() throws Exception {
		Security actual = terminal.getEditableSecurity(symbol1);
		
		assertSame(actual, terminal.getSecurity(symbol1));
		assertTrue(actual.onAvailable().isAlternateType(terminal.onSecurityAvailable()));
		assertTrue(actual.onSessionUpdate().isAlternateType(terminal.onSecuritySessionUpdate()));
		assertTrue(actual.onUpdate().isAlternateType(terminal.onSecurityUpdate()));
		assertTrue(actual.onBestAsk().isAlternateType(terminal.onSecurityBestAsk()));
		assertTrue(actual.onBestBid().isAlternateType(terminal.onSecurityBestBid()));
		assertTrue(actual.onLastTrade().isAlternateType(terminal.onSecurityLastTrade()));
		assertTrue(actual.onMarketDepthUpdate().isAlternateType(terminal.onSecurityMarketDepthUpdate()));
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		Set<Security> expected = new HashSet<Security>();
		expected.add(terminal.getEditableSecurity(symbol1));
		expected.add(terminal.getEditableSecurity(symbol2));
		expected.add(terminal.getEditableSecurity(symbol3));
		
		assertEquals(expected, terminal.getSecurities());
	}
	
	@Test
	public void testGetSecurityCount() {
		assertEquals(0, terminal.getSecurityCount());
		
		terminal.getEditableSecurity(symbol1);
		
		assertEquals(1, terminal.getSecurityCount());
		
		terminal.getEditableSecurity(symbol2);
		
		assertEquals(2, terminal.getSecurityCount());
	}
	
	@Test
	public void testIsSecurityExists() {
		assertFalse(terminal.isSecurityExists(symbol1));
		
		terminal.getEditableSecurity(symbol1);
		
		assertTrue(terminal.isSecurityExists(symbol1));
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		Capture<EditablePortfolio> captured = newCapture();
		dataProviderMock.subscribeStateUpdates(capture(captured));
		control.replay();
		
		EditablePortfolio actual = terminalWithMocks.getEditablePortfolio(account1);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(account1, actual.getAccount());
		assertTrue(actual.onAvailable().isAlternateType(terminalWithMocks.onPortfolioAvailable()));
		assertTrue(actual.onPositionAvailable().isAlternateType(terminalWithMocks.onPositionAvailable()));
		assertTrue(actual.onPositionChange().isAlternateType(terminalWithMocks.onPositionChange()));
		assertTrue(actual.onPositionCurrentPriceChange()
				.isAlternateType(terminalWithMocks.onPositionCurrentPriceChange()));
		assertTrue(actual.onPositionUpdate().isAlternateType(terminalWithMocks.onPositionUpdate()));
		assertTrue(actual.onUpdate().isAlternateType(terminalWithMocks.onPortfolioUpdate()));
		assertSame(actual, captured.getValue());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEditablePortfolio_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.getEditablePortfolio(account1);
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetPortfolio_ThrowsIfNotExists() throws Exception {
		terminal.getPortfolio(account2);
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		Portfolio actual = terminal.getEditablePortfolio(account3);
		
		assertSame(actual, terminal.getPortfolio(account3));
		assertTrue(actual.onAvailable().isAlternateType(terminal.onPortfolioAvailable()));
		assertTrue(actual.onPositionAvailable().isAlternateType(terminal.onPositionAvailable()));
		assertTrue(actual.onPositionChange().isAlternateType(terminal.onPositionChange()));
		assertTrue(actual.onPositionCurrentPriceChange().isAlternateType(terminal.onPositionCurrentPriceChange()));
		assertTrue(actual.onPositionUpdate().isAlternateType(terminal.onPositionUpdate()));
		assertTrue(actual.onUpdate().isAlternateType(terminal.onPortfolioUpdate()));
	}
	
	@Test
	public void testGetPortfolios() {
		Set<Portfolio> expected = new HashSet<Portfolio>();
		expected.add(terminal.getEditablePortfolio(account1));
		expected.add(terminal.getEditablePortfolio(account2));
		expected.add(terminal.getEditablePortfolio(account3));
		
		assertEquals(expected, terminal.getPortfolios());
	}
	
	@Test
	public void testIsPortfolioExists() {
		assertFalse(terminal.isPortfolioExists(account1));
		
		terminal.getEditablePortfolio(account1);
		
		assertTrue(terminal.isPortfolioExists(account1));
	}
	
	@Test
	public void testGetPortfolioCount() {
		assertEquals(0, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(account1);
		
		assertEquals(1, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(account2);
		
		assertEquals(2, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(account3);
		
		assertEquals(3, terminal.getPortfolioCount());
	}
	
	@Test
	public void testGetEditablePortfolio_FirstCallSetsDefaultPortfolio() throws Exception {
		EditablePortfolio expected = terminal.getEditablePortfolio(account1);
		
		assertSame(expected, terminal.getDefaultPortfolio());
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetDefaultPortfolio_ThrowsIfNotDefined() throws Exception {
		terminal.getDefaultPortfolio();
	}
	
	
	@Test
	public void testSetDefaultPortfolio() throws Exception {
		terminal.getEditablePortfolio(account1);
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account2);
		
		terminal.setDefaultPortfolio(portfolio);
		
		assertSame(portfolio, terminal.getDefaultPortfolio());
	}
	
	@Test
	public void testCreateOrder() throws Exception {
		EditableOrder order = terminal.createOrder(account3, symbol3);
		
		assertNotNull(order);
		assertEquals(1000L, order.getID());
		assertEquals(account3, order.getAccount());
		assertEquals(symbol3, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertSame(order, terminal.getOrder(1000L));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateOrder_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.createOrder(account3, symbol3);
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		terminal.createOrder(account1, symbol1);
		terminal.createOrder(account1, symbol1);
		
		assertTrue(terminal.isOrderExists(1000L));
		assertTrue(terminal.isOrderExists(1001L));
		assertFalse(terminal.isOrderExists(1002L));
	}
	
	@Test
	public void testGetOrders() throws Exception {
		Set<Order> expected = new HashSet<Order>();
		expected.add(terminal.createOrder(account1, symbol1));
		expected.add(terminal.createOrder(account2, symbol2));
		expected.add(terminal.createOrder(account3, symbol3));
		
		assertEquals(expected, terminal.getOrders());
	}
	
	@Test (expected=OrderNotExistsException.class)
	public void testGetOrder_ThrowsIfNotExists() throws Exception {
		terminal.getOrder(819L);
	}
	
	@Test
	public void testGetOrder() throws Exception {
		dataProviderStub.nextOrderID = 4099L;
		EditableOrder order = terminal.createOrder(account2, symbol2);
		
		assertSame(order, terminal.getEditableOrder(4099L));
	}
	
	@Test (expected=OrderNotExistsException.class)
	public void testGetEditableOrder_ThrowsIfNotFound() throws Exception {
		terminal.getEditableOrder(82419L);
	}
	
	@Test
	public void testGetEditableOrder() throws Exception {
		dataProviderStub.nextOrderID = 2050L;
		EditableOrder order = terminal.createOrder(account2, symbol2);
		
		assertSame(order, terminal.getEditableOrder(2050L));
	}
	
	@Test (expected=OrderOwnershipException.class)
	public void testPlaceOrder_ThrowsIfOrderIsNotOwnedToTerminal()
			throws Exception
	{
		Order order = terminal.createOrder(account1, symbol1);
		
		terminalWithMocks.placeOrder(order);
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		expect(dataProviderMock.getNextOrderID()).andReturn(834L);
		control.replay();
		EditableOrder order = (EditableOrder)
				terminalWithMocks.createOrder(account3, symbol3);
		control.reset();
		dataProviderMock.registerNewOrder(same(order));
		control.replay();
		
		terminalWithMocks.placeOrder(order);
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testPlaceOrder_ThrowsIfClosed() throws Exception {
		Order order = terminal.createOrder(account1, symbol1);
		terminal.close();
		
		terminal.placeOrder(order);
	}
	
	@Test (expected=OrderOwnershipException.class)
	public void testCancelOrder_ThrowsIfOrderIsNotOwnedToTerminal()
			throws Exception
	{
		Order order = terminal.createOrder(account2, symbol2);
		
		terminalWithMocks.cancelOrder(order);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		expect(dataProviderMock.getNextOrderID()).andReturn(1028L);
		control.replay();
		EditableOrder order = (EditableOrder)
				terminalWithMocks.createOrder(account1, symbol1);
		control.reset();
		dataProviderMock.cancelOrder(same(order));
		control.replay();
		
		terminalWithMocks.cancelOrder(order);
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCancelOrder_ThrowsIfClosed() throws Exception {
		Order order = terminal.createOrder(account2, symbol2);
		terminal.close();
		
		terminal.cancelOrder(order);
	}
	
	@Test
	public void testGetOrderCount() {
		assertEquals(0, terminal.getOrderCount());
		
		terminal.createOrder(account1, symbol1);
		
		assertEquals(1, terminal.getOrderCount());
		
		terminal.createOrder(account2, symbol2);
		
		assertEquals(2, terminal.getOrderCount());
	}
	
	@Test
	public void testCreateOrder5() throws Exception {
		dataProviderStub.nextOrderID = 934L;
		
		Order order = terminal.createOrder(account1, symbol1, OrderAction.BUY, 20L, 431.15d);
		
		assertNotNull(order);
		assertEquals(934L, order.getID());
		assertEquals(account1, order.getAccount());
		assertEquals(symbol1, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertEquals(OrderAction.BUY, order.getAction());
		assertEquals(OrderType.LIMIT, order.getType());
		assertEquals(new Long(20L), order.getInitialVolume());
		assertEquals(new Long(20L), order.getCurrentVolume());
		assertEquals(431.15d, order.getPrice(), 0.001d);
		assertNull(order.getComment());
	}
	
	@Test
	public void testCreateOrder4() throws Exception {
		dataProviderStub.nextOrderID = 714L;
		
		Order order = terminal.createOrder(account3, symbol3, OrderAction.SELL, 80L);
		
		assertNotNull(order);
		assertEquals(714L, order.getID());
		assertEquals(account3, order.getAccount());
		assertEquals(symbol3, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertEquals(OrderAction.SELL, order.getAction());
		assertEquals(OrderType.MARKET, order.getType());
		assertEquals(new Long(80L), order.getInitialVolume());
		assertEquals(new Long(80L), order.getCurrentVolume());
		assertNull(order.getPrice());
		assertNull(order.getComment());
	}
	
	@Test
	public void testSubscribe() {
		Capture<EditableSecurity> captured1 = newCapture(),
				captured2 = newCapture(), captured3 = newCapture();
		dataProviderMock.subscribeStateUpdates(capture(captured1));
		dataProviderMock.subscribeLevel1Data(capture(captured2));
		dataProviderMock.subscribeLevel2Data(capture(captured3));
		control.replay();
		
		terminalWithMocks.subscribe(symbol1);
		terminalWithMocks.subscribe(symbol1); // shouldn't subscribe
		
		control.verify();
		EditableSecurity expected = terminalWithMocks.getEditableSecurity(symbol1);
		assertSame(expected, captured1.getValue());
		assertSame(expected, captured2.getValue());
		assertSame(expected, captured3.getValue());
	}
	
	@Test
	public void testGetCurrentTime() {
		Instant time = Instant.now();
		expect(schedulerMock.getCurrentTime()).andReturn(time);
		control.replay();
		
		assertEquals(time, terminalWithMocks.getCurrentTime());
		
		control.verify();
	}
	
	@Test
	public void testSchedule_RIns() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Instant time = Instant.now();
		Runnable r = control.createMock(Runnable.class);
		expect(schedulerMock.schedule(r, time)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.schedule(r, time));
		
		control.verify();
	}

	@Test
	public void testSchedule_RInsL() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Instant time = Instant.now();
		Runnable r = control.createMock(Runnable.class);
		expect(schedulerMock.schedule(r, time, 1500L)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.schedule(r, time, 1500L));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_RL() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Runnable r = control.createMock(Runnable.class);
		expect(schedulerMock.schedule(r, 329L)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.schedule(r, 329L));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_RLL() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Runnable r = control.createMock(Runnable.class);
		expect(schedulerMock.schedule(r, 1200L, 3200L)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.schedule(r, 1200L, 3200L));
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_RInsL() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Runnable r = control.createMock(Runnable.class);
		Instant time = Instant.now();
		expect(schedulerMock.scheduleAtFixedRate(r, time, 3000L)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.scheduleAtFixedRate(r, time, 3000L));
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_RLL() {
		TaskHandler h = control.createMock(TaskHandler.class);
		Runnable r = control.createMock(Runnable.class);
		expect(schedulerMock.scheduleAtFixedRate(r, 5000L, 5200L)).andReturn(h);
		control.replay();
		
		assertSame(h, terminalWithMocks.scheduleAtFixedRate(r, 5000L, 5200L));
		
		control.verify();
	}
	
	@Test
	public void testClose_CloseAllSecurities() {
		EditableSecurity security1 = terminal.getEditableSecurity(symbol1);
		EditableSecurity security2 = terminal.getEditableSecurity(symbol2);
		EditableSecurity security3 = terminal.getEditableSecurity(symbol3);
		
		terminal.close();
		
		assertTrue(security1.isClosed());
		assertTrue(security2.isClosed());
		assertTrue(security3.isClosed());
		assertEquals(0, terminal.getSecurityCount());
	}
	
	@Test
	public void testClose_CloseAllPortfolios() {
		EditablePortfolio portfolio1 = terminal.getEditablePortfolio(account1);
		EditablePortfolio portfolio2 = terminal.getEditablePortfolio(account2);
		EditablePortfolio portfolio3 = terminal.getEditablePortfolio(account3);
		
		terminal.close();
		
		assertTrue(portfolio1.isClosed());
		assertTrue(portfolio2.isClosed());
		assertTrue(portfolio3.isClosed());
		assertEquals(0, terminal.getPortfolioCount());
	}
	
	@Test
	public void testClose_CloseAllOrders() {
		EditableOrder order1 = terminal.createOrder(account1, symbol1);
		EditableOrder order2 = terminal.createOrder(account2, symbol2);
		EditableOrder order3 = terminal.createOrder(account3, symbol3);
		
		terminal.close();
		
		assertTrue(order1.isClosed());
		assertTrue(order2.isClosed());
		assertTrue(order3.isClosed());
		assertEquals(0, terminal.getOrderCount());
	}
	
	@Test
	public void testClose_SkipIfClosed() {
		schedulerMock.close();
		control.replay();
		
		terminalWithMocks.close();
		terminalWithMocks.close();
		
		control.verify();
	}
	
	@Test
	public void testClose() {
		schedulerMock.close();
		control.replay();
		
		terminalWithMocks.close();
		
		control.verify();
		assertTrue(terminalWithMocks.isClosed());
	}
	
	@Test
	public void testClose_StopIfStarted() {
		terminal.start();
		
		terminal.close();
		
		assertFalse(terminal.isStarted());
	}
	
	@Test
	public void testClose_ClearEventListenersAndAlternates() {
		EventListenerStub listener = new EventListenerStub();
		EventType type = new EventTypeImpl();
		terminal.onTerminalReady().addAlternateType(type);
		terminal.onTerminalReady().addSyncListener(listener);
		terminal.onTerminalUnready().addAlternateType(type);
		terminal.onTerminalUnready().addSyncListener(listener);
		terminal.onOrderAvailable().addAlternateType(type);
		terminal.onOrderAvailable().addListener(listener);
		terminal.onOrderCancelFailed().addAlternateType(type);
		terminal.onOrderCancelFailed().addListener(listener);
		terminal.onOrderCancelled().addAlternateType(type);
		terminal.onOrderCancelled().addListener(listener);
		terminal.onOrderDeal().addAlternateType(type);
		terminal.onOrderDeal().addListener(listener);
		terminal.onOrderDone().addAlternateType(type);
		terminal.onOrderDone().addListener(listener);
		terminal.onOrderFailed().addAlternateType(type);
		terminal.onOrderFailed().addListener(listener);
		terminal.onOrderFilled().addAlternateType(type);
		terminal.onOrderFilled().addListener(listener);
		terminal.onOrderPartiallyFilled().addAlternateType(type);
		terminal.onOrderPartiallyFilled().addListener(listener);
		terminal.onOrderRegistered().addAlternateType(type);
		terminal.onOrderRegistered().addListener(listener);
		terminal.onOrderRegisterFailed().addAlternateType(type);
		terminal.onOrderRegisterFailed().addListener(listener);
		terminal.onOrderUpdate().addAlternateType(type);
		terminal.onOrderUpdate().addListener(listener);
		terminal.onPortfolioAvailable().addAlternateType(type);
		terminal.onPortfolioAvailable().addListener(listener);
		terminal.onPortfolioUpdate().addAlternateType(type);
		terminal.onPortfolioUpdate().addListener(listener);
		terminal.onPositionAvailable().addAlternateType(type);
		terminal.onPositionAvailable().addListener(listener);
		terminal.onPositionChange().addAlternateType(type);
		terminal.onPositionChange().addListener(listener);
		terminal.onPositionCurrentPriceChange().addAlternateType(type);
		terminal.onPositionCurrentPriceChange().addListener(listener);
		terminal.onPositionUpdate().addAlternateType(type);
		terminal.onPositionUpdate().addListener(listener);
		terminal.onSecurityAvailable().addAlternateType(type);
		terminal.onSecurityAvailable().addListener(listener);
		terminal.onSecuritySessionUpdate().addAlternateType(type);
		terminal.onSecuritySessionUpdate().addListener(listener);
		terminal.onSecurityUpdate().addAlternateType(type);
		terminal.onSecurityUpdate().addListener(listener);
		terminal.onSecurityMarketDepthUpdate().addAlternateType(type);
		terminal.onSecurityMarketDepthUpdate().addListener(listener);
		terminal.onSecurityBestAsk().addAlternateType(type);
		terminal.onSecurityBestAsk().addListener(listener);
		terminal.onSecurityBestBid().addAlternateType(type);
		terminal.onSecurityBestBid().addListener(listener);
		terminal.onSecurityLastTrade().addAlternateType(type);
		terminal.onSecurityLastTrade().addListener(listener);
		
		terminal.close();
		
		assertFalse(terminal.onTerminalReady().isAlternateType(type));
		assertFalse(terminal.onTerminalReady().isListener(listener));
		assertFalse(terminal.onTerminalUnready().isAlternateType(type));
		assertFalse(terminal.onTerminalUnready().isListener(listener));
		assertFalse(terminal.onOrderAvailable().hasAlternates());
		assertFalse(terminal.onOrderAvailable().hasListeners());
		assertFalse(terminal.onOrderCancelFailed().hasAlternates());
		assertFalse(terminal.onOrderCancelFailed().hasListeners());
		assertFalse(terminal.onOrderCancelled().hasAlternates());
		assertFalse(terminal.onOrderCancelled().hasListeners());
		assertFalse(terminal.onOrderDeal().hasAlternates());
		assertFalse(terminal.onOrderDeal().hasListeners());
		assertFalse(terminal.onOrderDone().hasAlternates());
		assertFalse(terminal.onOrderDone().hasListeners());
		assertFalse(terminal.onOrderFailed().hasAlternates());
		assertFalse(terminal.onOrderFailed().hasListeners());
		assertFalse(terminal.onOrderFilled().hasAlternates());
		assertFalse(terminal.onOrderFilled().hasListeners());
		assertFalse(terminal.onOrderPartiallyFilled().hasAlternates());
		assertFalse(terminal.onOrderPartiallyFilled().hasListeners());
		assertFalse(terminal.onOrderRegistered().hasAlternates());
		assertFalse(terminal.onOrderRegistered().hasListeners());
		assertFalse(terminal.onOrderRegisterFailed().hasAlternates());
		assertFalse(terminal.onOrderRegisterFailed().hasListeners());
		assertFalse(terminal.onOrderUpdate().hasAlternates());
		assertFalse(terminal.onOrderUpdate().hasListeners());
		assertFalse(terminal.onPortfolioAvailable().hasAlternates());
		assertFalse(terminal.onPortfolioAvailable().hasListeners());
		assertFalse(terminal.onPortfolioUpdate().hasAlternates());
		assertFalse(terminal.onPortfolioUpdate().hasListeners());
		assertFalse(terminal.onPositionAvailable().hasAlternates());
		assertFalse(terminal.onPositionAvailable().hasListeners());
		assertFalse(terminal.onPositionChange().hasAlternates());
		assertFalse(terminal.onPositionChange().hasListeners());
		assertFalse(terminal.onPositionCurrentPriceChange().hasAlternates());
		assertFalse(terminal.onPositionCurrentPriceChange().hasListeners());
		assertFalse(terminal.onPositionUpdate().hasAlternates());
		assertFalse(terminal.onPositionUpdate().hasListeners());
		assertFalse(terminal.onSecurityAvailable().hasAlternates());
		assertFalse(terminal.onSecurityAvailable().hasListeners());
		assertFalse(terminal.onSecuritySessionUpdate().hasAlternates());
		assertFalse(terminal.onSecuritySessionUpdate().hasListeners());
		assertFalse(terminal.onSecurityUpdate().hasAlternates());
		assertFalse(terminal.onSecurityUpdate().hasListeners());
		assertFalse(terminal.onSecurityMarketDepthUpdate().hasAlternates());
		assertFalse(terminal.onSecurityMarketDepthUpdate().hasListeners());
		assertFalse(terminal.onSecurityBestAsk().hasAlternates());
		assertFalse(terminal.onSecurityBestAsk().hasListeners());
		assertFalse(terminal.onSecurityBestBid().hasAlternates());
		assertFalse(terminal.onSecurityBestBid().hasListeners());
		assertFalse(terminal.onSecurityLastTrade().hasAlternates());
		assertFalse(terminal.onSecurityLastTrade().hasListeners());
	}
	
	@Test
	public void testStart() throws Exception {
		dataProviderMock.subscribeRemoteObjects(terminalWithMocks);
		control.replay();
		final CountDownLatch finished = new CountDownLatch(1);
		terminalWithMocks.onTerminalReady().addSyncListener(new EventListener() {
			@Override public void onEvent(Event event) {
				assertTrue(event.isType(terminalWithMocks.onTerminalReady()));
				TerminalEvent e = (TerminalEvent) event;
				Terminal t = e.getTerminal();
				assertSame(terminalWithMocks, t);
				assertTrue(t.isStarted());
				finished.countDown();
			}
		});
		
		terminalWithMocks.start();
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStart_ThrowsIfStarted() throws Exception {
		terminal.start();
		
		terminal.start();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStartThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.start();
	}
	
	@Test
	public void testStop() throws Exception {
		dataProviderMock.subscribeRemoteObjects(terminalWithMocks);
		control.replay();
		terminalWithMocks.start();
		control.reset();
		dataProviderMock.unsubscribeRemoteObjects(terminalWithMocks);
		control.replay();
		final CountDownLatch finished = new CountDownLatch(1);
		terminalWithMocks.onTerminalUnready().addSyncListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertTrue(event.isType(terminalWithMocks.onTerminalUnready()));
				TerminalEvent e = (TerminalEvent) event;
				Terminal t = e.getTerminal();
				assertSame(terminalWithMocks, t);
				assertFalse(t.isStarted());
				finished.countDown();
			}
		});
		
		terminalWithMocks.stop();
		
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testStop_DoNothingIfStopped() {
		control.replay();
		
		terminalWithMocks.stop();
		
		control.verify();
	}

}
