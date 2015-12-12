package ru.prolib.aquila.datatools.storage.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class SymbolEntityTest {
	private Symbol symbol;
	private SymbolEntity symbolEntity;

	@Before
	public void setUp() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "RUB", SymbolType.STK);
		symbolEntity = new SymbolEntity();
	}

	@Test
	public void testCtor_Defaults() {
		assertNull(symbolEntity.getId());
		assertNull(symbolEntity.getSymbol());
	}
	
	@Test
	public void testSettersAndGetters() throws Exception {
		symbolEntity.setId(815L);
		symbolEntity.setSymbol(symbol);
		
		assertEquals(new Long(815), symbolEntity.getId());
		assertSame(symbol, symbolEntity.getSymbol());
	}

}
