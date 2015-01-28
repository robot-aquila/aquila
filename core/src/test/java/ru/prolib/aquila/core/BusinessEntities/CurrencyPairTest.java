package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CurrencyPairTest {
	private CurrencyPair pair1;

	@Before
	public void setUp() throws Exception {
		pair1 = new CurrencyPair(ISO4217.USD, ISO4217.RUR);
	}
	
	@Test
	public void testConstruct2_Cur() throws Exception {
		assertSame(ISO4217.USD, pair1.getBaseCurrency());
		assertSame(ISO4217.RUR, pair1.getCounterCurrency());
	}
	
	@Test
	public void testConstruct2_Str() throws Exception {
		pair1 = new CurrencyPair("EUR", "USD");
		assertSame(ISO4217.EUR, pair1.getBaseCurrency());
		assertSame(ISO4217.USD, pair1.getCounterCurrency());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(pair1.equals(pair1));
		assertTrue(pair1.equals(new CurrencyPair("USD", "RUR")));
		assertFalse(pair1.equals(this));
		assertFalse(pair1.equals(null));
		assertFalse(pair1.equals(new CurrencyPair("EUR", "RUR")));
		assertFalse(pair1.equals(new CurrencyPair("USD", "EUR")));
		assertFalse(pair1.equals(new CurrencyPair("RUR", "USD")));
	}

}
