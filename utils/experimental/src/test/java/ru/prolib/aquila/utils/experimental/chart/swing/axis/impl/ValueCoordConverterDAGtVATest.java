package ru.prolib.aquila.utils.experimental.chart.swing.axis.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.impl.ValueCoordConverterDAGtVA;

public class ValueCoordConverterDAGtVATest {
	private ValueCoordConverterDAGtVA service;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testConvertation_Case1() {
		service = new ValueCoordConverterDAGtVA(
				new Range<>(of("100.5"), of("145.15")),
				new Range<>(10, 210));
		assertEquals(of("100.5"),  service.getMinValue());
		assertEquals(of("145.15"), service.getMaxValue());
		assertEquals( of("44.65"), service.getValueRange());
		assertEquals( 10, service.getDisplayMin());
		assertEquals(210, service.getDisplayMax());
		assertEquals(200, service.getDisplaySize());
		assertEquals(of("4.4792834"), service.getRatio());
		
		assertEquals( 10, service.toDisplay(of("100.5")));
		assertEquals(210, service.toDisplay(of("145.15")));
		assertEquals( 97, service.toDisplay(of("120.03"))); // 120.03-100.5=19.53*4.4792834 -> 87.480404802+10= 97
		assertEquals(209, service.toDisplay(of("145.00"))); // 145.00-100.5=44.5 *4.4792834 ->199.3281113  +10=209
		assertEquals(200, service.toDisplay(of("142.91"))); // 142.91-100.5=42.41*4.4792834 ->189.966408994+10=200
		assertEquals(172, service.toDisplay(of("136.57"))); // 136.57-100.5=36.07*4.4792834 ->161.567752238+10=172
		
		assertEquals(of("100.50"), service.toValue( 10));
		assertEquals(of("145.15"), service.toValue(210)); // 210-10=200/4.4792834=44.6499991+100.5=145.1499991->145.15
		assertEquals(of("119.92"), service.toValue( 97)); // 97-10=87/4.4792834=19.4227496+100.5=119.9227496->119.92
		assertEquals(of("144.93"), service.toValue(209)); // 209-10=199/4.4792834=44.4267492+100.5=144.9267491->114.93
		assertEquals(of("142.92"), service.toValue(200)); // 200-10=190/4.4792834=42.4174991+100.5=142.9174991->142.92
		assertEquals(of("136.67"), service.toValue(172)); // 172-10=162/4.4792834=36.1664993+100.5=136.6664993->136.67
	}
	
	@Test
	public void testConvertation_Case2() {
		service = new ValueCoordConverterDAGtVA(
				new Range<>(of("48.213"), of("97.42")),
				new Range<>(45, 100));
		assertEquals(of("48.213"), service.getMinValue());
		assertEquals(of("97.42"), service.getMaxValue());
		assertEquals(of("49.207"), service.getValueRange());
		assertEquals( 45, service.getDisplayMin());
		assertEquals(100, service.getDisplayMax());
		assertEquals( 55, service.getDisplaySize());
		assertEquals(of("1.11772716"), service.getRatio());
		
		assertEquals( 45, service.toDisplay(of("48.213")));
		assertEquals(100, service.toDisplay(of("97.42")));
		assertEquals( 47, service.toDisplay(of("50")));		// 50.000-48.213=1.787*1.11772716=1.99737843492+45=47
		assertEquals( 85, service.toDisplay(of("84.001"))); // 84.001-48.213=35.788*1.11772716=40.00121960208+45=85
		
		assertEquals(of("48.213"), service.toValue( 45));
		assertEquals(of("97.420"), service.toValue(100));
		assertEquals(of("50.002"), service.toValue( 47)); // 47-45=2/1.11772716=1.78934544+48.213=50.002
		assertEquals(of("84.000"), service.toValue( 85)); // 85-45=40/1.11772716=35.78690885+48.213=84.000
	}
	
	@Test
	public void testConvertation_Case3() {
		service = new ValueCoordConverterDAGtVA(
				new Range<>(of(90L), of(450L)),
				new Range<>(100, 500));
		assertEquals( of(90L), service.getMinValue());
		assertEquals(of(450L), service.getMaxValue());
		assertEquals(of(360L), service.getValueRange());
		assertEquals(100, service.getDisplayMin());
		assertEquals(500, service.getDisplayMax());
		assertEquals(400, service.getDisplaySize());
		assertEquals(of("1.11112"), service.getRatio());
		
		assertEquals(100, service.toDisplay(of(90L)));
		assertEquals(500, service.toDisplay(of(450L)));
		assertEquals(150, service.toDisplay(of(135L))); // 135-90=45*1.11112+100=150
		assertEquals(440, service.toDisplay(of(396L))); // 396-90=306*1.11112+100=440
		
		assertEquals(of( 90L), service.toValue(100));
		assertEquals(of(450L), service.toValue(500));
		assertEquals(of(135L), service.toValue(150));
		assertEquals(of(396L), service.toValue(440));
	}

}
