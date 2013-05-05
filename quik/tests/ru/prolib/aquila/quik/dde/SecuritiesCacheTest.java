package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class SecuritiesCacheTest {
	private static SecurityDescriptor desc1, desc2, desc3;
	private static SecurityCache sec1, sec2, sec3, sec4;
	private IMocksControl control;
	private EventDispatcher dispatcher1, dispatcher2;
	private EventType type1, type2;
	private SecuritiesCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		desc1 = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		desc2 = new SecurityDescriptor("RTS", "SPBFT", "USD", SecurityType.FUT);
		desc3 = new SecurityDescriptor("USD", "JPY", "JPY", SecurityType.CASH);
		/**
		 * Конструктор.
		 * <p>
		 * @param lotSize размер лота
		 * @param maxPrice максимально-возможная цена (null - не определена)
		 * @param minPrice минимально-возможная цена (null - не определена)
		 * @param minStepPrice цена минимального шага в валюте инструмента
		 * @param minStepSize минимальный шаг цены
		 * @param precision точность цены в десятичных знаках
		 * @param lastPrice цена последней сделки
		 * @param openPrice цена открытия последней сессии
		 * @param closePrice цена закрытия предыдущей сессии
		 * @param displayName полное наименование
		 * @param shortName краткое наименование
		 * @param askPrice цена предложения
		 * @param bidPrice цена спроса
		 * @param highPrice максимальная цена за сессию
		 * @param lowPrice минимальная цена за сессию
		 * @param descriptor дескриптор инструмента
		 */
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
		control = createStrictControl();
		dispatcher1 = control.createMock(EventDispatcher.class);
		dispatcher2 = control.createMock(EventDispatcher.class);
		type1 = new EventTypeImpl(dispatcher1);
		type2 = new EventTypeImpl(dispatcher2);
		cache = new SecuritiesCache(dispatcher1, type1);
		
		expect(dispatcher1.asString()).andStubReturn("foo");
		expect(dispatcher2.asString()).andStubReturn("bar");
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
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vRows)
			.add(dispatcher1)
			.add(dispatcher2);
		Variant<EventType> vType = new Variant<EventType>(vDisp)
			.add(type1)
			.add(type2);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		SecuritiesCache x = null, found = null;
		do {
			x = new SecuritiesCache(vDisp.get(), vType.get());
			for ( SecurityCache row : vRows.get() ) {
				x.put(row);
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
