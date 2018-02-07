package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDriverImplTest {
	private CategoryAxisDriverImpl driver;
	private CategoryAxisViewport viewport;

	@Before
	public void setUp() throws Exception {
		driver = new CategoryAxisDriverImpl(AxisDirection.RIGHT);
		viewport = new CategoryAxisViewportImpl();
		viewport.setCategoryRangeByFirstAndNumber(4, 3);
	}
	
	@Test
	public void testCtor() {
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_WhenDirectionIsUp() {
		new CategoryAxisDriverImpl(AxisDirection.UP);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetAxisDirection_WhenDirectionIsUp() {
		driver.setAxisDirection(AxisDirection.UP);
	}
	
	@Test
	public void testCreateMapper_Right_NumOfCategoriesLtNumOfBars() {
		viewport.setCategoryRangeByFirstAndNumber(0, 5);
		viewport.setPreferredNumberOfBars(10);

		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				-5, 10, CDecimalBD.of("2.00000"));
		assertEquals(expected, actual);
		assertEquals(4, expected.getLastVisibleCategory());
	}
	
	@Test
	public void testCreateMapper_Right_WhenPlotAreaHasAnOffset() {
		viewport.setCategoryRangeByFirstAndNumber(0, 5);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(10, 12), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(10,
				0, 5, CDecimalBD.of("2.40000"));
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateMapper_Right_NumOfCategoriesEqNumOfBars() {
		viewport.setCategoryRangeByFirstAndNumber(9, 10);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				9, 10, CDecimalBD.of("2.00000"));
		assertEquals(expected, actual);
		assertEquals(18, expected.getLastVisibleCategory());
	}
	
	@Test
	public void testCreateMapper_Right_NumOfCategoriesGtNumOfBars() {
		// The case wants to show up to 30 bars but has space only for 20 bars
		viewport.setCategoryRangeByFirstAndNumber(50, 30);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);

		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				60, 20, CDecimalBD.of("1.00000"));
		assertEquals(expected, actual);
		assertEquals(79, expected.getLastVisibleCategory());
	}

	@Test
	public void testCreateMapper_Right_PreferredNumberOfBarsHasMorePriorityThanNumOfCategories() {
		viewport.setCategoryRangeByFirstAndNumber(50, 20);
		viewport.setPreferredNumberOfBars(7);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				63, 7, CDecimalBD.of("2.85714"));
		assertEquals(expected, actual);
		assertEquals(69, expected.getLastVisibleCategory());
	}
}
