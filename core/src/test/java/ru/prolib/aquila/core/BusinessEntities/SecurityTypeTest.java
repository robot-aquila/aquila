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
public class SecurityTypeTest {
	
	@Test
	public void testToString() throws Exception {
		Map<SymbolType, String> map = new HashMap<SymbolType, String>();
		map.put(SymbolType.UNK,  "UNK");
		map.put(SymbolType.STK,  "STK");
		map.put(SymbolType.OPT,  "OPT");
		map.put(SymbolType.FUT,  "FUT");
		map.put(SymbolType.BOND, "BOND");
		map.put(SymbolType.CASH, "CASH");
		Iterator<Entry<SymbolType, String>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Entry<SymbolType, String> entry = it.next();
			assertEquals(entry.getValue(), entry.getKey().toString());
		}
	}
	
	@Test
	public void testOrdinal() throws Exception {
		assertEquals(0, SymbolType.UNK.ordinal());
		assertEquals(1, SymbolType.STK.ordinal());
		assertEquals(2, SymbolType.OPT.ordinal());
		assertEquals(3, SymbolType.FUT.ordinal());
		assertEquals(4, SymbolType.BOND.ordinal());
		assertEquals(5, SymbolType.CASH.ordinal());
	}
	
	@Test
	public void testValueOf() throws Exception {
		assertSame(SymbolType.FUT, SymbolType.valueOf("FUT"));
	}

}
