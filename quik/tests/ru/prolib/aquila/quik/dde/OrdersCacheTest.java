package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.OrderCache;
import ru.prolib.aquila.quik.dde.OrdersCache;

public class OrdersCacheTest {
	private IMocksControl control;
	private EventDispatcher dispatcher1, dispatcher2;
	private EventType type1, type2;
	private OrderCache order1, order2, order3, order4;
	private OrdersCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher1 = control.createMock(EventDispatcher.class);
		dispatcher2 = control.createMock(EventDispatcher.class);
		type1 = new EventTypeImpl(dispatcher1);
		type2 = new EventTypeImpl(dispatcher2);
		order1 = control.createMock(OrderCache.class);
		order2 = control.createMock(OrderCache.class);
		order3 = control.createMock(OrderCache.class);
		order4 = control.createMock(OrderCache.class);
		cache = new OrdersCache(dispatcher1, type1);
		
		expect(dispatcher1.asString()).andStubReturn("test");
		expect(dispatcher2.asString()).andStubReturn("foobar");
		expect(order1.getId()).andStubReturn(100L);
		expect(order2.getId()).andStubReturn(102L);
		expect(order3.getId()).andStubReturn(105L);
		expect(order4.getId()).andStubReturn(102L); // to replace
	}
	
	@Test
	public void testClear() throws Exception {
		control.replay();
		cache.put(order1);
		cache.put(order2);
		cache.put(order3);
		
		cache.clear();
		
		assertNull(cache.get(100L));
		assertNull(cache.get(102L));
		assertNull(cache.get(105L));
	}
	
	@Test
	public void testPutGet() throws Exception {
		control.replay();
		assertNull(cache.get(102L));
		cache.put(order2);
		assertSame(order2, cache.get(102L));
		cache.put(order4);
		assertSame(order4, cache.get(102L));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		control.replay();
		cache.put(order1);
		cache.put(order2);
		List<OrderCache> rows1 = new Vector<OrderCache>();
		rows1.add(order1);
		rows1.add(order2);
		List<OrderCache> rows2 = new Vector<OrderCache>();
		rows2.add(order3);
		Variant<List<OrderCache>> vRows = new Variant<List<OrderCache>>()
			.add(rows1)
			.add(rows2);
		Variant<EventType> vType = new Variant<EventType>(vRows)
			.add(type1)
			.add(type2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vType)
			.add(dispatcher1)
			.add(dispatcher2);
		Variant<?> iterator = vDisp;
		int foundCnt = 0;
		OrdersCache x = null, found = null;
		do {
			x = new OrdersCache(vDisp.get(), vType.get());
			for ( OrderCache order : vRows.get() ) {
				x.put(order);
			}
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(order1, found.get(100L));
		assertEquals(order2, found.get(102L));
		assertSame(type1, found.OnCacheUpdate());
		assertSame(dispatcher1, found.getEventDispatcher());
	}
	
	@Test
	public void testFireUpdateCache() throws Exception {
		dispatcher1.dispatch(eq(new EventImpl(type1)));
		control.replay();
		
		cache.fireUpdateCache();
		
		control.verify();
	}

}