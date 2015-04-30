package ru.prolib.aquila.probe.storage.model;

import static org.junit.Assert.*;

import java.util.Currency;

import org.joda.time.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

public class ConstantSecurityPropertiesEntityTest {
	private SecurityDescriptor descr;
	private SymbolEntity symbol;
	private ConstantSecurityPropertiesEntity entity;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("RTS", "SPB", "USD", SecurityType.FUT);
		symbol = new SymbolEntity();
		symbol.setDescriptor(descr);
		entity = new ConstantSecurityPropertiesEntity();
	}
	
	@Test
	public void testCtor_Defaults() throws Exception {
		assertNull(entity.getId());
		assertNull(entity.getSymbol());
		assertNull(entity.getDisplayName());
		assertNull(entity.getExpirationTime());
		assertNull(entity.getCurrencyOfCost());
	}

	@Test
	public void testSettersAndGetters() throws Exception {
		DateTime expiration = new DateTime(2015, 4, 26, 14, 30, 20, 0);
		Currency currency = Currency.getInstance("EUR");
		entity.setId(215L);
		entity.setSymbol(symbol);
		entity.setDisplayName("foobar");
		entity.setExpirationTime(expiration);
		entity.setCurrencyOfCost(currency);
		
		assertEquals(new Long(215L), entity.getId());
		assertSame(symbol, entity.getSymbol());
		assertEquals("foobar", entity.getDisplayName());
		assertEquals(expiration, entity.getExpirationTime());
		assertEquals(currency, entity.getCurrencyOfCost());
	}
	
	@Test
	public void testGetSecurityDescriptor() throws Exception {
		entity.setSymbol(symbol);
		assertSame(descr, entity.getSecurityDescriptor());
	}

}
