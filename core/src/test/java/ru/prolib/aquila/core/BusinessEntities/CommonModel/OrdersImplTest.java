package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.apache.log4j.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.utils.*;

public class OrdersImplTest {
	private IMocksControl control;
	private EventSystem es;
	private OrdersEventDispatcher dispatcher;
	private EditableTerminal<?> terminal;
	private EditableOrder o1,o2,o3;
	private OrdersImpl orders;
	private OrderFactory factory;
	private Counter orderIdSeq;
	
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
		factory = control.createMock(OrderFactory.class);
		orderIdSeq = new SimpleCounter(200);
		
		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		o1 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		o2 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		o3 = new OrderImpl(new OrderEventDispatcher(es), h, terminal);
		orders = new OrdersImpl(dispatcher, factory, orderIdSeq);
	}
	
	@Test
	public void testConstructor2() throws Exception {
		assertSame(dispatcher, orders.getEventDispatcher());
		assertSame(factory, orders.getOrderFactory());
		assertSame(orderIdSeq, orders.getIdSequence());
	}
	
	@Test
	public void testEventTypes() throws Exception {
		dispatcher = new OrdersEventDispatcher(es);
		orders = new OrdersImpl(dispatcher, factory, orderIdSeq);
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
	public void testCreateOrder() throws Exception {
		expect(factory.createOrder(same(terminal))).andReturn(o1);
		dispatcher.startRelayFor(same(o1));
		control.replay();
		
		EditableOrder actual = orders.createOrder(terminal);
		
		control.verify();
		assertSame(actual, o1);
		assertEquals(201, (int)o1.getId());
		assertTrue(orders.isOrderExists(201));
		assertSame(actual, orders.getOrder(201));
		assertEquals(201, orderIdSeq.get()); // the ID should be incremented
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(orders.equals(orders));
		assertFalse(orders.equals(null));
		assertFalse(orders.equals(this));
	}

}
