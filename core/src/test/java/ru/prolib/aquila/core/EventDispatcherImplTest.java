package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

public class EventDispatcherImplTest {
	private IMocksControl control;
	private EventTypeSI type1;
	private EventQueue queue;
	private EventDispatcherImpl dispatcher;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queue = control.createMock(EventQueue.class);
		dispatcher = new EventDispatcherImpl(queue, "TD");
		type1 = control.createMock(EventTypeSI.class);
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfQueueIsNull() throws Exception {
		new EventDispatcherImpl(null);
	}
	
	@Test
	public void testConstruct1_Ok() throws Exception {
		int autoId = EventDispatcherImpl.getAutoId();
		dispatcher = new EventDispatcherImpl(queue);
		assertSame(queue, dispatcher.getEventQueue());
		assertEquals("EvtDisp" + autoId, dispatcher.getId());
		assertEquals(autoId + 1, EventDispatcherImpl.getAutoId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(queue, dispatcher.getEventQueue());
		assertEquals("TD", dispatcher.getId());
	}
	
	@Test
	public void testToString() {
		assertEquals("TD", dispatcher.toString());
		assertEquals("TD", dispatcher.asString());
	}
	
	@Test
	public void testDispatch() throws Exception {
		EventSI event = new EventImpl(type1);
		queue.enqueue(same(event));
		control.replay();
		
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		control.replay();
		
		dispatcher.close();
		
		control.verify();
	}
	
	@Test
	public void testCreateType0() throws Exception {
		String expectedId = "TD.EvtType" + EventTypeImpl.getAutoId();
		EventTypeSI actual = dispatcher.createType();
		assertNotNull(actual);
		assertEquals(expectedId, actual.getId());
		assertFalse(actual.isOnlySyncMode());
	}
	
	@Test
	public void testCreateSyncType0() throws Exception {
		String expectedId = "TD.EvtType" + EventTypeImpl.getAutoId();
		EventTypeSI actual = dispatcher.createSyncType();
		assertNotNull(actual);
		assertEquals(expectedId, actual.getId());
		assertTrue(actual.isOnlySyncMode());
	}

	@Test
	public void testCreateType1() throws Exception {
		EventTypeSI actual = dispatcher.createType("foo");
		assertNotNull(actual);
		assertEquals("TD.foo", actual.getId());
		assertFalse(actual.isOnlySyncMode());
	}
	
	@Test
	public void testCreateSyncType1() throws Exception {
		EventTypeSI actual = dispatcher.createSyncType("bar");
		assertNotNull(actual);
		assertEquals("TD.bar", actual.getId());
		assertTrue(actual.isOnlySyncMode());
	}

}
