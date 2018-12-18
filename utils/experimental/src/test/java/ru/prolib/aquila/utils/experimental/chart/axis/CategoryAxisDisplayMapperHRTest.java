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
		
		assertEquals(0, mapper.toCategory( 5));
		assertEquals(0, mapper.toCategory( 6));
		assertEquals(0, mapper.toCategory( 7));
		assertEquals(0, mapper.toCategory( 8));
		assertEquals(1, mapper.toCategory( 9));
		assertEquals(1, mapper.toCategory(10));
		assertEquals(1, mapper.toCategory(11));
		assertEquals(1, mapper.toCategory(12));
		assertEquals(1, mapper.toCategory(13));
		assertEquals(2, mapper.toCategory(14));
		assertEquals(2, mapper.toCategory(15));
		assertEquals(2, mapper.toCategory(16));
		assertEquals(2, mapper.toCategory(17));
		assertEquals(3, mapper.toCategory(18));
		assertEquals(3, mapper.toCategory(19));
		assertEquals(3, mapper.toCategory(20));
		assertEquals(3, mapper.toCategory(21));
		assertEquals(4, mapper.toCategory(22));
		assertEquals(4, mapper.toCategory(23));
		assertEquals(4, mapper.toCategory(24));
		assertEquals(4, mapper.toCategory(25));
		assertEquals(4, mapper.toCategory(26));
		assertEquals(5, mapper.toCategory(27));
		assertEquals(5, mapper.toCategory(28));
		assertEquals(5, mapper.toCategory(29));
		assertEquals(5, mapper.toCategory(30));
		assertEquals(6, mapper.toCategory(31));
		assertEquals(6, mapper.toCategory(32));
		assertEquals(6, mapper.toCategory(33));
		assertEquals(6, mapper.toCategory(34));
		assertEquals(7, mapper.toCategory(35));
		assertEquals(7, mapper.toCategory(36));
		assertEquals(7, mapper.toCategory(37));
		assertEquals(7, mapper.toCategory(38));
		assertEquals(7, mapper.toCategory(39));
		assertEquals(8, mapper.toCategory(40));
		assertEquals(8, mapper.toCategory(41));
		assertEquals(8, mapper.toCategory(42));
		assertEquals(8, mapper.toCategory(43));
		assertEquals(9, mapper.toCategory(44));
		assertEquals(9, mapper.toCategory(45));
		assertEquals(9, mapper.toCategory(46));
		assertEquals(9, mapper.toCategory(47));
		assertEquals(9, mapper.toCategory(48));
	}
	
	@Test
	public void testCaseWhenViewportShiftedLeft() {
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
		
		// This should cover whole plot area
		assertEquals(-2, mapper.toCategory( 5));
		assertEquals(-2, mapper.toCategory( 6));
		assertEquals(-2, mapper.toCategory( 7));
		assertEquals(-2, mapper.toCategory( 8));
		assertEquals(-1, mapper.toCategory( 9));
		assertEquals(-1, mapper.toCategory(10));
		assertEquals(-1, mapper.toCategory(11));
		assertEquals(-1, mapper.toCategory(12));
		assertEquals(-1, mapper.toCategory(13));
		assertEquals(0, mapper.toCategory(14));
		assertEquals(0, mapper.toCategory(15));
		assertEquals(0, mapper.toCategory(16));
		assertEquals(0, mapper.toCategory(17));
		assertEquals(1, mapper.toCategory(18));
		assertEquals(1, mapper.toCategory(19));
		assertEquals(1, mapper.toCategory(20));
		assertEquals(1, mapper.toCategory(21));
		assertEquals(2, mapper.toCategory(22));
		assertEquals(2, mapper.toCategory(23));
		assertEquals(2, mapper.toCategory(24));
		assertEquals(2, mapper.toCategory(25));
		assertEquals(2, mapper.toCategory(26));
		assertEquals(3, mapper.toCategory(27));
		assertEquals(3, mapper.toCategory(28));
		assertEquals(3, mapper.toCategory(29));
		assertEquals(3, mapper.toCategory(30));
		assertEquals(4, mapper.toCategory(31));
		assertEquals(4, mapper.toCategory(32));
		assertEquals(4, mapper.toCategory(33));
		assertEquals(4, mapper.toCategory(34));
		assertEquals(5, mapper.toCategory(35));
		assertEquals(5, mapper.toCategory(36));
		assertEquals(5, mapper.toCategory(37));
		assertEquals(5, mapper.toCategory(38));
		assertEquals(5, mapper.toCategory(39));
		assertEquals(6, mapper.toCategory(40));
		assertEquals(6, mapper.toCategory(41));
		assertEquals(6, mapper.toCategory(42));
		assertEquals(6, mapper.toCategory(43));
		assertEquals(7, mapper.toCategory(44));
		assertEquals(7, mapper.toCategory(45));
		assertEquals(7, mapper.toCategory(46));
		assertEquals(7, mapper.toCategory(47));
		assertEquals(7, mapper.toCategory(48));
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

		assertEquals( 2, mapper.toCategory(10));
		assertEquals( 2, mapper.toCategory(11));
		assertEquals( 2, mapper.toCategory(12));
		assertEquals( 2, mapper.toCategory(13));
		assertEquals( 3, mapper.toCategory(14));
		assertEquals( 3, mapper.toCategory(15));
		assertEquals( 3, mapper.toCategory(16));
		assertEquals( 3, mapper.toCategory(17));
		assertEquals( 3, mapper.toCategory(18));
		assertEquals( 4, mapper.toCategory(19));
		assertEquals( 4, mapper.toCategory(20));
		assertEquals( 4, mapper.toCategory(21));
		assertEquals( 4, mapper.toCategory(22));
		assertEquals( 5, mapper.toCategory(23));
		assertEquals( 5, mapper.toCategory(24));
		assertEquals( 5, mapper.toCategory(25));
		assertEquals( 5, mapper.toCategory(26));
		assertEquals( 6, mapper.toCategory(27));
		assertEquals( 6, mapper.toCategory(28));
		assertEquals( 6, mapper.toCategory(29));
		assertEquals( 6, mapper.toCategory(30));
		assertEquals( 6, mapper.toCategory(31));
		assertEquals( 7, mapper.toCategory(32));
		assertEquals( 7, mapper.toCategory(33));
		assertEquals( 7, mapper.toCategory(34));
		assertEquals( 7, mapper.toCategory(35));
		assertEquals( 8, mapper.toCategory(36));
		assertEquals( 8, mapper.toCategory(37));
		assertEquals( 8, mapper.toCategory(38));
		assertEquals( 8, mapper.toCategory(39));
		assertEquals( 9, mapper.toCategory(40));
		assertEquals( 9, mapper.toCategory(41));
		assertEquals( 9, mapper.toCategory(42));
		assertEquals( 9, mapper.toCategory(43));
		assertEquals( 9, mapper.toCategory(44));
		assertEquals(10, mapper.toCategory(45));
		assertEquals(10, mapper.toCategory(46));
		assertEquals(10, mapper.toCategory(47));
		assertEquals(10, mapper.toCategory(48));
		// Can't test #11 because out of chart
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

		assertEquals(0, mapper.toCategory( 5));
		assertEquals(1, mapper.toCategory( 6));
		assertEquals(2, mapper.toCategory( 7));
		assertEquals(3, mapper.toCategory( 8));
		assertEquals(4, mapper.toCategory( 9));
		assertEquals(5, mapper.toCategory(10));
		assertEquals(6, mapper.toCategory(11));
		assertEquals(7, mapper.toCategory(12));
		assertEquals(8, mapper.toCategory(13));
		assertEquals(9, mapper.toCategory(14));
	}
	
	@Test
	public void testCase_WhenBarIndexIsLessThanFirstVisibleCategory() {
		mapper = new CategoryAxisDisplayMapperHR(30, 500, 10, of("3.1547"));
		
		assertEquals(new Segment1D(39,  4), mapper.toDisplay(503));
		assertEquals(503, mapper.toCategory(39));
		assertEquals(503, mapper.toCategory(40));
		assertEquals(503, mapper.toCategory(41));
		assertEquals(503, mapper.toCategory(42));
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
	
	@Test (expected=IllegalArgumentException.class)
	public void testToCategory_ThrowsIfCoordAtLeftOfDisplayArea() {
		mapper = new CategoryAxisDisplayMapperHR(10, 2, 10, of("4.352"));
		mapper.toCategory(7);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToCategory_ThrowsIfCoordAtRightOfDisplayArea() {
		mapper = new CategoryAxisDisplayMapperHR(10, 2, 10, of("4.352"));
		mapper.toCategory(70);
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
