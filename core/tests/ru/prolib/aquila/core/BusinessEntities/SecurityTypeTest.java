package ru.prolib.aquila.core.BusinessEntities;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-12-18<br>
 * $Id: SecurityTypeTest.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class SecurityTypeTest {

	/**
	 * Создать конструктор хэш-кода.
	 * <p>
	 * @return конструктор хэш-кода
	 */
	private static HashCodeBuilder hcBuilder() {
		return new HashCodeBuilder(20121219, 142011);
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hcode[] = new int[5];
		String scode[][] = {
				{ "UNK",  "Unknown" },
				{ "STK",  "Stock"   },
				{ "OPT",  "Option"  },
				{ "FUT",  "Futures" },
				{ "BOND", "Bond"    },
				{ "CASH", "Cash"    },
			};
		for ( int i = 0; i < hcode.length; i ++ ) {
			hcode[i] = hcBuilder()
				.append(scode[i][0])
				.append(scode[i][1])
				.toHashCode();
		}
		SecurityType stype[] = { SecurityType.UNK, SecurityType.STK,
				SecurityType.OPT, SecurityType.FUT, SecurityType.BOND,
				SecurityType.CASH };
		for ( int i = 0; i < hcode.length; i ++ ) {
			assertEquals("For " + stype[i], hcode[i], stype[i].hashCode());
			assertEquals(scode[i][0], stype[i].getCode());
			assertEquals(scode[i][1], stype[i].getName());
		}
	}
	
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

}
