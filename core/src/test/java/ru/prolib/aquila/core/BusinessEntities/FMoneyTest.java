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
		
		assertEquals(new BigDecimal("22.10"), x.toBigDecimal());
		assertEquals(RoundingMode.FLOOR, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
		assertEquals("JPY", x.getCurrencyCode());
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
	public void testCtor0() {
		FMoney x = new FMoney();
		
		assertEquals(new BigDecimal("0.00"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
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

}
