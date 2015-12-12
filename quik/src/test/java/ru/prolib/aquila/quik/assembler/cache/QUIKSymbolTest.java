package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;
import java.util.Currency;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class QUIKSymbolTest {
	private QUIKSymbol symbol;

	@Before
	public void setUp() throws Exception {
		symbol = new QUIKSymbol("RTS-12.13", "SPBFUT", ISO4217.USD,
				SymbolType.FUT, "RIZ3", "ShortName", "Future RTS-12.13");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals("RTS-12.13", symbol.getCode());
		assertEquals("SPBFUT", symbol.getClassCode());
		assertEquals(ISO4217.USD, symbol.getCurrency());
		assertEquals(SymbolType.FUT, symbol.getType());
		assertEquals("RIZ3", symbol.getSystemCode());
		assertEquals("ShortName", symbol.getShortName());
		assertEquals("Future RTS-12.13", symbol.getDisplayName());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int expected = new Symbol("RTS-12.13", "SPBFUT", ISO4217.USD, SymbolType.FUT).hashCode(); 
		assertEquals(expected, symbol.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(symbol.equals(new Symbol("RTS-12.13", "SPBFUT", ISO4217.USD, SymbolType.FUT)));
		assertTrue(symbol.equals(symbol));
		assertFalse(symbol.equals(null));
		assertFalse(symbol.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("RTS-12.13")
			.add("GZDBRSDS2");
		Variant<String> vClass = new Variant<String>(vCode)
			.add("SPBFUT")
			.add("ZEBRA");
		Variant<Currency> vCurr = new Variant<Currency>(vClass)
			.add(ISO4217.USD)
			.add(ISO4217.EUR);
		Variant<SymbolType> vType = new Variant<SymbolType>(vCurr)
			.add(SymbolType.FUT)
			.add(SymbolType.CASH);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		QUIKSymbol x, found = null;
		do {
			x = new QUIKSymbol(vCode.get(), vClass.get(),
					vCurr.get(), vType.get(), "NotUsed", "AnyName", "Jubba");
			if ( symbol.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("RTS-12.13", found.getCode());
		assertEquals("SPBFUT", found.getClassCode());
		assertEquals(ISO4217.USD, found.getCurrency());
		assertEquals(SymbolType.FUT, found.getType());
		assertEquals("NotUsed", found.getSystemCode());
		assertEquals("AnyName", found.getShortName());
		assertEquals("Jubba", found.getDisplayName());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("RTS-12.13@SPBFUT(FUT/USD)", symbol.toString());
	}

}
