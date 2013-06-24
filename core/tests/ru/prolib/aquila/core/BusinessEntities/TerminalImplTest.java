package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventSystem es;
	private Starter starter;
	private EditableSecurities securities;
	private EditablePortfolios portfolios;
	private EditableOrders orders;
	private EditableOrders stopOrders;
	private OrderProcessor orderProcessor;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onConn,onDisc,onStarted,onStopped,onPanic;
	private TerminalController controller;
	private TerminalImpl terminal;
	private EditableOrder order;
	private Security security;
	private Timer timer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		account = new Account("test");
		descr = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
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
		orderProcessor = control.createMock(OrderProcessor.class);
		dispatcherMock = control.createMock(EventDispatcher.class);
		security = control.createMock(Security.class);
		order = control.createMock(EditableOrder.class);
		timer = control.createMock(Timer.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Terminal");
		onConn = dispatcher.createType("OnConnected");
		onDisc = dispatcher.createType("OnDisconnected");
		onStarted = dispatcher.createType("OnStarted");
		onStopped = dispatcher.createType("OnStopped");
		onPanic = dispatcher.createType("OnPanic");
		terminal = new TerminalImpl(es, timer, starter, securities, portfolios,
									orders, stopOrders, 
									controller, dispatcher, 
									onConn, onDisc, onStarted, onStopped,
									onPanic);
		expect(security.getDescriptor()).andStubReturn(descr);
	}
	
	@Test
	public void testConstruct14_Ok() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(null);
		Variant<Timer> vTimer = new Variant<Timer>(vEs)
			.add(timer)
			.add(null);
		Variant<Starter> vStarter = new Variant<Starter>(vTimer)
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
		Variant<TerminalController> vCtrl =
				new Variant<TerminalController>(vStopOrds)
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
				TerminalImpl t = new TerminalImpl(vEs.get(), vTimer.get(),
						vStarter.get(), vSecurities.get(), vPortfolios.get(),
						vOrders.get(), vStopOrds.get(), 
						vCtrl.get(), vDisp.get(),
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
		
		assertSame(es, found.getEventSystem());
		assertSame(starter, found.getStarter());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertSame(controller, found.getTerminalController());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onConn, found.OnConnected());
		assertSame(onDisc, found.OnDisconnected());
		assertSame(onStarted, found.OnStarted());
		assertSame(onStopped, found.OnStopped());
		assertSame(onPanic, found.OnPanic());
		assertSame(timer, found.getTimer());
	}
	
	@Test
	public void testConstruct12_Ok() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(null);
		Variant<Starter> vStarter = new Variant<Starter>(vEs)
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
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vStopOrds)
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
				TerminalImpl t = new TerminalImpl(vEs.get(),
						vStarter.get(), vSecurities.get(),
						vPortfolios.get(), vOrders.get(), vStopOrds.get(),
						vDisp.get(), vOnConn.get(),
						vOnDisc.get(), vOnStart.get(),
						vOnStop.get(), vOnPanic.get());
				found = t;
				foundCount ++;
			} catch ( NullPointerException e ) {
				exceptionCount ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCount);
		assertEquals(iterator.count() - 1, exceptionCount);
		
		assertSame(es, found.getEventSystem());
		assertSame(starter, found.getStarter());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertEquals(new TerminalController(), found.getTerminalController());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onConn, found.OnConnected());
		assertSame(onDisc, found.OnDisconnected());
		assertSame(onStarted, found.OnStarted());
		assertSame(onStopped, found.OnStopped());
		assertSame(onPanic, found.OnPanic());
		assertEquals(new TimerLocal(), found.getTimer());
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
	public void testGetSecurity() throws Exception {
		Security s = control.createMock(Security.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("foo", "bar", "JPY", SecurityType.UNK);
		expect(securities.getSecurity(eq(descr))).andReturn(s);
		control.replay();
		
		assertSame(s, terminal.getSecurity(descr));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		SecurityDescriptor descr =
			new SecurityDescriptor("foo", "bar", "EUR", SecurityType.OPT);
		expect(securities.isSecurityExists(eq(descr))).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityExists(descr));
		
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
	public void testGetDefaultPortfolio() throws Exception {
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
	public void testPlaceOrder() throws Exception {
		Order order = control.createMock(Order.class);
		orderProcessor.placeOrder(same(order));
		control.replay();
		terminal.setOrderProcessorInstance(orderProcessor);
		
		terminal.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		Order order = control.createMock(Order.class);
		orderProcessor.cancelOrder(same(order));
		control.replay();
		terminal.setOrderProcessorInstance(orderProcessor);
		
		terminal.cancelOrder(order);
		
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
		orders.registerOrder(eq(123L), same(order));
		control.replay();
		terminal.registerOrder(123L, order);
		control.verify();
	}
	
	@Test
	public void testPurgeOrder() throws Exception {
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
		orders.registerPendingOrder(eq(897L), same(order));
		control.replay();
		terminal.registerPendingOrder(897L, order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingOrder() throws Exception {
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
		stopOrders.registerOrder(eq(819L), same(order));
		control.replay();
		terminal.registerStopOrder(819L, order);
		control.verify();
	}

	@Test
	public void testPurgeStopOrder() throws Exception {
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
		stopOrders.registerPendingOrder(eq(394L), same(order));
		control.replay();
		terminal.registerPendingStopOrder(394L, order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingStopOrder() throws Exception {
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
	public void testRegisterPortfolio1() throws Exception {
		Account account = new Account("TEST");
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		expect(portfolios.createPortfolio(same(terminal), eq(account)))
			.andStubReturn(p);
		control.replay();
		
		assertSame(p, terminal.createPortfolio(account));
		
		control.verify();
	}

	@Test
	public void testRegisterPortfolio2() throws Exception {
		Account account = new Account("TEST");
		EditableTerminal t2 = control.createMock(EditableTerminal.class);
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		expect(portfolios.createPortfolio(same(t2), eq(account)))
			.andStubReturn(p);
		control.replay();
		
		assertSame(p, terminal.createPortfolio(t2, account));
		
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
	public void testMovePendingOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.movePendingOrder(127l, 19l)).andReturn(order);
		control.replay();
		assertSame(order, terminal.movePendingOrder(127l, 19l));
		control.verify();
	}
	
	@Test
	public void testMovePendingStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.movePendingOrder(12l, 82l)).andReturn(order);
		control.replay();
		assertSame(order, terminal.movePendingStopOrder(12l, 82l));
		control.verify();
	}
	
	@Test
	public void testFireTerminalPanicEvent2() throws Exception {
		final Object[] args = new Object[] { };
		helpFireTerminalPanicEvent(new Runnable() {
			@Override public void run() {
				Event e = new PanicEvent(onPanic,200,"ABC",args);
				dispatcherMock.dispatch(eq(e));		
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
				Event e = new PanicEvent(onPanic,200,"ABC",args);
				dispatcherMock.dispatch(eq(e));
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
			terminal = new TerminalImpl(es, timer, starter,
					securities, portfolios, orders, stopOrders, 
					controller, dispatcherMock, 
					onConn, onDisc, onStarted, onStopped,
					onPanic);
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
	public void testGetCurrentTime() throws Exception {
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		control.replay();
		
		assertSame(time, terminal.getCurrentTime());
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurity2() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		expect(securities.createSecurity(same(terminal), eq(descr)))
			.andReturn(security);
		control.replay();
		
		assertSame(security, terminal.createSecurity(terminal, descr));
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurity1() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		expect(securities.createSecurity(same(terminal), eq(descr)))
			.andReturn(security);
		control.replay();
		
		assertSame(security, terminal.createSecurity(descr));
		
		control.verify();
	}
	
	@Test
	public void testSetGetOrderProcessor() throws Exception {
		assertNull(terminal.getOrderProcessorInstance());
		terminal.setOrderProcessorInstance(orderProcessor);
		assertSame(orderProcessor, terminal.getOrderProcessorInstance());
		assertSame(orderProcessor, terminal.getOrderProcessorInstance());
	}
	
	@Test
	public void testHasPendingOrders() throws Exception {
		expect(orders.hasPendingOrders()).andReturn(true);
		expect(orders.hasPendingOrders()).andReturn(false);
		control.replay();
		
		assertTrue(terminal.hasPendingOrders());
		assertFalse(terminal.hasPendingOrders());
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder1() throws Exception {
		EditableTerminal t2 = control.createMock(EditableTerminal.class);
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.createOrder(same(t2))).andReturn(order);
		control.replay();
		
		assertSame(order, terminal.createOrder(t2));
		
		control.verify();
	}
	
	@Test
	public void testHasPendingStopOrders() throws Exception {
		expect(stopOrders.hasPendingOrders()).andReturn(false).andReturn(true);
		control.replay();
		
		assertFalse(terminal.hasPendingStopOrders());
		assertTrue(terminal.hasPendingStopOrders());
		
		control.verify();
	}
	
	@Test
	public void testCreateStopOrder1() throws Exception {
		EditableTerminal t2 = control.createMock(EditableTerminal.class);
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.createOrder(same(t2))).andReturn(order);
		control.replay();
		
		assertSame(order, terminal.createStopOrder(t2));
		
		control.verify();
	}

	@Test
	public void testCreateOrder0() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.createOrder(same(terminal))).andReturn(order);
		control.replay();
		
		terminal.createOrder();
		
		control.verify();
	}

	@Test
	public void testCreateStopOrder0() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(stopOrders.createOrder(same(terminal))).andReturn(order);
		control.replay();
		
		terminal.createStopOrder();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(terminal.equals(terminal));
		assertFalse(terminal.equals(null));
		assertFalse(terminal.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(control.createMock(EventSystem.class));
		Variant<Timer> vTmr = new Variant<Timer>(vEs)
			.add(timer)
			.add(control.createMock(Timer.class));
		Variant<Starter> vSta = new Variant<Starter>(vTmr)
			.add(starter)
			.add(control.createMock(Starter.class));
		Variant<EditableSecurities> vScs = new Variant<EditableSecurities>(vSta)
			.add(securities)
			.add(control.createMock(EditableSecurities.class));
		Variant<EditablePortfolios> vPts = new Variant<EditablePortfolios>(vScs)
			.add(portfolios)
			.add(control.createMock(EditablePortfolios.class));
		Variant<EditableOrders> vOrds = new Variant<EditableOrders>(vPts)
			.add(orders)
			.add(control.createMock(EditableOrders.class));
		Variant<EditableOrders> vStOrds = new Variant<EditableOrders>(vOrds)
			.add(stopOrders)
			.add(control.createMock(EditableOrders.class));
		Variant<TerminalController> vCtrl =
				new Variant<TerminalController>(vStOrds)
			.add(controller)
			.add(control.createMock(TerminalController.class));
		Variant<String> vDispId = new Variant<String>(vCtrl)
			.add("Terminal")
			.add("TerminalX");
		Variant<String> vConnId = new Variant<String>(vDispId)
			.add("OnConnected")
			.add("OnConnectedX");
		Variant<String> vDiscId = new Variant<String>(vConnId)
			.add("OnDisconnected")
			.add("OnDisconnectedX");
		Variant<String> vStartId = new Variant<String>(vDiscId)
			.add("OnStarted")
			.add("OnStartedX");
		Variant<String> vStopId = new Variant<String>(vStartId)
			.add("OnStopped")
			.add("OnStoppedX");
		Variant<String> vPanicId = new Variant<String>(vStopId)
			.add("OnPanic")
			.add("OnPanicX");
		Variant<TerminalState> vStat = new Variant<TerminalState>(vPanicId)
			.add(TerminalState.STOPPED)
			.add(TerminalState.STARTING);
		Variant<OrderProcessor> vOrdProc = new Variant<OrderProcessor>(vStat)
			.add(orderProcessor)
			.add(control.createMock(OrderProcessor.class));
		Variant<?> iterator = vOrdProc;
		terminal.setTerminalState(TerminalState.STOPPED);
		terminal.setOrderProcessorInstance(orderProcessor);
		int foundCnt = 0;
		TerminalImpl x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new TerminalImpl(vEs.get(), vTmr.get(), vSta.get(), vScs.get(),
					vPts.get(), vOrds.get(), vStOrds.get(), vCtrl.get(),
					d, d.createType(vConnId.get()), d.createType(vDiscId.get()),
					d.createType(vStartId.get()), d.createType(vStopId.get()),
					d.createType(vPanicId.get()));
			x.setTerminalState(vStat.get());
			x.setOrderProcessorInstance(vOrdProc.get());
			if ( terminal.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(es, found.getEventSystem());
		assertSame(securities, found.getSecuritiesInstance());
		assertSame(portfolios, found.getPortfoliosInstance());
		assertSame(orders, found.getOrdersInstance());
		assertSame(stopOrders, found.getStopOrdersInstance());
		assertSame(starter, found.getStarter());
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onConn, found.OnConnected());
		assertEquals(onDisc, found.OnDisconnected());
		assertEquals(onStarted, found.OnStarted());
		assertEquals(onStopped, found.OnStopped());
		assertEquals(onPanic, found.OnPanic());
		assertSame(TerminalState.STOPPED, found.getTerminalState());
		assertSame(orderProcessor, found.getOrderProcessorInstance());
		assertSame(timer, found.getTimer());
	}
	
	@Test
	public void testCreateMarketOrderB() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.BUY);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(10L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.MARKET);
		order.setQtyRest(10L);
		control.replay();
		
		assertSame(order, terminal.createMarketOrderB(account, security, 10));
		
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderS() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.SELL);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(5L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.MARKET);
		order.setQtyRest(5L);
		control.replay();
		
		assertSame(order, terminal.createMarketOrderS(account, security, 5));
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderB() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.BUY);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(50L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.LIMIT);
		order.setQtyRest(50L);
		order.setPrice(23.5d);
		control.replay();
		
		assertSame(order,
				terminal.createLimitOrderB(account, security, 50, 23.5d));
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderS() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.SELL);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(10L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.LIMIT);
		order.setQtyRest(10L);
		order.setPrice(83.5d);
		control.replay();
		
		assertSame(order,
				terminal.createLimitOrderS(account, security, 10, 83.5d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitB() throws Exception {
		expect(stopOrders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.BUY);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(50L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.STOP_LIMIT);
		order.setPrice(24.0d);
		order.setStopLimitPrice(23.5d);
		control.replay();
		
		assertSame(order,
				terminal.createStopLimitB(account, security, 50, 23.5d, 24.0d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitS() throws Exception {
		expect(stopOrders.createOrder(same(terminal))).andReturn(order);
		order.setDirection(OrderDirection.SELL);
		Date time = new Date();
		expect(timer.getCurrentTime()).andReturn(time);
		order.setTime(time);
		order.setAccount(account);
		order.setQty(80L);
		order.setSecurityDescriptor(descr);
		order.setStatus(OrderStatus.PENDING);
		order.setType(OrderType.STOP_LIMIT);
		order.setPrice(22.0d);
		order.setStopLimitPrice(23.5d);
		control.replay();
		
		assertSame(order,
				terminal.createStopLimitS(account, security, 80, 23.5d, 22.0d));
		
		control.verify();
	}
	
	@Test
	public void testOnOrderTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderTrade()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnOrderTrade());
		
		control.verify();
	}

}
