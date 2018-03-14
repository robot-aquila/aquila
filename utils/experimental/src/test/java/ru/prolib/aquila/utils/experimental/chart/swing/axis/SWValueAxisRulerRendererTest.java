package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.ValueAxisLabelGenerator;

public class SWValueAxisRulerRendererTest {
	private IMocksControl control;
	private ValueAxisLabelGenerator labelGeneratorMock;
	private Font fontMock;
	private FontMetrics fontMetricsMock;
	private Graphics2D graphicsMock;
	private SWValueAxisRulerRenderer service;
	private ValueAxisDisplayMapper mapper, mapperMock;
	private ValueAxisDriver driver;
	private ValueAxisViewport viewport;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		labelGeneratorMock = control.createMock(ValueAxisLabelGenerator.class);
		fontMock = control.createMock(Font.class);
		fontMetricsMock = control.createMock(FontMetrics.class);
		graphicsMock = control.createMock(Graphics2D.class);
		mapperMock = control.createMock(ValueAxisDisplayMapper.class);
		service = new SWValueAxisRulerRenderer("foo", labelGeneratorMock, of("0.25"), fontMock);
		driver = new ValueAxisDriverImpl("val", AxisDirection.UP);
		viewport = new ValueAxisViewportImpl();
	}
	
	@After
	public void tearDown() throws Exception {
		mapper = null;
	}
	
	@Test
	public void testCtor4() {
		assertEquals("foo", service.getID());
		assertEquals(of("0.25"), service.getTickSize());
		assertSame(fontMock, service.getLabelFont());
		assertSame(labelGeneratorMock, service.getLabelGenerator());
	}
	
	@Test
	public void testCtor1() {
		service = new SWValueAxisRulerRenderer("zulu24");
		assertEquals("zulu24", service.getID());
		assertEquals(of("0.01"), service.getTickSize());
		assertSame(LABEL_FONT, service.getLabelFont());
		assertSame(ValueAxisLabelGenerator.getInstance(), service.getLabelGenerator());
	}
	
	@Test
	public void testSetTickSize() {
		service.setTickSize(of(10L));
		assertEquals(of(10L), service.getTickSize());
	}
	
	@Test
	public void testSetLabelFont() {
		service.setLabelFont(LABEL_FONT);
		assertSame(LABEL_FONT, service.getLabelFont());
	}
	
	@Test
	public void testGetMaxLabelWidth() {
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsMock);
		expect(fontMetricsMock.stringWidth("000000000000")).andReturn(40);
		control.replay();
		
		assertEquals(45, service.getMaxLabelWidth(graphicsMock));
		
		control.verify();;
	}
	
	@Test
	public void testGetMaxLabelHeight() {
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsMock);
		expect(fontMetricsMock.getHeight()).andReturn(14);
		control.replay();
		
		assertEquals(19, service.getMaxLabelHeight(graphicsMock));
		
		control.verify();
	}
	
	@Test
	public void testPrepareRuler() {
		expect(graphicsMock.getFontMetrics(fontMock)).andReturn(fontMetricsMock);
		expect(fontMetricsMock.getHeight()).andReturn(12);
		viewport.setValueRange(new Range<>(of("10.00"), of("20.00")));
		mapper = driver.createMapper(new Segment1D(0, 120), viewport);
		List<CDecimal> expectedValues = new ArrayList<>();
		expectedValues.add(of("10.00"));
		expectedValues.add(of("15.00"));
		expectedValues.add(of("20.00"));
		expect(labelGeneratorMock.getLabelValues(mapper, of("0.25"), 17)).andReturn(expectedValues);
		control.replay();
		
		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);

		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel(of("10.00"), "10.00", 119));
		expectedLabels.add(new RLabel(of("15.00"), "15.00",  59));
		expectedLabels.add(new RLabel(of("20.00"), "20.00",   0));
		PreparedRuler expected = new SWPreparedRulerVA(service, mapper, expectedLabels, fontMock);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDrawRuler_AtLeft() {
		Rectangle target = new Rectangle(Point2D.ZERO, 40, 120);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", 119));
		labels.add(new RLabel(of("15.00"), "15.00",  59));
		labels.add(new RLabel(of("20.00"), "20.00",   0));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		expect(graphicsMock.getFontMetrics(fontMock)).andStubReturn(fontMetricsMock);
		expect(fontMetricsMock.getAscent()).andReturn(19);
		graphicsMock.setFont(fontMock);
		graphicsMock.drawLine(37, 119, 39, 119);
		expect(fontMetricsMock.stringWidth("10.00")).andReturn(22);
		graphicsMock.drawString("10.00", 12, 128);
		graphicsMock.drawLine(37,  59, 39,  59);
		expect(fontMetricsMock.stringWidth("15.00")).andReturn(22);
		graphicsMock.drawString("15.00", 12,  68);
		graphicsMock.drawLine(37,   0, 39,   0);
		expect(fontMetricsMock.stringWidth("20.00")).andReturn(22);
		graphicsMock.drawString("20.00", 12,   9);
		control.replay();
		RulerSetup setup = new RulerSetup(new RulerID("foo", "bar", false));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawRuler_AtRight() {
		Rectangle target = new Rectangle(new Point2D(280, 0), 40, 120);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", 119));
		labels.add(new RLabel(of("15.00"), "15.00",  59));
		labels.add(new RLabel(of("20.00"), "20.00",   0));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		expect(graphicsMock.getFontMetrics(fontMock)).andStubReturn(fontMetricsMock);
		expect(fontMetricsMock.getAscent()).andReturn(19);
		graphicsMock.setFont(fontMock);
		graphicsMock.drawLine(280, 119,  282, 119);
		graphicsMock.drawString("10.00", 285, 128);
		graphicsMock.drawLine(280,  59,  282,  59);
		graphicsMock.drawString("15.00", 285,  68);
		graphicsMock.drawLine(280,   0,  282,   0);
		graphicsMock.drawString("20.00", 285,   9);
		control.replay();
		RulerSetup setup = new RulerSetup(new RulerID("foo", "bar", true));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testDrawRuler_AtTop_NotImplemented() {
		Rectangle target = new Rectangle(new Point2D(40, 0), 240, 20);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", -1));
		labels.add(new RLabel(of("15.00"), "15.00", -1));
		labels.add(new RLabel(of("20.00"), "20.00", -1));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		control.replay();
		RulerSetup setup = new RulerSetup(new RulerID("foo", "bar", false));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testDrawRuler_AtBottom_NotImplemented() {
		Rectangle target = new Rectangle(new Point2D(40, 100), 240, 20);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", -1));
		labels.add(new RLabel(of("15.00"), "15.00", -1));
		labels.add(new RLabel(of("20.00"), "20.00", -1));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		control.replay();
		RulerSetup setup = new RulerSetup(new RulerID("foo", "bar", true));
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
	}
	
	@Test
	public void testDrawGridLines_VerticalRuler() {
		Rectangle plot = new Rectangle(new Point2D(40, 20), 240, 80);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", 119));
		labels.add(new RLabel(of("15.00"), "15.00",  59));
		labels.add(new RLabel(of("20.00"), "20.00",   0));
		expect(mapperMock.getAxisDirection()).andStubReturn(AxisDirection.UP);
		graphicsMock.drawLine(40, 119, 279, 119);
		graphicsMock.drawLine(40,  59, 279,  59);
		graphicsMock.drawLine(40,   0, 279,   0);
		control.replay();
		
		service.drawGridLines(plot, graphicsMock, mapperMock, labels);
		
		control.verify();
	}

	@Test (expected=UnsupportedOperationException.class)
	public void testDrawGridLines_HorizontalRuler_NotImplemented() {
		Rectangle plot = new Rectangle(new Point2D(40, 20), 240, 80);		
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(of("10.00"), "10.00", -1));
		labels.add(new RLabel(of("15.00"), "15.00", -1));
		labels.add(new RLabel(of("20.00"), "20.00", -1));
		expect(mapperMock.getAxisDirection()).andStubReturn(AxisDirection.RIGHT);
		control.replay();
		
		service.drawGridLines(plot, graphicsMock, mapperMock, labels);
	}
	
	@Test
	public void testCreateRulerSetup() {
		RulerID rulerID = new RulerID("alpha", "beta", false);
		RulerSetup expected = new RulerSetup(rulerID);
		
		assertEquals(expected, service.createRulerSetup(rulerID));
	}

}
