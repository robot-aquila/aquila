package ru.prolib.aquila.quik.subsys.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;

/**
 * 2013-02-22<br>
 * $Id: QUIKGetOrderTypeCodeTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class QUIKGetOrderTypeCodeTest {
	private IMocksControl control;
	private G<String> gMode;
	private QUIKGetOrderTypeCode getter;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gMode = control.createMock(G.class);
		getter = new QUIKGetOrderTypeCode(gMode);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gMode, getter.getModeGetter());
	}
	
	@Test
	public void testGet() throws Exception {
		String fix[][] = {
				// mode, expected
				{ null, null },
				{ "LRO", "L" },
				{ "", null },
				{ "M", "M" },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gMode.get(this)).andReturn(fix[i][0]);
			control.replay();
			assertEquals("At #" + i, fix[i][1], getter.get(this));
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		G<String> gMode2 = control.createMock(G.class);
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new QUIKGetOrderTypeCode(gMode)));
		assertFalse(getter.equals(new QUIKGetOrderTypeCode(gMode2)));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}

}
