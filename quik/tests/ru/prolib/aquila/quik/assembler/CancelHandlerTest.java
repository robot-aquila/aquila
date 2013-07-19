package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.api.*;
import ru.prolib.aquila.t2q.T2QException;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class CancelHandlerTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private QUIKEditableTerminal terminal;
	private QUIKClient client;
	private EditableOrder order;
	private CancelHandler handler;
	private QUIKResponse response;
	private OrderSystemInfo info;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKEditableTerminal.class);
		client = control.createMock(QUIKClient.class);
		order = control.createMock(EditableOrder.class);
		handler = new CancelHandler(215, order);
		info = new OrderSystemInfo();
		
		expect(order.getSystemInfo()).andStubReturn(info);
		expect(order.getTerminal()).andStubReturn(terminal);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);
		expect(terminal.getClient()).andStubReturn(client);
	}
	
	@Test
	public void testHandle_NonFinal() throws Exception {
		response = new QUIKResponse(T2QTransStatus.SENT, 215, null, "test");
		control.replay();
		
		handler.handle(response);
		
		control.verify();
	}
	
	@Test
	public void testHandle_Success() throws Exception {
		response = new QUIKResponse(T2QTransStatus.DONE, 215, null, "test");
		client.removeHandler(eq(215));
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getCancellation().getResponse());
		assertNotNull(info.getCancellation().getResponseTime());
	}
	
	@Test
	public void testHandle_ErrorAndOrderStatusCancelSent() throws Exception {
		response = new QUIKResponse(T2QTransStatus.ERR_LIMIT, 215, null, "ERR");
		Date time = new Date();
		expect(order.getStatus()).andReturn(OrderStatus.CANCEL_SENT);
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(time);
		order.setStatus(OrderStatus.CANCEL_FAILED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(215);
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getCancellation().getResponse());
		assertNotNull(info.getCancellation().getResponseTime());
	}

	@Test
	public void testHandle_Error() throws Exception {
		response = new QUIKResponse(T2QTransStatus.ERR_AUTH, 215, null, "test");
		expect(order.getStatus()).andReturn(OrderStatus.FILLED);
		client.removeHandler(eq(215));
		control.replay();
		
		handler.handle(response);
		
		control.verify();
		assertSame(response, info.getCancellation().getResponse());
		assertNotNull(info.getCancellation().getResponseTime());
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		info.getRegistration()
			.setResponse(new QUIKResponse(T2QTransStatus.DONE, 214, 89L, "ok"));
		String expected = "TRANS_ID=215; ACTION=KILL_ORDER; "
			+ "ORDER_KEY=89; CLASSCODE=EQBR";
		client.send(eq(expected));
		order.setStatus(OrderStatus.CANCEL_SENT);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		handler.cancelOrder();
		
		control.verify();
		assertEquals(expected, info.getCancellation().getRequest());
		assertNotNull(info.getCancellation().getRequestTime());
	}

	@Test
	public void testCancelOrder_SendFailed() throws Exception {
		T2QException expected = new T2QException("test");
		info.getRegistration()
			.setResponse(new QUIKResponse(T2QTransStatus.DONE, 214, 89L, "ok"));
		client.send((String) anyObject());
		expectLastCall().andThrow(expected);
		order.setStatus(OrderStatus.CANCEL_FAILED);
		order.fireChangedEvent();
		order.resetChanges();
		client.removeHandler(215);
		control.replay();
		
		try {
			handler.cancelOrder();
			fail("Expected: " + OrderException.class);
		} catch ( OrderException e ) {
			control.verify();
			assertNotNull(info.getCancellation().getRequest());
			assertNotNull(info.getCancellation().getRequestTime());
			assertSame(expected, e.getCause());
		}
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Integer> vId = new Variant<Integer>()
			.add(215)
			.add(428);
		Variant<EditableOrder> vOrd = new Variant<EditableOrder>(vId)
			.add(order)
			.add(control.createMock(EditableOrder.class));
		Variant<?> iterator = vOrd;
		int foundCnt = 0;
		CancelHandler x, found = null;
		do {
			x = new CancelHandler(vId.get(), vOrd.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(215, found.getTransId());
		assertSame(order, found.getOrder());
	}

}
