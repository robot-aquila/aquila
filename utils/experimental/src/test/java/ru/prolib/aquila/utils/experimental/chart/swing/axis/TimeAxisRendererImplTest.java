package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.ChartConstants;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.TimeCategoryDataProvider;
import ru.prolib.aquila.utils.experimental.chart.axis.TimeCategoryDataProviderImpl;

public class TimeAxisRendererImplTest {
	private CategoryAxisViewport viewport;
	private Rectangle root;
	private ChartLayout layout;
	private TimeAxisRendererImpl axisTop, axisBottom;
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private TimeCategoryDataProvider dataProvider;
	private FontMetrics fontMetricsMock;

	@Before
	public void setUp() throws Exception {
		viewport = new CategoryAxisViewportImpl();
		viewport.setCategoryRangeByFirstAndNumber(0, 100);
		root = new Rectangle(Point2D.ZERO, 100, 80);
		layout = new ChartLayout(root);
		control = createStrictControl();
		graphicsMock = control.createMock(Graphics2D.class);
		fontMetricsMock = control.createMock(FontMetrics.class);
		dataProvider = new TimeCategoryDataProviderImpl();
		axisTop = new TimeAxisRendererImpl(AxisPosition.TOP, dataProvider);
		axisBottom = new TimeAxisRendererImpl(AxisPosition.BOTTOM, dataProvider);
	}
	
	@Test
	public void testCtor() {
		assertEquals(AxisPosition.TOP, axisTop.getAxisPosition());
	}
	
	@Test
	public void testGetPaintArea_TopVisible() {
		expect(graphicsMock.getFontMetrics(ChartConstants.LABEL_FONT))
			.andReturn(fontMetricsMock);
		expect(fontMetricsMock.stringWidth("0000-00-00")).andReturn(30);
		expect(fontMetricsMock.getHeight()).andReturn(8);
		control.replay();

		axisTop.setVisible(true);
		Rectangle actual = axisTop.getPaintArea(layout, graphicsMock);

		control.verify();
		// label height=8*2+3=19
		// ruler height=label height+2=21
		Rectangle expected = new Rectangle(Point2D.ZERO, 100, 21, root);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPaintArea_TopInvisible() {
		control.replay();
		
		axisTop.setVisible(false);
		Rectangle actual = axisTop.getPaintArea(layout, graphicsMock);
		
		control.verify();
		assertNull(actual);
	}

	@Test
	public void testGetPaintArea_BottomVisible() {
		expect(graphicsMock.getFontMetrics(ChartConstants.LABEL_FONT))
			.andReturn(fontMetricsMock);
		expect(fontMetricsMock.stringWidth("0000-00-00")).andReturn(30);
		expect(fontMetricsMock.getHeight()).andReturn(8);
		control.replay();
		
		axisBottom.setVisible(true);
		Rectangle actual = axisBottom.getPaintArea(layout, graphicsMock);
		
		control.verify();
		Rectangle expected = new Rectangle(new Point2D(0, 59), 100, 21, root);
		assertEquals(expected, actual);
		// additional test
		assertEquals(root.getLowerY(), actual.getLowerY());
	}
	
	@Test
	public void testGetPaintArea_BottomInvisible() {
		control.replay();
		
		axisBottom.setVisible(false);
		Rectangle actual = axisBottom.getPaintArea(layout, graphicsMock);
		
		control.verify();
		assertNull(actual);
	}
	
	@Test
	public void testGetPaintArea_ThrowsWhenAtRight() {
		fail();
	}
	
	@Test
	public void testGetPaintArea_ThrowsWhenAtLeft() {
		fail();
	}

}
