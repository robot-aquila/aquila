package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-16<br>
 * $Id$
 */
public class GNotNullTest {
	private IMocksControl control;
	private EditableTerminal firePanic;
	private G<Integer> gValue;
	private GNotNull<Integer> getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(EditableTerminal.class);
		gValue = control.createMock(G.class);
		getter = new GNotNull<Integer>(firePanic, gValue, "Test: ");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, getter.getFirePanicEvent());
		assertSame(gValue, getter.getValueGetter());
		assertEquals("Test: ", getter.getMessagePrefix());
	}
	
	@Test
	public void testGet_IfValueIsNull() throws Exception {
		expect(gValue.get(same(this))).andReturn(null);
		firePanic.firePanicEvent(eq(1),
				eq("Test: NULL values not allowed for: {}"),
				aryEq(new Object[] { gValue }));
		control.replay();
		
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		Integer value = new Integer(123);
		expect(gValue.get(same(this))).andReturn(value);
		control.replay();
		
		assertSame(value, getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vFire = new Variant<EditableTerminal>()
			.add(firePanic)
			.add(control.createMock(EditableTerminal.class));
		Variant<G<Integer>> vGtr = new Variant<G<Integer>>(vFire)
			.add(gValue)
			.add(control.createMock(G.class));
		Variant<String> vPfx = new Variant<String>(vGtr)
			.add("Test: ")
			.add("foobar");
		Variant<?> iterator = vPfx;
		int foundCnt = 0;
		GNotNull<Integer> found = null, x = null;
		do {
			x = new GNotNull<Integer>(vFire.get(), vGtr.get(), vPfx.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(gValue, found.getValueGetter());
		assertEquals("Test: ", found.getMessagePrefix());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "GNotNull[value=" + gValue + ", msgPfx='Test: ']";
		assertEquals(expected, getter.toString());
	}

}
