package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.TestEventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParamsBuilder;
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
	private ObjectFactory objectFactoryMock;
	private TerminalImpl terminal, terminalWithMocks;
	private EventListenerStub listenerStub;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	protected static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		schedulerMock = control.createMock(Scheduler.class);
		dataProviderMock = control.createMock(DataProvider.class);
		dataProviderStub = new DataProviderStubX();
		objectFactoryMock = control.createMock(ObjectFactory.class);
		TerminalParams params = new TerminalParams();
		params.setTerminalID("DummyTerminal");
		params.setDataProvider(dataProviderStub);
		params.setEventQueue(new TestEventQueueImpl());
		terminal = new TerminalImpl(params);
		params = new TerminalParams();
		params.setDataProvider(dataProviderMock);
		params.setScheduler(schedulerMock);
		params.setObjectFactory(objectFactoryMock);
		terminalWithMocks = new TerminalImpl(params);
		listenerStub = new EventListenerStub();
		
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		terminal.getEditablePortfolio(account1);
		terminal.getEditablePortfolio(account2);
		terminal.getEditablePortfolio(account3);
	}
	
	@After
	public void tearDown() throws Exception {
		control.resetToNice();
		terminal.close();
		terminalWithMocks.close();
	}
	
	private void assertOrderAlternateEventTypes(Order order) {
		assertTrue(order.onAvailable().isAlternateType(terminal.onOrderAvailable()));
		assertTrue(order.onCancelFailed().isAlternateType(terminal.onOrderCancelFailed()));
		assertTrue(order.onCancelled().isAlternateType(terminal.onOrderCancelled()));
		assertTrue(order.onDone().isAlternateType(terminal.onOrderDone()));
		assertTrue(order.onExecution().isAlternateType(terminal.onOrderExecution()));
		assertTrue(order.onFailed().isAlternateType(terminal.onOrderFailed()));
		assertTrue(order.onFilled().isAlternateType(terminal.onOrderFilled()));
		assertTrue(order.onPartiallyFilled().isAlternateType(terminal.onOrderPartiallyFilled()));
		assertTrue(order.onRegistered().isAlternateType(terminal.onOrderRegistered()));
		assertTrue(order.onRegisterFailed().isAlternateType(terminal.onOrderRegisterFailed()));
		assertTrue(order.onUpdate().isAlternateType(terminal.onOrderUpdate()));
		assertTrue(order.onArchived().isAlternateType(terminal.onOrderArchived()));
		assertTrue(order.onClose().isAlternateType(terminal.onOrderClose()));
	}
	
	private EditableOrder createTestOrder() {
		List<Account> accounts = new ArrayList<>();
		accounts.add(account1);
		accounts.add(account2);
		accounts.add(account3);
		List<Symbol> symbols = new ArrayList<>();
		symbols.add(symbol1);
		symbols.add(symbol2);
		symbols.add(symbol3);
		List<OrderType> types = new ArrayList<>();
		types.add(OrderType.LMT);
		types.add(OrderType.MKT);
		List<OrderAction> actions = new ArrayList<>();
		actions.add(OrderAction.BUY);
		actions.add(OrderAction.SELL);
		
		Order order = terminal.createOrder(
				accounts.get((int)(Math.random() * accounts.size())),
				symbols.get((int)(Math.random() * symbols.size())),
				types.get((int)(Math.random() * types.size())),
				actions.get((int)(Math.random() * actions.size())),
				CDecimalBD.of((long)(Math.random() * 9000)),
				CDecimalBD.of((long)(Math.random() * 9000.0)),
				null);
		return (EditableOrder) order;
	}
	
	private void assertOrderEvent(Order expectedOrder, EventType expectedType, Event actual) {
		OrderEvent e = (OrderEvent) actual;
		assertSame(expectedOrder, e.getOrder());
		assertTrue(actual.isType(expectedType));
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
		assertEquals(prefix + "ORDER_DEAL", terminal.onOrderExecution().getId());
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
		assertEquals(prefix + "ORDER_ARCHIVED", terminal.onOrderArchived().getId());
		assertEquals(prefix + "ORDER_CLOSE", terminal.onOrderClose().getId());
		assertEquals(prefix + "SECURITY_CLOSE", terminal.onSecurityClose().getId());
		assertEquals(prefix + "POSITION_CLOSE", terminal.onPositionClose().getId());
		assertEquals(prefix + "PORTFOLIO_CLOSE", terminal.onPortfolioClose().getId());
		assertNotNull(terminal.getLID());
		//assertTrue(LID.isLastCreatedLID(terminalWithMocks.getLID()));
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
		assertTrue(actual.onClose().isAlternateType(terminal.onSecurityClose()));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEditableSecurity_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.getEditableSecurity(symbol1);
	}

	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity_ThrowsIfNotExists() throws Exception {
		terminal.getSecurity(new Symbol("ZAAREE"));
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
		assertTrue(actual.onClose().isAlternateType(terminal.onSecurityClose()));
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
		assertEquals(3, terminal.getSecurityCount());
		
		terminal.getEditableSecurity(new Symbol("ZAMBI-1"));
		
		assertEquals(4, terminal.getSecurityCount());
		
		terminal.getEditableSecurity(new Symbol("ZAMBI-2"));
		
		assertEquals(5, terminal.getSecurityCount());
	}
	
	@Test
	public void testIsSecurityExists() {
		Symbol symbol = new Symbol("foo-bar");
		assertFalse(terminal.isSecurityExists(symbol));
		
		terminal.getEditableSecurity(symbol);
		
		assertTrue(terminal.isSecurityExists(symbol));
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		EditablePortfolio portfolioStub =
			new PortfolioImpl(new PortfolioParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withAccount(account1)
				.buildParams());
		expect(objectFactoryMock.createPortfolio(terminalWithMocks, account1)).andReturn(portfolioStub);
		dataProviderMock.subscribeStateUpdates(portfolioStub);
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
		assertTrue(actual.onClose().isAlternateType(terminalWithMocks.onPortfolioClose()));
		assertTrue(actual.onPositionClose().isAlternateType(terminalWithMocks.onPositionClose()));
		assertSame(portfolioStub, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEditablePortfolio_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.getEditablePortfolio(account1);
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetPortfolio_ThrowsIfNotExists() throws Exception {
		terminal.getPortfolio(new Account("ZYXEL-412"));
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
		assertTrue(actual.onClose().isAlternateType(terminal.onPortfolioClose()));
		assertTrue(actual.onPositionClose().isAlternateType(terminal.onPositionClose()));
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
		Account account = new Account("xxx");
		assertFalse(terminal.isPortfolioExists(account));
		
		terminal.getEditablePortfolio(account);
		
		assertTrue(terminal.isPortfolioExists(account));
	}
	
	@Test
	public void testGetPortfolioCount() {
		assertEquals(3, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(new Account("ZEBRA-1"));
		
		assertEquals(4, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(new Account("ZEBRA-2"));
		
		assertEquals(5, terminal.getPortfolioCount());
		
		terminal.getEditablePortfolio(new Account("ZEBRA-3"));
		
		assertEquals(6, terminal.getPortfolioCount());
	}
	
	@Test
	public void testGetEditablePortfolio_FirstCallSetsDefaultPortfolio() throws Exception {
		EditablePortfolio expected = terminal.getEditablePortfolio(account1);
		
		assertSame(expected, terminal.getDefaultPortfolio());
	}
	
	@Test (expected=PortfolioNotExistsException.class)
	public void testGetDefaultPortfolio_ThrowsIfNotDefined() throws Exception {
		terminal.setDefaultPortfolio(null);
		
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
	public void testCreateOrder3() throws Exception {
		EditableOrder order = terminal.createOrder(834L, account1, symbol1);
		
		assertNotNull(order);
		assertSame(order, terminal.getOrder(834L));
		assertEquals(account1, order.getAccount());
		assertEquals(symbol1, order.getSymbol());
		assertEquals(834L, order.getID());
		assertNull(order.getStatus());
		assertOrderAlternateEventTypes(order);
	}
	
	@Test
	public void testCreateOrder3_LocksPortfolioOnCreate() throws Exception {
		CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2);
		final Portfolio portfolio = terminal.getPortfolio(account1);
		AtomicInteger lastID = new AtomicInteger(0);
		Thread t1 = new Thread() {
			@Override
			public void run() {
				portfolio.lock();
				try {
					started.countDown();
					Thread.sleep(100L);
					lastID.set(1);
					successPoints.countDown();
				} catch ( InterruptedException e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlock();
				}
			}
		};
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1, TimeUnit.SECONDS) ) {
						terminal.createOrder(834L, account1, symbol1);
						lastID.set(2);
						successPoints.countDown();
					}
				} catch ( InterruptedException e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		t2.start();
		t1.start();
		assertTrue(successPoints.await(1, TimeUnit.SECONDS));
		assertEquals(2, lastID.get());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateOrder3_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.createOrder(800L, account1, symbol1);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCreateOrder3_ThrowsIfIDExists() throws Exception {
		terminal.createOrder(115L, account1, symbol2);
		
		terminal.createOrder(115L, account3, symbol2);
	}
	
	@Test
	public void testCreateOrder2() throws Exception {
		EditableOrder order = terminal.createOrder(account3, symbol3);
		
		assertNotNull(order);
		assertEquals(1000L, order.getID());
		assertEquals(account3, order.getAccount());
		assertEquals(symbol3, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertSame(order, terminal.getOrder(1000L));
		assertOrderAlternateEventTypes(order);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateOrder2_ThrowsIfClosed() throws Exception {
		terminal.close();
		
		terminal.createOrder(account3, symbol3);
	}
	
	@Test
	public void testCreateOrder2_LocksPortfolioOnCreate() throws Exception {
		CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2);
		final Portfolio portfolio = terminal.getPortfolio(account3);
		AtomicInteger lastID = new AtomicInteger(0);
		Thread t1 = new Thread() {
			@Override
			public void run() {
				portfolio.lock();
				try {
					started.countDown();
					Thread.sleep(100L);
					lastID.set(1);
					successPoints.countDown();
				} catch ( InterruptedException e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlock();
				}
			}
		};
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1, TimeUnit.SECONDS) ) {
						terminal.createOrder(account3, symbol3);
						lastID.set(2);
						successPoints.countDown();
					}
				} catch ( InterruptedException e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		t2.start();
		t1.start();
		assertTrue(successPoints.await(1, TimeUnit.SECONDS));
		assertEquals(2, lastID.get());
	}
	
	@Test
	public void testCreateOrder2_NoEventsProduced() throws Exception {
		terminal.onOrderAvailable().addListener(listenerStub);
		terminal.onOrderUpdate().addListener(listenerStub);
		
		terminal.createOrder(account3, symbol3);
		
		assertEquals(0, listenerStub.getEventCount());
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
		EditablePortfolio portfolioStub =
			new PortfolioImpl(new PortfolioParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withAccount(account3)
				.withObjectFactory(new ObjectFactoryImpl())
				.withController(new OSCControllerStub())
				.buildParams());
		EditableOrder orderStub =
			new OrderImpl(new OrderParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withAccount(account3)
				.withSymbol(symbol3)
				.withSecurity(terminal.getEditableSecurity(symbol3))
				.withPortfolio(terminal.getEditablePortfolio(account3))
				.withOrderID(834L)
				.buildParams());
		expect(objectFactoryMock.createPortfolio(terminalWithMocks, account3)).andReturn(portfolioStub);
		dataProviderMock.subscribeStateUpdates(portfolioStub);
		expect(dataProviderMock.getNextOrderID()).andReturn(834L);
		expect(objectFactoryMock.createOrder(terminalWithMocks, account3, symbol3, 834L)).andReturn(orderStub);
		expect(schedulerMock.getCurrentTime()).andStubReturn(T("2017-08-04T21:30:00Z"));
		control.replay();
		terminalWithMocks.getEditablePortfolio(account3);
		terminalWithMocks.createOrder(account3, symbol3);
		control.reset();
		dataProviderMock.registerNewOrder(same(orderStub));
		control.replay();
		
		terminalWithMocks.placeOrder(orderStub);
		
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
		EditablePortfolio portfolioStub =
			new PortfolioImpl(new PortfolioParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withAccount(account1)
				.withObjectFactory(new ObjectFactoryImpl())
				.withController(new OSCControllerStub())
				.buildParams());
		EditableOrder orderStub = new OrderImpl(new OrderParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withAccount(account1)
				.withSymbol(symbol1)
				.withSecurity(terminal.getEditableSecurity(symbol1))
				.withPortfolio(terminal.getEditablePortfolio(account1))
				.withOrderID(1028L)
				.buildParams());
		expect(objectFactoryMock.createPortfolio(terminalWithMocks, account1)).andReturn(portfolioStub);
		dataProviderMock.subscribeStateUpdates(portfolioStub);
		expect(dataProviderMock.getNextOrderID()).andReturn(1028L);
		expect(objectFactoryMock.createOrder(terminalWithMocks, account1, symbol1, 1028L)).andReturn(orderStub);
		expect(schedulerMock.getCurrentTime()).andStubReturn(T("2017-08-04T21:40:00Z"));
		control.replay();
		terminalWithMocks.getEditablePortfolio(account1);
		terminalWithMocks.createOrder(account1, symbol1);
		control.reset();
		dataProviderMock.cancelOrder(same(orderStub));
		control.replay();
		
		terminalWithMocks.cancelOrder(orderStub);
		
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
		
		Order order = terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(20L),
				CDecimalBD.of("431.15"));

		assertNotNull(order);
		assertTrue(Math.abs(ChronoUnit.MILLIS.between(order.getTime(), terminal.getCurrentTime())) < 100);
		assertEquals(934L, order.getID());
		assertEquals(terminal, order.getTerminal());
		assertEquals(account1, order.getAccount());
		assertEquals(symbol1, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertEquals(OrderAction.BUY, order.getAction());
		assertEquals(OrderType.LMT, order.getType());
		assertEquals(CDecimalBD.of(20L), order.getInitialVolume());
		assertEquals(CDecimalBD.of(20L), order.getCurrentVolume());
		assertEquals(CDecimalBD.of("431.15"), order.getPrice());
		assertNull(order.getComment());
		assertNull(order.getExecutedValue());
		assertOrderAlternateEventTypes(order);
	}
	
	@Test
	public void testCreateOrder5_NoEventsProduced() throws Exception {
		dataProviderStub.nextOrderID = 934L;
		terminal.onOrderAvailable().addListener(listenerStub);
		terminal.onOrderUpdate().addListener(listenerStub);
		
		terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(20L),
				CDecimalBD.of("431.15"));
		
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test
	public void testCreateOrder4() throws Exception {
		dataProviderStub.nextOrderID = 714L;
		
		Order order = terminal.createOrder(account3,
				symbol3,
				OrderAction.SELL,
				CDecimalBD.of(80L));
		
		assertNotNull(order);
		assertTrue(Math.abs(ChronoUnit.MILLIS.between(order.getTime(), terminal.getCurrentTime())) < 100);
		assertEquals(714L, order.getID());
		assertEquals(terminal, order.getTerminal());
		assertEquals(account3, order.getAccount());
		assertEquals(symbol3, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertEquals(OrderAction.SELL, order.getAction());
		assertEquals(OrderType.MKT, order.getType());
		assertEquals(CDecimalBD.of(80L), order.getInitialVolume());
		assertEquals(CDecimalBD.of(80L), order.getCurrentVolume());
		assertNull(order.getPrice());
		assertNull(order.getComment());
		assertNull(order.getExecutedValue());
		assertOrderAlternateEventTypes(order);
	}
	
	@Test
	public void testCreateOrder4_NoEventsProduced() throws Exception {
		dataProviderStub.nextOrderID = 714L;
		terminal.onOrderAvailable().addListener(listenerStub);
		terminal.onOrderUpdate().addListener(listenerStub);
		
		terminal.createOrder(account3,
				symbol3,
				OrderAction.SELL,
				CDecimalBD.of(80L));
		
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test
	public void testCreateOrder7() throws Exception {
		dataProviderStub.nextOrderID = 924L;
		
		Order order = terminal.createOrder(account3,
				symbol2,
				OrderType.MKT,
				OrderAction.SELL,
				CDecimalBD.of(400L),
				CDecimalBD.of("224.13"),
				"test order");
		
		assertNotNull(order);
		assertTrue(Math.abs(ChronoUnit.MILLIS.between(order.getTime(), terminal.getCurrentTime())) < 100);
		assertEquals(924L, order.getID());
		assertEquals(terminal, order.getTerminal());
		assertEquals(account3, order.getAccount());
		assertEquals(symbol2, order.getSymbol());
		assertEquals(OrderStatus.PENDING, order.getStatus());
		assertEquals(OrderAction.SELL, order.getAction());
		assertEquals(OrderType.MKT, order.getType());
		assertEquals(CDecimalBD.of(400L), order.getInitialVolume());
		assertEquals(CDecimalBD.of(400L), order.getCurrentVolume());
		assertEquals(CDecimalBD.of("224.13"), order.getPrice());
		assertEquals("test order", order.getComment());
		assertNull(order.getExecutedValue());
		assertOrderAlternateEventTypes(order);
	}
	
	@Test
	public void testCreateOrder7_NoEventsProduced() throws Exception {
		dataProviderStub.nextOrderID = 555L;
		terminal.onOrderAvailable().addListener(listenerStub);
		terminal.onOrderUpdate().addListener(listenerStub);
		
		terminal.createOrder(account3,
				symbol2,
				OrderType.MKT,
				OrderAction.SELL,
				CDecimalBD.of(400L),
				CDecimalBD.of("224.13"),
				"test order");

		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test
	public void testSubscribe() {
		EditableSecurity securityStub =
			new SecurityImpl(new SecurityParamsBuilder(terminalWithMocks.getEventQueue())
				.withTerminal(terminalWithMocks)
				.withSymbol(symbol1)
				.buildParams());
		expect(objectFactoryMock.createSecurity(terminalWithMocks, symbol1)).andReturn(securityStub);
		dataProviderMock.subscribeStateUpdates(securityStub);
		dataProviderMock.subscribeLevel1Data(symbol1, securityStub);
		dataProviderMock.subscribeLevel2Data(symbol1, securityStub);
		control.replay();
		
		terminalWithMocks.subscribe(symbol1);
		terminalWithMocks.subscribe(symbol1); // shouldn't subscribe
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe() {
		control.replay();

		terminalWithMocks.unsubscribe(symbol1);
		
		control.verify();
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
	public void testSchedule_SelfPlanned() {
		TaskHandler handlerMock = control.createMock(TaskHandler.class);
		SPRunnable runnableMock = control.createMock(SPRunnable.class);
		expect(schedulerMock.schedule(runnableMock)).andReturn(handlerMock);
		control.replay();
		
		assertSame(handlerMock, terminalWithMocks.schedule(runnableMock));
		
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
		EventType type = new EventTypeImpl();
		terminal.onTerminalReady().addAlternateType(type);
		terminal.onTerminalReady().addListener(listenerStub);
		terminal.onTerminalUnready().addAlternateType(type);
		terminal.onTerminalUnready().addListener(listenerStub);
		terminal.onOrderAvailable().addAlternateType(type);
		terminal.onOrderAvailable().addListener(listenerStub);
		terminal.onOrderCancelFailed().addAlternateType(type);
		terminal.onOrderCancelFailed().addListener(listenerStub);
		terminal.onOrderCancelled().addAlternateType(type);
		terminal.onOrderCancelled().addListener(listenerStub);
		terminal.onOrderExecution().addAlternateType(type);
		terminal.onOrderExecution().addListener(listenerStub);
		terminal.onOrderDone().addAlternateType(type);
		terminal.onOrderDone().addListener(listenerStub);
		terminal.onOrderFailed().addAlternateType(type);
		terminal.onOrderFailed().addListener(listenerStub);
		terminal.onOrderFilled().addAlternateType(type);
		terminal.onOrderFilled().addListener(listenerStub);
		terminal.onOrderPartiallyFilled().addAlternateType(type);
		terminal.onOrderPartiallyFilled().addListener(listenerStub);
		terminal.onOrderRegistered().addAlternateType(type);
		terminal.onOrderRegistered().addListener(listenerStub);
		terminal.onOrderRegisterFailed().addAlternateType(type);
		terminal.onOrderRegisterFailed().addListener(listenerStub);
		terminal.onOrderUpdate().addAlternateType(type);
		terminal.onOrderUpdate().addListener(listenerStub);
		terminal.onOrderArchived().addAlternateType(type);
		terminal.onOrderArchived().addListener(listenerStub);
		terminal.onPortfolioAvailable().addAlternateType(type);
		terminal.onPortfolioAvailable().addListener(listenerStub);
		terminal.onPortfolioUpdate().addAlternateType(type);
		terminal.onPortfolioUpdate().addListener(listenerStub);
		terminal.onPositionAvailable().addAlternateType(type);
		terminal.onPositionAvailable().addListener(listenerStub);
		terminal.onPositionChange().addAlternateType(type);
		terminal.onPositionChange().addListener(listenerStub);
		terminal.onPositionCurrentPriceChange().addAlternateType(type);
		terminal.onPositionCurrentPriceChange().addListener(listenerStub);
		terminal.onPositionUpdate().addAlternateType(type);
		terminal.onPositionUpdate().addListener(listenerStub);
		terminal.onSecurityAvailable().addAlternateType(type);
		terminal.onSecurityAvailable().addListener(listenerStub);
		terminal.onSecuritySessionUpdate().addAlternateType(type);
		terminal.onSecuritySessionUpdate().addListener(listenerStub);
		terminal.onSecurityUpdate().addAlternateType(type);
		terminal.onSecurityUpdate().addListener(listenerStub);
		terminal.onSecurityMarketDepthUpdate().addAlternateType(type);
		terminal.onSecurityMarketDepthUpdate().addListener(listenerStub);
		terminal.onSecurityBestAsk().addAlternateType(type);
		terminal.onSecurityBestAsk().addListener(listenerStub);
		terminal.onSecurityBestBid().addAlternateType(type);
		terminal.onSecurityBestBid().addListener(listenerStub);
		terminal.onSecurityLastTrade().addAlternateType(type);
		terminal.onSecurityLastTrade().addListener(listenerStub);
		// those event types shouldn't be cleared
		terminal.onSecurityClose().addListener(listenerStub);
		terminal.onSecurityClose().addAlternateType(type);
		terminal.onOrderClose().addListener(listenerStub);
		terminal.onOrderClose().addAlternateType(type);
		terminal.onPortfolioClose().addListener(listenerStub);
		terminal.onPortfolioClose().addAlternateType(type);
		terminal.onPositionClose().addListener(listenerStub);
		terminal.onPositionClose().addAlternateType(type);
		
		terminal.close();
		
		assertFalse(terminal.onTerminalReady().isAlternateType(type));
		assertFalse(terminal.onTerminalReady().isListener(listenerStub));
		assertFalse(terminal.onTerminalUnready().isAlternateType(type));
		assertFalse(terminal.onTerminalUnready().isListener(listenerStub));
		assertFalse(terminal.onOrderAvailable().hasAlternates());
		assertFalse(terminal.onOrderAvailable().hasListeners());
		assertFalse(terminal.onOrderCancelFailed().hasAlternates());
		assertFalse(terminal.onOrderCancelFailed().hasListeners());
		assertFalse(terminal.onOrderCancelled().hasAlternates());
		assertFalse(terminal.onOrderCancelled().hasListeners());
		assertFalse(terminal.onOrderExecution().hasAlternates());
		assertFalse(terminal.onOrderExecution().hasListeners());
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
		assertFalse(terminal.onOrderArchived().hasAlternates());
		assertFalse(terminal.onOrderArchived().hasListeners());
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
		// those event types shouldn't be cleared
		assertTrue(terminal.onSecurityClose().isListener(listenerStub));
		assertTrue(terminal.onSecurityClose().isAlternateType(type));
		assertTrue(terminal.onOrderClose().isListener(listenerStub));
		assertTrue(terminal.onOrderClose().isAlternateType(type));
		assertTrue(terminal.onPortfolioClose().isListener(listenerStub));
		assertTrue(terminal.onPortfolioClose().isAlternateType(type));
		assertTrue(terminal.onPositionClose().isListener(listenerStub));
		assertTrue(terminal.onPositionClose().isAlternateType(type));
	}
	
	@Test
	public void testStart() throws Exception {
		dataProviderMock.subscribeRemoteObjects(terminalWithMocks);
		control.replay();
		final CountDownLatch finished = new CountDownLatch(1);
		terminalWithMocks.onTerminalReady().addListener(new EventListener() {
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
		terminalWithMocks.onTerminalUnready().addListener(new EventListener() {
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
	
	@Test
	public void testArchiveOrders() throws Exception {
		dataProviderStub.nextOrderID = 1000L;
		EditableOrder order1 = createTestOrder(),
			order2 = createTestOrder(),
			order3 = createTestOrder(),
			order4 = createTestOrder(),
			order5 = createTestOrder();
		order2.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.STATUS, OrderStatus.CANCELLED)
				.withToken(OrderField.TIME_DONE, T("2017-08-06T22:20:00Z"))
				.buildUpdate());
		order5.consume(new DeltaUpdateBuilder()
				.withToken(OrderField.STATUS, OrderStatus.REJECTED)
				.withToken(OrderField.TIME_DONE, T("2017-08-06T22:20:00Z"))
				.withToken(OrderField.SYSTEM_MESSAGE, "Test Operation")
				.buildUpdate());
		terminal.onOrderArchived().addListener(listenerStub);
		
		terminal.archiveOrders();
		
		Set<Order> actual = terminal.getOrders();
		Set<Order> expected = new HashSet<>();
		expected.add(order1);
		expected.add(order3);
		expected.add(order4);
		assertEquals(expected, actual);
		assertFalse(order1.isClosed());
		assertTrue(order2.isClosed());
		assertFalse(order3.isClosed());
		assertFalse(order4.isClosed());
		assertTrue(order5.isClosed());
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(order2, terminal.onOrderArchived(), listenerStub.getEvent(0));
		assertOrderEvent(order5, terminal.onOrderArchived(), listenerStub.getEvent(1));
	}
	
	@Test
	public void testSuppressEvents() {
		EventDispatcher dispatcherMock = control.createMock(EventDispatcher.class);
		TerminalParams params = new TerminalParams();
		params.setDataProvider(new DataProviderStub());
		params.setEventDispatcher(dispatcherMock);
		terminal = new TerminalImpl(params);
		dispatcherMock.suppressEvents();
		control.replay();
		
		terminal.suppressEvents();
		
		control.verify();
	}
	
	@Test
	public void testRestoreEvents() {
		EventDispatcher dispatcherMock = control.createMock(EventDispatcher.class);
		TerminalParams params = new TerminalParams();
		params.setDataProvider(new DataProviderStub());
		params.setEventDispatcher(dispatcherMock);
		terminal = new TerminalImpl(params);
		dispatcherMock.restoreEvents();
		control.replay();
		
		terminal.restoreEvents();
		
		control.verify();
	}
	
	@Test
	public void testPurgeEvents() {
		EventDispatcher dispatcherMock = control.createMock(EventDispatcher.class);
		TerminalParams params = new TerminalParams();
		params.setDataProvider(new DataProviderStub());
		params.setEventDispatcher(dispatcherMock);
		terminal = new TerminalImpl(params);
		dispatcherMock.purgeEvents();
		control.replay();
		
		terminal.purgeEvents();
		
		control.verify();
	}
	
}
