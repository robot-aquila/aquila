package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDisplayMapperHRTest {
	private CategoryAxisDisplayMapperHR mapper;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() {
		mapper = null;
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsWhenBarWidthLt1() {
		new CategoryAxisDisplayMapperHR(5, 0, 10, of("0.99"));
	}
	
	@Test
	public void testCaseWhenViewportAtStart() {
		mapper = new CategoryAxisDisplayMapperHR(5, 0, 10, of("4.352"));
		
		assertEquals(AxisDirection.RIGHT, mapper.getAxisDirection());
		assertEquals(10, mapper.getNumberOfVisibleBars());
		assertEquals(0, mapper.getFirstVisibleCategory());
		assertEquals(9, mapper.getLastVisibleCategory());
		assertEquals(10, mapper.getNumberOfVisibleCategories());
		assertEquals(5, mapper.getPlotStart());
		assertEquals(44, mapper.getPlotSize());
		assertEquals(new Segment1D(5, 44), mapper.getPlot());
		
		assertEquals(new Segment1D( 5, 4), mapper.toDisplay(0));
		assertEquals(new Segment1D( 9, 5), mapper.toDisplay(1));
		assertEquals(new Segment1D(14, 4), mapper.toDisplay(2));
		assertEquals(new Segment1D(18, 4), mapper.toDisplay(3));
		assertEquals(new Segment1D(22, 5), mapper.toDisplay(4));
		assertEquals(new Segment1D(27, 4), mapper.toDisplay(5));
		assertEquals(new Segment1D(31, 4), mapper.toDisplay(6));
		assertEquals(new Segment1D(35, 5), mapper.toDisplay(7));
		assertEquals(new Segment1D(40, 4), mapper.toDisplay(8));
		assertEquals(new Segment1D(44, 5), mapper.toDisplay(9));
		
		// TODO: test toCategory method
	}
	
	@Test
	public void testCaseWhenViewportShifterLeft() {
		mapper = new CategoryAxisDisplayMapperHR(5, -2, 10, of("4.352"));
		
		assertEquals(AxisDirection.RIGHT, mapper.getAxisDirection());
		assertEquals(10, mapper.getNumberOfVisibleBars());
		assertEquals(0, mapper.getFirstVisibleCategory());
		assertEquals(7, mapper.getLastVisibleCategory());
		assertEquals(8, mapper.getNumberOfVisibleCategories());
		assertEquals(5, mapper.getPlotStart());
		assertEquals(44, mapper.getPlotSize());
		assertEquals(new Segment1D(5, 44), mapper.getPlot());
		
		assertEquals(new Segment1D(14, 4), mapper.toDisplay(0));
		assertEquals(new Segment1D(18, 4), mapper.toDisplay(1));
		assertEquals(new Segment1D(22, 5), mapper.toDisplay(2));
		assertEquals(new Segment1D(27, 4), mapper.toDisplay(3));
		assertEquals(new Segment1D(31, 4), mapper.toDisplay(4));
		assertEquals(new Segment1D(35, 5), mapper.toDisplay(5));
		assertEquals(new Segment1D(40, 4), mapper.toDisplay(6));
		assertEquals(new Segment1D(44, 5), mapper.toDisplay(7));
		
		// TODO: test toCategory method
	}
	
	@Test
	public void testCaseWhenViewportShiftedRight() {
		mapper = new CategoryAxisDisplayMapperHR(10, 2, 10, of("4.352"));
		
		assertEquals(AxisDirection.RIGHT, mapper.getAxisDirection());
		assertEquals(10, mapper.getNumberOfVisibleBars());
		assertEquals(2, mapper.getFirstVisibleCategory());
		assertEquals(11, mapper.getLastVisibleCategory());
		assertEquals(10, mapper.getNumberOfVisibleCategories());
		assertEquals(10, mapper.getPlotStart());
		assertEquals(44, mapper.getPlotSize());
		assertEquals(new Segment1D(10, 44), mapper.getPlot());

		assertEquals(new Segment1D(10, 4), mapper.toDisplay(2));
		assertEquals(new Segment1D(14, 5), mapper.toDisplay(3));
		assertEquals(new Segment1D(19, 4), mapper.toDisplay(4));
		assertEquals(new Segment1D(23, 4), mapper.toDisplay(5));
		assertEquals(new Segment1D(27, 5), mapper.toDisplay(6));
		assertEquals(new Segment1D(32, 4), mapper.toDisplay(7));
		assertEquals(new Segment1D(36, 4), mapper.toDisplay(8));
		assertEquals(new Segment1D(40, 5), mapper.toDisplay(9));
		assertEquals(new Segment1D(45, 4), mapper.toDisplay(10));
		assertEquals(new Segment1D(49, 5), mapper.toDisplay(11));

		// TODO: test toCategory method
	}
	
	@Test
	public void testCase_WhenBarWidthIsOnePx() {
		mapper = new CategoryAxisDisplayMapperHR(5, 0, 10, of(1L));
		assertEquals(5, mapper.getPlotStart());
		assertEquals(10, mapper.getPlotSize());
		assertEquals(new Segment1D(5, 10), mapper.getPlot());
		
		assertEquals(new Segment1D( 5, 1), mapper.toDisplay(0));
		assertEquals(new Segment1D( 6, 1), mapper.toDisplay(1));
		assertEquals(new Segment1D( 7, 1), mapper.toDisplay(2));
		assertEquals(new Segment1D( 8, 1), mapper.toDisplay(3));
		assertEquals(new Segment1D( 9, 1), mapper.toDisplay(4));
		assertEquals(new Segment1D(10, 1), mapper.toDisplay(5));
		assertEquals(new Segment1D(11, 1), mapper.toDisplay(6));
		assertEquals(new Segment1D(12, 1), mapper.toDisplay(7));
		assertEquals(new Segment1D(13, 1), mapper.toDisplay(8));
		assertEquals(new Segment1D(14, 1), mapper.toDisplay(9));

		// TODO: test toCategory method
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToDisplay_ThrowsIfCategoryLtFirstVisible() {
		mapper = new CategoryAxisDisplayMapperHR(10, 2, 10, of("4.352"));
		mapper.toDisplay(0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToDisplay_ThrowsIfCategoryGtLastVisible() {
		mapper = new CategoryAxisDisplayMapperHR(10, 2, 10, of("4.352"));
		mapper.toDisplay(12);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		mapper = new CategoryAxisDisplayMapperHR(5, -2, 10, of("4.352"));
		assertTrue(mapper.equals(mapper));
		assertFalse(mapper.equals(null));
		assertFalse(mapper.equals(this));
	}
	
	@Test
	public void testEquals() {
		mapper = new CategoryAxisDisplayMapperHR(5, -2, 10, of("4.352"));
		Variant<Integer> vFirstBarX = new Variant<>(5, 15),
				vFirstBarIdx = new Variant<>(vFirstBarX, -2, 100),
				vNumBars = new Variant<>(vFirstBarIdx, 10, 50);
		Variant<CDecimal> vBarW = new Variant<>(vNumBars, of("4.352"), of("9.27"));
		Variant<?> iterator = vBarW;
		int foundCnt = 0;
		CategoryAxisDisplayMapperHR x, found = null;
		do {
			x = new CategoryAxisDisplayMapperHR(vFirstBarX.get(),
					vFirstBarIdx.get(),
					vNumBars.get(),
					vBarW.get());
			if ( mapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(AxisDirection.RIGHT, found.getAxisDirection());
		assertEquals(10, found.getNumberOfVisibleBars());
		assertEquals(0, found.getFirstVisibleCategory());
		assertEquals(7, found.getLastVisibleCategory());
		assertEquals(8, found.getNumberOfVisibleCategories());
		assertEquals(new Segment1D(14, 4), found.toDisplay(0));
	}

}
