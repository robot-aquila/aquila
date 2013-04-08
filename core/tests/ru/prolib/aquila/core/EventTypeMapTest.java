package ru.prolib.aquila.core;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.*;

public class EventTypeMapTest {
	private IMocksControl control;
	private EventSystem es;
	private EventDispatcher dispatcher;
	private Hashtable<Integer, EventType> storage;
	private EventTypeMap<Integer> map;
	private EventType type1, type2;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		dispatcher = control.createMock(EventDispatcher.class);
		storage = control.createMock(Hashtable.class);
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		map = new EventTypeMap<Integer>(es, dispatcher, storage);
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(es, map.getEventSystem());
		assertSame(dispatcher, map.getEventDispatcher());
		assertSame(storage, map.getUnderlyingMap());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		map = new EventTypeMap<Integer>(es, dispatcher);
		assertSame(es, map.getEventSystem());
		assertSame(dispatcher, map.getEventDispatcher());
		assertEquals(new Hashtable<Integer,EventType>(),map.getUnderlyingMap());
	}
	
	@Test
	public void testClear() throws Exception {
		storage.clear();
		control.replay();
		
		map.clear();
		
		control.verify();
	}
	
	@Test
	public void testContainsKey() throws Exception {
		expect(storage.containsKey(eq(123))).andReturn(true);
		expect(storage.containsKey(eq(654))).andReturn(false);
		control.replay();
		
		assertTrue(map.containsKey(123));
		assertFalse(map.containsKey(654));
		
		control.verify();
	}
	
	@Test
	public void testContainsValue() throws Exception {
		expect(storage.containsValue(same(type1))).andReturn(true);
		expect(storage.containsValue(same(type2))).andReturn(false);
		control.replay();
		
		assertTrue(map.containsValue(type1));
		assertFalse(map.containsValue(type2));
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEntrySet() throws Exception {
		Set<Map.Entry<Integer, EventType>> expected =
			control.createMock(Set.class);
		expect(storage.entrySet()).andReturn(expected);
		control.replay();
		
		assertSame(expected, map.entrySet());
		
		control.verify();
	}
	
	@Test
	public void testGet_FirstTime() throws Exception {
		expect(storage.get(eq(765))).andReturn(null);
		expect(es.createGenericType(dispatcher, "765")).andReturn(type1);
		expect(storage.put(eq(765), same(type1))).andReturn(null);
		control.replay();
		
		assertSame(type1, map.get(765));
		
		control.verify();
	}
	
	@Test
	public void testGet_NextTime() throws Exception {
		expect(storage.get(eq(987))).andReturn(type2);
		control.replay();
		
		assertSame(type2, map.get(987));
		
		control.verify();
	}

	@Test
	public void testIsEmpty() throws Exception {
		expect(storage.isEmpty()).andReturn(true);
		expect(storage.isEmpty()).andReturn(false);
		control.replay();
		
		assertTrue(map.isEmpty());
		assertFalse(map.isEmpty());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testKeySet() throws Exception {
		Set<Integer> expected = control.createMock(Set.class);
		expect(storage.keySet()).andReturn(expected);
		control.replay();
		
		assertSame(expected, map.keySet());
		
		control.verify();
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testPut() throws Exception {
		map.put(248, type1);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testPutAll() throws Exception {
		map.putAll(storage);
	}
	
	@Test
	public void testRemove() throws Exception {
		expect(storage.remove(eq(876))).andReturn(type2);
		control.replay();
		
		assertSame(type2, map.remove(876));
		
		control.verify();
	}
	
	@Test
	public void testSize() throws Exception {
		expect(storage.size()).andReturn(34);
		control.replay();
		
		assertEquals(34, map.size());
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testValues() throws Exception {
		Collection<EventType> expected = control.createMock(Collection.class);
		expect(storage.values()).andReturn(expected);
		control.replay();
		
		assertSame(expected, map.values());
		
		control.verify();
	}
	
}
