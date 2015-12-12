package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.quik.assembler.*;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;
import ru.prolib.aquila.quik.assembler.cache.dde.RowDataConverter;
import ru.prolib.aquila.quik.assembler.cache.dde.SecuritiesGateway;

public class SecuritiesGatewayTest {
	private IMocksControl control;
	private Assembler asm;
	private RowDataConverter converter;
	private SecuritiesGateway gateway;
	private Map<String, Object> map;
	private Row row;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		asm = control.createMock(Assembler.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new SecuritiesGateway(converter, asm);
		map = new HashMap<String, Object>();
		row = new SimpleRow(map);
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
			//"open",
			//"prevlegalclosepr",
			"LONGNAME",
			"SHORTNAME",
			"offer",
			"bid",
			"high",
			"low",
			"curstepprice",
			"prevsettleprice", 
			"buydepo",		// ГО покупателя
			"selldepo",		// ГО продавца
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testProcess1() throws Exception {
		// фьючерс с указанной валютой
		// open, close, high, low, bid, ask - пустые строки (д.б. null)
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
		map.put("buydepo", 12350.0d);
		map.put("selldepo", 0.0d);
		map.put("prevsettleprice", 143800.0d);
		SecurityEntry expected = new SecurityEntry(1, 145600.0d, 135060.0d,
				6.20866d, 10.0, 0, 140720.0d, null, null, "RTS-3.13", "RIM3",
				null, null, null, null, "RIM3", "SPBFUT", ISO4217.USD,
				SymbolType.FUT,
				143800.0d, 12350.0d);
		asm.assemble(eq(expected));
		control.replay();
		
		gateway.process(row);
		
		control.verify();
	}
	
	@Test
	public void testProcess2() throws Exception {
		// акция (тип пустая строка), валюта шага пустая строка
		// макс/мин цена - пустая строка (д.б. null)
		// bid/ask/last - нулевые значения (д.б. null)
		// ГО, расч. цена - нулевые значения (д.б. null)
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
		map.put("curstepprice", "USD");
		map.put("buydepo", "");
		map.put("selldepo", 0.0d);
		//map.put("settleprice", 0.0d);
		SecurityEntry expected = new SecurityEntry(10, null, null,
				null, 0.01d, 2, null, 227.39d, 227.76d, "Уралкалий (ОАО) ао",
				"Уралкалий-ао", null, null, 230.0d, 227.39d,
				"URKA", "EQBR", ISO4217.USD, SymbolType.STK,
				null, null);
		asm.assemble(eq(expected));
		control.replay();
		
		gateway.process(row);
		
		control.verify();
	}
	
	@Test (expected=DDEException.class)
	public void testProcess_ThrowsIfCurrencyCodeZero() throws Exception {
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
		map.put("curstepprice", ""); // <-- !!!
		map.put("buydepo", 0.0d);
		map.put("selldepo", 0.0d);
		map.put("prevsettleprice", 0.0d);
		control.replay();
		
		gateway.process(row);
	}
	
	@Test
	public void testProcess_CurrencyCodeFix() throws Exception {
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
		map.put("curstepprice", "SUR"); // <-- !!!
		map.put("buydepo", 12350.0d);
		map.put("selldepo", 0.0d);
		map.put("prevsettleprice", "zuzumber");
		SecurityEntry expected = new SecurityEntry(10, null, null,
				null, 0.01d, 2, null, 227.39d, 227.76d, "Уралкалий (ОАО) ао",
				"Уралкалий-ао", null, null, 230.0d, 227.39d,
				"URKA", "EQBR", ISO4217.RUB, SymbolType.STK, null, 12350.0d);
		asm.assemble(eq(expected));
		control.replay();
		
		gateway.process(row);
		
		control.verify();
	}
	
	@Test (expected=DDEException.class)
	public void testProcess_ThrowsIfCurrencyNotExists() throws Exception {
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
		map.put("curstepprice", "ZZZ"); // <-- !!!
		map.put("buydepo", 12350.0d);
		map.put("selldepo", 0.0d);
		map.put("prevsettleprice", 143800.0d);
		control.replay();
		
		gateway.process(row);
	}
	
	@Test
	public void testShouldProcess() throws Exception {
		assertTrue(gateway.shouldProcess(row));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(gateway.equals(gateway));
		assertFalse(gateway.equals(null));
		assertFalse(gateway.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<RowDataConverter> vConv = new Variant<RowDataConverter>()
			.add(converter)
			.add(control.createMock(RowDataConverter.class));
		Variant<Assembler> vAsm = new Variant<Assembler>(vConv)
			.add(asm)
			.add(control.createMock(Assembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		SecuritiesGateway x, found = null;
		do {
			x = new SecuritiesGateway(vConv.get(), vAsm.get());
			if ( gateway.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(converter, found.getRowDataConverter());
		assertSame(asm, found.getAssembler());
	}
	
	@Test
	public void testShouldProcessRowByRow() throws Exception {
		assertTrue(gateway.shouldProcessRowByRow(null, null));
	}

}
