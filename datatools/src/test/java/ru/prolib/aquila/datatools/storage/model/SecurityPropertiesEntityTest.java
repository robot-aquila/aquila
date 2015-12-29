package ru.prolib.aquila.datatools.storage.model;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Currency;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class SecurityPropertiesEntityTest {
	private Symbol symbol;
	private SymbolEntity symbolEntity;
	private SecurityPropertiesEntity entity;

	@Before
	public void setUp() throws Exception {
		symbol = new Symbol("RTS", "SPB", "USD", SymbolType.FUTURE);
		symbolEntity = new SymbolEntity();
		symbolEntity.setSymbol(symbol);
		entity = new SecurityPropertiesEntity();
	}
	
	@Test
	public void testCtor_Defaults() throws Exception {
		assertNull(entity.getId());
		assertNull(entity.getSymbol());
		assertNull(entity.getDisplayName());
		assertNull(entity.getExpirationTime());
		assertNull(entity.getStartingTime());
		assertNull(entity.getCurrencyOfCost());
	}

	@Test
	public void testSettersAndGetters() throws Exception {
		LocalDateTime starting = LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0);
		LocalDateTime expiration = LocalDateTime.of(2015, 4, 26, 14, 30, 20, 0);
		Currency currency = Currency.getInstance("EUR");
		entity.setId(215L);
		entity.setSymbol(symbolEntity);
		entity.setDisplayName("foobar");
		entity.setStartingTime(starting);
		entity.setExpirationTime(expiration);
		entity.setCurrencyOfCost(currency);
		
		assertEquals(new Long(215L), entity.getId());
		assertSame(symbolEntity, entity.getSymbol());
		assertEquals("foobar", entity.getDisplayName());
		assertEquals(starting, entity.getStartingTime());
		assertEquals(expiration, entity.getExpirationTime());
		assertEquals(currency, entity.getCurrencyOfCost());
	}
	
	@Test
	public void testGetSymbol() throws Exception {
		entity.setSymbol(symbolEntity);
		assertSame(symbol, entity.getSymbolInfo());
	}

}
