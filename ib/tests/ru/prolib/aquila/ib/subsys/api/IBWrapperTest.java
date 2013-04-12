package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.easymock.IMocksControl;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEvent;
import ru.prolib.aquila.ib.event.IBEventAccounts;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.event.IBEventRequest;
import ru.prolib.aquila.ib.event.IBEventTick;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

import com.ib.client.*;

/**
 * 2012-11-14<br>
 * $Id: IBWrapperTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBWrapperTest {
	private static IMocksControl control;
	private static Logger logger;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	private IBWrapper wrapper;
	private EventDispatcher dispatcher;
	private EventType onConnectionClosed, onError, onNextValidId,
		onContractDetails, onManagedAccounts, onUpdateAccount,
		onUpdatePortfolio, onOpenOrder, onOrderStatus, onTick;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getLogger(EventQueueImpl.class)
			.setLevel(Level.ERROR);
		control = createStrictControl();
		logger = control.createMock(Logger.class);
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		IBWrapper.logger = LoggerFactory.getLogger(IBWrapper.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		dispatcher = eventSystem.createEventDispatcher();
		onConnectionClosed = eventSystem.createGenericType(dispatcher);
		onError = eventSystem.createGenericType(dispatcher);
		onNextValidId = eventSystem.createGenericType(dispatcher);
		onContractDetails = eventSystem.createGenericType(dispatcher);
		onManagedAccounts = eventSystem.createGenericType(dispatcher);
		onUpdateAccount = eventSystem.createGenericType(dispatcher);
		onUpdatePortfolio = eventSystem.createGenericType(dispatcher);
		onOpenOrder = eventSystem.createGenericType(dispatcher);
		onOrderStatus = eventSystem.createGenericType(dispatcher);
		onTick = eventSystem.createGenericType(dispatcher);
		wrapper = new IBWrapper(dispatcher, onConnectionClosed, onError,
				onNextValidId, onContractDetails, onManagedAccounts,
				onUpdateAccount, onUpdatePortfolio, onOpenOrder, onOrderStatus,
				onTick);
		IBWrapper.logger = logger;
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		queue.join(1000);
	}
	
	/**
	 * Создать обозреватель для теста события.
	 * <p>
	 * @param type тип события
	 * @param event ожидаемое событие
	 * @return счетчик-индикатор завершения теста
	 */
	private CountDownLatch checkEvent(EventType type, Event event) {
		final CountDownLatch finished = new CountDownLatch(1);
		final Event expected = event;
		type.addListener(new EventListener() {
			@Override
			public void onEvent(Event actual) {
				assertEquals(expected, actual);
				finished.countDown();
			}
		});
		return finished;
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(dispatcher, wrapper.getEventDispatcher());
		assertSame(onConnectionClosed, wrapper.OnConnectionClosed());
		assertSame(onError, wrapper.OnError());
		assertSame(onNextValidId, wrapper.OnNextValidId());
		assertSame(onContractDetails, wrapper.OnContractDetails());
		assertSame(onManagedAccounts, wrapper.OnManagedAccounts());
		assertSame(onUpdateAccount, wrapper.OnUpdateAccount());
		assertSame(onUpdatePortfolio, wrapper.OnUpdatePortfolio());
		assertSame(onOpenOrder, wrapper.OnOpenOrder());
		assertSame(onOrderStatus, wrapper.OnOrderStatus());
		assertSame(onTick, wrapper.OnTick());
	}
	
	@Test
	public void testConnectionClosed() throws Exception {
		CountDownLatch finished = checkEvent(wrapper.OnConnectionClosed(),
				new IBEvent(wrapper.OnConnectionClosed()));
		control.replay();
		wrapper.connectionClosed();
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testError_1Ex() throws Exception {
		Exception e = new Exception("test");
		logger.error(eq("unexpected call error(Exception)"), same(e));
		control.replay();
		//IBEventsImpl.logger = LoggerFactory.getLogger(IBEventsImpl.class);
		wrapper.error(e);
		control.verify();
	}
	
	@Test
	public void testError_1Str() throws Exception {
		logger.error(eq("unexpected call error(String): {}"), eq("test"));
		control.replay();
		//IBEventsImpl.logger = LoggerFactory.getLogger(IBEventsImpl.class);
		wrapper.error("test");
		control.verify();
	}
	
	@Test
	public void testError_3_NoDebug() throws Exception {
		CountDownLatch finished = checkEvent(onError,
				new IBEventError(onError, 5, 200, "test"));
		expect(logger.isDebugEnabled()).andReturn(false);
		control.replay();
		wrapper.error(5, 200, "test");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testError_3_Debug() throws Exception {
		CountDownLatch finished = checkEvent(onError,
				new IBEventError(onError, 5, 200, "test"));
		expect(logger.isDebugEnabled()).andReturn(true);
		Object a[] = { 5, 200, "test" };
		logger.debug(eq("error: reqid={}, code={}, msg={}"), aryEq(a));
		control.replay();
		wrapper.error(5, 200, "test");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	
	@Test
	public void testAccountDownloadEnd() throws Exception {
		//logger.debug("accountDownloadEnd not implemented");
		control.replay();
		wrapper.accountDownloadEnd("test");
		control.verify();
	}
	
	@Test
	public void testBondContractDetails_Ok() throws Exception {
		ContractDetails details = new ContractDetails();
		details.m_longName = "Unknown Company";
		CountDownLatch finished = checkEvent(onContractDetails,
				new IBEventContract(onContractDetails, 123,
						IBEventContract.SUBTYPE_BOND, details));
		control.replay();
		wrapper.bondContractDetails(123, details);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testContractDetails_Ok() throws Exception {
		ContractDetails details = new ContractDetails();
		details.m_longName = "Test Inc";
		CountDownLatch finished = checkEvent(onContractDetails,
				new IBEventContract(onContractDetails, 789,
						IBEventContract.SUBTYPE_NORM, details));
		control.replay();
		wrapper.contractDetails(789, details);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testContractDetailsEnd_Ok() throws Exception {
		CountDownLatch finished = checkEvent(onContractDetails,
				new IBEventContract(onContractDetails, 150,
						IBEventContract.SUBTYPE_END, null));
		control.replay();
		wrapper.contractDetailsEnd(150);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testCommissionReport() throws Exception {
		//logger.debug("commissionReport not implemented");
		control.replay();
		wrapper.commissionReport(new CommissionReport());
		control.verify();
	}
	
	@Test
	public void testCurrentTime() throws Exception {
		//logger.debug("currentTime not implemented");
		control.replay();
		wrapper.currentTime(100500L);
		control.verify();
	}
	
	@Test
	public void testDeltaNeutralValidation() throws Exception {
		//logger.debug("deltaNeutralValidation not implemented");
		control.replay();
		wrapper.deltaNeutralValidation(100, new UnderComp());
		control.verify();
	}

	@Test
	public void testExecDetails() throws Exception {
		//logger.debug("execDetails not implemented");
		control.replay();
		wrapper.execDetails(100, new Contract(), new Execution());
		control.verify();
	}
	
	@Test
	public void testExecDetailsEnd() throws Exception {
		//logger.debug("execDetailsEnd not implemented");
		control.replay();
		wrapper.execDetailsEnd(100);
		control.verify();
	}
	
	@Test
	public void testFundamentalData() throws Exception {
		//logger.debug("fundamentalData not implemented");
		control.replay();
		wrapper.fundamentalData(100, "test");
		control.verify();
	}
	
	@Test
	public void testHistoricalData() throws Exception {
		//logger.debug("historicalData not implemented");
		control.replay();
		wrapper.historicalData(100, "test", 1, 2, 1, 1, 20, 1, 5, false);
		control.verify();
	}
	
	@Test
	public void testManagedAccounts() throws Exception {
		CountDownLatch finished = checkEvent(onManagedAccounts,
				new IBEventAccounts(onManagedAccounts, "AC1,AC2"));
		control.replay();
		wrapper.managedAccounts("AC1,AC2");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testMarketDataType() throws Exception {
		//logger.debug("marketDataType not implemented");
		control.replay();
		wrapper.marketDataType(100, 500);
		control.verify();
	}
	
	@Test
	public void testNextValidId() throws Exception {
		CountDownLatch finished = checkEvent(wrapper.OnNextValidId(),
				new IBEventRequest(wrapper.OnNextValidId(), 100500));
		control.replay();
		wrapper.nextValidId(100500);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOpenOrder() throws Exception {
		Contract contract = new Contract(); contract.m_symbol = "AAPL";
		Order order = new Order(); order.m_clientId = 0; order.m_permId = 1;
		OrderState state = control.createMock(OrderState.class);
		CountDownLatch finished = checkEvent(wrapper.OnOpenOrder(),
				new IBEventOpenOrder(onOpenOrder, 200, contract, order, state));
		control.replay();
		wrapper.openOrder(200, contract, order, state);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOpenOrderEnd() throws Exception {
		//logger.debug("openOrderEnd not implemented");
		control.replay();
		wrapper.openOrderEnd();
		control.verify();
	}

	@Test
	public void testOrderStatus() throws Exception {
		CountDownLatch finished = checkEvent(wrapper.OnOrderStatus(),
				new IBEventOrderStatus(onOrderStatus, 500, "Pending",
						2, 8, 12.54d, 987, 86, 12.53d, 1, "x"));
		control.replay();
		wrapper.orderStatus(500,"Pending",2, 8, 12.54d, 987, 86, 12.53d, 1,"x");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testRealtimeBar() throws Exception {
		//logger.debug("realtimeBar not implemented");
		control.replay();
		wrapper.realtimeBar(1, 2, 3, 4, 5, 6, 7, 8, 9);
		control.verify();
	}
	
	@Test
	public void testReceiveFA() throws Exception {
		//logger.debug("receiveFA not implemented");
		control.replay();
		wrapper.receiveFA(1, "xml");
		control.verify();
	}
	
	@Test
	public void testScannerData() throws Exception {
		//logger.debug("scannerData not implemented");
		control.replay();
		wrapper.scannerData(1, 2, new ContractDetails(), "a", "b", "c", "d");
		control.verify();
	}
	
	@Test
	public void testScannerDataEnd() throws Exception {
		//logger.debug("scannerDataEnd not implemented");
		control.replay();
		wrapper.scannerDataEnd(1);
		control.verify();
	}
	
	@Test
	public void testScannerParameters() throws Exception {
		//logger.debug("scannerParameters not implemented");
		control.replay();
		wrapper.scannerParameters("xml");
		control.verify();
	}
	
	@Test
	public void testTickEFP() throws Exception {
		//logger.debug("tickEFP not implemented");
		control.replay();
		wrapper.tickEFP(1, 2, 3, "4", 5, 6, "7", 8, 9);
		control.verify();
	}
	
	@Test
	public void testTickGeneric() throws Exception {
		CountDownLatch finished = checkEvent(onTick,
				new IBEventTick(onTick, 200, TickType.AVG_VOLUME, 12.34d));
		control.replay();
		wrapper.tickGeneric(200, TickType.AVG_VOLUME, 12.34d);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testTickOptionComputation() throws Exception {
		//logger.debug("tickOptionComputation not implemented");
		control.replay();
		wrapper.tickOptionComputation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		control.verify();
	}
	
	@Test
	public void testTickPrice() throws Exception {
		CountDownLatch finished = checkEvent(onTick,
				new IBEventTick(onTick, 100, TickType.ASK, 100.25d));
		control.replay();
		wrapper.tickPrice(100, TickType.ASK, 100.25d, 0);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testTickSize() throws Exception {
		CountDownLatch finished = checkEvent(onTick,
				new IBEventTick(onTick, 150, TickType.ASK_SIZE, 1000.0d));
		control.replay();
		wrapper.tickSize(150, TickType.ASK_SIZE, 1000);
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testTickSnapshotEnd() throws Exception {
		//logger.debug("tickSnapshotEnd not implemented");
		control.replay();
		wrapper.tickSnapshotEnd(1);
		control.verify();
	}
	
	@Test
	public void testTickString() throws Exception {
		//logger.debug("tickString not implemented");
		control.replay();
		wrapper.tickString(1, 2, "3");
		control.verify();
	}
	
	@Test
	public void testUpdateAccountTime() throws Exception {
		//logger.debug("updateAccountTime not implemented");
		control.replay();
		wrapper.updateAccountTime("test");
		control.verify();
	}
	
	@Test
	public void testUpdateAccountValue() throws Exception {
		CountDownLatch finished = this.checkEvent(onUpdateAccount,
				new IBEventUpdateAccount(onUpdateAccount, "Equity",
						"100.00", "USD", "AC1"));
		control.replay();
		wrapper.updateAccountValue("Equity", "100.00", "USD", "AC1");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testUpdateMktDepth() throws Exception {
		//logger.debug("updateMktDepth not implemented");
		control.replay();
		wrapper.updateMktDepth(1, 2, 3, 4, 5, 6);
		control.verify();
	}

	@Test
	public void testUpdateMktDepthL2() throws Exception {
		//logger.debug("updateMktDepthL2 not implemented");
		control.replay();
		wrapper.updateMktDepthL2(1, 2, "3", 4, 5, 6, 7);
		control.verify();
	}
	
	@Test
	public void testUpdateNewsBulletin() throws Exception {
		//logger.debug("updateNewsBulletin not implemented");
		control.replay();
		wrapper.updateNewsBulletin(1, 2, "message", "origExchange");
		control.verify();
	}
	
	@Test
	public void testUpdatePortfolio() throws Exception {
		Contract c = new Contract();
		c.m_symbol = "AAPL";
		CountDownLatch finished = this.checkEvent(onUpdatePortfolio,
				new IBEventUpdatePortfolio(onUpdatePortfolio, c,
						10, 100.00, 1000.00, 99.99, 1.0, 2.0, "AC1"));
		control.replay();
		wrapper.updatePortfolio(c, 10, 100.00, 1000.00, 99.99, 1.0, 2.0, "AC1");
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vCls = new Variant<EventType>(vDisp)
			.add(onConnectionClosed)
			.add(control.createMock(EventType.class));
		Variant<EventType> vErr = new Variant<EventType>(vCls)
			.add(control.createMock(EventType.class))
			.add(onError);
		Variant<EventType> vNid = new Variant<EventType>(vErr)
			.add(control.createMock(EventType.class))
			.add(onNextValidId);
		Variant<EventType> vContDet = new Variant<EventType>(vNid)
			.add(control.createMock(EventType.class))
			.add(onContractDetails);
		Variant<EventType> vMgtAcc = new Variant<EventType>(vContDet)
			.add(control.createMock(EventType.class))
			.add(onManagedAccounts);
		Variant<EventType> vUpdAcc = new Variant<EventType>(vMgtAcc)
			.add(control.createMock(EventType.class))
			.add(onUpdateAccount);
		Variant<EventType> vUpdPrt = new Variant<EventType>(vUpdAcc)
			.add(onUpdatePortfolio)
			.add(control.createMock(EventType.class));
		Variant<EventType> vOpnOrd = new Variant<EventType>(vUpdPrt)
			.add(onOpenOrder)
			.add(control.createMock(EventType.class));
		Variant<EventType> vOrdStat = new Variant<EventType>(vOpnOrd)
			.add(control.createMock(EventType.class))
			.add(onOrderStatus);
		Variant<EventType> vTick = new Variant<EventType>(vOrdStat)
			.add(control.createMock(EventType.class))
			.add(onTick);
		Variant<?> iterator = vTick;
		int foundCnt = 0;
		IBWrapper x = null, found = null;
		do {
			x = new IBWrapper(vDisp.get(), vCls.get(), vErr.get(), vNid.get(),
				vContDet.get(), vMgtAcc.get(), vUpdAcc.get(), vUpdPrt.get(),
				vOpnOrd.get(), vOrdStat.get(), vTick.get());
			if ( wrapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onConnectionClosed, found.OnConnectionClosed());
		assertSame(onError, found.OnError());
		assertSame(onNextValidId, found.OnNextValidId());
		assertSame(onContractDetails, found.OnContractDetails());
		assertSame(onManagedAccounts, found.OnManagedAccounts());
		assertSame(onUpdateAccount, found.OnUpdateAccount());
		assertSame(onUpdatePortfolio, found.OnUpdatePortfolio());
		assertSame(onOpenOrder, found.OnOpenOrder());
		assertSame(onOrderStatus, found.OnOrderStatus());
		assertSame(onTick, found.OnTick());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(wrapper.equals(wrapper));
		assertFalse(wrapper.equals(null));
		assertFalse(wrapper.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 24519)
			.append(2) // VERSION
			.append(dispatcher)
			.append(onConnectionClosed)
			.append(onError)
			.append(onNextValidId)
			.append(onContractDetails)
			.append(onManagedAccounts)
			.append(onUpdateAccount)
			.append(onUpdatePortfolio)
			.append(onOpenOrder)
			.append(onOrderStatus)
			.append(onTick)
			.toHashCode();
		assertEquals(hashCode, wrapper.hashCode());
	}

}
