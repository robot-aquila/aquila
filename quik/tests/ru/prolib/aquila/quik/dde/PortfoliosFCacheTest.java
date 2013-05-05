package ru.prolib.aquila.quik.dde;


import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class PortfoliosFCacheTest {
	private IMocksControl control;
	private EventDispatcher dispatcher1, dispatcher2;
	private EventType type1, type2;
	private PortfolioFCache port1, port2, port3, port4;
	private PortfoliosFCache cache;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher1 = control.createMock(EventDispatcher.class);
		dispatcher2 = control.createMock(EventDispatcher.class);
		type1 = new EventTypeImpl(dispatcher1);
		type2 = new EventTypeImpl(dispatcher2);
		port1 = new PortfolioFCache("eqe01", "SPBFUT", 10000.0, 8000.0, 200.0d);
		port2 = new PortfolioFCache("jmk01", "BUZZZZ", 14000.0, 5000.0, 100.0d);
		port3 = new PortfolioFCache("tbs01", "SPBFUT", 11000.0, 1000.0, 800.0d);
		port4 = new PortfolioFCache("eqe01", "SPBFUT", 11500.0, 7000.0, 120.0d);
		cache = new PortfoliosFCache(dispatcher1, type1);
		
		expect(dispatcher1.asString()).andStubReturn("test");
		expect(dispatcher2.asString()).andStubReturn("foobar");
	}
	
	@Test
	public void testClear() throws Exception {
		cache.put(port1);
		cache.put(port2);
		cache.put(port3);
		
		cache.clear();
		assertNull(cache.get("eqe01", "SPBFUT"));
		assertNull(cache.get("jmk01", "BUZZZZ"));
		assertNull(cache.get("tbs01", "SPBFUT"));
		assertEquals(new Vector<PortfolioFCache>(), cache.getAll());
	}
	
	@Test
	public void testPutGet() throws Exception {
		assertNull(cache.get("eqe01", "SPBFUT"));
		cache.put(port1);
		cache.put(port2);
		assertSame(port1, cache.get("eqe01", "SPBFUT"));
		assertSame(port2, cache.get("jmk01", "BUZZZZ"));
		cache.put(port4);
		assertNotSame(port1, cache.get("eqe01", "SPBFUT"));
		assertSame(port4, cache.get("eqe01", "SPBFUT"));
	}
	
	@Test
	public void testGetAll() throws Exception {
		cache.put(port1);
		cache.put(port2);
		cache.put(port3);
		
		List<PortfolioFCache> expected = new Vector<PortfolioFCache>();
		expected.add(port1);
		expected.add(port2);
		expected.add(port3);
		
		assertEquals(expected, cache.getAll());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		cache.put(port1);
		cache.put(port2);
		
		List<PortfolioFCache> rows1 = new Vector<PortfolioFCache>();
		rows1.add(port1);
		rows1.add(port2);
		List<PortfolioFCache> rows2 = new Vector<PortfolioFCache>();
		rows2.add(port3);
		rows2.add(port1);
		Variant<List<PortfolioFCache>> vRows =
				new Variant<List<PortfolioFCache>>()
			.add(rows1)
			.add(rows2);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher1)
			.add(dispatcher2);
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type1)
			.add(type2);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		PortfoliosFCache x = null, found = null;
		do {
			x = new PortfoliosFCache(vDisp.get(), vType.get());
			for ( PortfolioFCache entry : vRows.get() ) {
				x.put(entry);
			}
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher1, found.getEventDispatcher());
		assertSame(type1, found.OnCacheUpdate());
		assertEquals(rows1, found.getAll());
	}

}
