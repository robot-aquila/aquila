package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-22<br>
 * $Id: GStringMapTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GStringMapTest {
	private IMocksControl control;
	private FirePanicEvent fire;
	private G<String> gKey;
	private Map<String, Integer> map1,map2;
	private GStringMap<Integer> getter_st,getter_ni;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		fire = control.createMock(FirePanicEvent.class);
		gKey = control.createMock(G.class);
		map1 = new HashMap<String, Integer>();
		map1.put("one", 1);
		map1.put("three", 3);
		map2 = new HashMap<String, Integer>();
		map2.put("foo", 8);
		map2.put("bar", 2);
		getter_st = new GStringMap<Integer>(fire,gKey,map1,null,true,"PFX: ");
		getter_ni = new GStringMap<Integer>(fire,gKey,map2,123,false,"A");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(fire, getter_st.getFirePanicEvent());
		assertSame(gKey, getter_st.getKeyGetter());
		assertNull(getter_st.getDefaultValue());
		assertTrue(getter_st.isStrict());
		assertEquals("PFX: ", getter_st.getMessagePrefix());
		
		assertSame(fire, getter_ni.getFirePanicEvent());
		assertSame(gKey, getter_ni.getKeyGetter());
		assertEquals(123, (int)getter_ni.getDefaultValue());
		assertFalse(getter_ni.isStrict());
		assertEquals("A", getter_ni.getMessagePrefix());
	}
	
	@Test
	public void testGet_ForStrict() throws Exception {
		String errMsg = "PFX: No matches found: {}";
		Object fix[][] = {
			// key, msg?, msg args?, result
			{ "one",	null,	null, 					 1		},
			{ null,		errMsg, new Object[]{ null },	 null	},
			{ "three",	null,	null,					 3		},
			{ "other",	errMsg,	new Object[]{ "other" }, null	},
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gKey.get(same(this))).andReturn((String) fix[i][0]);
			String m = (String) fix[i][1]; 
			if ( m != null ) {
				Object[] args = (Object[]) fix[i][2];
				fire.firePanicEvent(eq(1), eq(m), aryEq(args));
			}
			control.replay();
			Integer expected = (Integer) fix[i][3]; 
			Integer actual = getter_st.get(this);
			assertEquals("At #" + i, expected, actual);
			control.verify();
		}
	}
	
	@Test
	public void testGet_ForNice() throws Exception {
		Object fix[][] = {
			// key, result
			{ "foo",	8 },
			{ null,		123 },
			{ "bar",	2 },
			{ "buz",	123 },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gKey.get(same(this))).andReturn((String) fix[i][0]);
			control.replay();
			Integer expected = (Integer) fix[i][1]; 
			Integer actual = getter_ni.get(this);
			assertEquals("At #" + i, expected, actual);
			control.verify();
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter_st.equals(getter_st));
		assertFalse(getter_st.equals(this));
		assertFalse(getter_st.equals(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		//getter_ni = new GStringMap<Integer>(fire,gKey,map2,123,false,"A");
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(fire)
			.add(control.createMock(FirePanicEvent.class));
		Variant<G<String>> vGtr = new Variant<G<String>>(vFire)
			.add(gKey)
			.add(control.createMock(G.class));
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("foo", 8);
		m.put("bar", 2);
		Variant<Map<String, Integer>> vMap =
				new Variant<Map<String, Integer>>(vGtr)
			.add(m)
			.add(map1);
		Variant<Integer> vDef = new Variant<Integer>(vMap)
			.add(123)
			.add(321);
		Variant<Boolean> vStrict = new Variant<Boolean>(vDef)
			.add(true)
			.add(false);
		Variant<String> vPfx = new Variant<String>(vStrict)
			.add("A")
			.add("foobar");
		Variant<?> iterator = vPfx;
		int foundCnt = 0;
		GStringMap<Integer> x = null, found = null;
		do {
			x = new GStringMap<Integer>(vFire.get(), vGtr.get(), vMap.get(),
					vDef.get(), vStrict.get(), vPfx.get());
			if ( getter_ni.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(fire, found.getFirePanicEvent());
		assertSame(gKey, found.getKeyGetter());
		assertEquals(m, found.getMap());
		assertEquals(123, (int)found.getDefaultValue());
		assertFalse(found.isStrict());
		assertEquals("A", found.getMessagePrefix());
	}

	@Test
	public void testToString() throws Exception {
		String expected = "GStringMap[value=" + gKey
			+ ", map=" + map2 + ", strict=false, def=123, msgPfx='A']";
		assertEquals(expected, getter_ni.toString());
	}
	
}
