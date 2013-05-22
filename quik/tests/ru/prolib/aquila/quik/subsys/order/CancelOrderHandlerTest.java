package ru.prolib.aquila.quik.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.TransEvent;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class CancelOrderHandlerTest {
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private EditableOrders orders;
	private EditableOrder order;
	private EventType type;
	private CancelOrderHandler handler;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		type = control.createMock(EventType.class);
		orders = control.createMock(EditableOrders.class);
		order = control.createMock(EditableOrder.class);
		handler = new CancelOrderHandler(locator, orders, 1524L);
		expect(type.asString()).andStubReturn("test");
	}
	
	@Test
	public void testOnEvent_SentOrRecv() throws Exception {
		T2QTransStatus fix[] = { T2QTransStatus.RECV, T2QTransStatus.SENT };
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			control.replay();

			handler.onEvent(new TransEvent(type, fix[i], 100L, null, null));
			
			control.verify();
		}
	}
	
	@Test
	public void testOnEvent_SkipIfNotActive() throws Exception {
		T2QTransStatus fix[] = {
			T2QTransStatus.ERR_AUTH,
			T2QTransStatus.ERR_CON,
			T2QTransStatus.ERR_CROSS,
			T2QTransStatus.ERR_LIMIT,
			T2QTransStatus.ERR_NOK,
			T2QTransStatus.ERR_REJ,
			T2QTransStatus.ERR_TIMEOUT,
			T2QTransStatus.ERR_TSYS,
			T2QTransStatus.ERR_UNK,
			T2QTransStatus.ERR_UNSUPPORTED,
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			expect(orders.getEditableOrder(eq(1524L))).andReturn(order);
			expect(order.getStatus()).andReturn(OrderStatus.CANCELLED);
			control.replay();
			
			handler.onEvent(new TransEvent(type, fix[i], 100L, null, "bar"));
			
			control.verify();
		}
	}

	@Test
	public void testOnEvent_Failed() throws Exception {
		T2QTransStatus fix[] = {
			T2QTransStatus.ERR_AUTH,
			T2QTransStatus.ERR_CON,
			T2QTransStatus.ERR_CROSS,
			T2QTransStatus.ERR_LIMIT,
			T2QTransStatus.ERR_NOK,
			T2QTransStatus.ERR_REJ,
			T2QTransStatus.ERR_TIMEOUT,
			T2QTransStatus.ERR_TSYS,
			T2QTransStatus.ERR_UNK,
			T2QTransStatus.ERR_UNSUPPORTED,
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			Terminal terminal = control.createMock(Terminal.class);
			Date time = new Date();
			expect(order.getTerminal()).andStubReturn(terminal);
			expect(terminal.getCurrentTime()).andStubReturn(time);
			expect(orders.getEditableOrder(eq(1524L))).andReturn(order);
			expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
			order.setStatus(OrderStatus.FAILED);
			order.setLastChangeTime(time);
			order.fireChangedEvent();
			order.resetChanges();
			control.replay();
			
			handler.onEvent(new TransEvent(type, fix[i], 100L, null, "bar"));
			
			control.verify();
		}
	}

	@Test
	public void testOnEvent_Ok() throws Exception {
		control.replay();
		
		handler.onEvent(new TransEvent(type, T2QTransStatus.DONE, 1L, 2L, "?"));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<QUIKServiceLocator> vLoc = new Variant<QUIKServiceLocator>()
			.add(locator)
			.add(control.createMock(QUIKServiceLocator.class));
		Variant<EditableOrders> vOrds = new Variant<EditableOrders>(vLoc)
			.add(orders)
			.add(control.createMock(EditableOrders.class));
		Variant<Long> vId = new Variant<Long>(vOrds)
			.add(1524L)
			.add(8761L);
		Variant<?> iterator = vId;
		int foundCnt = 0;
		CancelOrderHandler x = null, found = null;
		do {
			x = new CancelOrderHandler(vLoc.get(), vOrds.get(), vId.get());
			if ( handler.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.locator);
		assertSame(orders, found.orders);
		assertEquals(1524L, found.orderId);
	}

}
