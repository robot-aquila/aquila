package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl.OrderController;

/**
 * 2012-09-22<br>
 * $Id: OrderImplTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderImplTest extends ContainerImplTest {
	private static Account account = new Account("port#120");
	private static Symbol symbol = new Symbol("MSFT");
	private IMocksControl control;
	private OrderImpl order;
	private EditableTerminal terminal;
	private OrderController controller;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ContainerImplTest.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
		controller = new OrderController();
		super.setUp();
	}
	
	@Test
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected String getID() {
		return "foobar.port#120[MSFT].ORDER#240";
	}
	
	private void prepareTerminal() {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getTerminalID()).andStubReturn("foobar");
		expect(terminal.getEventQueue()).andStubReturn(queue);
		control.replay();		
	}
	
	@Override
	protected ContainerImpl produceContainer() {
		prepareTerminal();
		order = new OrderImpl(terminal, account, symbol, 240);
		return order;
	}
	
	@Override
	protected ContainerImpl produceContainer(ContainerImpl.Controller controller) {
		prepareTerminal();
		order = new OrderImpl(terminal, account, symbol, 240, controller);
		return order;
	}
	
	private void assertOrderEvent(Event event, EventType expectedType) {
		OrderEvent e = (OrderEvent) event;
		assertTrue(e.isType(expectedType));
		assertSame(order, e.getOrder());
	}
	
	@Test
	public void testCtor_DefaultContainer() throws Exception {
		order = new OrderImpl(terminal, account, symbol, 240);
		assertEquals(OrderController.class, order.getController().getClass());
		assertNotNull(order.getTerminal());
		assertNotNull(order.getEventQueue());
		assertSame(terminal, order.getTerminal());
		assertSame(queue, order.getEventQueue());
		assertEquals(account, order.getAccount());
		assertEquals(symbol, order.getSymbol());
		String prefix = getID();
		assertEquals(prefix, order.getContainerID());
		assertEquals(prefix + ".CANCEL_FAILED", order.onCancelFailed().getId());
		assertEquals(prefix + ".CANCELLED", order.onCancelled().getId());
		assertEquals(prefix + ".DEAL", order.onExecution().getId());
		assertEquals(prefix + ".DONE", order.onDone().getId());
		assertEquals(prefix + ".FAILED", order.onFailed().getId());
		assertEquals(prefix + ".FILLED", order.onFilled().getId());
		assertEquals(prefix + ".PARTIALLY_FILLED", order.onPartiallyFilled().getId());
		assertEquals(prefix + ".REGISTERED", order.onRegistered().getId());
		assertEquals(prefix + ".REGISTER_FAILED", order.onRegisterFailed().getId());
		assertEquals(240, order.getID());
	}
	
	@Test
	public void testGetAction() throws Exception {
		getter = new Getter<OrderAction>() {
			@Override public OrderAction get() {
				return order.getAction();
			}
		};
		testGetter(OrderField.ACTION, OrderAction.BUY, OrderAction.SELL_SHORT);
	}

	@Test
	public void testGetComment() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return order.getComment();
			}
		};
		testGetter(OrderField.COMMENT, "foo", "bar");
	}
	
	@Test
	public void testGetCurrentVolume() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return order.getCurrentVolume();
			}			
		};
		testGetter(OrderField.CURRENT_VOLUME, 214L, 178L);
	}
	
	@Test
	public void testGetDoneTime() throws Exception {
		getter = new Getter<Instant>() {
			@Override public Instant get() {
				return order.getDoneTime();
			}
		};
		testGetter(OrderField.DONE_TIME, Instant.parse("1997-08-19T20:54:15Z"),
				Instant.parse("2045-01-15T00:19:34Z"));
	}

	@Test
	public void testGetExecutedValue() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return order.getExecutedValue();
			}			
		};
		testGetter(OrderField.EXECUTED_VALUE, 14052.13d, 16480.15d);
	}
	
	@Test
	public void testGetExternalID() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return order.getExternalID();
			}
		};
		testGetter(OrderField.EXTERNAL_ID, "foo140", "foo250");
	}
	
	@Test
	public void testGetInitialVolume() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return order.getInitialVolume();
			}			
		};
		testGetter(OrderField.INITIAL_VOLUME, 1000L, 450L);
	}
	
	@Test
	public void testGetPrice() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return order.getPrice();
			}			
		};
		testGetter(OrderField.PRICE, 230.45d, 245.13d);
	}
	
	@Test
	public void testGetStatus() throws Exception {
		getter = new Getter<OrderStatus>() {
			@Override public OrderStatus get() {
				return order.getStatus();
			}			
		};
		testGetter(OrderField.STATUS, OrderStatus.ACTIVE, OrderStatus.FILLED);
	}
	
	@Test
	public void testGetTime() throws Exception {
		getter = new Getter<Instant>() {
			@Override public Instant get() {
				return order.getTime();
			}
		};
		testGetter(OrderField.TIME, Instant.now(), Instant.parse("1992-12-25T00:00:00Z"));
	}
	
	@Test
	public void testGetType() throws Exception {
		getter = new Getter<OrderType>() {
			@Override public OrderType get() {
				return order.getType();
			}
		};
		testGetter(OrderField.TYPE, OrderType.LIMIT, OrderType.MARKET);
	}
	
	@Test
	public void testClose() {
		EventType type = new EventTypeImpl();
		order.onAvailable().addSyncListener(listenerStub);
		order.onAvailable().addAlternateType(type);
		order.onCancelFailed().addSyncListener(listenerStub);
		order.onCancelFailed().addAlternateType(type);
		order.onCancelled().addSyncListener(listenerStub);
		order.onCancelled().addAlternateType(type);
		order.onExecution().addSyncListener(listenerStub);
		order.onExecution().addAlternateType(type);
		order.onDone().addSyncListener(listenerStub);
		order.onDone().addAlternateType(type);
		order.onFailed().addSyncListener(listenerStub);
		order.onFailed().addAlternateType(type);
		order.onFilled().addSyncListener(listenerStub);
		order.onFilled().addAlternateType(type);
		order.onPartiallyFilled().addSyncListener(listenerStub);
		order.onPartiallyFilled().addAlternateType(type);
		order.onRegistered().addSyncListener(listenerStub);
		order.onRegistered().addAlternateType(type);
		order.onRegisterFailed().addSyncListener(listenerStub);
		order.onRegisterFailed().addAlternateType(type);
		order.onUpdate().addSyncListener(listenerStub);
		order.onUpdate().addAlternateType(type);
		
		order.close();
		
		assertFalse(order.onAvailable().hasListeners());
		assertFalse(order.onAvailable().hasAlternates());
		assertFalse(order.onCancelFailed().hasListeners());
		assertFalse(order.onCancelFailed().hasAlternates());
		assertFalse(order.onCancelled().hasListeners());
		assertFalse(order.onCancelled().hasAlternates());
		assertFalse(order.onExecution().hasListeners());
		assertFalse(order.onExecution().hasAlternates());
		assertFalse(order.onDone().hasListeners());
		assertFalse(order.onDone().hasAlternates());
		assertFalse(order.onFailed().hasListeners());
		assertFalse(order.onFailed().hasAlternates());
		assertFalse(order.onFilled().hasListeners());
		assertFalse(order.onFilled().hasAlternates());
		assertFalse(order.onPartiallyFilled().hasListeners());
		assertFalse(order.onPartiallyFilled().hasAlternates());
		assertFalse(order.onRegistered().hasListeners());
		assertFalse(order.onRegistered().hasAlternates());
		assertFalse(order.onRegisterFailed().hasListeners());
		assertFalse(order.onRegisterFailed().hasAlternates());
		assertFalse(order.onUpdate().hasListeners());
		assertFalse(order.onUpdate().hasAlternates());
		assertNull(order.getTerminal());
	}
	
	@Test
	public void testOrderController_HasMinimalData() throws Exception {
		assertFalse(controller.hasMinimalData(order));
		
		data.put(OrderField.ACTION, OrderAction.BUY);
		data.put(OrderField.TYPE, OrderType.LIMIT);
		data.put(OrderField.STATUS, OrderStatus.PENDING);
		data.put(OrderField.INITIAL_VOLUME, 100L);
		data.put(OrderField.CURRENT_VOLUME, 50L);
		order.update(data);
		
		assertTrue(controller.hasMinimalData(order));
	}

	@Test
	public void testOrderController_CancelFailed() {
		data.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
		order.update(data);
		order.onCancelFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onCancelFailed());
		assertOrderEvent(listenerStub.getEvent(1), order.onCancelFailed());
	}
	
	@Test
	public void testOrderController_Cancelled() {
		data.put(OrderField.STATUS, OrderStatus.CANCELLED);
		order.update(data);
		order.onCancelled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onCancelled());
		assertOrderEvent(listenerStub.getEvent(1), order.onCancelled());
	}
	
	@Test
	public void testOrderController_Done() {
		data.put(OrderField.STATUS, OrderStatus.FILLED);
		order.update(data);
		order.onDone().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onDone());
		assertOrderEvent(listenerStub.getEvent(1), order.onDone());
	}
	
	@Test
	public void testOrderController_Failed() {
		data.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
		order.update(data);
		order.onFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onFailed());
		assertOrderEvent(listenerStub.getEvent(1), order.onFailed());
	}
	
	@Test
	public void testOrderController_Filled() {
		data.put(OrderField.STATUS, OrderStatus.FILLED);
		order.update(data);
		order.onFilled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onFilled());
		assertOrderEvent(listenerStub.getEvent(1), order.onFilled());
	}

	@Test
	public void testOrderController_PartiallyFilled() {
		data.put(OrderField.STATUS, OrderStatus.CANCELLED);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		data.put(OrderField.CURRENT_VOLUME, 5L);
		order.update(data);
		order.onPartiallyFilled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onPartiallyFilled());
		assertOrderEvent(listenerStub.getEvent(1), order.onPartiallyFilled());
	}
	
	@Test
	public void testOrderController_Registered() {
		data.put(OrderField.STATUS, OrderStatus.ACTIVE);
		order.update(data);
		order.onRegistered().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onRegistered());
		assertOrderEvent(listenerStub.getEvent(1), order.onRegistered());
	}
	
	@Test
	public void testOrderController_RegisterFailed() {
		data.put(OrderField.STATUS, OrderStatus.REJECTED);
		order.update(data);
		order.onRegisterFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onRegisterFailed());
		assertOrderEvent(listenerStub.getEvent(1), order.onRegisterFailed());
	}
	
	@Test
	public void testUpdate_OnAvailable() throws Exception {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		getMocksControl().replay();

		data.put(12345, 415); // any value
		order.update(data);
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		OrderEvent event = (OrderEvent) listenerStub.getEvent(0);
		assertTrue(event.isType(order.onAvailable()));
		assertSame(order, event.getOrder());
	}

	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		container = produceContainer(controllerMock);
		container.onUpdate().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		controllerMock.processUpdate(container);
		getMocksControl().replay();
		
		data.put(12345, 415);
		order.update(data);
		data.put(12345, 450);
		order.update(data);

		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		OrderEvent event = (OrderEvent) listenerStub.getEvent(0);
		assertTrue(event.isType(order.onUpdate()));
		assertSame(order, event.getOrder());
	}

}
