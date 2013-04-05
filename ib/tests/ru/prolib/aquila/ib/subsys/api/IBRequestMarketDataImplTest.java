package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.*;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.TickType;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventTick;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBRequestMarketDataImpl;

/**
 * 2012-12-23<br>
 * $Id: IBRequestMarketDataImplTest.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestMarketDataImplTest {
	private static EventSystem es;
	private static EventQueue queue;
	private static Contract contract;
	private IMocksControl control;
	private EventType wOnError,wOnTick;
	private IBClient client;
	private EventDispatcher dispatcher;
	private EventType onError,onTick;
	private IBServiceLocator locator;
	private EditableTerminal terminal;
	private IBRequestMarketDataImpl request;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		es = new EventSystemImpl();
		queue = es.getEventQueue();
		contract = new Contract();
		contract.m_symbol = "AAPL";
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		wOnError = control.createMock(EventType.class);
		wOnTick = control.createMock(EventType.class);
		client = control.createMock(IBClient.class);
		terminal = control.createMock(EditableTerminal.class);
		locator = control.createMock(IBServiceLocator.class);
		expect(client.OnError()).andStubReturn(wOnError);
		expect(client.OnTick()).andStubReturn(wOnTick);
		expect(locator.getApiClient()).andStubReturn(client);
		expect(locator.getTerminal()).andStubReturn(terminal);
		queue.start();
		dispatcher = es.createEventDispatcher();
		onError = es.createGenericType(dispatcher);
		onTick = es.createGenericType(dispatcher);
		request = new IBRequestMarketDataImpl(locator, dispatcher,
				onError, onTick, 100500, contract);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		queue.join(1000);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, request.getServiceLocator());
		assertSame(dispatcher, request.getEventDispatcher());
		assertSame(onError, request.OnError());
		assertSame(onTick, request.OnTick());
		assertEquals(100500, request.getReqId());
		assertSame(contract, request.getContract());
	}
	
	@Test
	public void testStart_Ok() throws Exception {
		wOnError.addListener(same(request));
		wOnTick.addListener(same(request));
		client.reqMktData(eq(100500),same(contract),eq((String)null),eq(false));
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndDisconnected() throws Exception {
		wOnError.addListener(same(request));
		wOnTick.addListener(same(request));
		client.reqMktData(eq(100500),same(contract),eq((String)null),eq(false));
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(false);
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStart_ExceptionAndConnected() throws Exception {
		wOnError.addListener(same(request));
		wOnTick.addListener(same(request));
		client.reqMktData(eq(100500),same(contract),eq((String)null),eq(false));
		expectLastCall().andThrow(new IBException("Test exception"));
		expect(client.isConnected()).andReturn(true);
		terminal.firePanicEvent(1, "IBRequestMarketDataImpl#start");
		control.replay();
		
		request.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		client.cancelMktData(eq(100500));
		control.replay();
		
		request.stop();
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(request.equals(request));
		assertFalse(request.equals(null));
		assertFalse(request.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>()
			.add(locator)
			.add(null)
			.add(control.createMock(IBServiceLocator.class));
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vLoc)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class))
			.add(null);
		Variant<EventType> vErr = new Variant<EventType>(vDisp)
			.add(onError)
			.add(onTick)
			.add(null)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTick = new Variant<EventType>(vErr)
			.add(onTick)
			.add(onError)
			.add(null)
			.add(control.createMock(EventType.class));
		Variant<Integer> vReqId = new Variant<Integer>(vTick)
			.add(100500)
			.add(12345);
		Variant<Contract> vCont = new Variant<Contract>(vReqId)
			.add(contract)
			.add(null)
			.add(new Contract());
		Variant<?> iterator = vCont;
		int foundCnt = 0;
		IBRequestMarketDataImpl x = null, found = null;
		do {
			x = new IBRequestMarketDataImpl(vLoc.get(), vDisp.get(), vErr.get(),
					vTick.get(), vReqId.get(), vCont.get());
			if ( request.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onError, found.OnError());
		assertSame(onTick, found.OnTick());
		assertEquals(100500, found.getReqId());
		assertSame(contract, found.getContract());
	}
	
	@Test
	public void testOnEvent_OnError_SameReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final IBEventError expected =
				new IBEventError(onError, 100500, 200, "test");
		request.OnError().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(new IBEventError(wOnError, 100500, 200, "test"));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnError_DiffReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		request.OnError().addListener(new EventListener() {
			@Override public void onEvent(Event event) { finished.countDown(); }
		});
		control.replay();
		request.onEvent(new IBEventError(wOnError, 122599, 200, "test"));
		assertFalse(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testOnEvent_OnTick_SameReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final IBEventTick expected =
				new IBEventTick(onTick, 100500, TickType.ASK, 2000d);
		request.OnTick().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		control.replay();
		request.onEvent(new IBEventTick(wOnTick, 100500, TickType.ASK, 2000d));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnTick_DiffReqId() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		request.OnTick().addListener(new EventListener() {
			@Override public void onEvent(Event event) { finished.countDown(); }
		});
		control.replay();
		request.onEvent(new IBEventTick(wOnTick, 199599, TickType.ASK, 2000d));
		assertFalse(finished.await(100, TimeUnit.MILLISECONDS));
	}


}
