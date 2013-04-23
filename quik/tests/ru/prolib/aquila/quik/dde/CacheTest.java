package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class CacheTest {
	private IMocksControl control;
	private EventSystem es;
	private OrdersCache orders;
	private Cache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		orders = control.createMock(OrdersCache.class);
		cache = new Cache(orders);
	}
	
	@Test
	public void testCreateCache() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onOrdersUpdate = control.createMock(EventType.class);
		expect(es.createEventDispatcher("Cache")).andReturn(dispatcher);
		expect(es.createGenericType(dispatcher, "Orders"))
			.andReturn(onOrdersUpdate);
		control.replay();
		
		cache = Cache.createCache(es);
		
		control.verify();
		assertNotNull(cache);
		Cache expected = new Cache(new OrdersCache(dispatcher, onOrdersUpdate));
		assertEquals(expected, cache);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<OrdersCache> vOrders = new Variant<OrdersCache>()
			.add(orders)
			.add(control.createMock(OrdersCache.class));
		Variant<?> iterator = vOrders;
		int foundCnt = 0;
		Cache x = null, found = null;
		do {
			x = new Cache(vOrders.get());
			if ( cache.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(orders, found.getOrdersCache());
	}

}
