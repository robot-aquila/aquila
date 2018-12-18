package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContextImpl;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.interpolator.LineRenderer;
import ru.prolib.aquila.utils.experimental.chart.interpolator.Point;

public class SWIndicatorLayerTest {
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private LineRenderer lineRendererMock;
	private Shape shapeMock;
	private SeriesImpl<CDecimal> series;
	private CategoryAxisDriver caDriver;
	private CategoryAxisViewport caViewport;
	private CategoryAxisDisplayMapper caMapper;
	private ValueAxisDriver vaDriver;
	private ValueAxisViewport vaViewport;
	private ValueAxisDisplayMapper vaMapper;
	private BCDisplayContext context;
	private Rectangle plot;
	private Segment1D caSegment, vaSegment;
	private SWIndicatorLayer service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		graphicsMock = control.createMock(Graphics2D.class);
		lineRendererMock = control.createMock(LineRenderer.class);
		shapeMock = control.createMock(Shape.class);
		series = new SeriesImpl<>("foo");
		caDriver = new CategoryAxisDriverImpl("CATEGORY", AxisDirection.RIGHT);
		caViewport = new CategoryAxisViewportImpl();
		vaDriver = new ValueAxisDriverImpl("VALUE", AxisDirection.UP);
		vaViewport = new ValueAxisViewportImpl();
		plot = new Rectangle(Point2D.ZERO, 200, 100);
		caSegment = new Segment1D(0, 200);
		vaSegment = new Segment1D(0, 100);
		service = new SWIndicatorLayer(series, lineRendererMock);
	}
	
	@After
	public void tearDown() throws Exception {
		vaMapper = null;
		vaMapper = null;
		context = null;
	}
	
	@Test
	public void testCtor() {
		assertEquals(false, service.getParam(SWIndicatorLayer.NEGATE_VALUES_PARAM));
		assertEquals(2, service.getParam(SWIndicatorLayer.LINE_WIDTH_PARAM));
		assertEquals(Color.BLUE, service.getColor());
	}
	
	@Test
	public void testGetValueRange_NoData() throws Exception {
		series.add(null);
		series.add(null);
		series.add(null);
		
		Range<CDecimal> actual = service.getValueRange(0, 2);
		
		Range<CDecimal> expected = null;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_NoData_Negate() throws Exception {
		series.add(null);
		series.add(null);
		series.add(null);
		service.setParam(SWIndicatorLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0, 2);

		Range<CDecimal> expected = null;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange() throws Exception {
		series.add(of( 5L));
		series.add(of(10L));
		series.add(of(25L));
		series.add(of(13L));
		
		Range<CDecimal> actual = service.getValueRange(1, 3);
		
		Range<CDecimal> expected = new Range<>(of(10L), of(25L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_Negate() throws Exception {
		series.add(of( 5L));
		series.add(of(10L));
		series.add(of(25L));
		series.add(of(13L));
		service.setParam(SWIndicatorLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(1, 3);
		
		Range<CDecimal> expected = new Range<>(of(-25L), of(-10L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPaintLayer() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(2, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(caSegment, caViewport);	// 50px per bar
		vaViewport.setValueRange(new Range<>(of(0L), of(100L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 1 unit per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot, plot);
		series.add(of( 5L));
		series.add(of(10L));
		series.add(of(25L));
		series.add(null);
		series.add(of(13L));
		series.add(of(28L));
		series.add(of(45L));
		graphicsMock.setColor(Color.BLUE);
		graphicsMock.setStroke(new BasicStroke(2));
		List<Point> expectedPoints = new ArrayList<>();
		expectedPoints.add(new Point( 25, 74));
		expectedPoints.add(null);
		expectedPoints.add(new Point(125, 86));
		expectedPoints.add(new Point(175, 71));
		expect(lineRendererMock.renderLine(expectedPoints)).andReturn(shapeMock);
		graphicsMock.draw(shapeMock);
		control.replay();
		
		service.paintLayer(context, graphicsMock);
		
		control.verify();
	}

	@Test
	public void testPaintLayer_Negate() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(0, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(caSegment, caViewport);	// 50px per bar
		vaViewport.setValueRange(new Range<>(of(-100L), of(0L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 1 unit per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot, plot);
		series.add(of( 5L));
		series.add(of(10L));
		series.add(of(25L));
		series.add(of(13L));
		graphicsMock.setColor(Color.BLUE);
		graphicsMock.setStroke(new BasicStroke(2));
		List<Point> expectedPoints = new ArrayList<>();
		expectedPoints.add(new Point( 25,  4));
		expectedPoints.add(new Point( 75,  9));
		expectedPoints.add(new Point(125, 24));
		expectedPoints.add(new Point(175, 12));
		expect(lineRendererMock.renderLine(expectedPoints)).andReturn(shapeMock);
		graphicsMock.draw(shapeMock);
		control.replay();
		service.setParam(SWIndicatorLayer.NEGATE_VALUES_PARAM, true);
		
		service.paintLayer(context, graphicsMock);
		
		control.verify();
	}

}
