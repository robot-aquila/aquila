package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public class BarChartViewportImplTest {
	private BarChartViewportImpl viewport;

	@Before
	public void setUp() throws Exception {
		viewport = new BarChartViewportImpl();
	}

	@Test
	public void testSettersAndGetters() {
		assertEquals(0, viewport.getFirstVisibleCategory());
		assertEquals(0, viewport.getNumberOfVisibleCategories());
		
		viewport.setVisibleCategories(10,  50);
		viewport.setPreferredValueRange(new Range<CDecimal>(of(5L), of(10L)));
		viewport.setVisibleValueRange(new Range<CDecimal>(of(8L), of(16L)));
		
		assertEquals(10, viewport.getFirstVisibleCategory());
		assertEquals(50, viewport.getNumberOfVisibleCategories());
		assertEquals(new Range<CDecimal>(of(5L), of(10L)), viewport.getPreferredValueRange());
		assertEquals(new Range<CDecimal>(of(8L), of(16L)), viewport.getVisibleValueRange());
	}

}
