package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class OrdersCacheTest {
	private EventSystem es;
	private IMocksControl control;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onUpdate;
	private OrderCache order1, order2, order3, order4;
	private OrdersCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcherMock = control.createMock(EventDispatcher.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Cache");
		onUpdate = dispatcher.createType("OnUpdate");
		order1 = control.createMock(OrderCache.class);
		order2 = control.createMock(OrderCache.class);
		order3 = control.createMock(OrderCache.class);
		order4 = control.createMock(OrderCache.class);
		cache = new OrdersCache(dispatcher, onUpdate);
		
		expect(order1.getId()).andStubReturn(100L);
		expect(order2.getId()).andStubReturn(102L);
		expect(order3.getId()).andStubReturn(105L);
		expect(order4.getId()).andStubReturn(102L); // to replace
		
		expect(order1.getTransId()).andStubReturn(null);
		expect(order2.getTransId()).andStubReturn(824L);
		expect(order3.getTransId()).andStubReturn(0L);
		expect(order4.getTransId()).andStubReturn(112L);
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
	public void testGetAll() throws Exception {
		control.replay();
		cache.put(order1);
		cache.put(order3);
		
		List<OrderCache> expected = new LinkedList<OrderCache>();
		expected.add(order1);
		expected.add(order3);
		
		List<OrderCache> actual = cache.getAll();
		control.verify();
		assertEquals(expected, actual);
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
		Variant<String> vUpdId = new Variant<String>(vRows)
			.add("OnUpdate")
			.add("OnAnother");
		Variant<String> vDispId = new Variant<String>(vUpdId)
			.add("Cache")
			.add("Unknown");
		Variant<?> iterator = vDispId;
		int foundCnt = 0;
		OrdersCache x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new OrdersCache(d, d.createType(vUpdId.get()));
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
		assertEquals(onUpdate, found.OnCacheUpdate());
		assertEquals(dispatcher, found.getEventDispatcher());
	}
	
	@Test
	public void testFireUpdateCache() throws Exception {
		cache = new OrdersCache(dispatcherMock, onUpdate);
		dispatcherMock.dispatch(eq(new EventImpl(onUpdate)));
		control.replay();
		
		cache.fireUpdateCache();
		
		control.verify();
	}

}
