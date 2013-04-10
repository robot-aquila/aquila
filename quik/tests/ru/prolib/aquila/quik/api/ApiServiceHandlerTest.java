package ru.prolib.aquila.quik.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class ApiServiceHandlerTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventTypeMap<Long> onTransReplyMap;
	private EventType onConnStatus, onOrderStatus, onTradeStatus;
	private ApiServiceHandler handler;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		onTransReplyMap = control.createMock(EventTypeMap.class);
		onConnStatus = new EventTypeImpl(dispatcher);
		onOrderStatus = new EventTypeImpl(dispatcher);
		onTradeStatus = new EventTypeImpl(dispatcher);
		handler = new ApiServiceHandler(dispatcher, onTransReplyMap,
				onConnStatus, onOrderStatus, onTradeStatus);
		expect(dispatcher.asString()).andStubReturn("test");
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventTypeMap<Long>> vRplMap =
				new Variant<EventTypeMap<Long>>(vDisp)
			.add(onTransReplyMap)
			.add(control.createMock(EventTypeMap.class));
		Variant<EventType> vConType = new Variant<EventType>(vRplMap)
			.add(onConnStatus)
			.add(control.createMock(EventType.class));
		Variant<EventType> vOrdType = new Variant<EventType>(vConType)
			.add(onOrderStatus)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTrdType = new Variant<EventType>(vOrdType)
			.add(onTradeStatus)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vTrdType;
		int foundCnt = 0;
		ApiServiceHandler x = null, found = null;
		do {
			x = new ApiServiceHandler(vDisp.get(), vRplMap.get(),
					vConType.get(), vOrdType.get(), vTrdType.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onTransReplyMap, found.getEventTypeMap());
		assertSame(onConnStatus, found.OnConnStatus());
		assertSame(onOrderStatus, found.OnOrderStatus());
		assertSame(onTradeStatus, found.OnTradeStatus());
	}
	
	@Test
	public void testOnTransReply1() throws Exception {
		EventType type = new EventTypeImpl(dispatcher);
		expect(onTransReplyMap.get(123L)).andReturn(type);
		control.replay();
		
		assertSame(type, handler.OnTransReply(123L));
		
		control.verify();
	}
	
	@Test
	public void testOnConnStatus0() throws Exception {
		assertSame(onConnStatus, handler.OnConnStatus());
	}
	
	@Test
	public void testOnOrderStatus0() throws Exception {
		assertSame(onOrderStatus, handler.OnOrderStatus());
	}
	
	@Test
	public void testOnTradeStatus0() throws Exception {
		assertSame(onTradeStatus, handler.OnTradeStatus());
	}
	
	@Test
	public void testOnConnStatus1() throws Exception {
		ConnEvent expected = new ConnEvent(onConnStatus,T2QConnStatus.DLL_CONN);
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		handler.OnConnStatus(T2QConnStatus.DLL_CONN);
		
		control.verify();
	}
	
	@Test
	public void testOnOrderStatus1() throws Exception {
		T2QOrder order = control.createMock(T2QOrder.class);
		OrderEvent expected = new OrderEvent(onOrderStatus, order);
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		handler.OnOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testOnTradeStatus1() throws Exception {
		T2QTrade trade = control.createMock(T2QTrade.class);
		TradeEvent expected = new TradeEvent(onTradeStatus, trade);
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		handler.OnTradeStatus(trade);
		
		control.verify();
	}
	
	@Test
	public void testOnTransReply4() throws Exception {
		Object fix[][] = {
				// status, finish?
				{ T2QTransStatus.DONE, true },
				{ T2QTransStatus.SENT, false },
				{ T2QTransStatus.RECV, false },
				{ T2QTransStatus.ERR_AUTH, true },
				{ T2QTransStatus.ERR_CON, true },
				{ T2QTransStatus.ERR_CROSS, true },
				{ T2QTransStatus.ERR_LIMIT, true },
				{ T2QTransStatus.ERR_NOK, true },
				{ T2QTransStatus.ERR_REJ, true },
				{ T2QTransStatus.ERR_TIMEOUT, true },
				{ T2QTransStatus.ERR_TSYS, true },
				{ T2QTransStatus.ERR_UNK, true },
				{ T2QTransStatus.ERR_UNSUPPORTED, true },
		};
		
		EventType type;
		TransEvent expected;
		T2QTransStatus status;
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			type = new EventTypeImpl(dispatcher);
			status = (T2QTransStatus) fix[i][0];
			expected = new TransEvent(type, status, 100L, 200L, "test msg");
			expect(onTransReplyMap.get(eq(100L))).andReturn(type);
			dispatcher.dispatchForCurrentList(eq(expected));
			if ( (Boolean) fix[i][1] ) {
				expect(onTransReplyMap.remove(100L)).andReturn(type);
				dispatcher.removeListeners(same(type));
			}
			control.replay();
			
			handler.OnTransReply(status, 100L, 200L, "test msg");
			
			control.verify();
		}
	}

}
