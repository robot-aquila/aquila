package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolAnnualTest {
	private static final Symbol
		symbol1 = new Symbol("foo"),
		symbol2 = new Symbol("bar"),
		symbol3 = new Symbol("xev");
	private static final YearPoint
		point1 = new YearPoint(2010),
		point2 = new YearPoint(2015);
	private SymbolAnnual token;

	@Before
	public void setUp() throws Exception {
		token = new SymbolAnnual(symbol1, point1);
	}
	
	@Test
	public void testCtor2_SP() {
		assertEquals(symbol1, token.getSymbol());
		assertEquals(point1, token.getPoint());
	}
	
	@Test
	public void testCtor2_SI() {
		token = new SymbolAnnual(symbol2, 2015);
		assertEquals(symbol2, token.getSymbol());
		assertEquals(point2, token.getPoint());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(token.equals(token));
		assertFalse(token.equals(null));
		assertFalse(token.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Symbol> vSym = new Variant<>(symbol1, symbol2);
		Variant<YearPoint> vPoint = new Variant<>(vSym, point1, point2);
		Variant<?> iterator = vPoint;
		int foundCnt = 0;
		SymbolAnnual x, found = null;
		do {
			x = new SymbolAnnual(vSym.get(), vPoint.get());
			if ( token.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(symbol1, found.getSymbol());
		assertEquals(point1, found.getPoint());
	}

	@Test
	public void testToString() {
		assertEquals("foo[2010]", token.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(19009, 532671)
				.append(symbol1)
				.append(point1)
				.toHashCode(), token.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(token.compareTo(new SymbolAnnual(symbol1, 2010)) == 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol1, 2011)) < 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol1, 2009)) > 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol2, 2010)) > 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol2, 2000)) > 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol3, 2010)) < 0);
		assertTrue(token.compareTo(new SymbolAnnual(symbol3, 2022)) < 0);
	}

}
