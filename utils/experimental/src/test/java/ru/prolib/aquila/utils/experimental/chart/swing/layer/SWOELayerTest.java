package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.awt.Color;
import java.awt.Graphics2D;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContextImpl;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Polygon2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.data.OEEntrySet;
import ru.prolib.aquila.utils.experimental.chart.data.OEEntrySetImpl;

public class SWOELayerTest {
	private IMocksControl control;
	private Graphics2D graphicsMock;
	private SeriesImpl<OEEntrySet> entries;
	private SWOELayer service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		graphicsMock = control.createMock(Graphics2D.class);
		entries = new SeriesImpl<>();
		service = new SWOELayer(entries);
	}
	
	@Test
	public void testCtor() {
		assertEquals(Color.GREEN, service.getColor(SWOELayer.COLOR_ARROW_UP));
		assertEquals(Color.PINK, service.getColor(SWOELayer.COLOR_ARROW_DOWN));
		assertEquals(20, service.getParam(SWOELayer.PARAM_ARROW_WIDTH));
		assertEquals(10, service.getParam(SWOELayer.PARAM_ARROW_HEIGHT));
	}
	
	@Test
	public void testGetValueRange() throws Exception {
		entries.add(new OEEntrySetImpl().addEntry( true, of("12.94")));
		entries.add(null);
		entries.add(new OEEntrySetImpl().addEntry(false, of("10.74")).addEntry( true, of("11.55")));
		entries.add(null);
		entries.add(new OEEntrySetImpl().addEntry( true, of("12.86")).addEntry(false, of("10.50")));
		entries.add(new OEEntrySetImpl().addEntry(false, of("11.26")));
		
		Range<CDecimal> actual = service.getValueRange(1, 4);
		
		Range<CDecimal> expected = new Range<>(of("10.50"), of("12.86"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetValueRange_NoData() throws Exception {
		entries.add(null);
		entries.add(null);
		entries.add(null);
		entries.add(null);
		entries.add(null);
		entries.add(null);
		
		assertNull(service.getValueRange(1, 5));
	}
	
	@Test
	public void testPaint() throws Exception {
		Color cUp = service.getColor(SWOELayer.COLOR_ARROW_UP), cDn = service.getColor(SWOELayer.COLOR_ARROW_DOWN);
		entries.add(new OEEntrySetImpl().addEntry( true, of("12.94")));
		entries.add(null);
		entries.add(new OEEntrySetImpl().addEntry(false, of("10.74")).addEntry( true, of("11.55")));
		entries.add(null);
		entries.add(new OEEntrySetImpl().addEntry( true, of("12.86")).addEntry(false, of("10.50")));
		entries.add(new OEEntrySetImpl().addEntry(false, of("11.26")));
		CategoryAxisDisplayMapper cMapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		ValueAxisDisplayMapper vMapperMock = control.createMock(ValueAxisDisplayMapper.class);
		expect(cMapperMock.getAxisDirection()).andStubReturn(AxisDirection.RIGHT);
		expect(cMapperMock.getLastVisibleCategory()).andReturn(4);
		expect(cMapperMock.getFirstVisibleCategory()).andReturn(1);
		// set#2
		expect(cMapperMock.toDisplay(2)).andReturn(new Segment1D(10, 10));
		// set#2, entry#0
		expect(vMapperMock.toDisplay(of("10.74"))).andReturn(100);
		Polygon2D poly = new Polygon2D().addPointEx(15, 100).addPointEx( 5,  90).addPointEx(25,  90);
		graphicsMock.setColor(cDn);
		graphicsMock.fillPolygon(poly);
		// set#2, entry#1
		expect(vMapperMock.toDisplay(of("11.55"))).andReturn(85);
		poly = new Polygon2D().addPointEx(15, 85).addPointEx( 5, 95).addPointEx(25, 95);
		graphicsMock.setColor(cUp);
		graphicsMock.fillPolygon(poly);
		// set#4
		expect(cMapperMock.toDisplay(4)).andReturn(new Segment1D(30, 10));
		// set#4, entry#0
		expect(vMapperMock.toDisplay(of("12.86"))).andReturn(70);
		poly = new Polygon2D().addPointEx(35, 70).addPointEx(25, 80).addPointEx(45, 80);
		graphicsMock.setColor(cUp);
		graphicsMock.fillPolygon(poly);
		// set#4, entry#1
		expect(vMapperMock.toDisplay(of("10.50"))).andReturn(110);
		poly = new Polygon2D().addPointEx(35, 110).addPointEx(25, 100).addPointEx(45, 100);
		graphicsMock.setColor(cDn);
		graphicsMock.fillPolygon(poly);
		control.replay();
		Rectangle rootRect = new Rectangle(Point2D.ZERO, 200, 120);
		BCDisplayContext context = new BCDisplayContextImpl(cMapperMock, vMapperMock, rootRect);
		
		service.paint(context, graphicsMock);
		
		control.verify();
	}

}
