package ru.prolib.aquila.core.BusinessEntities.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-14<br>
 * $Id$
 */
public class GOrderDirTest {
	private IMocksControl control;
	private EditableTerminal firePanic;
	private G<String> gString;
	private GOrderDir getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(EditableTerminal.class);
		gString = control.createMock(G.class);
		getter = new GOrderDir(firePanic, gString, "BUY", "SELL", "Test: ");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, getter.getFirePanicEvent());
		assertSame(gString, getter.getValueGetter());
		assertEquals("BUY", getter.getBuyEquiv());
		assertEquals("SELL", getter.getSellEquiv());
		assertEquals("Test: ", getter.getMessagePrefix());
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		expect(gString.get(this)).andReturn("BUY");
		expect(gString.get(this)).andReturn("SELL");
		control.replay();
		
		assertEquals(Direction.BUY, getter.get(this));
		assertEquals(Direction.SELL, getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_PanicIfValueIsNull() throws Exception {
		expect(gString.get(this)).andReturn(null);
		firePanic.firePanicEvent(eq(1),
				eq("Test: NULL values not allowed for: {}"),
				aryEq(new Object[] { gString }));
		control.replay();
		
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_PanicIfValueIsUnexpected() throws Exception {
		expect(gString.get(this)).andReturn("zulu");
		firePanic.firePanicEvent(eq(1),
				eq("Test: Unexpected value '{}' for: {}"),
				aryEq(new Object[] { "zulu", getter } ));
		control.replay();
		
		assertNull(getter.get(this));
		
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
		Variant<G<String>> vGtr = new Variant<G<String>>(vFire)
			.add(gString)
			.add(control.createMock(G.class));
		Variant<String> vBuy = new Variant<String>(vGtr)
			.add("BUY")
			.add("B");
		Variant<String> vSell = new Variant<String>(vBuy)
			.add("SELL")
			.add("S");
		Variant<String> vMsgPfx = new Variant<String>(vSell)
			.add("Test: ")
			.add("foobar");
		Variant<?> iterator = vMsgPfx;
		int foundCnt = 0;
		GOrderDir x = null, found = null;
		do {
			x = new GOrderDir(vFire.get(), vGtr.get(),
					vBuy.get(), vSell.get(), vMsgPfx.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertSame(gString, found.getValueGetter());
		assertEquals("BUY", found.getBuyEquiv());
		assertEquals("SELL", found.getSellEquiv());
		assertEquals("Test: ", found.getMessagePrefix());
	}
	
	@Test
	public void testToString() throws Exception {
		String expected = "GOrderDir[value=" + gString
			+ ", buy='BUY', sell='SELL', msgPfx='Test: ']";
		assertEquals(expected, getter.toString());
	}

}
