package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class CDecimalBDTest {

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testCtor3_BD() {
		CDecimalBD x = new CDecimalBD(new BigDecimal("12.350"), "USD", RoundingMode.CEILING);
		
		assertEquals(new BigDecimal("12.350"), x.toBigDecimal());
		assertEquals("USD", x.getUnit());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(3, x.getScale());
	}
	
	@Test
	public void testCtor2_BD() {
		CDecimalBD x = new CDecimalBD(new BigDecimal("6.12"), "RUB");
		
		assertEquals(new BigDecimal("6.12"), x.toBigDecimal());
		assertEquals("RUB", x.getUnit());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
	}
	
	@Test
	public void testCtor1_BD() {
		CDecimalBD x = new CDecimalBD(new BigDecimal("256.44219"));
		
		assertEquals(new BigDecimal("256.44219"), x.toBigDecimal());
		assertNull(x.getUnit());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(5, x.getScale());
	}
	
	@Test
	public void testCtor3_S() {
		CDecimalBD x = new CDecimalBD("34.58617", "EUR", RoundingMode.FLOOR);
		
		assertEquals(new BigDecimal("34.58617"), x.toBigDecimal());
		assertEquals("EUR", x.getUnit());
		assertEquals(RoundingMode.FLOOR, x.getRoundingMode());
		assertEquals(5, x.getScale());
	}
	
	@Test
	public void testCtor2_S() {
		CDecimalBD x = new CDecimalBD("72.1", "%");
		
		assertEquals(new BigDecimal("72.1"), x.toBigDecimal());
		assertEquals("%", x.getUnit());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(1, x.getScale());
	}
	
	@Test
	public void testCtor1_S() {
		CDecimalBD x = new CDecimalBD("34.9720");
		
		assertEquals(new BigDecimal("34.9720"), x.toBigDecimal());
		assertNull(x.getUnit());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
	}
	
	@Test
	public void testToString() {
		CDecimalAbstract x1 = new CDecimalBD("34.25");
		CDecimalAbstract x2 = new CDecimalBD("-256.345", "USD");
		
		assertEquals("34.25", x1.toString());
		assertEquals("-256.345 USD", x2.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		CDecimalAbstract x = new CDecimalBD("25.19", "RUB", RoundingMode.HALF_UP);
		
		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals(this));
	}
	
	@Test
	public void testEquals() {
		CDecimalAbstract value = new CDecimalBD("25.19", "RUB", RoundingMode.HALF_UP);
		
		Variant<String> vVal = new Variant<>("25.19", "-92.65");
		Variant<String> vUnit = new Variant<>(vVal, "RUB", "USD", null);
		Variant<RoundingMode> vRM = new Variant<>(vUnit, RoundingMode.HALF_UP, RoundingMode.CEILING);
		Variant<?> iterator = vRM;
		int foundCnt = 0;
		CDecimalBD x, found = null;
		do {
			x = new CDecimalBD(vVal.get(), vUnit.get(), vRM.get());
			if ( value.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new BigDecimal("25.19"), found.toBigDecimal());
		assertEquals("RUB", found.getUnit());
		assertEquals(RoundingMode.HALF_UP, found.getRoundingMode());
		assertEquals(2, found.getScale());
	}
	
	@Test
	public void testCompareTo() {
		assertEquals( 1, new CDecimalBD("12.34", "RUR").compareTo(null));
		assertEquals( 0, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD(  "12.34", "RUR")));
		assertEquals( 0, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD("12.3400", "RUR")));
		assertEquals( 1, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD("12.3399", "RUR")));
		assertEquals(-1, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD("12.3401", "RUR")));
		assertEquals(-3, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD(  "12.34", "USD")));
		assertEquals(15, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD(  "12.34", "CAD")));
		assertEquals( 1, new CDecimalBD("12.34", "RUR").compareTo(new CDecimalBD(  "12.34")));
	}
	
	@Test
	public void testHashCode() {
		assertEquals(new HashCodeBuilder(1592781, 77263)
				.append(new BigDecimal("13.962"))
				.append("USD")
				.append(RoundingMode.CEILING)
				.toHashCode(), new CDecimalBD("13.962", "USD", RoundingMode.CEILING).hashCode());
	}
	
	@Test
	public void testAbs() {
		assertEquals(new CDecimalBD("12.382", "USD", RoundingMode.CEILING),
				new CDecimalBD("-12.382", "USD", RoundingMode.CEILING).abs());
		assertEquals(new CDecimalBD("12.382", "RUB", RoundingMode.HALF_DOWN),
				new CDecimalBD("12.382", "RUB", RoundingMode.HALF_DOWN).abs());
	}
	
	@Test
	public void testNegate() {
		assertEquals(new CDecimalBD("12.382", "USD", RoundingMode.CEILING),
				new CDecimalBD("-12.382", "USD", RoundingMode.CEILING).negate());
		assertEquals(new CDecimalBD("-12.382", "RUB", RoundingMode.HALF_DOWN),
				new CDecimalBD("12.382", "RUB", RoundingMode.HALF_DOWN).negate());
	}
	
	@Test
	public void testWithScale() {
		assertEquals(new CDecimalBD("12.383", "USD", RoundingMode.HALF_UP),
				new CDecimalBD("12.382546", "USD", RoundingMode.HALF_UP).withScale(3));
		assertEquals(new CDecimalBD("3.152000", "RUB", RoundingMode.CEILING),
				new CDecimalBD("3.152", "RUB", RoundingMode.CEILING).withScale(6));
	}
	
	@Test
	public void testWithZero() {
		assertEquals(new CDecimalBD("0.000", "EUR", RoundingMode.FLOOR),
				new CDecimalBD("56.721", "EUR", RoundingMode.FLOOR).withZero());
	}
	
	@Test
	public void testWithUnit() {
		assertEquals(new CDecimalBD("15.391", "USD", RoundingMode.HALF_DOWN),
				new CDecimalBD("15.391", "RUB", RoundingMode.HALF_DOWN).withUnit("USD"));
	}
	
	@Test
	public void testAdd_AA() {
		CDecimal expected = new CDecimalBD("24.956", null, RoundingMode.UNNECESSARY);
		CDecimal actual = new CDecimalBD("20.0", null, RoundingMode.UNNECESSARY)
				.add(new CDecimalBD("4.956", null, RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAdd_AU() {
		CDecimal expected = new CDecimalBD("120.506", "USD", RoundingMode.UP);
		CDecimal actual = new CDecimalBD("20.006", null, RoundingMode.UP)
				.add(new CDecimalBD("100.5", "USD", RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAdd_UA() {
		CDecimal expected = new CDecimalBD("540.210", "RUB", RoundingMode.HALF_DOWN);
		CDecimal actual = new CDecimalBD("540.400", "RUB", RoundingMode.HALF_DOWN)
				.add(new CDecimalBD("-0.190"));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAdd_UU() {
		CDecimal expected = new CDecimalBD("-200.00", "EUR", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("0.00", "EUR", RoundingMode.FLOOR)
				.add(new CDecimalBD("-200", "EUR", RoundingMode.HALF_DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAdd_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("100.0", "EUR").add(new CDecimalBD("100", "RUB"));
	}
	
	@Test
	public void testAdd_L() {
		CDecimal expected = new CDecimalBD("-256.345", "USD", RoundingMode.CEILING);
		CDecimal actual = new CDecimalBD("-260.345", "USD", RoundingMode.CEILING).add(4L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideAndDivideExact_Difference() {
		CDecimal dividend = new CDecimalBD("3.66");
		CDecimal divisor = new CDecimalBD("4");
		
		assertEquals(new CDecimalBD("0.92"), dividend.divide(divisor));
		assertEquals(new CDecimalBD("0.92"), dividend.divideExact(divisor, 2));
		assertEquals(new CDecimalBD("0.915"), dividend.divideExact(divisor, 3));
	}
	
	@Test
	public void testDivide_CD_AA() {
		CDecimal expected = new CDecimalBD("0.92", null, RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", null, RoundingMode.HALF_UP)
				.divide(new CDecimalBD("4", null, RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_CD_AU() {
		CDecimal expected = new CDecimalBD("-24.9010", "USD", RoundingMode.HALF_DOWN);
		CDecimal actual = new CDecimalBD("-12.4505", null, RoundingMode.HALF_DOWN)
				.divide(new CDecimalBD("0.5", "USD", RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_CD_UA() {
		CDecimal expected = new CDecimalBD("26.918100", "RUB", RoundingMode.CEILING);
		CDecimal actual = new CDecimalBD("141.050844", "RUB", RoundingMode.CEILING)
				.divide(new CDecimalBD("5.24", null, RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_CD_UU() {
		CDecimal expected = new CDecimalBD("76.000", "EUR", RoundingMode.HALF_DOWN);
		CDecimal actual = new CDecimalBD("1847.565", "EUR", RoundingMode.HALF_DOWN)
				.divide(new CDecimalBD("24.31", "EUR", RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivide_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("24.297", "EUR", RoundingMode.HALF_UP)
			.divide(new CDecimalBD("415", "RUB", RoundingMode.CEILING));
	}
	
	@Test
	public void testDivide_CD_Period() {
		CDecimal expected = new CDecimalBD("3.333");
		CDecimal actual = new CDecimalBD("100").divide(new CDecimalBD("30.000"));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_L() {
		CDecimal expected = new CDecimalBD("0.77", "RUB", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("188.58", "RUB", RoundingMode.FLOOR)
				.divide(242L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_L_Periodic() {
		CDecimal expected = new CDecimalBD("33.33");
		CDecimal actual = new CDecimalBD("100.00").divide(3L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_CD_AA() {
		CDecimal expected = new CDecimalBD("0.915", null, RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", null, RoundingMode.HALF_UP)
				.divideExact(new CDecimalBD("4", null, RoundingMode.DOWN), 3);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_CD_AU() {
		CDecimal expected = new CDecimalBD("0.915", "EUR", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", null, RoundingMode.HALF_UP)
				.divideExact(new CDecimalBD("4", "EUR", RoundingMode.DOWN), 3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_CD_UA() {
		CDecimal expected = new CDecimalBD("0.915", "RUB", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", "RUB", RoundingMode.HALF_UP)
				.divideExact(new CDecimalBD("4", null, RoundingMode.DOWN), 3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_CD_UU() {
		CDecimal expected = new CDecimalBD("0.915", "USD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", "USD", RoundingMode.HALF_UP)
				.divideExact(new CDecimalBD("4", "USD", RoundingMode.DOWN), 3);
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivideExact_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("3.66", "USD", RoundingMode.HALF_UP)
				.divideExact(new CDecimalBD("4", "EUR", RoundingMode.FLOOR), 3);
	}
	
	@Test
	public void testDivideExact_CD_Period() {
		CDecimal expected = new CDecimalBD("33.3333333");
		CDecimal actual = new CDecimalBD("100")
				.divideExact(new CDecimalBD("3"), 7);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_L() {
		CDecimal expected = new CDecimalBD("0.915", "EUR", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", "EUR", RoundingMode.HALF_UP)
				.divideExact(4L, 3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_L_Periodic() {
		CDecimal expected = new CDecimalBD("33.33333");
		CDecimal actual = new CDecimalBD("100").divideExact(3L, 5);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_CD_AA() {
		CDecimal expected = new CDecimalBD("-8.14", null, RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("14.27", null, RoundingMode.FLOOR)
				.multiply(new CDecimalBD("-0.57", null, RoundingMode.CEILING));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_CD_AU() {
		CDecimal expected = new CDecimalBD("-8.13", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("14.27", null, RoundingMode.HALF_UP)
				.multiply(new CDecimalBD("-0.57", "CAD", RoundingMode.FLOOR));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_CD_UA() {
		CDecimal expected = new CDecimalBD("223.704", "EUR", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("19.5", "EUR", RoundingMode.FLOOR)
				.multiply(new CDecimalBD("11.472", null, RoundingMode.HALF_DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_CD_UU() {
		CDecimal expected = new CDecimalBD("10647.277", "EUR", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("-928.11", "EUR", RoundingMode.FLOOR)
				.multiply(new CDecimalBD("-11.472", "EUR", RoundingMode.HALF_DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiply_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("928.11", "USD").multiply(new CDecimalBD("11.472", "EUR"));
	}
	
	@Test
	public void testMultiple_L() {
		CDecimal expected = new CDecimalBD("24.290", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("2.429", "CAD", RoundingMode.HALF_UP)
				.multiply(10L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultipleAndMultipleExact_Difference() {
		CDecimal multiplier = new CDecimalBD("3.66");
		CDecimal multiplicand = new CDecimalBD("15.26");
		
		assertEquals(new CDecimalBD("55.85"), multiplier.multiply(multiplicand));
		assertEquals(new CDecimalBD("55.8516"), multiplier.multiplyExact(multiplicand));
	}
	
	@Test
	public void testMultiplyExact_CD_AA() {
		CDecimal expected = new CDecimalBD("55.8516", null, RoundingMode.HALF_DOWN);
		CDecimal actual = new CDecimalBD("3.66", null, RoundingMode.HALF_DOWN)
				.multiplyExact(new CDecimalBD("15.26", null, RoundingMode.CEILING));
		
		assertEquals(expected, actual);
	}

	@Test
	public void testMultiplyExact_CD_AU() {
		CDecimal expected = new CDecimalBD("-516.29497905", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("9.215", null, RoundingMode.HALF_UP)
				.multiplyExact(new CDecimalBD("-56.02767", "CAD", RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiplyExact_CD_UA() {
		CDecimal expected = new CDecimalBD("-516.29497905", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("9.215", "CAD", RoundingMode.HALF_UP)
				.multiplyExact(new CDecimalBD("-56.02767", null, RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiplyExact_CD_UU() {
		CDecimal expected = new CDecimalBD("-516.29497905", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("9.215", "CAD", RoundingMode.HALF_UP)
				.multiplyExact(new CDecimalBD("-56.02767", "CAD", RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiplyExact_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("9.215", "CAD", RoundingMode.HALF_UP)
				.multiplyExact(new CDecimalBD("-56.02767", "EUR", RoundingMode.DOWN));
	}
	
	@Test
	public void testSubtract_CD_AA() {
		CDecimal expected = new CDecimalBD("-313.996", null, RoundingMode.DOWN);
		CDecimal actual = new CDecimalBD("-223.1", null, RoundingMode.DOWN)
				.subtract(new CDecimalBD("90.896", null, RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSubtract_CD_AU() {
		CDecimal expected = new CDecimalBD("-313.996", "USD", RoundingMode.DOWN);
		CDecimal actual = new CDecimalBD("-223.1", null, RoundingMode.DOWN)
				.subtract(new CDecimalBD("90.896", "USD", RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSubtract_CD_UA() {
		CDecimal expected = new CDecimalBD("-313.996", "EUR", RoundingMode.DOWN);
		CDecimal actual = new CDecimalBD("-223.1", "EUR", RoundingMode.DOWN)
				.subtract(new CDecimalBD("90.896", null, RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);	}
	
	@Test
	public void testSubtract_CD_UU() {
		CDecimal expected = new CDecimalBD("-313.996", "RUB", RoundingMode.DOWN);
		CDecimal actual = new CDecimalBD("-223.1", "RUB", RoundingMode.DOWN)
				.subtract(new CDecimalBD("90.896", "RUB", RoundingMode.HALF_UP));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSubtract_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("-223.1", "RUB").subtract(new CDecimalBD("90.896", "USD"));
	}
	
	@Test
	public void testSubtract_L() {
		CDecimal expected = new CDecimalBD("410.46", "EUR", RoundingMode.CEILING);
		CDecimal actual = new CDecimalBD("450.46", "EUR", RoundingMode.CEILING)
				.subtract(40L);
		
		assertEquals(expected, actual);
	}

}
