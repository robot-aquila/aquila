package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.concurrency.LID;

public class ObservableStateContainerImplTest {	
	private static final int STRING_NAME = 1;
	private static final int INTEGER_AGE = 2;
	private static final int INSTANT_TIME_OF_REG = 3;
	private static final int DOUBLE_CAPITAL = 4;
	private static final int LONG_SECONDS = 5;
	private static final int BOOL_ACTIVE = 6;

	public interface Getter<T> { public T get(); }
	
	protected EventQueue queue;
	protected ObservableStateContainerImpl container;
	protected Map<Integer, Object> data;
	protected Getter<?> getter;
	protected IMocksControl control;
	protected ObservableStateContainerImpl.Controller controllerMock;
	protected EventListenerStub listenerStub;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		controllerMock = control.createMock(ObservableStateContainerImpl.Controller.class);
		data = new HashMap<Integer, Object>();
		queue = new EventQueueImpl();
		container = produceContainer(new ObservableStateContainerImpl.ControllerStub());
		listenerStub = new EventListenerStub();
	}
	
	@After
	public void tearDown() throws Exception {
		data.clear();
		container.close();
		queue.stop();
		getter = null;
		listenerStub.clear();
	}
	
	protected IMocksControl getMocksControl() {
		return control;
	}
	
	/**
	 * Override this method to produce container ID.
	 * <p>
	 * @return container ID
	 */
	protected String getID() {
		return "foo";
	}
	
	/**
	 * Override this method to produce container with the specified controller.
	 * <p>
	 * @param controller - the controller
	 * @return container instance
	 */
	protected ObservableStateContainerImpl produceContainer(ObservableStateContainerImpl.Controller controller) {
		return new ObservableStateContainerImpl(queue, getID(), controller);
	}

	/**
	 * Override this method to produce container with default controller.
	 * <p>
	 * @return container instance
	 */
	protected ObservableStateContainerImpl produceContainer() {
		return new ObservableStateContainerImpl(queue, getID());
	}
	
	/**
	 * Basic test of getter method.
	 * <p>
	 * This method tests getter method, token change status and container update
	 * event. Uses getter and container instance.
	 * <p>
	 * @param token - token ID
	 * @param value1 - initial value
	 * @param value2 - new value
	 * @param nullValue - null equivalent (should be equals to if no token defined or token value is null)
	 */
	protected void testGetter_ChangesPlusEvent(int token,
			Object value1, Object value2, Object nullValue)
	{
		Object fixture[][] = {
				// initial value, new value, changed?
				{ null,		null,	false },
				{ value1,	value1,	false },
				{ null,		value2,	true  },
				{ value1,	value2,	true  },
				{ value1,	null,	true  },
		};
		Map<Integer, Object> data = new HashMap<Integer, Object>();
		for ( int i = 0; i < fixture.length; i ++ ) {
			listenerStub.clear();
			String msg = "At #" + i;
			data.clear();
			data.put(token, fixture[i][0]);
			container.update(data);
			container.resetChanges();
			container.onAvailable().addSyncListener(listenerStub);
			container.onUpdate().addSyncListener(listenerStub);
			data.clear();
			data.put(token, fixture[i][1]);
			container.update(data);
			container.onUpdate().removeListener(listenerStub);
			container.onAvailable().removeListener(listenerStub);
			boolean changed = (Boolean)fixture[i][2];
			assertEquals(msg, changed ? 1 : 0, listenerStub.getEventCount());
			assertEquals(msg, changed, container.hasChanged(token));
			Object expectedValue = fixture[i][1];
			if ( expectedValue == null ) {
				expectedValue = nullValue;
			}
			assertEquals(msg, expectedValue, getter.get());
		}
	}
	
	/**
	 * Basic test of getter method.
	 * <p>
	 * This method tests getter method, token change status and container update
	 * event. Uses getter and container instance. Works via
	 * {@link #testGetter_ChangesPlusEvent(int, Object, Object, Object)}
	 * with default null-value.
	 * <p>
	 * @param token - token ID
	 * @param value1 - initial value
	 * @param value2 - new value
	 */
	protected void testGetter_ChangesPlusEvent(int token,
			Object value1, Object value2)
	{
		testGetter_ChangesPlusEvent(token, value1, value2, null);
	}
	
	/**
	 * Test of getter method for multithreading access.
	 * <p>
	 * Uses getter and container instance.
	 * <p>
	 * @param token - token ID
	 * @param value1 - initial value
	 * @param value2 - new value
	 */
	protected void testGetter_Locking(int token, Object value1, Object value2)
		throws Exception
	{
		final CountDownLatch started = new CountDownLatch(1);
		final CountDownLatch finished = new CountDownLatch(1);
		new Thread() {
			@Override public void run() {
				container.lock();
				started.countDown();
				try {
					Thread.sleep(50);
					data.clear();
					data = new HashMap<Integer, Object>();
					data.put(token, value1);
					container.update(data);
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				} finally {
					container.unlock();
				}
				finished.countDown();
			}
		}.start();
		assertTrue(started.await(500, java.util.concurrent.TimeUnit.MILLISECONDS));
		Map<Integer, Object> dummy = new HashMap<Integer, Object>();
		dummy.put(token,  value2);
		container.update(dummy);
		assertTrue(finished.await(500, java.util.concurrent.TimeUnit.MILLISECONDS));
		assertEquals(value2, getter.get());
	}
	
	/**
	 * Test of getter method class cast exception.
	 * <p>
	 * Uses getter and container instance.
	 * <p>
	 * @param token - token ID
	 */
	protected void testGetter_ClassCastException(int token) {
		data.clear();
		data.put(token, new Object());
		container.update(data);
		
		try {
			getter.get();
			fail("Expected exception: " + ClassCastException.class);
		} catch ( ClassCastException e ) { }
	}
	

	/**
	 * Getter method complex test.
	 * <p>
	 * @param token - token ID
	 * @param value1 - initial value
	 * @param value2 - new value
	 * @param nullValue - null equivalent (should be equals to if no token defined or token value is null)
	 */
	protected void testGetter(int token, Object value1, Object value2, Object nullValue)
			throws Exception
	{
		testGetter_ChangesPlusEvent(token, value1, value2, nullValue);
		testGetter_Locking(token, value1, value2);
		testGetter_ClassCastException(token);		
	}
	
	/**
	 * Getter method complex test.
	 * <p>
	 * @param token - token ID
	 * @param value1 - initial value
	 * @param value2 - new value
	 */
	protected void testGetter(int token, Object value1, Object value2)
			throws Exception
	{
		testGetter(token, value1, value2, null);
	}
	
	@Test
	final public void testContainerImpl_Ctor4() {
		container = produceContainer(controllerMock);
		assertSame(queue, container.getEventQueue());
		assertSame(controllerMock, container.getController());
		assertEquals(getID(), container.getContainerID());
		assertEquals(getID() + ".UPDATE", container.onUpdate().getId());
		assertEquals(getID() + ".AVAILABLE", container.onAvailable().getId());
		assertFalse(container.isAvailable());
		assertFalse(container.isClosed());
		assertTrue(LID.isLastCreatedLID(container.getLID()));
	}
	
	@Test
	final public void testContainerImpl_Ctor3() {
		container = produceContainer();
		assertSame(queue, container.getEventQueue());
		assertNotNull(container.getController());
		assertEquals(getID(), container.getContainerID());
		assertEquals(getID() + ".UPDATE", container.onUpdate().getId());
		assertEquals(getID() + ".AVAILABLE", container.onAvailable().getId());
		assertFalse(container.isAvailable());
		assertFalse(container.isClosed());
		assertTrue(LID.isLastCreatedLID(container.getLID()));
	}
	
	@Test
	final public void testContainerImpl_IsDefined() {
		int tokens[] = { STRING_NAME, INTEGER_AGE };
		assertFalse(container.isDefined(tokens));
		
		data.put(STRING_NAME, "foobar");
		container.update(data);
		assertTrue(container.isDefined(STRING_NAME));
		assertFalse(container.isDefined(INTEGER_AGE));
		assertFalse(container.isDefined(tokens));
		
		data.put(INTEGER_AGE, new Integer(23));
		container.update(data);
		assertTrue(container.isDefined(STRING_NAME));
		assertTrue(container.isDefined(INTEGER_AGE));
		assertTrue(container.isDefined(tokens));
		
		data.put(STRING_NAME, null);
		container.update(data);
		assertFalse(container.isDefined(STRING_NAME));
		assertTrue(container.isDefined(INTEGER_AGE));
		assertFalse(container.isDefined(tokens));
	}
	
	@Test
	final public void testContainerImpl_AtLeastOnHasChanged() {
		int tokens[] = { BOOL_ACTIVE, LONG_SECONDS };
		data.put(INTEGER_AGE, new Integer(45));
		container.update(data);
		assertFalse(container.atLeastOneHasChanged(tokens));

		data.put(BOOL_ACTIVE, true);
		container.update(data);
		assertTrue(container.atLeastOneHasChanged(tokens));
		
		data.put(BOOL_ACTIVE, false);
		data.put(LONG_SECONDS, new Long(240));
		container.update(data);
		assertTrue(container.atLeastOneHasChanged(tokens));
		
		data.clear();
		data.put(STRING_NAME, "chopper");
		container.update(data);
		assertFalse(container.atLeastOneHasChanged(tokens));
	}
	
	@Test
	final public void testContainerImpl_HasChanged1() {
		container.update(data);
		
		assertFalse(container.hasChanged(DOUBLE_CAPITAL));
		assertFalse(container.hasChanged(INSTANT_TIME_OF_REG));
		assertFalse(container.hasChanged(LONG_SECONDS));
		assertFalse(container.hasChanged(BOOL_ACTIVE));
		
		data.put(DOUBLE_CAPITAL, new Double(140.25d));
		data.put(INSTANT_TIME_OF_REG, Instant.parse("2016-01-16T00:00:00Z"));
		container.update(data);
		
		assertTrue(container.hasChanged(DOUBLE_CAPITAL));
		assertTrue(container.hasChanged(INSTANT_TIME_OF_REG));
		assertFalse(container.hasChanged(LONG_SECONDS));
		assertFalse(container.hasChanged(BOOL_ACTIVE));
		
		data.clear();
		data.put(LONG_SECONDS, new Long(1000));
		data.put(BOOL_ACTIVE, false);
		container.update(data);
		
		assertFalse(container.hasChanged(DOUBLE_CAPITAL));
		assertFalse(container.hasChanged(INSTANT_TIME_OF_REG));
		assertTrue(container.hasChanged(LONG_SECONDS));
		assertTrue(container.hasChanged(BOOL_ACTIVE));
	}
	
	@Test
	final public void testContainerImpl_HasChanged0() {
		assertFalse(container.hasChanged());
		
		container.update(data);
		
		assertFalse(container.hasChanged());
		
		data.put(DOUBLE_CAPITAL, new Double(250.413d));
		container.update(data);
		
		assertTrue(container.hasChanged());
		
		container.resetChanges();
		
		assertFalse(container.hasChanged());
	}
	
	@Test
	final public void testContainerImpl_Update_MarksAsUpdatedOnlyActuallyChangedTokens() {
		data.put(DOUBLE_CAPITAL, new Double(712.315d));
		data.put(BOOL_ACTIVE, true);
		data.put(INSTANT_TIME_OF_REG, Instant.parse("2001-09-11T13:45:15Z"));
		data.put(INTEGER_AGE, 85);
		data.put(LONG_SECONDS, new Long(1024));
		data.put(STRING_NAME, "Gabba");
		container.update(data);
		
		Set<Integer> expected = new HashSet<Integer>();
		expected.add(DOUBLE_CAPITAL);
		expected.add(BOOL_ACTIVE);
		expected.add(INSTANT_TIME_OF_REG);
		expected.add(INTEGER_AGE);
		expected.add(LONG_SECONDS);
		expected.add(STRING_NAME);
		assertEquals(expected, container.getUpdatedTokens());
		
		container.update(data);
		assertEquals(new HashSet<Integer>(), container.getUpdatedTokens());
		
		data.put(DOUBLE_CAPITAL, new Double(819.221d));
		data.put(BOOL_ACTIVE, true);
		data.put(INSTANT_TIME_OF_REG, null);
		data.put(INTEGER_AGE, 45);
		data.put(LONG_SECONDS, new Long(1024));
		data.put(STRING_NAME, "Gabba");
		container.update(data);
		
		expected.clear();
		expected.add(DOUBLE_CAPITAL);
		expected.add(INSTANT_TIME_OF_REG);
		expected.add(INTEGER_AGE);
		assertEquals(expected, container.getUpdatedTokens());
	}
	
	@Test
	final public void testContainerImpl_GetBoolean() throws Exception {
		getter = new Getter<Boolean>() {
			@Override public Boolean get() {
				return container.getBoolean(BOOL_ACTIVE);
			}
		};
		testGetter(BOOL_ACTIVE, true, false);
	}
	
	@Test
	final public void testContainerImpl_GetDouble() throws Exception {
		getter = new Getter<Double>() {
			@Override public Double get() {
				return container.getDouble(DOUBLE_CAPITAL);
			}
		};
		testGetter(DOUBLE_CAPITAL, 345.215d, 44.99d);
	}
	
	@Test
	final public void testContainerImpl_GetInstant() throws Exception {
		getter = new Getter<Instant>() {
			@Override public Instant get() {
				return container.getInstant(INSTANT_TIME_OF_REG);
			}
		};
		testGetter(INSTANT_TIME_OF_REG,
				Instant.parse("2015-01-01T00:00:00Z"),
				Instant.parse("1990-12-31T23:59:59Z"));
	}
	
	@Test
	final public void testContainerImpl_GetInteger() throws Exception {
		getter = new Getter<Integer>() {
			@Override public Integer get() {
				return container.getInteger(INTEGER_AGE);
			}
		};
		testGetter(INTEGER_AGE, 80, 921);
	}
	
	@Test
	final public void testContainerImpl_GetLong() throws Exception {
		getter = new Getter<Long>() {
			@Override public Long get() {
				return container.getLong(LONG_SECONDS);
			}
		};
		testGetter(LONG_SECONDS, 1024L, 584L);
	}
	
	@Test
	final public void testContainerImpl_GetString() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return container.getString(STRING_NAME);
			}
		};
		testGetter(STRING_NAME, "foo", "bar");
	}
	
	@Test
	final public void testContainerImpl_GetObject() throws Exception {
		Object value1 = new Object(), value2 = new Object();
		getter = new Getter<Object>() {
			@Override public Object get() {
				return container.getObject(888);
			}
		};
		testGetter_ChangesPlusEvent(888, value1, value2);
		testGetter_Locking(888, value1, value2);
	}
	
	@Test
	final public void testContainerImpl_Update_NoChanges() throws Exception {
		EventListenerStub listener = new EventListenerStub();
		container.onAvailable().addSyncListener(listener);
		container.onUpdate().addSyncListener(listener);
		
		container.update(data);
		
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	final public void testContainerImpl_Update_HasNoMinimalData() {
		container = produceContainer(controllerMock);
		EventListenerStub listener = new EventListenerStub();
		container.onAvailable().addSyncListener(listener);
		container.onUpdate().addSyncListener(listener);
		expect(controllerMock.hasMinimalData(container)).andReturn(false);
		control.replay();
		
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		
		control.verify();
		assertEquals(0, listener.getEventCount());
	}
	
	@Test
	final public void testContainerImpl_Update_AvailableFirstTime() {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		container.onUpdate().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		control.replay();
		
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		
		control.verify();
		assertTrue(container.isAvailable());
		assertEquals(1, listenerStub.getEventCount());
		ContainerEvent e = (ContainerEvent) listenerStub.getEvent(0);
		assertTrue(e.isType(container.onAvailable()));
		assertSame(container, e.getContainer());
	}
	
	@Test
	final public void testContainerImpl_Update_AvailableButNoChangesForUpdate() {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		container.onUpdate().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		control.replay();
		
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		container.update(data);
		
		control.verify();
		assertTrue(container.isAvailable());
		assertEquals(1, listenerStub.getEventCount());
		ContainerEvent e = (ContainerEvent) listenerStub.getEvent(0);
		assertTrue(e.isType(container.onAvailable()));
		assertSame(container, e.getContainer());
	}

	@Test
	final public void testContainerImpl_Update_AvailableAndUpdate() {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		container.onUpdate().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		controllerMock.processUpdate(container);
		control.replay();
		
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		data.put(BOOL_ACTIVE, false);
		container.update(data);
		
		control.verify();
		assertTrue(container.isAvailable());
		assertEquals(2, listenerStub.getEventCount());
		ContainerEvent e = (ContainerEvent) listenerStub.getEvent(0);
		assertTrue(e.isType(container.onAvailable()));
		assertSame(container, e.getContainer());
		e = (ContainerEvent) listenerStub.getEvent(1);
		assertTrue(e.isType(container.onUpdate()));
		assertSame(container, e.getContainer());
	}
	
	@Test
	final public void testContainerImpl_Update_AvailableAnd2Updates() {
		container = produceContainer(controllerMock);
		container.onAvailable().addSyncListener(listenerStub);
		container.onUpdate().addSyncListener(listenerStub);
		expect(controllerMock.hasMinimalData(container)).andReturn(true);
		controllerMock.processAvailable(container);
		controllerMock.processUpdate(container);
		controllerMock.processUpdate(container);
		control.replay();
		
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		data.put(BOOL_ACTIVE, false);
		container.update(data);
		data.put(BOOL_ACTIVE, true);
		container.update(data);
		
		control.verify();
		assertTrue(container.isAvailable());
		assertEquals(3, listenerStub.getEventCount());
		ContainerEvent e = (ContainerEvent) listenerStub.getEvent(0);
		assertTrue(e.isType(container.onAvailable()));
		assertSame(container, e.getContainer());
		e = (ContainerEvent) listenerStub.getEvent(1);
		assertTrue(e.isType(container.onUpdate()));
		assertSame(container, e.getContainer());
		e = (ContainerEvent) listenerStub.getEvent(2);
		assertTrue(e.isType(container.onUpdate()));
		assertSame(container, e.getContainer());
	}	
	
	@Test
	final public void testContainerImpl_Close() {
		EventTypeImpl type = new EventTypeImpl();
		container.onUpdate().addListener(listenerStub);
		container.onUpdate().addAlternateType(type);
		container.onAvailable().addListener(listenerStub);
		container.onAvailable().addAlternateType(type);
		data.put(STRING_NAME, "foobar");
		container.update(data);
		
		container.close();
		
		assertEquals(new HashSet<Integer>(), container.getUpdatedTokens());
		assertFalse(container.isDefined(STRING_NAME));
		assertFalse(container.onUpdate().hasListeners());
		assertFalse(container.onUpdate().hasAlternates());
		assertFalse(container.onAvailable().hasListeners());
		assertFalse(container.onAvailable().hasAlternates());
		assertFalse(container.isAvailable());
		assertTrue(container.isClosed());
	}
	
	@Test
	public void testContainerImpl_IsAvailable() {
		assertFalse(container.isAvailable());
		
		data.put(BOOL_ACTIVE, true); // one attribute will be enough
		container.update(data);
		
		assertTrue(container.isAvailable());
	}
	
	@Test
	public void testContainerImpl_IsClosed() {
		assertFalse(container.isClosed());
		
		container.close();
		
		assertTrue(container.isClosed());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testContainerImpl_Update_ThrowsIfClosed() {
		container.close();
		
		container.update(data);
	}
	
	@Test
	public void testGetContent() throws Exception {
		data.put(BOOL_ACTIVE, true);
		data.put(DOUBLE_CAPITAL, 815.32d);
		data.put(INSTANT_TIME_OF_REG, Instant.EPOCH);
		data.put(INTEGER_AGE, 25);
		container.update(data);
		
		Map<Integer, Object> actual = container.getContent();
		
		Map<Integer, Object> expected = new HashMap<>();
		expected.put(BOOL_ACTIVE, true);
		expected.put(DOUBLE_CAPITAL, 815.32d);
		expected.put(INSTANT_TIME_OF_REG, Instant.EPOCH);
		expected.put(INTEGER_AGE, 25);
		assertEquals(expected, actual);
		assertNotSame(data, actual);
	}
	
	@Test
	public void testGetUpdatedContent() throws Exception {
		data.put(BOOL_ACTIVE, true);
		data.put(DOUBLE_CAPITAL, 815.32d);
		data.put(INSTANT_TIME_OF_REG, Instant.EPOCH);
		data.put(INTEGER_AGE, 25);
		container.update(data);
		data.clear();
		data.put(DOUBLE_CAPITAL, 850.69d);
		data.put(INSTANT_TIME_OF_REG, Instant.parse("2016-07-08T16:19:00Z"));
		container.update(data);
		
		Map<Integer, Object> actual = container.getUpdatedContent();

		Map<Integer, Object> expected = new HashMap<>();
		expected.put(DOUBLE_CAPITAL, 850.69d);
		expected.put(INSTANT_TIME_OF_REG, Instant.parse("2016-07-08T16:19:00Z"));
		assertEquals(expected, actual);
		assertNotSame(data, actual);
	}

}
