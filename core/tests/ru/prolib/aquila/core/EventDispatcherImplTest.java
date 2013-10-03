package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class EventDispatcherImplTest {
	private IMocksControl control;
	private EventType type1;
	private EventQueue queue;
	private EventDispatcherImpl dispatcher;
	private EventListener l1,l2,l4;
	
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
		type1 = control.createMock(EventType.class);
		l1 = control.createMock(EventListener.class);
		l2 = control.createMock(EventListener.class);
		l4 = control.createMock(EventListener.class);
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
	public void testAddListener() throws Exception {
		type1.addListener(same(l1));
		control.replay();
		
		dispatcher.addListener(type1, l1);
		
		control.verify();
	}
	
	@Test
	public void testRemoveListener() throws Exception {
		type1.removeListener(same(l1));
		control.replay();
		
		dispatcher.removeListener(type1, l1);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_SkipIfNoListeners() throws Exception {
		expect(type1.countListeners()).andReturn(0);
		control.replay();
		
		Event event = new EventImpl(type1);
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch() throws Exception {
		Event event = new EventImpl(type1);
		expect(type1.countListeners()).andReturn(1);
		queue.enqueue(same(event), same(dispatcher));
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
	public void testCountListeners() throws Exception {
		expect(type1.countListeners()).andReturn(81);
		control.replay();
		
		assertEquals(81, dispatcher.countListeners(type1));
		
		control.verify();
	}
	
	@Test
	public void testGetListeners() throws Exception {
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(l1);
		expected.add(l4);
		expect(type1.getListeners()).andReturn(expected);
		control.replay();
		
		assertSame(expected, dispatcher.getListeners(type1));
		
		control.verify();
	}
	
	@Test
	public void testIsTypeListener() throws Exception {
		expect(type1.isListener(l1)).andReturn(true);
		expect(type1.isListener(l2)).andReturn(false);
		control.replay();
		
		assertTrue(dispatcher.isTypeListener(type1, l1));
		assertFalse(dispatcher.isTypeListener(type1, l2));
		
		control.verify();
	}
	
	@Test
	public void testRemoveListeners() throws Exception {
		type1.removeListeners();
		control.replay();
		
		dispatcher.removeListeners(type1);
		
		control.verify();
	}
	
	@Test
	public void testDispatchForCurrentList() throws Exception {
		Vector<EventListener> expected = new Vector<EventListener>();
		expected.add(l1);
		expected.add(l2);
		Event event = new EventImpl(type1);
		expect(type1.countListeners()).andReturn(2);
		expect(type1.getListeners()).andReturn(expected);
		queue.enqueue(same(event), eq(expected));
		control.replay();
		
		dispatcher.dispatchForCurrentList(event);
		
		control.verify();
	}

	@Test
	public void testDispatchForCurrentList_SkipIfNoListeners()
		throws Exception
	{
		Event event = new EventImpl(type1);
		expect(type1.countListeners()).andReturn(0);
		control.replay();
		
		dispatcher.dispatchForCurrentList(event);
		
		control.verify();
	}

	@Test
	public void testCreateType0() throws Exception {
		String expectedId = "TD.EvtType" + EventTypeImpl.getAutoId();
		EventType expected = new EventTypeImpl(expectedId);
		assertEquals(expected, dispatcher.createType());
	}
	
	@Test
	public void testCreateType1() throws Exception {
		EventType expected = new EventTypeImpl("TD.foobar");
		assertEquals(expected, dispatcher.createType("foobar"));
	}
	
	@Test
	public void testAdditionalWorkflowTest() throws Exception {
		EventSystem eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = new EventDispatcherImpl(queue);
		type1 = new EventTypeImpl();
		
		final Event event1 = new EventImpl(type1);
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( event == event1 ) {
					try {
						queue.stop();
					} catch ( StarterException e ) {
						fail("Unhandled exception: " + e);
					}
				}
			}
		};
		type1.addListener(listener);
		queue.start();
		for ( int i = 0; i < 10; i ++ ) {
			dispatcher.dispatch(new EventImpl(type1));
			Thread.sleep((long) (Math.random() * 100));
		}
		dispatcher.dispatch(event1);
		queue.join(1000);
	}
	
	@Test
	public void testEquals() throws Exception {
		dispatcher = new EventDispatcherImpl(new SimpleEventQueue("foo"), "ad");
		Variant<EventQueue> vQue = new Variant<EventQueue>()
			.add(new SimpleEventQueue("foo"))
			.add(new SimpleEventQueue("bar"));
		Variant<String> vId = new Variant<String>(vQue)
			.add("ad")
			.add("bad");
		Variant<?> iterator = vId;
		int foundCnt = 0;
		EventDispatcherImpl x, found = null;
		do {
			x = new EventDispatcherImpl(vQue.get(), vId.get());
			if ( dispatcher.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new SimpleEventQueue("foo"), found.getEventQueue());
		assertEquals("ad", found.getId());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		dispatcher = new EventDispatcherImpl(new SimpleEventQueue("foo"), "ad");
		assertTrue(dispatcher.equals(dispatcher));
		assertFalse(dispatcher.equals(control.createMock(EventDispatcher.class)));
		assertFalse(dispatcher.equals(null));
		assertFalse(dispatcher.equals(this));
	}

}
