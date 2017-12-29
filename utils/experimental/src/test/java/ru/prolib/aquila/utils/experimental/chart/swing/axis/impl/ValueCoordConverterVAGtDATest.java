package ru.prolib.aquila.utils.experimental.chart.swing.axis.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.impl.ValueCoordConverterVAGtDA;

public class ValueCoordConverterVAGtDATest {
	private ValueCoordConverterVAGtDA service;

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConvertation_Case1() {
		service = new ValueCoordConverterVAGtDA(
				new Range<>(of("75.1234"), of("447")),
				new Range<>(10, 200));
		assertEquals( of("75.1234"), service.getMinValue());
		assertEquals(of("447"),		 service.getMaxValue());
		assertEquals(of("371.8766"), service.getValueRange());
		assertEquals( 10, service.getDisplayMin());
		assertEquals(200, service.getDisplayMax());
		assertEquals(190, service.getDisplaySize());
		assertEquals(of("1.957245264"), service.getRatio());
		
		assertEquals( 10, service.toDisplay( of("75.1234")));
		assertEquals(200, service.toDisplay(of("447.0000")));
		assertEquals(175, service.toDisplay(of("399.0027"))); // 399.0027-75.1234=323.8793/1.957245264+10=175
		assertEquals( 29, service.toDisplay(of("112.09")));   // 112.09-75.1234=36.9666/1.957245264+10=29
		
		assertEquals( of("75.1234"), service.toValue( 10));
		assertEquals(of("447.0000"), service.toValue(200));
		assertEquals(of("398.0689"), service.toValue(175)); // 175-10=165*1.957245264+75.1234=398.0689
		assertEquals(of("112.3111"), service.toValue( 29)); // 29-10=19*1.957245264+75.1234=112.3111
	}
	
	@Test
	public void testConvertation_Case2() {
		service = new ValueCoordConverterVAGtDA(
				new Range<>(of(61290L), of(69780L)),
				new Range<>(15, 220));
		assertEquals(of(61290L), service.getMinValue());
		assertEquals(of(69780L), service.getMaxValue());
		assertEquals(of( 8490L), service.getValueRange());
		assertEquals( 15, service.getDisplayMin());
		assertEquals(220, service.getDisplayMax());
		assertEquals(205, service.getDisplaySize());
		assertEquals(of("41.41464"), service.getRatio());
		
		assertEquals( 15, service.toDisplay(of(61290L)));
		assertEquals(220, service.toDisplay(of(69780L)));
		assertEquals(181, service.toDisplay(of(68150L))); // 68150-61290=6870/41.41464+15=181
		assertEquals( 35, service.toDisplay(of(62120L))); // 62120-61290=830/41.41464+15=35
		
		assertEquals(of(61290L), service.toValue( 15));
		assertEquals(of(69780L), service.toValue(220));
		assertEquals(of(68165L), service.toValue(181)); // 181-15=166*41.41464+61290=68165
		assertEquals(of(62118L), service.toValue( 35)); // 35-15=20*41.41464+61290=62118
	}
	
	@Test
	public void testConvertation_Case3() {
		service = new ValueCoordConverterVAGtDA(
				new Range<>(of("250.13"), of("492.01")),
				new Range<>(45, 268));
		assertEquals(of("250.13"), service.getMinValue());
		assertEquals(of("492.01"), service.getMaxValue());
		assertEquals(of("241.88"), service.getValueRange());
		assertEquals( 45, service.getDisplayMin());
		assertEquals(268, service.getDisplayMax());
		assertEquals(223, service.getDisplaySize());
		assertEquals(of("1.0846637"), service.getRatio());
		
		assertEquals( 45, service.toDisplay(of("250.13")));
		assertEquals(268, service.toDisplay(of("492.01")));
		assertEquals( 96, service.toDisplay(of("305.29"))); // 305.29-250.13=55.16/1.0846637+45=96
		assertEquals(258, service.toDisplay(of("481.15"))); // 481.15-250.13=231.02/1.0846637+45=258
		
		assertEquals(of("250.13"), service.toValue( 45));
		assertEquals(of("492.01"), service.toValue(268));
		assertEquals(of("305.45"), service.toValue( 96)); // 96-45=51*1.0846637+250.13=305.45
		assertEquals(of("481.16"), service.toValue(258)); // 258-45=213*1.0846637+250.13=481.16
	}

}
