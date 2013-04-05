package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestContractImpl;

/**
 * 2012-11-19<br>
 * $Id: IBRequestContractImplTest.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestContractImplTest {
	private static EventSystem eSys;
	private static Contract contract;
	private IMocksControl control;
	private IBClient client;
	private EventType wOnError,wOnContract;
	private EventDispatcher dispatcher;
	private EventType onError,onResponse;
	private IBRequestContractImpl request;
	private IBServiceLocator locator;
	private EditableTerminal terminal;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		eSys = new EventSystemImpl();
		contract = new Contract();
		contract.m_symbol = "SBER";		
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		client = control.createMock(IBClient.class);
		terminal = control.createMock(EditableTerminal.class);
		locator = control.createMock(IBServiceLocator.class);

		wOnError = control.createMock(EventType.class);
		wOnContract = control.createMock(EventType.class);
		expect(client.OnError()).andStubReturn(wOnError);
		expect(client.OnContractDetails()).andStubReturn(wOnContract);
		expect(locator.getApiClient()).andStubReturn(client);
		expect(locator.getTerminal()).andStubReturn(terminal);
		
		dispatcher = eSys.createEventDispatcher();
		onError = eSys.createGenericType(dispatcher);
		onResponse = eSys.createGenericType(dispatcher);
		request = new IBRequestContractImpl(locator, dispatcher,
				onError, onResponse, 100, contract);
		eSys.getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		eSys.getEventQueue().stop();
		eSys.getEventQueue().join(1000);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, request.getServiceLocator());
		assertSame(dispatcher, request.getEventDispatcher());
		assertSame(onError, request.OnError());
		assertSame(onResponse, request.OnResponse());
		assertEquals(100, request.getReqId());
		assertSame(contract, request.getContract());
	}
	
	@Test
	public void testStart_Ok() throws Exception {
		wOnContract.addListener(request);
		wOnError.addListener(request);
		client.reqContractDetails(eq(100), same(contract));
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndConnected() throws Exception {
		wOnContract.addListener(request);
		wOnError.addListener(request);
		client.reqContractDetails(eq(100), same(contract));
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(true);
		terminal.firePanicEvent(1, "IBRequestContractImpl#start");
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndDisconnected() throws Exception {
		wOnContract.addListener(request);
		wOnError.addListener(request);
		client.reqContractDetails(eq(100), same(contract));
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(false);
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		control.replay();
		
		request.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_Error_SameReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		onError.addListener(new EventListener() {
			@Override
			public void onEvent(Event e) {
				assertEquals(new IBEventError(onError, 100, 200, "test"), e);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(new IBEventError(wOnError, 100, 200, "test"));
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_Error_DiffReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		onError.addListener(new EventListener() {
			@Override public void onEvent(Event e) { finished.countDown(); }
		});
		control.replay();
		request.onEvent(new IBEventError(wOnError, 199, 200, "test"));
		assertFalse(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_ContractDetails_SameReqId() throws Exception {
		final ContractDetails details = new ContractDetails();
		final CountDownLatch finished = new CountDownLatch(1);
		onResponse.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(new IBEventContract(onResponse, 100,
						IBEventContract.SUBTYPE_NORM, details), event);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(new IBEventContract(wOnContract, 100,
				IBEventContract.SUBTYPE_NORM, details));
		control.verify();
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_ContractDetails_DiffReqId() throws Exception {
		final ContractDetails details = new ContractDetails();
		final CountDownLatch finished = new CountDownLatch(1);
		onResponse.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) { finished.countDown(); }
		});
		control.replay();
		request.onEvent(new IBEventContract(wOnContract, 199,
				IBEventContract.SUBTYPE_NORM, details));
		assertFalse(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 222521)
			.append(locator)
			.append(dispatcher)
			.append(onError)
			.append(onResponse)
			.append(100)
			.append(contract)
			.toHashCode();
		assertEquals(hashCode, request.hashCode());
	}

	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>()
			.add(control.createMock(IBServiceLocator.class))
			.add(locator);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vLoc)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vErr = new Variant<EventType>(vDisp)
			.add(onError)
			.add(control.createMock(EventType.class));
		Variant<EventType> vResp = new Variant<EventType>(vErr)
			.add(control.createMock(EventType.class))
			.add(onResponse);
		Variant<Integer> vNum = new Variant<Integer>(vResp)
			.add(567)
			.add(100);
		Variant<Contract> vCntr = new Variant<Contract>(vNum)
			.add(contract)
			.add(new Contract());
		Variant<?> iterator = vCntr;
		int foundCnt = 0;
		IBRequestContractImpl found = null, x = null;
		do {
			x = new IBRequestContractImpl(vLoc.get(), vDisp.get(), vErr.get(),
					vResp.get(), vNum.get(), vCntr.get());
			if ( request.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onError, found.OnError());
		assertSame(onResponse, found.OnResponse());
		assertEquals(100, found.getReqId());
		assertSame(contract, found.getContract());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(request.equals(request));
		assertFalse(request.equals(null));
		assertFalse(request.equals(this));
	}

}
