package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.utils.Range;

public class BCHistogramLayerTest {
	private SeriesImpl<CDecimal> series;
	private BCHistogramLayer service;

	@Before
	public void setUp() throws Exception {
		series = new SeriesImpl<>("foo");
		service = new BCHistogramLayer(series);
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
		service.setParam(BCHistogramLayer.NEGATE_VALUES_PARAM, true);
		
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
		service.setParam(BCHistogramLayer.NEGATE_VALUES_PARAM, true);
		
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
		service.setParam(BCHistogramLayer.NEGATE_VALUES_PARAM, true);
		
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
		service.setParam(BCHistogramLayer.NEGATE_VALUES_PARAM, true);
		
		Range<CDecimal> actual = service.getValueRange(0,  series.getLength());
		
		Range<CDecimal> expected = new Range<>(of(0L), of(8L));
		assertEquals(expected, actual);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
