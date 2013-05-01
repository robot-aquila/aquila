package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class TradesCacheTest {
	private IMocksControl control;
	private EventDispatcher dispatcher1, dispatcher2;
	private EventType type1, type2;
	private TradeCache trade1, trade2, trade3, trade4;
	private TradesCache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * Создает кэш-запись сделки с указанными параметрами.
	 * <p>
	 * Фиксированные значения: цена 1.0, кол-во 1, объем 1.0.
	 * <p>
	 * @param id номер сделки
	 * @param time время сделки в формате yyyy-MM-dd HH:mm:ss
	 * @param orderId номер заявки
	 * @return кэш-запись сделки
	 * @throws Exception
	 */
	private TradeCache createEntry(Long id, String time, Long orderId)
			throws Exception
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return new TradeCache(id, format.parse(time), orderId, 1.0d, 1L, 1.0d);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher1 = control.createMock(EventDispatcher.class);
		dispatcher2 = control.createMock(EventDispatcher.class);
		type1 = new EventTypeImpl(dispatcher1);
		type2 = new EventTypeImpl(dispatcher2);
		trade1 = createEntry(100L, "2013-01-01 00:00:00", 421L);
		trade2 = createEntry(102L, "2013-01-01 00:00:01", 421L);
		trade3 = createEntry(105L, "2013-01-01 00:00:02", 420L);
		trade4 = createEntry(102L, "2013-01-01 00:00:01", 421L); // to replace
		cache = new TradesCache(dispatcher1, type1);
		
		expect(dispatcher1.asString()).andStubReturn("test");
		expect(dispatcher2.asString()).andStubReturn("foobar");
	}

	@Test
	public void testClear() throws Exception {
		cache.put(trade1);
		cache.put(trade2);
		cache.put(trade3);
		
		cache.clear();
		assertNull(cache.get(100L));
		assertNull(cache.get(102L));
		assertNull(cache.get(105L));
	}
	
	@Test
	public void testPutGet() throws Exception {
		assertNull(cache.get(102L));
		cache.put(trade1);
		cache.put(trade2);
		assertSame(trade1, cache.get(100L));
		assertSame(trade2, cache.get(102L));
		cache.put(trade4);
		assertNotSame(trade2, cache.get(102L));
		assertSame(trade4, cache.get(102L));
	}
	
	@Test
	public void testGetAll() throws Exception {
		cache.put(trade1);
		cache.put(trade3);
		cache.put(trade4);
		
		List<TradeCache> expected = new Vector<TradeCache>();
		expected.add(trade1);
		expected.add(trade3);
		expected.add(trade4);
		
		assertEquals(expected, cache.getAll());
	}
	
	@Test
	public void testGetAllByOrderId() throws Exception {
		cache.put(trade1);
		cache.put(trade2);
		cache.put(trade3);
		
		List<TradeCache> expected = new Vector<TradeCache>();
		expected.add(trade1);
		expected.add(trade2);
		assertEquals(expected, cache.getAllByOrderId(421L));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		cache.put(trade1);
		cache.put(trade2);
		
		List<TradeCache> rows1 = new Vector<TradeCache>();
		rows1.add(trade1);
		rows1.add(trade2);
		List<TradeCache> rows2 = new Vector<TradeCache>();
		rows2.add(trade3);
		rows2.add(trade1);
		Variant<List<TradeCache>> vRows = new Variant<List<TradeCache>>()
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
		TradesCache x = null, found = null;
		do {
			x = new TradesCache(vDisp.get(), vType.get());
			for ( TradeCache entry : vRows.get() ) {
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
		assertSame(trade1, found.get(100L));
		assertSame(trade2, found.get(102L));
	}

}
