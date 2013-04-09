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
	private EventTypeMap<Long> onTransReplyMap;
	private EventType onConnStatus, onOrderStatus, onTradeStatus;
	private ApiService api;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = control.createMock(T2QService.class);
		onTransReplyMap = control.createMock(EventTypeMap.class);
		onConnStatus = control.createMock(EventType.class);
		onOrderStatus = control.createMock(EventType.class);
		onTradeStatus = control.createMock(EventType.class);
		api = new ApiService(service, onTransReplyMap, onConnStatus,
				onOrderStatus, onTradeStatus);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(api.equals(api));
		assertFalse(api.equals(null));
		assertFalse(api.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<T2QService> vT2q = new Variant<T2QService>()
			.add(service)
			.add(control.createMock(T2QService.class));
		Variant<EventTypeMap<Long>> vTypeRpl =
				new Variant<EventTypeMap<Long>>(vT2q)
			.add(onTransReplyMap)
			.add(control.createMock(EventTypeMap.class));
		Variant<EventType> vTypeCon = new Variant<EventType>(vTypeRpl)
			.add(onConnStatus)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTypeOrd = new Variant<EventType>(vTypeCon)
			.add(onOrderStatus)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTypeTrd = new Variant<EventType>(vTypeOrd)
			.add(onTradeStatus)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vTypeTrd;
		int foundCnt = 0;
		ApiService x = null, found = null;
		do {
			x = new ApiService(vT2q.get(), vTypeRpl.get(), vTypeCon.get(),
					vTypeOrd.get(), vTypeTrd.get());
			if ( api.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(service, found.service);
		assertSame(onTransReplyMap, found.onTransReplyMap);
		assertSame(onConnStatus, found.OnConnectionStatus());
		assertSame(onOrderStatus, found.OnOrderStatus());
		assertSame(onTradeStatus, found.OnTradeStatus());
	}
	
	@Test
	public void testOnTransactionReply() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(onTransReplyMap.get(893L)).andReturn(type);
		control.replay();
		
		assertSame(type, api.OnTransactionReply(893L));
		
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
