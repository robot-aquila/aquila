package ru.prolib.aquila.core.data.getter;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.getter.GPrice;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-25<br>
 * $Id: GPriceTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GPriceTest {
	private IMocksControl control;
	private GPrice getter;
	private G<Double> gValue;
	private G<PriceUnit> gUnit;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gValue = control.createMock(G.class);
		gUnit = control.createMock(G.class);
		getter = new GPrice(gValue, gUnit);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(gValue, getter.getValueGetter());
		assertSame(gUnit, getter.getUnitGetter());
	}
	
	@Test
	public void testGet() throws Exception {
		Object fixture[][] = {
			// value, unit, expected
			{ 12.345d,	PriceUnit.MONEY, new Price(PriceUnit.MONEY, 12.345d) },
			{ 12.345d,	null,   		 null },
			{ null,		PriceUnit.MONEY, null },
			{ null,		null,   		 null },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			Price expected = (Price) fixture[i][2];
			control.resetToStrict();
			expect(gValue.get(this)).andReturn((Double) fixture[i][0]);
			expect(gUnit.get(this)).andReturn((PriceUnit) fixture[i][1]);
			control.replay();
			assertEquals(msg, expected, getter.get(this));
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		
		Variant<G<Double>> vValue = new Variant<G<Double>>()
			.add(gValue)
			.add(null)
			.add(control.createMock(G.class));
		Variant<G<PriceUnit>> vUnit = new Variant<G<PriceUnit>>(vValue)
			.add(gUnit)
			.add(null)
			.add(control.createMock(G.class));
		int foundCnt = 0;
		GPrice found = null;
		do {
			GPrice actual = new GPrice(vValue.get(), vUnit.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vUnit.next() );
		assertEquals(1, foundCnt);
		assertSame(gValue, found.getValueGetter());
		assertSame(gUnit, found.getUnitGetter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/75701)
			.append(gValue)
			.append(gUnit)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
