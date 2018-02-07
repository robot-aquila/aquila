package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.core.utils.Variant;

public class ValueAxisViewportImplTest {
	private ValueAxisViewportImpl viewport;

	@Before
	public void setUp() throws Exception {
		viewport = new ValueAxisViewportImpl();
		viewport.setValueRange(new Range<>(of(10L), of(20L)));
	}

	@Test
	public void testSetValueRange() {
		assertEquals(new Range<CDecimal>(of(10L), of(20L)), viewport.getValueRange());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(viewport.equals(viewport));
		assertFalse(viewport.equals(this));
		assertFalse(viewport.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<Long> vMin = new Variant<>(10L, 5L);
		Variant<Long> vMax = new Variant<>(vMin, 20L, 50L);
		Variant<?> iterator = vMax;
		int foundCnt = 0;
		ValueAxisViewportImpl x, found = null;
		do {
			x = new ValueAxisViewportImpl();
			x.setValueRange(new Range<>(of(vMin.get()), of(vMax.get())));
			if ( viewport.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Range<CDecimal>(of(10L), of(20L)), found.getValueRange());
	}

}
