package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ChartLayoutTest {
	private Rectangle root, top, bottom, left, right, plot;
	private ChartLayout layout;

	@Before
	public void setUp() throws Exception {
		root = new Rectangle(Point2D.ZERO, 1000, 800);
		top = new Rectangle(Point2D.ZERO, 1000, 20, root);
		bottom = new Rectangle(new Point2D(0, 780), 1000, 20, root);
		left = new Rectangle(new Point2D(0, 20), 40, 760, root);
		right = new Rectangle(new Point2D(960, 20), 40, 760, root);
		plot = new Rectangle(new Point2D(40, 20), 920, 779, root);
		layout = new ChartLayout(root);
	}

	@Test
	public void testSettersAndGetters() {
		layout.setTopAxis(top);
		layout.setBottomAxis(bottom);
		layout.setLeftAxis(left);
		layout.setRightAxis(right);
		layout.setPlotArea(plot);
		
		assertEquals(root, layout.getRoot());
		assertEquals(top, layout.getTopAxis());
		assertEquals(bottom, layout.getBottomAxis());
		assertEquals(left, layout.getLeftAxis());
		assertEquals(right, layout.getRightAxis());
		assertEquals(plot, layout.getPlotArea());
	}
	
	@Test
	public void testAutoPlotArea_WhenHorizontalAxisIsLonger() {
		// schematic layout representation
		// ---------
		// |       |
		// ---------
		// | |   | |
		// | |   | |
		// ---------
		// |       |
		// ---------
		layout.autoPlotArea();
		
		Rectangle expected = new Rectangle(Point2D.ZERO, 1000, 800, root);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setTopAxis(top);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(0, 20), 1000, 780, root);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setBottomAxis(bottom);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(0, 20), 1000, 760, root);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setLeftAxis(left);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 20), 960, 760, root);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setRightAxis(right);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 20), 920, 760, root);
		assertEquals(expected, layout.getPlotArea());
	}
	
	@Test
	public void testAutoPlotArea_WhenVerticalAxisIsLonger() {
		// schematic layout representation
		// ---------
		// | |   | |
		// | |---| |
		// | |   | |
		// | |   | |
		// | |---| |
		// | |   | |
		// ---------
		layout.autoPlotArea();

		Rectangle expected = new Rectangle(new Point2D(0, 0), 1000, 800, root);
		assertEquals(expected, layout.getPlotArea());
		
		left = new Rectangle(Point2D.ZERO, 40, 1000, root);
		layout.setLeftAxis(left);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 0), 960, 800, root);
		assertEquals(expected, layout.getPlotArea());
		
		right = new Rectangle(new Point2D(960, 0), 40, 1000, root);
		layout.setRightAxis(right);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 0), 920, 800, root);
		assertEquals(expected, layout.getPlotArea());
		
		top = new Rectangle(new Point2D(40, 0), 920, 20, root);
		layout.setTopAxis(top);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 20), 920, 780, root);
		assertEquals(expected, layout.getPlotArea());
		
		bottom = new Rectangle(new Point2D(40, 780), 920, 20, root);
		layout.setBottomAxis(bottom);
		layout.autoPlotArea();

		expected = new Rectangle(new Point2D(40, 20), 920, 760, root);
		assertEquals(expected, layout.getPlotArea());
	}

}
