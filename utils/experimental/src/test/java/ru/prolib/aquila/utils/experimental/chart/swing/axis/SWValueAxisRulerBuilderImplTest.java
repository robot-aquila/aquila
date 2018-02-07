package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

public class SWValueAxisRulerBuilderImplTest {
	private SWValueAxisRulerBuilderImpl builder;

	@Before
	public void setUp() throws Exception {
		builder = new SWValueAxisRulerBuilderImpl();
	}
	
	@Test
	public void testCtor() {
		assertEquals(of("0.01"), builder.getTickSize());
		assertSame(LABEL_FONT, builder.getLabelFont());
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
