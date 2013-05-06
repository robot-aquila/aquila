package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.SimpleRow;

public class PositionsFGatewayTest {
	private IMocksControl control;
	private PositionsFCache cache;
	private RowDataConverter converter;
	private PositionsFGateway gateway;
	private Map<String, Object> map;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(PositionsFCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new PositionsFGateway(cache, converter);
		map = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
			"TRDACCID",
			"FIRMID",
			"SEC_SHORT_NAME",
			"START_NET",
			"TOTAL_NET",
			"VARMARGIN",
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
	public void testGetKeyValue() throws Exception {
		map.put("FIRMID", "SPBFUT");
		map.put("TRDACCID", "eqe01");
		map.put("SEC_SHORT_NAME", "RIM3");
		Row row = new SimpleRow(map);
		
		assertEquals("SPBFUT#eqe01#RIM3", gateway.getKeyValue(row));
	}
	
	@Test
	public void testShouldCache() throws Exception {
		Row row = new SimpleRow(map);
		assertTrue(gateway.shouldCache(row));
	}
	
	@Test
	public void testToCache() throws Exception {
		map.put("TRDACCID", "eqe02");
		map.put("FIRMID", "BCS01");
		map.put("SEC_SHORT_NAME", "RIM3");
		map.put("START_NET", 12.0d);
		map.put("TOTAL_NET", 6.0d);
		map.put("VARMARGIN", 134.96d);
		Row row = new SimpleRow(map);
		PositionFCache expected = new PositionFCache("eqe02", "BCS01", "RIM3",
				12L, 6L, 134.96d);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}

}
