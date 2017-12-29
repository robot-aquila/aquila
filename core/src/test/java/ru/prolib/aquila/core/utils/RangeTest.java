package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

public class RangeTest {
	private Range<CDecimal> range;

	@Before
	public void setUp() throws Exception {
		range = new Range<>(of("256.12"), of("768.94"));
	}
	
	@Test
	public void testCtor2() {
		assertEquals(of("256.12"), range.getMin());
		assertEquals(of("768.94"), range.getMax());
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor2_ThrowsIfMinIsNull() {
		new Range<CDecimal>(null, of("768.94"));
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor2_ThrowsIfMaxIsNull() {
		new Range<CDecimal>(null, of("256.12"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor2_ThrowsIfMinGtMax() {
		new Range<CDecimal>(of("768.94"), of("256.12"));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(range.equals(range));
		assertFalse(range.equals(null));
		assertFalse(range.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<CDecimal> vMin = new Variant<CDecimal>()
				.add(of("256.12")).add(of("14.1205"));
		Variant<CDecimal> vMax = new Variant<CDecimal>(vMin)
				.add(of("768.94")).add(of("400.09"));
		Variant<?> iterator = vMax;
		int foundCnt = 0;
		Range<CDecimal> x, found = null;
		do {
			x = new Range<>(vMin.get(), vMax.get());
			if ( range.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(of("256.12"), found.getMin());
		assertEquals(of("768.94"), found.getMax());
	}

	@Test
	public void testExtend() {
		assertSame(range, range.extend(null));
		Range<CDecimal> expected = new Range<>(of("170.05"), of("768.94"));
		assertEquals(expected, range.extend(new Range<>(of("170.05"), of("405.72"))));
		expected = new Range<>(of("256.12"), of("808.07"));
		assertEquals(expected, range.extend(new Range<>(of("308.92"), of("808.07"))));
		expected = new Range<>(of("92.48"), of("942.56"));
		assertEquals(expected, range.extend(new Range<>(of("92.48"), of("942.56"))));
	}	

}
