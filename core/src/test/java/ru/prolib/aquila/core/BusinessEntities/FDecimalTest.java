package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class FDecimalTest {

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testToBigDecimal2_DI() {
		assertEquals(new BigDecimal("12.05"), FDecimal.toBigDecimal(12.049d, 2));
		assertEquals(new BigDecimal("12.04"), FDecimal.toBigDecimal(12.044d, 2));
		assertEquals(new BigDecimal("-12.1"), FDecimal.toBigDecimal(-12.09d, 1));
	}
	
	@Test (expected=NumberFormatException.class)
	public void testToBigDecimal1_S_InvalidFormat() {
		FDecimal.toBigDecimal("foo");
	}

	@Test
	public void testToBigDecimal_S_ScientificNotation() {
		assertEquals(new BigDecimal("0.0001"), FDecimal.toBigDecimal("1.0E-4"));
		assertEquals(new BigDecimal("100"), FDecimal.toBigDecimal("1E2"));
		assertEquals(new BigDecimal("1234"), FDecimal.toBigDecimal("1.234E+3"));
		assertEquals(new BigDecimal("-0.0001"), FDecimal.toBigDecimal("-1.0E-4"));
		assertEquals(new BigDecimal("-100"), FDecimal.toBigDecimal("-1E2"));
		assertEquals(new BigDecimal("-1234"), FDecimal.toBigDecimal("-1.234E+3"));
	}

	@Test
	public void testToBigDecimal_S_TrimTrailingZeroes() {
		assertEquals(new BigDecimal("0.0001"), FDecimal.toBigDecimal("0.00010"));
		assertEquals(new BigDecimal("10"), FDecimal.toBigDecimal("10.0"));
		assertEquals(new BigDecimal("100"), FDecimal.toBigDecimal("100.000"));
		assertEquals(new BigDecimal("-0.0001"), FDecimal.toBigDecimal("-0.00010"));
		assertEquals(new BigDecimal("-10"), FDecimal.toBigDecimal("-10.0"));
		assertEquals(new BigDecimal("-100"), FDecimal.toBigDecimal("-100.000"));
	}
	
	@Test (expected=NumberFormatException.class)
	public void testToBigDecimal_SIRM_InvalidFormat() {
		FDecimal.toBigDecimal("foo", 2, RoundingMode.HALF_DOWN);
	}
	
	@Test
	public void testToBigDecimal_SIRM_ScientificNotation() {
		assertEquals(new BigDecimal("123"), FDecimal.toBigDecimal("1.234E+2", 0, RoundingMode.HALF_UP));
		assertEquals(new BigDecimal("124"), FDecimal.toBigDecimal("1.235E2", 0, RoundingMode.HALF_UP));
		assertEquals(new BigDecimal("-100.00"), FDecimal.toBigDecimal("-1E2", 2, RoundingMode.HALF_DOWN));
	}

	@Test
	public void testCtor2_BDRM() {
		FDecimal x = new FDecimal(new BigDecimal("200.05"), RoundingMode.CEILING);
		
		assertEquals(new BigDecimal("200.05"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(200.05d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor1_BD() {
		FDecimal x = new FDecimal(new BigDecimal("115.240"));
		
		assertEquals(new BigDecimal("115.240"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(115.24d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor3_DIRM() {
		FDecimal x = new FDecimal(0.3401d, 4, RoundingMode.HALF_DOWN);
		
		assertEquals(new BigDecimal("0.3401"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_DOWN, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(0.3401d, x.doubleValue(), 0.00001d);
	}
	
	@Test
	public void testCtor2_DI() {
		FDecimal x = new FDecimal(22.095d, 2);
		
		assertEquals(new BigDecimal("22.10"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor2_SRM() {
		FDecimal x = new FDecimal("22.10", RoundingMode.FLOOR);
		
		assertEquals(new BigDecimal("22.1"), x.toBigDecimal());
		assertEquals(RoundingMode.FLOOR, x.getRoundingMode());
		assertEquals(1, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor2_SRM_ScientificNotation() {
		FDecimal x = new FDecimal("1.0E-4", RoundingMode.DOWN);
		
		assertEquals(new BigDecimal("0.0001"), x.toBigDecimal());
		assertEquals(RoundingMode.DOWN, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(0.0001d, x.doubleValue(), 0.000001d);
	}
	
	@Test
	public void testCtor2_SRM_TrimTrailingZeroes() {
		FDecimal x = new FDecimal("22.050", RoundingMode.HALF_DOWN);
		
		assertEquals(new BigDecimal("22.05"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_DOWN, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.05d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor2_SRM_NoDecimals() {
		FDecimal x = new FDecimal("22.000", RoundingMode.CEILING);
		
		assertEquals(new BigDecimal("22"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(22.0d, x.doubleValue(), 0.01d);
	}
	
	@Test (expected=NumberFormatException.class)
	public void testCtor2_SRM_InvalidFormat() {
		new FDecimal("foo", RoundingMode.CEILING);
	}

	@Test
	public void testCtor1_S() {
		FDecimal x = new FDecimal("127.092");
		
		assertEquals(new BigDecimal("127.092"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(127.092d, x.doubleValue(), 0.0001d);
	}
	
	@Test
	public void testCtor1_S_ScientificNotation() {
		FDecimal x = new FDecimal("-5.34E-2");
		
		assertEquals(new BigDecimal("-0.0534"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-0.0534d, x.doubleValue(), 0.0001d);
	}
	
	@Test
	public void testCtor1_S_TrimTrailingZeroes() {
		FDecimal x = new FDecimal("-27.00010");
		
		assertEquals(new BigDecimal("-27.0001"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-27.0001d, x.doubleValue(), 0.0001d);
	}
	
	@Test
	public void testCtor1_S_NoDecimals() {
		FDecimal x = new FDecimal("-27.00000");
		
		assertEquals(new BigDecimal("-27"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(-27.0d, x.doubleValue(), 0.01d);
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor1_S_InvalidFormat() {
		new FDecimal("foo");
	}

	@Test
	public void testCtor3_SIRM() {
		FDecimal x = new FDecimal("25.1409", 3, RoundingMode.HALF_UP);
		
		assertEquals(new BigDecimal("25.141"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(25.141d, x.doubleValue(), 0.001d);
	}

	@Test
	public void testCtor3_SIRM_ScientificNotation() {
		FDecimal x = new FDecimal("1.234E+2", 0, RoundingMode.HALF_UP);
		
		assertEquals(new BigDecimal("123"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(123.0d, x.doubleValue(), 0.01d);
	}
	
	@Test
	public void testCtor3_SIRM_TrimTrailingZeroes() {
		FDecimal x = new FDecimal("115.0200", 3, RoundingMode.CEILING);
		
		assertEquals(new BigDecimal("115.020"), x.toBigDecimal());
		assertEquals(RoundingMode.CEILING, x.getRoundingMode());
		assertEquals(3, x.getScale());
		assertEquals(115.02d, x.doubleValue(), 0.001d);
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor3_SIRM_InvalidFormat() {
		new FDecimal("foo", 3, RoundingMode.CEILING);
	}

	@Test
	public void testCtor2_SI() {
		FDecimal x = new FDecimal("115.256", 2);
		
		assertEquals(new BigDecimal("115.26"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(115.26d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testCtor2_SI_ScientificNotation() {
		FDecimal x = new FDecimal("3.15E2", 1);
		
		assertEquals(new BigDecimal("315.0"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(1, x.getScale());
		assertEquals(315.0d, x.doubleValue(), 0.01d);
	}
	
	@Test
	public void testCtor2_SI_TrimTrailingZeroes() {
		FDecimal x = new FDecimal("-2.98900", 4);
		
		assertEquals(new BigDecimal("-2.9890"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(4, x.getScale());
		assertEquals(-2.989d, x.doubleValue(), 0.0001d);
	}

	@Test (expected=NumberFormatException.class)
	public void testCtor2_SI_InvalidFormat() {
		new FDecimal("bar", 2);
	}

	@Test
	public void testCtor0() {
		FDecimal x = new FDecimal();
		
		assertEquals(new BigDecimal("0"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(0, x.getScale());
		assertEquals(0.0d, x.doubleValue(), 0.001d);
	}
	
	@Test
	public void testToString() {
		assertEquals("24.097", new FDecimal(24.097d, 3).toString());
		assertEquals("115.05", new FDecimal("115.05").toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		FDecimal x = new FDecimal();
		
		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals(this));
	}
	
	@Test
	public void testEquals() {
		FDecimal value = new FDecimal(115.08d, 2, RoundingMode.FLOOR);
		
		Variant<Double> vVal = new Variant<Double>(115.08d, 24.19d);
		Variant<Integer> vScale = new Variant<Integer>(vVal, 2, 4);
		Variant<RoundingMode> vRm = new Variant<RoundingMode>(vScale)
				.add(RoundingMode.FLOOR)
				.add(RoundingMode.UNNECESSARY);
		Variant<?> iterator = vRm;
		int foundCnt = 0;
		FDecimal x, found = null;
		do {
			x = new FDecimal(vVal.get(), vScale.get(), vRm.get());
			if ( value.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(115.08d, found.doubleValue(), 0.001d);
		assertEquals(2, found.getScale());
		assertEquals(RoundingMode.FLOOR, found.getRoundingMode());
	}
	
	@Test
	public void testCompareTo() {
		assertEquals( 1, new FDecimal("12.34").compareTo(null));
		assertEquals( 0, new FDecimal("12.34").compareTo(new FDecimal("12.34")));
		assertEquals( 0, new FDecimal("12.34").compareTo(new FDecimal("12.3400")));
		assertEquals( 1, new FDecimal("12.34").compareTo(new FDecimal("12.3399")));
		assertEquals(-1, new FDecimal("12.34").compareTo(new FDecimal("12.3401")));
	}
	
	@Test
	public void testWithScale1() {
		FDecimal expected = new FDecimal("115.2260", 4, RoundingMode.HALF_DOWN);
		FDecimal actual = new FDecimal("115.226", RoundingMode.HALF_DOWN).withScale(4);
		assertEquals(expected, actual);
		
		expected = new FDecimal("115.23");
		actual = new FDecimal("115.226").withScale(2);
		assertEquals(expected, actual);
	}

}
