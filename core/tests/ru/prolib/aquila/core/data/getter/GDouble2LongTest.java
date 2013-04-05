package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-14<br>
 * $Id$
 */
public class GDouble2LongTest {
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private G<Double> gDouble;
	private GDouble2Long getter_strict, getter_nice;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(FirePanicEvent.class);
		gDouble = control.createMock(G.class);
		getter_strict = new GDouble2Long(firePanic, gDouble, true, "STRICT: ");
		getter_nice = new GDouble2Long(firePanic, gDouble, false, "NICE: ");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, getter_strict.getFirePanicEvent());
		assertSame(gDouble, getter_strict.getValueGetter());
		assertTrue(getter_strict.isStrict());
		assertEquals("STRICT: ", getter_strict.getMessagePrefix());
		
		assertSame(firePanic, getter_nice.getFirePanicEvent());
		assertSame(gDouble, getter_nice.getValueGetter());
		assertFalse(getter_nice.isStrict());
		assertEquals("NICE: ", getter_nice.getMessagePrefix());
	}
	
	@Test
	public void testGet_ForStrictIfValueIsNull() throws Exception {
		expect(gDouble.get(this)).andReturn(null);
		firePanic.firePanicEvent(eq(1),
				eq("STRICT: NULL values not allowed for: {}"),
				aryEq(new Object[] { gDouble }));
		control.replay();
		
		assertNull(getter_strict.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_ForStrict_Ok() throws Exception {
		expect(gDouble.get(this)).andReturn(new Double(12.34));
		control.replay();
		
		assertEquals(new Long(12), getter_strict.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_ForNiceIfValueIsNull() throws Exception {
		expect(gDouble.get(this)).andReturn(null);
		control.replay();
		
		assertNull(getter_nice.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_ForNice_Ok() throws Exception {
		expect(gDouble.get(this)).andReturn(new Double(123.456));
		control.replay();
		
		assertEquals(new Long(123), getter_nice.get(this));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter_strict.equals(getter_strict));
		assertFalse(getter_strict.equals(this));
		assertFalse(getter_strict.equals(null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(control.createMock(FirePanicEvent.class));
		Variant<G<Double>> vGtr = new Variant<G<Double>>(vFire)
			.add(gDouble)
			.add(control.createMock(G.class));
		Variant<Boolean> vStrict = new Variant<Boolean>(vGtr)
			.add(true)
			.add(false);
		Variant<String> vMsgPfx = new Variant<String>(vStrict)
			.add("STRICT: ")
			.add("NICE: ");
		Variant<?> iterator = vMsgPfx;
		int foundCnt = 0;
		GDouble2Long x = null, found = null;
		do {
			x = new GDouble2Long(vFire.get(), vGtr.get(), vStrict.get(),
					vMsgPfx.get());
			if ( getter_strict.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(gDouble, found.getValueGetter());
		assertTrue(found.isStrict());
		assertEquals("STRICT: ", found.getMessagePrefix());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected1 = "GDouble2Long[value=" + gDouble
			+ ", strict=true, msgPfx='STRICT: ']";
		assertEquals(expected1, getter_strict.toString());
		
		String expected2 = "GDouble2Long[value=" + gDouble
			+ ", strict=false, msgPfx='NICE: ']";
		assertEquals(expected2, getter_nice.toString());
	}

}
