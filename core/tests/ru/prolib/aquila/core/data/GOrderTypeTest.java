package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-27<br>
 * $Id: GOrderTypeTest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GOrderTypeTest {
	private static IMocksControl control;
	private static G<Security> gSecurity;
	private static G<Double> gPrice;
	private static Security security;
	private static GOrderType getter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		gSecurity = control.createMock(G.class);
		gPrice = control.createMock(G.class);
		security = control.createMock(Security.class);
		getter = new GOrderType(gSecurity, gPrice);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gSecurity, getter.getSecurityGetter());
		assertSame(gPrice, getter.getPriceGetter());
	}
	
	@Test
	public void testGet() throws Exception {
		Object fix[][] = {
				// security, price, expected
				{ null,		null,		null },
				{ null,		10.25d,		null },
				{ security, null,		null },
				{ security, 10.25d,		OrderType.LIMIT  },
				{ security,  0.00d,		OrderType.MARKET },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			control.resetToStrict();
			expect(security.getMinStepSize()).andStubReturn(0.01d);
			expect(gSecurity.get(this)).andReturn((Security) fix[i][0]);
			expect(gPrice.get(this)).andReturn((Double) fix[i][1]);
			control.replay();
			assertEquals(msg, fix[i][2], getter.get(this));
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<Double>> vPrice = new Variant<G<Double>>()
			.add(gPrice)
			.add(null)
			.add(control.createMock(G.class));
		Variant<G<Security>> vSecurity = new Variant<G<Security>>(vPrice)
			.add(gSecurity)
			.add(null)
			.add(control.createMock(G.class));
		int foundCnt = 0;
		GOrderType found = null;
		do {
			GOrderType actual =
				new GOrderType(vSecurity.get(), vPrice.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vSecurity.next() );
		assertEquals(1, foundCnt);
		assertSame(gPrice, found.getPriceGetter());
		assertSame(gSecurity, found.getSecurityGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(this));
		assertFalse(getter.equals(null));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/73211)
			.append(gSecurity)
			.append(gPrice)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
