package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.BarChartViewport;
import ru.prolib.aquila.utils.experimental.chart.BarChartViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.ChartConstants;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.GraphicsProvider;

public class BarChartAxisHTest {
	private GraphicsProvider graphicsProvider;
	private BarChartViewport viewport;
	private Rectangle root;
	private ChartLayout layout;
	private BarChartAxisH<Instant> axisTop, axisBottom;
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private FontMetrics fontMetricsMock;

	@Before
	public void setUp() throws Exception {
		graphicsProvider = new GraphicsProvider();
		viewport = new BarChartViewportImpl(0, 100);
		root = new Rectangle(Point2D.ZERO, 100, 80);
		layout = new ChartLayout(root);
		control = createStrictControl();
		graphicsMock = control.createMock(Graphics2D.class);
		graphicsProvider.setGraphics(graphicsMock);
		fontMetricsMock = control.createMock(FontMetrics.class);
		axisTop = new BarChartAxisH<>(BarChartAxisH.POSITION_TOP, graphicsProvider);
		axisBottom = new BarChartAxisH<>(BarChartAxisH.POSITION_BOTTOM, graphicsProvider);
	}
	
	@Test
	public void testGetPaintArea_TopVisible() {
		expect(graphicsMock.getFontMetrics(ChartConstants.LABEL_FONT)).andReturn(fontMetricsMock);
		expect(fontMetricsMock.getHeight()).andReturn(8);
		control.replay();

		axisTop.setVisible(true);
		Rectangle actual = axisTop.getPaintArea(viewport, layout);

		control.verify();
		Rectangle expected = new Rectangle(new Point2D(0, 67), 100, 13, root);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPaintArea_TopInvisible() {
		control.replay();
		
		axisTop.setVisible(false);
		Rectangle actual = axisTop.getPaintArea(viewport, layout);
		
		control.verify();
		assertNull(actual);
	}

	@Test
	public void testGetPaintArea_BottomVisible() {
		expect(graphicsMock.getFontMetrics(ChartConstants.LABEL_FONT)).andReturn(fontMetricsMock);
		expect(fontMetricsMock.getHeight()).andReturn(8);
		control.replay();
		
		axisBottom.setVisible(true);
		Rectangle actual = axisBottom.getPaintArea(viewport, layout);
		
		control.verify();
		Rectangle expected = new Rectangle(Point2D.ZERO, 100, 13, root);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPaintArea_BottomInvisible() {
		control.replay();
		
		axisBottom.setVisible(false);
		Rectangle actual = axisBottom.getPaintArea(viewport, layout);
		
		control.verify();
		assertNull(actual);
	}

}
