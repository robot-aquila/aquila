package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.utils.Variant;


/**
 * 2012-08-16<br>
 * $Id: TerminalImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class TerminalImplTest {
	private IMocksControl control;
	private Starter starter;
	private EditableSecurities securities;
	private EditablePortfolios portfolios;
	private EditableOrders orders;
	private EditableOrders stopOrders;
	private OrderBuilder orderBuilder;
	private OrderProcessor orderProcessor;
	private EventDispatcher dispatcher;
	private EventType onConn,onDisc,onStarted,onStopped,onPanic;
	private TerminalController controller;
	private TerminalImpl terminal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		controller = control.createMock(TerminalController.class);
		starter = control.createMock(Starter.class);
		securities = control.createMock(EditableSecurities.class);
		portfolios = control.createMock(EditablePortfolios.class);
		orders = control.createMock(EditableOrders.class);
		stopOrders = control.createMock(EditableOrders.class);
		orderBuilder = control.createMock(OrderBuilder.class);
		orderProcessor = control.createMock(OrderProcessor.class);
		dispatcher = control.createMock(EventDispatcher.class);
		onConn = control.createMock(EventType.class);
		onDisc = control.createMock(EventType.class);
		onStarted = control.createMock(EventType.class);
		onStopped = control.createMock(EventType.class);
		onPanic = control.createMock(EventType.class);
		terminal = new TerminalImpl(starter, securities, portfolios,
									orders, stopOrders, orderBuilder,
									orderProcessor, controller, dispatcher, 
									onConn, onDisc, onStarted, onStopped,
									onPanic);
		expect(dispatcher.asString()).andStubReturn("TestDispatcher");
		expect(onPanic.asString()).andStubReturn("OnPanic");
		expect(onDisc.asString()).andStubReturn("OnDisconnected");
		expect(onStopped.asString()).andStubReturn("OnStopped");
	}
	
	@Test
	public void testConstruct14_Ok() throws Exception {
		Variant<Starter> vStarter = new Variant<Starter>()
			.add(starter).add(null);
		Variant<EditableSecurities> vSecurities =
				new Variant<EditableSecurities>(vStarter)
			.add(securities).add(null);
		Variant<EditablePortfolios> vPortfolios =
				new Variant<EditablePortfolios>(vSecurities)
			.add(portfolios).add(null);
		Variant<EditableOrders> vOrders =
				new Variant<EditableOrders>(vPortfolios)
			.add(orders).add(null);
		Variant<EditableOrders> vStopOrds =
				new Variant<EditableOrders>(vOrders)
			.add(stopOrders).add(null);
		Variant<OrderBuilder> vOrdBldr = new Variant<OrderBuilder>(vStopOrds)
			.add(null)
			.add(orderBuilder);
		Variant<OrderProcessor> vOrdProc = new Variant<OrderProcessor>(vOrdBldr)
			.add(null)
			.add(orderProcessor);
		Variant<TerminalController> vCtrl =
				new Variant<TerminalController>(vOrdProc)
			.add(null)
			.add(controller);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vCtrl)
			.add(null)
			.add(dispatcher);
		Variant<EventType> vOnConn = new Variant<EventType>(vDisp)
			.add(onConn)
			.add(null);
		Variant<EventType> vOnDisc = new Variant<EventType>(vOnConn)
			.add(null)
			.add(onDisc);
		Variant<EventType> vOnStart = new Variant<EventType>(vOnDisc)
			.add(null)
			.add(onStarted);
		Variant<EventType> vOnStop = new Variant<EventType>(vOnStart)
			.add(null)
			.add(onStopped);
		Variant<EventType> vOnPanic = new Variant<EventType>(vOnStop)
			.add(null)
			.add(onPanic);
		Variant<?> iterator = vOnPanic;
		int foundCount = 0,exceptionCount = 0;
		TerminalImpl found = null;
		do {
			try {
				TerminalImpl t = new TerminalImpl(vStarter.get(),
						vSecurities.get(), vPortfolios.get(),
						vOrders.get(), vStopOrds.get(), vOrdBldr.get(),
						vOrdProc.get(), vCtrl.get(), vDisp.get(),
						vOnConn.get(), vOnDisc.get(),
						vOnStart.get(), vOnStop.get(),
						vOnPanic.get());
				found = t;
				foundCount ++;
			} catch ( NullPointerException e ) {
				exceptionCount ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCount);
		assertEquals(iterator.count() - 1, exceptionCount);
		
		assertSame(starter, found.getStarter());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertSame(orderBuilder, found.getOrderBuilderInstance());
		assertSame(orderProcessor, found.getOrderProcessorInstance());
		assertSame(controller, found.getTerminalController());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onConn, found.OnConnected());
		assertSame(onDisc, found.OnDisconnected());
		assertSame(onStarted, found.OnStarted());
		assertSame(onStopped, found.OnStopped());
		assertSame(onPanic, found.OnPanic());
	}
	
	@Test
	public void testConstruct13_Ok() throws Exception {
		Variant<Starter> vStarter = new Variant<Starter>()
			.add(starter).add(null);
		Variant<EditableSecurities> vSecurities =
				new Variant<EditableSecurities>(vStarter)
			.add(securities).add(null);
		Variant<EditablePortfolios> vPortfolios =
				new Variant<EditablePortfolios>(vSecurities)
			.add(portfolios).add(null);
		Variant<EditableOrders> vOrders =
				new Variant<EditableOrders>(vPortfolios)
			.add(orders).add(null);
		Variant<EditableOrders> vStopOrds =
				new Variant<EditableOrders>(vOrders)
			.add(stopOrders).add(null);
		Variant<OrderBuilder> vOrdBldr = new Variant<OrderBuilder>(vStopOrds)
			.add(null)
			.add(orderBuilder);
		Variant<OrderProcessor> vOrdProc = new Variant<OrderProcessor>(vOrdBldr)
			.add(null)
			.add(orderProcessor);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vOrdProc)
			.add(null)
			.add(dispatcher);
		Variant<EventType> vOnConn = new Variant<EventType>(vDisp)
			.add(onConn)
			.add(null);
		Variant<EventType> vOnDisc = new Variant<EventType>(vOnConn)
			.add(null)
			.add(onDisc);
		Variant<EventType> vOnStart = new Variant<EventType>(vOnDisc)
			.add(null)
			.add(onStarted);
		Variant<EventType> vOnStop = new Variant<EventType>(vOnStart)
			.add(null)
			.add(onStopped);
		Variant<EventType> vOnPanic = new Variant<EventType>(vOnStop)
			.add(null)
			.add(onPanic);
		Variant<?> iterator = vOnPanic;
		int foundCount = 0,exceptionCount = 0;
		TerminalImpl found = null;
		do {
			try {
				TerminalImpl t = new TerminalImpl(vStarter.get(),
						vSecurities.get(), vPortfolios.get(),
						vOrders.get(), vStopOrds.get(), vOrdBldr.get(),
						vOrdProc.get(), vDisp.get(),
						vOnConn.get(), vOnDisc.get(),
						vOnStart.get(), vOnStop.get(),
						vOnPanic.get());
				found = t;
				foundCount ++;
			} catch ( NullPointerException e ) {
				exceptionCount ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCount);
		assertEquals(iterator.count() - 1, exceptionCount);
		
		assertSame(starter, found.getStarter());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertSame(orderBuilder, found.getOrderBuilderInstance());
		assertSame(orderProcessor, found.getOrderProcessorInstance());
		assertSame(TerminalController.class,
				found.getTerminalController().getClass());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onConn, found.OnConnected());
		assertSame(onDisc, found.OnDisconnected());
		assertSame(onStarted, found.OnStarted());
		assertSame(onStopped, found.OnStopped());
		assertSame(onPanic, found.OnPanic());
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		@SuppressWarnings("unchecked")
		List<Security> list = control.createMock(List.class);
		expect(securities.getSecurities()).andReturn(list);
		control.replay();
		
		assertSame(list, terminal.getSecurities());
		
		control.verify();

	}
	
	@Test
	public void testGetSecurity2() throws Exception {
		Security s = control.createMock(Security.class);
		expect(securities.getSecurity(eq("a"), eq("b"))).andReturn(s);
		control.replay();
		
		assertSame(s, terminal.getSecurity("a", "b"));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity1Descr() throws Exception {
		Security s = control.createMock(Security.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("foo", "bar", "JPY", SecurityType.UNK);
		expect(securities.getSecurity(eq(descr))).andReturn(s);
		control.replay();
		
		assertSame(s, terminal.getSecurity(descr));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity1Code() throws Exception {
		Security s = control.createMock(Security.class);
		expect(securities.getSecurity("foo")).andReturn(s);
		control.replay();
		
		assertSame(s, terminal.getSecurity("foo"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists2() throws Exception {
		expect(securities.isSecurityExists("a", "b")).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityExists("a", "b"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists1Code() throws Exception {
		expect(securities.isSecurityExists("charlie")).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityExists("charlie"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists1Descr() throws Exception {
		SecurityDescriptor descr =
			new SecurityDescriptor("foo", "bar", "EUR", SecurityType.OPT);
		expect(securities.isSecurityExists(eq(descr))).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityExists(descr));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityAmbiguous() throws Exception {
		expect(securities.isSecurityAmbiguous("gamma")).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityAmbiguous("gamma"));
		
		control.verify();
	}
	
	@Test
	public void testOnSecurityAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(securities.OnSecurityAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnSecurityAvailable());
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolios() throws Exception {
		@SuppressWarnings("unchecked")
		List<Portfolio> list = control.createMock(List.class);
		expect(portfolios.getPortfolios()).andReturn(list);
		control.replay();
		
		assertSame(list, terminal.getPortfolios());
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio1() throws Exception {
		Portfolio p = control.createMock(Portfolio.class);
		expect(portfolios.getPortfolio(new Account("number"))).andReturn(p);
		control.replay();
		
		assertSame(p, terminal.getPortfolio(new Account("number")));
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio0() throws Exception {
		Portfolio p = control.createMock(Portfolio.class);
		expect(portfolios.getDefaultPortfolio()).andReturn(p);
		control.replay();
		
		assertSame(p, terminal.getDefaultPortfolio());
		
		control.verify();
	}
	
	@Test
	public void testIsPortfolioAvailable() throws Exception {
		expect(portfolios.isPortfolioAvailable(new Account("gaga")))
			.andReturn(true);
		control.replay();
		
		assertTrue(terminal.isPortfolioAvailable(new Account("gaga")));
		
		control.verify();
	}
	
	@Test
	public void testOnPortfolioAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(portfolios.OnPortfolioAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnPortfolioAvailable());
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		Object[][] fix = {
				// initial state, should start?, exception?
				{ TerminalState.CONNECTED, false, true  },
				{ TerminalState.STARTED,   false, true  },
				{ TerminalState.STARTING,  false, false },
				{ TerminalState.STOPPED,   true,  false },
				{ TerminalState.STOPPING,  false, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			boolean exception = (Boolean) fix[i][2];
			TerminalState state = (TerminalState) fix[i][0];
			terminal.setTerminalState(state);
			if ( flag ) controller.runStartSequence(same(terminal));
			control.replay();
			try {
				terminal.start();
				assertFalse(msg, exception);
			} catch ( StarterException e ) {
				assertTrue(msg, exception);
			}
			control.verify();
			assertEquals(msg, flag ? TerminalState.STARTING : state,
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testStop() throws Exception {
		Object[][] fix = {
				// initial state, should stop?, exception?
				{ TerminalState.CONNECTED, true,  false },
				{ TerminalState.STARTED,   true,  false },
				{ TerminalState.STARTING,  false, false },
				{ TerminalState.STOPPED,   false, true  },
				{ TerminalState.STOPPING,  false, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			boolean exception = (Boolean) fix[i][2];
			TerminalState state = (TerminalState) fix[i][0];
			terminal.setTerminalState(state);
			if ( flag ) controller.runStopSequence(same(terminal));
			control.replay();
			try {
				terminal.stop();
				assertFalse(msg, exception);
			} catch ( StarterException e ) {
				assertTrue(msg, exception);
			}
			control.verify();
			assertEquals(msg, flag ? TerminalState.STOPPING : state,
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		expect(orders.isOrderExists(123L)).andReturn(true);
		expect(orders.isOrderExists(321L)).andReturn(false);
		control.replay();
		
		assertTrue(terminal.isOrderExists(123L));
		assertFalse(terminal.isOrderExists(321L));
		
		control.verify();
	}

	@Test
	public void testGetOrders() throws Exception {
		List<Order> list = new LinkedList<Order>();
		expect(orders.getOrders()).andReturn(list);
		control.replay();
		
		assertSame(list, terminal.getOrders());
		
		control.verify();
	}

	@Test
	public void testGetOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(orders.getOrder(345L)).andReturn(order);
		control.replay();
		
		assertSame(order, terminal.getOrder(345L));
		
		control.verify();
	}
	
	@Test
	public void testOnOrderAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnOrderAvailable());
		
		control.verify();
	}

	@Test
	public void testIsStopOrderExists() throws Exception {
		expect(stopOrders.isOrderExists(222L)).andReturn(true);
		expect(stopOrders.isOrderExists(333L)).andReturn(false);
		control.replay();
		
		assertTrue(terminal.isStopOrderExists(222L));
		assertFalse(terminal.isStopOrderExists(333L));
		
		control.verify();
	}

	@Test
	public void testGetStopOrders() throws Exception {
		List<Order> list = new LinkedList<Order>();
		expect(stopOrders.getOrders()).andReturn(list);
		control.replay();
		
		assertSame(list, terminal.getStopOrders());
		
		control.verify();
	}

	@Test
	public void testGetStopOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(stopOrders.getOrder(444L)).andReturn(order);
		control.replay();
		
		assertSame(order, terminal.getStopOrder(444L));
		
		control.verify();
	}
	
	@Test
	public void testOnStopOrderAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnStopOrderAvailable());
		
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderB() throws Exception {
		Order order = control.createMock(Order.class);
		Account account = new Account("test");
		Security security = control.createMock(Security.class);
		expect(orderBuilder.createMarketOrderB(same(account),
				same(security), eq(100L)))
			.andReturn(order);
		control.replay();
		assertSame(order, terminal.createMarketOrderB(account, security, 100L));
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderS() throws Exception {
		Order order = control.createMock(Order.class);
		Account account = new Account("test");
		Security security = control.createMock(Security.class);
		expect(orderBuilder.createMarketOrderS(same(account),
				same(security), eq(500L)))
			.andReturn(order);
		control.replay();
		assertSame(order, terminal.createMarketOrderS(account, security, 500L));
		control.verify();
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		Order order = control.createMock(Order.class);
		orderProcessor.placeOrder(same(order));
		control.replay();
		terminal.placeOrder(order);
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		Order order = control.createMock(Order.class);
		orderProcessor.cancelOrder(same(order));
		control.replay();
		terminal.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testGetDefaultCurrency() throws Exception {
		expect(securities.getDefaultCurrency()).andReturn("EUR");
		control.replay();
		assertEquals("EUR", terminal.getDefaultCurrency());
		control.verify();
	}
	
	@Test
	public void testGetDefaultType() throws Exception {
		expect(securities.getDefaultType()).andReturn(SecurityType.FUT);
		control.replay();
		assertEquals(SecurityType.FUT, terminal.getDefaultType());
		control.verify();
	}
	
	@Test
	public void testGetOrdersCount() throws Exception {
		expect(orders.getOrdersCount()).andReturn(12345);
		control.replay();
		assertEquals(12345, terminal.getOrdersCount());
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderCancelFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderCancelFailed());
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderCancelled()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderCancelled());
		control.verify();
	}
	
	@Test
	public void testOnOrderChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderChanged()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderChanged());
		control.verify();
	}
	
	@Test
	public void testOnOrderDone() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderDone()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderDone());
		control.verify();
	}
	
	@Test
	public void testOnOrderFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderFailed());
		control.verify();
	}
	
	@Test
	public void testOnOrderFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderFilled()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderFilled());
		control.verify();
	}
	
	@Test
	public void testOnOrderPartiallyFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderPartiallyFilled()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderPartiallyFilled());
		control.verify();
	}
	
	@Test
	public void testOnOrderRegistered() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderRegistered()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderRegistered());
		control.verify();
	}
	
	@Test
	public void testOnOrderRegisterFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderRegisterFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnOrderRegisterFailed());
		control.verify();
	}

	@Test
	public void testOnSecurityChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(securities.OnSecurityChanged()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnSecurityChanged());
		control.verify();
	}

	@Test
	public void testOnSecurityTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(securities.OnSecurityTrade()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnSecurityTrade());
		control.verify();
	}

	@Test
	public void testOnPortfolioChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(portfolios.OnPortfolioChanged()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnPortfolioChanged());
		control.verify();
	}

	@Test
	public void testOnPositionAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(portfolios.OnPositionAvailable()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnPositionAvailable());
		control.verify();
	}

	@Test
	public void testOnPositionChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(portfolios.OnPositionChanged()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnPositionChanged());
		control.verify();
	}
	
	@Test
	public void testGetSecuritiesCount() throws Exception {
		expect(securities.getSecuritiesCount()).andReturn(234);
		control.replay();
		assertEquals(234, terminal.getSecuritiesCount());
		control.verify();
	}
	
	@Test
	public void testGetPortfoliosCount() throws Exception {
		expect(portfolios.getPortfoliosCount()).andReturn(2341);
		control.replay();
		assertEquals(2341, terminal.getPortfoliosCount());
		control.verify();
	}

	@Test
	public void testGetStopOrdersCount() throws Exception {
		expect(stopOrders.getOrdersCount()).andReturn(2234);
		control.replay();
		assertEquals(2234, terminal.getStopOrdersCount());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderChanged()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderChanged());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderCancelFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderCancelFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderCancelFailed());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderCancelled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderCancelled()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderCancelled());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderDone() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderDone()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderDone());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderFailed());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderRegistered() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderRegistered()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderRegistered());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderRegisterFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderRegisterFailed()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnStopOrderRegisterFailed());
		control.verify();
	}
	
	@Test
	public void testFireOrderAvailableEvent() throws Exception {
		Order order = control.createMock(Order.class);
		orders.fireOrderAvailableEvent(same(order));
		control.replay();
		terminal.fireOrderAvailableEvent(order);
		control.verify();
	}
	
	@Test
	public void testGetEditableOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.getEditableOrder(158l)).andReturn(order);
		control.replay();
		assertSame(order, terminal.getEditableOrder(158l));
		control.verify();
	}
	
	@Test
	public void testRegisterOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		orders.registerOrder(same(order));
		control.replay();
		terminal.registerOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		orders.purgeOrder(same(order));
		control.replay();
		terminal.purgeOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeOrder_ById() throws Exception {
		orders.purgeOrder(eq(192l));
		control.replay();
		terminal.purgeOrder(192l);
		control.verify();
	}
	
	@Test
	public void testIsPendingOrder() throws Exception {
		expect(orders.isPendingOrder(eq(276l))).andReturn(true);
		expect(orders.isPendingOrder(eq(112l))).andReturn(false);
		control.replay();
		assertTrue(terminal.isPendingOrder(276l));
		assertFalse(terminal.isPendingOrder(112l));
		control.verify();
	}
	
	@Test
	public void testRegisterPendingOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		orders.registerPendingOrder(same(order));
		control.replay();
		terminal.registerPendingOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		orders.purgePendingOrder(same(order));
		control.replay();
		terminal.purgePendingOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingOrder_ById() throws Exception {
		orders.purgePendingOrder(eq(761l));
		control.replay();
		terminal.purgePendingOrder(761l);
		control.verify();
	}
	
	@Test
	public void testGetPendingOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.getPendingOrder(eq(628l))).andReturn(order);
		control.replay();
		assertSame(order, terminal.getPendingOrder(628l));
		control.verify();
	}
	
	@Test
	public void testFireStopOrderAvailableEvent() throws Exception {
		Order order = control.createMock(Order.class);
		stopOrders.fireOrderAvailableEvent(same(order));
		control.replay();
		terminal.fireStopOrderAvailableEvent(order);
		control.verify();
	}
	
	@Test
	public void testGetEditableStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.getEditableOrder(eq(192l))).andReturn(order);
		control.replay();
		assertSame(order, terminal.getEditableStopOrder(192l));
		control.verify();
	}
	
	@Test
	public void testRegisterStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		stopOrders.registerOrder(same(order));
		control.replay();
		terminal.registerStopOrder(order);
		control.verify();
	}

	@Test
	public void testPurgeStopOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		stopOrders.purgeOrder(same(order));
		control.replay();
		terminal.purgeStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeStopOrder_ById() throws Exception {
		stopOrders.purgeOrder(eq(754l));
		control.replay();
		terminal.purgeStopOrder(754l);
		control.verify();
	}
	
	@Test
	public void testIsPendingStopOrder() throws Exception {
		expect(stopOrders.isPendingOrder(eq(781l))).andReturn(false);
		expect(stopOrders.isPendingOrder(eq(442l))).andReturn(true);
		control.replay();
		assertFalse(terminal.isPendingStopOrder(781l));
		assertTrue(terminal.isPendingStopOrder(442l));
		control.verify();
	}
	
	@Test
	public void testRegisterPendingStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		stopOrders.registerPendingOrder(same(order));
		control.replay();
		terminal.registerPendingStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingStopOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		stopOrders.purgePendingOrder(same(order));
		control.replay();
		terminal.purgePendingStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingStopOrder_ById() throws Exception {
		stopOrders.purgePendingOrder(eq(245l));
		control.replay();
		terminal.purgePendingStopOrder(245l);
		control.verify();
	}
	
	@Test
	public void testGetPendingStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.getPendingOrder(eq(811l))).andReturn(order);
		control.replay();
		assertSame(order, terminal.getPendingStopOrder(811));
		control.verify();
	}
	
	@Test
	public void testFirePortfolioAvailableEvent() throws Exception {
		Portfolio portfolio = control.createMock(Portfolio.class);
		portfolios.firePortfolioAvailableEvent(same(portfolio));
		control.replay();
		terminal.firePortfolioAvailableEvent(portfolio);
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		expect(portfolios.getEditablePortfolio(eq(new Account("TST1"))))
			.andReturn(port);
		control.replay();
		assertSame(port, terminal.getEditablePortfolio(new Account("TST1")));
		control.verify();
	}
	
	@Test
	public void testRegisterPortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		portfolios.registerPortfolio(same(port));
		control.replay();
		terminal.registerPortfolio(port);
		control.verify();
	}
	
	@Test
	public void testSetDefaultPortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		portfolios.setDefaultPortfolio(same(port));
		control.replay();
		terminal.setDefaultPortfolio(port);
		control.verify();
	}
	
	@Test
	public void testGetEditableSecurity() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		expect(securities.getEditableSecurity(eq(descr))).andReturn(security);
		control.replay();
		assertSame(security, terminal.getEditableSecurity(descr));
		control.verify();
	}
	
	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		Security security = control.createMock(Security.class);
		securities.fireSecurityAvailableEvent(same(security));
		control.replay();
		terminal.fireSecurityAvailableEvent(security);
		control.verify();
	}
	
	@Test
	public void testFireTerminalConnectedEvent() throws Exception {
		Object[][] fix = {
				// initial state, dispatch & change?
				{ TerminalState.CONNECTED, false },
				{ TerminalState.STARTED, true },
				{ TerminalState.STARTING, false },
				{ TerminalState.STOPPED, false },
				{ TerminalState.STOPPING, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			TerminalState state = (TerminalState) fix[i][0];
			terminal.setTerminalState(state);
			if ( flag ) dispatcher.dispatch(new EventImpl(onConn));
			control.replay();
			terminal.fireTerminalConnectedEvent();
			control.verify();
			assertEquals(msg, flag ? TerminalState.CONNECTED : state,
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testFireTerminalDisconnectedEvent() throws Exception {
		Object[][] fix = {
				// initial state, dispatch & change?, expected state
				{ TerminalState.CONNECTED, true,  TerminalState.STARTED },
				{ TerminalState.STARTED,   false, TerminalState.STARTED },
				{ TerminalState.STARTING,  false, TerminalState.STARTING },
				{ TerminalState.STOPPED,   false, TerminalState.STOPPED },
				// еще не остановлен, можно
				{ TerminalState.STOPPING,  true,  TerminalState.STOPPING }, 
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			terminal.setTerminalState((TerminalState) fix[i][0]);
			if ( flag ) dispatcher.dispatch(new EventImpl(onDisc));
			control.replay();
			terminal.fireTerminalDisconnectedEvent();
			control.verify();
			assertEquals(msg, (TerminalState) fix[i][2],
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testFireTerminalStartedEvent() throws Exception {
		dispatcher.dispatch(new EventImpl(onStarted));
		control.replay();
		
		terminal.fireTerminalStartedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalStoppedEvent() throws Exception {
		dispatcher.dispatch(new EventImpl(onStarted));
		control.replay();
		
		terminal.fireTerminalStartedEvent();
		
		control.verify();
	}
	
	@Test
	public void testMakePendingOrderAsRegisteredIfExists() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.makePendingOrderAsRegisteredIfExists(127l, 19l))
			.andReturn(order);
		control.replay();
		assertSame(order,
				terminal.makePendingOrderAsRegisteredIfExists(127l, 19l));
		control.verify();
	}
	
	@Test
	public void testMakePendingStopOrderAsRegisteredIfExists()
			throws Exception
	{
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.makePendingOrderAsRegisteredIfExists(12l, 82l))
			.andReturn(order);
		control.replay();
		assertSame(order,
				terminal.makePendingStopOrderAsRegisteredIfExists(12l, 82l));
		control.verify();
	}
	
	@Test
	public void testFireTerminalPanicEvent2() throws Exception {
		final Object[] args = new Object[] { };
		helpFireTerminalPanicEvent(new Runnable() {
			@Override public void run() {
				dispatcher.dispatch(eq(new PanicEvent(onPanic,200,"ABC",args)));		
			}
		}, new Runnable() {
			@Override public void run() {
				terminal.firePanicEvent(200, "ABC");
			}
		});
	}

	@Test
	public void testFireTerminalPanicEvent3() throws Exception {
		final Object[] args = new Object[] { 500, "A", 800 };
		helpFireTerminalPanicEvent(new Runnable() {
			@Override public void run() {
				dispatcher.dispatch(eq(new PanicEvent(onPanic,200,"ABC",args)));		
			}
		}, new Runnable() {
			@Override public void run() {
				terminal.firePanicEvent(200, "ABC", args);
			}
		});
	}
	
	/**
	 * Вспомогательный метод тестирования.
	 * <p>
	 * Предназначен для тестирования методов
	 * {@link TerminalImpl#firePanicEvent(int, String) и
	 * {@link TerminalImpl#firePanicEvent(int, String, Object[])}. 
	 * <p>
	 * @param expectAction установить ожидания
	 * @param initAction инициирующее действие
	 * @throws Exception
	 */
	private void helpFireTerminalPanicEvent(Runnable expectAction,
			Runnable initAction) throws Exception
	{
		Object fix[][] = {
				// state, stop?
				{ TerminalState.CONNECTED, true  },
				{ TerminalState.STARTED,   true  },
				{ TerminalState.STARTING,  false },
				{ TerminalState.STOPPED,   false },
				{ TerminalState.STOPPING,  false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			TerminalState state = (TerminalState) fix[i][0];
			terminal.setTerminalState(state);
			if ( flag ) {
				expectAction.run();
				controller.runStopSequence(same(terminal));
			}
			control.replay();
			initAction.run();
			control.verify();
			assertEquals(msg, flag ? TerminalState.STOPPING : state,
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testStopped() throws Exception {
		Object[][] fix = {
				// state, stopped?
				{ TerminalState.CONNECTED, false },
				{ TerminalState.STARTED, false },
				{ TerminalState.STARTING, false },
				{ TerminalState.STOPPED, true },
				{ TerminalState.STOPPING, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			terminal.setTerminalState((TerminalState) fix[i][0]);
			assertEquals(msg, fix[i][1], terminal.stopped());
		}
	}
	
	@Test
	public void testStarted() throws Exception {
		Object[][] fix = {
				// state, started?
				{ TerminalState.CONNECTED, true },
				{ TerminalState.STARTED, true },
				{ TerminalState.STARTING, false },
				{ TerminalState.STOPPED, false },
				{ TerminalState.STOPPING, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			terminal.setTerminalState((TerminalState) fix[i][0]);
			assertEquals(msg, fix[i][1], terminal.started());
		}
	}
	
	@Test
	public void testConnected() throws Exception {
		Object[][] fix = {
				// state, connected?
				{ TerminalState.CONNECTED, true },
				{ TerminalState.STARTED, false },
				{ TerminalState.STARTING, false },
				{ TerminalState.STOPPED, false },
				{ TerminalState.STOPPING, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			terminal.setTerminalState((TerminalState) fix[i][0]);
			assertEquals(msg, fix[i][1], terminal.connected());
		}
	}
	
	@Test
	public void testGetSetTerminalState() throws Exception {
		assertEquals(TerminalState.STOPPED, terminal.getTerminalState());
		terminal.setTerminalState(TerminalState.CONNECTED);
		assertEquals(TerminalState.CONNECTED, terminal.getTerminalState());
	}
	
	@Test
	public void testOnStopOrderFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(stopOrders.OnOrderFilled()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnStopOrderFilled());
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderB() throws Exception {
		Account account = new Account("FOO");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(orderBuilder.createLimitOrderB(account, sec, 10, 230.05d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,terminal.createLimitOrderB(account, sec, 10, 230.05d));
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderS() throws Exception {
		Account account = new Account("FOO");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(orderBuilder.createLimitOrderS(account, sec, 10, 230.05d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,terminal.createLimitOrderS(account, sec, 10, 230.05d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitB() throws Exception {
		Account account = new Account("FOO");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(orderBuilder.createStopLimitB(account, sec, 10, 230.05d, 235.d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,
				terminal.createStopLimitB(account, sec, 10, 230.05d, 235.d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitS() throws Exception {
		Account account = new Account("FOO");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(orderBuilder.createStopLimitS(account, sec, 10, 230.05d, 215.d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,
				terminal.createStopLimitS(account, sec, 10, 230.05d, 215.d));
		
		control.verify();
	}
	
}
