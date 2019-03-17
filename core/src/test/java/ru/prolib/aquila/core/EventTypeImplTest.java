package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.eque.HierarchyOfAlternatesListener;

/**
 * 2012-04-09
 * $Id: EventTypeImplTest.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventTypeImplTest {
	
	protected static <T> List<T> SetToList(Set<T> unordered) {
		List<T> ordered = new ArrayList<>();
		for ( T x : unordered ) {
			ordered.add(x);
		}
		return ordered;
	}
	
	@SafeVarargs
	protected static <T> List<T> HSetToList(T... objs) {
		Set<T> unordered = new LinkedHashSet<>();
		for ( T x : objs ) {
			unordered.add(x);
		}
		return SetToList(unordered);
	}
	
	@SafeVarargs
	protected static <T> Set<T> HSet(T... objs) {
		Set<T> unordered = new LinkedHashSet<>();
		for ( T x : objs ) {
			unordered.add(x);
		}
		return unordered;
	}

	private IMocksControl control;
	private EventTypeImpl type0, type1, type2, type3, type4;
	private EventListener listener1, listener2, listener3;
	private HierarchyOfAlternatesListener hoaListenerMock1, hoaListenerMock2;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		listener1 = control.createMock(EventListener.class);
		listener2 = control.createMock(EventListener.class);
		listener3 = control.createMock(EventListener.class);
		hoaListenerMock1 = control.createMock(HierarchyOfAlternatesListener.class);
		hoaListenerMock2 = control.createMock(HierarchyOfAlternatesListener.class);
		type0 = new EventTypeImpl("MyType");
		type1 = new EventTypeImpl("Alternate1");
		type2 = new EventTypeImpl("Alternate2");
		type3 = new EventTypeImpl("Alternate3");
		type4 = new EventTypeImpl("Alternate4");
	}
	
	@Test
	public void testConstruct0() throws Exception {
		int autoId = EventTypeImpl.getAutoId();
		type0 = new EventTypeImpl();
		assertEquals("EvtType" + autoId, type0.getId());
		assertEquals(autoId + 1, EventTypeImpl.getAutoId());
	}
	
	@Test
	public void testConstruct1_S() throws Exception {
		type0 = new EventTypeImpl("MyType");
		assertEquals("MyType", type0.getId());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("MyType", type0.toString());
	}
	
	@Test
	public void testAddListener() throws Exception {
		Set<EventListener> expected = new LinkedHashSet<>();
		expected.add(listener1);
		expected.add(listener2);
		expected.add(listener3);
		
		type0.addListener(listener1);
		type0.addListener(listener2);
		type0.addListener(listener3);
		assertEquals(expected, type0.getListeners());
	}
	
	@Test
	public void testRemoveListener() throws Exception {
		type0.addListener(listener1);
		type0.addListener(listener2);
		type0.addListener(listener3);
		Set<EventListener> expected = new LinkedHashSet<>();
		expected.add(listener3);
		
		type0.removeListener(listener1);
		type0.removeListener(listener2);
		
		assertEquals(expected, type0.getListeners());
	}
	
	@Test
	public void testIsListener() throws Exception {
		type0.addListener(listener1);
		type0.addListener(listener2);
		type0.addListener(listener3);
		type0.removeListener(listener3);
		
		assertTrue(type0.isListener(listener1));
		assertTrue(type0.isListener(listener2));
		assertFalse(type0.isListener(listener3));
	}
	
	@Test
	public void testListenOnce() throws Exception {
		ListenOnce actual = (ListenOnce) type0.listenOnce(listener1);
		assertTrue(type0.isListener(actual));
		assertSame(listener1, actual.getListener());
		assertSame(type0, actual.getEventType());
	}
	
	@Test
	public void testRemoveListeners() throws Exception {
		type0.addListener(listener1);
		type0.addListener(listener2);
		
		type0.removeListeners();
		
		assertEquals(0, type0.countListeners());
	}
	
	@Test
	public void testCountListeners() throws Exception {
		type0.addListener(listener1);
		assertEquals(1, type0.countListeners());
		type0.addListener(listener2);
		assertEquals(2, type0.countListeners());
		type0.addListener(listener3);
		assertEquals(3, type0.countListeners());
	}
	
	@Test
	public void testGetListeners() throws Exception {
		type0.addListener(listener1);
		type0.addListener(listener2);
		Set<EventListener> expected = new LinkedHashSet<>();
		expected.add(listener1);
		expected.add(listener2);
		
		assertEquals(expected, type0.getListeners());
	}
	
	@Test
	public void testListeners_SpecialCases() throws Exception {
		testListeners_SpecialCases(new HelperProxy() {
			@Override public void addListener(EventListener listener) {
				type0.addListener(listener);
			}

			@Override public Set<EventListener> getListeners() {
				return type0.getListeners();
			}

			@Override public boolean isListener(EventListener listener) {
				return type0.isListener(listener);
			}

			@Override public void removeListener(EventListener listener) {
				type0.removeListener(listener);
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
		type0.addAlternateType(type1);
		
		Set<EventType> expected = new LinkedHashSet<EventType>();
		expected.add(type1);
		assertEquals(expected, type0.getAlternateTypes());
	}
	
	@Test (expected=NullPointerException.class)
	public void testAddAlternateType_ThrowsNullPointer() throws Exception {
		type0.addAlternateType(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddAlternateType_ThrowsIfThis() {
		type0.addAlternateType(type0);
	}
	
	@Test
	public void testIsAlternateType() throws Exception {
		type0.addAlternateType(type1);
		
		assertTrue(type0.isAlternateType(type1));
		assertFalse(type0.isAlternateType(type2));
	}
	
	@Test
	public void testRemoveAlternateType() throws Exception {
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		type0.removeAlternateType(type1);
		
		assertFalse(type0.isAlternateType(type1));
		assertTrue(type0.isAlternateType(type2));
	}
	
	@Test
	public void testHasAlternates() throws Exception {
		assertFalse(type0.hasAlternates());
		type0.addAlternateType(type1);
		assertTrue(type0.hasAlternates());
	}
	
	@Test
	public void testHasListeners() throws Exception {
		assertFalse(type0.hasListeners());
		
		type0.addListener(listener1);
		
		assertTrue(type0.hasListeners());
		
		type0.addListener(listener2);
		type0.removeListener(listener1);
		
		assertTrue(type0.hasListeners());
		
		type0.removeListener(listener2);
		
		assertFalse(type0.hasListeners());
	}
	
	@Test
	public void testRemoveAlternates() {
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		
		type0.removeAlternates();
		
		assertFalse(type0.isAlternateType(type1));
		assertFalse(type0.isAlternateType(type2));
	}
	
	@Test
	public void testCountAlternates() {
		assertEquals(0, type0.countAlternates());
		
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		
		assertEquals(2, type0.countAlternates());
		
		type0.removeAlternateType(type2);
		
		assertEquals(1, type0.countAlternates());
	}
	
	@Test
	public void testRemoveAlternatesAndListeners() {
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		type0.addListener(listener1);
		type0.addListener(listener2);
		type0.addListener(listener3);
		
		type0.removeAlternatesAndListeners();
		
		assertEquals(0, type0.countAlternates());
		assertEquals(0, type0.countListeners());
	}

	@Test
	public void testHOA_AddAlternateType_CacheAllRelated() {
		type1.addAlternateType(type2);
		
		type0.addAlternateType(type1);

		assertTrue(type1.isListener(type0));
		Set<EventType> expected = new LinkedHashSet<>();
		expected.add(type0);
		expected.add(type1);
		expected.add(type2);
		assertEquals(expected, type0.getFullListOfRelatedTypes());
	}
	
	@Test
	public void testHOA_AddAlternateType_NotifyHOAListeners() {
		// Prepare hierarchy and objects
		type1.addAlternateType(type2);
		type0.addListener(hoaListenerMock1);
		type0.addListener(hoaListenerMock2);
		// Setup expectations
		List<HierarchyOfAlternatesListener> hoals = HSetToList(hoaListenerMock1, hoaListenerMock2);
		for ( HierarchyOfAlternatesListener hoal : hoals ) {
			hoal.onHierarchyOfAlternatesChange(anyObject());
		}
		control.replay();
		
		type0.addAlternateType(type1);
		
		control.verify();
	}
	
	@Test
	public void testHOA_AddAlternateType_CyclicReferences() {
		type1.addAlternateType(type0);
		control.replay();
		
		type0.addAlternateType(type1);

		control.verify();
	}
	
	@Test
	public void testHOA_AddAlternateType_SubscribeToNotifications() {
		EventType typeMock = control.createMock(EventType.class);
		typeMock.addListener(type0);
		expect(typeMock.getAlternateTypes()).andReturn(new LinkedHashSet<>());
		control.replay();
		
		type0.addAlternateType(typeMock);
		
		control.verify();
	}

	@Test
	public void testHOA_RemoveAlternateType_RemoveCached() {
		type1.addAlternateType(type2);
		type0.addAlternateType(type1);
		type0.addAlternateType(type3);
		
		type0.removeAlternateType(type1);
		
		Set<EventType> expected = new LinkedHashSet<>();
		expected.add(type0);
		expected.add(type3);
		assertEquals(expected, type0.getFullListOfRelatedTypes());
	}

	@Test
	public void testHOA_RemoveAlternateType_IfUsedMoreThanOnce1() {
		type1.addAlternateType(type2);
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		
		type0.removeAlternateType(type1);
		
		Set<EventType> expected = new LinkedHashSet<>();
		expected.add(type0);
		expected.add(type2);
		assertEquals(expected, type0.getFullListOfRelatedTypes());
	}
	
	@Test
	public void testHOA_RemoveAlternateType_IfUsedMoreThanOnce2() {
		type1.addAlternateType(type2);
		type0.addAlternateType(type1);
		type0.addAlternateType(type2);
		
		type0.removeAlternateType(type2);
		
		Set<EventType> expected = new LinkedHashSet<>();
		expected.add(type0);
		expected.add(type1);
		expected.add(type2);
		assertEquals(expected, type0.getFullListOfRelatedTypes());
	}

	@Test
	public void testHOA_RemoveAlternateType_NotifyHOAListeners() {
		type0.addAlternateType(type1);
		type0.addListener(hoaListenerMock1);
		hoaListenerMock1.onHierarchyOfAlternatesChange(anyObject());
		control.replay();
		
		type0.removeAlternateType(type1);
		
		control.verify();
	}
	
	@Test
	public void testHOA_RemoveAlternateType_UnsubscribeFromNotifications() {
		EventType typeMock = control.createMock(EventType.class);
		typeMock.addListener(type0);
		expect(typeMock.getAlternateTypes()).andReturn(new LinkedHashSet<>());
		control.replay();
		type0.addAlternateType(typeMock);
		control.resetToStrict();
		typeMock.removeListener(type0);
		control.replay();
		
		type0.removeAlternateType(typeMock);
		
		control.verify();
	}
	
	@Test
	public void testHOA_RemoveAlternates() {
		type3.addAlternateType(type1);
		type3.addAlternateType(type2);
		type0.addAlternateType(type3);
		type0.addAlternateType(type4);
		
		type3.removeAlternates();
		
		assertEquals(HSet(type0, type3, type4), type0.getFullListOfRelatedTypes());
	}
	
	@Test
	public void testHOA_RemoveAlternatesAndListeners() {
		type3.addAlternateType(type1);
		type3.addAlternateType(type2);
		type0.addAlternateType(type3);
		type0.addAlternateType(type4);
		
		type3.removeAlternatesAndListeners();
		
		assertEquals(HSet(type0, type3, type4), type0.getFullListOfRelatedTypes());
	}
	
	@Test
	public void testHOA_NotifyWithCyclicReferences() {
		type2.addAlternateType(type3);
		type3.addAlternateType(type0);
		
		type0.addAlternateType(type1);
		
		assertEquals(HSet(type0, type1), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2, type3, type1, type0), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());

		type0.addAlternateType(type2);
		
		assertEquals(HSet(type0, type1, type2, type3), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2, type3, type1, type0), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type2, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());

		type3.addAlternateType(type4);
		
		assertEquals(HSet(type0, type1, type2, type3, type4), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2, type4, type3, type1, type0), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type4, type2, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());

		type0.addAlternateType(type3);
		
		assertEquals(HSet(type0, type1, type2, type3, type4), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2, type4, type3, type1, type0), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type4, type2, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());
		
		type2.removeAlternateType(type3);
		
		assertEquals(HSet(type0, type1, type2, type3, type4), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type4, type2, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());
		
		type0.removeAlternateType(type3);

		assertEquals(HSet(type0, type2, type1), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type4, type2, type1, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());

		type0.removeAlternateType(type2);
		type0.removeAlternateType(type1);
		
		assertEquals(HSet(type0), type0.getFullListOfRelatedTypes());
		assertEquals(HSet(type1), type1.getFullListOfRelatedTypes());
		assertEquals(HSet(type2), type2.getFullListOfRelatedTypes());
		assertEquals(HSet(type3, type4, type0), type3.getFullListOfRelatedTypes());
		assertEquals(HSet(type4), type4.getFullListOfRelatedTypes());
	}
	
}
