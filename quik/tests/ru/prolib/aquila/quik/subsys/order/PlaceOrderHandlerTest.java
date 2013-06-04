package ru.prolib.aquila.quik.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.TransEvent;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class PlaceOrderHandlerTest {
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private EditableOrders orders;
	private EditableOrder order;
	private Counter failedOrderNumerator;
	private EventType type;
	private PlaceOrderHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		orders = control.createMock(EditableOrders.class);
		order = control.createMock(EditableOrder.class);
		failedOrderNumerator = control.createMock(Counter.class);
		locator = control.createMock(QUIKServiceLocator.class);
		handler = new PlaceOrderHandler(locator, orders);
		expect(locator.getFailedOrderNumerator())
			.andStubReturn(failedOrderNumerator);
		expect(type.asString()).andStubReturn("test");
	}
	
	@Test
	public void testOnEvent_SentOrRecv() throws Exception {
		T2QTransStatus fix[] = { T2QTransStatus.RECV, T2QTransStatus.SENT };
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			control.replay();

			handler.onEvent(new TransEvent(type, fix[i], 100L, 200L, "foo"));
			
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
			Date time = new Date();
			Terminal terminal = control.createMock(Terminal.class);
			expect(order.getTerminal()).andStubReturn(terminal);
			expect(terminal.getCurrentTime()).andStubReturn(time);
			expect(failedOrderNumerator.decrementAndGet()).andReturn(-123);
			expect(orders.getPendingOrder(100L)).andReturn(order);
			expect(orders.movePendingOrder(100L, -123L)).andReturn(order);
			order.setAvailable(eq(true));
			orders.fireOrderAvailableEvent(same(order));
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
		expect(orders.getPendingOrder(100L)).andReturn(order);
		expect(orders.movePendingOrder(1L, 2L)).andReturn(order);
		order.setAvailable(eq(true));
		orders.fireOrderAvailableEvent(same(order));
		order.setStatus(OrderStatus.ACTIVE);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		handler.onEvent(new TransEvent(type, T2QTransStatus.DONE, 1L, 2L, "k"));
		
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
		Variant<?> iterator = vOrds;
		int foundCnt = 0;
		PlaceOrderHandler x = null, found = null;
		do {
			x = new PlaceOrderHandler(vLoc.get(), vOrds.get());
			if ( handler.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.locator);
		assertSame(orders, found.orders);
	}

}
