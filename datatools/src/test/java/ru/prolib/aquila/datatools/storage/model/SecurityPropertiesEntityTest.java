package ru.prolib.aquila.datatools.storage.model;

import static org.junit.Assert.*;

import java.util.Currency;

import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class SecurityPropertiesEntityTest {
	private SecurityDescriptor descr;
	private SymbolEntity symbol;
	private SecurityPropertiesEntity entity;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("RTS", "SPB", "USD", SecurityType.FUT);
		symbol = new SymbolEntity();
		symbol.setDescriptor(descr);
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
		DateTime starting = new DateTime(2010, 1, 1, 0, 0, 0, 0);
		DateTime expiration = new DateTime(2015, 4, 26, 14, 30, 20, 0);
		Currency currency = Currency.getInstance("EUR");
		entity.setId(215L);
		entity.setSymbol(symbol);
		entity.setDisplayName("foobar");
		entity.setStartingTime(starting);
		entity.setExpirationTime(expiration);
		entity.setCurrencyOfCost(currency);
		
		assertEquals(new Long(215L), entity.getId());
		assertSame(symbol, entity.getSymbol());
		assertEquals("foobar", entity.getDisplayName());
		assertEquals(starting, entity.getStartingTime());
		assertEquals(expiration, entity.getExpirationTime());
		assertEquals(currency, entity.getCurrencyOfCost());
	}
	
	@Test
	public void testGetSecurityDescriptor() throws Exception {
		entity.setSymbol(symbol);
		assertSame(descr, entity.getSecurityDescriptor());
	}

}
