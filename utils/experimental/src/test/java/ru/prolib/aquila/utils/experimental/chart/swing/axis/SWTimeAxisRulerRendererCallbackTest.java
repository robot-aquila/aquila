package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

public class SWTimeAxisRulerRendererCallbackTest {
	private IMocksControl control;
	private Font fontMock;
	private Graphics2D graphicsMock;
	private FontMetrics fontMetricsMock;
	private CategoryAxisDisplayMapper mapperMock;
	private SWTimeAxisRulerRendererCallback service;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		fontMock = control.createMock(Font.class);
		fontMetricsMock = control.createMock(FontMetrics.class);
		graphicsMock = control.createMock(Graphics2D.class);
		mapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		service = new SWTimeAxisRulerRendererCallback();
	}
	
	@Test
	public void testGetInstance() {
		SWRendererCallbackCA actual = SWTimeAxisRulerRendererCallback.getInstance();
		
		assertNotNull(actual);
		assertEquals(SWTimeAxisRulerRendererCallback.class, actual.getClass());
		assertSame(actual, SWTimeAxisRulerRendererCallback.getInstance());
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
		graphicsMock.setColor(Color.BLACK);
		expect(graphicsMock.getFontMetrics()).andStubReturn(fontMetricsMock);
		expect(fontMetricsMock.getAscent()).andReturn(12);
		graphicsMock.drawLine(40, 100, 239, 100); // inner line
		graphicsMock.drawLine(45, 100,  45, 119);
		graphicsMock.drawString("9h",   47, 112);
		graphicsMock.drawLine(65, 100,  65, 119);
		graphicsMock.drawString(":55",  67, 112);
		graphicsMock.drawLine(85, 100,  85, 119);
		expect(fontMetricsMock.stringWidth("10h")).andReturn(5);
		graphicsMock.drawString("10h",  87, 112);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", true))
				.setShowInnerLine(true)
				.setShowOuterLine(false);
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawRuler_AtBottom_SkipTextOfLastLabelIfOutOfBounds() {
		Rectangle plot = new Rectangle(new Point2D(0, 80), 200, 20);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(11, "8h", 146)); // this should be drawn normally
		labels.add(new RLabel(12, "9h", 196)); // this text should be skipped 
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.setFont(fontMock);
		graphicsMock.setColor(Color.BLACK);
		expect(graphicsMock.getFontMetrics()).andStubReturn(fontMetricsMock);
		expect(fontMetricsMock.getAscent()).andReturn(12);
		graphicsMock.drawLine(146, 80, 146, 99);
		graphicsMock.drawString("8h",  148, 92);
		graphicsMock.drawLine(196, 80, 196, 99);
		expect(fontMetricsMock.stringWidth("9h")).andReturn(8);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", true))
				.setShowInnerLine(false)
				.setShowOuterLine(false);
		
		service.drawRuler(setup, plot, graphicsMock, mapperMock, labels, fontMock);
		
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
		graphicsMock.setColor(Color.BLACK);
		expect(graphicsMock.getFontMetrics()).andReturn(fontMetricsMock);
		graphicsMock.drawLine(30,  0, 429,  0); // outer line
		graphicsMock.drawLine(45,  0,  45, 24);
		graphicsMock.drawString("9h",  47, 22);
		graphicsMock.drawLine(65,  0,  65, 24);
		graphicsMock.drawString(":55", 67, 22);
		graphicsMock.drawLine(85,  0,  85, 24);
		expect(fontMetricsMock.stringWidth("10h")).andReturn(5);
		graphicsMock.drawString("10h", 87, 22);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", false))
				.setShowInnerLine(false)
				.setShowOuterLine(true);
		
		service.drawRuler(setup, target, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawRules_AtTop_SkipTextOfLastLabelIfOutOfBounds() {
		Rectangle plot = new Rectangle(new Point2D(0, 0), 200, 20);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(86, "3h", 146)); // this should be drawn normally
		labels.add(new RLabel(87, "5h", 196)); // this text should be skipped 
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.setFont(fontMock);
		graphicsMock.setColor(Color.BLACK);
		expect(graphicsMock.getFontMetrics()).andStubReturn(fontMetricsMock);
		graphicsMock.drawLine(146, 0, 146, 19);
		graphicsMock.drawString("3h",  148, 17);
		graphicsMock.drawLine(196, 0, 196, 19);
		expect(fontMetricsMock.stringWidth("5h")).andReturn(8);
		control.replay();
		SWTimeAxisRulerSetup setup = new SWTimeAxisRulerSetup(new RulerID("foo", "bar", false))
				.setShowInnerLine(false)
				.setShowOuterLine(false);
		
		service.drawRuler(setup, plot, graphicsMock, mapperMock, labels, fontMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawGridLines_HorizontalRuler() {
		GridLinesSetup setup = new GridLinesSetup(new RulerRendererID("foo", "bar"));
		Rectangle plot = new Rectangle(new Point2D(50, 15), 400, 250);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		graphicsMock.setColor(Color.GRAY);
		graphicsMock.drawLine(45, 15, 45, 264);
		graphicsMock.drawLine(65, 15, 65, 264);
		graphicsMock.drawLine(85, 15, 85, 264);
		control.replay();
		
		service.drawGridLines(setup, plot, graphicsMock, mapperMock, labels);
		
		control.verify();
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testDrawGridLines_VerticalRuler_NotImplemented() {
		GridLinesSetup setup = new GridLinesSetup(new RulerRendererID("foo", "bar"));
		Rectangle plot = new Rectangle(new Point2D(50, 15), 400, 250);
		List<RLabel> labels = new ArrayList<>();
		labels.add(new RLabel(5,  "9h", 45));
		labels.add(new RLabel(7, ":55", 65));
		labels.add(new RLabel(9, "10h", 85));
		expect(mapperMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		
		service.drawGridLines(setup, plot, graphicsMock, mapperMock, labels);
	}

}
