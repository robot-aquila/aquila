package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl.OrderController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParamsBuilder;
import ru.prolib.aquila.core.data.DataProviderStub;

/**
 * 2012-09-22<br>
 * $Id: OrderImplTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderImplTest extends ObservableStateContainerImplTest {
	protected static Account account = new Account("port#120");
	protected static Symbol symbol = new Symbol("MSFT");
	private OrderImpl order;
	private SchedulerStub schedulerStub;
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
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withTerminalID("foobar")
				.withEventQueue(queue)
				.withScheduler(schedulerStub)
				.buildTerminal();
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);
	}
	
	protected void setOrder(OrderImpl order) {
		this.order = order;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		setOrder(order = new OrderImpl(new OrderParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(240L)
				.buildParams()));
		return order;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(OSCController controller) {
		prepareTerminal();
		setOrder(order = new OrderImpl(new OrderParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(240L)
				.withController(controller)
				.buildParams()));
		return order;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(EventDispatcher eventDispatcher,
			OSCController controller)
	{
		prepareTerminal();
		setOrder(order = new OrderImpl(new OrderParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(240L)
				.withController(controller)
				.withEventDispatcher(eventDispatcher)
				.buildParams()));
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
		data.put(OrderField.TYPE, OrderType.LMT);
		data.put(OrderField.STATUS, OrderStatus.PENDING);
		data.put(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L));
		data.put(OrderField.CURRENT_VOLUME, CDecimalBD.of(10L));
		order.update(data);
	}
	
	@Test
	public void testCtor_DefaultContainer() throws Exception {
		produceContainer();
		assertEquals(OrderController.class, order.getController().getClass());
		assertNotNull(order.getTerminal());
		assertNotNull(order.getEventDispatcher());
		assertSame(terminal, order.getTerminal());
		assertSame(queue, ((EventDispatcherImpl) order.getEventDispatcher()).getEventQueue());
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
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return order.getCurrentVolume();
			}			
		};
		testGetter(OrderField.CURRENT_VOLUME, CDecimalBD.of(214L), CDecimalBD.of(178L));
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
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return order.getExecutedValue();
			}			
		};
		testGetter(OrderField.EXECUTED_VALUE,
				CDecimalBD.of("14052.13", "RUB"), CDecimalBD.of("16480.15", "RUB"));
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
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return order.getInitialVolume();
			}			
		};
		testGetter(OrderField.INITIAL_VOLUME, CDecimalBD.of(1000L), CDecimalBD.of(450L));
	}
	
	@Test
	public void testGetPrice() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return order.getPrice();
			}			
		};
		testGetter(OrderField.PRICE, CDecimalBD.of("230.45"), CDecimalBD.of("245.13"));
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
		testGetter(OrderField.TYPE, OrderType.LMT, OrderType.MKT);
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
	public void testGetUserDefinedLong() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return order.getUserDefinedLong();
			}
		};
		testGetter(OrderField.USER_DEFINED_LONG, 215L, 436L);
	}
	
	@Test
	public void testGetUserDefinedString() throws Exception {
		getter = new Getter<String>() {
			@Override
			public String get() {
				return order.getUserDefinedString();
			}
		};
		testGetter(OrderField.USER_DEFINED_STRING, "foo", "bar");
	}
	
	@Test
	public void testClose() {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T18:16:00Z");
		order.onClose().addListener(listenerStub);
		EventType type = new EventTypeImpl();
		order.onAvailable().addListener(listenerStub);
		order.onAvailable().addAlternateType(type);
		order.onCancelFailed().addListener(listenerStub);
		order.onCancelFailed().addAlternateType(type);
		order.onCancelled().addListener(listenerStub);
		order.onCancelled().addAlternateType(type);
		order.onExecution().addListener(listenerStub);
		order.onExecution().addAlternateType(type);
		order.onDone().addListener(listenerStub);
		order.onDone().addAlternateType(type);
		order.onFailed().addListener(listenerStub);
		order.onFailed().addAlternateType(type);
		order.onFilled().addListener(listenerStub);
		order.onFilled().addAlternateType(type);
		order.onPartiallyFilled().addListener(listenerStub);
		order.onPartiallyFilled().addAlternateType(type);
		order.onRegistered().addListener(listenerStub);
		order.onRegistered().addAlternateType(type);
		order.onRegisterFailed().addListener(listenerStub);
		order.onRegisterFailed().addAlternateType(type);
		order.onUpdate().addListener(listenerStub);
		order.onUpdate().addAlternateType(type);
		order.onArchived().addListener(listenerStub);
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
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0),
				OrderEvent.class,
				order.onClose(),
				order,
				T("2017-08-04T18:16:00Z"));
	}
	
	@Test
	public void testOrderController_HasMinimalData() throws Exception {
		Instant time = Instant.EPOCH;
		produceContainer();
		assertFalse(controller.hasMinimalData(order, time));
		
		data.put(OrderField.ACTION, OrderAction.BUY);
		data.put(OrderField.TYPE, OrderType.LMT);
		data.put(OrderField.STATUS, OrderStatus.PENDING);
		data.put(OrderField.INITIAL_VOLUME, CDecimalBD.of(100L));
		data.put(OrderField.CURRENT_VOLUME, CDecimalBD.of(50L));
		order.update(data);
		
		assertTrue(controller.hasMinimalData(order, time));
	}
	
	private void testOrderController_ProcessAvailable_Ok(OrderStatus newStatus,
			EventType type) throws Exception
	{
		Instant time = T("2017-08-04T02:40:00Z");
		listenerStub.clear();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addListener(listenerStub);
		
		controller.processAvailable(order, time);
		
		type.removeListener(listenerStub);
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0), OrderEvent.class, type, order, time);
	}
	
	private void testOrderController_ProcessAvailable_SkipIfStatusEventsDisabled(OrderStatus newStatus,
			EventType type) throws Exception
	{
		listenerStub.clear();
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addListener(listenerStub);
		order.setStatusEventsEnabled(false);
		
		controller.processAvailable(order, Instant.EPOCH);
		
		type.removeListener(listenerStub);
		assertEquals(0, listenerStub.getEventCount());
	}
	
	private void testOrderController_ProcessUpdate_Prepare(OrderStatus newStatus) {
		listenerStub.clear();
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.withToken(OrderField.ACTION, OrderAction.BUY)
			.withToken(OrderField.TYPE, OrderType.LMT);
		if ( order.getInitialVolume() == null ) {
			builder.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(100L));
		}
		if ( order.getCurrentVolume() == null ) {
			builder.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(100L));
		}
		order.consume(builder.buildUpdate());
	}
	
	private void testOrderController_ProcessUpdate_Ok(OrderStatus newStatus,
			EventType type) throws Exception
	{
		Instant time = T("2017-08-04T02:45:30Z");
		testOrderController_ProcessUpdate_Prepare(newStatus);
		type.addListener(listenerStub);
		
		controller.processUpdate(order, time);
		
		type.removeListener(listenerStub);
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0), OrderEvent.class, type, order, time);
	}
	
	private void testOrderController_ProcessUpdate_SkipIfNotAvailable(OrderStatus newStatus,
			EventType type) throws Exception
	{
		listenerStub.clear();
		assertFalse(order.isAvailable());
		order.consume(new DeltaUpdateBuilder()
			.withToken(OrderField.STATUS, newStatus)
			.buildUpdate());
		type.addListener(listenerStub);
		assertFalse(order.isAvailable());
		
		controller.processUpdate(order, T("2017-08-04T02:45:00Z"));
		
		type.removeListener(listenerStub);
		assertEquals(0, listenerStub.getEventCount());
	}
	
	private void testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus newStatus,
			EventType type) throws Exception
	{
		testOrderController_ProcessUpdate_Prepare(newStatus);
		order.setStatusEventsEnabled(false);
		type.addListener(listenerStub);
		
		controller.processUpdate(order, T("2017-08-04T02:50:00Z"));
		
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
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(5L))
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
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(5L))
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
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(5L))
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
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(5L))
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
			.withToken(OrderField.INITIAL_VOLUME, CDecimalBD.of(10L))
			.withToken(OrderField.CURRENT_VOLUME, CDecimalBD.of(5L))
			.buildUpdate());
		testOrderController_ProcessUpdate_SkipIfStatusEventsDisabled(OrderStatus.CANCELLED, order.onPartiallyFilled());
	}

	@Test
	public void testOrderController_GetCurrentTime_IfNotClosed() {
		schedulerStub.setFixedTime("2017-08-04T02:35:00Z");
		
		assertEquals(T("2017-08-04T02:35:00Z"), controller.getCurrentTime(order));
	}

	@Test
	public void testOrderController_GetCurrentTime_IfClosed() {
		order.close();
		
		assertNull(controller.getCurrentTime(order));
	}
	
	@Test
	public void testUpdate_OnAvailable() throws Exception {
		Instant time = T("2017-08-04T02:50:15Z");
		container = produceContainer(controllerMock);
		container.onAvailable().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(order)).andReturn(time);
		controllerMock.processUpdate(order, time);
		expect(controllerMock.hasMinimalData(order, time)).andReturn(true);
		controllerMock.processAvailable(order, time);
		getMocksControl().replay();

		data.put(12345, 415); // any value
		order.update(data);
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0), OrderEvent.class, order.onAvailable(), order, time);
	}

	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		Instant time1 = T("2017-08-04T02:55:00Z"), time2 = T("2017-08-04T02:55:10Z");
		container = produceContainer(controllerMock);
		container.onUpdate().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(order)).andReturn(time1);
		controllerMock.processUpdate(order, time1);
		expect(controllerMock.hasMinimalData(order, time1)).andReturn(true);
		controllerMock.processAvailable(order, time1);
		expect(controllerMock.getCurrentTime(order)).andReturn(time2);
		controllerMock.processUpdate(order, time2);
		getMocksControl().replay();
		
		data.put(12345, 415);
		order.update(data);
		data.put(12345, 450);
		order.update(data);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertContainerEvent(listenerStub.getEvent(0),
				OrderUpdateEvent.class,
				order.onUpdate(),
				order,
				time1,
				m(p(12345, null)),
				m(p(12345, 415)));
		assertContainerEvent(listenerStub.getEvent(1),
				OrderUpdateEvent.class,
				order.onUpdate(),
				order,
				time2,
				m(p(12345, 415)),
				m(p(12345, 450)));
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
			CDecimal price, CDecimal volume, CDecimal value)
	{
		return new OrderExecutionImpl(terminal, id, externalID, symbol,
			order.getAction(), 240L, time, price, volume, value);
	}
	
	@Test
	public void testAddExecution6() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.onExecution().addListener(listenerStub);
		
		OrderExecution actual = order.addExecution(100L, "x1", now,
				CDecimalBD.of("80.00"),
				CDecimalBD.of(10L),
				CDecimalBD.of("800.00", "RUB"));
		
		OrderExecution expected = newExec(100L, "x1", now,
				CDecimalBD.of("80.00"),
				CDecimalBD.of(10L),
				CDecimalBD.of("800.00", "RUB"));
		assertEquals(expected, actual);
		assertEquals(expected, order.getExecution(100L));
		assertEquals(0, listenerStub.getEventCount());
	}
	
	@Test (expected=OrderException.class)
	public void testAddExecution6_ThrowsIfAlreadyExists() throws Exception {
		Instant now = Instant.now();
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		order.addExecution(100L, "foo1", now, CDecimalBD.of("34.15"),
				CDecimalBD.of(10L),
				CDecimalBD.of("341.50", "RUB"));
		
		order.addExecution(100L, "foo1", now, CDecimalBD.of("34.15"),
				CDecimalBD.of(10L),
				CDecimalBD.of("341.50", "RUB"));
	}
	
	@Test
	public void testGetExecution() throws Exception {
		data.put(OrderField.ACTION, OrderAction.BUY);
		order.update(data);
		Instant now = Instant.now();
		order.addExecution(100L, "foo1", now, CDecimalBD.of("34.15"),
				CDecimalBD.of(10L),
				CDecimalBD.of("341.50", "RUB"));
		order.addExecution(101L, "foo2", now.plusMillis(1), CDecimalBD.of("34.25"),
				CDecimalBD.of(20L),
				CDecimalBD.of("683.00", "RUB"));
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
		order.onArchived().addListener(listenerStub);
		
		order.fireArchived();
		
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onArchived());
	}
	
	@Test
	public void testAddExecution1() throws Exception {
		Instant time = Instant.EPOCH;
		OrderExecution execution = newExec(2005, "foo2005", time,
				CDecimalBD.of("112.34"),
				CDecimalBD.of(10L),
				CDecimalBD.of("1123.40", "RUB"));
		order.onExecution().addListener(listenerStub);
		
		order.addExecution(execution);
		
		assertFalse(order.hasChanged());
		assertEquals(0, listenerStub.getEventCount());
		assertEquals(execution, order.getExecution(2005));
	}
	
	@Test (expected=OrderException.class)
	public void testAddExecution1_ThrowsIfAlreadyExists() throws Exception {
		Instant time = Instant.EPOCH;
		
		order.addExecution(newExec(2008, "x", time, CDecimalBD.of("48.15"),
				CDecimalBD.of(1L),
				CDecimalBD.of("48.15", "RUB")));
		order.addExecution(newExec(2008, "y", time, CDecimalBD.of("12.78"),
				CDecimalBD.of(1L),
				CDecimalBD.of("12.78", "RUB")));
	}
	
	@Test
	public void testFireExecutionAdded() throws Exception {
		Instant time = Instant.now();
		OrderExecution execution = newExec(512, null, time, CDecimalBD.of("45.14"),
				CDecimalBD.of(1L),
				CDecimalBD.of("45.14", "RUB"));
		order.onExecution().addListener(listenerStub);
		
		order.fireExecution(execution);
		
		assertEquals(1, listenerStub.getEventCount());
		assertOrderEvent(listenerStub.getEvent(0), order.onExecution());
		assertEquals(execution, ((OrderExecutionEvent) listenerStub.getEvent(0)).getExecution());
	}
	
	@Test
	public void testGetExecutions() throws Exception {
		Instant time = Instant.now();
		OrderExecution exec1 = newExec(501, null, time, CDecimalBD.of("10.00"),
				CDecimalBD.of(1L),
				CDecimalBD.of("20.00", "RUB"));
		OrderExecution exec2 = newExec(502, null, time, CDecimalBD.of("10.10"),
				CDecimalBD.of(5L),
				CDecimalBD.of("40.00", "RUB"));
		OrderExecution exec3 = newExec(503, "xo", time, CDecimalBD.of("10.20"),
				CDecimalBD.of(2L),
				CDecimalBD.of("50.00", "RUB"));
		order.addExecution(exec1);
		order.addExecution(exec2);
		order.addExecution(exec3);
		
		List<OrderExecution> expected = new ArrayList<>();
		expected.add(exec1);
		expected.add(exec2);
		expected.add(exec3);
		assertEquals(expected, order.getExecutions());
	}
	
	@Test
	public void testGettersOfRelatedObjects() throws Exception {
		Terminal terminalMock = control.createMock(Terminal.class);
		control.replay();
		Security expectedSecurity = terminal.getSecurity(symbol);
		Portfolio expectedPortfolio = terminal.getPortfolio(account);
		Position expectedPosition = expectedPortfolio.getPosition(symbol);
		order = new OrderImpl(new OrderParamsBuilder(queue)
				.withTerminal(terminalMock)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(240L)
				.withSecurity(expectedSecurity)
				.withPortfolio(expectedPortfolio)
				.withPosition(expectedPosition)
				.withID("zxy")
				.buildParams());

		assertSame(expectedSecurity, order.getSecurity());
		assertSame(expectedPortfolio, order.getPortfolio());
		assertSame(expectedPosition, order.getPosition());
		
		control.verify();
	}

}
