package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ChartLayoutTest {
	private Rectangle root, top, bottom, left, right, plot;
	private ChartLayout layout;

	@Before
	public void setUp() throws Exception {
		root = new Rectangle(new Point2D(0, 799), 1000, 800);
		top = new Rectangle(new Point2D(0, 799), 1000, 20, root);
		bottom = new Rectangle(new Point2D(0, 19), 1000, 20, root);
		left = new Rectangle(new Point2D(0, 779), 40, 760, root);
		right = new Rectangle(new Point2D(960, 779), 40, 760, root);
		plot = new Rectangle(new Point2D(20, 799), 920, 779, root);
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
		
		Rectangle expected = new Rectangle(new Point2D(0, 799), 1000, 800);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setTopAxis(top);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(0, 779), 1000, 780);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setBottomAxis(bottom);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(0, 779), 1000, 760);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setLeftAxis(left);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 779), 960, 760);
		assertEquals(expected, layout.getPlotArea());
		
		layout.setRightAxis(right);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 779), 920, 760);
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

		Rectangle expected = new Rectangle(new Point2D(0, 799), 1000, 800);
		assertEquals(expected, layout.getPlotArea());
		
		left = new Rectangle(new Point2D(0, 799), 40, 1000);
		layout.setLeftAxis(left);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 799), 960, 800);
		assertEquals(expected, layout.getPlotArea());
		
		right = new Rectangle(new Point2D(960, 799), 40, 1000);
		layout.setRightAxis(right);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 799), 920, 800);
		assertEquals(expected, layout.getPlotArea());
		
		top = new Rectangle(new Point2D(40, 799), 920, 20);
		layout.setTopAxis(top);
		layout.autoPlotArea();
		
		expected = new Rectangle(new Point2D(40, 779), 920, 780);
		assertEquals(expected, layout.getPlotArea());
		
		bottom = new Rectangle(new Point2D(40, 19), 920, 20);
		layout.setBottomAxis(bottom);
		layout.autoPlotArea();

		expected = new Rectangle(new Point2D(40, 779), 920, 760);
		assertEquals(expected, layout.getPlotArea());
	}

}
