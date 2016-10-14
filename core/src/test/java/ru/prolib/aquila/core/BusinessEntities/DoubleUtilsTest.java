package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DoubleUtilsTest {
	private DoubleUtils utils;

	@Before
	public void setUp() throws Exception {
		utils = new DoubleUtils(2);
	}
	
	@Test
	public void testCtor0() {
		utils = new DoubleUtils();
		assertEquals(2, utils.getScale());
		assertEquals(0.005d, utils.getEpsilon(), 0.0001d);
		assertEquals(100d, utils.getPower(), 0.01d);
	}
	
	@Test
	public void testCtor1() {
		utils = new DoubleUtils(8);
		assertEquals(8, utils.getScale());
		assertEquals(0.000000005d, utils.getEpsilon(), 0.0000000001d);
		assertEquals(100000000d, utils.getPower(), 0.01d);
	}
	
	@Test
	public void testSetScale() {
		utils.setScale(4);
		assertEquals(4, utils.getScale());
		assertEquals(0.00005d, utils.getEpsilon(), 0.000001d);
		assertEquals(10000d, utils.getPower(), 0.01d);
	}
	
	@Test
	public void testIsEquals() {
		assertTrue(utils.isEquals(100.4500d, 100.450d));
		assertFalse(utils.isEquals(100.45d, 100.46d));
		
		assertTrue(utils.isEquals(102.29651d, 102.30d));
		assertTrue(utils.isEquals(102.40561d, 102.41d));
		assertTrue(utils.isEquals(102.45021d, 102.45d));
		utils.setScale(4);
		assertFalse(utils.isEquals(102.29651d, 102.30d));
		assertFalse(utils.isEquals(102.40561d, 102.41d));
		assertFalse(utils.isEquals(102.45021d, 102.45d));
	}
	
	@Test
	public void testRound() {
		assertEquals(102.30d, utils.round(102.29651d), 0.00001d);
		assertEquals(102.41d, utils.round(102.40561d), 0.00001d);
		assertEquals(102.45d, utils.round(102.45021d), 0.00001d);
		assertEquals(3.16d, utils.round(3.155d), 0.001d);
		assertEquals(-3.16d, utils.round(-3.155d), 0.001d);
	}
	
	@Test
	public void testScaleOf() {
		assertEquals(2, utils.scaleOf(0.05d));
		assertEquals(2, utils.scaleOf(0.01d));
		assertEquals(3, utils.scaleOf(0.005d));
		assertEquals(4, utils.scaleOf(10.1001d));
		assertEquals(0, utils.scaleOf(10.0d));
		assertEquals(0, utils.scaleOf(0.0d));
	}

}
