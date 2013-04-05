package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

public class EventDispatcherImplTest {
	private IMocksControl control;
	private EventType type1,type2;
	private EventQueue queue;
	private EventDispatcherImpl dispatcher;
	private EventListener l1,l2,l3,l4;
	
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
		type1 = new EventTypeImpl(dispatcher);
		type2 = new EventTypeImpl(dispatcher);
		l1 = control.createMock(EventListener.class);
		l2 = control.createMock(EventListener.class);
		l3 = control.createMock(EventListener.class);
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
	public void testDispatch_DoNothingIfNoListeners() throws Exception {
		control.replay();
		
		Event event = new EventImpl(type1);
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_EnqueuesIfHasListeners() throws Exception {
		Event event = new EventImpl(type1);
		queue.enqueue(same(event), same(dispatcher));
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type1, l3);
		dispatcher.addListener(type2, l4);
		
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_EachListenerAddOnce() throws Exception {
		Event event = new EventImpl(type1);
		queue.enqueue(same(event), same(dispatcher));
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l1);
		
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_SkipsAfterRemoveListener() throws Exception {
		Event event = new EventImpl(type1);
		queue.enqueue(same(event), same(dispatcher));
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.removeListener(type1, l1);
		
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_SkipsAfterLastListenerRemoved() throws Exception {
		Event event = new EventImpl(type1);
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.removeListener(type1, l1);
		
		dispatcher.dispatch(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_SkipsAllAfterClose() throws Exception {
		Event event1 = new EventImpl(type1);
		Event event2 = new EventImpl(type2);
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type1, l3);
		dispatcher.addListener(type2, l4);
		dispatcher.close();
		
		dispatcher.dispatch(event1);
		dispatcher.dispatch(event2);
		
		control.verify();
	}
	
	@Test
	public void testRemoveListener_SkipIfNoListeners() throws Exception {
		control.replay();
		
		dispatcher.removeListener(type1, l1);
		
		control.verify();
	}
	
	@Test
	public void testCountListeners_Ok() throws Exception {
		control.replay();
		
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type2, l2);
		assertEquals(2, dispatcher.countListeners(type1));
		assertEquals(1, dispatcher.countListeners(type2));
		
		control.verify();
	}
	
	@Test
	public void testCountListeners_ZeroIfUnknownType() throws Exception {
		control.replay();
		
		assertEquals(0, dispatcher.countListeners(type2));
		
		control.verify();
	}
	
	@Test
	public void testAdditionalWorkflowTest() throws Exception {
		EventSystem eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = new EventDispatcherImpl(queue);
		
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
		dispatcher.addListener(type1, listener);
		queue.start();
		for ( int i = 0; i < 10; i ++ ) {
			dispatcher.dispatch(new EventImpl(type1));
			Thread.sleep((long) (Math.random() * 100));
		}
		dispatcher.dispatch(event1);
		queue.join(1000);
	}

	@Test
	public void testGetListeners_EmtyListIfTypeNotExists() throws Exception {
		Vector<EventListener> empty = new Vector<EventListener>();
		assertEquals(empty, dispatcher.getListeners(type1));
		assertEquals(empty, dispatcher.getListeners(type2));
	}
	
	@Test
	public void testGetListeners_Ok() throws Exception {
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type2, l4);
		dispatcher.addListener(type1, l3);

		Vector<EventListener> expected1 = new Vector<EventListener>();
		expected1.add(l2);
		expected1.add(l1);
		expected1.add(l3);
		Vector<EventListener> expected2 = new Vector<EventListener>();
		expected2.add(l4);
		assertEquals(expected1, dispatcher.getListeners(type1));
		assertEquals(expected2, dispatcher.getListeners(type2));
	}
	
	@Test
	public void testIsTypeListener() throws Exception {
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type2, l4);
		dispatcher.addListener(type1, l4);
		
		assertTrue(dispatcher.isTypeListener(type1, l2));
		assertFalse(dispatcher.isTypeListener(type2, l2));
		
		assertTrue(dispatcher.isTypeListener(type2, l4));
		assertTrue(dispatcher.isTypeListener(type1, l4));
		
		assertFalse(dispatcher.isTypeListener(type1, l3));
		assertFalse(dispatcher.isTypeListener(type2, l3));
	}
	
	@Test
	public void testRemoveListeners1() throws Exception {
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type2, l2);
		
		dispatcher.removeListeners(type1);
		
		assertEquals(0, dispatcher.countListeners(type1));
		assertEquals(1, dispatcher.countListeners(type2));
	}
	
	@Test
	public void testDispatchForCurrentList_HasListeners() throws Exception {
		dispatcher.addListener(type1, l1);
		dispatcher.addListener(type1, l2);
		dispatcher.addListener(type2, l3);
		Vector<EventListener> expected = new Vector<EventListener>();
		expected.add(l1);
		expected.add(l2);
		Event event = new EventImpl(type1);
		queue.enqueue(same(event), eq(expected));
		control.replay();
		
		dispatcher.dispatchForCurrentList(event);
		
		control.verify();
	}
	
	@Test
	public void testDispatchForCurrentList_NoListeners() throws Exception {
		Event event = new EventImpl(type1);
		control.replay();
		
		dispatcher.dispatchForCurrentList(event);
		
		control.verify();
	}
	
}
