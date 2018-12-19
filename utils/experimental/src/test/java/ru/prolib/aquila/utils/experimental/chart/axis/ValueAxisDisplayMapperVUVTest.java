package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ValueAxisDisplayMapperVUVTest {
	private ValueAxisDisplayMapperVUV mapper;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() {
		mapper = null;
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsWhenRatioLt1() {
		mapper = new ValueAxisDisplayMapperVUV(2, 5, new Range<>(of(0L), of(4L)));
	}
	
	@Test
	public void testCtor() {
		mapper = new ValueAxisDisplayMapperVUV(2, 5, new Range<>(of(0L), of(15L)));
		assertEquals(2, mapper.getPlotStart());
		assertEquals(5, mapper.getPlotSize());
		assertEquals(new Segment1D(2, 5), mapper.getPlot());
		assertEquals(new Range<CDecimal>(of(0L), of(15L)), mapper.getValueRange());
		assertEquals(AxisDirection.UP, mapper.getAxisDirection());
	}
	
	@Test
	public void testConvertation_SimpleCase() {
		mapper = new ValueAxisDisplayMapperVUV(2, 5, new Range<>(of(0L), of(15L)));
		assertEquals(of("3.00000"), mapper.getRatio()); // ratio=(15-0)/(6-1)=3
		
		assertEquals(6, mapper.toDisplay(of( 0L)));
		assertEquals(6, mapper.toDisplay(of( 1L)));
		assertEquals(6, mapper.toDisplay(of( 2L)));
		assertEquals(5, mapper.toDisplay(of( 3L)));
		assertEquals(5, mapper.toDisplay(of( 4L)));
		assertEquals(5, mapper.toDisplay(of( 5L)));
		assertEquals(4, mapper.toDisplay(of( 6L)));
		assertEquals(4, mapper.toDisplay(of( 7L)));
		assertEquals(4, mapper.toDisplay(of( 8L)));
		assertEquals(3, mapper.toDisplay(of( 9L)));
		assertEquals(3, mapper.toDisplay(of(10L)));
		assertEquals(3, mapper.toDisplay(of(11L)));
		assertEquals(2, mapper.toDisplay(of(12L)));
		assertEquals(2, mapper.toDisplay(of(13L)));
		assertEquals(2, mapper.toDisplay(of(14L)));
	}
	
	@Test
	public void testConvertation_Case1() {
		mapper = new ValueAxisDisplayMapperVUV(10, 190,
				new Range<>(of("75.1234"), of("447")));
		assertEquals( of("75.1234"), mapper.getMinValue());
		assertEquals(of("447"),		 mapper.getMaxValue()); // height=371.8766
		assertEquals(new Range<>(of("75.1234"), of("447")), mapper.getValueRange());
		assertEquals( 10, mapper.getPlotStart());
		assertEquals(190, mapper.getPlotSize());
		assertEquals(new Segment1D(10, 190), mapper.getPlot());
		assertEquals(of("1.957245264"), mapper.getRatio()); // 371.8766/190=1.957245264
		
		assertEquals(199, mapper.toDisplay( of("75.1234")));
		assertEquals( 10, mapper.toDisplay(of("447.0000")));
		assertEquals( 34, mapper.toDisplay(of("399.0027")));	// 399.0027-75.1234=323.8793/1.957245264=165
																// 10+190-1-165= 34
		assertEquals(181, mapper.toDisplay(of("112.09")));		// 112.09  -75.1234= 36.9666/1.957245264= 18
																// 10+190-1- 18=181
		
		assertEquals( of("75.1234"), mapper.toValue(199));
		assertEquals(of("445.0428"), mapper.toValue( 10));		// (190-1-( 10-10))*1.957245264+75.1234=445.0428
		assertEquals(of("398.0689"), mapper.toValue( 34));		// (190-1-( 34-10))*1.957245264+75.1234=398.0689
		assertEquals(of("110.3538"), mapper.toValue(181));		// (190-1-(181-10))*1.957245264+75.1234=110.3538
	}
	
	@Test
	public void testConvertation_Case2() {
		mapper = new ValueAxisDisplayMapperVUV(15, 205,
				new Range<>(of(61290L), of(69780L)));
		assertEquals(of(61290L), mapper.getMinValue());
		assertEquals(of(69780L), mapper.getMaxValue()); // height 8490
		assertEquals( 15, mapper.getPlotStart());
		assertEquals(205, mapper.getPlotSize());
		assertEquals(new Segment1D(15, 205), mapper.getPlot());
		assertEquals(of("41.41464"), mapper.getRatio());
		
		assertEquals(219, mapper.toDisplay(of(61290L)));
		assertEquals( 15, mapper.toDisplay(of(69780L)));
		assertEquals( 54, mapper.toDisplay(of(68150L)));	// 68150-61290=6860/41.41464=165.64191
															// 15+205-1-165= 54
		assertEquals(199, mapper.toDisplay(of(62120L)));	// 62120-61290= 830/41.41464= 20.04122
															// 15+205-1- 20=199
		
		assertEquals(of(61290L), mapper.toValue(219));
		assertEquals(of(69739L), mapper.toValue( 15));		// (205-1-( 15-15))*41.41464+61290=69739
		assertEquals(of(68123L), mapper.toValue( 54));		// (205-1-( 54-15))*41.41464+61290=68123
		assertEquals(of(62118L), mapper.toValue(199));
	}

	@Test
	public void testConvertation_Case3() {
		mapper = new ValueAxisDisplayMapperVUV(45, 223,
				new Range<>(of("250.13"), of("492.01")));
		assertEquals(of("250.13"), mapper.getMinValue());
		assertEquals(of("492.01"), mapper.getMaxValue()); // height 241.88
		assertEquals( 45, mapper.getPlotStart());
		assertEquals(223, mapper.getPlotSize());
		assertEquals(new Segment1D(45, 223), mapper.getPlot());
		assertEquals(of("1.0846637"), mapper.getRatio()); // ratio=1.0846637
		
		assertEquals(267, mapper.toDisplay(of("250.13")));
		assertEquals( 45, mapper.toDisplay(of("492.01")));
		assertEquals(217, mapper.toDisplay(of("305.29")));	// 305.29-250.13= 55.16/1.0846637= 50.8544722
															// 45+223-1- 50=217
		assertEquals( 55, mapper.toDisplay(of("481.15")));	// 481.15-250.13=231.02/1.0846637=212.9876754
															// 45+223-1-212=55
		
		assertEquals(of("250.13"), mapper.toValue(267));
		assertEquals(of("490.93"), mapper.toValue( 45));	// (223-1-( 45-45))*1.0846637+250.13=490.93
		assertEquals(of("435.61"), mapper.toValue( 96));	// (223-1-( 96-45))*1.0846637+250.13=435.61 
		assertEquals(of("259.89"), mapper.toValue(258));	// (223-1-(258-45))*1.0846637+250.13=259.89
	}

	@Test
	public void testEquals_SpecialCases() {
		mapper = new ValueAxisDisplayMapperVUV(45, 223,
				new Range<>(of("250.13"), of("492.01")));
		assertTrue(mapper.equals(mapper));
		assertFalse(mapper.equals(this));
		assertFalse(mapper.equals(null));
	}
	
	@Test
	public void testEquals() {
		mapper = new ValueAxisDisplayMapperVUV(45, 223, new Range<>(of("250.13"), of("492.01")));
		Variant<Integer> vY = new Variant<>(45, 40), vH = new Variant<>(vY, 223, 205);
		Variant<Range<CDecimal>> vRng = new Variant<>(vH);
		vRng.add(new Range<>(of("250.13"), of("492.01")));
		vRng.add(new Range<>(of("200.00"), of("800.00")));
		Variant<?> iterator = vRng;
		int foundCnt = 0;
		ValueAxisDisplayMapperVUV x, found = null;
		do {
			x = new ValueAxisDisplayMapperVUV(vY.get(), vH.get(), vRng.get());
			if ( mapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(45, found.getPlotStart());
		assertEquals(223, found.getPlotSize());
		assertEquals(new Range<>(of("250.13"), of("492.01")), found.getValueRange());
	}
	
	@Test
	public void testConvertation_TestIncorrectCoordForNegativeRange() {
		// 4 units per 1 pixel
		mapper = new ValueAxisDisplayMapperVUV(5, 10, new Range<>(of(-50L), of(0L)));
		
		assertEquals( 5, mapper.toDisplay(of(  0L)));
		assertEquals( 5, mapper.toDisplay(of(- 1L)));
		assertEquals( 5, mapper.toDisplay(of(- 2L)));
		assertEquals( 5, mapper.toDisplay(of(- 3L)));
		assertEquals( 5, mapper.toDisplay(of(- 4L)));
		assertEquals( 5, mapper.toDisplay(of(- 5L)));
		assertEquals( 6, mapper.toDisplay(of(- 6L)));
		assertEquals( 6, mapper.toDisplay(of(- 7L)));
		assertEquals( 6, mapper.toDisplay(of(- 8L)));
		assertEquals( 6, mapper.toDisplay(of(- 9L)));
		assertEquals( 6, mapper.toDisplay(of(-10L)));
		assertEquals( 7, mapper.toDisplay(of(-11L)));
		assertEquals( 7, mapper.toDisplay(of(-12L)));
		assertEquals( 7, mapper.toDisplay(of(-13L)));
		assertEquals( 7, mapper.toDisplay(of(-14L)));
		assertEquals( 7, mapper.toDisplay(of(-15L)));
		assertEquals( 8, mapper.toDisplay(of(-16L)));
		assertEquals( 8, mapper.toDisplay(of(-17L)));
		assertEquals( 8, mapper.toDisplay(of(-18L)));
		assertEquals( 8, mapper.toDisplay(of(-19L)));
		assertEquals( 8, mapper.toDisplay(of(-20L)));
		assertEquals( 9, mapper.toDisplay(of(-21L)));
		assertEquals( 9, mapper.toDisplay(of(-22L)));
		assertEquals( 9, mapper.toDisplay(of(-23L)));
		assertEquals( 9, mapper.toDisplay(of(-24L)));
		assertEquals( 9, mapper.toDisplay(of(-25L)));
		assertEquals(10, mapper.toDisplay(of(-26L)));
		assertEquals(10, mapper.toDisplay(of(-27L)));
		assertEquals(10, mapper.toDisplay(of(-28L)));
		assertEquals(10, mapper.toDisplay(of(-29L)));
		assertEquals(10, mapper.toDisplay(of(-30L)));
		assertEquals(11, mapper.toDisplay(of(-31L)));
		assertEquals(11, mapper.toDisplay(of(-32L)));
		assertEquals(11, mapper.toDisplay(of(-33L)));
		assertEquals(11, mapper.toDisplay(of(-34L)));
		assertEquals(11, mapper.toDisplay(of(-35L)));
		assertEquals(12, mapper.toDisplay(of(-36L)));
		assertEquals(12, mapper.toDisplay(of(-37L)));
		assertEquals(12, mapper.toDisplay(of(-38L)));
		assertEquals(12, mapper.toDisplay(of(-39L)));
		assertEquals(12, mapper.toDisplay(of(-40L)));
		assertEquals(13, mapper.toDisplay(of(-41L)));
		assertEquals(13, mapper.toDisplay(of(-42L)));
		assertEquals(13, mapper.toDisplay(of(-43L)));
		assertEquals(13, mapper.toDisplay(of(-44L)));
		assertEquals(13, mapper.toDisplay(of(-45L)));
		assertEquals(14, mapper.toDisplay(of(-46L)));
		assertEquals(14, mapper.toDisplay(of(-47L)));
		assertEquals(14, mapper.toDisplay(of(-48L)));
		assertEquals(14, mapper.toDisplay(of(-49L)));
		assertEquals(14, mapper.toDisplay(of(-50L)));
		
		assertEquals(of( -5L), mapper.toValue( 5));
		assertEquals(of(-10L), mapper.toValue( 6));
		assertEquals(of(-15L), mapper.toValue( 7));
		assertEquals(of(-20L), mapper.toValue( 8));
		assertEquals(of(-25L), mapper.toValue( 9));
		assertEquals(of(-30L), mapper.toValue(10));
		assertEquals(of(-35L), mapper.toValue(11));
		assertEquals(of(-40L), mapper.toValue(12));
		assertEquals(of(-45L), mapper.toValue(13));
		assertEquals(of(-50L), mapper.toValue(14));
	}
	
	@Test
	public void testConvertation_TestIncorrectCoordForPositiveRange() {
		// 4 units per 1 pixel
		mapper = new ValueAxisDisplayMapperVUV(5, 10, new Range<>(of(0L), of(50L)));
		
		assertEquals(14, mapper.toDisplay(of( 0L)));
		assertEquals(14, mapper.toDisplay(of( 1L)));
		assertEquals(14, mapper.toDisplay(of( 2L)));
		assertEquals(14, mapper.toDisplay(of( 3L)));
		assertEquals(14, mapper.toDisplay(of( 4L)));
		assertEquals(13, mapper.toDisplay(of( 5L)));
		assertEquals(13, mapper.toDisplay(of( 6L)));
		assertEquals(13, mapper.toDisplay(of( 7L)));
		assertEquals(13, mapper.toDisplay(of( 8L)));
		assertEquals(13, mapper.toDisplay(of( 9L)));
		assertEquals(12, mapper.toDisplay(of(10L)));
		assertEquals(12, mapper.toDisplay(of(11L)));
		assertEquals(12, mapper.toDisplay(of(12L)));
		assertEquals(12, mapper.toDisplay(of(13L)));
		assertEquals(12, mapper.toDisplay(of(14L)));
		assertEquals(11, mapper.toDisplay(of(15L)));
		assertEquals(11, mapper.toDisplay(of(16L)));
		assertEquals(11, mapper.toDisplay(of(17L)));
		assertEquals(11, mapper.toDisplay(of(18L)));
		assertEquals(11, mapper.toDisplay(of(19L)));
		assertEquals(10, mapper.toDisplay(of(20L)));
		assertEquals(10, mapper.toDisplay(of(21L)));
		assertEquals(10, mapper.toDisplay(of(22L)));
		assertEquals(10, mapper.toDisplay(of(23L)));
		assertEquals(10, mapper.toDisplay(of(24L)));
		assertEquals( 9, mapper.toDisplay(of(25L)));
		assertEquals( 9, mapper.toDisplay(of(26L)));
		assertEquals( 9, mapper.toDisplay(of(27L)));
		assertEquals( 9, mapper.toDisplay(of(28L)));
		assertEquals( 9, mapper.toDisplay(of(29L)));
		assertEquals( 8, mapper.toDisplay(of(30L)));
		assertEquals( 8, mapper.toDisplay(of(31L)));
		assertEquals( 8, mapper.toDisplay(of(32L)));
		assertEquals( 8, mapper.toDisplay(of(33L)));
		assertEquals( 8, mapper.toDisplay(of(34L)));
		assertEquals( 7, mapper.toDisplay(of(35L)));
		assertEquals( 7, mapper.toDisplay(of(36L)));
		assertEquals( 7, mapper.toDisplay(of(37L)));
		assertEquals( 7, mapper.toDisplay(of(38L)));
		assertEquals( 7, mapper.toDisplay(of(39L)));
		assertEquals( 6, mapper.toDisplay(of(40L)));
		assertEquals( 6, mapper.toDisplay(of(41L)));
		assertEquals( 6, mapper.toDisplay(of(42L)));
		assertEquals( 6, mapper.toDisplay(of(43L)));
		assertEquals( 6, mapper.toDisplay(of(44L)));
		assertEquals( 5, mapper.toDisplay(of(45L)));
		assertEquals( 5, mapper.toDisplay(of(46L)));
		assertEquals( 5, mapper.toDisplay(of(47L)));
		assertEquals( 5, mapper.toDisplay(of(48L)));
		assertEquals( 5, mapper.toDisplay(of(49L)));
		assertEquals( 5, mapper.toDisplay(of(50L)));
		
		assertEquals(of( 0L), mapper.toValue(14));
		assertEquals(of( 5L), mapper.toValue(13));
		assertEquals(of(10L), mapper.toValue(12));
		assertEquals(of(15L), mapper.toValue(11));
		assertEquals(of(20L), mapper.toValue(10));
		assertEquals(of(25L), mapper.toValue( 9));
		assertEquals(of(30L), mapper.toValue( 8));
		assertEquals(of(35L), mapper.toValue( 7));
		assertEquals(of(40L), mapper.toValue( 6));
		assertEquals(of(45L), mapper.toValue( 5));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConvertation_SpecialCase_ZeroRangeIsRestricted() throws Exception {
		new ValueAxisDisplayMapperVUV(5, 10, new Range<>(of("212.56"), of("212.56")));
	}

}
