package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;

public class StopOrdersGatewayTest {
	private static SimpleDateFormat timeFormat;
	private IMocksControl control;
	private StopOrdersCache cache;
	private RowDataConverter converter;
	private StopOrdersGateway gateway;
	private Map<String, Object> map;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(StopOrdersCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new StopOrdersGateway(cache, converter);
		map = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
				"STOP_ORDERNUM",
				"TRANSID",
				"STATUS",
				"SECCODE",
				"CLASSCODE",
				"ACCOUNT",
				"CLIENTCODE",
				"BUYSELL",
				"QTY",
				"PRICE",
				"STOP_ORDERKIND",
				"LINKED_ORDER",
				"CONDITION_PRICE",
				"CONDITION_PRICE2",
				"OFFSET",
				"OFFSET_UNITS",
				"SPREAD",
				"SPREAD_UNITS",
				"STOP_ORDERDATE",
				"STOP_ORDERTIME",
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testToCache1() throws Exception {
		// активный стоп-лимит на покупку, с номером транзы
		map.put("STOP_ORDERNUM", 150.0d);
		map.put("TRANSID", 200.0d);
		map.put("STATUS", "ACTIVE");
		map.put("SECCODE", "RIM3");
		map.put("CLASSCODE", "SPBFUT");
		map.put("ACCOUNT", "eqe01");
		map.put("CLIENTCODE", "3466");
		map.put("BUYSELL", "B");
		map.put("QTY", 10.0d);
		map.put("PRICE", 149100.0d);
		map.put("STOP_ORDERKIND", "Стоп-лимит");
		map.put("LINKED_ORDER", 0.0d);
		map.put("CONDITION_PRICE", 149000.0d);
		map.put("CONDITION_PRICE2", 0.0d);
		map.put("OFFSET", 0.0d);
		map.put("OFFSET_UNITS", "");
		map.put("SPREAD", 0.0d);
		map.put("SPREAD_UNITS", "");
		map.put("STOP_ORDERDATE", "2013-02-22");
		map.put("STOP_ORDERTIME", "19:03:00");
		Row row = new SimpleRow(map);
		StopOrderCache expected = new StopOrderCache(150L, 200L,
				OrderStatus.ACTIVE, "RIM3", "SPBFUT", "eqe01", "3466",
				OrderDirection.BUY, 10L, 149100.0d, 149000.0d,
				null, null, null, null,
				timeFormat.parse("2013-02-22 19:03:00"),
				null,
				OrderType.STOP_LIMIT);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testToCache2() throws Exception {
		// снятый тэйк-профит на продажу, без номера транзы
		map.put("STOP_ORDERNUM", 153.0d);
		map.put("TRANSID", 0.0d);
		map.put("STATUS", "KILLED");
		map.put("SECCODE", "GAZP");
		map.put("CLASSCODE", "EQBR");
		map.put("ACCOUNT", "LX01");
		map.put("CLIENTCODE", "88s2");
		map.put("BUYSELL", "S");
		map.put("QTY", 20.0d);
		map.put("PRICE", 0.0d);
		map.put("STOP_ORDERKIND", "Тэйк-профит");
		map.put("LINKED_ORDER", 0.0d);
		map.put("CONDITION_PRICE", 150000.0d);
		map.put("CONDITION_PRICE2", 0.0d);
		map.put("OFFSET", 1.0d);
		map.put("OFFSET_UNITS", "%");
		map.put("SPREAD", 20.0d);
		map.put("SPREAD_UNITS", "Д");
		map.put("STOP_ORDERDATE", "2010-01-01");
		map.put("STOP_ORDERTIME", "12:00:00");
		Row row = new SimpleRow(map);
		StopOrderCache expected = new StopOrderCache(153L, null,
				OrderStatus.CANCELLED, "GAZP", "EQBR", "LX01", "88s2",
				OrderDirection.SELL, 20L, null, null, 150000.0d,
				new Price(PriceUnit.PERCENT, 1.0d),
				new Price(PriceUnit.MONEY, 20.0d),
				null,
				timeFormat.parse("2010-01-01 12:00:00"),
				null,
				OrderType.TAKE_PROFIT);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}

	@Test
	public void testToCache3() throws Exception {
		// исполненный тэк-профит&стоп-лимит на покупку
		map.put("STOP_ORDERNUM", 1024.0d);
		map.put("TRANSID", 889.0d);
		map.put("STATUS", "FILLED");
		map.put("SECCODE", "SBER");
		map.put("CLASSCODE", "EQBR");
		map.put("ACCOUNT", "LX01");
		map.put("CLIENTCODE", "88s2");
		map.put("BUYSELL", "B");
		map.put("QTY", 15.0d);
		map.put("PRICE", 151000.0d);
		map.put("STOP_ORDERKIND", "Тэйк-профит и стоп-лимит");
		map.put("LINKED_ORDER", 617.0d);
		map.put("CONDITION_PRICE", 160000.0d); // тэйк-профит цена
		map.put("CONDITION_PRICE2", 151500.0d);
		map.put("OFFSET", 2.0d);
		map.put("OFFSET_UNITS", "Д");
		map.put("SPREAD", 5.0d);
		map.put("SPREAD_UNITS", "%");
		map.put("STOP_ORDERDATE", "2011-01-01");
		map.put("STOP_ORDERTIME", "12:10:00");
		Row row = new SimpleRow(map);
		StopOrderCache expected = new StopOrderCache(1024L, 889L,
				OrderStatus.FILLED, "SBER", "EQBR", "LX01", "88s2",
				OrderDirection.BUY, 15L, 151000.0d, 151500.0d, 160000.0d,
				new Price(PriceUnit.MONEY, 2.0d),
				new Price(PriceUnit.PERCENT, 5.0d),
				617L,
				timeFormat.parse("2011-01-01 12:10:00"),
				null,
				OrderType.TPSL);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testGetKeyValue() throws Exception {
		map.put("STOP_ORDERNUM", 210.0d);
		Row row = new SimpleRow(map);
		
		assertEquals(new Long(210L), gateway.getKeyValue(row));
	}
	
	@Test
	public void testFireCacheUpdate() throws Exception {
		cache.fireUpdateCache();
		control.replay();
		
		gateway.fireUpdateCache();
		
		control.verify();
	}
	
	@Test
	public void testClearCache() throws Exception {
		cache.clear();
		control.replay();
		
		gateway.clearCache();
		
		control.verify();
	}
	
	@Test
	public void testShouldCache() throws Exception {
		Row row = new SimpleRow(map);
		assertTrue(gateway.shouldCache(row));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(gateway.equals(gateway));
		assertFalse(gateway.equals(null));
		assertFalse(gateway.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<StopOrdersCache> vCache = new Variant<StopOrdersCache>()
			.add(cache)
			.add(control.createMock(StopOrdersCache.class));
		Variant<RowDataConverter> vConv = new Variant<RowDataConverter>(vCache)
			.add(converter)
			.add(control.createMock(RowDataConverter.class));
		Variant<?> iterator = vConv;
		int foundCnt = 0;
		StopOrdersGateway x = null, found = null;
		do {
			x = new StopOrdersGateway(vCache.get(), vConv.get());
			if ( gateway.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(cache, found.getStopOrdersCache());
		assertSame(converter, found.getRowDataConverter());
	}

}
