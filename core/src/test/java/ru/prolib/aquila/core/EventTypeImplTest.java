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
	private EventTypeImpl type, type1, type2;
	private EventListener listener1, listener2, listener3;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listener1 = control.createMock(EventListener.class);
		listener2 = control.createMock(EventListener.class);
		listener3 = control.createMock(EventListener.class);
		type = new EventTypeImpl("MyType");
		type1 = new EventTypeImpl("Alternate1");
		type2 = new EventTypeImpl("Alternate2");
	}
	
	@Test
	public void testConstruct0() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type = new EventTypeImpl();
		assertEquals("EvtType" + autoId, type.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
	}
	
	@Test
	public void testConstruct1_S() throws Exception {
		type = new EventTypeImpl("MyType");
		assertEquals("MyType", type.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("MyType", type.toString());
	}
	
	@Test
	public void testAddListener() throws Exception {
		Set<EventListener> expected = new HashSet<>();
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
		Set<EventListener> expected = new HashSet<>();
		expected.add(listener3);
		
		type.removeListener(listener1);
		type.removeListener(listener2);
		
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testIsListener() throws Exception {
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		type.removeListener(listener3);
		
		assertTrue(type.isListener(listener1));
		assertTrue(type.isListener(listener2));
		assertFalse(type.isListener(listener3));
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
		
		type.removeListeners();
		
		assertEquals(0, type.countListeners());
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
		Set<EventListener> expected = new HashSet<>();
		expected.add(listener1);
		expected.add(listener2);
		
		assertEquals(expected, type.getListeners());
	}
	
	@Test
	public void testListeners_SpecialCases() throws Exception {
		testListeners_SpecialCases(new HelperProxy() {
			@Override public void addListener(EventListener listener) {
				type.addListener(listener);
			}

			@Override public Set<EventListener> getListeners() {
				return type.getListeners();
			}

			@Override public boolean isListener(EventListener listener) {
				return type.isListener(listener);
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
		public Set<EventListener> getListeners();
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
		Set<EventListener> list = proxy.getListeners();
		assertEquals(1, list.size());
		assertTrue(list.contains(listener1));
		
		proxy.addListener(listener2);
		assertTrue(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertFalse(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(2, list.size());
		assertTrue(list.contains(listener1));
		assertTrue(list.contains(listener2));

		proxy.addListener(listener3);
		assertTrue(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertTrue(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(3, list.size());
		assertTrue(list.contains(listener1));
		assertTrue(list.contains(listener2));
		assertTrue(list.contains(listener3));
		
		proxy.removeListener(listener1);
		assertFalse(proxy.isListener(listener1));
		assertTrue(proxy.isListener(listener2));
		assertTrue(proxy.isListener(listener3));
		list = proxy.getListeners();
		assertEquals(2, list.size());
		assertTrue(list.contains(listener2));
		assertTrue(list.contains(listener3));
	}
	
	@Test
	public void testAddAlternateType() throws Exception {
		type.addAlternateType(type1);
		
		Set<EventType> expected = new HashSet<EventType>();
		expected.add(type1);
		assertEquals(expected, type.getAlternateTypes());
	}
	
	@Test (expected=NullPointerException.class)
	public void testAddAlternateType_ThrowsNullPointer() throws Exception {
		type.addAlternateType(null);
	}
	
	@Test
	public void testIsAlternateType() throws Exception {
		type.addAlternateType(type1);
		
		assertTrue(type.isAlternateType(type1));
		assertFalse(type.isAlternateType(type2));
	}
	
	@Test
	public void testRemoveAlternateType() throws Exception {
		type.addAlternateType(type1);
		type.addAlternateType(type2);
		type.removeAlternateType(type1);
		
		assertFalse(type.isAlternateType(type1));
		assertTrue(type.isAlternateType(type2));
	}
	
	@Test
	public void testHasAlternates() throws Exception {
		assertFalse(type.hasAlternates());
		type.addAlternateType(type1);
		assertTrue(type.hasAlternates());
	}
	
	@Test
	public void testHasListeners() throws Exception {
		assertFalse(type.hasListeners());
		
		type.addListener(listener1);
		
		assertTrue(type.hasListeners());
		
		type.addListener(listener2);
		type.removeListener(listener1);
		
		assertTrue(type.hasListeners());
		
		type.removeListener(listener2);
		
		assertFalse(type.hasListeners());
	}
	
	@Test
	public void testRemoveAlternates() {
		type.addAlternateType(type1);
		type.addAlternateType(type2);
		
		type.removeAlternates();
		
		assertFalse(type.isAlternateType(type1));
		assertFalse(type.isAlternateType(type2));
	}
	
	@Test
	public void testCountAlternates() {
		assertEquals(0, type.countAlternates());
		
		type.addAlternateType(type1);
		type.addAlternateType(type2);
		
		assertEquals(2, type.countAlternates());
		
		type.removeAlternateType(type2);
		
		assertEquals(1, type.countAlternates());
	}
	
	@Test
	public void testRemoveAlternatesAndListeners() {
		type.addAlternateType(type1);
		type.addAlternateType(type2);
		type.addListener(listener1);
		type.addListener(listener2);
		type.addListener(listener3);
		
		type.removeAlternatesAndListeners();
		
		assertEquals(0, type.countAlternates());
		assertEquals(0, type.countListeners());
	}
	
}
