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
	private EventTypeImpl type;
	private EventListener listener1, listener2, listener3;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listener1 = control.createMock(EventListener.class);
		listener2 = control.createMock(EventListener.class);
		listener3 = control.createMock(EventListener.class);
		type = new EventTypeImpl("MyType");
	}
	
	@Test
	public void testConstruct0() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type = new EventTypeImpl();
		assertEquals("EvtType" + autoId, type.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertEquals("MyType", type.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("MyType", type.toString());
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
		assertTrue(type.equals(new EventTypeImpl("MyType")));
		assertFalse(type.equals(new EventTypeImpl("OtherType")));
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
			x = new EventTypeImpl();
			for ( EventListener l : vRows.get() ) { x.addListener(l); }
			if ( type.compareListeners(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rows1, found.getListeners());
	}
	
	/**
	 * Класс тестового наблюдателя, который при сравнении всегда дает
	 * положительный результат.
	 */
	private static class TestListener implements EventListener {

		@Override
		public void onEvent(Event event) {
			
		}
		
		@Override
		public boolean equals(Object other) {
			return true;
		}
		
	}

	@Test
	public void testListeners_SpecialCases() throws Exception {
		// Данный тест утверждает, что тип события работает с наблюдателями
		// в рамках экземпляров и не использует equals для проверки вхождения
		// в список.
		listener1 = new TestListener();
		listener2 = new TestListener();
		listener3 = new TestListener();
		
		type.addListener(listener1);
		assertTrue(type.isListener(listener1));
		assertFalse(type.isListener(listener2));
		assertFalse(type.isListener(listener3));
		List<EventListener> list = type.getListeners();
		assertEquals(1, list.size());
		assertSame(listener1, list.get(0));
		
		type.addListener(listener2);
		assertTrue(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertFalse(type.isListener(listener3));
		list = type.getListeners();
		assertEquals(2, list.size());
		assertSame(listener1, list.get(0));
		assertSame(listener2, list.get(1));

		type.addListener(listener3);
		assertTrue(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertTrue(type.isListener(listener3));
		list = type.getListeners();
		assertEquals(3, list.size());
		assertSame(listener1, list.get(0));
		assertSame(listener2, list.get(1));
		assertSame(listener3, list.get(2));
		
		type.removeListener(listener1);
		assertFalse(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertTrue(type.isListener(listener3));
		list = type.getListeners();
		assertEquals(2, list.size());
		assertSame(listener2, list.get(0));
		assertSame(listener3, list.get(1));
		
		
	}
}
