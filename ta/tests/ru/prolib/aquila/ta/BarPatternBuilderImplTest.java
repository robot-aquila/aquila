package ru.prolib.aquila.ta;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.BarPattern;
import ru.prolib.aquila.ta.BarPatternBuilderImpl;

public class BarPatternBuilderImplTest {
	BarPatternBuilderImpl builder;

	@Before
	public void setUp() throws Exception {
		builder = new BarPatternBuilderImpl();
	}
	
	@Test
	public void testBuildBarPattern() throws Exception {
		Candle bar = new Candle(new Date(), 150d, 180d, 119d, 144d, 0);
		BarPattern pattern = builder.buildBarPattern(130d, 10d, bar);
		assertNotNull(pattern);
		assertEquals(5, pattern.getTop());
		assertEquals(-1, pattern.getBottom());
		assertEquals(2, pattern.getOpen());
		assertEquals(1, pattern.getClose());
	}

}
