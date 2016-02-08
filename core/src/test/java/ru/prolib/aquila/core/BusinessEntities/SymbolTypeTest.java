package ru.prolib.aquila.core.BusinessEntities;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import static org.junit.Assert.*;
import org.junit.*;

/**
 * 2012-12-18<br>
 * $Id: SecurityTypeTest.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class SymbolTypeTest {
	
	@Test
	public void testToString() throws Exception {
		Map<SymbolType, String> map = new HashMap<SymbolType, String>();
		map.put(SymbolType.UNKNOWN,  "U");
		map.put(SymbolType.STOCK,	 "S");
		map.put(SymbolType.OPTION,	 "O");
		map.put(SymbolType.FUTURE,	 "F");
		map.put(SymbolType.BOND,	 "B");
		map.put(SymbolType.CURRENCY, "C");
		Iterator<Entry<SymbolType, String>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Entry<SymbolType, String> entry = it.next();
			assertEquals(entry.getValue(), entry.getKey().toString());
		}
	}
	
	@Test
	public void testOrdinal() throws Exception {
		assertEquals(0, SymbolType.UNKNOWN.ordinal());
		assertEquals(1, SymbolType.STOCK.ordinal());
		assertEquals(2, SymbolType.OPTION.ordinal());
		assertEquals(3, SymbolType.FUTURE.ordinal());
		assertEquals(4, SymbolType.BOND.ordinal());
		assertEquals(5, SymbolType.CURRENCY.ordinal());
	}
	
	@Test
	public void testValueOf() throws Exception {
		assertSame(SymbolType.FUTURE, SymbolType.valueOf("F"));
	}
	
	@Test
	public void testValueOf_Null() throws Exception {
		assertNull(SymbolType.valueOf(null));
	}

}
