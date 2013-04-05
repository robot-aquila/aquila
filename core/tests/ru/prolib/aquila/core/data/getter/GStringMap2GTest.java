package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-23<br>
 * $Id: GStringMap2GTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GStringMap2GTest {
	private IMocksControl control;
	private G<String> gKey1, gKey2;
	private G<Integer> g1, g2;
	private Map<String, G<Integer>> map1, map2;
	private GStringMap2G<Integer> getter1, getter2;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gKey1 = control.createMock(G.class);
		gKey2 = control.createMock(G.class);
		g1 = control.createMock(G.class);
		g2 = control.createMock(G.class);
		map1 = new HashMap<String, G<Integer>>();
		map1.put("foo", g1);
		map1.put("bar", g2);
		map2 = new HashMap<String, G<Integer>>();
		map1.put("bar", g2);
		getter1 = new GStringMap2G<Integer>(gKey1, map1, 100);
		getter2 = new GStringMap2G<Integer>(gKey2, map2, null);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gKey1, getter1.getKeyGetter());
		assertEquals(map1, getter1.getMap());
		assertEquals(100, (int) getter1.getDefaultValue());
		
		assertSame(gKey2, getter2.getKeyGetter());
		assertEquals(map2, getter2.getMap());
		assertNull(getter2.getDefaultValue());
	}
	
	@Test
	public void testGet_MatchFound() throws Exception {
		Integer value = new Integer(12345);
		expect(gKey1.get(same(this))).andReturn("bar");
		expect(g2.get(same(this))).andReturn(value);
		control.replay();
		
		assertSame(value, getter1.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_FoundGetterReturnsNull() throws Exception {
		expect(gKey1.get(same(this))).andReturn("bar");
		expect(g2.get(same(this))).andReturn(null);
		control.replay();
		
		assertNull(getter1.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_NoMatchesFoundReturnsDefault() throws Exception {
		expect(gKey1.get(same(this))).andReturn("echo");
		control.replay();
		
		assertEquals(new Integer(100), getter1.get(this));
		
		control.verify();
	}

	@Test
	public void testGet_NoMatchesFoundReturnsNull() throws Exception {
		expect(gKey2.get(same(this))).andReturn("echo");
		control.replay();
		
		assertNull(getter2.get(this));
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter1.equals(getter1));
		assertFalse(getter1.equals(null));
		assertFalse(getter1.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<G<String>> vKey = new Variant<G<String>>()
			.add(gKey1)
			.add(gKey2);
		Variant<Map<String, G<Integer>>> vMap =
				new Variant<Map<String, G<Integer>>>(vKey)
			.add(map1)
			.add(map2);
		Variant<Integer> vDef = new Variant<Integer>(vMap)
			.add(100)
			.add(null);
		Variant<?> iterator = vDef;
		int foundCnt = 0;
		GStringMap2G<Integer> found = null, x = null;
		do {
			x = new GStringMap2G<Integer>(vKey.get(), vMap.get(), vDef.get());
			if ( getter1.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gKey1, found.getKeyGetter());
		assertEquals(map1, found.getMap());
		assertEquals(new Integer(100), found.getDefaultValue());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "GStringMap2G[value=" + gKey1
			+ ", map=" + map1 + ", def=100]";
		assertEquals(expected, getter1.toString());
	}

}
