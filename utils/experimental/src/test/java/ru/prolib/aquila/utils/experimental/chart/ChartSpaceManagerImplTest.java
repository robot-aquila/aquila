package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManagerImpl.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManagerImpl.HorizontalLabelSize;
import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManagerImpl.VerticalLabelSize;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerSpace;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;

public class ChartSpaceManagerImplTest {
	
	static class RendererStub implements RulerRenderer {
		private final String id;
		private final int width, height;
		
		public RendererStub(String id, int width, int height) {
			this.id = id;
			this.width = width;
			this.height = height;
		}

		@Override
		public String getID() {
			return id;
		}

		@Override
		public int getMaxLabelWidth(Object device) {
			return width;
		}

		@Override
		public int getMaxLabelHeight(Object device) {
			return height;
		}
		
	}
	
	private IMocksControl control;
	private LinkedHashMap<String, AxisDriver> drivers;
	private HashMap<ChartRulerID, RulerSetup> rulerSetups;
	private AxisDriver axisDriver1, axisDriver2;
	private ChartSpaceManagerImpl service;
	private Object deviceStub = new Object();

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		drivers = new LinkedHashMap<>();
		rulerSetups = new HashMap<>();
		axisDriver1 = new ValueAxisDriverImpl("foo", AxisDirection.UP);
		axisDriver1.registerRenderer(new RendererStub("VALUE1", 30, -1));
		axisDriver1.registerRenderer(new RendererStub("VALUE2", 40, -1));
		axisDriver2 = new ValueAxisDriverImpl("bar", AxisDirection.UP);
		axisDriver2.registerRenderer(new RendererStub("VALUE3", 25, -1));
		axisDriver2.registerRenderer(new RendererStub("VALUE4", 35, -1));
		service = new ChartSpaceManagerImpl(new HorizontalLabelSize(), drivers, rulerSetups);
	}
	
	@Test
	public void testVerticalLabelSize_GetLabelMaxSize() {
		RulerRenderer rendererMock = control.createMock(RulerRenderer.class);
		Object deviceStub = new Object();
		expect(rendererMock.getMaxLabelHeight(deviceStub)).andReturn(24);
		control.replay();
		VerticalLabelSize strategy = new VerticalLabelSize();
		
		assertEquals(24, strategy.getLabelMaxSize(rendererMock, deviceStub));
		
		control.verify();
	}
	
	@Test
	public void testVerticalLabelSize_IsValidAxis() {
		AxisDriver driverMock = control.createMock(AxisDriver.class);
		expect(driverMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		expect(driverMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		VerticalLabelSize strategy = new VerticalLabelSize();
		
		assertTrue(strategy.isValidAxis(driverMock));
		assertFalse(strategy.isValidAxis(driverMock));
		
		control.verify();
	}
	
	@Test
	public void testHorizontalLabelSize_GetLabelMaxSize() {
		RulerRenderer rendererMock = control.createMock(RulerRenderer.class);
		Object deviceStub = new Object();
		expect(rendererMock.getMaxLabelWidth(deviceStub)).andReturn(56);
		control.replay();
		HorizontalLabelSize strategy = new HorizontalLabelSize();
		
		assertEquals(56, strategy.getLabelMaxSize(rendererMock, deviceStub));
		
		control.verify();
	}
	
	@Test
	public void testHorizontalLabelSize_IsValidAxis() {
		AxisDriver driverMock = control.createMock(AxisDriver.class);
		expect(driverMock.getAxisDirection()).andReturn(AxisDirection.RIGHT);
		expect(driverMock.getAxisDirection()).andReturn(AxisDirection.UP);
		control.replay();
		HorizontalLabelSize strategy = new HorizontalLabelSize();
		
		assertFalse(strategy.isValidAxis(driverMock));
		assertTrue(strategy.isValidAxis(driverMock));
	}
	
	@Test
	public void testRegisterAxis() {
		service.registerAxis(axisDriver1);
		
		assertEquals(1, drivers.size());
		assertSame(axisDriver1, drivers.get("foo"));
		
		service.registerAxis(axisDriver2);
		
		assertEquals(2, drivers.size());
		assertSame(axisDriver2, drivers.get("bar"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterAxis_ThrowsIfAlreadyRegistered() {
		drivers.put("foo", axisDriver1);
		
		service.registerAxis(axisDriver1);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterAxis_ThrowsIfWrongOrientation() {
		axisDriver1 = new ValueAxisDriverImpl("foo", AxisDirection.RIGHT);
		
		service.registerAxis(axisDriver1);
	}
	
	@Test
	public void testSetRulerVisibility() {
		service.registerAxis(axisDriver1);
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE1", true);
		service.setRulerDisplayPriority(rulerID, 100);
		
		service.setRulerVisibility(rulerID, true);
		assertEquals(new RulerSetup(true, 100), rulerSetups.get(rulerID));
		
		service.setRulerVisibility(rulerID, false);
		assertEquals(new RulerSetup(false, 100), rulerSetups.get(rulerID));
		
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetRulerVisibility_ThrowsIfAxisNotExists() {
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE1", true);
		
		service.setRulerVisibility(rulerID, true);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetRulerVisibility_ThrowsIfRendererNotExists() {
		service.registerAxis(axisDriver1);
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE-X", true);
		
		service.setRulerVisibility(rulerID, true);
	}
	
	@Test
	public void testSetRulerDisplayPriority() {
		service.registerAxis(axisDriver1);
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE2", false);
		service.setRulerVisibility(rulerID, true);
		
		service.setRulerDisplayPriority(rulerID, 50);
		assertEquals(new RulerSetup(true, 50), rulerSetups.get(rulerID));
		
		service.setRulerDisplayPriority(rulerID, 75);
		assertEquals(new RulerSetup(true, 75), rulerSetups.get(rulerID));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetRulerDisplayPriority_ThrowsIfAxisNotExists() {
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE2", false);
		
		service.setRulerDisplayPriority(rulerID, 100);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetRulerDisplayPriority_ThrowsIfRendererNotExists() {
		service.registerAxis(axisDriver1);
		ChartRulerID rulerID = new ChartRulerID("foo", "VALUE-X", false);
		
		service.setRulerDisplayPriority(rulerID, 100);
	}
	
	@Test
	public void testPrepareLayout3SSD_ThrowsIfDataSpaceOutOfDisplay() {
		fail();
	}
	
	@Test
	public void testPrepareLayout3SSD_AllRulersVisible_DefaultOrder() {
		fail();
	}
	
	@Test
	public void testPrepareLayout3SSD_NotEnoughSpace_CutoffRulersWithLowPriority() {
		fail();
	}

	@Test
	public void testPrepareLayout3SID_AllRulersVisible_DefaultOrder() {
		service.registerAxis(axisDriver1);
		service.registerAxis(axisDriver2);
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE1", false), true);
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE1", true),  true);
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", false), true);
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", true),  true);
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE3", false), true);
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE3", true),  true);
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE4", false), true);
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE4", true),  true);
		
		ChartSpaceLayout actual = service.prepareLayout(new Segment1D(0, 1000), 500, deviceStub);
		
		Segment1D expectedDataSpace = new Segment1D(130, 740);
		List<ChartRulerSpace> expectedRulers = new ArrayList<>();
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE1", false),
											   new Segment1D(100, 30)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE1", true),
											   new Segment1D(870, 30)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE2", false),
											   new Segment1D( 60, 40)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE2", true),
											   new Segment1D(900, 40)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE3", false),
											   new Segment1D( 35, 25)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE3", true),
											   new Segment1D(940, 25)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE4", false),
											   new Segment1D(  0, 35)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE4", true),
											   new Segment1D(965, 35)));
		ChartSpaceLayout expected = new ChartSpaceLayoutImpl(expectedDataSpace, expectedRulers);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPrepareLayout3SID_ShiftedToUpper() {
		service.registerAxis(axisDriver1);
		service.registerAxis(axisDriver2);
		// The order of calling does not matter to final priority
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE4", true),  true); // U-35
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", false), true); // L-40
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", true),  true); // U-40
		
		ChartSpaceLayout actual = service.prepareLayout(new Segment1D(10, 1000), 500, deviceStub);
		
		Segment1D expectedDataSpace = new Segment1D(50, 885);
		List<ChartRulerSpace> expectedRulers = new ArrayList<>();
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE2", false),
											   new Segment1D( 10, 40)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE2", true),
											   new Segment1D(935, 40)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE4", true),
											   new Segment1D(975, 35)));
		ChartSpaceLayout expected = new ChartSpaceLayoutImpl(expectedDataSpace, expectedRulers);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testPrepareLayout3SID_ThrowsIfRulersMaxSpaceGtDisplaySpace() {
		service.prepareLayout(new Segment1D(10, 1000), 1001, deviceStub);
	}
	
	@Test
	public void testPrepareLayout3SID_NotEnoughSpace_CutoffRulersWithLowPriority() {
		service.registerAxis(axisDriver1);
		service.registerAxis(axisDriver2);
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE1", false), true); // L-30
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE1", true),  true); // U-30
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", false), true); // L-40
		service.setRulerVisibility(new ChartRulerID("foo", "VALUE2", true),  true); // U-40
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE3", false), true); // L-25
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE3", true),  true); // U-25
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE4", false), true); // L-35
		service.setRulerVisibility(new ChartRulerID("bar", "VALUE4", true),  true); // U-35
		service.setRulerDisplayPriority(new ChartRulerID("foo", "VALUE1", false), 4); // L+30=155 (start skipping)
		service.setRulerDisplayPriority(new ChartRulerID("foo", "VALUE1",  true), 7); // U+30=
		service.setRulerDisplayPriority(new ChartRulerID("foo", "VALUE2", false), 1); // L+40= 65
		service.setRulerDisplayPriority(new ChartRulerID("foo", "VALUE2",  true), 6); // U+40
		service.setRulerDisplayPriority(new ChartRulerID("bar", "VALUE3", false), 0); // L+25= 25
		service.setRulerDisplayPriority(new ChartRulerID("bar", "VALUE3",  true), 3); // U+25=125
		service.setRulerDisplayPriority(new ChartRulerID("bar", "VALUE4", false), 2); // L+35=100
		service.setRulerDisplayPriority(new ChartRulerID("bar", "VALUE4",  true), 5); // U+35=
		// Layout: 35|40|25|data space|25
		
		ChartSpaceLayout actual = service.prepareLayout(new Segment1D(0, 1000), 150, deviceStub);
		
		Segment1D expectedDataSpace = new Segment1D(100, 875);
		List<ChartRulerSpace> expectedRulers = new ArrayList<>();
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE3", false),
											   new Segment1D( 75, 25)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("foo", "VALUE2", false),
											   new Segment1D( 35, 40)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE4", false),
											   new Segment1D(  0, 35)));
		expectedRulers.add(new ChartRulerSpace(new ChartRulerID("bar", "VALUE3", true),
											   new Segment1D(975, 25)));
		ChartSpaceLayout expected = new ChartSpaceLayoutImpl(expectedDataSpace, expectedRulers);
		assertEquals(expected, actual);
	}

}
