package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
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

public class SWCandlestickLayerTest {
	
	static Interval IM5(String timeString) {
		Instant start = Instant.parse(timeString);
		return Interval.of(start, start.plusSeconds(300));
	}
	
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private SeriesImpl<Candle> series;
	private SWCandlestickLayer service;
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
		service = new SWCandlestickLayer(series);
		caDriver = new CategoryAxisDriverImpl("CATEGORY", AxisDirection.RIGHT);
		caViewport = new CategoryAxisViewportImpl();
		vaDriver = new ValueAxisDriverImpl("VALUE", AxisDirection.UP);
		vaViewport = new ValueAxisViewportImpl();
		plot = new Rectangle(Point2D.ZERO, 200, 100);
		caSegment = new Segment1D(0, 200);
		vaSegment = new Segment1D(0, 200);
	}
	
	@After
	public void tearDown() throws Exception {
		vaMapper = null;
		vaMapper = null;
		context = null;
	}
	
	@Test
	public void testGetValueRange() throws Exception {
		series.add(new Candle(IM5("2018-02-25T00:00:00Z"), of(100L), of(105L), of( 98L), of(102L), of( 1L)));
		series.add(new Candle(IM5("2018-02-25T00:05:00Z"), of(102L), of(107L), of( 96L), of(101L), of( 5L)));
		series.add(null);
		series.add(new Candle(IM5("2018-02-25T00:10:00Z"), of(103L), of(103L), of(101L), of(101L), of(10L)));
		
		Range<CDecimal> actual = service.getValueRange(0, 4);
		
		Range<CDecimal> expected = new Range<>(of(96L), of(107L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_LayerInvisible() throws Exception {
		series.add(new Candle(IM5("2018-02-25T00:00:00Z"), of(100L), of(105L), of( 98L), of(102L), of( 1L)));
		series.add(new Candle(IM5("2018-02-25T00:05:00Z"), of(102L), of(107L), of( 96L), of(101L), of( 5L)));
		series.add(new Candle(IM5("2018-02-25T00:10:00Z"), of(103L), of(103L), of(101L), of(101L), of(10L)));
		service.setVisible(false);
		
		Range<CDecimal> actual = service.getValueRange(0,  3);
		
		Range<CDecimal> expected = null;
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_NoData() throws Exception {
		series.add(null);
		series.add(null);
		series.add(null);
		
		Range<CDecimal> actual = service.getValueRange(0, 3);
		
		Range<CDecimal> expected = null;
		assertEquals(expected, actual);
	}

	@Test
	public void testPaintLayer_Right() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(2, 4);
		caViewport.setPreferredNumberOfBars(4);
		caMapper = caDriver.createMapper(caSegment, caViewport);	// 50px per bar
		vaViewport.setValueRange(new Range<>(of(0L), of(200L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 1 unit per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot);
		series.add(new Candle(IM5("2018-02-24T23:55:00Z"), of( 90L), of(106L), of( 90L), of(105L), of(20L)));
		series.add(null);
		series.add(new Candle(IM5("2018-02-25T00:00:00Z"), of(100L), of(105L), of( 98L), of(102L), of( 1L))); // bullish
		series.add(new Candle(IM5("2018-02-25T00:05:00Z"), of(102L), of(107L), of( 96L), of(101L), of( 5L))); // bearish
		series.add(null); // test null value
		series.add(new Candle(IM5("2018-02-25T00:10:00Z"), of(103L), of(103L), of(101L), of(101L), of(10L))); // bearish
		graphicsMock.setStroke(new BasicStroke(2));
		// draw candle #2
		graphicsMock.setColor(new Color(0, 80, 0));		// bullish shadow color
		graphicsMock.drawLine(24, 94, 24, 101);			// H=200-105-1=94 L=200- 98-1=101
		graphicsMock.setColor(new Color(0, 128, 0));	// bullish body color
		graphicsMock.fillRect(0, 97, 49, 3);			// O=200-100-1=99 C=200-102-1= 97
		// draw candle #3
		graphicsMock.setColor(new Color(80, 0, 0));		// bearish shadow color
		graphicsMock.drawLine(74, 92, 74, 103);			// H=200-107-1=92 L=200- 96-1=103
		graphicsMock.setColor(new Color(230, 0, 0));	// bearish body color
		graphicsMock.fillRect(50, 97, 49, 2);			// O=200-102-1=97 C=200-101-1= 98
		// candle #4 is null
		// draw candle #5
		graphicsMock.setColor(new Color(80, 0, 0));
		graphicsMock.drawLine(174, 96, 174, 98);
		graphicsMock.setColor(new Color(230, 0, 0));
		graphicsMock.fillRect(150, 96, 49, 3);
		control.replay();
		
		service.paintLayer(context, graphicsMock);
		
		control.verify();
	}
	
	@Test
	public void testPaintLayer_Right_BarLengthEq1() throws Exception {
		caViewport.setCategoryRangeByFirstAndNumber(0, 3);
		caViewport.setPreferredNumberOfBars(3);
		caMapper = caDriver.createMapper(new Segment1D(0, 3), caViewport);	// 1px per bar
		vaViewport.setValueRange(new Range<>(of(0L), of(200L)));		
		vaMapper = vaDriver.createMapper(vaSegment, vaViewport);	// 1 unit per 1px
		context = new BCDisplayContextImpl(caMapper, vaMapper, plot);
		series.add(new Candle(IM5("2018-02-25T00:00:00Z"), of(100L), of(105L), of( 98L), of(102L), of( 1L))); // bullish
		series.add(new Candle(IM5("2018-02-25T00:05:00Z"), of(102L), of(107L), of( 96L), of(101L), of( 5L))); // bearish
		series.add(new Candle(IM5("2018-02-25T00:10:00Z"), of(103L), of(103L), of(101L), of(101L), of(10L))); // bearish
		graphicsMock.setStroke(new BasicStroke(2));
		// draw candle #0
		graphicsMock.setColor(new Color(0, 80, 0));
		graphicsMock.drawLine(0, 94, 0, 101);
		graphicsMock.setColor(new Color(0, 128, 0));
		graphicsMock.drawLine(0, 97, 0, 99);
		// draw candle #1
		graphicsMock.setColor(new Color(80, 0, 0));
		graphicsMock.drawLine(1, 92, 1, 103);
		graphicsMock.setColor(new Color(230, 0, 0));
		graphicsMock.drawLine(1, 97, 1, 98);
		// draw candle #2
		graphicsMock.setColor(new Color(80, 0, 0));
		graphicsMock.drawLine(2, 96, 2, 98);
		graphicsMock.setColor(new Color(230, 0, 0));
		graphicsMock.drawLine(2, 96, 2, 98);
		control.replay();
		
		service.paintLayer(context, graphicsMock);
		
		control.verify();
	}

}
