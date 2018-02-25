package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.awt.Color;
import java.awt.Graphics2D;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

public class SWHistogramLayerTest {
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private SeriesImpl<CDecimal> series;
	private SWHistogramLayer service;
	private CategoryAxisDriver caDriver;
	private CategoryAxisViewport caViewport;
	private CategoryAxisDisplayMapper caMapper;
	private ValueAxisDriver vaDriver;
	private ValueAxisViewport vaViewport;
	private ValueAxisDisplayMapper vaMapper;
	private BCDisplayContext context;
	private Rectangle plot;
	private Segment1D caSegment, vaSegment;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		graphicsMock = control.createMock(Graphics2D.class);
		series = new SeriesImpl<>("foo");
		service = new SWHistogramLayer(series);
		caDriver = new CategoryAxisDriverImpl("CATEGORY", AxisDirection.RIGHT);
		caViewport = new CategoryAxisViewportImpl();
		vaDriver = new ValueAxisDriverImpl("VALUE", AxisDirection.UP);
		vaViewport = new ValueAxisViewportImpl();
		plot = new Rectangle(Point2D.ZERO, 200, 100);
		caSegment = new Segment1D(0, 200);
		vaSegment = new Segment1D(0, 100);
	}
	
	@After
	public void tearDown() throws Exception {
		vaMapper = null;
		vaMapper = null;
		context = null;
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
		service.setParam(SWHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0, 2);

		Range<CDecimal> expected = null;
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValueRange_ZeroBtwMinAndMax() throws Exception {
		series.add(of(10L));
		series.add(null);
		series.add(of(-3L));
		series.add(of(15L));
		series.add(null);
		series.add(of(-4L));
		
		Range<CDecimal> actual = service.getValueRange(0,  series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(-4L), of(15L));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValueRange_ZeroBtwMinAndMax_Negate() throws Exception {
		series.add(of(10L));
		series.add(null);
		series.add(of(-3L));
		series.add(of(15L));
		series.add(null);
		series.add(of(-4L));
		service.setParam(SWHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0,  series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(-15L), of(4L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_MinAndMaxGtZero() throws Exception {
		series.add(of(10L));
		series.add(null);
		series.add(of(3L));
		series.add(of(15L));
		series.add(null);
		
		Range<CDecimal> actual = service.getValueRange(0, series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(0L), of(15L));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValueRange_MinAndMaxGtZero_Negate() throws Exception {
		series.add(of(10L));
		series.add(null);
		series.add(of(3L));
		series.add(of(15L));
		series.add(null);
		service.setParam(SWHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0, series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(-15L), of(0L));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValueRange_MinAndMaxLtZero() throws Exception {
		series.add(of(-2L));
		series.add(null);
		series.add(of(-8L));
		
		Range<CDecimal> actual = service.getValueRange(0,  series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(-8L), of(0L));
		assertEquals(expected, actual);
	}

	@Test
	public void testGetValueRange_MinAndMaxLtZero_Negate() throws Exception {
		series.add(of(-2L));
		series.add(null);
		series.add(of(-8L));
		service.setParam(SWHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0,  series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(0L), of(8L));
		assertEquals(expected, actual);
	}
	
	@Test
	@Ignore
	public void testPaintLayer_Up_SkipCuzNotSupported() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPaintLayer_Right_NullValues() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(2, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(caSegment, caViewport);	// 50px per bar
		vaViewport.setValueRange(new Range<>(of(0L), of(500L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 5 units per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot);
		series.add(of(  5L));
		series.add(of(  1L));
		series.add(of(250L));
		series.add(    null);
		series.add(of( 38L));
		series.add(of(120L));
		graphicsMock.setColor(Color.GRAY);
		graphicsMock.fillRect(  0, 49, 49, 51);
		graphicsMock.fillRect(100, 92, 49,  8);
		graphicsMock.fillRect(150, 75, 49, 25);
		control.replay();
		
		service.paint(context, graphicsMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintLayer_Right_Negate() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(2, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(caSegment, caViewport);	// 50px per bar
		vaViewport.setValueRange(new Range<>(of(-500L), of(0L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 5 units per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot);
		series.add(of(  5L));
		series.add(of(  1L));
		series.add(of(250L));
		series.add(    null);
		series.add(of( 38L));
		series.add(of(120L));
		graphicsMock.setColor(Color.GRAY);
		graphicsMock.fillRect(  0, 0, 49, 50);
		graphicsMock.fillRect(100, 0, 49,  8);
		graphicsMock.fillRect(150, 0, 49, 24);
		control.replay();
		service.setParam(SWHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		service.paint(context, graphicsMock);
		
		control.verify();
	}

	@Test
	public void testPaintLayer_Right_BarLengthEq1() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(2, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(new Segment1D(0, 4), caViewport);	// 1px per bar
		vaViewport.setValueRange(new Range<>(of(0L), of(500L)));
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 5 units per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot);
		series.add(of(  5L));
		series.add(of(  1L));
		series.add(of(250L));
		series.add(    null);
		series.add(of( 38L));
		series.add(of(120L));
		graphicsMock.setColor(Color.GRAY);
		graphicsMock.drawLine(0, 49, 0, 99);
		graphicsMock.drawLine(2, 92, 2, 99);
		graphicsMock.drawLine(3, 75, 3, 99);
		control.replay();
		
		service.paint(context, graphicsMock);
		
		control.verify();
	}

}
