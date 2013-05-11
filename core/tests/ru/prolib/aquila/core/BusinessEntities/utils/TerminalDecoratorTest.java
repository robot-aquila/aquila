package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;

/**
 * 2012-08-17<br>
 * $Id: TerminalDecoratorTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class TerminalDecoratorTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private TerminalDecorator decorator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		decorator = new TerminalDecorator();
		decorator.setTerminal(terminal);
	}
	
	@Test
	public void testGetEventSystem() throws Exception {
		EventSystem es = control.createMock(EventSystem.class);
		expect(terminal.getEventSystem()).andReturn(es);
		control.replay();
		
		assertSame(es, decorator.getEventSystem());
		
		control.verify();
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		@SuppressWarnings("unchecked")
		List<Security> ss = control.createMock(List.class);
		expect(terminal.getSecurities()).andReturn(ss);
		control.replay();
		
		assertSame(ss, decorator.getSecurities());
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		Security s = control.createMock(Security.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("one", "two", "USD", SecurityType.FUT);
		expect(terminal.getSecurity(eq(descr))).andReturn(s);
		control.replay();
		
		assertSame(s, decorator.getSecurity(descr));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		SecurityDescriptor descr =
			new SecurityDescriptor("one", "two", "EUR", SecurityType.STK);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(true);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		control.replay();
		
		assertTrue(decorator.isSecurityExists(descr));
		assertFalse(decorator.isSecurityExists(descr));
		
		control.verify();
	}
	
	@Test
	public void testOnSecurityAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnSecurityAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnSecurityAvailable());
		
		control.verify();
	}
	
	@Test
	public void testIsPortfolioAvailable() throws Exception {
		Account acc = new Account("PORT");
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		control.replay();
		
		assertTrue(decorator.isPortfolioAvailable(acc));
		assertFalse(decorator.isPortfolioAvailable(acc));
		
		control.verify();
	}
	
	@Test
	public void testOnPortfolioAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnPortfolioAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnPortfolioAvailable());
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolios() throws Exception {
		@SuppressWarnings("unchecked")
		List<Portfolio> list = control.createMock(List.class);
		expect(terminal.getPortfolios()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getPortfolios());
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio1() throws Exception {
		Account acc = new Account("foobar");
		Portfolio p = control.createMock(Portfolio.class);
		expect(terminal.getPortfolio(eq(acc))).andReturn(p);
		control.replay();
		
		assertSame(p, decorator.getPortfolio(acc));
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		terminal.start();
		control.replay();
		
		decorator.start();
		
		control.verify();
	}
	
	@Test
	public void testStarted() throws Exception {
		expect(terminal.started()).andReturn(true);
		expect(terminal.started()).andReturn(false);
		control.replay();
		
		assertTrue(decorator.started());
		assertFalse(decorator.started());
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		terminal.stop();
		control.replay();
		
		decorator.stop();
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio0() throws Exception {
		Portfolio p = control.createMock(Portfolio.class);
		expect(terminal.getDefaultPortfolio()).andReturn(p);
		control.replay();
		
		assertSame(p, decorator.getDefaultPortfolio());
		
		control.verify();
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		expect(terminal.isOrderExists(123L)).andReturn(true);
		expect(terminal.isOrderExists(567L)).andReturn(false);
		control.replay();
		
		assertTrue(decorator.isOrderExists(123L));
		assertFalse(decorator.isOrderExists(567L));
		
		control.verify();
	}
	
	@Test
	public void testGetOrders() throws Exception {
		List<Order> list = new LinkedList<Order>();
		expect(terminal.getOrders()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getOrders());
		
		control.verify();
	}
	
	@Test
	public void testGetOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(terminal.getOrder(123L)).andReturn(order);
		control.replay();
		
		assertSame(order, decorator.getOrder(123L));
		
		control.verify();
	}
	
	@Test
	public void testOnOrderAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnOrderAvailable());
		
		control.verify();
	}
	
	@Test
	public void testIsStopOrderExists() throws Exception {
		expect(terminal.isStopOrderExists(543L)).andReturn(false);
		expect(terminal.isStopOrderExists(876L)).andReturn(true);
		control.replay();

		assertFalse(decorator.isStopOrderExists(543L));
		assertTrue(decorator.isStopOrderExists(876L));
		
		control.verify();
	}

	@Test
	public void testGetStopOrders() throws Exception {
		List<Order> list = new LinkedList<Order>();
		expect(terminal.getStopOrders()).andReturn(list);
		control.replay();
		
		assertSame(list, decorator.getStopOrders());
		
		control.verify();
	}
	
	@Test
	public void testGetStopOrder() throws Exception {
		Order order = control.createMock(Order.class);
		expect(terminal.getStopOrder(234L)).andReturn(order);
		control.replay();
		
		assertSame(order, decorator.getStopOrder(234L));
		
		control.verify();
	}
	
	@Test
	public void testOnStopOrderAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderAvailable()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnStopOrderAvailable());
		
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderB() throws Exception {
		Order order = control.createMock(Order.class);
		Account acc = new Account("test");
		Security sec = control.createMock(Security.class);
		expect(terminal.createMarketOrderB(acc, sec, 100L)).andReturn(order);
		control.replay();
		assertSame(order, decorator.createMarketOrderB(acc, sec, 100L));
		control.verify();
	}
	
	@Test
	public void testCreateMarketOrderS() throws Exception {
		Order order = control.createMock(Order.class);
		Account acc = new Account("test");
		Security sec = control.createMock(Security.class);
		expect(terminal.createMarketOrderS(acc, sec, 200L)).andReturn(order);
		control.replay();
		assertSame(order, decorator.createMarketOrderS(acc, sec, 200L));
		control.verify();
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		Order order = control.createMock(Order.class);
		terminal.placeOrder(same(order));
		control.replay();
		decorator.placeOrder(order);
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		Order order = control.createMock(Order.class);
		terminal.cancelOrder(same(order));
		control.replay();
		decorator.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testGetOrdersCount() throws Exception {
		expect(terminal.getOrdersCount()).andReturn(12345);
		control.replay();
		assertEquals(12345, decorator.getOrdersCount());
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderCancelFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderCancelFailed());
		control.verify();
	}
	
	@Test
	public void testOnOrderCancelled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderCancelled()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderCancelled());
		control.verify();
	}
	
	@Test
	public void testOnOrderChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderChanged()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderChanged());
		control.verify();
	}
	
	@Test
	public void testOnOrderDone() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderDone()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderDone());
		control.verify();
	}
	
	@Test
	public void testOnOrderFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderFailed());
		control.verify();
	}
	
	@Test
	public void testOnOrderFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderFilled()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderFilled());
		control.verify();
	}
	
	@Test
	public void testOnOrderPartiallyFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderPartiallyFilled()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderPartiallyFilled());
		control.verify();
	}
	
	@Test
	public void testOnOrderRegistered() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderRegistered()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderRegistered());
		control.verify();
	}
	
	@Test
	public void testOnOrderRegisterFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnOrderRegisterFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnOrderRegisterFailed());
		control.verify();
	}

	@Test
	public void testOnSecurityChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnSecurityChanged()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnSecurityChanged());
		control.verify();
	}

	@Test
	public void testOnSecurityTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnSecurityTrade()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnSecurityTrade());
		control.verify();
	}

	@Test
	public void testOnPortfolioChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnPortfolioChanged()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnPortfolioChanged());
		control.verify();
	}

	@Test
	public void testOnPositionAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnPositionAvailable()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnPositionAvailable());
		control.verify();
	}

	@Test
	public void testOnPositionChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnPositionChanged()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnPositionChanged());
		control.verify();
	}
	
	@Test
	public void testGetSecuritiesCount() throws Exception {
		expect(terminal.getSecuritiesCount()).andReturn(234);
		control.replay();
		assertEquals(234, decorator.getSecuritiesCount());
		control.verify();
	}
	
	@Test
	public void testGetPortfoliosCount() throws Exception {
		expect(terminal.getPortfoliosCount()).andReturn(2341);
		control.replay();
		assertEquals(2341, decorator.getPortfoliosCount());
		control.verify();
	}

	@Test
	public void testGetStopOrdersCount() throws Exception {
		expect(terminal.getStopOrdersCount()).andReturn(2234);
		control.replay();
		assertEquals(2234, decorator.getStopOrdersCount());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderChanged()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderChanged());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderCancelFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderCancelFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderCancelFailed());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderCancelled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderCancelled()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderCancelled());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderDone() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderDone()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderDone());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderFailed());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderRegistered() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderRegistered()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderRegistered());
		control.verify();
	}
	
	@Test
	public void testOnStopOrderRegisterFailed() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderRegisterFailed()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnStopOrderRegisterFailed());
		control.verify();
	}

	@Test
	public void testFireOrderAvailableEvent() throws Exception {
		Order order = control.createMock(Order.class);
		terminal.fireOrderAvailableEvent(same(order));
		control.replay();
		decorator.fireOrderAvailableEvent(order);
		control.verify();
	}
	
	@Test
	public void testGetEditableOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.getEditableOrder(158l)).andReturn(order);
		control.replay();
		assertSame(order, decorator.getEditableOrder(158l));
		control.verify();
	}
	
	@Test
	public void testRegisterOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.registerOrder(same(order));
		control.replay();
		decorator.registerOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.purgeOrder(same(order));
		control.replay();
		decorator.purgeOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeOrder_ById() throws Exception {
		terminal.purgeOrder(eq(192l));
		control.replay();
		decorator.purgeOrder(192l);
		control.verify();
	}
	
	@Test
	public void testIsPendingOrder() throws Exception {
		expect(terminal.isPendingOrder(eq(276l))).andReturn(true);
		expect(terminal.isPendingOrder(eq(112l))).andReturn(false);
		control.replay();
		assertTrue(decorator.isPendingOrder(276l));
		assertFalse(decorator.isPendingOrder(112l));
		control.verify();
	}
	
	@Test
	public void testRegisterPendingOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.registerPendingOrder(same(order));
		control.replay();
		decorator.registerPendingOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.purgePendingOrder(same(order));
		control.replay();
		decorator.purgePendingOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingOrder_ById() throws Exception {
		terminal.purgePendingOrder(eq(761l));
		control.replay();
		decorator.purgePendingOrder(761l);
		control.verify();
	}
	
	@Test
	public void testGetPendingOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.getPendingOrder(eq(628l))).andReturn(order);
		control.replay();
		assertSame(order, decorator.getPendingOrder(628l));
		control.verify();
	}
	
	@Test
	public void testFireStopOrderAvailableEvent() throws Exception {
		Order order = control.createMock(Order.class);
		terminal.fireStopOrderAvailableEvent(same(order));
		control.replay();
		decorator.fireStopOrderAvailableEvent(order);
		control.verify();
	}
	
	@Test
	public void testGetEditableStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.getEditableStopOrder(eq(192l))).andReturn(order);
		control.replay();
		assertSame(order, decorator.getEditableStopOrder(192l));
		control.verify();
	}
	
	@Test
	public void testRegisterStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.registerStopOrder(same(order));
		control.replay();
		decorator.registerStopOrder(order);
		control.verify();
	}

	@Test
	public void testPurgeStopOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.purgeStopOrder(same(order));
		control.replay();
		decorator.purgeStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgeStopOrder_ById() throws Exception {
		terminal.purgeStopOrder(eq(754l));
		control.replay();
		decorator.purgeStopOrder(754l);
		control.verify();
	}
	
	@Test
	public void testIsPendingStopOrder() throws Exception {
		expect(terminal.isPendingStopOrder(eq(781l))).andReturn(false);
		expect(terminal.isPendingStopOrder(eq(442l))).andReturn(true);
		control.replay();
		assertFalse(decorator.isPendingStopOrder(781l));
		assertTrue(decorator.isPendingStopOrder(442l));
		control.verify();
	}
	
	@Test
	public void testRegisterPendingStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.registerPendingStopOrder(same(order));
		control.replay();
		decorator.registerPendingStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingStopOrder_ByOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		terminal.purgePendingStopOrder(same(order));
		control.replay();
		decorator.purgePendingStopOrder(order);
		control.verify();
	}
	
	@Test
	public void testPurgePendingStopOrder_ById() throws Exception {
		terminal.purgePendingStopOrder(eq(245l));
		control.replay();
		decorator.purgePendingStopOrder(245l);
		control.verify();
	}
	
	@Test
	public void testGetPendingStopOrder() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.getPendingStopOrder(eq(811l))).andReturn(order);
		control.replay();
		assertSame(order, decorator.getPendingStopOrder(811));
		control.verify();
	}
	
	@Test
	public void testFirePortfolioAvailableEvent() throws Exception {
		Portfolio portfolio = control.createMock(Portfolio.class);
		terminal.firePortfolioAvailableEvent(same(portfolio));
		control.replay();
		decorator.firePortfolioAvailableEvent(portfolio);
		control.verify();
	}
	
	@Test
	public void testGetEditablePortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		expect(terminal.getEditablePortfolio(eq(new Account("TST1"))))
			.andReturn(port);
		control.replay();
		assertSame(port, decorator.getEditablePortfolio(new Account("TST1")));
		control.verify();
	}
	
	@Test
	public void testRegisterPortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		terminal.registerPortfolio(same(port));
		control.replay();
		decorator.registerPortfolio(port);
		control.verify();
	}
	
	@Test
	public void testSetDefaultPortfolio() throws Exception {
		EditablePortfolio port = control.createMock(EditablePortfolio.class);
		terminal.setDefaultPortfolio(same(port));
		control.replay();
		decorator.setDefaultPortfolio(port);
		control.verify();
	}
	
	@Test
	public void testGetEditableSecurity() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityDescriptor descr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(security);
		control.replay();
		assertSame(security, decorator.getEditableSecurity(descr));
		control.verify();
	}
	
	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		Security security = control.createMock(Security.class);
		terminal.fireSecurityAvailableEvent(same(security));
		control.replay();
		decorator.fireSecurityAvailableEvent(security);
		control.verify();
	}
	
	@Test
	public void testOnTerminalConnected() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnConnected()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnConnected());
		control.verify();
	}
	
	@Test
	public void testOnTerminalDisconnected() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnDisconnected()).andReturn(type);
		control.replay();
		assertSame(type, decorator.OnDisconnected());
		control.verify();
	}
	
	@Test
	public void testFireTerminalConnectedEvent() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		decorator.fireTerminalConnectedEvent();
		control.verify();
	}
	
	@Test
	public void testFireTerminalDisconnectedEvent() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		control.replay();
		decorator.fireTerminalDisconnectedEvent();
		control.verify();
	}
	
	@Test
	public void testMakePendingOrderAsRegisteredIfExists() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.makePendingOrderAsRegisteredIfExists(127l, 19l))
			.andReturn(order);
		control.replay();
		assertSame(order,
				decorator.makePendingOrderAsRegisteredIfExists(127l, 19l));
		control.verify();
	}
	
	@Test
	public void testMakePendingStopOrderAsRegisteredIfExists()
			throws Exception
	{
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(terminal.makePendingStopOrderAsRegisteredIfExists(12l, 82l))
			.andReturn(order);
		control.replay();
		assertSame(order,
				decorator.makePendingStopOrderAsRegisteredIfExists(12l, 82l));
		control.verify();
	}
	
	@Test
	public void testOnStarted() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStarted()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnStarted());
		
		control.verify();
	}

	@Test
	public void testOnStopped() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopped()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnStopped());
		
		control.verify();
	}
	
	@Test
	public void testOnPanic() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnPanic()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnPanic());
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalPanicEvent2() throws Exception {
		terminal.firePanicEvent(eq(2), eq("ABC"));
		control.replay();
		
		decorator.firePanicEvent(2, "ABC");
		
		control.verify();
	}

	@Test
	public void testFireTerminalPanicEvent3() throws Exception {
		Object[] args = { 1, 2, 3 };
		terminal.firePanicEvent(eq(2), eq("ABC"), eq(args));
		control.replay();
		
		decorator.firePanicEvent(2, "ABC", args);
		
		control.verify();
	}
	
	@Test
	public void testGetOrderProcessorInstance() throws Exception {
		OrderProcessor processor = control.createMock(OrderProcessor.class);
		expect(terminal.getOrderProcessorInstance()).andReturn(processor);
		control.replay();
		
		assertSame(processor, decorator.getOrderProcessorInstance());
		
		control.verify();
	}
	
	@Test
	public void testGetOrderBuilderInstance() throws Exception {
		OrderBuilder builder = control.createMock(OrderBuilder.class);
		expect(terminal.getOrderBuilderInstance()).andReturn(builder);
		control.replay();
		
		assertSame(builder, decorator.getOrderBuilderInstance());
		
		control.verify();
	}
	
	@Test
	public void testGetSecuritiesInstance() throws Exception {
		EditableSecurities secs = control.createMock(EditableSecurities.class);
		expect(terminal.getSecuritiesInstance()).andReturn(secs);
		control.replay();
		
		assertSame(secs, decorator.getSecuritiesInstance());
		
		control.verify();
	}

	@Test
	public void testGetPortfoliosInstance() throws Exception {
		EditablePortfolios ports = control.createMock(EditablePortfolios.class);
		expect(terminal.getPortfoliosInstance()).andReturn(ports);
		control.replay();
		
		assertSame(ports, decorator.getPortfoliosInstance());
		
		control.verify();
	}

	@Test
	public void testGetOrdersInstance() throws Exception {
		EditableOrders ords = control.createMock(EditableOrders.class);
		expect(terminal.getOrdersInstance()).andReturn(ords);
		control.replay();
		
		assertSame(ords, decorator.getOrdersInstance());
		
		control.verify();
	}

	@Test
	public void testGetStopOrdersInstance() throws Exception {
		EditableOrders ords = control.createMock(EditableOrders.class);
		expect(terminal.getStopOrdersInstance()).andReturn(ords);
		control.replay();
		
		assertSame(ords, decorator.getStopOrdersInstance());
		
		control.verify();
	}
	
	@Test
	public void testStopped() throws Exception {
		expect(terminal.stopped()).andReturn(true);
		expect(terminal.stopped()).andReturn(false);
		control.replay();
		
		assertTrue(decorator.stopped());
		assertFalse(decorator.stopped());
		
		control.verify();
	}
	
	@Test
	public void testConnected() throws Exception {
		expect(terminal.connected()).andReturn(true);
		expect(terminal.connected()).andReturn(false);
		control.replay();
		
		assertTrue(decorator.connected());
		assertFalse(decorator.connected());
		
		control.verify();
	}
	
	@Test
	public void testGetTerminalState() throws Exception {
		expect(terminal.getTerminalState()).andReturn(TerminalState.CONNECTED);
		control.replay();
		
		assertEquals(TerminalState.CONNECTED, decorator.getTerminalState());
		
		control.verify();
	}
	
	@Test
	public void testGetStarter() throws Exception {
		Starter starter = control.createMock(Starter.class);
		expect(terminal.getStarter()).andReturn(starter);
		control.replay();
		
		assertSame(starter, decorator.getStarter());
		
		control.verify();
	}

	@Test
	public void testFireTerminalStartedEvent() throws Exception {
		terminal.fireTerminalStartedEvent();
		control.replay();
		
		decorator.fireTerminalStartedEvent();
		
		control.verify();
	}
	
	@Test
	public void testFireTerminalStoppedEvent() throws Exception {
		terminal.fireTerminalStoppedEvent();
		control.replay();
		
		decorator.fireTerminalStoppedEvent();
		
		control.verify();
	}
	
	@Test
	public void testSetTerminalStatus() throws Exception {
		terminal.setTerminalState(TerminalState.STOPPING);
		control.replay();
		
		decorator.setTerminalState(TerminalState.STOPPING);
		
		control.verify();
	}
	
	@Test
	public void testOnStopOrderFilled() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(terminal.OnStopOrderFilled()).andReturn(type);
		control.replay();
		
		assertSame(type, decorator.OnStopOrderFilled());
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderB() throws Exception {
		Account account = new Account("FOO", "BAR");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(terminal.createLimitOrderB(account, sec, 10, 125.86d))
			.andReturn(order);
		control.replay();
		
		assertSame(order, decorator.createLimitOrderB(account,sec,10,125.86d));
		
		control.verify();
	}
	
	@Test
	public void testCreateLimitOrderS() throws Exception {
		Account account = new Account("FOO", "BAR");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(terminal.createLimitOrderS(account, sec, 10, 125.86d))
			.andReturn(order);
		control.replay();
		
		assertSame(order, decorator.createLimitOrderS(account,sec,10,125.86d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitB() throws Exception {
		Account account = new Account("FOO", "BAR");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(terminal.createStopLimitB(account, sec, 10, 125.86d, 128.0d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,
				decorator.createStopLimitB(account, sec, 10, 125.86d, 128.0d));
		
		control.verify();
	}
	
	@Test
	public void testCreateStopLimitS() throws Exception {
		Account account = new Account("FOO", "BAR");
		Security sec = control.createMock(Security.class);
		Order order = control.createMock(Order.class);
		expect(terminal.createStopLimitS(account, sec, 10, 129.86d, 128.0d))
			.andReturn(order);
		control.replay();
		
		assertSame(order,
				decorator.createStopLimitS(account, sec, 10, 129.86d, 128.0d));
		
		control.verify();
	}
	
	@Test
	public void testGetCurrentTime() throws Exception {
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		control.replay();
		
		assertSame(time, decorator.getCurrentTime());
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurity1() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		SecurityDescriptor descr
			= new SecurityDescriptor("FOO", "BAR", "SUR", SecurityType.STK);
		expect(terminal.createSecurity(same(decorator), same(descr)))
			.andReturn(security);
		control.replay();
		
		assertSame(security, decorator.createSecurity(descr));
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurity2() throws Exception {
		EditableSecurity security = control.createMock(EditableSecurity.class);
		EditableTerminal terminal = control.createMock(EditableTerminal.class);
		SecurityDescriptor descr
			= new SecurityDescriptor("FOO", "BAR", "SUR", SecurityType.STK);
		expect(terminal.createSecurity(same(terminal), same(descr)))
			.andReturn(security);
		control.replay();
		
		assertSame(security, decorator.createSecurity(terminal, descr));
		
		control.verify();
	}
	
	@Test
	public void testSetOrderProcessor() throws Exception {
		OrderProcessor processor = control.createMock(OrderProcessor.class);
		terminal.setOrderProcessorInstance(same(processor));
		control.replay();
		
		decorator.setOrderProcessorInstance(processor);
		
		control.verify();
	}
	
}
