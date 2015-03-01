package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

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
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testConstruct1_S() throws Exception {
		type = new EventTypeImpl("MyType");
		assertEquals("MyType", type.getId());
		assertFalse(type.isOnlySyncMode());
	}
	
	@Test
	public void testConstruct1_B() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type = new EventTypeImpl(true);
		assertEquals("EvtType" + autoId, type.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
		assertTrue(type.isOnlySyncMode());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		type = new EventTypeImpl("Zulu", true);
		assertEquals("Zulu", type.getId());
		assertTrue(type.isOnlySyncMode());
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
		assertEquals(expected, type.getAsyncListeners());
		assertEquals(new Vector<EventListener>(), type.getSyncListeners());
	}
	
	@Test
	public void testAddListener_OnlySyncMode() throws Exception {
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener1);
		expected.add(listener2);
		
		type = new EventTypeImpl(true);
		type.addListener(listener1);
		type.addListener(listener2);
		
		assertEquals(new Vector<EventListener>(), type.getAsyncListeners());
		assertEquals(expected, type.getSyncListeners());
	}
	
	@Test
	public void testAddListener_MoveSyncToAsync() throws Exception {
		type.addSyncListener(listener1);
		assertTrue(type.isSyncListener(listener1));
		type.addListener(listener1);
		assertFalse(type.isSyncListener(listener1));
		assertTrue(type.isAsyncListener(listener1));
	}
	
	@Test
	public void testAddSyncListener() throws Exception {
		type.addSyncListener(listener2);
		type.addSyncListener(listener3);
		type.addListener(listener1);
		
		Vector<EventListener> expected = new Vector<EventListener>();
		expected.add(listener2);
		expected.add(listener3);
		
		assertEquals(expected, type.getSyncListeners());
	}
	
	@Test
	public void testAddSyncListener_MoveAsyncToSync() throws Exception {
		type.addListener(listener1);
		assertTrue(type.isAsyncListener(listener1));
		type.addSyncListener(listener1);
		assertFalse(type.isAsyncListener(listener1));
		assertTrue(type.isSyncListener(listener1));
	}
	
	@Test
	public void testRemoveListener_Async() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener3);
		
		type.removeListener(listener1);
		type.removeListener(listener2);
		
		assertEquals(expected, type.getAsyncListeners());
	}
	
	@Test
	public void testRemoveListener_Sync() throws Exception {
		type.addSyncListener(listener1);
		type.addSyncListener(listener2);
		type.addSyncListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener3);
		
		type.removeListener(listener1);
		type.removeListener(listener2);
		
		assertEquals(expected, type.getSyncListeners());
	}
	
	@Test
	public void testIsListener() throws Exception {
		type.addSyncListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		type.removeListener(listener3);
		
		assertTrue(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertFalse(type.isListener(listener3));
	}
	
	@Test
	public void testIsAsyncListener() throws Exception {
		type.addListener(listener1);
		type.addSyncListener(listener2);
		assertTrue(type.isAsyncListener(listener1));
		assertFalse(type.isAsyncListener(listener2));
	}
	
	@Test
	public void testIsSyncListener() throws Exception {
		type.addListener(listener1);
		type.addSyncListener(listener2);
		assertFalse(type.isSyncListener(listener1));
		assertTrue(type.isSyncListener(listener2));
	}
	
	@Test
	public void testListenOnce() throws Exception {
		ListenOnce actual = (ListenOnce) type.listenOnce(listener1);
		assertTrue(type.isListener(actual));
		assertSame(listener1, actual.getListener());
		assertSame(type, actual.getEventType());
	}
	
	@Test
	public void testRemoveListeners() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addSyncListener(listener3);
		
		type.removeListeners();
		
		assertEquals(0, type.countListeners());
	}
	
	@Test
	public void testCountListeners() throws Exception {
		type.addListener(listener1);
		assertEquals(1, type.countListeners());
		type.addListener(listener2);
		assertEquals(2, type.countListeners());
		type.addSyncListener(listener3);
		assertEquals(3, type.countListeners());
	}

	@Test
	public void testGetAsyncListeners() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addSyncListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener1);
		expected.add(listener2);
		
		assertEquals(expected, type.getAsyncListeners());
	}
	
	@Test
	public void testGetSyncListeners() throws Exception {
		type.addSyncListener(listener1);
		type.addSyncListener(listener2);
		type.addListener(listener3);
		List<EventListener> expected = new Vector<EventListener>();
		expected.add(listener1);
		expected.add(listener2);
		
		assertEquals(expected, type.getSyncListeners());
	}
	
	@Test
	public void testAsyncListeners_SpecialCases() throws Exception {
		testListeners_SpecialCases(new HelperProxy() {
			@Override public void addListener(EventListener listener) {
				type.addListener(listener);
			}

			@Override public List<EventListener> getListeners() {
				return type.getAsyncListeners();
			}

			@Override public boolean isListener(EventListener listener) {
				return type.isAsyncListener(listener);
			}

			@Override public void removeListener(EventListener listener) {
				type.removeListener(listener);
			}
		});
	}

	@Test
	public void testSyncListeners_SpecialCases() throws Exception {
		testListeners_SpecialCases(new HelperProxy() {
			@Override public void addListener(EventListener listener) {
				type.addSyncListener(listener);
			}

			@Override public List<EventListener> getListeners() {
				return type.getSyncListeners();
			}

			@Override public boolean isListener(EventListener listener) {
				return type.isSyncListener(listener);
			}

			@Override public void removeListener(EventListener listener) {
				type.removeListener(listener);
			}
		});
	}

	/**
	 * Класс тестового наблюдателя, который при сравнении всегда дает
	 * положительный результат.
	 */
	private static class TestListener implements EventListener {
		@Override public void onEvent(Event event) { }
		@Override public boolean equals(Object other) { return true; }
	}
	
	/**
	 * Вспомогательный интерфейс для тестирования подписчиков.
	 */
	interface HelperProxy {
		public void addListener(EventListener listener);
		public List<EventListener> getListeners();
		public boolean isListener(EventListener listener);
		public void removeListener(EventListener listener);
	}

	private void testListeners_SpecialCases(HelperProxy proxy) throws Exception {
		// Данный тест утверждает, что тип события работает с наблюдателями
		// в рамках экземпляров и не использует equals для проверки вхождения
		// в список.
		listener1 = new TestListener();
		listener2 = new TestListener();
		listener3 = new TestListener();
		
		proxy.addListener(listener1);
		assertTrue(proxy.isListener(listener1));
		assertFalse(proxy.isListener(listener2));
		assertFalse(proxy.isListener(listener3));
		List<EventListener> list = proxy.getListeners();
		assertEquals(1, list.size());
		assertSame(listener1, list.get(0));
		
		proxy.addListener(listener2);
		assertTrue(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertFalse(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(2, list.size());
		assertSame(listener1, list.get(0));
		assertSame(listener2, list.get(1));

		proxy.addListener(listener3);
		assertTrue(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertTrue(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(3, list.size());
		assertSame(listener1, list.get(0));
		assertSame(listener2, list.get(1));
		assertSame(listener3, list.get(2));
		
		proxy.removeListener(listener1);
		assertFalse(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertTrue(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(2, list.size());
		assertSame(listener2, list.get(0));
		assertSame(listener3, list.get(1));
	}
	
}
