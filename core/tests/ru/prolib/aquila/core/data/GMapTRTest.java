package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.OrderType;

/**
 * 2012-11-03<br>
 * $Id: GMapTRTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapTRTest {
	private static G<String> key;
	private static Map<String, OrderType> map;
	private static GMapTR<OrderType> getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		key = new GString();
		map = new HashMap<String, OrderType>();
		map.put("StopLoss", OrderType.STOP_LIMIT);
		map.put("Market", OrderType.MARKET);
		getter = new GMapTR<OrderType>(key, map);
	}
	
	@Test
	public void testGet() throws Exception {
		assertSame(OrderType.MARKET, getter.get("Market"));
		assertSame(OrderType.STOP_LIMIT, getter.get("StopLoss"));
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Map<String, OrderType> map2 = new HashMap<String, OrderType>();
		map2.put("Market", OrderType.MARKET);
		map2.put("StopLoss", OrderType.STOP_LIMIT);
		G<String> key2 = new GString();
		Map<String, OrderType> map3 = new HashMap<String, OrderType>();
		map3.put("StopLoss", OrderType.STOP_LIMIT);
		G<String> key3 = new GConst<String>("foobar");
		
		assertTrue(getter.equals(new GMapTR<OrderType>(key2, map2)));
		assertFalse(getter.equals(new GMapTR<OrderType>(key3, map2)));
		assertFalse(getter.equals(new GMapTR<OrderType>(key2, map3)));
		assertFalse(getter.equals(new GMapTR<OrderType>(key3, map3)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/22213)
			.append(key)
			.append(map)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
