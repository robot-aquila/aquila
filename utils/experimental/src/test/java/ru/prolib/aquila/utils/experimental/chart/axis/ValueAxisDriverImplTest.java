package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ValueAxisDriverImplTest {
	private ValueAxisDriverImpl driver;
	private ValueAxisViewport viewport;

	@Before
	public void setUp() throws Exception {
		driver = new ValueAxisDriverImpl(AxisDirection.UP);
		viewport = new ValueAxisViewportImpl();
		viewport.setValueRange(new Range<>(of(1000L), of(2000L)));
	}
	
	@Test
	public void testCtor() {
		assertEquals(AxisDirection.UP, driver.getAxisDirection());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_WhenDirectionIsRight() {
		new ValueAxisDriverImpl(AxisDirection.RIGHT);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetAxisDirection_WhenDirectionIsRight() {
		driver.setAxisDirection(AxisDirection.RIGHT);
	}
	
	@Test
	public void testCreateMapper_Up_ValueAreaGtDisplay() {
		ValueAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 100), viewport);

		ValueAxisDisplayMapper expected = new ValueAxisDisplayMapperVUV(0, 100,
				new Range<>(of(1000L), of(2000L)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdaterImpl_Up_DisplayGtValueArea() {
		viewport.setValueRange(new Range<>(of(10L), of(20L)));

		ValueAxisDisplayMapper actual = driver.createMapper(new Segment1D(10, 80), viewport);
		
		ValueAxisDisplayMapper expected = new ValueAxisDisplayMapperVUD(10, 80,
				new Range<>(of(10L), of(20L)));
		assertEquals(expected, actual);
	}
	
}
