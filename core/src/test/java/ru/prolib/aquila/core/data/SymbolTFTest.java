package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolTFTest {
	private static final Symbol
		symbol1 = new Symbol("AAPL"),
		symbol2 = new Symbol("MSFT");
	private TFSymbol key;

	@Before
	public void setUp() throws Exception {
		key = new TFSymbol(symbol1, ZTFrame.M10);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(symbol1, key.getSymbol());
		assertEquals(ZTFrame.M10, key.getTimeFrame());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(key.equals(key));
		assertFalse(key.equals(null));
		assertFalse(key.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Symbol> vSym = new Variant<>(symbol1, symbol2);
		Variant<ZTFrame> vTF = new Variant<>(vSym, ZTFrame.M10, ZTFrame.D1);
		Variant<?> iterator = vTF;
		int foundCnt = 0;
		TFSymbol x, found = null;
		do {
			x = new TFSymbol(vSym.get(), vTF.get());
			if ( key.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(symbol1, found.getSymbol());
		assertEquals(ZTFrame.M10, found.getTimeFrame());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(648271, 715)
				.append(symbol1)
				.append(ZTFrame.M10)
				.toHashCode(), key.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("AAPL[M10[UTC]]", key.toString());
	}

}
