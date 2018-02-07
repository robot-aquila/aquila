package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class RectangleTest {
	private Rectangle rect, parent;

	@Before
	public void setUp() throws Exception {
		parent = new Rectangle(new Point2D(0, 180), 200, 150);
	}
	
	@Test
	public void testCtor3() {
		rect = new Rectangle(new Point2D(10, 20), 3, 5);
		assertEquals(new Point2D(10, 20), rect.getUpperLeft());
		assertEquals(10, rect.getLeftX());
		assertEquals(20, rect.getUpperY());
		assertEquals(new Point2D(12, 24), rect.getLowerRight());
		assertEquals(12, rect.getRightX());
		assertEquals(24, rect.getLowerY());
		assertEquals(3, rect.getWidth());
		assertEquals(5, rect.getHeight());
		assertNull(rect.getParent());
	}
	
	@Test
	public void testCtor4() {
		rect = new Rectangle(new Point2D(5, 15), 100, 80, parent);
		assertEquals(new Point2D(5, 15), rect.getUpperLeft());
		assertEquals( 5, rect.getLeftX());
		assertEquals(15, rect.getUpperY());
		assertEquals(new Point2D(104, 94), rect.getLowerRight());
		assertEquals(104, rect.getRightX());
		assertEquals( 94, rect.getLowerY());
		assertEquals(100, rect.getWidth());
		assertEquals( 80, rect.getHeight());
		assertSame(parent, rect.getParent());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor4_ThrowsIfNegativeWidth() {
		new Rectangle(Point2D.ZERO, -20, 80, parent);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor4_ThrowsIfZeroWidth() {
		new Rectangle(Point2D.ZERO, 0, 80, parent);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor4_ThrowsIfNegativeHeight() {
		new Rectangle(Point2D.ZERO, 100, -20, parent);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor4_ThrowsIfZeroHeight() {
		new Rectangle(Point2D.ZERO, 100, 0, parent);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(parent.equals(parent));
		assertFalse(parent.equals(null));
		assertFalse(parent.equals(this));
	}
	
	@Test
	public void testEquals() {
		rect = new Rectangle(new Point2D(5, 15), 100, 80, parent);
		Variant<Point2D> vPoint = new Variant<Point2D>()
				.add(new Point2D(5, 15))
				.add(new Point2D(0, 10));
		Variant<Integer> vW = new Variant<>(vPoint, 100, 26);
		Variant<Integer> vH = new Variant<>(vW, 80, 140);
		Variant<Rectangle> vParent = new Variant<Rectangle>(vH)
				.add(parent)
				.add(null)
				.add(new Rectangle(Point2D.ZERO, 10, 10));
		Variant<?> iterator = vParent;
		int foundCnt = 0;
		Rectangle x, found = null;
		do {
			x = new Rectangle(vPoint.get(), vW.get(), vH.get(), vParent.get());
			if ( rect.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Point2D(5, 15), found.getUpperLeft());
		assertEquals(100, found.getWidth());
		assertEquals( 80, found.getHeight());
		assertSame(parent, found.getParent());
	}

	@Test
	public void testToString() {
		rect = new Rectangle(new Point2D(5, 15), 100, 80, parent);
		String expected = "Rectangle[x=5,y=15,w=100,h=80,p=Rectangle[x=0,y=180,w=200,h=150]]";
		assertEquals(expected, rect.toString());
		expected = "Rectangle[x=0,y=180,w=200,h=150]";
		assertEquals(expected, parent.toString());
	}

}
