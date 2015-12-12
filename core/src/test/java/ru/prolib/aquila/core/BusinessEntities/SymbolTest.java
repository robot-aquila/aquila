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
		symbol = new Symbol("SBER", "EQBR", "USD", SymbolType.STK);
	}
	
	@Test
	public void testConstruct4_WithCurrencyCode() throws Exception {
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getClassCode());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STK, symbol.getType());
	}
	
	@Test
	public void testConstruct4_WithCurrency() throws Exception {
		symbol = new Symbol("ZZZ-12.13", "XYZ",
				ISO4217.EUR, SymbolType.FUT);
		assertEquals("ZZZ-12.13", symbol.getCode());
		assertEquals("XYZ", symbol.getClassCode());
		assertEquals(ISO4217.EUR, symbol.getCurrency());
		assertEquals("EUR", symbol.getCurrencyCode());
		assertEquals(SymbolType.FUT, symbol.getType());		
	}
	
	@Test
	public void testConstruct3_WithCurrencyCode() throws Exception {
		symbol = new Symbol("SBER", "EQBR", "USD");
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getClassCode());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STK, symbol.getType());
	}
	
	@Test
	public void testConstruct3_WithCurrency() throws Exception {
		symbol = new Symbol("SBER", "EQBR", ISO4217.USD);
		assertEquals("SBER", symbol.getCode());
		assertEquals("EQBR", symbol.getClassCode());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals("USD", symbol.getCurrencyCode());
		assertEquals(SymbolType.STK, symbol.getType());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder()
			.append("SBER")
			.append("EQBR")
			.append(ISO4217.USD)
			.append(SymbolType.STK)
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
		Variant<String> vCode = new Variant<String>()
			.add("SBER")
			.add("GAZP")
			.add(null);
		Variant<String> vClass = new Variant<String>(vCode)
			.add("EQBR")
			.add("SMART")
			.add(null);
		Variant<String> vCurr = new Variant<String>(vClass)
			.add("RUB")
			.add("USD");
		Variant<SymbolType> vType = new Variant<SymbolType>(vCurr)
			.add(SymbolType.UNK)
			.add(SymbolType.STK)
			.add(null);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		Symbol x = null, found = null;
		do {
			x = new Symbol(vCode.get(), vClass.get(), vCurr.get(),
					vType.get());
			if ( symbol.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("SBER", found.getCode());
		assertEquals("EQBR", found.getClassCode());
		assertEquals("USD", found.getCurrencyCode());
		assertEquals(SymbolType.STK, found.getType());
	}
	
	@Test
	public void testIsValid() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("")
			.add("AAPL")
			.add(null);
		Variant<String> vClass = new Variant<String>(vCode)
			.add("")
			.add("SMART")
			.add(null);
		Variant<Currency> vCurr = new Variant<Currency>(vClass)
			.add(ISO4217.USD)
			.add(null);
		Variant<SymbolType> vType = new Variant<SymbolType>(vCurr)
			.add(SymbolType.STK)
			.add(null);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		Symbol x = null, found = null;
		do {
			x = new Symbol(vCode.get(), vClass.get(), vCurr.get(),
					vType.get());
			if ( x.isValid() ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("AAPL", found.getCode());
		assertEquals("SMART", found.getClassCode());
		assertEquals(ISO4217.USD, found.getCurrency());
		assertEquals(SymbolType.STK, found.getType());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("SBER@EQBR(STK/USD)", symbol.toString());
	}


}
