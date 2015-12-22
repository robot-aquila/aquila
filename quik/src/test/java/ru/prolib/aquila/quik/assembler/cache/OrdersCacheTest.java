package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QOrder;

public class OrdersCacheTest {
	private IMocksControl control;
	private T2QOrder order1, order2, order3, order4;
	private EventDispatcher dispatcher;
	private EventType type;
	private OrdersCache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		order1 = control.createMock(T2QOrder.class);
		order2 = control.createMock(T2QOrder.class);
		order3 = control.createMock(T2QOrder.class);
		order4 = control.createMock(T2QOrder.class);
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventType.class);
		cache = new OrdersCache(dispatcher, type);
		
		expect(order1.getOrderId()).andStubReturn(10L);
		expect(order1.getTransId()).andStubReturn(0L);
		expect(order2.getOrderId()).andStubReturn(11L);
		expect(order2.getTransId()).andStubReturn(5L);
		expect(order3.getOrderId()).andStubReturn(12L);
		expect(order3.getTransId()).andStubReturn(6L);
		expect(order4.getOrderId()).andStubReturn(13L);
		expect(order4.getTransId()).andStubReturn(0L);
	}
	
	@Test
	public void testGet_All() throws Exception {
		control.replay();
		cache.set(order1);
		cache.set(order2);
		cache.set(order3);
		cache.set(order4);
		
		List<T2QOrder> expected = new Vector<T2QOrder>();
		expected.add(order1);
		expected.add(order2);
		expected.add(order3);
		expected.add(order4);
		
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testGet_ByLocalId() throws Exception {
		control.replay();
		cache.set(order1);
		cache.set(order2);
		cache.set(order4); // duplicated by local ID
		
		assertSame(order1, cache.get(0));
		assertSame(order2, cache.get(5));
		assertNull(cache.get(6));
	}
	
	@Test
	public void testGet_BySystemId() throws Exception {
		control.replay();
		cache.set(order1);
		cache.set(order2);
		cache.set(order4);
		
		assertSame(order1, cache.get(10L));
		assertSame(order2, cache.get(11L));
		assertSame(order4, cache.get(13L));
		assertNull(cache.get(215L));
	}
	
	@Test
	public void testPut() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		cache.put(order1);
		
		control.verify();
		assertSame(order1, cache.get(10L));
	}
	
	@Test
	public void testPurge() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		expectLastCall().times(2);
		control.replay();
		cache.set(order1);
		cache.set(order2);
		cache.set(order3);
		cache.set(order4);
		
		cache.purge(0);
		cache.purge(5);
		
		List<T2QOrder> expected = new Vector<T2QOrder>();
		expected.add(order3);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPurge_NoEventsIfNotRemoved() throws Exception {
		control.replay();
		cache.set(order1);
		cache.set(order2);
		cache.set(order3);
		cache.set(order4);
		
		cache.purge(824);
		
		List<T2QOrder> expected = new Vector<T2QOrder>();
		expected.add(order1);
		expected.add(order2);
		expected.add(order3);
		expected.add(order4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<T2QOrder> rows1 = new Vector<T2QOrder>();
		rows1.add(order1);
		rows1.add(order2);
		List<T2QOrder> rows2 = new Vector<T2QOrder>();
		rows2.add(order3);
		rows2.add(order4);
		Variant<List<T2QOrder>> vRows = new Variant<List<T2QOrder>>()
			.add(rows1)
			.add(rows2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		OrdersCache x, found = null;
		control.replay();
		for ( T2QOrder entry : rows1 ) {
			cache.set(entry);
		}
		do {
			x = new OrdersCache(vDisp.get(), vType.get());
			for ( T2QOrder entry : vRows.get() ) {
				x.set(entry);
			}
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(type, found.OnUpdate());
		assertEquals(rows1, found.get());
	}

}
