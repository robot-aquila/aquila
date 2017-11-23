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
	public void testConstants() {
		assertEquals("RUB", CDecimalBD.RUB);
		assertEquals("USD", CDecimalBD.USD);
		assertEquals("EUR", CDecimalBD.EUR);
		assertEquals("CAD", CDecimalBD.CAD);
		
		assertEquals(new CDecimalBD("0"), CDecimalBD.ZERO);
		
		assertEquals(new CDecimalBD("0.00",		CDecimalBD.RUB), CDecimalBD.ZERO_RUB2);
		assertEquals(new CDecimalBD("0.00000",	CDecimalBD.RUB), CDecimalBD.ZERO_RUB5);
		assertEquals(new CDecimalBD("0.00",		CDecimalBD.USD), CDecimalBD.ZERO_USD2);
		assertEquals(new CDecimalBD("0.00000",	CDecimalBD.USD), CDecimalBD.ZERO_USD5);
	}
	
	@Test
	public void testOf3_SSRM() {
		CDecimal expected = new CDecimalBD("12.82", "RUB", RoundingMode.DOWN);
		CDecimal actual = CDecimalBD.of("12.82", "RUB", RoundingMode.DOWN);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOf2_SS() {
		CDecimal expected = new CDecimalBD("25.115", "USD", RoundingMode.HALF_UP);
		CDecimal actual = CDecimalBD.of("25.115", "USD");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOf1_S() {
		CDecimal expected = new CDecimalBD("25.115", null, RoundingMode.HALF_UP);
		CDecimal actual = CDecimalBD.of("25.115");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOf1_L() {
		CDecimal expected = new CDecimalBD("100", null, RoundingMode.HALF_UP);
		CDecimal actual = CDecimalBD.of(100L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOfRUB2_S() {
		CDecimal expected = new CDecimalBD("100.15", "RUB");
		CDecimal actual = CDecimalBD.ofRUB2("100.15000");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOfRUB5_S() {
		CDecimal expected = new CDecimalBD("1.15851", "RUB");
		CDecimal actual = CDecimalBD.ofRUB5("1.15851000");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOfUSD2_S() {
		CDecimal expected = new CDecimalBD("6.78", "USD");
		CDecimal actual = CDecimalBD.ofUSD2("6.78000");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOfUSD5_S() {
		CDecimal expected = new CDecimalBD("2.15000", "USD");
		CDecimal actual = CDecimalBD.ofUSD5("2.15");
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor3_BD_ThrowsNPEIfValueIsNull() {
		new CDecimalBD((BigDecimal)null, "USD", RoundingMode.HALF_UP);
	}
	
	@Test (expected=NullPointerException.class)
	public void testCtor3_BD_ThrowsNPEIfRoundingModeIsNull() {
		new CDecimalBD(new BigDecimal("0.01"), "USD", null);
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
	public void testWithScale2() {
		CDecimal expected = new CDecimalBD("12.381", "CAD", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("12.380564", "CAD", RoundingMode.FLOOR)
				.withScale(3, RoundingMode.HALF_UP);
		assertEquals(expected, actual);
	}
	
	@Test (expected=ArithmeticException.class)
	public void testWithScale2_SpecialCase_ThrowsIfUnableToScale() {
		new CDecimalBD("12.380564").withScale(3, RoundingMode.UNNECESSARY);
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
	public void testAdd_CD_AA() {
		CDecimal expected = new CDecimalBD("24.956", null, RoundingMode.UNNECESSARY);
		CDecimal actual = new CDecimalBD("20.0", null, RoundingMode.UNNECESSARY)
				.add(new CDecimalBD("4.956", null, RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAdd_CD_AU_Throws() {
		new CDecimalBD("20.006").add(new CDecimalBD("100.5", "USD"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAdd_CD_UA_Throws() {
		new CDecimalBD("540.400", "RUB").add(new CDecimalBD("-0.190"));
	}
	
	@Test
	public void testAdd_CD_UU() {
		CDecimal expected = new CDecimalBD("-200.00", "EUR", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("0.00", "EUR", RoundingMode.FLOOR)
				.add(new CDecimalBD("-200", "EUR", RoundingMode.HALF_DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAdd_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("100.0", "EUR").add(new CDecimalBD("100", "RUB"));
	}
	
	@Test
	public void testAdd_L_AA() {
		CDecimal expected = new CDecimalBD("-256.345", null, RoundingMode.CEILING);
		CDecimal actual = new CDecimalBD("-260.345", null, RoundingMode.CEILING).add(4L);
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testAdd_L_UA_Throws() {
		CDecimalBD.ofRUB2("17.24").add(100L);
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
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivide_CD_AU_Throws() {
		CDecimalBD.of("-12.4505").divide(CDecimalBD.ofUSD2("0.5"));
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
		CDecimal expected = new CDecimalBD("76.000", null, RoundingMode.HALF_DOWN);
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
	public void testDivide_L_U() {
		CDecimal expected = new CDecimalBD("0.77", "RUB", RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("188.58", "RUB", RoundingMode.FLOOR)
				.divide(242L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivide_L_A() {
		CDecimal expected = CDecimalBD.of(10L);
		CDecimal actual = CDecimalBD.of(200L).divide(20L);
		
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
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivideExact_CD_AU_Throws() {
		CDecimalBD.of("3.66").divideExact(CDecimalBD.ofUSD2("4"), 3);
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
		CDecimal expected = new CDecimalBD("0.915", null, RoundingMode.HALF_UP);
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
	public void testDivideExact_CD_Periodic() {
		CDecimal expected = new CDecimalBD("33.3333333");
		CDecimal actual = new CDecimalBD("100")
				.divideExact(new CDecimalBD("3"), 7);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact3_CD_AA() {
		CDecimal expected = CDecimalBD.of("15.218", null, RoundingMode.HALF_EVEN);
		CDecimal actual = CDecimalBD.of("61.17636", null, RoundingMode.HALF_EVEN)
				.divideExact(CDecimalBD.of("4.02"), 3, RoundingMode.UNNECESSARY);
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivideExact3_CD_AU_Throws() {
		CDecimalBD.of("115.08").divideExact(CDecimalBD.ofUSD2("89"), 10, RoundingMode.HALF_UP);
	}
	
	@Test
	public void testDivideExact3_CD_Periodic() {
		CDecimal expected = CDecimalBD.of("42.66666", null, RoundingMode.HALF_DOWN);
		CDecimal actual = new CDecimalBD("128", null, RoundingMode.HALF_DOWN)
				.divideExact(CDecimalBD.of(3L), 5, RoundingMode.FLOOR);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact3_CD_UA() {
		CDecimal expected = CDecimalBD.of("-1.05087", "RUB", RoundingMode.HALF_UP);
		CDecimal actual = CDecimalBD.ofRUB5("8.407")
				.divideExact(CDecimalBD.of("-8"), 5, RoundingMode.HALF_DOWN);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact3_CD_UU() {
		CDecimal expected = CDecimalBD.ofRUB5("-1.05088");
		CDecimal actual = CDecimalBD.ofRUB5("8.407")
				.divideExact(CDecimalBD.of("-8"), 5, RoundingMode.HALF_UP);
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivideExact3_CD_UU_ThrowsIfUnitMismatch() {
		CDecimalBD.ofRUB5("8.407").divideExact(CDecimalBD.ofUSD2("-8"), 5, RoundingMode.HALF_DOWN);
	}
	
	@Test (expected=ArithmeticException.class)
	public void testDivideExact3_CD_ThrowsRoundingError() {
		CDecimalBD.of("100.01").divideExact(CDecimalBD.of(10L), 2, RoundingMode.UNNECESSARY);
	}
	
	@Test
	public void testDivideExact_L_A() {
		CDecimal expected = new CDecimalBD("0.9150", "EUR", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("3.66", "EUR", RoundingMode.HALF_UP)
				.divideExact(4L, 4);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_L_Periodic() {
		CDecimal expected = new CDecimalBD("33.33333");
		CDecimal actual = new CDecimalBD("100").divideExact(3L, 5);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDivideExact_L_U() {
		CDecimal expected = CDecimalBD.ofRUB5("25");
		CDecimal actual = CDecimalBD.ofRUB2("100").divideExact(4L, 5);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_CD_AA() {
		CDecimal expected = new CDecimalBD("-8.14", null, RoundingMode.FLOOR);
		CDecimal actual = new CDecimalBD("14.27", null, RoundingMode.FLOOR)
				.multiply(new CDecimalBD("-0.57", null, RoundingMode.CEILING));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiply_CD_AU_Throws() {
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
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiply_CD_UU_Throws() {
		CDecimalBD.ofUSD2("-928.11").multiply(CDecimalBD.ofRUB2("-11.47"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiply_CD_UU_ThrowsIfUnitMismatch() {
		new CDecimalBD("928.11", "USD").multiply(new CDecimalBD("11.472", "EUR"));
	}
	
	@Test
	public void testMultiple_L_U() {
		CDecimal expected = new CDecimalBD("24.290", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("2.429", "CAD", RoundingMode.HALF_UP)
				.multiply(10L);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultiply_L_A() {
		CDecimal expected = CDecimalBD.of(400L);
		CDecimal actual = CDecimalBD.of(100L).multiply(4L);
		
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

	@Test (expected=IllegalArgumentException.class)
	public void testMultiplyExact_CD_AU_Throws() {
		CDecimalBD.of("9.215").multiplyExact(CDecimalBD.ofUSD5("-56.02767"));
	}
	
	@Test
	public void testMultiplyExact_CD_UA() {
		CDecimal expected = new CDecimalBD("-516.29497905", "CAD", RoundingMode.HALF_UP);
		CDecimal actual = new CDecimalBD("9.215", "CAD", RoundingMode.HALF_UP)
				.multiplyExact(new CDecimalBD("-56.02767", null, RoundingMode.DOWN));
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiplyExact_CD_UU_Throws() {
		CDecimalBD.ofUSD5("9.215").multiplyExact(CDecimalBD.ofUSD5("-56.02767"));
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
	
	@Test (expected=IllegalArgumentException.class)
	public void testSubtract_CD_AU_Throws() {
		CDecimalBD.of("-223.1").subtract(CDecimalBD.ofUSD5("90.896"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSubtract_CD_UA_Throws() {
		CDecimalBD.ofRUB2("-223.1").subtract(CDecimalBD.of("90.896"));
	}
	
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
	public void testMax() {
		CDecimal x1 = new CDecimalBD("410.46"),
				x2 = new CDecimalBD("410.45");
		
		assertSame(x1, x1.max(x2));
		
		x1 = new CDecimalBD("410.46", "USD");
		x2 = new CDecimalBD("410.45", "EUR");
		
		assertSame(x1, x1.max(x2));
		
		x1 = new CDecimalBD("410.46", "USD");
		x2 = new CDecimalBD("410.46", "USD");
		
		assertSame(x1, x1.max(x2));
		
		x1 = new CDecimalBD("410.46", "EUR");
		x2 = new CDecimalBD("410.46", "USD");
		
		assertSame(x2, x1.max(x2));
	}
	
	@Test
	public void testMin() {
		CDecimal x1 = new CDecimalBD("410.46"),
				x2 = new CDecimalBD("410.45");
		
		assertSame(x2, x1.min(x2));
		
		x1 = new CDecimalBD("410.46", "USD");
		x2 = new CDecimalBD("410.45", "EUR");
		
		assertSame(x2, x1.min(x2));
		
		x1 = new CDecimalBD("410.46", "USD");
		x2 = new CDecimalBD("410.46", "USD");
		
		assertSame(x1, x1.min(x2));
		
		x1 = new CDecimalBD("410.46", "EUR");
		x2 = new CDecimalBD("410.46", "USD");
		
		assertSame(x1, x1.min(x2));
	}
	
	@Test
	public void testToAbstract() {
		assertEquals(CDecimalBD.of("24.92"), CDecimalBD.ofRUB2("24.92").toAbstract());
		assertEquals(CDecimalBD.of("24.92"), CDecimalBD.of("24.92").toAbstract());
	}
	
	@Test
	public void testIsAbstract() {
		assertTrue(CDecimalBD.of(12L).isAbstract());
		assertFalse(CDecimalBD.ofRUB2("15").isAbstract());
	}
	
	@Test
	public void testIsSameUnitAs() {
		CDecimal x1 = CDecimalBD.ofRUB2("26.19"),
				x2 = CDecimalBD.ofRUB5("24.99512"),
				x3 = CDecimalBD.ofUSD2("100"),
				x4 = CDecimalBD.of(200L),
				x5 = CDecimalBD.of(300L);
		assertTrue(x1.isSameUnitAs(x2));
		assertFalse(x1.isSameUnitAs(x3));
		assertTrue(x4.isSameUnitAs(x5));
		assertFalse(x5.isSameUnitAs(x3));
	}
	
	@Test
	public void testWhenNull() {
		CDecimal x1 = CDecimalBD.ofRUB2("100"),
				x2 = CDecimalBD.ofUSD2("200");
		assertSame(x1, x1.whenNull(null));
		assertSame(x2, x1.whenNull(x2));
	}
	
	@Test
	public void testSqrt_I() {
		assertEquals(CDecimalBD.of(2L), CDecimalBD.of(4L).sqrt(0));
		assertEquals(CDecimalBD.of("2.00"), CDecimalBD.of(4L).sqrt(2));
		assertEquals(CDecimalBD.of("269.577237"), CDecimalBD.of("72671.886655").sqrt(6));
		assertEquals(CDecimalBD.of("269.57723690"), CDecimalBD.of("72671.886655").sqrt(8));
		assertEquals(CDecimalBD.of("2.0000", "USD", RoundingMode.CEILING),
				CDecimalBD.of("4", "USD", RoundingMode.CEILING).sqrt(4));
	}
	
	@Test
	public void testPow_I() {
		assertEquals(CDecimalBD.of(4L), CDecimalBD.of(2L).pow(2));
		assertEquals(CDecimalBD.of("10000000000000", "RUB", RoundingMode.UNNECESSARY),
				CDecimalBD.of("10", "RUB", RoundingMode.UNNECESSARY).pow(13));
	}

}
