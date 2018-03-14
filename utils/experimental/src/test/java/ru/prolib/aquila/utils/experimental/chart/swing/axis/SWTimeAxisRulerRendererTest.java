package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;
import static org.easymock.EasyMock.*;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapperImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRenderer.LabelDimensions;

public class SWTimeAxisRulerRendererTest {
	private static ZoneId MSK = ZoneId.of("Europe/Moscow");
	
	static Instant atMSK(String timeString) {
		return LocalDateTime.parse(timeString).atZone(MSK).toInstant();
	}
	
	static class FontMetricsStub extends FontMetrics {
		private static final long serialVersionUID = 1L;
		private final int charWidth, charHeight;

		public FontMetricsStub(int charWidth, int charHeight) {
			super(null);
			this.charWidth = charWidth;
			this.charHeight = charHeight;
		}
		
		@Override
		public int stringWidth(String text) {
			return text.length() * charWidth;
		}
		
		@Override
		public int getHeight() {
			return charHeight;
		}
		
	}
	
	private IMocksControl control;
	private SW2MTFAdapter adapterMock;
	private Font fontMock;
	private FontMetrics fontMetricsStub, fontMetricsMock;
	private Graphics2D graphicsMock;
	private SWTimeAxisRulerRenderer service;
	private CategoryAxisDisplayMapper mapper, mapperMock;
	private CategoryAxisDriver driver;
	private CategoryAxisViewport viewport;
	private TSeriesImpl<Instant> categories;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		adapterMock = control.createMock(SW2MTFAdapter.class);
		fontMock = control.createMock(Font.class);
		fontMetricsMock = control.createMock(FontMetrics.class);
		fontMetricsStub = new FontMetricsStub(4, 8);
		graphicsMock = control.createMock(Graphics2D.class);
		mapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		service = new SWTimeAxisRulerRenderer("bar", adapterMock, fontMock);
		driver = new CategoryAxisDriverImpl("time", AxisDirection.RIGHT);
		viewport = new CategoryAxisViewportImpl();
		categories = new TSeriesImpl<>(ZTFrame.M15);
	}
	
	@After
	public void tearDown() throws Exception {
		mapper = null;
	}
	
	private MTFLabelMapper createLabelMapper(String... labels) {
		int hour = -1;
		List<MTFLabel> dummy = new ArrayList<>();
		for ( String label : labels ) {
			if ( label.endsWith("h") ) {
				hour = Integer.valueOf(label.substring(0, label.length() - 1));
				dummy.add(new MTFLabel(LocalTime.of(hour, 0), label, true));
			} else if ( label.startsWith(":") ) {
				int minute = Integer.valueOf(label.substring(1));
				dummy.add(new MTFLabel(LocalTime.of(hour, minute), label, false));
			}
		}
		return new MTFLabelMapperImpl(MSK, dummy);
	}
	
	private void addCategories(String... timeString) {
		for ( String x : timeString ) {
			Instant t = atMSK(x);
			categories.set(t, t);
		}
	}
	
	@Test
	public void testCtor3() {
		assertEquals("bar", service.getID());
		assertEquals(adapterMock, service.getMTFAdapter());
		assertEquals(fontMock, service.getLabelFont());
	}

	@Test
	public void testCtor1() {
		service = new SWTimeAxisRulerRenderer("foo");
		assertEquals("foo", service.getID());
		assertSame(SW2MTFAdapterImpl.getInstance(), service.getMTFAdapter());
		assertSame(LABEL_FONT, service.getLabelFont());
	}
	
	@Test
	public void testCtor2() {
		service = new SWTimeAxisRulerRenderer("zulu24", categories);
		assertEquals("zulu24", service.getID());
		assertSame(SW2MTFAdapterImpl.getInstance(), service.getMTFAdapter());
		assertSame(LABEL_FONT, service.getLabelFont());
		assertEquals(categories, service.getCategories());
	}

	@Test
	public void testSetLabelFont() {
		service.setLabelFont(LABEL_FONT);
		assertSame(LABEL_FONT, service.getLabelFont());
	}
	
	@Test
	public void testSetCategories() {
		service.setCategories(categories);
		assertSame(categories, service.getCategories());
	}
	
	@Test
	public void testGetMaxLabelWidth() {
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		control.replay();
		
		assertEquals(25, service.getMaxLabelWidth(graphicsMock));
		
		control.verify();
	}
	
	@Test
	public void testGetLabelWidth() {
		assertEquals(37, service.getLabelWidth("foobar24", fontMetricsStub));
	}
	
	@Test
	public void testGetLabelHeight() {
		assertEquals(13, service.getLabelHeight("foobar24", fontMetricsStub));
	}
	
	@Test
	public void testGetMaxLabelHeight() {
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		control.replay();
		
		assertEquals(13, service.getMaxLabelHeight(graphicsMock));
		
		control.verify();
	}

	@Test
	public void testPrepareRuler() {
		// bar size is 200 / 10 = 20px
		// 3 chars per label is 12px size -> one label per bar
		// each coordinate +10px because of center of bar
		viewport.setCategoryRangeByFirstAndNumber(5, 10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		expect(adapterMock.getLabelMapper(mapper, ZTFrame.M15,
				new LabelDimensions(service, fontMetricsStub)))
			.andReturn(createLabelMapper( "9h", ":15", ":30", ":45",
										 "10h", ":15", ":30", ":45",
										 "11h", ":15", ":30", ":45",
										 "12h", ":15", ":30", ":45",
										 "13h", ":15", ":30", ":45"));
		control.replay();
		addCategories("2018-02-19T09:00:00",
					  "2018-02-19T09:15:00",
					  "2018-02-19T09:30:00",
					  "2018-02-19T09:45:00",
					  "2018-02-19T10:00:00",
					  "2018-02-19T10:15:00",
					  "2018-02-19T10:30:00",
					  "2018-02-19T10:45:00",
					  "2018-02-19T11:00:00",
					  "2018-02-19T11:15:00",
					  "2018-02-19T11:30:00",
					  "2018-02-19T11:45:00",
					  "2018-02-19T12:00:00",
					  "2018-02-19T12:15:00",
					  "2018-02-19T12:30:00");
		service.setCategories(categories);
		
		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);
		
		control.verify();
		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel( 5, ":15",  10));
		expectedLabels.add(new RLabel( 6, ":30",  30));
		expectedLabels.add(new RLabel( 7, ":45",  50));
		expectedLabels.add(new RLabel( 8, "11h",  70));
		expectedLabels.add(new RLabel( 9, ":15",  90));
		expectedLabels.add(new RLabel(10, ":30", 110));
		expectedLabels.add(new RLabel(11, ":45", 130));
		expectedLabels.add(new RLabel(12, "12h", 150));
		expectedLabels.add(new RLabel(13, ":15", 170));
		expectedLabels.add(new RLabel(14, ":30", 190));
		PreparedRuler expected = new SWPreparedRulerCA(service,
				mapper, expectedLabels, fontMock);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPrepareRuler_SkipNullValues() {
		// bar size is 200 / 10 = 20px
		// 3 chars per label is 12px size -> one label per bar
		// each coordinate +10px because of center of bar
		viewport.setCategoryRangeByFirstAndNumber(5, 10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		expect(adapterMock.getLabelMapper(mapper, ZTFrame.M15,
				new LabelDimensions(service, fontMetricsStub)))
			.andReturn(createLabelMapper( "9h", ":15", ":30", ":45",
										 "10h", ":15", ":30", ":45",
										 "11h", ":15", ":30", ":45",
										 "12h", ":15", ":30", ":45",
										 "13h", ":15", ":30", ":45"));
		control.replay();
		addCategories("2018-02-19T09:00:00",
					  "2018-02-19T09:15:00",
					  "2018-02-19T09:30:00",
					  "2018-02-19T09:45:00",
					  "2018-02-19T10:00:00",
					  "2018-02-19T10:15:00",
					  "2018-02-19T10:30:00",
					  "2018-02-19T10:45:00",
					  "2018-02-19T11:00:00",
					  "2018-02-19T11:15:00",
					  "2018-02-19T11:30:00",
					  "2018-02-19T11:45:00",
					  "2018-02-19T12:00:00",
					  "2018-02-19T12:15:00",
					  "2018-02-19T12:30:00");
		categories.set(atMSK("2018-02-19T11:00:00"), null);
		categories.set(atMSK("2018-02-19T11:30:00"), null);
		categories.set(atMSK("2018-02-19T11:45:00"), null);
		service.setCategories(categories);
		
		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);
		
		control.verify();
		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel( 5, ":15",  10));
		expectedLabels.add(new RLabel( 6, ":30",  30));
		expectedLabels.add(new RLabel( 7, ":45",  50));
		expectedLabels.add(new RLabel( 9, ":15",  90));
		expectedLabels.add(new RLabel(12, "12h", 150));
		expectedLabels.add(new RLabel(13, ":15", 170));
		expectedLabels.add(new RLabel(14, ":30", 190));
		PreparedRuler expected = new SWPreparedRulerCA(service,
				mapper, expectedLabels, fontMock);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPrepareRuler_SkipValuesWhichAreNotMatchedWithLabel() {
		viewport.setCategoryRangeByFirstAndNumber(0, 10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		expect(adapterMock.getLabelMapper(mapper, ZTFrame.M5,
				new LabelDimensions(service, fontMetricsStub)))
			.andReturn(createLabelMapper("10h", ":15", ":30", ":45",
										 "11h", ":15", ":30", ":45",
										 "12h", ":15", ":30", ":45"));
		control.replay();
		categories = new TSeriesImpl<>(ZTFrame.M5);
		addCategories("2018-02-18T10:00:00",
					  "2018-02-18T10:10:00",
					  "2018-02-18T10:20:00",
					  "2018-02-18T10:30:00",
					  "2018-02-18T10:40:00",
					  "2018-02-18T10:50:00",
					  "2018-02-18T11:00:00",
					  "2018-02-18T11:10:00",
					  "2018-02-18T11:20:00",
					  "2018-02-18T11:30:00");
		service.setCategories(categories);

		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);
		
		control.verify();
		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel(0, "10h",  10));
		expectedLabels.add(new RLabel(3, ":30",  70));
		expectedLabels.add(new RLabel(6, "11h", 130));
		expectedLabels.add(new RLabel(9, ":30", 190));
		PreparedRuler expected = new SWPreparedRulerCA(service,
				mapper, expectedLabels, fontMock);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPrepareRuler_SkipWhenOverlappedPreviousLabel() {
		// bar size is 100 / 10 = 10px
		// 3 chars label has 12px size -> one label per 2 bars
		// each coordinate is +5px because of center of bar
		viewport.setCategoryRangeByFirstAndNumber(0, 5);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 100), viewport);
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsStub);
		expect(adapterMock.getLabelMapper(mapper, ZTFrame.M15,
				new LabelDimensions(service, fontMetricsStub)))
			.andReturn(createLabelMapper("10h", ":15", ":30", ":45", "11h"));
		control.replay();
		addCategories("2018-02-18T10:00:00",
					  "2018-02-18T10:15:00",
					  "2018-02-18T10:30:00",
					  "2018-02-18T10:45:00",
					  "2018-02-18T11:00:00");
		service.setCategories(categories);
		
		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);
		
		control.verify();
		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel(0, "10h", 55));
		expectedLabels.add(new RLabel(2, ":30", 75));
		expectedLabels.add(new RLabel(4, "11h", 95));
		PreparedRuler expected = new SWPreparedRulerCA(service,
				mapper, expectedLabels, fontMock);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDrawRuler_AtBottom() {
		Rectangle target = new Rectangle(new Point2D(40, 100), 200, 20);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.setFont(fontMock);
		expect(graphicsMock.getFontMetrics()).andStubReturn(fontMetricsMock);
		expect(fontMetricsMock.getAscent()).andReturn(12);
		graphicsMock.drawLine(40, 100, 239, 100); // inner line
		graphicsMock.drawLine(45, 100,  45, 119);
		graphicsMock.drawString("9h",   47, 112);
		graphicsMock.drawLine(65, 100,  65, 119);
		graphicsMock.drawString(":55",  67, 112);
		graphicsMock.drawLine(85, 100,  85, 119);
		graphicsMock.drawString("10h",  87, 112);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", true))
				.setShowInnerLine(true)
				.setShowOuterLine(false);
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testDrawRuler_AtLeft_NotImplemented() {
		Rectangle target = new Rectangle(new Point2D(0, 20), 60, 85);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", false));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testDrawRuler_AtRight_NotImplemented() {
		Rectangle target = new Rectangle(new Point2D(250, 40), 60, 100);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", true));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
	}
	
	@Test
	public void testDrawRuler_AtTop() {
		Rectangle target = new Rectangle(new Point2D(30, 0), 400, 25);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.setFont(fontMock);
		expect(graphicsMock.getFontMetrics()).andReturn(fontMetricsMock);
		graphicsMock.drawLine(30,  0, 429,  0); // outer line
		graphicsMock.drawLine(45,  0,  45, 24);
		graphicsMock.drawString("9h",  47, 22);
		graphicsMock.drawLine(65,  0,  65, 24);
		graphicsMock.drawString(":55", 67, 22);
		graphicsMock.drawLine(85,  0,  85, 24);
		graphicsMock.drawString("10h", 87, 22);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", false))
				.setShowInnerLine(false)
				.setShowOuterLine(true);
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}

	@Test
	public void testDrawGridLines_HorizontalRuler() {
		Rectangle plot = new Rectangle(new Point2D(50, 15), 400, 250);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.drawLine(45, 15, 45, 264);
		graphicsMock.drawLine(65, 15, 65, 264);
		graphicsMock.drawLine(85, 15, 85, 264);
		control.replay();
		
		service.drawGridLines(plot, graphicsMock, mapperMock, labels);
		
		control.verify();
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testDrawGridLines_VerticalRuler_NotImplemented() {
		Rectangle plot = new Rectangle(new Point2D(50, 15), 400, 250);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		
		service.drawGridLines(plot, graphicsMock, mapperMock, labels);
	}
	
	@Test
	public void testCreateRulerSetup() {
		RulerID rulerID = new RulerID("foo", "bar", true);
		SWTimeAxisRulerSetup expected = new SWTimeAxisRulerSetup(rulerID);
		
		assertEquals(expected, service.createRulerSetup(rulerID));
	}
	
}
