package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static  org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.BasicConfigurator;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SchedulerStub;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFReactorTest {
	private static final Symbol symbol = new Symbol("MSFT");
	
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private QForts facadeMock;
	private QFObjectRegistry registryMock;
	private QFSessionSchedule scheduleMock;
	private QFSymbolDataService sdsMock;
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
	
	QFReactor createWithEventBasedL1Source() {
		return new QFReactor(
				facadeMock,
				registryMock,
				scheduleMock,
				seqOrderID,
				sdsMock,
				QFOrderExecutionTriggerMode.USE_LAST_TRADE_EVENT_OF_SECURITY
			);
	}
	
	QFReactor createAsL1UpdateConsumer() {
		return new QFReactor(
				facadeMock,
				registryMock,
				scheduleMock,
				seqOrderID,
				sdsMock,
				QFOrderExecutionTriggerMode.USE_L1UPDATES_WHEN_ORDER_APPEARS
			);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facadeMock = control.createMock(QForts.class);
		registryMock = control.createMock(QFObjectRegistryImpl.class);
		scheduleMock = control.createMock(QFSessionSchedule.class);
		sdsMock = control.createMock(QFSymbolDataService.class);
		seqOrderID = new AtomicLong(1000L);
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerStub)
			.buildTerminal();
		security = terminal.getEditableSecurity(symbol);
		security.update(SecurityField.TICK_SIZE, CDecimalBD.of("0.01"));
		reactor = createWithEventBasedL1Source();
	}
	
	@Test
	public void testCtor5() {
		reactor = new QFReactor(facadeMock, registryMock, scheduleMock, seqOrderID, sdsMock);
		assertEquals(QFOrderExecutionTriggerMode.USE_LAST_TRADE_EVENT_OF_SECURITY, reactor.getOrderExecutionTriggerMode());
	}
	
	@Test
	public void testCtor6() {
		reactor = new QFReactor(facadeMock, registryMock, scheduleMock, seqOrderID, sdsMock,
				QFOrderExecutionTriggerMode.USE_L1UPDATES_WHEN_ORDER_APPEARS);
		assertEquals(QFOrderExecutionTriggerMode.USE_L1UPDATES_WHEN_ORDER_APPEARS, reactor.getOrderExecutionTriggerMode());
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
	public void testSubscribe_Symbol() throws Exception {
		SubscrHandler handlerMock = control.createMock(SubscrHandler.class);
		sdsMock.setTerminal(terminal);
		expect(sdsMock.onSubscribe(symbol, MDLevel.L1_BBO)).andReturn(handlerMock);
		control.replay();
		
		assertSame(handlerMock, reactor.subscribe(symbol, MDLevel.L1_BBO, terminal));
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Account_IfExists() {
		Account account = new Account("FOO-237");
		terminal.getEditablePortfolio(account);
		control.replay();
		
		reactor.subscribe(account, terminal);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Account_IfNotExists() throws Exception {
		Account account = new Account("FOO-237");
		Capture<EditablePortfolio> cap1 = Capture.newInstance();
		Capture<EditablePortfolio> cap2 = Capture.newInstance();
		facadeMock.registerPortfolio(capture(cap1));
		facadeMock.changeBalance(capture(cap2), eq(CDecimalBD.ofRUB2("1000000.00")));
		control.replay();
		
		reactor.subscribe(account, terminal);
		
		control.verify();
		assertTrue(terminal.isPortfolioExists(account));
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		assertSame(portfolio, cap1.getValue());
		assertSame(portfolio, cap2.getValue());
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
	public void testSubscribeRemoteObjects_EventBasedL1SourceMode() {
		reactor = createWithEventBasedL1Source();
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		TaskHandler taskHandlerMock = control.createMock(TaskHandler.class);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerMock)
			.buildTerminal();
		sdsMock.setTerminal(terminal);
		sdsMock.onConnectionStatusChange(true);
		expect(schedulerMock.schedule(reactor)).andReturn(taskHandlerMock);
		control.replay();
		
		reactor.subscribeRemoteObjects(terminal);
		
		control.verify();
		assertTrue(terminal.onSecurityLastTrade().isListener(reactor));
		assertTrue(terminal.onSecurityUpdate().isListener(reactor));
		assertSame(terminal, reactor.getTerminal());
		assertSame(taskHandlerMock, reactor.getTaskHandler());
	}
	
	@Test
	public void testSubscribeRemoteObjects_L1UpdateConsumerMode() {
		reactor = createAsL1UpdateConsumer();
		Scheduler schedulerMock = control.createMock(Scheduler.class);
		TaskHandler taskHandlerMock = control.createMock(TaskHandler.class);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.withScheduler(schedulerMock)
			.buildTerminal();
		sdsMock.setTerminal(terminal);
		sdsMock.onConnectionStatusChange(true);
		expect(schedulerMock.schedule(reactor)).andReturn(taskHandlerMock);
		control.replay();
		
		reactor.subscribeRemoteObjects(terminal);
		
		control.verify();
		assertFalse(terminal.onSecurityLastTrade().isListener(reactor));
		assertTrue(terminal.onSecurityUpdate().isListener(reactor));
		assertSame(terminal, reactor.getTerminal());
		assertSame(taskHandlerMock, reactor.getTaskHandler());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testUnsubscribeRemoteObjects_ThrowsIfNotStarted() throws Exception {
		control.replay();
		
		reactor.unsubscribeRemoteObjects(terminal);
	}

	private void testUnsubscribeRemoteObjects() {
		TaskHandler taskHandlerMock = control.createMock(TaskHandler.class);
		reactor.setTerminal(terminal);
		reactor.setTaskHandler(taskHandlerMock);
		terminal.onSecurityLastTrade().addListener(reactor);
		terminal.onSecurityUpdate().addListener(reactor);
		sdsMock.onConnectionStatusChange(false);
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
	public void testUnsubscribeRemoteObjects_EventBasedL1SourceMode() {
		testUnsubscribeRemoteObjects();
	}
	
	@Test
	public void testUnsubscribeRemoteObjects_L1UpdateConsumerMode() {
		testUnsubscribeRemoteObjects();
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
	public void testOnEvent_SecurityLastTrade() throws Exception {
		facadeMock.handleOrders(security, of(2500L), of("54.02"), "CHAP01");
		control.replay();
		
		reactor.onEvent(new SecurityTickEvent(terminal.onSecurityLastTrade(), security,
				null, new Tick(TickType.TRADE, T("2017-04-25T11:25:00Z"), of("54.02"), of(2500L), ZERO, "CHAP01")));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SecurityLastTrade_ThrowsIfNotAnEventBasedL1UpdateSourceSelected() {
		reactor = createAsL1UpdateConsumer();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Unexpected event in a not event-based L1 source mode");
		
		reactor.onEvent(new SecurityTickEvent(terminal.onSecurityLastTrade(), security, null, null));
	}
	
	@Test
	public void testOnEvent_InitialMarginUpdate_SkipIfNotUpdated() throws Exception {
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security, null);
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
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security, null);
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
		SecurityEvent e = new SecurityEvent(terminal.onSecurityUpdate(), security, null);
		Set<Integer> updatedTokens = new HashSet<>();
		updatedTokens.add(SecurityField.INITIAL_MARGIN);
		e.setUpdatedTokens(updatedTokens);
		expect(scheduleMock.getCurrentPeriod(anyObject())).andReturn(periodID);
		control.replay();
		
		reactor.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testClose() {
		control.replay();
		
		reactor.close();
		
		control.verify();
	}

	@Test
	public void testConsume_L1Update_SkipIfTerminalNotDefined() {
		reactor = createAsL1UpdateConsumer();
		control.replay();
		reactor.setTerminal(null);
		
		reactor.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2020-03-02T18:00:00Z")
				.withPrice(120950L)
				.withSize(100L)
				.buildL1Update());
		
		control.verify();
	}
	
	@Test
	public void testConsume_L1Update_ThrowsIfSecurityNotExists() {
		reactor = createAsL1UpdateConsumer();
		control.replay();
		reactor.setTerminal(terminal);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Expected security not exists: BAZOOKA-251-AT-MINE");
		
		reactor.consume(new L1UpdateBuilder(new Symbol("BAZOOKA-251-AT-MINE"))
				.withTrade()
				.withTime("2020-03-02T18:02:00Z")
				.withPrice(130930L)
				.withSize(95L)
				.buildL1Update());
	}
	
	@Test
	public void testConsume_L1Update_SecurityExceptionThrownByTerminal() throws Exception {
		reactor = createAsL1UpdateConsumer();
		EditableTerminal terminalMock = control.createMock(EditableTerminal.class);
		expect(terminalMock.isSecurityExists(symbol)).andReturn(true);
		expect(terminalMock.getSecurity(symbol)).andThrow(new SecurityException("Test error"));
		control.replay();
		reactor.setTerminal(terminalMock);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Unexpected exception: ");
		
		reactor.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2020-03-02T18:16:00Z")
				.withPrice(296100L)
				.withSize(25L)
				.buildL1Update());
	}
	
	@Test
	public void testConsume_L1Update_TransactionExceptionThrownByFacade() throws Exception {
		reactor = createAsL1UpdateConsumer();
		facadeMock.handleOrders(security, of(20L), of(55302L), "ZAZ15");
		expectLastCall().andThrow(new QFTransactionException("Test error"));
		control.replay();
		reactor.setTerminal(terminal);
		
		reactor.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2020-03-02T18:39:00Z")
				.withPrice(55302L)
				.withSize(20L)
				.withComment("ZAZ15")
				.buildL1Update());

		control.verify();
	}

	@Test
	public void testConsume_L1Update_OK() throws Exception {
		reactor = createAsL1UpdateConsumer();
		facadeMock.handleOrders(security, of(35L), of("504.12"), "BOOZ5");
		control.replay();
		reactor.setTerminal(terminal);
		
		reactor.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2020-03-02T18:50:00Z")
				.withPrice("504.12")
				.withSize(35L)
				.withComment("BOOZ5")
				.buildL1Update());

		control.verify();
	}
	
	@Test
	public void testConsume_L1Update_ThrowsIfEventBasedL1SourceEnabled() {
		reactor = createWithEventBasedL1Source();
		control.replay();
		reactor.setTerminal(terminal);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Unexpected update in a non-consumer L1 source mode");
		
		reactor.consume(new L1UpdateBuilder(symbol)
				.withTrade()
				.withTime("2020-03-02T18:50:00Z")
				.withPrice("504.12")
				.withSize(35L)
				.buildL1Update());

		control.verify();
	}

}
