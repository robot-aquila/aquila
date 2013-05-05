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
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.DDEException;

public class OrdersGatewayTest {
	private static SimpleDateFormat format;
	private IMocksControl control;
	private OrdersCache cache;
	private RowDataConverter converter;
	private OrdersGateway gateway;
	private Map<String, Object> map;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(OrdersCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new OrdersGateway(cache, converter);
		map = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
				"ORDERNUM",
				"TRANSID",
				"STATUS",
				"SECCODE",
				"CLASSCODE",
				"ACCOUNT",
				"CLIENTCODE",
				"BUYSELL",
				"QTY",
				"BALANCE",
				"PRICE",
				"ORDERDATE",
				"ORDERTIME",
				"WITHDRAW_DATE",
				"WITHDRAW_TIME",
				"MODE",
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testFireUpdateCache() throws Exception {
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
	public void testToCache1() throws Exception {
		// лимитная, частично исполненная, на покупку, без номера транзы
		map.put("ORDERNUM", 1001.0d);
		map.put("TRANSID", 0.0d);
		map.put("STATUS", "KILLED");
		map.put("SECCODE", "SBER");
		map.put("CLASSCODE", "EQBR");
		map.put("ACCOUNT", "LX001");
		map.put("CLIENTCODE", "8690");
		map.put("BUYSELL", "B");
		map.put("QTY", 10.0d);
		map.put("BALANCE", 5.0d);
		map.put("PRICE", 124.98d);
		map.put("ORDERDATE", "2013-04-23");
		map.put("ORDERTIME", "18:19:34");
		map.put("WITHDRAW_DATE", "2013-04-25");
		map.put("WITHDRAW_TIME", "11:34:19");
		map.put("MODE", "LRO");
		Row row = new SimpleRow(map);
		OrderCache expected = new OrderCache(1001L, null, OrderStatus.CANCELLED,
				"SBER", "EQBR", "LX001", "8690", OrderDirection.BUY,
				10L, 5L, 124.98d,
				format.parse("2013-04-23 18:19:34"),
				format.parse("2013-04-25 11:34:19"),
				OrderType.LIMIT);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testToCache2() throws Exception {
		// рыночная, активна, на продажу, с номером транзы
		map.put("ORDERNUM", 1002.0d);
		map.put("TRANSID", 508.0d);
		map.put("STATUS", "ACTIVE");
		map.put("SECCODE", "RIM3");
		map.put("CLASSCODE", "SPBFUT");
		map.put("ACCOUNT", "eqe1000");
		map.put("CLIENTCODE", "2418");
		map.put("BUYSELL", "S");
		map.put("QTY", 20.0d);
		map.put("BALANCE", 20.0d);
		map.put("PRICE", 0.0d);
		map.put("ORDERDATE", "2013-01-12");
		map.put("ORDERTIME", "12:15:40");
		map.put("WITHDRAW_DATE", "");
		map.put("WITHDRAW_TIME", "");
		map.put("MODE", "MRO");
		Row row = new SimpleRow(map);
		OrderCache expected = new OrderCache(1002L, 508L, OrderStatus.ACTIVE,
				"RIM3", "SPBFUT", "eqe1000", "2418", OrderDirection.SELL,
				20L, 20L, null,
				format.parse("2013-01-12 12:15:40"),
				null,
				OrderType.MARKET);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testToCache3() throws Exception {
		// иной тип, исполнена, на покупку, без номера транзы
		map.put("ORDERNUM", 1008.0d);
		map.put("TRANSID", 0.0);
		map.put("STATUS", "FILLED");
		map.put("SECCODE", "GAZP");
		map.put("CLASSCODE", "EQNE");
		map.put("ACCOUNT", "LX0002");
		map.put("CLIENTCODE", "1534");
		map.put("BUYSELL", "B");
		map.put("QTY", 120.0d);
		map.put("BALANCE", 0.0d);
		map.put("PRICE", 248.0d);
		map.put("ORDERDATE", "2012-12-28");
		map.put("ORDERTIME", "08:24:19");
		map.put("WITHDRAW_DATE", "");
		map.put("WITHDRAW_TIME", "");
		map.put("MODE", "IXX");
		Row row = new SimpleRow(map);
		OrderCache expected = new OrderCache(1008L, null, OrderStatus.FILLED,
				"GAZP", "EQNE", "LX0002", "1534", OrderDirection.BUY,
				120L, 0L, 248.0d,
				format.parse("2012-12-28 08:24:19"),
				null,
				OrderType.OTHER);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testGetKeyValue() throws Exception {
		map.put("ORDERNUM", 1008.0d);
		Row row = new SimpleRow(map);
		
		assertEquals(new Long(1008), gateway.getKeyValue(row));
	}
	
	@Test (expected=DDEException.class)
	public void testGetKeyValue_ThrowsIfNullKey() throws Exception {
		Row row = new SimpleRow(map);
		gateway.getKeyValue(row);
	}
	
	@Test
	public void testShouldCache() throws Exception {
		Row row = new SimpleRow(map);
		assertTrue(gateway.shouldCache(row));
	}

}
