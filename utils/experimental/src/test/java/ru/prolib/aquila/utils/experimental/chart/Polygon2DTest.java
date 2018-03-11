package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Test;

public class Polygon2DTest {

	@Test
	public void testAddPointAndEquals() {
		Polygon2D poly1 = new Polygon2D();
		poly1.addPoint(15, 100);
		poly1.addPoint( 5,  90);
		poly1.addPoint(25,  90);
		Polygon2D poly2 = new Polygon2D();
		poly2.addPoint(15, 100);
		poly2.addPoint( 5,  90);
		poly2.addPoint(25,  90);
		assertEquals(poly2, poly1);
	}

	@Test
	public void testAddPointExAndEquals() {
		Polygon2D poly1 = new Polygon2D();
		assertSame(poly1, poly1.addPointEx(15, 100));
		assertSame(poly1, poly1.addPointEx( 5,  90));
		assertSame(poly1, poly1.addPointEx(25,  90));
		Polygon2D poly2 = new Polygon2D();
		poly2.addPointEx(15, 100);
		poly2.addPointEx( 5,  90);
		poly2.addPointEx(25,  90);
		assertEquals(poly2, poly1);
	}

}
