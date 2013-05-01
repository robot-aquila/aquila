package ru.prolib.aquila.quik.dde;


import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class TradeCacheTest {
	private static SimpleDateFormat timeFormat;
	private TradeCache row;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		row = new TradeCache(20L, timeFormat.parse("2013-01-01 03:30:00"),
				208L, 12.34d, 100L, 1200.34d);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Long> vId = new Variant<Long>()
			.add(20L)
			.add(380L);
		Variant<Date> vTime = new Variant<Date>(vId)
			.add(timeFormat.parse("2013-01-01 03:30:00"))
			.add(timeFormat.parse("1999-06-19 15:45:30"));
		Variant<Long> vOrdId = new Variant<Long>(vTime)
			.add(208L)
			.add(726L);
		Variant<Double> vPrice = new Variant<Double>(vOrdId)
			.add(12.34d)
			.add(28.17d);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(100L)
			.add(200L);
		Variant<Double> vVol = new Variant<Double>(vQty)
			.add(1200.34d)
			.add(863.05d);
		Variant<?> iterator = vVol;
		int foundCnt = 0;
		TradeCache x = null, found = null;
		do {
			x = new TradeCache(vId.get(), vTime.get(), vOrdId.get(),
					vPrice.get(), vQty.get(), vVol.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals((Long) 20L, found.getId());
		assertEquals(timeFormat.parse("2013-01-01 03:30:00"), found.getTime());
		assertEquals((Long) 208L, found.getOrderId());
		assertEquals((Double) 12.34d, found.getPrice());
		assertEquals((Long) 100L, found.getQty());
		assertEquals((Double) 1200.34d, found.getVolume());
	}

}
