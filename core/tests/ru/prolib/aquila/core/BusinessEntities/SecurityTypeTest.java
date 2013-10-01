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
		Map<SecurityType, String> map = new HashMap<SecurityType, String>();
		map.put(SecurityType.UNK,  "UNK");
		map.put(SecurityType.STK,  "STK");
		map.put(SecurityType.OPT,  "OPT");
		map.put(SecurityType.FUT,  "FUT");
		map.put(SecurityType.BOND, "BOND");
		map.put(SecurityType.CASH, "CASH");
		Iterator<Entry<SecurityType, String>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Entry<SecurityType, String> entry = it.next();
			assertEquals(entry.getValue(), entry.getKey().toString());
		}
	}
	
	@Test
	public void testOrdinal() throws Exception {
		assertEquals(0, SecurityType.UNK.ordinal());
		assertEquals(1, SecurityType.STK.ordinal());
		assertEquals(2, SecurityType.OPT.ordinal());
		assertEquals(3, SecurityType.FUT.ordinal());
		assertEquals(4, SecurityType.BOND.ordinal());
		assertEquals(5, SecurityType.CASH.ordinal());
	}

}
