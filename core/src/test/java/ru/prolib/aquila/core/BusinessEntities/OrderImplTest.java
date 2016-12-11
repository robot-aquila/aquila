package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
public class OrderImplTest extends ObservableStateContainerImplTest {
	protected static Account account = new Account("port#120");
	protected static Symbol symbol = new Symbol("MSFT");
	protected IMocksControl control;
	private OrderImpl order;
	protected EditableTerminal terminal;
	private OrderController controller;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ObservableStateContainerImplTest.setUpBeforeClass();
	}

	@Before
	public void setUp() throws Exception {
		controller = new OrderController();
		super.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected String getID() {
		return "foobar.port#120[MSFT].ORDER#240";
	}
	
	protected void prepareTerminal() {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getTerminalID()).andStubReturn("foobar");
		expect(terminal.getEventQueue()).andStubReturn(queue);
		control.replay();		
	}
	
	protected void setOrder(OrderImpl order) {
		this.order = order;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		setOrder(order = new OrderImpl(terminal, account, symbol, 240));
		return order;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(ObservableStateContainerImpl.Controller controller) {
		prepareTerminal();
		setOrder(order = new OrderImpl(terminal, account, symbol, 240, controller));
		return order;
	}
	
	protected void assertOrderEvent(Event event, EventType expectedType) {
		OrderEvent e = (OrderEvent) event;
		assertTrue(e.isType(expectedType));
		assertSame(order, e.getOrder());
	}
	
	protected void makeOrderAvailableWithTrueController() {
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
		assertEquals(prefix + ".ARCHIVED", order.onArchived().getId());
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
	public void testGetTimeDone() throws Exception {
		getter = new Getter<Instant>() {
			@Override public Instant get() {
				return order.getTimeDone();
			}
		};
		testGetter(OrderField.TIME_DONE, Instant.parse("1997-08-19T20:54:15Z"),
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
		order.onArchived().addSyncListener(listenerStub);
		order.onArchived().addAlternateType(type);
		
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
		assertFalse(order.onArchived().hasListeners());
		assertFalse(order.onArchived().hasAlternates());
		assertNull(order.getTerminal());
	}
	
	@Test
	public void testOrderController_HasMinimalData() throws Exception {
		produceContainer();
		assertFalse(controller.hasMinimalData(order));
		
		data.put(OrderField.ACTION, OrderAction.BUY);
		data.put(OrderField.TYPE, OrderType.LIMIT);
		data.put(OrderField.STATUS, OrderStatus.PENDING);
		data.put(OrderField.INITIAL_VOLUME, 100L);
		data.put(OrderField.CURRENT_VOLUME, 50L);
		order.update(data);
		
		assertTrue(controller.hasMinimalData(order));
	}
	
	private void testOrderController_ProcessAvailable_Ok(OrderStatus newStatus,
			EventType type) throws Exception
	{
		listenerStub.clear();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addSyncListener(listenerStub);
		
		controller.processAvailable(order);
		
		type.removeListener(listenerStub);
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(type));
		assertSame(order, ((ContainerEvent)listenerStub.getEvent(0)).getContainer());
	}
	
	private void testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus newStatus,
			EventType type) throws Exception
	{
		listenerStub.clear();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addSyncListener(listenerStub);
		order.setStatusEventsEnabled(false);
		
		controller.processAvailable(order);
		
		type.removeListener(listenerStub);
		assertEquals(0, listenerStub.getEventCount());
	}
	
	private void testOrderController_ProcessUpdate_Prepare(OrderStatus newStatus) {
		listenerStub.clear();
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.withToken(OrderField.ACTION, OrderAction.BUY)
			.withToken(OrderField.TYPE, OrderType.LIMIT);
		if ( order.getInitialVolume() == null ) {
			builder.withToken(OrderField.INITIAL_VOLUME, 100L);
		}
		if ( order.getCurrentVolume() == null ) {
			builder.withToken(OrderField.CURRENT_VOLUME, 100L);
		}
		order.consume(builder.buildUpdate());
	}
	
	private void testOrderController_ProcessUpdate_Ok(OrderStatus newStatus,
			EventType type) throws Exception
	{
		testOrderController_ProcessUpdate_Prepare(newStatus);
		type.addSyncListener(listenerStub);
		
		controller.processUpdate(order);
		
		type.removeListener(listenerStub);
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(type));
		assertSame(order, ((ContainerEvent)listenerStub.getEvent(0)).getContainer());
	}
	
	private void testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus newStatus,
			EventType type) throws Exception
	{
		listenerStub.clear();
		assertFalse(order.isAvailable());
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addSyncListener(listenerStub);
		assertFalse(order.isAvailable());
		
		controller.processUpdate(order);
		
		type.removeListener(listenerStub);
		assertEquals(0, listenerStub.getEventCount());
	}
	
	private void testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus newStatus,
			EventType type) throws Exception
	{
		testOrderController_ProcessUpdate_Prepare(newStatus);
		order.setStatusEventsEnabled(false);
		type.addSyncListener(listenerStub);
		
		controller.processUpdate(order);
		
		type.removeListener(listenerStub);
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test
	public void testOrderController_ProcessAvailable_Ok() throws Exception {
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.CANCEL_FAILED, order.onCancelFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.CANCELLED, order.onCancelled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.FILLED, order.onDone());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.CANCEL_FAILED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.REJECTED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.FILLED, order.onFilled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.ACTIVE, order.onRegistered());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_Ok(OrderStatus.REJECTED, order.onRegisterFailed());
		tearDown();
		
		setUp();
		produceContainer();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.CURRENT_VOLUME, 5L)
			.buildUpdate());
		testOrderController_ProcessAvailable_Ok(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}
	
	@Test
	public void testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled() throws Exception {
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.CANCEL_FAILED, order.onCancelFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.CANCELLED, order.onCancelled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.FILLED, order.onDone());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.CANCEL_FAILED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.REJECTED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();		
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.FILLED, order.onFilled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.ACTIVE, order.onRegistered());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.REJECTED, order.onRegisterFailed());
		tearDown();
		
		setUp();
		produceContainer();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.CURRENT_VOLUME, 5L)
			.buildUpdate());
		testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}
	
	@Test
	public void testOrderController_ProcessUpdate_Ok() throws Exception {
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.CANCEL_FAILED, order.onCancelFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.CANCELLED, order.onCancelled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.FILLED, order.onDone());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.CANCEL_FAILED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.REJECTED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.FILLED, order.onFilled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.ACTIVE, order.onRegistered());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_Ok(OrderStatus.REJECTED, order.onRegisterFailed());
		tearDown();
		
		setUp();
		produceContainer();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.CURRENT_VOLUME, 5L)
			.buildUpdate());
		testOrderController_ProcessUpdate_Ok(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}
	
	@Test
	public void testOrderController_ProcessUpdate_SkipIfNotAvailable() throws Exception {
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.CANCEL_FAILED, order.onCancelFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.CANCELLED, order.onCancelled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.FILLED, order.onDone());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.CANCEL_FAILED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.REJECTED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.FILLED, order.onFilled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.ACTIVE, order.onRegistered());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.REJECTED, order.onRegisterFailed());
		tearDown();
		
		setUp();
		produceContainer();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.CURRENT_VOLUME, 5L)
			.buildUpdate());
		testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}
	
	@Test
	public void testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled() throws Exception {
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.CANCEL_FAILED, order.onCancelFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.CANCELLED, order.onCancelled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.FILLED, order.onDone());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.CANCEL_FAILED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.REJECTED, order.onFailed());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.FILLED, order.onFilled());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.ACTIVE, order.onRegistered());
		tearDown();
		
		setUp();
		produceContainer();
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.REJECTED, order.onRegisterFailed());
		tearDown();
		
		setUp();
		produceContainer();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.INITIAL_VOLUME, 10L)
			.withToken(OrderField.CURRENT_VOLUME, 5L)
			.buildUpdate());
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}
	
	@Test
	public void testUpdate_OnAvailable() throws Exception {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		controllerMock.processUpdate(container);
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
		controllerMock.processUpdate(container);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		controllerMock.processUpdate(container);
		getMocksControl().replay();
		
		data.put(12345, 415);
		order.update(data);
		data.put(12345, 450);
		order.update(data);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(container.onUpdate()));
		assertSame(order, ((OrderEvent) listenerStub.getEvent(0)).getOrder());
		assertTrue(listenerStub.getEvent(1).isType(container.onUpdate()));
		assertSame(order, ((OrderEvent) listenerStub.getEvent(1)).getOrder());
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
	protected OrderExecution newExec(long id, String externalID, Instant time,
			double price, long volume, double value)
	{
		return new OrderExecutionImpl(terminal, id, externalID, symbol,
			order.getAction(), 240L, time, price, volume, value);
	}
	
	@Test
	public void testAddExecution6() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.onExecution().addSyncListener(listenerStub);
		
		OrderExecution actual = order.addExecution(100L, "x1", now, 80.0d, 10L, 800.0d);
		
		OrderExecution expected = newExec(100L, "x1", now, 80.0d, 10L, 800.0d);
		assertEquals(expected, actual);
		assertEquals(expected, order.getExecution(100L));
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test (expected=OrderException.class)
	public void testAddExecution6_ThrowsIfAlreadyExists() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		order.addExecution(100L, "foo1", now, 34.15d, 10L, 341.50d);
		
		order.addExecution(100L, "foo1", now, 34.15d, 10L, 341.50d);
	}
	
	@Test
	public void testGetExecution() throws Exception {
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		Instant now = Instant.now();
		order.addExecution(100L, "foo1", now,				 34.15d, 10L, 341.50d);
		order.addExecution(101L, "foo2", now.plusMillis(1), 34.25d, 20L, 683.00d);
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
	public void testFireArchived() throws Exception {
		order.onArchived().addSyncListener(listenerStub);
		
		order.fireArchived();
		
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onArchived());
	}
	
	@Test
	public void testAddExecution1() throws Exception {
		Instant time = Instant.EPOCH;
		OrderExecution execution = newExec(2005, "foo2005", time, 112.34d, 10L, 1123.40d);
		order.onExecution().addSyncListener(listenerStub);
		
		order.addExecution(execution);
		
		assertFalse(order.hasChanged());
		assertEquals(0, listenerStub.getEventCount());
		assertEquals(execution, order.getExecution(2005));
	}
	
	@Test (expected=OrderException.class)
	public void testAddExecution1_ThrowsIfAlreadyExists() throws Exception {
		Instant time = Instant.EPOCH;
		
		order.addExecution(newExec(2008, "x", time, 48.15d, 1L, 48.15d));
		order.addExecution(newExec(2008, "y", time, 12.78d, 1L, 12.78d));
	}
	
	@Test
	public void testFireExecutionAdded() throws Exception {
		Instant time = Instant.now();
		OrderExecution execution = newExec(512, null, time, 45.14d, 1L, 45.14d);
		order.onExecution().addSyncListener(listenerStub);
		
		order.fireExecution(execution);
		
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onExecution());
		assertEquals(execution, ((OrderExecutionEvent) listenerStub.getEvent(0)).getExecution());
	}
	
	@Test
	public void testGetExecutions() throws Exception {
		Instant time = Instant.now();
		OrderExecution exec1 = newExec(501, null, time, 10.00d, 1L, 20.00d);
		OrderExecution exec2 = newExec(502, null, time, 10.10d, 5L, 40.00d);
		OrderExecution exec3 = newExec(503, "xo", time, 10.20d, 2L, 50.00d);
		order.addExecution(exec1);
		order.addExecution(exec2);
		order.addExecution(exec3);
		
		List<OrderExecution> expected = new ArrayList<>();
		expected.add(exec1);
		expected.add(exec2);
		expected.add(exec3);
		assertEquals(expected, order.getExecutions());
	}

}
