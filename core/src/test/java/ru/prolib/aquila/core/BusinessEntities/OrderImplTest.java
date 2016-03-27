package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private void makeOrderAvailableWithTrueController() {
		produceContainer();
		data.put(OrderField.ACTION, OrderAction.BUY);
		data.put(OrderField.TYPE, OrderType.LIMIT);
		data.put(OrderField.STATUS, OrderStatus.PENDING);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		data.put(OrderField.CURRENT_VOLUME, 10L);
		order.update(data);
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
		assertEquals(prefix + ".EXECUTION", order.onExecution().getId());
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
	public void testGetSystemMessage() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return order.getSystemMessage();
			}
		};
		testGetter(OrderField.SYSTEM_MESSAGE, "foo", "bar");
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
	public void testOrderController_CancelFailed_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
		order.update(data);
		order.onCancelFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_Cancelled_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.CANCELLED);
		order.update(data);
		order.onCancelled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_Done_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.FILLED);
		order.update(data);
		order.onDone().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_Failed_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
		order.update(data);
		order.onFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_Filled_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.FILLED);
		order.update(data);
		order.onFilled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_PartiallyFilled_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.CANCELLED);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		data.put(OrderField.CURRENT_VOLUME, 5L);
		order.update(data);
		order.onPartiallyFilled().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_Registered_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.ACTIVE);
		order.update(data);
		order.onRegistered().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	public void testOrderController_RegisterFailed_SkipWhenStatusEventsDisabled() throws Exception {
		order.setStatusEventsEnabled(false);
		data.put(OrderField.STATUS, OrderStatus.REJECTED);
		order.update(data);
		order.onRegisterFailed().addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		controller.processUpdate(order);
		
		assertEquals(0, listenerStub.getEventCount());
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
	
	/**
	 * Create test execution.
	 * <p>
	 * @param id - execution id
	 * @param externalID - external ID
	 * @param time - execution time
	 * @param price - price per unit
	 * @param volume - executed volume
	 * @param value - executed value
	 * @return execution instance
	 */
	private OrderExecution newExec(long id, String externalID, Instant time,
			double price, long volume, double value)
	{
		return new OrderExecutionImpl(terminal, id, externalID, symbol,
			order.getAction(), 240L, time, price, volume, value);
	}
	
	@Test
	public void testAddExecution() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.onExecution().addSyncListener(listenerStub);
		
		order.addExecution(100L, "x1", now, 80.0d, 10L, 800.0d);
		
		List<OrderExecution> expected = new ArrayList<>();
		expected.add(newExec(100L, "x1", now, 80.0d, 10L, 800.0d));
		assertEquals(expected, order.getExecutions());
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onExecution());
		assertEquals(expected.get(0), ((OrderExecutionEvent) listenerStub.getEvent(0)).getExecution());
	}
	
	@Test (expected=OrderException.class)
	public void testAddExecution_ThrowsIfAlreadyExists() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		order.loadExecution(100L, "foo1", now, 34.15d, 10L, 341.50d);
		
		order.addExecution(100L, "foo1", now, 34.15d, 10L, 341.50d);
	}
	
	@Test
	public void testLoadExecution() throws Exception {
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		Instant now = Instant.now();
		order.onExecution().addSyncListener(listenerStub);

		order.loadExecution(100L, "foo1", now,				 34.15d, 10L, 341.50d);
		order.loadExecution(101L, "foo2", now.plusMillis(1), 34.25d, 20L, 683.00d);
		order.loadExecution(102L, "foo3", now.plusMillis(5), 34.43d, 10L, 344.30d);
		
		List<OrderExecution> expected = new ArrayList<>();
		expected.add(newExec(100L, "foo1", now,				  34.15d, 10L, 341.50d));
		expected.add(newExec(101L, "foo2", now.plusMillis(1), 34.25d, 20L, 683.00d));
		expected.add(newExec(102L, "foo3", now.plusMillis(5), 34.43d, 10L, 344.30d));
		assertEquals(expected, order.getExecutions());
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test (expected=OrderException.class)
	public void testLoadExecution_ThrowsIfAlreadyExists() throws Exception {
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		Instant now = Instant.now();
		order.loadExecution(100L, "foo1", now, 34.15d, 10L, 341.50d);
		
		order.loadExecution(100L, "foo2", now, 34.25d, 20L, 683.00d);
	}
	
	@Test
	public void testGetExecution() throws Exception {
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		Instant now = Instant.now();
		order.loadExecution(100L, "foo1", now,				 34.15d, 10L, 341.50d);
		order.loadExecution(101L, "foo2", now.plusMillis(1), 34.25d, 20L, 683.00d);
		List<OrderExecution> executions = order.getExecutions();
		
		assertSame(executions.get(0), order.getExecution(100L));
		assertSame(executions.get(1), order.getExecution(101L));
	}
	
	@Test (expected=OrderException.class)
	public void testGetExecution_ThrowsIfExecutionNotExists() throws Exception {
		order.getExecution(834L);
	}
	
	@Test
	public void testSetStatusEventsEnabled() throws Exception {
		assertTrue(order.isStatusEventsEnabled());
		order.setStatusEventsEnabled(false);
		assertFalse(order.isStatusEventsEnabled());
		order.setStatusEventsEnabled(true);
		assertTrue(order.isStatusEventsEnabled());
	}
	
	@Test
	public void testGetChangeWhenExecutionAdded() throws Exception {
		data.put(OrderField.ACTION, OrderAction.SELL);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		order.update(data);
		Instant now = Instant.now();
		order.loadExecution(1005L, "x1", now,				70.10d, 5L, 100.0d);
		order.loadExecution(1006L, "x2", now.plusMillis(1), 70.95d, 2L, 213.00d);
		
		Map<Integer, Object> actual = order.getChangeWhenExecutionAdded();
		
		Map<Integer, Object> expected = new HashMap<Integer, Object>();
		expected.put(OrderField.CURRENT_VOLUME, 3L);
		expected.put(OrderField.EXECUTED_VALUE, 313.0d);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetChangeWhenExecutionAdded_WhenFullyFilled() throws Exception {
		data.put(OrderField.ACTION, OrderAction.SELL);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		order.update(data);
		Instant now = Instant.now();
		order.loadExecution(1005L, "x1", now,				70.10d, 5L, 100.0d);
		order.loadExecution(1006L, "x2", now.plusMillis(1), 70.95d, 2L, 213.00d);
		order.loadExecution(1007L, "x3", now.plusMillis(2), 70.82d, 3L, 205.00d);
		
		Map<Integer, Object> actual = order.getChangeWhenExecutionAdded();
		
		Map<Integer, Object> expected = new HashMap<Integer, Object>();
		expected.put(OrderField.CURRENT_VOLUME, 0L);
		expected.put(OrderField.EXECUTED_VALUE, 518.0d);
		expected.put(OrderField.STATUS, OrderStatus.FILLED);
		expected.put(OrderField.DONE_TIME, now.plusMillis(2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetChangeWhenCancelled() throws Exception {
		Instant time = Instant.now();
		data.put(OrderField.ACTION, OrderAction.SELL);
		data.put(OrderField.INITIAL_VOLUME, 10L);
		data.put(OrderField.CURRENT_VOLUME, 10L);
		order.update(data);
		
		Map<Integer, Object> actual = order.getChangeWhenCancelled(time);
		
		Map<Integer, Object> expected = new HashMap<>();
		expected.put(OrderField.STATUS, OrderStatus.CANCELLED);
		expected.put(OrderField.DONE_TIME, time);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetChangeWhenRejected() throws Exception {
		Instant time = Instant.now();
		
		Map<Integer, Object> actual = order.getChangeWhenRejected(time, "rejected");
		
		Map<Integer, Object> expected = new HashMap<>();
		expected.put(OrderField.STATUS, OrderStatus.REJECTED);
		expected.put(OrderField.DONE_TIME, time);
		expected.put(OrderField.SYSTEM_MESSAGE, "rejected");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetChangeWhenRegistered() throws Exception {
		
		Map<Integer, Object> actual = order.getChangeWhenRegistered();
		
		Map<Integer, Object> expected = new HashMap<>();
		expected.put(OrderField.STATUS, OrderStatus.ACTIVE);
		assertEquals(expected, actual);
	}

	@Test
	public void testGetChangeWhenCancelFailed() throws Exception {
		Instant time = Instant.now();
		
		Map<Integer, Object> actual = order.getChangeWhenCancelFailed(time, "some error");
		
		Map<Integer, Object> expected = new HashMap<>();
		expected.put(OrderField.STATUS, OrderStatus.CANCEL_FAILED);
		expected.put(OrderField.SYSTEM_MESSAGE, "some error");
		expected.put(OrderField.DONE_TIME, time);
		assertEquals(expected, actual);
	}

	@Test
	public void testUpdateWhenExecutionAdded() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant now = Instant.now();
		order.loadExecution(1005L, "x1", now,				70.10d, 5L, 100.0d);
		order.loadExecution(1006L, "x2", now.plusMillis(1), 70.95d, 2L, 213.00d);
		order.onUpdate().addSyncListener(listenerStub);
		order.onFilled().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);

		order.updateWhenExecutionAdded();
		
		assertEquals(3L, (long)order.getCurrentVolume());
		assertEquals(313.0d, order.getExecutedValue(), 0.01d);
		assertEquals(OrderStatus.PENDING, order.getStatus()); // not changed
		assertNull(order.getDoneTime());
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
	}
	
	@Test
	public void testUpdateWhenExecutionAdded_WhenFullyFilled() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant now = Instant.now();
		order.loadExecution(1005L, "x1", now,				70.10d, 5L, 100.0d);
		order.loadExecution(1006L, "x2", now.plusMillis(1), 70.95d, 2L, 213.00d);
		order.loadExecution(1007L, "x3", now.plusMillis(2), 70.82d, 3L, 205.00d);
		order.onUpdate().addSyncListener(listenerStub);
		order.onFilled().addSyncListener(listenerStub);
		order.onFailed().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);
		
		order.updateWhenExecutionAdded();

		assertEquals(0L, (long)order.getCurrentVolume());
		assertEquals(518.0d, order.getExecutedValue(), 0.01d);
		assertEquals(OrderStatus.FILLED, order.getStatus());
		assertEquals(now.plusMillis(2), order.getDoneTime());
		assertEquals(3, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onFilled());
		assertOrderEvent(listenerStub.getEvent(2), order.onDone());
	}

	@Test
	public void testUpdateWhenCancelled() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant now = Instant.now();
		order.onUpdate().addSyncListener(listenerStub);
		order.onCancelled().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);
		
		order.updateWhenCancelled(now);
		
		assertEquals(10L, (long)order.getCurrentVolume());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());
		assertEquals(now, order.getDoneTime());
		assertEquals(3, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onCancelled());
		assertOrderEvent(listenerStub.getEvent(2), order.onDone());
	}
	
	@Test
	public void testUpdateWhenCancelled_PartiallyFilled() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant now = Instant.now();
		data.put(OrderField.CURRENT_VOLUME, 5L);
		order.update(data);
		order.onUpdate().addSyncListener(listenerStub);
		order.onCancelled().addSyncListener(listenerStub);
		order.onPartiallyFilled().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);
		
		order.updateWhenCancelled(now.plusMillis(10));
		
		assertEquals(5L, (long)order.getCurrentVolume());
		assertEquals(OrderStatus.CANCELLED, order.getStatus());
		assertEquals(now.plusMillis(10), order.getDoneTime());
		assertEquals(3, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onPartiallyFilled());
		assertOrderEvent(listenerStub.getEvent(2), order.onDone());
	}

	@Test
	public void testUpdateWhenRejected() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant now = Instant.now();
		order.onUpdate().addSyncListener(listenerStub);
		order.onRegisterFailed().addSyncListener(listenerStub);
		order.onFailed().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);
		
		order.updateWhenRejected(now, "insufficient funds");
		
		assertEquals(OrderStatus.REJECTED, order.getStatus());
		assertEquals(now, order.getDoneTime());
		assertEquals("insufficient funds", order.getSystemMessage());
		assertEquals(4, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onRegisterFailed());
		assertOrderEvent(listenerStub.getEvent(2), order.onFailed());
		assertOrderEvent(listenerStub.getEvent(3), order.onDone());
	}
	
	@Test
	public void testUpdateWhenRegistered() throws Exception {
		makeOrderAvailableWithTrueController();
		order.onUpdate().addSyncListener(listenerStub);
		order.onRegistered().addSyncListener(listenerStub);
		
		order.updateWhenRegistered();
		
		assertEquals(OrderStatus.ACTIVE, order.getStatus());
		assertEquals(2, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onRegistered());
	}
	
	@Test
	public void testUpdateWhenCancelFailed() throws Exception {
		makeOrderAvailableWithTrueController();
		Instant time = Instant.parse("2017-01-01T00:00:00Z");
		order.onUpdate().addSyncListener(listenerStub);
		order.onCancelFailed().addSyncListener(listenerStub);
		order.onFailed().addSyncListener(listenerStub);
		order.onDone().addSyncListener(listenerStub);
		
		order.updateWhenCancelFailed(time, "test error");

		assertEquals(OrderStatus.CANCEL_FAILED, order.getStatus());
		assertEquals("test error", order.getSystemMessage());
		assertEquals(time, order.getDoneTime());
		assertEquals(4, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onUpdate());
		assertOrderEvent(listenerStub.getEvent(1), order.onCancelFailed());
		assertOrderEvent(listenerStub.getEvent(2), order.onFailed());
		assertOrderEvent(listenerStub.getEvent(3), order.onDone());
	}

}
