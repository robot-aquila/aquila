package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.*;

public class ApiServiceTest {
	private IMocksControl control;
	private T2QService service;
	private ApiServiceHandler handler;
	private EventType type;
	private ApiService api;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = control.createMock(T2QService.class);
		handler = control.createMock(ApiServiceHandler.class);
		type = control.createMock(EventType.class);
		api = new ApiService(service, handler);
		expect(type.asString()).andStubReturn("test");
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(api.equals(api));
		assertFalse(api.equals(null));
		assertFalse(api.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<T2QService> vSrv = new Variant<T2QService>()
			.add(service)
			.add(control.createMock(T2QService.class));
		Variant<ApiServiceHandler> vHdr = new Variant<ApiServiceHandler>(vSrv)
			.add(handler)
			.add(control.createMock(ApiServiceHandler.class));
		Variant<?> iterator = vHdr;
		int foundCnt = 0;
		ApiService x = null, found = null;
		do {
			x = new ApiService(vSrv.get(), vHdr.get());
			if ( api.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(service, found.getService());
		assertSame(handler, found.getHandler());
	}
	
	@Test
	public void testOnTransReply() throws Exception {
		expect(handler.OnTransReply(893L)).andReturn(type);
		control.replay();
		
		assertSame(type, api.OnTransReply(893L));
		
		control.verify();
	}
	
	@Test
	public void testOnConnStatus() throws Exception {
		expect(handler.OnConnStatus()).andReturn(type);
		control.replay();
		
		assertSame(type, api.OnConnStatus());
		
		control.verify();
	}
	
	@Test
	public void testOnOrderStatus() throws Exception {
		expect(handler.OnOrderStatus()).andReturn(type);
		control.replay();
		
		assertSame(type, api.OnOrderStatus());
		
		control.verify();
	}

	@Test
	public void testOnTradeStatus() throws Exception {
		expect(handler.OnTradeStatus()).andReturn(type);
		control.replay();
		
		assertSame(type, api.OnTradeStatus());
		
		control.verify();
	}
	
	@Test
	public void testConnect() throws Exception {
		service.connect("some path");
		control.replay();
		
		api.connect("some path");
		
		control.verify();
	}
	
	@Test  (expected=ApiServiceException.class)
	public void testConnect_IfException() throws Exception {
		service.connect("some path");
		expectLastCall().andThrow(new T2QException("test"));
		control.replay();
		
		api.connect("some path");
	}
	
	@Test
	public void testDisconnect() throws Exception {
		service.disconnect();
		control.replay();
		
		api.disconnect();
		
		control.verify();
	}
	
	@Test
	public void testSend() throws Exception {
		service.send("trans string");
		control.replay();
		
		api.send("trans string");
		
		control.verify();
	}

	@Test (expected=ApiServiceException.class)
	public void testSend_IfException() throws Exception {
		service.send("trans string");
		expectLastCall().andThrow(new T2QException("test"));
		control.replay();
		
		api.send("trans string");
	}

}
