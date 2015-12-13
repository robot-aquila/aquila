package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.Currency;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class SymbolTest {
	private Symbol symbol;

	@Before
	public void setUp() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "USD", SymbolType.STOCK);
	}
	
	@Test
	public void testConstruct4_WithCurrencyCode() throws Exception {
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getExchangeID());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STOCK, symbol.getType());
	}
	
	@Test
	public void testConstruct4_WithCurrency() throws Exception {
		symbol = new Symbol("ZZZ-12.13", "XYZ",
				ISO4217.EUR, SymbolType.FUTURE);
		assertEquals("ZZZ-12.13", symbol.getCode());
		assertEquals("XYZ", symbol.getExchangeID());
		assertEquals(ISO4217.EUR, symbol.getCurrency());
		assertEquals("EUR", symbol.getCurrencyCode());
		assertEquals(SymbolType.FUTURE, symbol.getType());		
	}
	
	@Test
	public void testConstruct3_WithCurrencyCode() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "USD");
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getExchangeID());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STOCK, symbol.getType());
	}
	
	@Test
	public void testConstruct3_WithCurrency() throws Exception {
		symbol = new Symbol("SBER", "EQBR", ISO4217.USD);
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getExchangeID());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STOCK, symbol.getType());
	}
	
	@Test
	public void testConstruct4_FourStrings() throws Exception {
		symbol = new Symbol("AAPL", "NASDAQ", "USD", "S");
		assertEquals("AAPL", symbol.getCode());
		assertEquals("NASDAQ", symbol.getExchangeID());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals("S", symbol.getTypeCode());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		symbol = new Symbol("F:Si-12.15@SPBFUT:RUR");
		assertEquals("Si-12.15", symbol.getCode());
		assertEquals(SymbolType.FUTURE, symbol.getType());
		assertEquals("SPBFUT", symbol.getExchangeID());
		assertEquals(ISO4217.RUR, symbol.getCurrency());
	}
	
	@Test
	public void testConstruct1_WithEmptyParts() throws Exception {
		symbol = new Symbol(":Si-12.15@:");
		assertEquals("Si-12.15", symbol.getCode());
		assertNull(symbol.getTypeCode());
		assertNull(symbol.getExchangeID());
		assertNull(symbol.getCurrencyCode());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder()
			.append("SBER")
			.append("EQBR")
			.append("USD")
			.append("S")
			.hashCode(), symbol.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(symbol.equals(null));
		assertTrue(symbol.equals(symbol));
		assertFalse(symbol.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vCode = new Variant<String>().add("SBER").add("GAZP");
		Variant<String> vClass = new Variant<String>(vCode).add("EQBR").add("SMART");
		Variant<String> vCurr = new Variant<String>(vClass).add("RUB").add("USD");
		Variant<String> vType = new Variant<String>(vCurr).add("U").add("S");
		Variant<?> iterator = vType;
		int foundCnt = 0;
		Symbol x = null, found = null;
		do {
			x = new Symbol(vCode.get(), vClass.get(), vCurr.get(), vType.get());
			if ( symbol.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("SBER", found.getCode());
		assertEquals("EQBR", found.getExchangeID());
		assertEquals("USD", found.getCurrencyCode());
		assertEquals("S", found.getTypeCode());
	}
	
	@Test
	public void testIsValid() throws Exception {
		assertTrue(new Symbol("AAPL").isValid());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("S:SBER@EQBR:USD", symbol.toString());
		symbol.setTypeCode(null);
		assertEquals("SBER@EQBR:USD", symbol.toString());
		symbol.setCurrencyCode(null);
		assertEquals("SBER@EQBR", symbol.toString());
		symbol.setExchangeID(null);
		assertEquals("SBER", symbol.toString());
	}
	
	@Test (expected=NullPointerException.class)
	public void testSetCode_ThrowsIfNullCode() throws Exception {
		symbol.setCode(null);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSetCode_ThrowsIfEmptyCode() throws Exception {
		symbol.setCode("");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetExchangeID_ThrowsIfEmptyCode() throws Exception {
		symbol.setExchangeID("");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetTypeCode_ThrowsIfEmptyCode() throws Exception {
		symbol.setTypeCode("");
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetCurrencyCode_ThrowsIfEmptyCode() throws Exception {
		symbol.setCurrencyCode("");
	}
	
}
