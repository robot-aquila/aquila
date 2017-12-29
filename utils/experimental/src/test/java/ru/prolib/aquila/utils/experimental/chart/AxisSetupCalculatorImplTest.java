package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.ValueAxisSetup;

public class AxisSetupCalculatorImplTest {
	
	static CDecimal cd(long value) {
		return CDecimalBD.of(value);
	}
	
	static CDecimal cd(String value) {
		return CDecimalBD.of(value);
	}
	
	private AxisSetupCalculatorImpl service;

	@Before
	public void setUp() throws Exception {
		service = new AxisSetupCalculatorImpl();
	}

	@Test
	public void testGetValueAxisSetup_WhenMinGridStepLtLength() {
		ValueAxisSetup actual = service.getValueAxisSetup(cd(100L), cd(200L), cd(1L), 25, 30);
		
		ValueAxisSetup expected = new ValueAxisSetup(cd(100L), cd(200L), 25, 0, 25);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueAxisSetup_WhenLengthIsMultipleOfMinStepPx() {
		ValueAxisSetup actual = service.getValueAxisSetup(cd(100L), cd(200L), cd(1L), 50, 10);
		
		ValueAxisSetup expected = new ValueAxisSetup(cd(100L), cd(200L), 50, 5, 10);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueAxisSetup_WhenReminderCanSpreadOverLength() {
		ValueAxisSetup actual = service.getValueAxisSetup(cd(100L), cd(200L), cd(1L), 55, 10);
		
		ValueAxisSetup expected = new ValueAxisSetup(cd(100L), cd(200L), 55, 5, 11);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueAxisSetup_WhenSearchSuitableStepCount() {
		ValueAxisSetup actual = service.getValueAxisSetup(cd(100L), cd(200L), cd(1L), 56, 10);
		
		ValueAxisSetup expected = new ValueAxisSetup(cd(100L), cd(200L), 56, 4, 14);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testX() {
		CDecimal lengthPx = CDecimalBD.of(30L);
		CDecimal valueRange = CDecimalBD.of("31.0015");
		CDecimal r = valueRange.divideExact(lengthPx, 9, RoundingMode.CEILING);
		System.out.println(r);
	}

}
