package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.apache.log4j.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrdersImplTest {
	private IMocksControl control;
	private EventSystem es;
	private OrdersEventDispatcher dispatcher;
	private EditableTerminal terminal;
	private EditableOrder o1,o2,o3;
	private OrdersImpl orders;
	
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
		dispatcher = control.createMock(OrdersEventDispatcher.class);
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getEventSystem()).andStubReturn(es);
		
		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		o1 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		o2 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		o3 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		orders = new OrdersImpl(dispatcher);
	}
	
	@Test
	public void testEventTypes() throws Exception {
		dispatcher = new OrdersEventDispatcher(es);
		orders = new OrdersImpl(dispatcher);
		assertSame(dispatcher, orders.getEventDispatcher());
		assertSame(dispatcher.OnAvailable(), orders.OnOrderAvailable());
		assertSame(dispatcher.OnCancelFailed(), orders.OnOrderCancelFailed());
		assertSame(dispatcher.OnCancelled(), orders.OnOrderCancelled());
		assertSame(dispatcher.OnChanged(), orders.OnOrderChanged());
		assertSame(dispatcher.OnDone(), orders.OnOrderDone());
		assertSame(dispatcher.OnFailed(), orders.OnOrderFailed());
		assertSame(dispatcher.OnFilled(), orders.OnOrderFilled());
		assertSame(dispatcher.OnPartiallyFilled(),
				orders.OnOrderPartiallyFilled());
		assertSame(dispatcher.OnRegistered(), orders.OnOrderRegistered());
		assertSame(dispatcher.OnRegisterFailed(),
				orders.OnOrderRegisterFailed());
		assertSame(dispatcher.OnTrade(), orders.OnOrderTrade());
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
	public void testFireEvents_Available() throws Exception {
		o1 = control.createMock(EditableOrder.class);
		expect(o1.isAvailable()).andReturn(false);
		o1.setAvailable(true);
		dispatcher.fireAvailable(same(o1));
		o1.resetChanges();
		control.replay();
		
		orders.fireEvents(o1);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Changed() throws Exception {
		o1 = control.createMock(EditableOrder.class);
		expect(o1.isAvailable()).andReturn(true);
		o1.fireChangedEvent();
		o1.resetChanges();
		control.replay();
		
		orders.fireEvents(o1);
		
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
		orders.setOrder(8, o1);
		dispatcher.stopRelayFor(same(o1));
		control.replay();
		
		orders.purgeOrder(8);
		
		control.verify();
	}
	
	@Test
	public void testPurgeOrder_ForNonExisting() throws Exception {
		control.replay();
		
		orders.purgeOrder(8);
		
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
		dispatcher.startRelayFor(same(o1));
		control.replay();
		
		orders.registerOrder(200, o1);
		
		control.verify();
		
		assertTrue(orders.isOrderExists(200));
		assertSame(o1, orders.getOrder(200));
		assertEquals(new Integer(200), o1.getId());
	}
	
	@Test (expected=OrderAlreadyExistsException.class)
	public void testRegisterOrder_ThrowsIfOrderExists() throws Exception {
		o2.setId(123);
		orders.setOrder(123, o2);
		
		orders.registerOrder(123, o1);
	}
	
	@Test
	public void testCreateOrder() throws Exception {
		OrderImpl expected = new OrderImpl(new OrderEventDispatcher(es),
				new Vector<OrderStateHandler>(), terminal);
		control.replay();
		
		OrderImpl actual = (OrderImpl) orders.createOrder(terminal);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
		
		OrderEventDispatcher d = actual.getEventDispatcher();
		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		h.add(new OrderStateHandler(d, new OrderIsRegistered(), d.OnRegistered()));
		h.add(new OrderStateHandler(d, new OrderIsRegisterFailed(), d.OnRegisterFailed()));
		h.add(new OrderStateHandler(d, new OrderIsCancelled(), d.OnCancelled()));
		h.add(new OrderStateHandler(d, new OrderIsCancelFailed(), d.OnCancelFailed()));
		h.add(new OrderStateHandler(d, new OrderIsFilled(), d.OnFilled()));
		h.add(new OrderStateHandler(d, new OrderIsPartiallyFilled(), d.OnPartiallyFilled()));
		h.add(new OrderStateHandler(d, new OrderIsChanged(), d.OnChanged()));
		h.add(new OrderStateHandler(d, new OrderIsDone(), d.OnDone()));
		h.add(new OrderStateHandler(d, new OrderIsFailed(), d.OnFailed()));
		
		assertEquals(h, actual.getStateHandlers());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(orders.equals(orders));
		assertFalse(orders.equals(null));
		assertFalse(orders.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		o1.setId(1);
		o2.setId(2);
		o3.setId(3);
		List<EditableOrder> list1 = new Vector<EditableOrder>();
		list1.add(o1);
		List<EditableOrder> list2 = new Vector<EditableOrder>();
		list2.add(o2);
		list2.add(o3);
		
		Variant<List<EditableOrder>> vList = new Variant<List<EditableOrder>>()
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
			x = new OrdersImpl(dispatcher);
			for ( EditableOrder order : vList.get() ) {
				x.setOrder(order.getId(), order);
			}
			if ( orders.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(list1, found.getOrders());
	}

}
