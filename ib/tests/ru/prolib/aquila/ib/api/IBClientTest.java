package ru.prolib.aquila.ib.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

public class IBClientTest {
	private IMocksControl control;
	private EClientSocket socket;
	private Counter requestId;
	private IBWrapper wrapper;
	private IBClient client;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		socket = control.createMock(EClientSocket.class);
		requestId = control.createMock(Counter.class);
		wrapper = control.createMock(IBWrapper.class);
		client = new IBClient(socket, wrapper, requestId);
	}
	
	@Test
	public void testSetMainHandler() throws Exception {
		MainHandler handler = control.createMock(MainHandler.class);
		wrapper.setMainHandler(same(handler));
		control.replay();
		
		client.setMainHandler(handler);
		
		control.verify();
	}
	
	@Test
	public void testGetMainHandler() throws Exception {
		MainHandler handler = control.createMock(MainHandler.class);
		expect(wrapper.getMainHandler()).andReturn(handler);
		control.replay();
		
		assertSame(handler, client.getMainHandler());
		
		control.verify();
	}
	
	@Test
	public void testSetContractHandler() throws Exception {
		ContractHandler handler = control.createMock(ContractHandler.class);
		wrapper.setContractHandler(eq(5), same(handler));
		control.replay();
		
		client.setContractHandler(5, handler);
		
		control.verify();
	}
	
	@Test
	public void testSetOrderHandler() throws Exception {
		OrderHandler handler = control.createMock(OrderHandler.class);
		wrapper.setOrderHandler(eq(9), same(handler));
		control.replay();
		
		client.setOrderHandler(9, handler);
		
		control.verify();
	}
	
	@Test
	public void testRemoveHandler() throws Exception {
		wrapper.removeHandler(eq(92));
		control.replay();
		
		client.removeHandler(92);
		
		control.verify();
	}
	
	@Test
	public void testConnect_IfConnected() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		control.replay();
		
		client.connect(null);
		
		control.verify();
	}
	
	@Test
	public void testConnect_IfNotConnectedAndSuccess() throws Exception {
		IBConfig config = new IBConfig("localhost", 4001, 5); 
		expect(socket.isConnected()).andReturn(false);
		socket.eConnect(eq("localhost"), eq(4001), eq(5));
		expect(socket.isConnected()).andReturn(true);
		wrapper.connectionOpened();
		control.replay();
		
		client.connect(config);
		
		control.verify();
	}
	
	@Test
	public void testConnect_IfNotConnectedAndFailed() throws Exception {
		IBConfig config = new IBConfig("localhost", 4001, 5); 
		expect(socket.isConnected()).andReturn(false);
		socket.eConnect(eq("localhost"), eq(4001), eq(5));
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		
		client.connect(config);
		
		control.verify();
	}
	
	@Test
	public void testDisconnect_IfConnected() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		socket.eDisconnect();
		wrapper.connectionClosed();
		control.replay();
		
		client.disconnect();
		
		control.verify();
	}
	
	@Test
	public void testDisconnect_IfNotConnected() throws Exception {
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		
		client.disconnect();
		
		control.verify();
	}
	
	@Test
	public void testConnected() throws Exception {
		expect(socket.isConnected()).andReturn(true);
		expect(socket.isConnected()).andReturn(false);
		control.replay();
		
		assertTrue(client.connected());
		assertFalse(client.connected());
		
		control.verify();
	}
	
	@Test
	public void testNextReqId() throws Exception {
		expect(requestId.getAndIncrement()).andReturn(829);
		control.replay();
		
		assertEquals(829, client.nextReqId());
		
		control.verify();
	}
	
	@Test
	public void testReqAutoOpenOrders() throws Exception {
		socket.reqAutoOpenOrders(eq(true));
		socket.reqAutoOpenOrders(eq(false));
		control.replay();
		
		client.reqAutoOpenOrders(true);
		client.reqAutoOpenOrders(false);
		
		control.verify();
	}
	
	@Test
	public void testReqOpenOrders() throws Exception {
		socket.reqOpenOrders();
		control.replay();
		
		client.reqOpenOrders();
		
		control.verify();
	}
	
	@Test
	public void testReqAllOpenOrders() throws Exception {
		socket.reqAllOpenOrders();
		control.replay();
		
		client.reqAllOpenOrders();
		
		control.verify();
	}
	
	@Test
	public void testReqAccountUpdates() throws Exception {
		socket.reqAccountUpdates(eq(true), eq("TEST"));
		socket.reqAccountUpdates(eq(false), eq("BEST"));
		control.replay();
		
		client.reqAccountUpdates(true, "TEST");
		client.reqAccountUpdates(false, "BEST");
		
		control.verify();
	}
	
	@Test
	public void testConstruct1() throws Exception {
		client = new IBClient(requestId);
		assertNotNull(client.getSocket());
		assertEquals(new IBWrapper(), client.getSocket().wrapper());
		assertSame(requestId, client.getRequestNumerator());
		assertEquals(new IBWrapper(), client.getWrapper());
		assertSame(client.getSocket().wrapper(), client.getWrapper());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(client.equals(client));
		assertFalse(client.equals(null));
		assertFalse(client.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBWrapper> vWrp = new Variant<IBWrapper>()
			.add(wrapper)
			.add(control.createMock(IBWrapper.class));
		Variant<Counter> vReqId = new Variant<Counter>(vWrp)
			.add(requestId)
			.add(control.createMock(Counter.class));
		Variant<?> iterator = vReqId;
		int foundCnt = 0;
		IBClient x = null, found = null;
		do {
			x = new IBClient(socket, vWrp.get(), vReqId.get());
			if ( client.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(wrapper, found.getWrapper());
		assertSame(requestId, found.getRequestNumerator());
	}
	
	@Test
	public void testReqContractDetails() throws Exception {
		Contract contract = new Contract();
		socket.reqContractDetails(eq(824), same(contract));
		control.replay();
		
		client.reqContractDetails(824, contract);
		
		control.verify();
	}

}
