package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.SimpleRow;
import ru.prolib.aquila.dde.DDEException;

public class PortfoliosFGatewayTest {
	private IMocksControl control;
	private PortfoliosFCache cache;
	private RowDataConverter converter;
	private PortfoliosFGateway gateway;
	private Map<String, Object> map;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(PortfoliosFCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new PortfoliosFGateway(cache, converter);
		map = new HashMap<String, Object>();
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
			"TRDACCID",
			"FIRMID",
			"CBPLPLANNED",
			"CBPLIMIT",
			"VARMARGIN",
			"LIMIT_TYPE",
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
		Row row = new SimpleRow(map);
		
		assertEquals("SPBFUT#eqe01", gateway.getKeyValue(row));
	}
	
	@Test
	public void testShouldCache_NoIfLimitTypeMismatch() throws Exception {
		map.put("LIMIT_TYPE", "some limit type");
		Row row = new SimpleRow(map);
		assertFalse(gateway.shouldCache(row));
	}
	
	@Test
	public void testShouldCache_Yes() throws Exception {
		map.put("LIMIT_TYPE", "Ден.средства");
		Row row = new SimpleRow(map);
		assertTrue(gateway.shouldCache(row));
	}

	@Test
	public void testToCache() throws Exception {
		map.put("TRDACCID", "eqe02");
		map.put("FIRMID", "BCS01");
		map.put("CBPLIMIT", 28150.18d);
		map.put("CBPLPLANNED", 24900.02d);
		map.put("VARMARGIN", -1230.0d);
		Row row = new SimpleRow(map);
		PortfolioFCache expected = new PortfolioFCache("eqe02", "BCS01",
				28150.18d, 24900.02d, -1230.0d);
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}

}
