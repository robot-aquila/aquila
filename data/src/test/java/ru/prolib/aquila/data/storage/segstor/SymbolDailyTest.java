package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolDailyTest {
	private static final Symbol
		symbol1 = new Symbol("foo"),
		symbol2 = new Symbol("bar"),
		symbol3 = new Symbol("xev");
	private static final DatePoint
		point1 = new DatePoint(2017, 9, 11),
		point2 = new DatePoint(1998, 6, 15);
	private SymbolDaily token;

	@Before
	public void setUp() throws Exception {
		token = new SymbolDaily(symbol1, point1);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(symbol1, token.getSymbol());
		assertEquals(point1, token.getPoint());
	}
	
	@Test
	public void testCtor4() {
		token = new SymbolDaily(symbol2, 1998, 6, 15);
		assertEquals(symbol2, token.getSymbol());
		assertEquals(point2, token.getPoint());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(token.equals(token));
		assertFalse(token.equals(this));
		assertFalse(token.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<Symbol> vSym = new Variant<>(symbol1, symbol2);
		Variant<DatePoint> vPoint = new Variant<>(vSym, point1, point2);
		Variant<?> iterator = vPoint;
		int foundCnt = 0;
		SymbolDaily x, found = null;
		do {
			x = new SymbolDaily(vSym.get(), vPoint.get());
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
		assertEquals("foo[2017-09-11]", token.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(1824927, 144801)
				.append(symbol1)
				.append(point1)
				.toHashCode(), token.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(token.compareTo(new SymbolDaily(symbol1, 2017, 9, 11)) == 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol1, 2017, 9, 12)) < 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol1, 2017, 9, 10)) > 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol2, 2017, 9, 11)) > 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol2, 2017, 9, 10)) > 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol3, 2017, 9, 11)) < 0);
		assertTrue(token.compareTo(new SymbolDaily(symbol3, 2022, 1, 10)) < 0);
	}
	
	@Test
	public void testToMonthly() {
		assertEquals(new SymbolMonthly(symbol1, 2017, 9), token.toMonthly());
	}
	
	@Test
	public void testToAnnual() {
		assertEquals(new SymbolAnnual(symbol1, 2017), token.toAnnual());
	}

}
