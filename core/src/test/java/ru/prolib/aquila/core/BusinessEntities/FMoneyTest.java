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
	public void testConstants() {
		assertEquals(1, FMoney.VERSION);
		assertEquals("USD", FMoney.USD);
		assertEquals("EUR", FMoney.EUR);
		assertEquals("RUB", FMoney.RUB);
		assertEquals("JPY", FMoney.JPY);
		assertEquals("CAD", FMoney.CAD);
		assertEquals(FMoney.of(0, 0, FMoney.USD), FMoney.ZERO_USD0);
		assertEquals(FMoney.of(0, 1, FMoney.USD), FMoney.ZERO_USD1);
		assertEquals(FMoney.of(0, 2, FMoney.USD), FMoney.ZERO_USD2);
		assertEquals(FMoney.of(0, 3, FMoney.USD), FMoney.ZERO_USD3);
		assertEquals(FMoney.of(0, 4, FMoney.USD), FMoney.ZERO_USD4);
		assertEquals(FMoney.of(0, 5, FMoney.USD), FMoney.ZERO_USD5);
		assertEquals(FMoney.of(0, 0, FMoney.EUR), FMoney.ZERO_EUR0);
		assertEquals(FMoney.of(0, 1, FMoney.EUR), FMoney.ZERO_EUR1);
		assertEquals(FMoney.of(0, 2, FMoney.EUR), FMoney.ZERO_EUR2);
		assertEquals(FMoney.of(0, 3, FMoney.EUR), FMoney.ZERO_EUR3);
		assertEquals(FMoney.of(0, 4, FMoney.EUR), FMoney.ZERO_EUR4);
		assertEquals(FMoney.of(0, 5, FMoney.EUR), FMoney.ZERO_EUR5);
		assertEquals(FMoney.of(0, 0, FMoney.RUB), FMoney.ZERO_RUB0);
		assertEquals(FMoney.of(0, 1, FMoney.RUB), FMoney.ZERO_RUB1);
		assertEquals(FMoney.of(0, 2, FMoney.RUB), FMoney.ZERO_RUB2);
		assertEquals(FMoney.of(0, 3, FMoney.RUB), FMoney.ZERO_RUB3);
		assertEquals(FMoney.of(0, 4, FMoney.RUB), FMoney.ZERO_RUB4);
		assertEquals(FMoney.of(0, 5, FMoney.RUB), FMoney.ZERO_RUB5);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf_1_S_Throws() {
		FMoney.of("23.15");
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf_2_SI_Throws() {
		FMoney.of("23.15", 3);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf_2_DI_Throws() {
		FMoney.of(23.15, 3);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf_3_SIRM_Throws() {
		FMoney.of("23.15", 3, RoundingMode.DOWN);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf_1_BD_Throws() {
		FMoney.of(new BigDecimal("23.15"));
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf_2_BDI_Throws() {
		FMoney.of(new BigDecimal("23.15"), 3);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf_3_BDIRM_Throws() {
		FMoney.of(new BigDecimal("23.15"), 3, RoundingMode.CEILING);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf0_1_S_Throws() {
		FMoney.of0("23.15");
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf1_1_S_Throws() {
		FMoney.of1("23.15");
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf2_1_S_Throws() {
		FMoney.of2("23.15");
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf3_1_S_Throws() {
		FMoney.of3("23.15");
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf4_1_S_Throws() {
		FMoney.of4("23.15");
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf0_1_D_Throws() {
		FMoney.of0(23.15);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf1_1_D_Throws() {
		FMoney.of1(23.15);
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testOf2_1_D_Throws() {
		FMoney.of2(23.15);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf3_1_D_Throws() {
		FMoney.of3(23.15);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testOf4_1_D_Throws() {
		FMoney.of4(23.15);
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
	public void testToStringWithCurrency() {
		assertEquals("24.097 RUR", new FMoney("24.097", "RUR").toStringWithCurrency());
		assertEquals("115.05 JPY", new FMoney("115.05", "JPY").toStringWithCurrency());
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

	@Test (expected=IllegalArgumentException.class)
	public void testSubtract1_Money_ThrowsIfCurrencyMismatch() {
		new FMoney("115.24", "USD").subtract(new FMoney("10.25", "RUB"));
	}
	
	@Test
	public void testSubtract1_Money() {
		assertEquals(new FMoney("115.25", RoundingMode.CEILING, "USD"),
			new FMoney("120.00", 2, RoundingMode.CEILING, "USD")
				.subtract(new FMoney("4.75", "USD")));
		
		assertEquals(new FMoney("12.425", 3, RoundingMode.HALF_UP, "RUB"),
				new FMoney("97.925", "RUB")
					.subtract(new FMoney("85.5", "RUB")));
			
		assertEquals(new FMoney("-12.425", 3, RoundingMode.HALF_DOWN, "CAD"),
			new FMoney("-230.1", 1, RoundingMode.HALF_DOWN, "CAD")
				.subtract(new FMoney("-217.675", 3, RoundingMode.FLOOR, "CAD")));
	}
	
	@Test
	public void testSubtract1_Decimal() {
		assertEquals(new FMoney("115.25", RoundingMode.CEILING, "USD"),
			new FMoney("120.00", 2, RoundingMode.CEILING, "USD")
				.subtract(new FDecimal("4.75")));
			
		assertEquals(new FMoney("12.425", 3, RoundingMode.HALF_UP, "RUB"),
			new FMoney("97.925", "RUB")
				.subtract(new FDecimal("85.5")));
		
		assertEquals(new FMoney("-12.425", 3, RoundingMode.HALF_DOWN, "CAD"),
			new FMoney("-230.1", 1, RoundingMode.HALF_DOWN, "CAD")
				.subtract(new FDecimal("-217.675", 3, RoundingMode.FLOOR)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAdd1_Money_ThrowsIfCurrencyMismatch() {
		new FMoney("12.05", "EUR").add(new FMoney("86.24", "RUB"));
	}
	
	@Test
	public void testAdd1_Money() {
		assertEquals(new FMoney("265.948", RoundingMode.DOWN, "EUR"),
			new FMoney("-262", RoundingMode.DOWN, "EUR")
				.add(new FMoney("527.948", "EUR")));
		
		assertEquals(new FMoney("97.925", RoundingMode.HALF_UP, "JPY"),
			new FMoney("12.425", "JPY")
				.add(new FMoney("85.5", "JPY")));
		
		assertEquals(new FMoney("-230.100", 3, RoundingMode.HALF_DOWN, "RUB"),
			new FMoney("-217.675", RoundingMode.HALF_DOWN, "RUB")
				.add(new FMoney("-12.425", 3, RoundingMode.FLOOR, "RUB")));
	}
	
	@Test
	public void testAdd1_Decimal() {
		assertEquals(new FMoney("265.948", RoundingMode.DOWN, "EUR"),
			new FMoney("-262", RoundingMode.DOWN, "EUR")
				.add(new FDecimal("527.948")));
		
		assertEquals(new FMoney("97.925", RoundingMode.HALF_UP, "JPY"),
			new FMoney("12.425", "JPY")
				.add(new FDecimal("85.5")));
		
		assertEquals(new FMoney("-230.100", 3, RoundingMode.HALF_DOWN, "RUB"),
			new FMoney("-217.675", RoundingMode.HALF_DOWN, "RUB")
				.add(new FDecimal("-12.425", 3, RoundingMode.FLOOR)));
	}
	
	@Test
	public void testOf_2_SS() {
		assertEquals(new FMoney("586.15", 2, RoundingMode.HALF_UP, "JPY"),
				FMoney.of("586.15", "JPY"));
	}

	@Test
	public void testOf_3_SIS() {
		assertEquals(new FMoney("586.150", 3, RoundingMode.HALF_UP, "CAD"),
				FMoney.of("586.15", 3, "CAD"));
	}
	
	@Test
	public void testOf_3_DIS() {
		assertEquals(new FMoney("586.150", 3, RoundingMode.HALF_UP, "USD"),
				FMoney.of(586.150452, 3, "USD"));
	}
	
	@Test
	public void testOf_2_BDS() {
		assertEquals(new FMoney("12.26", 2, RoundingMode.HALF_UP, "RUR"),
				FMoney.of(new BigDecimal("12.26"), "RUR"));
	}
	
	@Test
	public void testOf_3_BDIS() {
		assertEquals(new FMoney("-0.192", 3, RoundingMode.HALF_UP, "CAD"),
				FMoney.of(new BigDecimal("-0.19226"), 3, "CAD"));
	}
	
	@Test
	public void testOfUSD_2_SI() {
		assertEquals(new FMoney("12.50000", 5, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD("12.5", 5));
	}
	
	@Test
	public void testOfUSD_2_DI() {
		assertEquals(new FMoney("12.50000", 5, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD(12.5, 5));
	}
	
	@Test
	public void testOfUSD1_1_S() {
		assertEquals(new FMoney("12.5", 1, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD1("12.5051"));
	}
	
	@Test
	public void testOfUSD2_1_S() {
		assertEquals(new FMoney("12.51", 2, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD2("12.5051"));
	}

	@Test
	public void testOfUSD3_1_S() {
		assertEquals(new FMoney("12.505", 3, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD3("12.505126"));
	}

	@Test
	public void testOfUSD4_1_S() {
		assertEquals(new FMoney("12.5051", 4, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD4("12.505126"));
	}

	@Test
	public void testOfUSD5_1_S() {
		assertEquals(new FMoney("12.50513", 5, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD5("12.505126"));
	}

	@Test
	public void testOfUSD1_1_D() {
		assertEquals(new FMoney("12.5", 1, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD1(12.5051));
	}
	
	@Test
	public void testOfUSD2_1_D() {
		assertEquals(new FMoney("12.51", 2, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD2(12.5051));
	}

	@Test
	public void testOfUSD3_1_D() {
		assertEquals(new FMoney("12.505", 3, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD3(12.505126));
	}

	@Test
	public void testOfUSD4_1_D() {
		assertEquals(new FMoney("12.5051", 4, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD4(12.505126));
	}

	@Test
	public void testOfUSD5_1_D() {
		assertEquals(new FMoney("12.50513", 5, RoundingMode.HALF_UP, "USD"),
				FMoney.ofUSD5(12.505126));
	}

	@Test
	public void testOfEUR_2_SI() {
		assertEquals(new FMoney("5.291", 3, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR("5.29062", 3));
	}
	
	@Test
	public void testOfEUR_2_DI() {
		assertEquals(new FMoney("5.291", 3, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR(5.29062, 3));
	}
	
	@Test
	public void testOfEUR1_1_S() {
		assertEquals(new FMoney("5.3", 1, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR1("5.29062"));
	}

	@Test
	public void testOfEUR2_1_S() {
		assertEquals(new FMoney("5.29", 2, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR2("5.29062"));
	}

	@Test
	public void testOfEUR3_1_S() {
		assertEquals(new FMoney("5.291", 3, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR3("5.29062"));
	}

	@Test
	public void testOfEUR4_1_S() {
		assertEquals(new FMoney("5.2906", 4, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR4("5.29062"));
	}
	
	@Test
	public void testOfEUR5_1_S() {
		assertEquals(new FMoney("5.29062", 5, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR5("5.2906215"));
	}

	@Test
	public void testOfEUR1_1_D() {
		assertEquals(new FMoney("5.3", 1, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR1(5.29062));
	}

	@Test
	public void testOfEUR2_1_D() {
		assertEquals(new FMoney("5.29", 2, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR2(5.29062));
	}

	@Test
	public void testOfEUR3_1_D() {
		assertEquals(new FMoney("5.291", 3, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR3(5.29062));
	}

	@Test
	public void testOfEUR4_1_D() {
		assertEquals(new FMoney("5.2906", 4, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR4(5.29062));
	}

	@Test
	public void testOfEUR5_1_D() {
		assertEquals(new FMoney("5.29062", 5, RoundingMode.HALF_UP, "EUR"),
				FMoney.ofEUR5(5.2906215));
	}

	@Test
	public void testOfRUB_2_SI() {
		assertEquals(new FMoney("-5.0340", 4, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB("-5.034", 4));
	}

	@Test
	public void testOfRUB_2_DI() {
		assertEquals(new FMoney("-5.0340", 4, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB(-5.034, 4));
	}

	@Test
	public void testOfRUB1_1_S() {
		assertEquals(new FMoney("-5.0", 1, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB1("-5.034"));
	}

	@Test
	public void testOfRUB2_1_S() {
		assertEquals(new FMoney("-5.03", 2, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB2("-5.034"));
	}

	@Test
	public void testOfRUB3_1_S() {
		assertEquals(new FMoney("-5.034", 3, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB3("-5.034"));
	}

	@Test
	public void testOfRUB4_1_S() {
		assertEquals(new FMoney("-5.0340", 4, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB4("-5.034"));
	}

	@Test
	public void testOfRUB5_1_S() {
		assertEquals(new FMoney("-5.03401", 5, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB5("-5.034011"));
	}

	@Test
	public void testOfRUB1_1_D() {
		assertEquals(new FMoney("-5.0", 1, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB1(-5.034));
	}

	@Test
	public void testOfRUB2_1_D() {
		assertEquals(new FMoney("-5.03", 2, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB2(-5.034));
	}

	@Test
	public void testOfRUB3_1_D() {
		assertEquals(new FMoney("-5.034", 3, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB3(-5.034));
	}

	@Test
	public void testOfRUB4_1_D() {
		assertEquals(new FMoney("-5.0340", 4, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB4(-5.034));
	}

	@Test
	public void testOfRUB5_1_D() {
		assertEquals(new FMoney("-5.03400", 5, RoundingMode.HALF_UP, "RUB"),
				FMoney.ofRUB5(-5.034001));
	}

	@Test
	public void testWithZero() {
		assertEquals(new FMoney("0.0000", 4, RoundingMode.DOWN, FMoney.RUB),
				new FMoney("172.9012", RoundingMode.DOWN, FMoney.RUB).withZero());
	}
	
	@Test
	public void testMultiply1_FDecimal() {
		assertEquals(new FMoney(1164.216, 3, RoundingMode.HALF_UP, FMoney.EUR),
				FMoney.ofEUR3(76.292).multiply(FDecimal.of2(15.26)));
		
		assertEquals(new FMoney(6.79, 2, RoundingMode.CEILING, FMoney.RUB),
				new FMoney(26.1, 1, RoundingMode.CEILING, FMoney.RUB)
					.multiply(FDecimal.of2(0.26)));
	}
	
	@Test
	public void testMultiplyExact_FDecimal() {
		assertEquals(new FMoney(1164.21592, 5, RoundingMode.HALF_UP, FMoney.USD),
				FMoney.ofUSD3(76.292).multiplyExact(FDecimal.of2(15.26)));
		
		assertEquals(new FMoney(6.786, 3, RoundingMode.HALF_DOWN, FMoney.EUR),
				new FMoney(26.1, 1, RoundingMode.HALF_DOWN, FMoney.EUR)
					.multiplyExact(FDecimal.of2(0.26)));
	}
	
	@Test
	public void testMultiply1_FMoney() {
		assertEquals(new FMoney(1164.216, 3, RoundingMode.HALF_UP, FMoney.EUR),
				FMoney.ofEUR3(76.292).multiply(FMoney.ofEUR2(15.26)));
		
		assertEquals(new FMoney(6.79, 2, RoundingMode.CEILING, FMoney.RUB),
				new FMoney(26.1, 1, RoundingMode.CEILING, FMoney.RUB)
					.multiply(FMoney.ofRUB2(0.26)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiply1_FMoney_ThrowsIfCurrencyMismatch() {
		FMoney.ofUSD2(23.15).multiply(FMoney.ofRUB2(1.0));
	}
	
	@Test
	public void testMultiplyExact_FMoney() {
		assertEquals(new FMoney(1164.21592, 5, RoundingMode.HALF_UP, FMoney.USD),
				FMoney.ofUSD3(76.292).multiplyExact(FMoney.ofUSD2(15.26)));
		
		assertEquals(new FMoney(6.786, 3, RoundingMode.HALF_DOWN, FMoney.EUR),
				new FMoney(26.1, 1, RoundingMode.HALF_DOWN, FMoney.EUR)
					.multiplyExact(FMoney.ofEUR2(0.26)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMultiplyExact_FMoney_ThrowsIfCurrencyMismatch() {
		FMoney.ofUSD2(23.15).multiplyExact(FMoney.ofRUB2(1.0));
	}
	
	@Test
	public void testMultiply1_Long() {
		assertEquals(new FMoney(4194.16, 3, RoundingMode.HALF_UP, FMoney.RUB),
				FMoney.ofRUB3(524.27).multiply(8L));
		
		assertEquals(new FMoney(25.0, 0, RoundingMode.UNNECESSARY, FMoney.CAD),
				new FMoney(5.0, 0, RoundingMode.UNNECESSARY, FMoney.CAD).multiply(5L));
	}
	
	@Test
	public void testNegate() {
		assertEquals(new FMoney(0.0, 5, RoundingMode.HALF_EVEN, FMoney.USD),
				new FMoney(0.0, 5, RoundingMode.HALF_EVEN, FMoney.USD).negate());
		
		assertEquals(new FMoney(-12.576, 3, RoundingMode.CEILING, FMoney.EUR),
				new FMoney(12.576, 3, RoundingMode.CEILING, FMoney.EUR).negate());
		
		assertEquals(new FMoney(35.24, 2, RoundingMode.HALF_UP, FMoney.RUB),
				new FMoney(-35.24, 2, FMoney.RUB).negate());
	}
	
	@Test
	public void testDivide1_FDecimal() {
		assertEquals(new FMoney(2.019, 3, RoundingMode.HALF_UP, FMoney.USD),
				FMoney.ofUSD3(428.256).divide(FDecimal.of2(212.1)));
		
		assertEquals(new FMoney(9.766, 3, RoundingMode.FLOOR, FMoney.RUB),
				new FMoney(12.56, 2, RoundingMode.FLOOR, FMoney.RUB)
					.divide(FDecimal.of3(1.286)));
	}
	
	@Test
	public void testDivide1_Long() {
		assertEquals(new FMoney(2.020, 3, RoundingMode.HALF_UP, FMoney.EUR),
				FMoney.ofEUR3(428.256).divide(212L));

		assertEquals(new FMoney(-11.25, 2, RoundingMode.CEILING, FMoney.USD),
				new FMoney(-56.28, 2, RoundingMode.CEILING, FMoney.USD)
					.divide(5L));
	}
	
	@Test
	public void testDivide1_FMoney() {
		assertEquals(new FMoney(2.019, 3, RoundingMode.HALF_UP, FMoney.USD),
				FMoney.ofUSD3(428.256).divide(FMoney.ofUSD2(212.1)));
		
		assertEquals(new FMoney(9.766, 3, RoundingMode.FLOOR, FMoney.RUB),
				new FMoney(12.56, 2, RoundingMode.FLOOR, FMoney.RUB)
					.divide(FMoney.ofRUB3(1.286)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivide1_FMoney_ThrowsIfCurrencyMismatch() {
		FMoney.ofUSD2(15.0).divide(FMoney.ofRUB2(5.0));
	}
	
	@Test
	public void testDivide3_FDecimal() {
		assertEquals(new FMoney(9, 0, RoundingMode.HALF_DOWN, FMoney.RUB),
				new FMoney(67.5, 1, RoundingMode.HALF_DOWN, FMoney.RUB)
					.divide(FDecimal.of1(7.5), 0, RoundingMode.UNNECESSARY));
	}
	
	@Test (expected=ArithmeticException.class)
	public void testDivide3_FDecimal_ThrowsIfScaleIsInsufficient() {
		new FMoney(67.6, 1, FMoney.JPY)
			.divide(FDecimal.of1(7.5), 0, RoundingMode.UNNECESSARY);
	}
		
	@Test
	public void testDivide3_Long() {
		assertEquals(new FMoney(24.2, 1, RoundingMode.HALF_DOWN, FMoney.CAD),
				new FMoney(169.4, 1, RoundingMode.HALF_DOWN, FMoney.CAD)
					.divide(7L, 1, RoundingMode.UNNECESSARY));
	}
	
	@Test (expected=ArithmeticException.class)
	public void testDivide3Long_ThrowsIfScaleIsInsufficient() {
		new FMoney(169.4, 1, RoundingMode.HALF_DOWN, FMoney.RUB)
			.divide(7L, 0, RoundingMode.UNNECESSARY);
	}

	@Test
	public void testDivide3_FMoney() {
		assertEquals(new FMoney(9, 0, RoundingMode.HALF_DOWN, FMoney.RUB),
				new FMoney(67.5, 1, RoundingMode.HALF_DOWN, FMoney.RUB)
					.divide(FMoney.ofRUB1(7.5), 0, RoundingMode.UNNECESSARY));
	}
	
	@Test (expected=ArithmeticException.class)
	public void testDivide3_FMoney_ThrowsIfScaleIsInsufficient() {
		new FMoney(169.4, 1, RoundingMode.HALF_DOWN, FMoney.RUB)
			.divide(FMoney.ofRUB2(7.0), 0, RoundingMode.UNNECESSARY);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDivide3_FMoney_ThrowsIfCurrencyMismatch() {
		assertEquals(new FMoney(9, 0, RoundingMode.HALF_DOWN, FMoney.RUB),
				new FMoney(67.5, 1, RoundingMode.HALF_DOWN, FMoney.RUB)
					.divide(FMoney.ofUSD1(7.5), 0, RoundingMode.UNNECESSARY));
	}
	
	@Test
	public void testAbs() {
		assertEquals(new FMoney(0.0, 4, RoundingMode.FLOOR, FMoney.EUR),
				new FMoney(0.0, 4, RoundingMode.FLOOR, FMoney.EUR).abs());
		
		assertEquals(new FMoney(34.1564, 4, RoundingMode.CEILING, FMoney.RUB),
				new FMoney(34.1564, 4, RoundingMode.CEILING, FMoney.RUB).abs());

		assertEquals(new FMoney(34.1564, 4, RoundingMode.CEILING, FMoney.USD),
				new FMoney(-34.1564, 4, RoundingMode.CEILING, FMoney.USD).abs());
	}
	
}
