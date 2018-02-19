package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelGenerator;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapperImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelSize;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SW2MTFAdapterImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWLabelDimensions;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SW2MTFAdapterImpl.*;

public class SW2MTFAdapterTest {
	private static ZoneId MSK = ZoneId.of("Europe/Moscow"), UTC = ZoneId.of("UTC");
	private IMocksControl control;
	private MTFLabelGenerator generatorMock;
	private SWLabelDimensions labelDimensionsMock;
	private CategoryAxisDriver driver;
	private CategoryAxisViewport viewport;
	private CategoryAxisDisplayMapper mapper;
	private SW2MTFAdapterImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		generatorMock = control.createMock(MTFLabelGenerator.class);
		labelDimensionsMock = control.createMock(SWLabelDimensions.class);
		driver = new CategoryAxisDriverImpl("X", AxisDirection.RIGHT);
		viewport = new CategoryAxisViewportImpl();
		service = new SW2MTFAdapterImpl(generatorMock);
	}
	
	@After
	public void tearDown() throws Exception {
		mapper = null;
		service = null;
	}
	
	@Test
	public void testCtor1() {
		assertSame(generatorMock, service.getLabelGenerator());
	}
	
	@Test
	public void testCtor0() {
		service = new SW2MTFAdapterImpl();
		assertSame(MTFLabelGenerator.getInstance(), service.getLabelGenerator());
	}
	
	@Test
	public void testLabelWidth_GetVisibleSize() {
		LabelWidth service = new LabelWidth(labelDimensionsMock);
		expect(labelDimensionsMock.getLabelWidth("foo")).andReturn(29);
		control.replay();
		
		assertEquals(29, service.getVisibleSize("foo"));
		
		control.verify();
	}
	
	@Test
	public void testLabelWidth_Equals() {
		LabelWidth service = new LabelWidth(labelDimensionsMock);
		assertTrue(service.equals(service));
		assertTrue(service.equals(new LabelWidth(labelDimensionsMock)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testLabelHeight_GetVisibleSize() {
		LabelHeight service = new LabelHeight(labelDimensionsMock);
		expect(labelDimensionsMock.getLabelHeight("bar")).andReturn(9);
		control.replay();
		
		assertEquals(9, service.getVisibleSize("bar"));
		
		control.verify();
	}
	
	@Test
	public void testLabelHeight_Equals() {
		LabelHeight service = new LabelHeight(labelDimensionsMock);
		assertTrue(service.equals(service));
		assertTrue(service.equals(new LabelHeight(labelDimensionsMock)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testSetting_TFMinutes_Getters() {
		viewport.setCategoryRangeByFirstAndNumber(5, 10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		Setting service = new Setting(mapper, ZTFrame.M5, labelDimensionsMock);
		
		assertEquals(UTC, service.getZoneID());
		assertEquals(20, service.getBarSize());
		assertEquals(5, service.getPeriod());
		assertEquals(new LabelWidth(labelDimensionsMock), service.getLabelSize());
		assertTrue(service.isSupportedTFrame());
	}
	
	@Test
	public void testSetting_TFHours_Getters() {
		viewport.setCategoryRangeByFirstAndNumber(0,  10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 100), viewport);
		Setting service = new Setting(mapper, ZTFrame.H1MSK, labelDimensionsMock);
		
		assertEquals(MSK, service.getZoneID());
		assertEquals(10, service.getBarSize());
		assertEquals(60, service.getPeriod());
		assertEquals(new LabelWidth(labelDimensionsMock), service.getLabelSize());
		assertTrue(service.isSupportedTFrame());
	}
	
	@Test
	public void testSetting_TFDays_Getters() {
		viewport.setCategoryRangeByFirstAndNumber(50,  20);
		viewport.setPreferredNumberOfBars(20);
		mapper = driver.createMapper(new Segment1D(0, 100), viewport);
		Setting service = new Setting(mapper, ZTFrame.D1, labelDimensionsMock);
		
		assertEquals(UTC, service.getZoneID());
		assertEquals(5, service.getBarSize());
		assertEquals(new LabelWidth(labelDimensionsMock), service.getLabelSize());
		assertFalse(service.isSupportedTFrame());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetting_FDays_GetPeriodThrows() {
		viewport.setCategoryRangeByFirstAndNumber(50,  20);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 100), viewport);
		Setting service = new Setting(mapper, ZTFrame.D1, labelDimensionsMock);

		service.getPeriod();
	}
	
	@Test
	public void testSetting_Equals_SpecialCases() {
		viewport.setCategoryRangeByFirstAndNumber(5, 10);
		viewport.setPreferredNumberOfBars(10);
		mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		Setting service = new Setting(mapper, ZTFrame.M5, labelDimensionsMock);

		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testSetting_Equals() {
		Setting service = new Setting(ZTFrame.M15, 10, new LabelWidth(labelDimensionsMock));
		Variant<ZTFrame> vTF = new Variant<>(ZTFrame.M15, ZTFrame.H1);
		Variant<Integer> vBS = new Variant<>(vTF, 10, 20);
		Variant<MTFLabelSize> vLS = new Variant<MTFLabelSize>(vBS)
				.add(new LabelWidth(labelDimensionsMock))
				.add(new LabelHeight(labelDimensionsMock));
		Variant<?> iterator = vLS;
		int foundCnt = 0;
		Setting x, found = null;
		do {
			x = new Setting(vTF.get(), vBS.get(), vLS.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertTrue(found.isSupportedTFrame());
		assertEquals(15, found.getPeriod());
		assertEquals(10, found.getBarSize());
		assertEquals(new LabelWidth(labelDimensionsMock), found.getLabelSize());
	}
	
	@Test
	public void testGetLabelMapper_SupportedTFrame() {
		viewport.setCategoryRangeByFirstAndNumber(5, 20);
		viewport.setPreferredNumberOfBars(15);
		mapper = driver.createMapper(new Segment1D(10,  160), viewport);
		List<MTFLabel> resultLabels1 = new ArrayList<>();
		resultLabels1.add(new MTFLabel(LocalTime.of(10, 0), "10h", true));
		resultLabels1.add(new MTFLabel(LocalTime.of(10, 5), ":05", false));
		expect(generatorMock.getIntradayLabels(10, 5, new LabelWidth(labelDimensionsMock)))
			.andReturn(resultLabels1);
		control.replay();
		
		MTFLabelMapper actual1 = service.getLabelMapper(mapper, ZTFrame.M5MSK, labelDimensionsMock);
		
		control.verify();
		MTFLabelMapper expected = new MTFLabelMapperImpl(MSK, resultLabels1);
		assertEquals(expected, actual1);
		
		// Get cached
		assertSame(actual1, service.getLabelMapper(mapper, ZTFrame.M5MSK, labelDimensionsMock));
		
		// Reset cache
		control.resetToStrict();
		viewport.setCategoryRangeByFirstAndNumber(1, 777); // that does not matter
		viewport.setPreferredNumberOfBars(10); // bar size will change
		mapper = driver.createMapper(new Segment1D(10,  160), viewport);
		List<MTFLabel> resultLabels2 = new ArrayList<>();
		resultLabels2.add(new MTFLabel(LocalTime.of(12, 55), "12:55", false));
		resultLabels2.add(new MTFLabel(LocalTime.of(13,  0),   "13h", true));
		expect(generatorMock.getIntradayLabels(16, 5, new LabelWidth(labelDimensionsMock)))
			.andReturn(resultLabels2);
		control.replay();
		
		MTFLabelMapper actual2 = service.getLabelMapper(mapper, ZTFrame.M5MSK, labelDimensionsMock);
		
		control.verify();
		assertNotSame(actual2, actual1);
		expected = new MTFLabelMapperImpl(MSK, resultLabels2);
		assertEquals(expected, actual2);
	}
	
	@Test
	public void testGetLabelMapper_UnsupportedTFrame() {
		viewport.setCategoryRangeByFirstAndNumber(5, 20);
		viewport.setPreferredNumberOfBars(15);
		mapper = driver.createMapper(new Segment1D(10,  160), viewport);
		control.replay();
		
		MTFLabelMapper actual1 = service.getLabelMapper(mapper, ZTFrame.D1, labelDimensionsMock);
		
		control.verify();
		List<MTFLabel> resultLabels1 = new ArrayList<>();
		resultLabels1.add(new MTFLabel(LocalTime.MIDNIGHT, "0h", true));
		MTFLabelMapper expected = new MTFLabelMapperImpl(UTC, resultLabels1);
		assertEquals(expected, actual1);
		
		// Reset cache, ask generator for labels
		control.resetToStrict();
		viewport.setCategoryRangeByFirstAndNumber(1, 777); // that does not matter
		viewport.setPreferredNumberOfBars(10); // bar size will change
		mapper = driver.createMapper(new Segment1D(10,  160), viewport);
		List<MTFLabel> resultLabels2 = new ArrayList<>();
		resultLabels2.add(new MTFLabel(LocalTime.of(12, 55), "12:55", false));
		resultLabels2.add(new MTFLabel(LocalTime.of(13,  0),   "13h", true));
		expect(generatorMock.getIntradayLabels(16, 5, new LabelWidth(labelDimensionsMock)))
			.andReturn(resultLabels2);
		control.replay();
		
		MTFLabelMapper actual2 = service.getLabelMapper(mapper, ZTFrame.M5MSK, labelDimensionsMock);

		control.verify();
		assertNotSame(actual2, actual1);
		expected = new MTFLabelMapperImpl(MSK, resultLabels2);
		assertEquals(expected, actual2);

		// Reset cache, return back to labels of unsupported TF
		control.resetToStrict();
		control.replay();
		
		MTFLabelMapper actual3 = service.getLabelMapper(mapper, ZTFrame.D1MSK, labelDimensionsMock);
		
		control.verify();
		assertNotSame(actual3, actual2);
		expected = new MTFLabelMapperImpl(MSK, resultLabels1);
		assertEquals(expected, actual3);
	}

}
