package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class PositionsFCacheTest {
	private EventSystem es;
	private IMocksControl control;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onUpdate;
	private PositionFCache pos1, pos2, pos3, pos4;
	private PositionsFCache cache;
	
	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		control = createStrictControl();
		dispatcher = es.createEventDispatcher("Cache");
		dispatcherMock = control.createMock(EventDispatcher.class);
		onUpdate = dispatcher.createType("OnUpdate");
		pos1 = new PositionFCache("eqe01", "SPBFUT", "RIM3", 1L, 5L, 200.0d);
		pos2 = new PositionFCache("jmk01", "BUZZZZ", "GAZP", 0L, 1L, -18.0d);
		pos3 = new PositionFCache("tbs01", "SPBFUT", "SBER", 8L, 8L, 12.0d);
		pos4 = new PositionFCache("eqe01", "SPBFUT", "RIM3", 2L, 1L, 10.0d);
		cache = new PositionsFCache(dispatcher, onUpdate);
		
		expect(dispatcherMock.asString()).andStubReturn("foobar");
	}
	
	@Test
	public void testClear() throws Exception {
		cache.put(pos1);
		cache.put(pos2);
		cache.put(pos3);
		
		cache.clear();
		assertNull(cache.get("eqe01", "SPBFUT", "RIM3"));
		assertNull(cache.get("jmk01", "BUZZZZ", "GAZP"));
		assertNull(cache.get("tbs01", "SPBFUT", "SBER"));
		assertEquals(new Vector<PositionFCache>(), cache.getAll());
	}

	@Test
	public void testPutGet() throws Exception {
		assertNull(cache.get("eqe01", "SPBFUT", "RIM3"));
		assertNull(cache.get("jmk01", "BUZZZZ", "GAZP"));
		cache.put(pos1);
		cache.put(pos2);
		assertSame(pos1, cache.get("eqe01", "SPBFUT", "RIM3"));
		assertSame(pos2, cache.get("jmk01", "BUZZZZ", "GAZP"));
		cache.put(pos4);
		assertNotSame(pos1, cache.get("eqe01", "SPBFUT", "RIM3"));
		assertSame(pos4, cache.get("eqe01", "SPBFUT", "RIM3"));
	}
	
	@Test
	public void testGetAll() throws Exception {
		cache.put(pos1);
		cache.put(pos2);
		cache.put(pos3);
		
		List<PositionFCache> expected = new Vector<PositionFCache>();
		expected.add(pos1);
		expected.add(pos2);
		expected.add(pos3);
		
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
		cache.put(pos1);
		cache.put(pos2);
		
		List<PositionFCache> rows1 = new Vector<PositionFCache>();
		rows1.add(pos1);
		rows1.add(pos2);
		List<PositionFCache> rows2 = new Vector<PositionFCache>();
		rows2.add(pos3);
		rows2.add(pos1);
		Variant<List<PositionFCache>> vRows =
				new Variant<List<PositionFCache>>()
			.add(rows1)
			.add(rows2);
		Variant<String> vDispId = new Variant<String>(vRows)
			.add("Cache")
			.add("Unknown");
		Variant<String> vUpdId = new Variant<String>(vDispId)
			.add("OnUpdate")
			.add("Someone");
		Variant<?> iterator = vUpdId;
		int foundCnt = 0;
		PositionsFCache x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new PositionsFCache(d, d.createType(vUpdId.get()));
			for ( PositionFCache entry : vRows.get() ) {
				x.put(entry);
			}
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onUpdate, found.OnCacheUpdate());
		assertEquals(rows1, found.getAll());
	}
	
	@Test
	public void testFireUpdateCache() throws Exception {
		cache = new PositionsFCache(dispatcherMock, onUpdate);
		dispatcherMock.dispatch(eq(new EventImpl(onUpdate)));
		control.replay();
		
		cache.fireUpdateCache();
		
		control.verify();
	}

}
