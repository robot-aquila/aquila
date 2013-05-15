package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class SecuritiesCacheTest {
	private static SecurityDescriptor desc1, desc2, desc3;
	private static SecurityCache sec1, sec2, sec3, sec4;
	private EventSystem es;
	private IMocksControl control;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onUpdate;
	private SecuritiesCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		desc1 = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		desc2 = new SecurityDescriptor("RTS", "SPBFT", "USD", SecurityType.FUT);
		desc3 = new SecurityDescriptor("USD", "JPY", "JPY", SecurityType.CASH);
		sec1 = new SecurityCache(10, 200.00d, 180.00d, 0.5d, 0.5d, 1,
				199.00d, 185.00d, 184.00d, "Сбербанк АО", "Сбер",
				187.00d, 187.50d, 198.95d, 183.00d, desc1);
		sec2 = new SecurityCache(20, 400.00d, 380.00d, 0.1d, 0.2d, 2,
				299.00d, 285.00d, 284.00d, "Фьючерс РТС", "Фьюч",
				287.00d, 287.50d, 298.95d, 283.00d, desc2);
		sec3 = new SecurityCache(5, 100.00d, 120.00d, 0.2d, 0.4d, 5,
				399.00d, 385.00d, 384.00d, "Forex JPY", "JPY/USD",
				387.00d, 387.50d, 398.95d, 383.00d, desc3);
		// для замены на место sec1
		sec4 = new SecurityCache(12, 100.00d, 80.00d, 0.01d, 0.02d, 5,
				109.00d, 105.00d, 104.00d, "СБЕРБАНК АО", "СБЕР",
				107.00d, 107.50d, 108.95d, 103.00d, desc1);
	}
	
	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		control = createStrictControl();
		dispatcher = es.createEventDispatcher("Cache");
		onUpdate = dispatcher.createType("OnUpdate");
		dispatcherMock = control.createMock(EventDispatcher.class);
		cache = new SecuritiesCache(dispatcher, onUpdate);
		
		expect(dispatcherMock.asString()).andStubReturn("bar");
	}
	
	@Test
	public void testClear() throws Exception {
		cache.put(sec1);
		cache.put(sec2);
		cache.put(sec3);
		
		cache.clear();
		assertNull(cache.get(desc1));
		assertNull(cache.get(desc2));
		assertNull(cache.get(desc3));
		assertEquals(new Vector<SecurityCache>(), cache.getAll());
	}
	
	@Test
	public void testPutGet() throws Exception {
		assertNull(cache.get(desc1));
		cache.put(sec1);
		cache.put(sec2);
		assertSame(sec1, cache.get(desc1));
		assertSame(sec2, cache.get(desc2));
		cache.put(sec4);
		assertNotSame(sec1, cache.get(desc1));
		assertSame(sec4, cache.get(desc1));
	}
	
	@Test
	public void testGetAll() throws Exception {
		cache.put(sec1);
		cache.put(sec3);
		
		List<SecurityCache> expected = new Vector<SecurityCache>();
		expected.add(sec1);
		expected.add(sec3);
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
		cache.put(sec2);
		cache.put(sec3);
		
		List<SecurityCache> rows1 = new Vector<SecurityCache>();
		rows1.add(sec2);
		rows1.add(sec3);
		List<SecurityCache> rows2 = new Vector<SecurityCache>();
		rows2.add(sec1);
		rows2.add(sec2);
		rows2.add(sec3);
		Variant<List<SecurityCache>> vRows = new Variant<List<SecurityCache>>()
			.add(rows1)
			.add(rows2);
		Variant<String> vDispId = new Variant<String>(vRows)
			.add("Cache")
			.add("Unknown");
		Variant<String> vUpdId = new Variant<String>(vDispId)
			.add("OnUpdate")
			.add("OnSome");
		Variant<?> iterator = vUpdId;
		int foundCnt = 0;
		SecuritiesCache x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new SecuritiesCache(d, d.createType(vUpdId.get()));
			for ( SecurityCache row : vRows.get() ) {
				x.put(row);
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
		cache = new SecuritiesCache(dispatcherMock, onUpdate);
		dispatcherMock.dispatch(eq(new EventImpl(onUpdate)));
		control.replay();
		
		cache.fireUpdateCache();
		
		control.verify();
	}

}
