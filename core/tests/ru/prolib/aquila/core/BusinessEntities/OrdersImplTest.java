package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;
import org.apache.log4j.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;

/**
 * 2012-10-17<br>
 * $Id: OrdersImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class OrdersImplTest {
	private static EventSystem eventSystem;
	private static EventQueue queue;
	private static EventDispatcher dispatcher;
	private static EventType onAvailable,onCancelFailed,onCancelled,onChanged,
		onDone,onFailed,onFilled,onPartiallyFilled,onRegistered,
		onRegisterFailed;
	private static Terminal terminal;
	private EditableOrder o1,o2,o3;
	private OrdersImpl orders;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = eventSystem.createEventDispatcher();
		onAvailable = eventSystem.createGenericType(dispatcher);
		onCancelFailed = eventSystem.createGenericType(dispatcher);
		onCancelled = eventSystem.createGenericType(dispatcher);
		onChanged = eventSystem.createGenericType(dispatcher);
		onDone = eventSystem.createGenericType(dispatcher);
		onFailed = eventSystem.createGenericType(dispatcher);
		onFilled = eventSystem.createGenericType(dispatcher);
		onPartiallyFilled = eventSystem.createGenericType(dispatcher);
		onRegistered = eventSystem.createGenericType(dispatcher);
		onRegisterFailed = eventSystem.createGenericType(dispatcher);
		terminal = new TerminalDecorator();
	}

	@Before
	public void setUp() throws Exception {
		o1 = createOrder();
		o2 = createOrder();
		o3 = createOrder();
		orders = new OrdersImpl(dispatcher, onAvailable, onCancelFailed,
				onCancelled, onChanged, onDone, onFailed, onFilled,
				onPartiallyFilled, onRegistered, onRegisterFailed);
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		dispatcher.close();
		queue.stop();
		assertTrue(queue.join(1000));
	}

	/**
	 * Создать пустую тестовую заявку.
	 * <p>
	 * @return заявка
	 */
	private static EditableOrder createOrder() {
		EventDispatcher edisp = eventSystem.createEventDispatcher();
		return new OrderImpl(edisp, eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				eventSystem.createGenericType(edisp),
				new LinkedList<OrderHandler>(),
				terminal);
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
	}
	
	@Test
	public void testIsOrderExists() throws Exception {
		o1.setId(12345L);
		orders.registerOrder(o1);
		
		assertTrue(orders.isOrderExists(12345L));
		assertFalse(orders.isOrderExists(87654L));
	}
	
	@Test
	public void testGetOrders() throws Exception {
		o1.setId(8L); orders.registerOrder(o1);
		o2.setId(9L); orders.registerOrder(o2);
		o3.setId(5L); orders.registerOrder(o3);
		
		List<Order> list = orders.getOrders();
		
		assertNotNull(list);
		assertEquals(3, list.size());
		assertSame(o1, list.get(0));
		assertSame(o2, list.get(1));
		assertSame(o3, list.get(2));
	}
	
	@Test
	public void testGetOrder_Ok() throws Exception {
		o1.setId(8L); orders.registerOrder(o1);
		o2.setId(9L); orders.registerOrder(o2);
		o3.setId(5L); orders.registerOrder(o3);
		
		assertSame(o1, orders.getOrder(8L));
		assertSame(o2, orders.getOrder(9L));
	}
	
	@Test (expected=OrderNotExistsException.class)
	public void testGetOrder_ThrowsIfOrderNotExists() throws Exception {
		orders.getOrder(8L);
	}
	
	@Test
	public void testFireOrderAvailableEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final OrderEvent expected = new OrderEvent(onAvailable, o1);
		orders.OnOrderAvailable().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		orders.fireOrderAvailableEvent(o1);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testGetEditableOrder_Ok() throws Exception {
		o1.setId(8L); orders.registerOrder(o1);
		o2.setId(9L); orders.registerOrder(o2);
		o3.setId(5L); orders.registerOrder(o3);
		
		assertSame(o1, orders.getEditableOrder(8L));
		assertSame(o2, orders.getEditableOrder(9L));
	}
	
	@Test
	public void testGetEditableOrder_ReturnsNullIfOrderNotExists()
		throws Exception
	{
		assertNull(orders.getEditableOrder(8L));
	}
	
	@Test
	public void testRegisterOrder_Ok() throws Exception {
		o1.setId(8L);
		orders.registerOrder(o1);
		assertTrue(orders.isOrderExists(8L));
		assertTrue(o1.OnCancelFailed().isListener(orders));
		assertTrue(o1.OnCancelled().isListener(orders));
		assertTrue(o1.OnChanged().isListener(orders));
		assertTrue(o1.OnDone().isListener(orders));
		assertTrue(o1.OnFailed().isListener(orders));
		assertTrue(o1.OnFilled().isListener(orders));
		assertTrue(o1.OnPartiallyFilled().isListener(orders));
		assertTrue(o1.OnRegistered().isListener(orders));
		assertTrue(o1.OnRegisterFailed().isListener(orders));
	}
	
	@Test
	public void testRegisterOrder_ThrowsIfOrderExists() throws Exception {
		o1.setId(8L);
		orders.registerOrder(o1);
		o2.setId(8L);
		try {
			orders.registerOrder(o2);
			fail("Expected exception: " +
					OrderAlreadyExistsException.class.getSimpleName());
		} catch ( OrderAlreadyExistsException e ) { }
		assertFalse(o2.OnCancelFailed().isListener(orders));
		assertFalse(o2.OnCancelled().isListener(orders));
		assertFalse(o2.OnChanged().isListener(orders));
		assertFalse(o2.OnDone().isListener(orders));
		assertFalse(o2.OnFailed().isListener(orders));
		assertFalse(o2.OnFilled().isListener(orders));
		assertFalse(o2.OnPartiallyFilled().isListener(orders));
		assertFalse(o2.OnRegistered().isListener(orders));
		assertFalse(o2.OnRegisterFailed().isListener(orders));
	}
	
	@Test
	public void testRegisterOrder_ThrowsIfNoIdSet() throws Exception {
		try {
			orders.registerOrder(o1);
			fail("Expected exception: " + OrderException.class.getSimpleName());
		} catch ( OrderException e ) { }
		assertFalse(o1.OnCancelFailed().isListener(orders));
		assertFalse(o1.OnCancelled().isListener(orders));
		assertFalse(o1.OnChanged().isListener(orders));
		assertFalse(o1.OnDone().isListener(orders));
		assertFalse(o1.OnFailed().isListener(orders));
		assertFalse(o1.OnFilled().isListener(orders));
		assertFalse(o1.OnPartiallyFilled().isListener(orders));
		assertFalse(o1.OnRegistered().isListener(orders));
		assertFalse(o1.OnRegisterFailed().isListener(orders));
	}
	
	@Test
	public void testPurgeOrder_ByEditableOrder() throws Exception {
		o1.OnCancelFailed().addListener(orders);
		o1.OnCancelled().addListener(orders);
		o1.OnChanged().addListener(orders);
		o1.OnDone().addListener(orders);
		o1.OnFailed().addListener(orders);
		o1.OnFilled().addListener(orders);
		o1.OnPartiallyFilled().addListener(orders);
		o1.OnRegistered().addListener(orders);
		o1.OnRegisterFailed().addListener(orders);
		o1.setId(8L);
		orders.registerOrder(o1);
		
		orders.purgeOrder(o1);
		
		assertFalse(orders.isOrderExists(8L));
		assertFalse(o1.OnCancelFailed().isListener(orders));
		assertFalse(o1.OnCancelled().isListener(orders));
		assertFalse(o1.OnChanged().isListener(orders));
		assertFalse(o1.OnDone().isListener(orders));
		assertFalse(o1.OnFailed().isListener(orders));
		assertFalse(o1.OnFilled().isListener(orders));
		assertFalse(o1.OnPartiallyFilled().isListener(orders));
		assertFalse(o1.OnRegistered().isListener(orders));
		assertFalse(o1.OnRegisterFailed().isListener(orders));
	}
	
	@Test
	public void testPurgeOrder_ById() throws Exception {
		o1.OnCancelFailed().addListener(orders);
		o1.OnCancelled().addListener(orders);
		o1.OnChanged().addListener(orders);
		o1.OnDone().addListener(orders);
		o1.OnFailed().addListener(orders);
		o1.OnFilled().addListener(orders);
		o1.OnPartiallyFilled().addListener(orders);
		o1.OnRegistered().addListener(orders);
		o1.OnRegisterFailed().addListener(orders);
		o1.setId(8L);
		orders.registerOrder(o1);
		
		orders.purgeOrder(8L);
		
		assertFalse(orders.isOrderExists(8L));
		assertFalse(o1.OnCancelFailed().isListener(orders));
		assertFalse(o1.OnCancelled().isListener(orders));
		assertFalse(o1.OnChanged().isListener(orders));
		assertFalse(o1.OnDone().isListener(orders));
		assertFalse(o1.OnFailed().isListener(orders));
		assertFalse(o1.OnFilled().isListener(orders));
		assertFalse(o1.OnPartiallyFilled().isListener(orders));
		assertFalse(o1.OnRegistered().isListener(orders));
		assertFalse(o1.OnRegisterFailed().isListener(orders));
	}
	
	@Test
	public void testPurgeOrder_ByIdIfNotRegistered() throws Exception {
		orders.purgeOrder(8L);
	}
	
	@Test
	public void testIsPendingOrder() throws Exception {
		o1.setTransactionId(125L);
		orders.registerPendingOrder(o1);
		o2.setId(321L);
		o2.setTransactionId(321L);
		orders.registerOrder(o2);
		
		assertTrue(orders.isPendingOrder(125L));
		assertFalse(orders.isPendingOrder(321L));
	}
	
	@Test
	public void testRegisterPendingOrder_Ok() throws Exception {
		o1.setTransactionId(678L); orders.registerPendingOrder(o1);
		o2.setTransactionId(555L); orders.registerPendingOrder(o2);
		
		assertSame(o1, orders.getPendingOrder(678L));
		assertSame(o2, orders.getPendingOrder(555L));
	}
	
	@Test (expected=OrderAlreadyExistsException.class)
	public void testRegisterPendingOrder_ThrowsIfExists() throws Exception {
		o1.setTransactionId(12L);
		orders.registerPendingOrder(o1);
		o2.setTransactionId(12L);
		orders.registerPendingOrder(o2);
	}
	
	@Test (expected=OrderException.class)
	public void testRegisterPendingOrder_ThrowsIfNoIdSet() throws Exception {
		orders.registerPendingOrder(o1);
	}
	
	@Test
	public void testPurgePendingOrder_ByEditableOrder() throws Exception {
		o1.setTransactionId(123L);
		orders.registerPendingOrder(o1);
		orders.purgePendingOrder(o1);
		assertFalse(orders.isPendingOrder(123L));
	}
	
	@Test
	public void testPurgePendingOrder_ByTransactionId() throws Exception {
		o1.setTransactionId(123L);
		orders.registerPendingOrder(o1);
		orders.purgePendingOrder(123L);
		assertFalse(orders.isPendingOrder(123L));
	}
	
	@Test
	public void testGetPendingOrder_Ok() throws Exception {
		o1.setTransactionId(123L);
		orders.registerPendingOrder(o1);
		assertSame(o1, orders.getPendingOrder(123L));
	}
	
	@Test
	public void testGetPendingOrder_ReturnsNullIfNotExists() throws Exception {
		assertNull(orders.getPendingOrder(321L));
	}

	@Test
	public void testOnEvent_OnCancellFailed() throws Exception {
		final OrderEvent expected = new OrderEvent(onCancelFailed, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderCancelFailed().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnCancelFailed(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnCancelled() throws Exception {
		final OrderEvent expected = new OrderEvent(onCancelled, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderCancelled().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnCancelled(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnChanged() throws Exception {
		final OrderEvent expected = new OrderEvent(onChanged, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderChanged().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnChanged(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testOnEvent_OnDone() throws Exception {
		final OrderEvent expected = new OrderEvent(onDone, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderDone().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnDone(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnFailed() throws Exception {
		final OrderEvent expected = new OrderEvent(onFailed, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderFailed().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnFailed(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnFilled() throws Exception {
		final OrderEvent expected = new OrderEvent(onFilled, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderFilled().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnFilled(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnPartiallyFilled() throws Exception {
		final OrderEvent expected = new OrderEvent(onPartiallyFilled, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderPartiallyFilled().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnPartiallyFilled(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testOnEvent_OnRegistered() throws Exception {
		final OrderEvent expected = new OrderEvent(onRegistered, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderRegistered().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnRegistered(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_OnRegisterFailed() throws Exception {
		final OrderEvent expected = new OrderEvent(onRegisterFailed, o1);
		final CountDownLatch finished = new CountDownLatch(1);
		orders.OnOrderRegisterFailed().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
			
		});
		orders.onEvent(new OrderEvent(o1.OnRegisterFailed(), o1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testGetOrdersCount() throws Exception {
		o1.setId(1L); orders.registerOrder(o1);
		o2.setId(2L); o2.setTransactionId(20L); orders.registerPendingOrder(o2);
		assertEquals(1, orders.getOrdersCount());
		o3.setId(4L); orders.registerOrder(o3);
		assertEquals(2, orders.getOrdersCount());
	}
	
	@Test
	public void testMakePendingOrderAsRegisteredIfExists_IfExists()
			throws Exception
	{
		o1.setTransactionId(125l);
		orders.registerPendingOrder(o1);
		
		o2 = orders.makePendingOrderAsRegisteredIfExists(125l, 876l);
		assertNotNull(o2);
		assertSame(o1, o2);
		assertEquals(new Long(876), o2.getId());
		assertTrue(orders.isOrderExists(876l));
		assertFalse(orders.isPendingOrder(125l));
	}
	
	@Test
	public void testMakePendingOrderAsRegisteredIfExists_IfNotExists()
			throws Exception
	{
		assertNull(orders.makePendingOrderAsRegisteredIfExists(125l, 876l));
	}

}
