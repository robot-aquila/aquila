package ru.prolib.aquila.ib.subsys.api;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEvent;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.subsys.api.IBApiEventDispatcher;
import ru.prolib.aquila.ib.subsys.api.IBClientImpl;

import com.ib.client.*;

/**
 * 2012-11-23<br>
 * $Id: IBClientImplTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBClientImplTest {
	private static IMocksControl control;
	private static EClientSocket socket;
	private static IBApiEventDispatcher wrapper;
	private static EventType wOnConnectionClosed, wOnError, type;
	private static EventType onConnectionClosed, onConnectionOpened;;
	private static EventDispatcher dispatcher;
	private static IBClientImpl client; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		socket = control.createMock(EClientSocket.class);
		wrapper = control.createMock(IBApiEventDispatcher.class);
		type = control.createMock(EventType.class);
		wOnConnectionClosed = control.createMock(EventType.class);
		wOnError = control.createMock(EventType.class);
		onConnectionClosed = control.createMock(EventType.class);
		onConnectionOpened = control.createMock(EventType.class);
		dispatcher = control.createMock(EventDispatcher.class);
		client = new IBClientImpl(socket, wrapper, dispatcher,
				onConnectionOpened, onConnectionClosed);
		
		expect(dispatcher.asString()).andStubReturn("disp");
		expect(onConnectionOpened.asString()).andStubReturn("opened");
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		expect(wrapper.OnConnectionClosed()).andStubReturn(wOnConnectionClosed);
		expect(wrapper.OnError()).andStubReturn(wOnError);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(socket, client.getSocket());
		assertSame(wrapper, client.getApiEventDispatcher());
		assertSame(dispatcher, client.getEventDispatcher());
	}
	
	@Test
	public void testIsConnected() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		assertTrue(client.isConnected());
		assertFalse(client.isConnected());
		control.verify();
	}
	
	@Test
	public void testEConnect_Success() throws Exception {
		socket.eConnect(eq("192.168.1.1"), eq(4001), eq(1));
		expect(socket.isConnected()).andReturn(true);
		dispatcher.dispatch(eq(new IBEvent(onConnectionOpened)));
		control.replay();
		client.eConnect("192.168.1.1", 4001, 1);
		control.verify();
	}
	
	@Test
	public void testEConnect_Fail() throws Exception {
		socket.eConnect(eq("192.168.1.1"), eq(4001), eq(1));
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.eConnect("192.168.1.1", 4001, 1);
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected, TODO: pop last error", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testEDisconnect() throws Exception {
		socket.eDisconnect();
		control.replay();
		client.eDisconnect();
		control.verify();
	}
	
	@Test
	public void testReqContractDetails_Ok() throws Exception {
		Contract contract = new Contract();
		contract.m_symbol = "AAPL";
		expect(socket.isConnected()).andReturn(true);
		socket.reqContractDetails(eq(200), same(contract));
		control.replay();
		client.reqContractDetails(200, contract);
		control.verify();
	}
	
	@Test
	public void testReqContractDetails_NotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.reqContractDetails(200, new Contract());
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testReqAccountUpdates_SubscribeOk() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.reqAccountUpdates(eq(true), eq("TEST"));
		control.replay();
		client.reqAccountUpdates(true, "TEST");
		control.verify();
	}
	
	@Test
	public void testReqAccountUpdates_SubscribeNotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.reqAccountUpdates(true, "ZULU");
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testReqAccountUpdates_UnsubscribeOk() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.reqAccountUpdates(eq(false), eq("TEST"));
		control.replay();
		client.reqAccountUpdates(false, "TEST");
		control.verify();
	}
	
	@Test
	public void testReqAccountUpdates_UnsubscribeNotConnected()
			throws Exception
	{
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		client.reqAccountUpdates(false, "TEST");
		control.verify();
	}
	
	@Test
	public void testReqManagedAccts_Ok() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.reqManagedAccts();
		control.replay();
		client.reqManagedAccts();
		control.verify();
	}
	
	@Test
	public void testReqManagedAccts_NotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.reqManagedAccts();
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testOnConnectionClosed() throws Exception {
		// Если кто то желает следить за этим событием, то
		// включаем отлов событий-индикаторов от враппера
		wOnConnectionClosed.addListener(client);
		wOnError.addListener(client);
		control.replay();
		assertSame(onConnectionClosed, client.OnConnectionClosed());
		control.verify();
	}

	@Test
	public void testOnError() throws Exception {
		expect(wrapper.OnError()).andReturn(type);
		control.replay();
		assertSame(type, client.OnError());
		control.verify();
	}
	
	@Test
	public void testOnNextValidId() throws Exception {
		expect(wrapper.OnNextValidId()).andReturn(type);
		control.replay();
		assertSame(type, client.OnNextValidId());
		control.verify();
	}
	
	@Test
	public void testOnContractDetails() throws Exception {
		expect(wrapper.OnContractDetails()).andReturn(type);
		control.replay();
		assertSame(type, client.OnContractDetails());
		control.verify();
	}
	
	@Test
	public void testOnManagedAccounts() throws Exception {
		expect(wrapper.OnManagedAccounts()).andReturn(type);
		control.replay();
		assertSame(type, client.OnManagedAccounts());
		control.verify();
	}
	
	@Test
	public void testOnUpdateAccount() throws Exception {
		expect(wrapper.OnUpdateAccount()).andReturn(type);
		control.replay();
		assertSame(type, client.OnUpdateAccount());
		control.verify();
	}

	@Test
	public void testOnUpdatePortfolio() throws Exception {
		expect(wrapper.OnUpdatePortfolio()).andReturn(type);
		control.replay();
		assertSame(type, client.OnUpdatePortfolio());
		control.verify();
	}
	
	@Test
	public void testOnOpenOrder() throws Exception {
		expect(wrapper.OnOpenOrder()).andReturn(type);
		control.replay();
		assertSame(type, client.OnOpenOrder());
		control.verify();
	}
	
	@Test
	public void testOnOrderStatus() throws Exception {
		expect(wrapper.OnOrderStatus()).andReturn(type);
		control.replay();
		assertSame(type, client.OnOrderStatus());
		control.verify();
	}
	
	@Test
	public void testOnTick() throws Exception {
		expect(wrapper.OnTick()).andReturn(type);
		control.replay();
		assertSame(type, client.OnTick());
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_NotConnected() throws Exception {
		Contract contract = new Contract();
		Order order = new Order();
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.placeOrder(89, contract, order);
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_Ok() throws Exception {
		Contract contract = new Contract();
		Order order = new Order();
		expect(socket.isConnected()).andReturn(true);
		socket.placeOrder(123, contract, order);
		control.replay();
		client.placeOrder(123, contract, order);
		control.verify();
	}

	@Test
	public void testCancelOrder_NotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.cancelOrder(100);
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Ok() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.cancelOrder(123);
		control.replay();
		client.cancelOrder(123);
		control.verify();
	}
	
	@Test
	public void testReqMktData_Ok() throws Exception {
		Contract contract = new Contract();
		expect(socket.isConnected()).andReturn(true);
		socket.reqMktData(eq(12), same(contract), eq("100"), eq(true));
		control.replay();
		client.reqMktData(12, contract, "100", true);
		control.verify();
	}

	@Test
	public void testReqMktData_NotConnected() throws Exception {
		Contract contract = new Contract();
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.reqMktData(12, contract, "100", true);
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}

	@Test
	public void testCancelMktData_Ok() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.cancelMktData(eq(123));
		control.replay();
		client.cancelMktData(123);
		control.verify();
	}
	
	@Test
	public void testCancelMktData_NotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		client.cancelMktData(123);
		control.verify();
	}
	
	@Test
	public void testReqAutoOpenOrders_SubscrOk() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.reqAutoOpenOrders(eq(true));
		control.replay();
		client.reqAutoOpenOrders(true);
		control.verify();
	}
	
	@Test
	public void testReqAutoOpenOrders_SubscrNotConn() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		try {
			client.reqAutoOpenOrders(true);
			fail("Expected exception: " + IBException.class.getSimpleName());
		} catch ( IBException e ) {
			assertEquals("Not connected", e.getMessage());
		}
		control.verify();
	}
	
	@Test
	public void testReqAutoOpenOrders_UnsubscrOk() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.reqAutoOpenOrders(eq(true));
		control.replay();
		client.reqAutoOpenOrders(true);
		control.verify();
	}
	
	@Test
	public void testReqAutoOpenOrders_UnsubscrNotConn() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		client.reqAutoOpenOrders(false);
		control.verify();
	}
	
	@Test
	public void testOnConnectionOpened() throws Exception {
		control.replay();
		assertSame(onConnectionOpened, client.OnConnectionOpened());
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnWrapperConnectionClosed() throws Exception {
		dispatcher.dispatch(new IBEvent(onConnectionClosed));
		control.replay();
		client.onEvent(new IBEvent(wOnConnectionClosed));
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnWrapperError_ConnStatusOk() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		control.replay();
		client.onEvent(new IBEventError(wOnError, 1, 200, "test"));
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnWrapperError_Disconnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		dispatcher.dispatch(new IBEvent(onConnectionClosed));
		control.replay();
		client.onEvent(new IBEventError(wOnError, 1, 200, "test"));
		control.verify();
	}
	
}
