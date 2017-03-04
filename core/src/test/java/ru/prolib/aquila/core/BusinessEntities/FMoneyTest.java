package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class FMoneyTest {

	@Before
	public void setUp() throws Exception {
		
	}

	
	@Test
	public void testCtor3_BDRMS() {
		FMoney x = new FMoney(new BigDecimal("200.05"), RoundingMode.CEILING, "USD");
		
		assertEquals(new BigDecimal("200.05"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(200.05d, x.doubleValue(), 0.001d);
		assertEquals("USD", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor2_BDS() {
		FMoney x = new FMoney(new BigDecimal("115.240"), "RUB");
		
		assertEquals(new BigDecimal("115.240"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(115.24d, x.doubleValue(), 0.001d);
		assertEquals("RUB", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor4_DIRMS() {
		FMoney x = new FMoney(0.3401d, 4, RoundingMode.HALF_DOWN, "UAH");
		
		assertEquals(new BigDecimal("0.3401"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_DOWN, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(0.3401d, x.doubleValue(), 0.00001d);
		assertEquals("UAH", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor3_DIS() {
		FMoney x = new FMoney(22.095d, 2, "EUR");
		
		assertEquals(new BigDecimal("22.10"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
		assertEquals("EUR", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor3_SRMS() {
		FMoney x = new FMoney("22.10", RoundingMode.FLOOR, "JPY");
		
		assertEquals(new BigDecimal("22.1"), x.toBigDecimal());
		assertEquals(RoundingMode.FLOOR, x.getRoundingMode());
		assertEquals(1, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
		assertEquals("JPY", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor3_SRMS_ScientificNotation() {
		FMoney x = new FMoney("1.0E-4", RoundingMode.DOWN, "CAD");
		
		assertEquals(new BigDecimal("0.0001"), x.toBigDecimal());
		assertEquals(RoundingMode.DOWN, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(0.0001d, x.doubleValue(), 0.000001d);
		assertEquals("CAD", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor3_SRMS_TrimTrailingZeroes() {
		FMoney x = new FMoney("22.050", RoundingMode.HALF_DOWN, "USD");
		
		assertEquals(new BigDecimal("22.05"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_DOWN, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.05d, x.doubleValue(), 0.001d);
		assertEquals("USD", x.getCurrencyCode());
	}

	@Test
	public void testCtor3_SRMS_NoDecimals() {
		FMoney x = new FMoney("22.000", RoundingMode.CEILING, "EUR");
		
		assertEquals(new BigDecimal("22"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(22.0d, x.doubleValue(), 0.01d);
		assertEquals("EUR", x.getCurrencyCode());
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor3_SRMS_InvalidFormat() {
		new FMoney("foo", RoundingMode.CEILING, "USD");
	}

	@Test
	public void testCtor2_SS() {
		FMoney x = new FMoney("127.092", "USD");
		
		assertEquals(new BigDecimal("127.092"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(127.092d, x.doubleValue(), 0.0001d);
		assertEquals("USD", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor2_SS_ScientificNotation() {
		FMoney x = new FMoney("-5.34E-2", "RUB");
		
		assertEquals(new BigDecimal("-0.0534"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-0.0534d, x.doubleValue(), 0.0001d);
		assertEquals("RUB", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor2_SS_TrimTrailingZeroes() {
		FMoney x = new FMoney("-27.00010", "JPY");
		
		assertEquals(new BigDecimal("-27.0001"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-27.0001d, x.doubleValue(), 0.0001d);
		assertEquals("JPY", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor2_SS_NoDecimals() {
		FMoney x = new FMoney("-27.00000", "USD");
		
		assertEquals(new BigDecimal("-27"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(-27.0d, x.doubleValue(), 0.01d);
		assertEquals("USD", x.getCurrencyCode());
	}
	
	@Test (expected=NumberFormatException.class)
	public void testCtor2_SS_InvalidFormat() {
		new FMoney("foo", "EUR");
	}

	@Test
	public void testCtor4_SIRMS() {
		FMoney x = new FMoney("25.1409", 3, RoundingMode.HALF_UP, "RUB");
		
		assertEquals(new BigDecimal("25.141"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(25.141d, x.doubleValue(), 0.001d);
		assertEquals("RUB", x.getCurrencyCode());
	}

	@Test
	public void testCtor4_SIRMS_ScientificNotation() {
		FMoney x = new FMoney("1.234E+2", 0, RoundingMode.HALF_UP, "USD");
		
		assertEquals(new BigDecimal("123"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(123.0d, x.doubleValue(), 0.01d);
		assertEquals("USD", x.getCurrencyCode());
	}
	
	@Test
	public void testCtor4_SIRMS_TrimTrailingZeroes() {
		FMoney x = new FMoney("115.0200", 3, RoundingMode.CEILING, "EUR");
		
		assertEquals(new BigDecimal("115.020"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(115.02d, x.doubleValue(), 0.001d);
		assertEquals("EUR", x.getCurrencyCode());
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor4_SIRMS_InvalidFormat() {
		new FMoney("foo", 3, RoundingMode.CEILING, "EUR");
	}
	
	@Test
	public void testCtor3_SIS() {
		FMoney x = new FMoney("115.256", 2, "USD");
		
		assertEquals(new BigDecimal("115.26"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(115.26d, x.doubleValue(), 0.001d);
		assertEquals("USD", x.getCurrencyCode());
	}

	@Test
	public void testCtor3_SIS_ScientificNotation() {
		FMoney x = new FMoney("3.15E2", 1, "JPY");
		
		assertEquals(new BigDecimal("315.0"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(1, x.getScale());
		assertEquals(315.0d, x.doubleValue(), 0.01d);
		assertEquals("JPY", x.getCurrencyCode());
	}

	@Test
	public void testCtor3_SIS_TrimTrailingZeroes() {
		FMoney x = new FMoney("-2.98900", 4, "RUB");
		
		assertEquals(new BigDecimal("-2.9890"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-2.989d, x.doubleValue(), 0.0001d);
		assertEquals("RUB", x.getCurrencyCode());
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor3_SIS_InvalidFormat() {
		new FMoney("bar", 2, "RUB");
	}
	
	@Test
	public void testCtor0() {
		FMoney x = new FMoney();
		
		assertEquals(new BigDecimal("0"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(0.0d, x.doubleValue(), 0.001d);
		assertEquals("USD", x.getCurrencyCode());		
	}
	
	@Test
	public void testToString() {
		assertEquals("24.097", new FMoney(24.097d, 3, "RUR").toString());
		assertEquals("115.05", new FMoney("115.05", "JPY").toString());
	}

	@Test
	public void testEquals_SpecialCases() {
		FMoney x = new FMoney();
		
		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals(this));
	}
	
	@Test
	public void testEquals() {
		FMoney value = new FMoney(115.08d, 2, RoundingMode.FLOOR, "CAD");
		
		Variant<Double> vVal = new Variant<Double>(115.08d, 24.19d);
		Variant<Integer> vScale = new Variant<Integer>(vVal, 2, 4);
		Variant<RoundingMode> vRm = new Variant<RoundingMode>(vScale)
				.add(RoundingMode.FLOOR)
				.add(RoundingMode.UNNECESSARY);
		Variant<String> vCur = new Variant<String>(vRm, "CAD", "RUR");
		Variant<?> iterator = vCur;
		int foundCnt = 0;
		FMoney x, found = null;
		do {
			x = new FMoney(vVal.get(), vScale.get(), vRm.get(), vCur.get());
			if ( value.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(115.08d, found.doubleValue(), 0.001d);
		assertEquals(2, found.getScale());
		assertEquals(RoundingMode.FLOOR, found.getRoundingMode());
		assertEquals("CAD", found.getCurrencyCode());
	}

	@Test
	public void testCompareTo() {
		assertEquals( 1, new FMoney("12.34", "RUR").compareTo(null));
		assertEquals( 0, new FMoney("12.34", "RUR").compareTo(new FMoney("12.34", "RUR")));
		assertEquals( 0, new FMoney("12.34", "RUR").compareTo(new FMoney("12.3400", "RUR")));
		assertEquals( 1, new FMoney("12.34", "RUR").compareTo(new FMoney("12.3399", "RUR")));
		assertEquals(-1, new FMoney("12.34", "RUR").compareTo(new FMoney("12.3401", "RUR")));
		assertEquals(-1, new FMoney("12.34", "RUR").compareTo(new FMoney("12.34", "USD")));
		assertEquals( 1, new FMoney("12.34", "RUR").compareTo(new FMoney("12.34", "CAD")));
		assertEquals( 0, new FMoney("12.34", "RUR").compareTo(new FDecimal("12.34")));
	}
	
	@Test
	public void testWithScale1() {
		FMoney expected = new FMoney("115.2260", 4, RoundingMode.HALF_DOWN, "USD");
		FMoney actual = new FMoney("115.226", RoundingMode.HALF_DOWN, "USD").withScale(4);
		assertEquals(expected, actual);
		
		expected = new FMoney("115.23", "RUB");
		actual = new FMoney("115.226", "RUB").withScale(2);
		assertEquals(expected, actual);
	}


}
