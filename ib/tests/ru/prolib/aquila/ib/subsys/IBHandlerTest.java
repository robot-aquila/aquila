package ru.prolib.aquila.ib.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.event.*;
import ru.prolib.aquila.ib.subsys.IBHandler;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.*;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactory;

/**
 * 2013-01-08<br>
 * $Id: IBHandlerTest.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBHandlerTest {
	private IMocksControl control;
	private IBServiceLocator locator;
	private IBClient client;
	private IBContracts contracts;
	private IBRunnableFactory rfact;
	private Counter transId;
	private EventType onMgtAccs, onUpdAcc, onUpdPort, onOpnOrd,
		onOrdStat, onNextId;
	private Runnable runnable;
	private IBRequestFactory freq;
	private IBHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass( ) throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		client = control.createMock(IBClient.class);
		contracts = control.createMock(IBContracts.class);
		rfact = control.createMock(IBRunnableFactory.class);
		freq = control.createMock(IBRequestFactory.class);
		transId = control.createMock(Counter.class);
		onMgtAccs = control.createMock(EventType.class);
		onUpdAcc = control.createMock(EventType.class);
		onUpdPort = control.createMock(EventType.class);
		onOpnOrd = control.createMock(EventType.class);
		onOrdStat = control.createMock(EventType.class);
		onNextId = control.createMock(EventType.class);
		runnable = control.createMock(Runnable.class);
		handler = new IBHandler(locator);

		expect(locator.getApiClient()).andStubReturn(client);
		expect(client.OnManagedAccounts()).andStubReturn(onMgtAccs);
		expect(client.OnUpdateAccount()).andStubReturn(onUpdAcc);
		expect(client.OnUpdatePortfolio()).andStubReturn(onUpdPort);
		expect(client.OnOpenOrder()).andStubReturn(onOpnOrd);
		expect(client.OnOrderStatus()).andStubReturn(onOrdStat);
		expect(client.OnNextValidId()).andStubReturn(onNextId);
		expect(locator.getContracts()).andStubReturn(contracts);
		expect(locator.getRunnableFactory()).andStubReturn(rfact);
		expect(locator.getTransactionNumerator()).andStubReturn(transId);
		expect(locator.getRequestFactory()).andStubReturn(freq);
	}
	
	@Test
	public void testStart() throws Exception {
		onMgtAccs.addListener(same(handler));
		onUpdAcc.addListener(same(handler));
		onUpdPort.addListener(same(handler));
		onOpnOrd.addListener(same(handler));
		onOrdStat.addListener(same(handler));
		contracts.start();
		onNextId.addListener(same(handler));
		control.replay();
		
		handler.start();
		
		control.verify();
		
	}
	
	@Test
	public void testOnEvent_OnManagedAccounts() throws Exception {
		Event event = new IBEventAccounts(onMgtAccs, "X1,X2,X3");
		IBRequestAccountUpdates
			r1 = control.createMock(IBRequestAccountUpdates.class),
			r2 = control.createMock(IBRequestAccountUpdates.class),
			r3 = control.createMock(IBRequestAccountUpdates.class);
		expect(freq.requestAccountUpdates("X1")).andReturn(r1); r1.start();
		expect(freq.requestAccountUpdates("X2")).andReturn(r2); r2.start();
		expect(freq.requestAccountUpdates("X3")).andReturn(r3); r3.start();
		control.replay();
		
		handler.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnUpdateAccount() throws Exception {
		IBEventUpdateAccount event =
			new IBEventUpdateAccount(onUpdAcc, "K", "V", "USD", "X1");
		expect(rfact.createUpdateAccount(same(event))).andReturn(runnable);
		runnable.run();
		control.replay();
		handler.onEvent(event);
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnUpdatePortfolio() throws Exception {
		IBEventUpdatePortfolio event = new IBEventUpdatePortfolio(onUpdPort,
				new Contract(), 0, 0d, 0d, 0d, 0d, 0d, "X1");
		expect(rfact.createUpdatePosition(same(event))).andReturn(runnable);
		runnable.run();
		control.replay();
		handler.onEvent(event);
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnOpenOrder() throws Exception {
		IBEventOrder event = new IBEventOrder(onOpnOrd, 100500);
		expect(rfact.createUpdateOrder(same(event))).andReturn(runnable);
		runnable.run();
		control.replay();
		handler.onEvent(event);
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnOrderStatus() throws Exception {
		IBEventOrder event = new IBEventOrder(onOrdStat, 100500);
		expect(rfact.createUpdateOrder(same(event))).andReturn(runnable);
		runnable.run();
		control.replay();
		handler.onEvent(event);
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnNextValidId_Update() throws Exception {
		IBEventRequest event = new IBEventRequest(onNextId, 876);
		expect(transId.get()).andReturn(1);
		transId.set(eq(876));
		control.replay();
		
		handler.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnNextValidId_SkipLessThanCurr() throws Exception {
		IBEventRequest event = new IBEventRequest(onNextId, 876);
		expect(transId.get()).andReturn(1000);
		control.replay();
		
		handler.onEvent(event);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		IBServiceLocator locator2 = control.createMock(IBServiceLocator.class);
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
		assertTrue(handler.equals(new IBHandler(locator)));
		assertFalse(handler.equals(new IBHandler(locator2)));
	}

}
