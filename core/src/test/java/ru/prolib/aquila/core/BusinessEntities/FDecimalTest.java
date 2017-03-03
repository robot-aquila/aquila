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
		
		assertEquals(new BigDecimal("22.10"), x.toBigDecimal());
		assertEquals(RoundingMode.FLOOR, x.getRoundingMode());
		assertEquals(2, x.getScale());
		assertEquals(22.1d, x.doubleValue(), 0.001d);
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
	public void testCtor0() {
		FDecimal x = new FDecimal();
		
		assertEquals(new BigDecimal("0.00"), x.toBigDecimal());
		assertEquals(RoundingMode.HALF_UP, x.getRoundingMode());
		assertEquals(2, x.getScale());
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

}
