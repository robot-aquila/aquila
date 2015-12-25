package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.DataHandler;
import ru.prolib.aquila.core.data.DataHandlerImpl;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.utils.*;

/**
 * 2012-08-16<br>
 * $Id: TerminalImplTest.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public class TerminalImplTest {
	private static Account account;
	private static Symbol symbol;
	private IMocksControl control;
	private TerminalController controller;
	private TerminalEventDispatcher dispatcher;
	private Securities securities;
	private Portfolios portfolios;
	private Orders orders;
	private StarterQueue starter;
	private Scheduler scheduler;
	private EventSystem es;
	private OrderProcessor orderProcessor;
	private TerminalImpl terminal;
	private EditableOrder order;
	private Security security;
	private Runnable task;
	private TaskHandler taskHandler;
	private DateTime time = new DateTime();
	private DataProvider dataProvider;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		account = new Account("test");
		symbol = new Symbol("GAZP", "EQBR", "RUB", SymbolType.STOCK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		task = control.createMock(Runnable.class);
		controller = control.createMock(TerminalController.class);
		dispatcher = control.createMock(TerminalEventDispatcher.class);
		securities = control.createMock(Securities.class);
		portfolios = control.createMock(Portfolios.class);
		orders = control.createMock(OrdersImpl.class);
		starter = control.createMock(StarterQueue.class);
		scheduler = control.createMock(Scheduler.class);
		es = new EventSystemImpl();
		orderProcessor = control.createMock(OrderProcessor.class);
		security = control.createMock(Security.class);
		order = control.createMock(EditableOrder.class);
		dataProvider = control.createMock(DataProvider.class);
		TerminalParams params = new TerminalParams();
		params.setTerminalController(controller);
		params.setEventDispatcher(dispatcher);
		params.setSecurityRepository(securities);
		params.setPortfolioRepository(portfolios);
		params.setOrderRepository(orders);
		params.setStarter(starter);
		params.setScheduler(scheduler);
		params.setEventSystem(es);
		params.setOrderProcessor(orderProcessor);
		params.setDataProvider(dataProvider);
		params.setTerminalID("DummyTerminal");
		terminal = new TerminalImpl(params);

		expect(security.getSymbol()).andStubReturn(symbol);
		taskHandler = new TaskHandlerImpl(task, scheduler);
	}
	
	@Test
	public void testVersion() throws Exception {
		assertEquals(1, Terminal.VERSION);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(controller, terminal.getTerminalController());
		assertSame(dispatcher, terminal.getTerminalEventDispatcher());
		assertSame(securities, terminal.getSecurityStorage());
		assertSame(portfolios, terminal.getPortfolioStorage());
		assertSame(orders, terminal.getOrderStorage());
		assertSame(starter, terminal.getStarter());
		assertSame(scheduler, terminal.getScheduler());
		assertSame(es, terminal.getEventSystem());
		assertSame(orderProcessor, terminal.getOrderProcessor());
		assertEquals("DummyTerminal", terminal.getTerminalID());
		assertSame(dataProvider, terminal.getDataProvider());
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
		Symbol symbol = new Symbol("foo", "bar", "USD", SymbolType.UNKNOWN);
		expect(securities.getSecurity(eq(symbol))).andReturn(s);
		control.replay();
		
		assertSame(s, terminal.getSecurity(symbol));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		Symbol symbol = new Symbol("foo", "bar", "EUR", SymbolType.OPTION);
		expect(securities.isSecurityExists(eq(symbol))).andReturn(true);
		control.replay();
		
		assertTrue(terminal.isSecurityExists(symbol));
		
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
		expect(orders.isOrderExists(123)).andReturn(true);
		expect(orders.isOrderExists(321)).andReturn(false);
		control.replay();
		
		assertTrue(terminal.isOrderExists(123));
		assertFalse(terminal.isOrderExists(321));
		
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
		expect(orders.getOrder(345)).andReturn(order);
		control.replay();
		
		assertSame(order, terminal.getOrder(345));
		
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
	public void testPlaceOrder1() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.PENDING);
		expect(order.getActivator()).andReturn(null);
		orderProcessor.placeOrder(same(terminal), same(order));
		control.replay();
		
		terminal.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder1_Pending_WithActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(order.getStatus()).andReturn(OrderStatus.PENDING);
		expect(order.getActivator()).andReturn(activator);
		activator.start(same(order));
		order.setStatus(eq(OrderStatus.CONDITION));
		orders.fireEvents(same(order));
		control.replay();
		
		terminal.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder1_Condition_StopActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(order.getStatus()).andReturn(OrderStatus.CONDITION);
		expect(order.getActivator()).andReturn(activator);
		activator.stop();
		orderProcessor.placeOrder(same(terminal), same(order));
		control.replay();
		
		terminal.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Active() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		orderProcessor.cancelOrder(same(terminal), same(order));
		control.replay();
		
		terminal.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Pending() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.PENDING);
		order.setStatus(OrderStatus.CANCELLED);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(time);
		orders.fireEvents(same(order));
		control.replay();
		
		terminal.cancelOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Condition_StopActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(order.getStatus()).andReturn(OrderStatus.CONDITION);
		expect(order.getActivator()).andReturn(activator);
		activator.stop();
		order.setStatus(OrderStatus.CANCELLED);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(time);
		orders.fireEvents(same(order));
		control.replay();
		
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
	public void testFireEvents() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		orders.fireEvents(same(order));
		control.replay();
		
		terminal.fireEvents(order);
		
		control.verify();
	}
	
	@Test
	public void testGetEditableOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(orders.getEditableOrder(158)).andReturn(order);
		control.replay();
		assertSame(order, terminal.getEditableOrder(158));
		control.verify();
	}
	
	@Test
	public void testPurgeOrder() throws Exception {
		orders.purgeOrder(eq(192));
		control.replay();
		terminal.purgeOrder(192);
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio() throws Exception {
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		portfolios.fireEvents(same(p));
		control.replay();
		
		terminal.fireEvents(p);
		
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio1() throws Exception {
		Account a = new Account("TST1");
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		expect(portfolios.getEditablePortfolio(same(terminal), eq(a)))
			.andReturn(p);
		control.replay();
		
		assertSame(p, terminal.getEditablePortfolio(new Account("TST1")));
		
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
	public void testGetEditableSecurity1() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		Symbol symbol = control.createMock(Symbol.class);
		expect(securities.getEditableSecurity(same(terminal), eq(symbol))).andReturn(security);
		control.replay();
		
		assertSame(security, terminal.getEditableSecurity(symbol));
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Security() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		securities.fireEvents(same(security));
		control.replay();
		
		terminal.fireEvents(security);
		
		control.verify();
	}
	
	public void testTerminalConnected(Runnable initiator) throws Exception {
		Object[][] fix = {
				// initial state, dispatch & change?
				{ TerminalState.CONNECTED, false },
				{ TerminalState.STARTED, true },
				{ TerminalState.STARTING, false },
				{ TerminalState.STOPPED, false },
				{ TerminalState.STOPPING, false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			TerminalState state = (TerminalState) fix[i][0];
			terminal.setTerminalState(state);
			if ( flag ) {
				dispatcher.fireConnected(same(terminal));
			}
			control.replay();
			
			initiator.run();
			
			control.verify();
			assertEquals(msg, flag ? TerminalState.CONNECTED : state,
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testFireTerminalConnectedEvent() throws Exception {
		testTerminalConnected(new Runnable() {
			@Override public void run() {
				terminal.fireTerminalConnectedEvent();				
			}
		});
	}
	
	@Test
	public void testMarkTerminalConnected() throws Exception {
		testTerminalConnected(new Runnable() {
			@Override public void run() {
				terminal.markTerminalConnected();
			}
		});
	}
	
	private void testTerminalDisconnected(Runnable initiator) throws Exception {
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
			setUp();
			String msg = "At #" + i;
			boolean flag = (Boolean) fix[i][1];
			terminal.setTerminalState((TerminalState) fix[i][0]);
			if ( flag ) {
				dispatcher.fireDisconnected(same(terminal));
			}
			control.replay();
			
			initiator.run();
			
			control.verify();
			assertEquals(msg, (TerminalState) fix[i][2],
					terminal.getTerminalState());
		}
	}
	
	@Test
	public void testFireTerminalDisconnectedEvent() throws Exception {
		testTerminalDisconnected(new Runnable() {
			@Override public void run() {
				terminal.fireTerminalDisconnectedEvent();
			}
		});
	}
	
	@Test
	public void testMarkTerminalDisconnected() throws Exception {
		testTerminalDisconnected(new Runnable() {
			@Override public void run() {
				terminal.markTerminalDisconnected();
			}
		});
	}
	
	@Test
	public void testFireTerminalStartedEvent() throws Exception {
		dispatcher.fireStarted();
		control.replay();
		
		terminal.fireTerminalStartedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalStoppedEvent() throws Exception {
		dispatcher.fireStopped();
		control.replay();
		
		terminal.fireTerminalStoppedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalPanicEvent2() throws Exception {
		final Object[] args = { };
		helpFireTerminalPanicEvent(new Runnable() {
			@Override public void run() {
				dispatcher.firePanic(eq(200), eq("ABC"), aryEq(args));		
			}
		}, new Runnable() {
			@Override public void run() {
				terminal.firePanicEvent(200, "ABC");
			}
		});
	}

	@Test
	public void testFireTerminalPanicEvent3() throws Exception {
		final Object[] args = { 500, "A", 800 };
		helpFireTerminalPanicEvent(new Runnable() {
			@Override public void run() {
				dispatcher.firePanic(eq(200), eq("ABC"), aryEq(args));
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
	public void testGetCurrentTime() throws Exception {
		expect(scheduler.getCurrentTime()).andReturn(time);
		control.replay();
		
		assertSame(time, terminal.getCurrentTime());
		
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
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(terminal.equals(terminal));
		assertFalse(terminal.equals(null));
		assertFalse(terminal.equals(this));
	}
	
	@Test
	public void testOnOrderTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(orders.OnOrderTrade()).andReturn(type);
		control.replay();
		
		assertSame(type, terminal.OnOrderTrade());
		
		control.verify();
	}
	
	@Test
	public void testRequestSecurity_IfNotExists() throws Exception {
		DataHandler h1 = new DataHandlerImpl("state-update");
		DataHandler h2 = new DataHandlerImpl("trade-update");
		EditableSecurity security = control.createMock(EditableSecurity.class);
		expect(securities.isSecurityExists(symbol)).andReturn(false);
		expect(securities.getEditableSecurity(terminal, symbol)).andReturn(security);
		expect(dataProvider.subscribeForStateUpdates(security)).andReturn(h1);
		expect(dataProvider.subscribeForTradeUpdates(security)).andReturn(h2);
		control.replay();
		
		terminal.requestSecurity(symbol);
		
		control.verify();
	}
	
	@Test
	public void testRequestSecurity_IfExists() throws Exception {
		expect(securities.isSecurityExists(symbol)).andReturn(true);
		control.replay();
		
		terminal.requestSecurity(symbol);
		
		control.verify();
	}
	
	@Test
	public void testEventTypes() throws Exception {
		terminal = (TerminalImpl) new BasicTerminalBuilder().buildTerminal();
		dispatcher = terminal.getTerminalEventDispatcher();
		assertSame(dispatcher.OnConnected(), terminal.OnConnected());
		assertSame(dispatcher.OnDisconnected(), terminal.OnDisconnected());
		assertSame(dispatcher.OnPanic(), terminal.OnPanic());
		assertSame(dispatcher.OnRequestSecurityError(),
				terminal.OnRequestSecurityError());
		assertSame(dispatcher.OnStarted(), terminal.OnStarted());
		assertSame(dispatcher.OnStopped(), terminal.OnStopped());
	}
	
	@Test
	public void testFireRequestSecurityError() throws Exception {
		dispatcher.fireSecurityRequestError(eq(symbol), eq(1), eq("test msg"));
		control.replay();
		
		terminal.fireSecurityRequestError(symbol, 1, "test msg");
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder5() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		order.setType(eq(OrderType.LIMIT));
		order.setAccount(eq(account));
		order.setDirection(eq(Direction.BUY));
		order.setSymbol(eq(symbol));
		order.setQty(eq(1L));
		order.setQtyRest(eq(1L));
		order.setPrice(eq(15d));
		order.resetChanges();
		orders.fireEvents(same(order));
		control.replay();
		
		assertSame(order,
			terminal.createOrder(account, Direction.BUY, security, 1L, 15d));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder5_WithActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class); 
		expect(orders.createOrder(same(terminal))).andReturn(order);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		order.setType(eq(OrderType.LIMIT));
		order.setAccount(eq(account));
		order.setDirection(eq(Direction.BUY));
		order.setSymbol(eq(symbol));
		order.setQty(eq(1L));
		order.setQtyRest(eq(1L));
		order.setPrice(eq(15d));
		order.setActivator(same(activator));
		order.resetChanges();
		orders.fireEvents(same(order));
		control.replay();
		
		assertSame(order, terminal.createOrder(account, Direction.BUY, security,
				1L, 15d, activator));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder4() throws Exception {
		expect(orders.createOrder(same(terminal))).andReturn(order);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		order.setType(eq(OrderType.MARKET));
		order.setAccount(eq(account));
		order.setDirection(eq(Direction.SELL));
		order.setSymbol(eq(symbol));
		order.setQty(eq(10L));
		order.setQtyRest(eq(10L));
		order.resetChanges();
		orders.fireEvents(same(order));
		control.replay();
		
		assertSame(order,
			terminal.createOrder(account, Direction.SELL, security, 10L));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder4_WithActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(orders.createOrder(same(terminal))).andReturn(order);
		expect(scheduler.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		order.setType(eq(OrderType.MARKET));
		order.setAccount(eq(account));
		order.setDirection(eq(Direction.SELL));
		order.setSymbol(eq(symbol));
		order.setQty(eq(10L));
		order.setQtyRest(eq(10L));
		order.setActivator(same(activator));
		order.resetChanges();
		orders.fireEvents(same(order));
		control.replay();
		
		assertSame(order, terminal.createOrder(account, Direction.SELL,
				security, 10L, activator));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TD() throws Exception {
		expect(scheduler.schedule(task, time)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.schedule(task, time));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TDL() throws Exception {
		expect(scheduler.schedule(task, time, 215L)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.schedule(task, time, 215L));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TL() throws Exception {
		expect(scheduler.schedule(task, 220L)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.schedule(task, 220L));
		
		control.verify();
	}
	
	@Test
	public void testSchedule_TLL() throws Exception {
		expect(scheduler.schedule(task, 118L, 215L)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.schedule(task, 118L, 215L));
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_TDL() throws Exception {
		expect(scheduler.scheduleAtFixedRate(task, time, 302L))
			.andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.scheduleAtFixedRate(task, time, 302L));
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate_TLL() throws Exception {
		expect(scheduler.scheduleAtFixedRate(task, 80L, 94L))
			.andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.scheduleAtFixedRate(task, 80L, 94L));
		
		control.verify();
	}
	
	@Test
	public void testCancel() throws Exception {
		scheduler.cancel(task);
		control.replay();
		
		terminal.cancel(task);
		
		control.verify();
	}
	
	@Test
	public void testGetTaskHandler() throws Exception {
		expect(scheduler.getTaskHandler(task)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, terminal.getTaskHandler(task));
		
		control.verify();
	}
	
	@Test
	public void testScheduled() throws Exception {
		expect(scheduler.scheduled(task)).andReturn(true);
		expect(scheduler.scheduled(task)).andReturn(false);
		control.replay();
		
		assertTrue(terminal.scheduled(task));
		assertFalse(terminal.scheduled(task));
		
		control.verify();
	}
	
	@Test
	public void testOnReady() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnReady()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnReady());
		control.verify();
	}
	
	@Test
	public void testOnUnready() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(dispatcher.OnUnready()).andReturn(type);
		control.replay();
		assertSame(type, terminal.OnUnready());
		control.verify();
	}
	
	@Test
	public void testFireTerminalReady() throws Exception {
		dispatcher.fireReady();
		control.replay();
		
		terminal.fireTerminalReady();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalUnready() throws Exception {
		dispatcher.fireUnready();
		control.replay();
		
		terminal.fireTerminalUnready();
		
		control.verify();
	}
	
	@Test
	public void testGetOrderIdSequence() throws Exception {
		Counter idSeq = new SimpleCounter();
		expect(orders.getIdSequence()).andReturn(idSeq);
		control.replay();
		
		assertSame(idSeq, terminal.getOrderIdSequence());
		
		control.verify();
	}
	
}
