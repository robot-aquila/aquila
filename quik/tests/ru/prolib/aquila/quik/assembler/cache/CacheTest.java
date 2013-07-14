package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.t2q.T2QOrder;

public class CacheTest {
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private DescriptorsCache descrs;
	private PositionsCache positions;
	private OrdersCache orders;
	private EventType type;
	private Cache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("BCS", "LX01");
		descr = new SecurityDescriptor("GAZP", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		descrs = control.createMock(DescriptorsCache.class);
		positions = control.createMock(PositionsCache.class);
		orders = control.createMock(OrdersCache.class);
		type = control.createMock(EventType.class);
		cache = new Cache(descrs, positions, orders);
	}
	
	@Test
	public void testGetDescriptors0() throws Exception {
		List<SecurityDescriptor> expected = new Vector<SecurityDescriptor>();
		expect(descrs.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getDescriptors());
		
		control.verify();
	}

	@Test
	public void testGetDescriptor1() throws Exception {
		expect(descrs.get(eq("foo"))).andReturn(descr);
		control.replay();
		
		assertSame(descr, cache.getDescriptor("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetDescriptor2() throws Exception {
		expect(descrs.get(eq("foo"), eq("bar"))).andReturn(descr);
		control.replay();
		
		assertSame(descr, cache.getDescriptor("foo", "bar"));
		
		control.verify();
	}
	
	@Test
	public void testOnDescriptorsUpdate() throws Exception {
		expect(descrs.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnDescriptorsUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_SecurityEntry() throws Exception {
		SecurityEntry entry = control.createMock(SecurityEntry.class);
		expect(descrs.put(same(entry))).andReturn(true);
		control.replay();
		
		assertTrue(cache.put(entry));
		
		control.verify();
	}
	
	@Test
	public void testGetPositions0() throws Exception {
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expect(positions.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getPositions());
		
		control.verify();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(positions.get(same(account), eq("sber"))).andReturn(entry);
		control.replay();
		
		assertSame(entry, cache.getPosition(account, "sber"));
		
		control.verify();
	}
	
	@Test
	public void testGetPositions1() throws Exception {
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expect(positions.get(eq("gazp"))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getPositions("gazp"));
		
		control.verify();
	}
	
	@Test
	public void testOnPositionsUpdate() throws Exception {
		expect(positions.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnPositionsUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_PositionEntry() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		positions.put(same(entry));
		control.replay();
		
		cache.put(entry);
		
		control.verify();
	}
	
	@Test
	public void testPurge_PositionEntry() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		positions.purge(same(entry));
		control.replay();
		
		cache.purge(entry);
		
		control.verify();
	}
	
	@Test
	public void testGetOrders() throws Exception {
		List<T2QOrder> expected = new Vector<T2QOrder>();
		expect(orders.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrders());
		
		control.verify();
	}
	
	@Test
	public void testGetOrder_ByLocalId() throws Exception {
		T2QOrder expected = control.createMock(T2QOrder.class);
		expect(orders.get(eq(12))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrder(12));
		
		control.verify();
	}
	
	@Test
	public void testGetOrder_BySystemId() throws Exception {
		T2QOrder expected = control.createMock(T2QOrder.class);
		expect(orders.get(eq(524L))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrder(524L));
		
		control.verify();
	}
	
	@Test
	public void testOnOrdersUpdate() throws Exception {
		expect(orders.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnOrdersUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_OrderEntry() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		orders.put(same(entry));
		control.replay();
		
		cache.put(entry);
		
		control.verify();
	}
	
	@Test
	public void testPurge_ByLocalOrderId() throws Exception {
		orders.purge(eq(815));
		control.replay();
		
		cache.purge(815);
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<DescriptorsCache> vDsc = new Variant<DescriptorsCache>()
			.add(descrs)
			.add(control.createMock(DescriptorsCache.class));
		Variant<PositionsCache> vPos = new Variant<PositionsCache>(vDsc)
			.add(positions)
			.add(control.createMock(PositionsCache.class));
		Variant<OrdersCache> vOrd = new Variant<OrdersCache>(vPos)
			.add(orders)
			.add(control.createMock(OrdersCache.class));
		Variant<?> iterator = vOrd;
		int foundCnt = 0;
		Cache x, found = null;
		do {
			x = new Cache(vDsc.get(), vPos.get(), vOrd.get());
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(descrs, found.getDescriptorsCache());
		assertSame(positions, found.getPositionsCache());
		assertSame(orders, found.getOrdersCache());
	}

}
