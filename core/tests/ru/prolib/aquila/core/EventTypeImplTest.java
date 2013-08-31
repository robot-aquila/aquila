package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-04-09
 * $Id: EventTypeImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventTypeImplTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventTypeImpl type;
	private EventListener listener1, listener2, listener3;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcherImpl.class);
		listener1 = control.createMock(EventListener.class);
		listener2 = control.createMock(EventListener.class);
		listener3 = control.createMock(EventListener.class);
		type = new EventTypeImpl(dispatcher, "MyType");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct1_ThrowsIfDispatcherIsNull() throws Exception {
		new EventTypeImpl(null);
	}
	
	@Test
	public void testConstruct1_Ok() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type = new EventTypeImpl(dispatcher);
		assertSame(dispatcher, type.getEventDispatcher());
		assertEquals("EvtType" + autoId, type.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertSame(dispatcher, type.getEventDispatcher());
		assertEquals("MyType", type.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		expect(dispatcher.asString()).andReturn("MyDisp");
		control.replay();
		assertEquals("MyDisp.MyType", type.toString());
		control.verify();
	}
	
	@Test
	public void testAsString() throws Exception {
		expect(dispatcher.asString()).andReturn("MyDisp");
		control.replay();
		assertEquals("MyDisp.MyType", type.asString());
		control.verify();
	}
	
	@Test
	public void testAddListener() throws Exception {
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener1);
		expected.add(listener2);
		expected.add(listener3);
		
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testRemoveListener() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener3);
		
		type.removeListener(listener1);
		type.removeListener(listener2);
		
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testIsListener() throws Exception {
		type.addListener(listener2);
		type.addListener(listener3);
		type.removeListener(listener3);
		
		assertFalse(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertFalse(type.isListener(listener3));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(type.equals(type));
		assertTrue(type.equals(new EventTypeImpl(dispatcher, "MyType")));
		assertFalse(type.equals(null));
		assertFalse(type.equals(this));
	}
	
	@Test
	public void testOnce() throws Exception {
		ListenOnce actual = (ListenOnce) type.once(listener1);
		assertTrue(type.isListener(actual));
		assertSame(listener1, actual.getListener());
		assertSame(type, actual.getEventType());
	}
	
	@Test
	public void testRemoveListeners() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		
		type.removeListeners();
		
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testCountListeners() throws Exception {
		type.addListener(listener1);
		assertEquals(1, type.countListeners());
		type.addListener(listener2);
		assertEquals(2, type.countListeners());
		type.addListener(listener3);
		assertEquals(3, type.countListeners());
	}

	@Test
	public void testGetListeners() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener1);
		expected.add(listener2);
		
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testCompareListeners() throws Exception {
		List<EventListener> rows1 = new Vector<EventListener>();
		rows1.add(listener1);
		rows1.add(listener3);
		List<EventListener> rows2 = new Vector<EventListener>();
		rows2.add(listener3);
		List<EventListener> rows3 = new Vector<EventListener>();
		rows3.add(listener3);
		rows3.add(listener1);
		
		for ( EventListener l : rows1 ) { type.addListener(l); }
		
		Variant<List<EventListener>> vRows = new Variant<List<EventListener>>()
			.add(rows1)
			.add(rows2)
			.add(rows3);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		EventTypeImpl x, found = null;
		do {
			x = new EventTypeImpl(dispatcher);
			for ( EventListener l : vRows.get() ) { x.addListener(l); }
			if ( type.compareListeners(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rows1, found.getListeners());
	}

}
