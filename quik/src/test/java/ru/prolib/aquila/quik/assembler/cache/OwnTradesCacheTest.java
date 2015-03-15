package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QTrade;

public class OwnTradesCacheTest {
	private IMocksControl control;
	private T2QTrade trade1, trade2, trade3, trade4;
	private EventDispatcher dispatcher;
	private EventTypeSI type;
	private OwnTradesCache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		trade1 = control.createMock(T2QTrade.class);
		trade2 = control.createMock(T2QTrade.class);
		trade3 = control.createMock(T2QTrade.class);
		trade4 = control.createMock(T2QTrade.class);
		dispatcher = control.createMock(EventDispatcher.class);
		type = control.createMock(EventTypeSI.class);
		cache = new OwnTradesCache(dispatcher, type);
		
		expect(trade1.getOrderId()).andStubReturn(10L);
		expect(trade2.getOrderId()).andStubReturn(10L);
		expect(trade3.getOrderId()).andStubReturn(11L);
		expect(trade4.getOrderId()).andStubReturn(12L);
		
		expect(trade1.getId()).andStubReturn(100L);
		expect(trade2.getId()).andStubReturn(101L);
		expect(trade3.getId()).andStubReturn(102L);
		expect(trade4.getId()).andStubReturn(103L);
	}
	
	@Test
	public void testGet_All() throws Exception {
		control.replay();
		cache.set(trade1);
		cache.set(trade2);
		cache.set(trade3);
		cache.set(trade4);
		
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expected.add(trade1);
		expected.add(trade2);
		expected.add(trade3);
		expected.add(trade4);
		
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testGetByOrder() throws Exception {
		control.replay();
		cache.set(trade1);
		cache.set(trade2);
		cache.set(trade3);
		cache.set(trade4);
		
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expected.add(trade1);
		expected.add(trade2);
		
		assertEquals(expected, cache.getByOrder(10L));
	}
	
	@Test
	public void testGet_BySystemId() throws Exception {
		control.replay();
		cache.set(trade1);
		cache.set(trade2);
		cache.set(trade3);
		cache.set(trade4);

		assertSame(trade1, cache.get(100L));
		assertSame(trade2, cache.get(101L));
		assertSame(trade3, cache.get(102L));
		assertSame(trade4, cache.get(103L));
	}
	
	@Test
	public void testOnUpdate() throws Exception {
		assertSame(type, cache.OnUpdate());
	}
	
	@Test
	public void testPut() throws Exception {
		dispatcher.dispatch(eq(new EventImpl(type)));
		control.replay();
		
		cache.put(trade1);
		
		control.verify();
		assertSame(trade1, cache.get(100L));
	}
	
	@Test
	public void testPurge() throws Exception {
		dispatcher.dispatch(new EventImpl(type));
		control.replay();
		cache.set(trade1);
		cache.set(trade2);
		cache.set(trade3);
		cache.set(trade4);

		cache.purge(10L);
		
		control.verify();
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expected.add(trade3);
		expected.add(trade4);
		assertEquals(expected, cache.get());
	}
	
	@Test
	public void testPurge_NoEventsIfNotRemoved() throws Exception {
		control.replay();
		cache.set(trade1);
		cache.set(trade2);
		cache.set(trade3);
		cache.set(trade4);

		cache.purge(112L);
		
		control.verify();
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expected.add(trade1);
		expected.add(trade2);
		expected.add(trade3);
		expected.add(trade4);
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
		List<T2QTrade> rows1 = new Vector<T2QTrade>();
		rows1.add(trade1);
		rows1.add(trade2);
		List<T2QTrade> rows2 = new Vector<T2QTrade>();
		rows2.add(trade3);
		rows2.add(trade4);
		Variant<List<T2QTrade>> vRows = new Variant<List<T2QTrade>>()
			.add(rows1)
			.add(rows2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventTypeSI> vType = new Variant<EventTypeSI>(vDisp)
			.add(type)
			.add(control.createMock(EventTypeSI.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		OwnTradesCache x, found = null;
		control.replay();
		for ( T2QTrade entry : rows1 ) {
			cache.set(entry);
		}
		do {
			x = new OwnTradesCache(vDisp.get(), vType.get());
			for ( T2QTrade entry : vRows.get() ) {
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
	}


}
