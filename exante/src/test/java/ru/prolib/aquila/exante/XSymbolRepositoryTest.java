package ru.prolib.aquila.exante;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;

public class XSymbolRepositoryTest {

	@Rule
	public ExpectedException eex = ExpectedException.none();

	private Map<String, XSymbol> sec_id_to_desc;
	private Map<String, Symbol> sec_id_to_sym;
	private Map<Symbol, String> sym_to_sec_id;
	private XSymbol desc1, desc2, desc3;
	private Symbol symbol1, symbol2, symbol3;
	private XSymbolRepository service;
	
	@Before
	public void setUp() throws Exception {
		sec_id_to_desc = new HashMap<>();
		sec_id_to_sym = new HashMap<>();
		sym_to_sec_id = new HashMap<>();
		service = new XSymbolRepository(sec_id_to_desc, sym_to_sec_id, sec_id_to_sym);
		desc1 = new XSymbol("AAPL", "AAPL.NYSE", "NYSE", "EXXXXX", "USD");
		desc2 = new XSymbol("EUR", "EUR", null, "MRCXXX", "EUR");
		desc3 = new XSymbol("RTS-9.19", "RIU9.FORTS", "FORTS", "FXXXXX", "RUR");
		symbol1 = new Symbol("AAPL", "NYSE", "USD", SymbolType.STOCK);
		symbol2 = new Symbol("EUR", null, "EUR", SymbolType.CURRENCY);
		symbol3 = new Symbol("RTS-9.19", "FORTS", "RUR", SymbolType.FUTURES);
	}

	@Test
	public void testRegister() {
		service.register(symbol1, desc1);
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);
		
		Map<String, XSymbol> expected_sec_id_to_desc = new HashMap<>();
		expected_sec_id_to_desc.put("AAPL.NYSE", desc1);
		expected_sec_id_to_desc.put("EUR", desc2);
		expected_sec_id_to_desc.put("RIU9.FORTS", desc3);
		assertEquals(expected_sec_id_to_desc, sec_id_to_desc);
		
		Map<String, Symbol> expected_sec_id_to_sym = new HashMap<>();
		expected_sec_id_to_sym.put("AAPL.NYSE", symbol1);
		expected_sec_id_to_sym.put("EUR", symbol2);
		expected_sec_id_to_sym.put("RIU9.FORTS", symbol3);
		assertEquals(expected_sec_id_to_sym, sec_id_to_sym);
		
		Map<Symbol, String> expected_sym_to_sec_id = new HashMap<>();
		expected_sym_to_sec_id.put(symbol1, "AAPL.NYSE");
		expected_sym_to_sec_id.put(symbol2, "EUR");
		expected_sym_to_sec_id.put(symbol3, "RIU9.FORTS");
		assertEquals(expected_sym_to_sec_id, sym_to_sec_id);
	}
	
	@Test
	public void testGetSymbol() {
		service.register(symbol1, desc1);
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);

		assertEquals(symbol1, service.getSymbol("AAPL.NYSE"));
		assertEquals(symbol2, service.getSymbol("EUR"));
		assertEquals(symbol3, service.getSymbol("RIU9.FORTS"));
	}
	
	@Test
	public void testGetSymbol_ThrowsIfNotExists() {
		service.register(symbol1, desc1);
		service.register(symbol3, desc3);
		
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Security ID not found: EUR");
		
		service.getSymbol("EUR");
	}
	
	@Test
	public void testGetSecurityID() {
		service.register(symbol1, desc1);
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);

		assertEquals("AAPL.NYSE", service.getSecurityID(symbol1));
		assertEquals("EUR", service.getSecurityID(symbol2));
		assertEquals("RIU9.FORTS", service.getSecurityID(symbol3));
	}
	
	@Test
	public void testGetSecurityIDThrowsIfNotExists() {
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol not found: S:AAPL@NYSE:USD");
		
		service.getSecurityID(symbol1);
	}
	
	@Test
	public void testGetBySecurityID() {
		service.register(symbol1, desc1);
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);

		assertSame(desc1, service.getBySecurityID("AAPL.NYSE"));
		assertSame(desc2, service.getBySecurityID("EUR"));
		assertSame(desc3, service.getBySecurityID("RIU9.FORTS"));
	}
	
	@Test
	public void testGetBySecurityID_ThrowsIfNotExists() {
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Security ID not found: AAPL.NYSE");
		
		service.getBySecurityID("AAPL.NYSE");
	}
	
	@Test
	public void testGetBySymbol() {
		service.register(symbol1, desc1);
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);

		assertSame(desc1, service.getBySymbol(symbol1));
		assertSame(desc2, service.getBySymbol(symbol2));
		assertSame(desc3, service.getBySymbol(symbol3));
	}
	
	@Test
	public void testGetBySymbol_ThrowsIfNotExists() {
		service.register(symbol2, desc2);
		service.register(symbol3, desc3);
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Symbol not found: S:AAPL@NYSE:USD");
		
		service.getBySymbol(symbol1);
	}

}
