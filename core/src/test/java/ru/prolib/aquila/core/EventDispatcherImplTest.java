package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.LinkedList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcherImpl.CachedEvent;

public class EventDispatcherImplTest {
	private IMocksControl control;
	private EventType type1, type2, type3;
	private EventQueue queue;
	private LinkedList<CachedEvent> cacheStub;
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
		cacheStub = new LinkedList<>();
		dispatcher = new EventDispatcherImpl(queue, "TD", cacheStub);
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		type3 = control.createMock(EventType.class);
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
	public void testDispatch2() throws Exception {
		queue.enqueue(type1, SimpleEventFactory.getInstance());
		control.replay();
		
		dispatcher.dispatch(type1, SimpleEventFactory.getInstance());
		
		control.verify();
	}
	
	@Test
	public void testDispatch2_SuppressMode() throws Exception {
		control.replay();
		dispatcher.suppressEvents();
		
		dispatcher.dispatch(type1, SimpleEventFactory.getInstance());
		dispatcher.dispatch(type2, SimpleEventFactory.getInstance());
		
		control.verify();
		LinkedList<CachedEvent> expected = new LinkedList<>();
		expected.add(new CachedEvent(type1, SimpleEventFactory.getInstance()));
		expected.add(new CachedEvent(type2, SimpleEventFactory.getInstance()));
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testRestoreEvents() throws Exception {
		dispatcher.suppressEvents();
		dispatcher.dispatch(type1, SimpleEventFactory.getInstance());
		dispatcher.dispatch(type2, SimpleEventFactory.getInstance());
		queue.enqueue(type1, SimpleEventFactory.getInstance());
		queue.enqueue(type2, SimpleEventFactory.getInstance());
		queue.enqueue(type3, SimpleEventFactory.getInstance());
		control.replay();
		
		dispatcher.restoreEvents();
		dispatcher.dispatch(type3, SimpleEventFactory.getInstance());
		
		control.verify();
		assertEquals(0, cacheStub.size());
	}
	
	@Test
	public void testPurgeEvents() throws Exception {
		dispatcher.suppressEvents();
		dispatcher.dispatch(type1, SimpleEventFactory.getInstance());
		dispatcher.dispatch(type2, SimpleEventFactory.getInstance());
		queue.enqueue(type3, SimpleEventFactory.getInstance());
		control.replay();
		assertEquals(2, cacheStub.size());
		
		dispatcher.purgeEvents();
		
		assertEquals(0, cacheStub.size());
		dispatcher.dispatch(type3, SimpleEventFactory.getInstance());
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
		EventType actual = dispatcher.createType();
		assertNotNull(actual);
		assertEquals(expectedId, actual.getId());
	}

	@Test
	public void testCreateType1() throws Exception {
		EventType actual = dispatcher.createType("foo");
		assertNotNull(actual);
		assertEquals("TD.foo", actual.getId());
	}
	
	@Test
	public void testSuppressAndRestoreEvents_SequentialCalls() {
		dispatcher.suppressEvents();
		dispatcher.suppressEvents();
		control.replay();

		dispatcher.dispatch(type1, SimpleEventFactory.getInstance());
		
		dispatcher.restoreEvents();
		control.verify();
		control.resetToStrict();
		queue.enqueue(type1, SimpleEventFactory.getInstance());
		queue.enqueue(type2, SimpleEventFactory.getInstance());
		control.replay();
		dispatcher.restoreEvents();

		dispatcher.dispatch(type2, SimpleEventFactory.getInstance());

		control.verify();
	}

}
