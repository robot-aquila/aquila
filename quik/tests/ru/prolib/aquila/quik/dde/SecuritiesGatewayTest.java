package ru.prolib.aquila.quik.dde;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.row.*;

public class SecuritiesGatewayTest {
	private IMocksControl control;
	private SecuritiesCache cache;
	private RowDataConverter converter;
	private SecuritiesGateway gateway;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cache = control.createMock(SecuritiesCache.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new SecuritiesGateway(cache, converter);
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
			"lotsize",
			"pricemax",
			"pricemin",
			"steppricet",
			"SEC_PRICE_STEP",
			"SEC_SCALE",
			"CODE",
			"CLASS_CODE",
			"last",
			"open",
			"prevlegalclosepr",
			"LONGNAME",
			"SHORTNAME",
			"offer",
			"bid",
			"high",
			"low",
			"curstepprice",
			"CLASSNAME",
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
		// фьючерс с указанной валютой
		// open, close, high, low, bid, ask - пустые строки (д.б. null)
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lotsize", 1.0d);
		map.put("pricemax", 145600.0d);
		map.put("pricemin", 135060.0d);
		map.put("steppricet", 6.20866d);
		map.put("SEC_PRICE_STEP", 10.0d);
		map.put("SEC_SCALE", 0d);
		map.put("CODE", "RIM3");
		map.put("CLASS_CODE", "SPBFUT");
		map.put("last", 140720.0d);
		map.put("open", "");
		map.put("prevlegalclosepr", "");
		map.put("LONGNAME", "RTS-3.13");
		map.put("SHORTNAME", "RIM3");
		map.put("offer", "");
		map.put("bid", "");
		map.put("high", "");
		map.put("low", "");
		map.put("curstepprice", "USD");
		map.put("CLASSNAME", "ФОРТС фьючерсы");
		
		Row row = new SimpleRow(map);
		SecurityCache expected = new SecurityCache(1, 145600.0d, 135060.0d,
				6.20866d, 10.0, 0, 140720.0d, null, null, "RTS-3.13", "RIM3",
				null, null, null, null, new SecurityDescriptor("RIM3", "SPBFUT",
						"USD", SecurityType.FUT));
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testToCache2() throws Exception {
		// акция (тип пустая строка), валюта шага пустая строка
		// макс/мин цена - пустая строка (д.б. null)
		// bid/ask/last - нулевые значения (д.б. null)
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lotsize", 10.0d);
		map.put("pricemax", "");
		map.put("pricemin", "");
		map.put("steppricet", "");
		map.put("SEC_PRICE_STEP", 0.01d);
		map.put("SEC_SCALE", 2d);
		map.put("CODE", "URKA");
		map.put("CLASS_CODE", "EQBR");
		map.put("last", 0.0d);
		map.put("open", 227.39d);
		map.put("prevlegalclosepr", 227.76d);
		map.put("LONGNAME", "Уралкалий (ОАО) ао");
		map.put("SHORTNAME", "Уралкалий-ао");
		map.put("offer", 0.0d);
		map.put("bid", 0.0d);
		map.put("high", 230.0d);
		map.put("low", 227.39d);
		map.put("curstepprice", "");
		map.put("CLASSNAME", "А1-Акции");
		
		Row row = new SimpleRow(map);
		SecurityCache expected = new SecurityCache(10, null, null,
				null, 0.01d, 2, null, 227.39d, 227.76d, "Уралкалий (ОАО) ао",
				"Уралкалий-ао", null, null, 230.0d, 227.39d,
				new SecurityDescriptor("URKA", "EQBR", "SUR",SecurityType.STK));
		cache.put(eq(expected));
		control.replay();
		
		gateway.toCache(row);
		
		control.verify();
	}
	
	@Test
	public void testGetKeyValue() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CODE", "GAZP");
		map.put("CLASS_CODE", "EQBR");
		map.put("curstepprice", "");
		map.put("CLASSNAME", "А1-Акции");
		Row row = new SimpleRow(map);
		
		assertEquals(
				new SecurityDescriptor("GAZP", "EQBR", "SUR", SecurityType.STK),
				gateway.getKeyValue(row));
	}

}
