package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Point2DTest {
	private Point2D point;

	@Before
	public void setUp() throws Exception {
		point = new Point2D(100, 150);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(100, point.getX());
		assertEquals(150, point.getY());
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(point.equals(point));
		assertFalse(point.equals(null));
		assertFalse(point.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(point.equals(new Point2D(100, 150)));
		assertFalse(point.equals(new Point2D(101, 150)));
		assertFalse(point.equals(new Point2D(100, 151)));
		assertFalse(point.equals(new Point2D(101, 151)));
	}
	
	@Test
	public void testToString() {
		assertEquals("Point2D[100,150]", point.toString());
	}

}
