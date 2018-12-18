package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContextImpl;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.data.ALOData;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataImpl;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataProvider;

public class SWALOLayerTest {
	private IMocksControl control;
	private ALODataProvider dataProviderMock;
	private Graphics2D graphicsMock;
	private ValueAxisDisplayMapper vMapperMock;
	private Rectangle plot;
	private BCDisplayContext context;
	private SWALOLayer service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dataProviderMock = control.createMock(ALODataProvider.class);
		graphicsMock = control.createMock(Graphics2D.class);
		vMapperMock = control.createMock(ValueAxisDisplayMapper.class);
		plot = new Rectangle(Point2D.ZERO, 200, 100);
		context = new BCDisplayContextImpl(null, vMapperMock, plot, plot);
		service = new SWALOLayer("foo", dataProviderMock);
	}
	
	@Test
	public void testCtor() {
		assertEquals("foo", service.getId());
		assertEquals(new Color(0, 128, 0), service.getColor(SWALOLayer.COLOR_BUY_ORDER));
		assertEquals(new Color(210, 0, 0), service.getColor(SWALOLayer.COLOR_SELL_ORDER));
		assertEquals(2f, service.getParam(SWALOLayer.PARAM_LINE_WIDTH));
	}
	
	@Test
	public void testGetValueRange_NoData() {
		expect(dataProviderMock.getOrderVolumes()).andReturn(new ArrayList<>());
		control.replay();
		
		assertNull(service.getValueRange(0,  10));
		
		control.verify();
	}
	
	@Test
	public void testGetValueRange() {
		List<ALOData> expectedData = new ArrayList<>();
		expectedData.add(new ALODataImpl(of("112.84"), of(1L), of(0L)));
		expectedData.add(new ALODataImpl(of("109.03"), of(200L), of(15L)));
		expectedData.add(new ALODataImpl(of("125.91"), of(500L), of(3L)));
		expect(dataProviderMock.getOrderVolumes()).andReturn(expectedData);
		control.replay();
		
		assertEquals(new Range<CDecimal>(of("109.03"), of("125.91")), service.getValueRange(0,  10));
		
		control.verify();
	}
	
	@Test
	public void testGetValueRange_Invisible() {
		control.replay();
		service.setVisible(false);
		
		assertNull(service.getValueRange(0,  10));
		
		control.verify();		
	}
	
	@Test
	public void testPaintLayer() {
		expect(vMapperMock.getAxisDirection()).andStubReturn(AxisDirection.UP);
		float dp[] = { 40f, 40f };
		Stroke cStr = new BasicStroke(2f),
			   dStr = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, dp, 0f);
		List<ALOData> stubData = new ArrayList<>();
		stubData.add(new ALODataImpl(of("12.05"), of(10L), of(0L)));	// buy volume only
		stubData.add(new ALODataImpl(of("11.24"), of(0L), of(50L)));	// sell volume only
		stubData.add(new ALODataImpl(of("10.82"), of(1L), of(5L)));		// both buy and sell volumes
		expect(dataProviderMock.getOrderVolumes()).andReturn(stubData);
		expect(vMapperMock.toDisplay(of("12.05"))).andReturn(75);
		graphicsMock.setStroke(cStr);
		graphicsMock.setColor(new Color(0, 128, 0));
		graphicsMock.drawLine(0, 75, 199, 75);
		expect(vMapperMock.toDisplay(of("11.24"))).andReturn(50);
		graphicsMock.setStroke(cStr);
		graphicsMock.setColor(new Color(210, 0, 0));
		graphicsMock.drawLine(0, 50, 199, 50);
		expect(vMapperMock.toDisplay(of("10.82"))).andReturn(25);
		graphicsMock.setStroke(cStr);
		graphicsMock.setColor(new Color(0, 128, 0));
		graphicsMock.drawLine(0, 25, 199, 25);
		graphicsMock.setStroke(dStr);
		graphicsMock.setColor(new Color(210, 0, 0));
		graphicsMock.drawLine(0, 25, 199, 25);
		control.replay();
		
		service.paint(context, graphicsMock);
		
		control.verify();
	}

	@Test
	public void test_X() {
		Polygon poly = new Polygon();
		poly.addPoint(0, 0);
		poly.addPoint(4, 4);
		poly.addPoint(0, 4);
		//poly.invalidate();
		assertFalse(poly.contains(0, 0));
		assertFalse(poly.contains(1, 0));
		assertFalse(poly.contains(2, 0));
		assertFalse(poly.contains(3, 0));
		assertFalse(poly.contains(4, 0));
		
		assertTrue(poly.contains(0, 1));
		assertFalse(poly.contains(1, 1));
		assertFalse(poly.contains(2, 1));
		assertFalse(poly.contains(3, 1));
		assertFalse(poly.contains(4, 1));

		assertTrue(poly.contains(0, 2));
		assertTrue(poly.contains(1, 2));
		assertFalse(poly.contains(2, 2));
		assertFalse(poly.contains(3, 2));
		assertFalse(poly.contains(4, 2));

		assertTrue(poly.contains(0, 3));
		assertTrue(poly.contains(1, 3));
		assertTrue(poly.contains(2, 3));
		assertFalse(poly.contains(3, 3));
		assertFalse(poly.contains(4, 3));

		assertFalse(poly.contains(0, 4));
		assertFalse(poly.contains(1, 4));
		assertFalse(poly.contains(2, 4));
		assertFalse(poly.contains(3, 4));
		assertFalse(poly.contains(4, 4));

	}

}
