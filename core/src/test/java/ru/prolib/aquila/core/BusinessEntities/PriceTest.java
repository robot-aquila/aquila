package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-24<br>
 * $Id: PriceTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class PriceTest {
	
	@Test
	public void testConstruct() throws Exception {
		Price price = new Price(PriceUnit.PERCENT, 12.5678D);
		assertSame(PriceUnit.PERCENT, price.getUnit());
		assertEquals(12.5678D, price.getValue(), 0.00001D);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<PriceUnit> vUnit = new Variant<PriceUnit>()
			.add(PriceUnit.PERCENT)
			.add(PriceUnit.MONEY)
			.add(null);
		Variant<Double> vValue = new Variant<Double>(vUnit)
			.add(10.00d)
			.add(null);
		int foundCnt = 0;
		Price foundObj = null;
		Price expected = new Price(PriceUnit.PERCENT, 10.00d);
		do {
			Price actual = new Price(vUnit.get(), vValue.get());
			if ( expected.equals(actual) ) {
				foundObj = actual;
				foundCnt ++;
			}
			
		} while ( vValue.next() );
		assertEquals(1, foundCnt);
		assertNotNull(foundObj);
		assertSame(PriceUnit.PERCENT, foundObj.getUnit());
		assertEquals(10.00d, foundObj.getValue(), 0.001d);
	}
	
	@Test
	public void testHashCode() throws Exception {
		Price price = new Price(PriceUnit.PERCENT, 12.5678D);
		assertEquals(new HashCodeBuilder(20121231, 170523)
			.append(Price.class)
			.append(PriceUnit.PERCENT)
			.append(12.5678d)
			.toHashCode(), price.hashCode());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("12.5678 %",
				new Price(PriceUnit.PERCENT, 12.5678D).toString());
		assertEquals("80.99 PU",
				new Price(PriceUnit.MONEY, 80.99D).toString());
		assertEquals("100.0 Pts.",
				new Price(PriceUnit.POINT, 100D).toString());
	}

}
