package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.api.*;
import ru.prolib.aquila.quik.assembler.cache.QUIKSymbol;
import ru.prolib.aquila.t2q.T2QException;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class PlaceHandlerTest {
	private static Account account;
	private static QUIKSymbol symbol;
	private IMocksControl control;
	private QUIKTerminal terminal;
	private QUIKClient client;
	private EditableOrder order;
	private Security security;
	private PlaceHandler handler;
	private QUIKResponse response;
	private OrderSystemInfo info;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("SPOT", "18210", "LX01");
		symbol = new QUIKSymbol("RTS-12.13", "SPBFUT", ISO4217.USD,
				SymbolType.STK, "RIZ3", "ShortCode", "Future RTS-12.13");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		order = control.createMock(EditableOrder.class);
		client = control.createMock(QUIKClient.class);
		security = control.createMock(Security.class);
		handler = new PlaceHandler(order);
		info = new OrderSystemInfo();
		
		expect(order.getTerminal()).andStubReturn(terminal);
		expect(order.getId()).andStubReturn(214);
		expect(order.getSystemInfo()).andStubReturn(info);
		expect(order.getAccount()).andStubReturn(account);
		expect(order.getSymbol()).andStubReturn(symbol);
		expect(order.getSecurity()).andStubReturn(security);
		expect(order.getQty()).andStubReturn(1000L);
		expect(terminal.getClient()).andStubReturn(client);
	}
	
	@Test
	public void testHandle_NonFinal() throws Exception {
		response = new QUIKResponse(T2QTransStatus.SENT, 214, null, "test");
		control.replay();
		
		handler.handle(response);
		
		control.verify();
	}
	
	@Test
	public void testHandle_Success() throws Exception {
		response = new QUIKResponse(T2QTransStatus.DONE, 214, 800L, "test");
		client.removeHandler(eq(214));
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getRegistration().getResponse());
		assertNotNull(info.getRegistration().getResponseTime());
	}
	
	@Test
	public void testHandle_ErrorAndOrderStatusSent() throws Exception {
		response = new QUIKResponse(T2QTransStatus.ERR_LIMIT, 214, null, "ERR");
		DateTime time = new DateTime();
		expect(order.getStatus()).andReturn(OrderStatus.SENT);
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(time);
		order.setStatus(OrderStatus.REJECTED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(214));
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getRegistration().getResponse());
		assertNotNull(info.getRegistration().getResponseTime());
	}
	
	@Test
	public void testHandle_Error() throws Exception {
		response = new QUIKResponse(T2QTransStatus.ERR_AUTH, 214, null, "ERR");
		expect(order.getStatus()).andReturn(OrderStatus.CANCELLED);
		client.removeHandler(eq(214));
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getRegistration().getResponse());
		assertNotNull(info.getRegistration().getResponseTime());
	}
	
	@Test
	public void testPlaceOrder_Market() throws Exception {
		expect(order.getDirection()).andStubReturn(Direction.BUY);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getComment()).andStubReturn("");
		String expected = "TRANS_ID=214; ACTION=NEW_ORDER; "
			+ "CLIENT_CODE=18210; ACCOUNT=LX01; CLASSCODE=SPBFUT; "
			+ "SECCODE=RIZ3; OPERATION=B; QUANTITY=1000; TYPE=M; PRICE=0";
		client.send(expected);
		order.setStatus(OrderStatus.SENT);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		handler.placeOrder();
		
		control.verify();
		assertEquals(expected, info.getRegistration().getRequest());
		assertNotNull(info.getRegistration().getRequestTime());
	}
	
	@Test
	public void testPlaceOrder_Limit() throws Exception {
		expect(order.getDirection()).andStubReturn(Direction.SELL);
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getPrice()).andStubReturn(14.95d);
		// will be cut to 20 chars
		expect(order.getComment()).andStubReturn("one;two;three;four;five");
		expect(security.shrinkPrice(eq(14.95d))).andReturn("14.5");
		
		String expected = "TRANS_ID=214; ACTION=NEW_ORDER; "
			+ "CLIENT_CODE=18210; ACCOUNT=LX01; "
			+ "CLASSCODE=SPBFUT; SECCODE=RIZ3; OPERATION=S; QUANTITY=1000; "
			+ "TYPE=L; PRICE=14.5";
		client.send(expected);
		order.setStatus(OrderStatus.SENT);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		handler.placeOrder();
		
		control.verify();
		assertEquals(expected, info.getRegistration().getRequest());
		assertNotNull(info.getRegistration().getRequestTime());
	}
	
	@Test
	public void testPlaceOrder_SecurityFailed() throws Exception {
		SecurityException expected = new SecurityException("test"); 
		expect(order.getDirection()).andStubReturn(Direction.SELL);
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getPrice()).andStubReturn(14.95d);
		expect(order.getComment()).andStubReturn("");
		expect(order.getSecurity()).andThrow(expected);
		order.setStatus(OrderStatus.REJECTED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(214));
		control.replay();

		try {
			handler.placeOrder();
			fail("Expected: " + OrderException.class);
		} catch ( OrderException e ) {
			control.verify();
			assertSame(expected, e.getCause());
		}
	}
	
	@Test
	public void testPlaceOrder_SendFailed() throws Exception {
		T2QException expected = new T2QException("test"); 
		expect(order.getDirection()).andStubReturn(Direction.BUY);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getComment()).andStubReturn("");
		client.send((String) anyObject());
		expectLastCall().andThrow(expected);
		order.setStatus(OrderStatus.REJECTED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(eq(214));
		control.replay();

		try {
			handler.placeOrder();
			fail("Expected: " + OrderException.class);
		} catch ( OrderException e ) {
			control.verify();
			assertSame(expected, e.getCause());
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
		EditableOrder o2 = control.createMock(EditableOrder.class);
		assertFalse(handler.equals(new PlaceHandler(o2)));
		assertTrue(handler.equals(new PlaceHandler(order)));
	}

}
