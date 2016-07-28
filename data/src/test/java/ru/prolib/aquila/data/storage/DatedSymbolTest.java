package ru.prolib.aquila.data.storage;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DatedSymbolTest {
	private static Symbol symbol1 = new Symbol("SBER"), symbol2 = new Symbol("MSFT");
	private static LocalDate date1 = LocalDate.of(1997, 3, 15), date2 = LocalDate.of(2006, 1, 20);
	private DatedSymbol descr;

	@Before
	public void setUp() throws Exception {
		descr = new DatedSymbol(symbol1, date1);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(symbol1, descr.getSymbol());
		assertEquals(date1, descr.getDate());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(descr.equals(descr));
		assertFalse(descr.equals(this));
		assertFalse(descr.equals(null));
	}
	
	@Test
	public void testEquals() {
		DatedSymbol descr1 = new DatedSymbol(symbol1, date1),
				descr2 = new DatedSymbol(symbol1, date2),
				descr3 = new DatedSymbol(symbol2, date1),
				descr4 = new DatedSymbol(symbol2, date2);
		
		assertTrue(descr.equals(descr1));
		assertFalse(descr.equals(descr2));
		assertFalse(descr.equals(descr3));
		assertFalse(descr.equals(descr4));
	}
	
	@Test
	public void testToString() {
		assertEquals("DatedSymbol[SBER at 1997-03-15]", descr.toString());
	}

}
