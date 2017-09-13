package ru.prolib.aquila.data.storage.segstor;

import static org.junit.Assert.*;

import java.time.Month;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolMonthlyTest {
	private static final Symbol
		symbol1 = new Symbol("foo"),
		symbol2 = new Symbol("bar"),
		symbol3 = new Symbol("xev");
	private static final MonthPoint
		point1 = new MonthPoint(2017, Month.JANUARY),
		point2 = new MonthPoint(2010, Month.JULY);
	private SymbolMonthly token;

	@Before
	public void setUp() throws Exception {
		token = new SymbolMonthly(symbol1, point1);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(symbol1, token.getSymbol());
		assertEquals(point1, token.getPoint());
	}
	
	@Test
	public void testCtor3_SIM() {
		token = new SymbolMonthly(symbol2, 2010, Month.JULY);
		assertEquals(symbol2, token.getSymbol());
		assertEquals(point2, token.getPoint());
	}
	
	@Test
	public void testCtor3_SII() {
		token = new SymbolMonthly(symbol3, 2001, 12);
		assertEquals(symbol3, token.getSymbol());
		assertEquals(new MonthPoint(2001, Month.DECEMBER), token.getPoint());
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
		Variant<MonthPoint> vPoint = new Variant<>(vSym, point1, point2);
		Variant<?> iterator = vPoint;
		int foundCnt = 0;
		SymbolMonthly x, found = null;
		do {
			x = new SymbolMonthly(vSym.get(), vPoint.get());
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
		assertEquals("foo[2017, JANUARY]", token.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(6517, 91192231)
				.append(symbol1)
				.append(point1)
				.toHashCode(), token.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(token.compareTo(new SymbolMonthly(symbol1, 2017, Month.JANUARY)) == 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol1, 2017, Month.FEBRUARY)) < 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol1, 2016, Month.DECEMBER)) > 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol2, 2017, Month.JANUARY)) > 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol2, 2016, Month.DECEMBER)) > 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol3, 2017, Month.JANUARY)) < 0);
		assertTrue(token.compareTo(new SymbolMonthly(symbol3, 2022, Month.JUNE)) < 0);
	}
	
	@Test
	public void testToAnnual() {
		assertEquals(new SymbolAnnual(symbol1, 2017), token.toAnnual());
	}

}
