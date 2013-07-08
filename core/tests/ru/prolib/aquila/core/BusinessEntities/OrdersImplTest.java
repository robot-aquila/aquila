package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.BusinessEntities.validator.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-17<br>
 * $Id: OrdersImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class OrdersImplTest {
	private IMocksControl control;
	private EventSystem es;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onAvailable,onCancelFailed,onCancelled,onChanged,
		onDone,onFailed,onFilled,onPartiallyFilled,onRegistered,
		onRegisterFailed, onTrade;
	private EditableTerminal terminal;
	private EditableOrder o1,o2,o3;
	/**
	 * Order with event types (not a mock)
	 */
	private EditableOrder owt;
	private OrdersImpl orders, orders2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Orders");
		onAvailable = dispatcher.createType("OnAvailable");
		onCancelFailed = dispatcher.createType("OnCancelFailed");
		onCancelled = dispatcher.createType("OnCancelled");
		onChanged = dispatcher.createType("OnChanged");
		onDone = dispatcher.createType("OnDone");
		onFailed = dispatcher.createType("OnFailed");
		onFilled = dispatcher.createType("OnFilled");
		onPartiallyFilled = dispatcher.createType("OnPartiallyFilled");
		onRegistered = dispatcher.createType("OnRegistered");
		onRegisterFailed = dispatcher.createType("OnRegisterFailed");
		onTrade = dispatcher.createType("OnTrade");
		dispatcherMock = control.createMock(EventDispatcher.class);
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getEventSystem()).andStubReturn(es);
		
		o1 = control.createMock(EditableOrder.class);
		o2 = control.createMock(EditableOrder.class);
		o3 = control.createMock(EditableOrder.class);
		orders = new OrdersImpl(dispatcher, onAvailable, onCancelFailed,
				onCancelled, onChanged, onDone, onFailed, onFilled,
				onPartiallyFilled, onRegistered, onRegisterFailed,
				onTrade);
		orders2 = new OrdersImpl(dispatcherMock, onAvailable, onCancelFailed,
				onCancelled, onChanged, onDone, onFailed, onFilled,
				onPartiallyFilled, onRegistered, onRegisterFailed,
				onTrade);
		
		EventDispatcher d = es.createEventDispatcher("Order");
		owt = new OrderImpl(d, d.createType(), d.createType(),
				d.createType(), d.createType(), d.createType(), d.createType(),
				d.createType(), d.createType(), d.createType(), d.createType(),
				new Vector<OrderHandler>(), terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(dispatcher, orders.getEventDispatcher());
		assertSame(onAvailable, orders.OnOrderAvailable());
		assertSame(onCancelFailed, orders.OnOrderCancelFailed());
		assertSame(onCancelled, orders.OnOrderCancelled());
		assertSame(onChanged, orders.OnOrderChanged());
		assertSame(onDone, orders.OnOrderDone());
		assertSame(onFailed, orders.OnOrderFailed());
		assertSame(onFilled, orders.OnOrderFilled());
		assertSame(onPartiallyFilled, orders.OnOrderPartiallyFilled());
		assertSame(onRegistered, orders.OnOrderRegistered());
		assertSame(onRegisterFailed, orders.OnOrderRegisterFailed());
		assertSame(onTrade, orders.OnOrderTrade());
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		orders.setOrder(12345, o1);
		assertTrue(orders.isOrderExists(12345));
		assertFalse(orders.isOrderExists(87654));
	}
	
	@Test
	public void testGetOrders() throws Exception {
		orders.setOrder(8, o1);
		orders.setOrder(9, o2);
		orders.setOrder(5, o3);
		List<Order> expected = new Vector<Order>();
		expected.add(o1);
		expected.add(o2);
		expected.add(o3);
		
		List<Order> list = orders.getOrders();
		assertNotNull(list);
		assertEquals(expected, list);
	}
	
	@Test
	public void testGetOrder_Ok() throws Exception {
		orders.setOrder(8, o1);
		assertSame(o1, orders.getOrder(8));
	}
	
	@Test (expected=OrderNotExistsException.class)
	public void testGetOrder_ThrowsIfOrderNotExists() throws Exception {
		orders.getOrder(8);
	}
	
	@Test
	public void testFireOrderAvailableEvent() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onAvailable, o1));
		control.replay();
		
		orders2.fireOrderAvailableEvent(o1);
		
		control.verify();
	}
	
	@Test
	public void testGetEditableOrder_Ok() throws Exception {
		orders.setOrder(8, o1);
		orders.setOrder(9, o2);
		
		assertSame(o1, orders.getEditableOrder(8));
		assertSame(o2, orders.getEditableOrder(9));
	}
	
	@Test (expected=OrderNotExistsException.class)
	public void testGetEditableOrder_ThrowsIfOrderNotExists()
		throws Exception
	{
		orders.getEditableOrder(8);
	}
	
	@Test
	public void testPurgeOrder_ForExisting() throws Exception {
		owt.OnCancelFailed().addListener(orders);
		owt.OnCancelled().addListener(orders);
		owt.OnChanged().addListener(orders);
		owt.OnDone().addListener(orders);
		owt.OnFailed().addListener(orders);
		owt.OnFilled().addListener(orders);
		owt.OnPartiallyFilled().addListener(orders);
		owt.OnRegistered().addListener(orders);
		owt.OnRegisterFailed().addListener(orders);
		owt.OnTrade().addListener(orders);
		orders.setOrder(8, owt);
		
		orders.purgeOrder(8);
		
		assertFalse(orders.isOrderExists(8));
		assertFalse(owt.OnCancelFailed().isListener(orders));
		assertFalse(owt.OnCancelled().isListener(orders));
		assertFalse(owt.OnChanged().isListener(orders));
		assertFalse(owt.OnDone().isListener(orders));
		assertFalse(owt.OnFailed().isListener(orders));
		assertFalse(owt.OnFilled().isListener(orders));
		assertFalse(owt.OnPartiallyFilled().isListener(orders));
		assertFalse(owt.OnRegistered().isListener(orders));
		assertFalse(owt.OnRegisterFailed().isListener(orders));
		assertFalse(owt.OnTrade().isListener(orders));
	}
	
	@Test
	public void testPurgeOrder_ForNonExisting() throws Exception {
		orders.purgeOrder(8);
	}

	@Test
	public void testOnEvent_OnCancellFailed() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onCancelFailed, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnCancelFailed(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnCancelled() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onCancelled, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnCancelled(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onChanged, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnChanged(), owt));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnDone() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onDone, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnDone(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnFailed() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onFailed, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnFailed(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnFilled() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onFilled, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnFilled(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnPartiallyFilled() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onPartiallyFilled, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnPartiallyFilled(), owt));
		
		control.verify();
	}

	@Test
	public void testOnEvent_OnRegistered() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onRegistered, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnRegistered(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnRegisterFailed() throws Exception {
		dispatcherMock.dispatch(new OrderEvent(onRegisterFailed, owt));
		control.replay();
		
		orders2.onEvent(new OrderEvent(owt.OnRegisterFailed(), owt));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnTrade() throws Exception {
		Trade t = new Trade(terminal);
		dispatcherMock.dispatch(new OrderTradeEvent(onTrade, owt, t));
		control.replay();
		
		orders2.onEvent(new OrderTradeEvent(owt.OnTrade(), owt, t));
		
		control.verify();
	}

	@Test
	public void testGetOrdersCount() throws Exception {
		assertEquals(0, orders.getOrdersCount());
		orders.setOrder(1, o1);
		assertEquals(1, orders.getOrdersCount());
		orders.setOrder(2, o2);
		assertEquals(2, orders.getOrdersCount());
		orders.setOrder(40, o3);
		assertEquals(3, orders.getOrdersCount());
	}
	
	@Test
	public void testRegisterOrder() throws Exception {
		orders.registerOrder(200, owt);
		
		assertTrue(orders.isOrderExists(200));
		assertSame(owt, orders.getOrder(200));
		assertEquals(new Integer(200), owt.getId());
		assertTrue(owt.OnCancelFailed().isListener(orders));
		assertTrue(owt.OnCancelled().isListener(orders));
		assertTrue(owt.OnChanged().isListener(orders));
		assertTrue(owt.OnDone().isListener(orders));
		assertTrue(owt.OnFailed().isListener(orders));
		assertTrue(owt.OnFilled().isListener(orders));
		assertTrue(owt.OnPartiallyFilled().isListener(orders));
		assertTrue(owt.OnRegistered().isListener(orders));
		assertTrue(owt.OnRegisterFailed().isListener(orders));
		assertTrue(owt.OnTrade().isListener(orders));
	}
	
	@Test (expected=OrderAlreadyExistsException.class)
	public void testRegisterOrder_ThrowsIfOrderExists() throws Exception {
		orders.setOrder(123, o1);
		orders.registerOrder(123, owt);
	}
	
	@Test
	public void testCreateOrder() throws Exception {
		EventDispatcher d = es.createEventDispatcher("Order");
		List<OrderEventHandler> h = new Vector<OrderEventHandler>();
		h.add(new OrderEventHandler(d, new OrderIsRegistered(),
				d.createType("OnRegister")));
		h.add(new OrderEventHandler(d, new OrderIsRegisterFailed(),
				d.createType("OnRegisterFailed")));
		h.add(new OrderEventHandler(d, new OrderIsCancelled(),
				d.createType("OnCancelled")));
		h.add(new OrderEventHandler(d, new OrderIsCancelFailed(),
				d.createType("OnCancelFailed")));
		h.add(new OrderEventHandler(d, new OrderIsFilled(),
				d.createType("OnFilled")));
		h.add(new OrderEventHandler(d, new OrderIsPartiallyFilled(),
				d.createType("OnPartiallyFilled")));
		h.add(new OrderEventHandler(d, new OrderIsChanged(),
				d.createType("OnChanged")));
		h.add(new OrderEventHandler(d, new OrderIsDone(),
				d.createType("OnDone")));
		h.add(new OrderEventHandler(d, new OrderIsFailed(),
				d.createType("OnFailed")));
		OrderImpl expected = new OrderImpl(d, h.get(0).getEventType(),
				h.get(1).getEventType(), h.get(2).getEventType(),
				h.get(3).getEventType(), h.get(4).getEventType(),
				h.get(5).getEventType(), h.get(6).getEventType(),
				h.get(7).getEventType(), h.get(8).getEventType(),
				d.createType("OnTrade"), h, terminal);
		control.replay();
		
		OrderImpl actual = (OrderImpl) orders.createOrder(terminal);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(orders.equals(orders));
		assertFalse(orders.equals(null));
		assertFalse(orders.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		expect(o1.getId()).andStubReturn(1);
		expect(o2.getId()).andStubReturn(2);
		expect(o3.getId()).andStubReturn(3);
		List<EditableOrder> list1 = new Vector<EditableOrder>();
		list1.add(o1);
		List<EditableOrder> list2 = new Vector<EditableOrder>();
		list2.add(o2);
		list2.add(o3);
		
		Variant<String> vDispId = new Variant<String>()
			.add("Orders")
			.add("Another");
		Variant<String> vAvlId = new Variant<String>(vDispId)
			.add("OnAvailable")
			.add("OnAvailableX");
		Variant<String> vCnclFailId = new Variant<String>(vAvlId)
			.add("OnCancelFailed")
			.add("OnCancelFailedX");
		Variant<String> vCnclId = new Variant<String>(vCnclFailId)
			.add("OnCancelled")
			.add("OnCancelledX");
		Variant<String> vChngId = new Variant<String>(vCnclId)
			.add("OnChanged")
			.add("OnChangedX");
		Variant<String> vDoneId = new Variant<String>(vChngId)
			.add("OnDone")
			.add("OnDoneX");
		Variant<String> vFailId = new Variant<String>(vDoneId)
			.add("OnFailed")
			.add("OnFailedX");
		Variant<String> vFillId = new Variant<String>(vFailId)
			.add("OnFilled")
			.add("OnFilledX");
		Variant<String> vPartFillId = new Variant<String>(vFillId)
			.add("OnPartiallyFilled")
			.add("OnPartiallyFilledX");
		Variant<String> vRegId = new Variant<String>(vPartFillId)
			.add("OnRegistered")
			.add("OnRegisteredX");
		Variant<String> vRegFailId = new Variant<String>(vRegId)
			.add("OnRegisterFailed")
			.add("OnRegisterFailedX");
		Variant<String> vTrdId = new Variant<String>(vRegFailId)
			.add("OnTrade")
			.add("OnTradeX");
		Variant<List<EditableOrder>> vList =
				new Variant<List<EditableOrder>>(vTrdId)
			.add(list1)
			.add(list2);
		Variant<?> iterator = vList;
		control.replay();
		for ( EditableOrder order : list1 ) {
			orders.setOrder(order.getId(), order);
		}
		int foundCnt = 0;
		OrdersImpl x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new OrdersImpl(d, d.createType(vAvlId.get()),
					d.createType(vCnclFailId.get()),
					d.createType(vCnclId.get()),
					d.createType(vChngId.get()),
					d.createType(vDoneId.get()),
					d.createType(vFailId.get()),
					d.createType(vFillId.get()),
					d.createType(vPartFillId.get()),
					d.createType(vRegId.get()),
					d.createType(vRegFailId.get()),
					d.createType(vTrdId.get()));
			for ( EditableOrder order : vList.get() ) {
				x.setOrder(order.getId(), order);
			}
			if ( orders.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onAvailable, found.OnOrderAvailable());
		assertEquals(onCancelFailed, found.OnOrderCancelFailed());
		assertEquals(onCancelled, found.OnOrderCancelled());
		assertEquals(onChanged, found.OnOrderChanged());
		assertEquals(onDone, found.OnOrderDone());
		assertEquals(onFailed, found.OnOrderFailed());
		assertEquals(onFilled, found.OnOrderFilled());
		assertEquals(onPartiallyFilled, found.OnOrderPartiallyFilled());
		assertEquals(onRegistered, found.OnOrderRegistered());
		assertEquals(onRegisterFailed, found.OnOrderRegisterFailed());
		assertEquals(onTrade, found.OnOrderTrade());
		assertEquals(list1, found.getOrders());
	}

}
