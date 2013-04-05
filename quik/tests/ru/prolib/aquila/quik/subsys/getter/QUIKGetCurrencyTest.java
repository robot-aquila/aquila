package ru.prolib.aquila.quik.subsys.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-25<br>
 * $Id: QUIKGetCurrencyTest.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class QUIKGetCurrencyTest {
	private IMocksControl control;
	private G<String> gCode;
	private QUIKGetCurrency getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gCode = control.createMock(G.class);
		getter = new QUIKGetCurrency(gCode, "SUR");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gCode, getter.getCodeGetter());
		assertEquals("SUR", getter.getDefaultCurrencyCode());
	}
	
	@Test
	public void testGet() throws Exception {
		String fix[][] = {
				// code, expected
				{ "USD", "USD" },
				{ null,  "SUR" },
				{ "",    "SUR" },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gCode.get(this)).andReturn(fix[i][0]);
			control.replay();
			assertEquals("At #" + i, fix[i][1], getter.get(this));
			control.verify();
		}
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
		Variant<G<String>> vGtr = new Variant<G<String>>()
			.add(gCode)
			.add(control.createMock(G.class));
		Variant<String> vDef = new Variant<String>(vGtr)
			.add("SUR")
			.add("BUR");
		Variant<?> iterator = vDef;
		int foundCnt = 0;
		QUIKGetCurrency x = null, found = null;
		do {
			x = new QUIKGetCurrency(vGtr.get(), vDef.get());
			if ( getter.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gCode, found.getCodeGetter());
		assertEquals("SUR", found.getDefaultCurrencyCode());
	}

}
