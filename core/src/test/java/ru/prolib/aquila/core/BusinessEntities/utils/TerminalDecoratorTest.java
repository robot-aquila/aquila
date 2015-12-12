package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;

public class TerminalDecoratorTest {
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private EventType eventTypeMock;
	private Account account;
	private Security securityMock;
	private Order orderMock;
	private Portfolio portfolioMock;
	private Symbol symbol;
	private DateTime time;
	private Runnable runnable;
	private TaskHandler taskHandler; 
	private TerminalDecorator decorator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		eventTypeMock = control.createMock(EventType.class);
		account = new Account("Duna"); 
		securityMock = control.createMock(Security.class);
		orderMock = control.createMock(Order.class);
		portfolioMock = control.createMock(Portfolio.class);
		symbol = new Symbol("KSPS", "XXX", ISO4217.USD);
		time = new DateTime();
		runnable = control.createMock(Runnable.class);
		taskHandler = control.createMock(TaskHandler.class);
		decorator = new TerminalDecorator(terminalMock);
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		assertSame(terminalMock, decorator.getTerminal());
	}
	
	@Test
	public void testStarted() throws Exception {
		expect(terminalMock.started()).andReturn(false);
		expect(terminalMock.started()).andReturn(true);
		control.replay();
		
		assertFalse(decorator.started());
		assertTrue(decorator.started());
		
		control.verify();
	}
	
	@Test
	public void testStopped() throws Exception {
		expect(terminalMock.stopped()).andReturn(true);
		expect(terminalMock.stopped()).andReturn(false);
		control.replay();

		assertTrue(decorator.stopped());
		assertFalse(decorator.stopped());
		
		control.verify();
	}
	
	@Test
	public void testConnected() throws Exception {
		expect(terminalMock.connected()).andReturn(true);
		expect(terminalMock.connected()).andReturn(false);
		control.replay();

		assertTrue(decorator.connected());
		assertFalse(decorator.connected());
		
		control.verify();
	}
	
	@Test
	public void testGetTerminalState() throws Exception {
		expect(terminalMock.getTerminalState()).andReturn(TerminalState.CONNECTED);
		expect(terminalMock.getTerminalState()).andReturn(TerminalState.STARTING);
		control.replay();
		
		assertEquals(TerminalState.CONNECTED, decorator.getTerminalState());
		assertEquals(TerminalState.STARTING, decorator.getTerminalState());
		
		control.verify();
	}
	
	@Test
	public void testOnConnected() throws Exception {
		expect(terminalMock.OnConnected()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnConnected());
		
		control.verify();
	}

	@Test
	public void testOnDisconnected() throws Exception {
		expect(terminalMock.OnDisconnected()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnDisconnected());
		
		control.verify();
	}
	
	@Test
	public void testOnStarted() throws Exception {
		expect(terminalMock.OnStarted()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnStarted());
		
		control.verify();
	}
	
	@Test
	public void testOnStopped() throws Exception {
		expect(terminalMock.OnStopped()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnStopped());
		
		control.verify();
	}

	@Test
	public void testOnPanic() throws Exception {
		expect(terminalMock.OnPanic()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnPanic());
		
		control.verify();
	}
	
	@Test
	public void testOnReady() throws Exception {
		expect(terminalMock.OnReady()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnReady());
		
		control.verify();
	}
	
	@Test
	public void testOnUnready() throws Exception {
		expect(terminalMock.OnUnready()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnUnready());
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder5() throws Exception {
		expect(terminalMock.createOrder(account, Direction.BUY,
				securityMock, 100, 28.15d)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.createOrder(account, Direction.BUY,
				securityMock, 100, 28.15d));
		
		control.verify();
	}

	@Test
	public void testCreateOrder4() throws Exception {
		expect(terminalMock.createOrder(account, Direction.SELL,
				securityMock, 250)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.createOrder(account, Direction.SELL,
				securityMock, 250));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder6WActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(terminalMock.createOrder(account, Direction.BUY,
				securityMock, 1000, 250.34d, activator)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.createOrder(account, Direction.BUY,
				securityMock, 1000, 250.34d, activator));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder5WActivator() throws Exception {
		OrderActivator activator = control.createMock(OrderActivator.class);
		expect(terminalMock.createOrder(account, Direction.SELL,
				securityMock, 820, activator)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.createOrder(account, Direction.SELL,
				securityMock, 820, activator));
		
		control.verify();
	}

	@Test
	public void testRequestSecurity() throws Exception {
		terminalMock.requestSecurity(symbol);
		control.replay();
		
		decorator.requestSecurity(symbol);
		
		control.verify();
	}

	@Test
	public void testOnRequestSecurityError() throws Exception {
		expect(terminalMock.OnRequestSecurityError()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnRequestSecurityError());
		
		control.verify();
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		expect(terminalMock.isOrderExists(250)).andReturn(false);
		expect(terminalMock.isOrderExists(251)).andReturn(true);
		control.replay();
		
		assertFalse(decorator.isOrderExists(250));
		assertTrue(decorator.isOrderExists(251));
		
		control.verify();
	}

	@Test
	public void testGetOrders() throws Exception {
		List<Order> list = new Vector<Order>();
		expect(terminalMock.getOrders()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getOrders());
		
		control.verify();
	}
	
	@Test
	public void testGetOrdersCount() throws Exception {
		expect(terminalMock.getOrdersCount()).andReturn(540);
		control.replay();
		
		assertEquals(540, decorator.getOrdersCount());
		
		control.verify();
	}
	
	@Test
	public void testGetOrder() throws Exception {
		expect(terminalMock.getOrder(15)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.getOrder(15));
		
		control.verify();
	}

	@Test
	public void testOnOrderAvailable() throws Exception {
		expect(terminalMock.OnOrderAvailable()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderAvailable());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelFailed() throws Exception {
		expect(terminalMock.OnOrderCancelFailed()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderCancelFailed());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelled() throws Exception {
		expect(terminalMock.OnOrderCancelled()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderCancelled());
		
		control.verify();
	}

	@Test
	public void testOnOrderChanged() throws Exception {
		expect(terminalMock.OnOrderChanged()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderChanged());
		
		control.verify();
	}

	@Test
	public void testOnOrderDone() throws Exception {
		expect(terminalMock.OnOrderDone()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderDone());
		
		control.verify();
	}

	@Test
	public void testOnOrderFailed() throws Exception {
		expect(terminalMock.OnOrderFailed()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderFailed());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderFilled() throws Exception {
		expect(terminalMock.OnOrderFilled()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderFilled());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderPartiallyFilled() throws Exception {
		expect(terminalMock.OnOrderPartiallyFilled()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderPartiallyFilled());
		
		control.verify();
	}

	@Test
	public void testOnOrderRegistered() throws Exception {
		expect(terminalMock.OnOrderRegistered()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderRegistered());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderRegisterFailed() throws Exception {
		expect(terminalMock.OnOrderRegisterFailed()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderRegisterFailed());
		
		control.verify();
	}

	@Test
	public void testOnOrderTrade() throws Exception {
		expect(terminalMock.OnOrderTrade()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnOrderTrade());
		
		control.verify();
	}

	@Test
	public void testIsPortfolioAvailable() throws Exception {
		expect(terminalMock.isPortfolioAvailable(account)).andReturn(true);
		expect(terminalMock.isPortfolioAvailable(account)).andReturn(false);
		control.replay();
		
		assertTrue(decorator.isPortfolioAvailable(account));
		assertFalse(decorator.isPortfolioAvailable(account));
		
		control.verify();
	}

	@Test
	public void testOnPortfolioAvailable() throws Exception {
		expect(terminalMock.OnPortfolioAvailable()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnPortfolioAvailable());
		
		control.verify();
	}

	@Test
	public void testGetPortfolios() throws Exception {
		List<Portfolio> list = new Vector<Portfolio>();
		expect(terminalMock.getPortfolios()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getPortfolios());
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		expect(terminalMock.getPortfolio(account)).andReturn(portfolioMock);
		control.replay();
		
		assertSame(portfolioMock, decorator.getPortfolio(account));
		
		control.verify();
	}

	@Test
	public void testGetDefaultPortfolio() throws Exception {
		expect(terminalMock.getDefaultPortfolio()).andReturn(portfolioMock);
		control.replay();
		
		assertSame(portfolioMock, decorator.getDefaultPortfolio());
		
		control.verify();
	}

	@Test
	public void testOnPortfolioChanged() throws Exception {
		expect(terminalMock.OnPortfolioChanged()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnPortfolioChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnPositionAvailable() throws Exception {
		expect(terminalMock.OnPositionAvailable()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnPositionAvailable());
		
		control.verify();
	}
	
	@Test
	public void testOnPositionChanged() throws Exception {
		expect(terminalMock.OnPositionChanged()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnPositionChanged());
		
		control.verify();
	}

	@Test
	public void testGetPortfoliosCount() throws Exception {
		expect(terminalMock.getPortfoliosCount()).andReturn(215);
		control.replay();
		
		assertEquals(215, decorator.getPortfoliosCount());
		
		control.verify();
	}

	@Test
	public void testGetSecurities() throws Exception {
		List<Security> list = new Vector<Security>();
		expect(terminalMock.getSecurities()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getSecurities());
		
		control.verify();
	}

	@Test
	public void testGetSecurity() throws Exception {
		expect(terminalMock.getSecurity(symbol)).andReturn(securityMock);
		control.replay();
		
		assertSame(securityMock, decorator.getSecurity(symbol));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		expect(terminalMock.isSecurityExists(symbol)).andReturn(false);
		expect(terminalMock.isSecurityExists(symbol)).andReturn(true);
		control.replay();
		
		assertFalse(decorator.isSecurityExists(symbol));
		assertTrue(decorator.isSecurityExists(symbol));
		
		control.verify();
	}

	@Test
	public void testOnSecurityAvailable() throws Exception {
		expect(terminalMock.OnSecurityAvailable()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnSecurityAvailable());
		
		control.verify();
	}
	
	@Test
	public void testOnSecurityChanged() throws Exception {
		expect(terminalMock.OnSecurityChanged()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnSecurityChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnSecurityTrade() throws Exception {
		expect(terminalMock.OnSecurityTrade()).andReturn(eventTypeMock);
		control.replay();
		
		assertSame(eventTypeMock, decorator.OnSecurityTrade());
		
		control.verify();
	}
	
	@Test
	public void testGetSecuritiesCount() throws Exception {
		expect(terminalMock.getSecuritiesCount()).andReturn(712);
		control.replay();
		
		assertEquals(712, decorator.getSecuritiesCount());
		
		control.verify();
	}

	@Test
	public void testStart() throws Exception {
		terminalMock.start();
		control.replay();
		
		decorator.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		terminalMock.stop();
		control.replay();
		
		decorator.stop();
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		terminalMock.placeOrder(orderMock);
		control.replay();
		
		decorator.placeOrder(orderMock);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		terminalMock.cancelOrder(orderMock);
		control.replay();
		
		decorator.cancelOrder(orderMock);
		
		control.verify();
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		expect(terminalMock.getCurrentTime()).andReturn(time);
		control.replay();
		
		assertSame(time, decorator.getCurrentTime());
		
		control.verify();
	}

	@Test
	public void testSchedule2_RT() throws Exception {
		expect(terminalMock.schedule(runnable, time)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.schedule(runnable, time));
		
		control.verify();
	}

	@Test
	public void testSchedule3_RTL() throws Exception {
		expect(terminalMock.schedule(runnable, time, 250)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.schedule(runnable, time, 250));
		
		control.verify();
	}

	@Test
	public void testSchedule2_RL() throws Exception {
		expect(terminalMock.schedule(runnable, 420)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.schedule(runnable, 420));
		
		control.verify();
	}
	
	@Test
	public void testSchedule3_RLL() throws Exception {
		expect(terminalMock.schedule(runnable, 280, 415)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.schedule(runnable, 280, 415));
		
		control.verify();
	}

	@Test
	public void testScheduleAtFixedRate3_RTL() throws Exception {
		expect(terminalMock.scheduleAtFixedRate(runnable, time, 820))
			.andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.scheduleAtFixedRate(runnable, time, 820));
		
		control.verify();
	}
	
	@Test
	public void testScheduleAtFixedRate3_RLL() throws Exception {
		expect(terminalMock.scheduleAtFixedRate(runnable, 100, 210))
			.andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.scheduleAtFixedRate(runnable, 100, 210));
		
		control.verify();
	}
	
	@Test
	public void testCancel() throws Exception {
		terminalMock.cancel(runnable);
		control.replay();
		
		decorator.cancel(runnable);
		
		control.verify();
	}
	
	@Test
	public void testScheduled() throws Exception {
		expect(terminalMock.scheduled(runnable)).andReturn(false);
		expect(terminalMock.scheduled(runnable)).andReturn(true);
		control.replay();
		
		assertFalse(decorator.scheduled(runnable));
		assertTrue(decorator.scheduled(runnable));
		
		control.verify();
	}
	
	@Test
	public void testGetTaskHandler() throws Exception {
		expect(terminalMock.getTaskHandler(runnable)).andReturn(taskHandler);
		control.replay();
		
		assertSame(taskHandler, decorator.getTaskHandler(runnable));
		
		control.verify();
	}
	
	@Test
	public void testGetEventSystem() throws Exception {
		EventSystem esMock = control.createMock(EventSystem.class);
		expect(terminalMock.getEventSystem()).andReturn(esMock);
		control.replay();
		
		assertSame(esMock, decorator.getEventSystem());
		
		control.verify();
	}

	@Test
	public void testMarkTerminalConnected() throws Exception {
		terminalMock.markTerminalConnected();
		control.replay();
		
		decorator.markTerminalConnected();
		
		control.verify();
	}
	
	@Test
	public void testMarkTerminalDisconnected() throws Exception {
		terminalMock.markTerminalDisconnected();
		control.replay();
		
		decorator.markTerminalDisconnected();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalConnectedEvent() throws Exception {
		terminalMock.fireTerminalConnectedEvent();;
		control.replay();
		
		decorator.fireTerminalConnectedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalDisconnectedEvent() throws Exception {
		terminalMock.fireTerminalDisconnectedEvent();;
		control.replay();
		
		decorator.fireTerminalDisconnectedEvent();
		
		control.verify();
	}
	
	@Test
	public void testGetOrderProcessor() throws Exception {
		OrderProcessor opMock = control.createMock(OrderProcessor.class);
		expect(terminalMock.getOrderProcessor()).andReturn(opMock);
		control.replay();
		
		assertSame(opMock, decorator.getOrderProcessor());
		
		control.verify();
	}
	
	@Test
	public void testGetStarter() throws Exception {
		StarterQueue starterMock = control.createMock(StarterQueue.class);
		expect(terminalMock.getStarter()).andReturn(starterMock);
		control.replay();
		
		assertSame(starterMock, decorator.getStarter());
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalStartedEvent() throws Exception {
		terminalMock.fireTerminalStartedEvent();
		control.replay();
		
		decorator.fireTerminalStartedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalStoppedEvent() throws Exception {
		terminalMock.fireTerminalStoppedEvent();
		control.replay();
		
		decorator.fireTerminalStoppedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalReady() throws Exception {
		terminalMock.fireTerminalReady();
		control.replay();
		
		decorator.fireTerminalReady();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalUnready() throws Exception {
		terminalMock.fireTerminalUnready();
		control.replay();
		
		decorator.fireTerminalUnready();
		
		control.verify();;
	}

	@Test
	public void testSetTerminalState() throws Exception {
		terminalMock.setTerminalState(TerminalState.STOPPING);
		control.replay();
		
		decorator.setTerminalState(TerminalState.STOPPING);
		
		control.verify();
	}

	@Test
	public void testGetEditableSecurity() throws Exception {
		EditableSecurity secMock = control.createMock(EditableSecurity.class);
		expect(terminalMock.getEditableSecurity(symbol)).andReturn(secMock);
		control.replay();
		
		assertSame(secMock, decorator.getEditableSecurity(symbol));
		
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		EditablePortfolio portMock = control.createMock(EditablePortfolio.class);
		expect(terminalMock.getEditablePortfolio(account)).andReturn(portMock);
		control.replay();
		
		assertSame(portMock, decorator.getEditablePortfolio(account));
		
		control.verify();
	}
	
	@Test
	public void testCreateOrder() throws Exception {
		EditableOrder orderMock = control.createMock(EditableOrder.class);
		expect(terminalMock.createOrder()).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.createOrder());
		
		control.verify();
	}
	
	@Test
	public void testFireSecurityRequestError() throws Exception {
		terminalMock.fireSecurityRequestError(symbol, 1, "foo");
		control.replay();
		
		decorator.fireSecurityRequestError(symbol, 1, "foo");
		
		control.verify();
	}

	@Test
	public void testFireEvents_Security() throws Exception {
		EditableSecurity secMock = control.createMock(EditableSecurity.class);
		terminalMock.fireEvents(secMock);
		control.replay();
		
		decorator.fireEvents(secMock);
		
		control.verify();
	}

	@Test
	public void testFirePanicEvent2() throws Exception {
		terminalMock.firePanicEvent(112, "error");
		control.replay();
		
		decorator.firePanicEvent(112, "error");
		
		control.verify();
	}
	
	@Test
	public void testFirePanicEvent3() throws Exception {
		Object[] args = { 1, 2, 3 };
		terminalMock.firePanicEvent(425, "hello", args);
		control.replay();
		
		decorator.firePanicEvent(425, "hello", args);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Order() throws Exception {
		EditableOrder orderMock = control.createMock(EditableOrder.class);
		terminalMock.fireEvents(orderMock);
		control.replay();
		
		decorator.fireEvents(orderMock);
		
		control.verify();
	}

	@Test
	public void testGetEditableOrder() throws Exception {
		EditableOrder orderMock = control.createMock(EditableOrder.class);
		expect(terminalMock.getEditableOrder(1323)).andReturn(orderMock);
		control.replay();
		
		assertSame(orderMock, decorator.getEditableOrder(1323));
		
		control.verify();
	}
	
	@Test
	public void testPurgeOrder() throws Exception {
		terminalMock.purgeOrder(872);
		control.replay();
		
		decorator.purgeOrder(872);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio() throws Exception {
		EditablePortfolio portMock = control.createMock(EditablePortfolio.class);
		terminalMock.fireEvents(portMock);
		control.replay();
		
		decorator.fireEvents(portMock);
		
		control.verify();
	}
	
	@Test
	public void testSetDefaultPortfolio() throws Exception {
		EditablePortfolio portMock = control.createMock(EditablePortfolio.class);
		terminalMock.setDefaultPortfolio(portMock);
		control.replay();
		
		decorator.setDefaultPortfolio(portMock);
		
		control.verify();
	}
	
	@Test
	public void testGetOrderIdSequence() throws Exception {
		Counter idSeq = new SimpleCounter();
		expect(terminalMock.getOrderIdSequence()).andReturn(idSeq);
		control.replay();
		
		assertSame(idSeq, decorator.getOrderIdSequence());
		
		control.verify();
	}

}
