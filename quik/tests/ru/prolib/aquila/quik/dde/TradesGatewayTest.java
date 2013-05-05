package ru.prolib.aquila.quik.dde;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.SimpleRow;
import ru.prolib.aquila.dde.DDEException;

public class TradesGatewayTest {
	private static SimpleDateFormat format;
	private IMocksControl control;
	private TradesCache cache;
	private RowDataConverter converter;
	private TradesGateway gateway;
	private Map<String, Object> map;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(TradesCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new TradesGateway(cache, converter);
		map = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
				"TRADENUM",
				"SESSION_DATE",
				"TRADETIME",
				"ORDERNUM",
				"PRICE",
				"QTY",
				"VALUE",
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
		map.put("TRADENUM", 812.0d);
		map.put("SESSION_DATE", "2013-04-23");
		map.put("TRADETIME", "18:19:34");
		map.put("ORDERNUM", 215.0d);
		map.put("PRICE", 12.34d);
		map.put("QTY", 100.0d);
		map.put("VALUE", 83419.48d);

		Row row = new SimpleRow(map);
		TradeCache expected = new TradeCache(812L,
				format.parse("2013-04-23 18:19:34"),
				215L, 12.34d, 100L, 83419.48d);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testGetKeyValue() throws Exception {
		map.put("TRADENUM", 762.0d);
		Row row = new SimpleRow(map);
		
		assertEquals(new Long(762), gateway.getKeyValue(row));
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
