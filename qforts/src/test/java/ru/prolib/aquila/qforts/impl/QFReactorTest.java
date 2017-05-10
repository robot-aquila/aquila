package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static  org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFReactorTest {
	private static final Symbol symbol = new Symbol("MSFT");
	private IMocksControl control;
	private QForts facadeMock;
	private QFObjectRegistry registryMock;
	private QFSessionSchedule scheduleMock;
	private AtomicLong seqOrderID;
	private EditableTerminal terminal;
	private EditableSecurity security;
	private SchedulerStub schedulerStub;
	private QFReactor reactor;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facadeMock = control.createMock(QForts.class);
		registryMock = control.createMock(QFObjectRegistry.class);
		scheduleMock = control.createMock(QFSessionSchedule.class);
		seqOrderID = new AtomicLong(1000L);
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerStub)
			.buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		security.update(SecurityField.TICK_SIZE, FDecimal.of2(0.01));
		reactor = new QFReactor(facadeMock, registryMock, scheduleMock, seqOrderID);
	}
	
	@Test
	public void testRun_SkipIfNotInitialized() {
		control.replay();
		
		reactor.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_UpdateByMarket() throws Exception {
		reactor.setTerminal(terminal);
		schedulerStub.setFixedTime("2017-04-25T09:48:00Z");
		expect(scheduleMock.getCurrentProc(T("2017-04-25T09:48:00Z")))
			.andReturn(QFSessionProc.UPDATE_BY_MARKET);
		facadeMock.updateByMarket();
		control.replay();
		
		reactor.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_MidClearing() throws Exception {
		reactor.setTerminal(terminal);
		schedulerStub.setFixedTime("2017-04-25T09:53:00Z");
		expect(scheduleMock.getCurrentProc(T("2017-04-25T09:53:00Z")))
			.andReturn(QFSessionProc.MID_CLEARING);
		facadeMock.midClearing();
		control.replay();
		
		reactor.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_Clearing() throws Exception {
		reactor.setTerminal(terminal);
		schedulerStub.setFixedTime("2017-04-25T09:56:00Z");
		expect(scheduleMock.getCurrentProc(T("2017-04-25T09:56:00Z")))
			.andReturn(QFSessionProc.CLEARING);
		facadeMock.clearing();
		control.replay();
		
		reactor.run();
		
		control.verify();
	}
	
	@Test
	public void testGetNextExecutionTime() {
		expect(scheduleMock.getNextRunTime(T("2017-04-25T09:57:00Z")))
			.andReturn(T("2017-04-25T10:00:00Z"));
		control.replay();
		
		assertEquals(T("2017-04-25T10:00:00Z"), reactor.getNextExecutionTime(T("2017-04-25T09:57:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testIsLongTermTask() {
		assertFalse(reactor.isLongTermTask());
	}
	
	@Test
	public void testSubscribeStateUpdates_Security() {
		facadeMock.registerSecurity(security);
		control.replay();
		
		reactor.subscribeStateUpdates(security);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel1Data() {
		control.replay();
		
		reactor.subscribeLevel1Data(symbol, security);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel2Data() {
		control.replay();
		
		reactor.subscribeLevel2Data(symbol, security);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeStateUpdates_Portfolio() {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(new Account("TEST"));
		facadeMock.registerPortfolio(portfolio);
		control.replay();
		
		reactor.subscribeStateUpdates(portfolio);
		
		control.verify();
	}
	
	@Test
	public void testGetNextOrderID() {
		seqOrderID.set(2500L);
		control.replay();
		
		assertEquals(2501L, reactor.getNextOrderID());
		
		control.verify();
		assertEquals(2501L, seqOrderID.get());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSubscribeRemoteObjects_ThrowsIfStarted() {
		reactor.setTerminal(terminal);
		control.replay();
		
		reactor.subscribeRemoteObjects(terminal);
	}
	
	@Test
	public void testSubscribeRemoteObjects() {
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		TaskHandler taskHandlerMock = control.createMock(TaskHandler.class);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerMock)
			.buildTerminal();
		expect(schedulerMock.schedule(reactor)).andReturn(taskHandlerMock);
		control.replay();
		
		reactor.subscribeRemoteObjects(terminal);
		
		control.verify();
		assertTrue(terminal.onSecurityLastTrade().isListener(reactor));
		assertTrue(terminal.onSecurityUpdate().isListener(reactor));
		assertSame(terminal, reactor.getTerminal());
		assertSame(taskHandlerMock, reactor.getTaskHandler());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testUnsubscribeRemoteObjects_ThrowsIfNotStarted() throws Exception {
		control.replay();
		
		reactor.unsubscribeRemoteObjects(terminal);
	}

	@Test
	public void testUnsubscribeRemoteObjects() {
		TaskHandler taskHandlerMock = control.createMock(TaskHandler.class);
		reactor.setTerminal(terminal);
		reactor.setTaskHandler(taskHandlerMock);
		terminal.onSecurityLastTrade().addListener(reactor);
		terminal.onSecurityUpdate().addListener(reactor);
		expect(taskHandlerMock.cancel()).andReturn(true);
		control.replay();
		
		reactor.unsubscribeRemoteObjects(terminal);
		
		control.verify();
		assertFalse(terminal.onSecurityLastTrade().isListener(reactor));
		assertFalse(terminal.onSecurityUpdate().isListener(reactor));
		assertNull(reactor.getTerminal());
		assertNull(reactor.getTaskHandler());
	}
	
	@Test
	public void testRegisterNewOrder() throws Exception {
		EditableOrder orderMock = control.createMock(EditableOrder.class);
		facadeMock.registerOrder(orderMock);
		control.replay();
		
		reactor.registerNewOrder(orderMock);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder orderMock = control.createMock(EditableOrder.class);
		facadeMock.cancelOrder(orderMock);
		control.replay();
		
		reactor.cancelOrder(orderMock);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfIsNotSecurityEvent() throws Exception {
		Event eMock = control.createMock(Event.class);
		control.replay();
		
		reactor.onEvent(eMock);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfNotRegistered() throws Exception {
		expect(registryMock.isRegistered(security)).andStubReturn(false);
		control.replay();
		
		reactor.onEvent(new SecurityEvent(terminal.onSecurityUpdate(), security));
		reactor.onEvent(new SecurityTickEvent(terminal.onSecurityLastTrade(), security, Tick.NULL_ASK));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SecurityLastTrade() throws Exception {
		expect(registryMock.isRegistered(security)).andReturn(true);
		facadeMock.handleOrders(security, 2500L, FDecimal.of2(54.02));
		control.replay();
		
		reactor.onEvent(new SecurityTickEvent(terminal.onSecurityLastTrade(), security,
				Tick.ofTrade(T("2017-04-25T11:25:00Z"), 54.02d, 2500L)));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_InitialMarginUpdate_SkipIfNotUpdated() throws Exception {
		expect(registryMock.isRegistered(security)).andReturn(true);
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security);
		Set<Integer> updatedTokens = new HashSet<>();
		updatedTokens.add(SecurityField.SETTLEMENT_PRICE);
		control.replay();
		
		reactor.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_InitialMarginUpdate_AllowedPeriods() throws Exception {
		testOnEvent_InitialMarginUpdate_AllowedPeriod(QFSessionSchedule.PCVM1_1);
		testOnEvent_InitialMarginUpdate_AllowedPeriod(QFSessionSchedule.PCVM1_2);
		testOnEvent_InitialMarginUpdate_AllowedPeriod(QFSessionSchedule.PCVM2);
	}
	
	private void testOnEvent_InitialMarginUpdate_AllowedPeriod(int periodID) throws Exception {
		setUp();
		expect(registryMock.isRegistered(security)).andReturn(true);
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security);
		Set<Integer> updatedTokens = new HashSet<>();
		updatedTokens.add(SecurityField.INITIAL_MARGIN);
		e.setUpdatedTokens(updatedTokens);
		expect(scheduleMock.getCurrentPeriod(anyObject())).andReturn(periodID);
		facadeMock.updateMargin(security);
		control.replay();
		
		reactor.onEvent(e);
		
		control.verify();
	}

	@Test
	public void testOnEvent_InitialMarginUpdate_DisallowedPeriods() throws Exception {
		int restricted[] = {
				QFSessionSchedule.PIC,
				QFSessionSchedule.PMC,
				QFSessionSchedule.VP1,
				QFSessionSchedule.VP2,
				QFSessionSchedule.VP3,
				QFSessionSchedule.VPZ
		};
		for ( int dummy : restricted ) {
			testOnEvent_InitialMarginUpdate_DisallowedPeriod(dummy);
		}
	}
	
	private void testOnEvent_InitialMarginUpdate_DisallowedPeriod(int periodID) throws Exception {
		setUp();
		expect(registryMock.isRegistered(security)).andReturn(true);
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security);
		Set<Integer> updatedTokens = new HashSet<>();
		updatedTokens.add(SecurityField.INITIAL_MARGIN);
		e.setUpdatedTokens(updatedTokens);
		expect(scheduleMock.getCurrentPeriod(anyObject())).andReturn(periodID);
		control.replay();
		
		reactor.onEvent(e);
		
		control.verify();
	}

}