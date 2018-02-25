package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ValueAxisDisplayMapperVUDTest {
	private ValueAxisDisplayMapperVUD mapper;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() {
		mapper = null;
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_ThrowsWhenRatioLt1() {
		// TODO: there are cases when ratio<1 but must be OK
		new ValueAxisDisplayMapperVUD(0, 10, new Range<>(of(0L), of(11L)));
	}
	
	@Test
	public void testCtor() {
		mapper = new ValueAxisDisplayMapperVUD(2, 6, new Range<>(of(0L), of(2L)));
		assertEquals(2, mapper.getPlotStart());
		assertEquals(6, mapper.getPlotSize());
		assertEquals(new Segment1D(2, 6), mapper.getPlot());
		assertEquals(new Range<CDecimal>(of(0L), of(2L)), mapper.getValueRange());
		assertEquals(AxisDirection.UP, mapper.getAxisDirection());
	}
	
	@Test
	public void testConvertation_SimpleCase1() {
		mapper = new ValueAxisDisplayMapperVUD(2, 6, new Range<>(of(2L), of(5L)));
		
		assertEquals(of("1.71428"), mapper.getRatio());
		
		assertEquals(7, mapper.toDisplay(of(2L)));
		assertEquals(6, mapper.toDisplay(of("2.5")));
		assertEquals(5, mapper.toDisplay(of(3L)));
		assertEquals(4, mapper.toDisplay(of("3.5")));
		assertEquals(4, mapper.toDisplay(of(4L)));
		assertEquals(3, mapper.toDisplay(of("4.5")));
		assertEquals(2, mapper.toDisplay(of(5L)));
	}
	
	@Test
	public void testConvertation_SimpleCase2() {
		mapper = new ValueAxisDisplayMapperVUD(2, 7, new Range<>(of(0L), of(2L)));
		
		// height = 2
		// per pixel: 2 / 7 = 0.28571
		// fix range: 2 + 0.28571 = 2.28571 <- fixed range to calculate ratio
		// exp.ratio: 7 / 2.28571 = 3.0625057421982666217499157810921 -> 3.06250 
		assertEquals(of("3.06250"), mapper.getRatio());
		
		assertEquals(2, mapper.toDisplay(of("2.0")));
		assertEquals(2, mapper.toDisplay(of("1.9")));
		assertEquals(2, mapper.toDisplay(of("1.8")));
		assertEquals(3, mapper.toDisplay(of("1.7")));
		assertEquals(3, mapper.toDisplay(of("1.6")));
		assertEquals(3, mapper.toDisplay(of("1.5")));
		assertEquals(4, mapper.toDisplay(of("1.4")));
		assertEquals(4, mapper.toDisplay(of("1.3")));
		assertEquals(4, mapper.toDisplay(of("1.2")));
		assertEquals(5, mapper.toDisplay(of("1.1")));
		assertEquals(5, mapper.toDisplay(of("1.0")));
		assertEquals(5, mapper.toDisplay(of("0.9")));
		assertEquals(6, mapper.toDisplay(of("0.8")));
		assertEquals(6, mapper.toDisplay(of("0.7")));
		assertEquals(6, mapper.toDisplay(of("0.6")));
		assertEquals(6, mapper.toDisplay(of("0.5")));
		assertEquals(7, mapper.toDisplay(of("0.4")));
		assertEquals(7, mapper.toDisplay(of("0.3")));
		assertEquals(7, mapper.toDisplay(of("0.2")));
		assertEquals(8, mapper.toDisplay(of("0.1")));
		assertEquals(8, mapper.toDisplay(of("0.0")));
	}
	
	@Test
	public void testConvertation_Case1() {
		mapper = new ValueAxisDisplayMapperVUD(10, 200,
				new Range<>(of("100.5"), of("145.15")));
		assertEquals(of("100.5"),  mapper.getMinValue());
		assertEquals(of("145.15"), mapper.getMaxValue()); // height 44.65 
		assertEquals(new Range<>(of("100.5"), of("145.15")), mapper.getValueRange());
		assertEquals( 10, mapper.getPlotStart());
		assertEquals(200, mapper.getPlotSize());
		assertEquals(new Segment1D(10, 200), mapper.getPlot());
		// per pixel: 44.65 / 200 = 0.22325
		// fix range: 44.65 + 0.22325 = 44.87325 <- fixed range to calculate ratio
		// exp.ratio: 200 / 44.87325 = 4.4569983230543809507891672655758 -> 4.4569983 
		assertEquals(of("4.4569983"), mapper.getRatio());
		
		assertEquals(209, mapper.toDisplay(of("100.5")));
		assertEquals( 10, mapper.toDisplay(of("145.15")));
		assertEquals(122, mapper.toDisplay(of("120.03")));	// 120.03-100.5=19.53*4.4569983= 87.0451768
															// 10+200-1- 87=122
		assertEquals( 11, mapper.toDisplay(of("145.00")));	// 145.00-100.5=44.5 *4.4569983=198.3364243
															// 10+200-1-198= 11
		assertEquals( 20, mapper.toDisplay(of("142.91")));	// 142.91-100.5=42.41*4.4569983=189.0212979
															// 10+200-1-189= 20
		assertEquals( 48, mapper.toDisplay(of("136.57")));	// 136.57-100.5=36.07*4.4569983=160.7639286
															// 10+200-1-161= 48

		assertEquals(of("100.50"), mapper.toValue(209));
		assertEquals(of("100.72"), mapper.toValue(208));	// (200-1-(208-10))/4.4569983+100.5=100.72
		assertEquals(208, mapper.toDisplay(of("100.72")));	// additional test
		assertEquals(of("102.52"), mapper.toValue(200));	// (200-1-(200-10))/4.4569983+100.5=102.52
		assertEquals(of("108.80"), mapper.toValue(172));	// (200-1-(172-10))/4.4569983+100.5=108.80
		assertEquals(of("125.63"), mapper.toValue( 97));	// (200-1-( 97-10))/4.4569983+100.5=125.63
		assertEquals(of("144.92"), mapper.toValue( 11));	// (200-1-( 11-10))/4.4569983+100.5=144.92
		assertEquals(of("145.15"), mapper.toValue( 10));
	}
	
	@Test
	public void testConvertation_Case2() {
		mapper = new ValueAxisDisplayMapperVUD(45, 55, new Range<>(of("48.213"), of("97.42")));
		assertEquals(of("48.213"), mapper.getMinValue());
		assertEquals(of("97.42"), mapper.getMaxValue()); // height 49.207
		assertEquals(45, mapper.getPlotStart());
		assertEquals(55, mapper.getPlotSize());
		assertEquals(new Segment1D(45, 55), mapper.getPlot());
		// per pixel: 49.207 / 55 = 0.89467273
		// fix range: 49.207 + 0.89467273 = 50.10167273 <- fixed range to calculate ratio
		// exp.ratio: 55 / 50.10167273 = 1.0977677391411119059457398679152 -> 1.09776773 (not 4 cuz round down!)
		assertEquals(of("1.09776773"), mapper.getRatio());

		assertEquals( 99, mapper.toDisplay(of("48.213")));
		assertEquals( 45, mapper.toDisplay(of("97.42")));
		assertEquals( 97, mapper.toDisplay(of("50")));		// 50.000-48.213= 1.787*1.09776773= 1.96171093
															// 45+55-1- 2=97
		assertEquals( 60, mapper.toDisplay(of("84.001")));	// 84.001-48.213=35.788*1.09776773=39.28691152
															// 45+55-1-39=60

		assertEquals(of("48.213"), mapper.toValue(99));
		assertEquals(of("97.404"), mapper.toValue(45));		// (55-1-(45-45))/1.09776773+48.213=97.404
		assertEquals(of("50.035"), mapper.toValue(97));		// (55-1-(97-45))/1.09776773+48.213=50.035
		assertEquals(of("83.740"), mapper.toValue(60));		// (55-1-(60-45))/1.09776773+48.213=83.740
	}
	
	@Test
	public void testConvertation_Case3() {
		mapper = new ValueAxisDisplayMapperVUD(100, 400, new Range<>(of(90L), of(450L)));
		assertEquals( of(90L), mapper.getMinValue());
		assertEquals(of(450L), mapper.getMaxValue()); // height 360
		assertEquals(100, mapper.getPlotStart());
		assertEquals(400, mapper.getPlotSize());
		assertEquals(new Segment1D(100, 400), mapper.getPlot());
		
		// per pixel: 360 / 400 = 0.9
		// fix range: 360 + 0.9 = 360.9 <- fixed range to calculate ratio
		// exp.ratio: 400 / 360.9 = 1.1083402604599612080908839013577 -> 1.10834
		assertEquals(of("1.10834"), mapper.getRatio());

		assertEquals(499, mapper.toDisplay(of( 90L)));
		assertEquals(100, mapper.toDisplay(of(450L)));
		assertEquals(449, mapper.toDisplay(of(135L)));	// 135-90= 45*1.10834= 49.8753 ->100+400-1- 50=449
		assertEquals(160, mapper.toDisplay(of(396L)));	// 396-90=306*1.10834=339.15204->100+400-1-339=160

		assertEquals( of(90L), mapper.toValue(499));
		assertEquals(of(450L), mapper.toValue(100));
		assertEquals(of(135L), mapper.toValue(449));
		assertEquals(of(396L), mapper.toValue(160));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		mapper = new ValueAxisDisplayMapperVUD(10, 200, new Range<>(of("100.5"), of("145.15")));
		assertTrue(mapper.equals(mapper));
		assertFalse(mapper.equals(null));
		assertFalse(mapper.equals(this));
	}

	@Test
	public void testEquals() {
		mapper = new ValueAxisDisplayMapperVUD(10, 200, new Range<>(of("100.5"), of("145.15")));
		Variant<Integer> vY = new Variant<>(10, 5),
				vH = new Variant<>(vY, 200, 250);
		Variant<Range<CDecimal>> vRng = new Variant<>(vH);
		vRng.add(new Range<>(of("100.5"), of("145.15")));
		vRng.add(new Range<>(of(105L), of(140L)));
		Variant<?> iterator = vRng;
		int foundCnt = 0;
		ValueAxisDisplayMapperVUD x, found = null;
		do {
			x = new ValueAxisDisplayMapperVUD(vY.get(), vH.get(), vRng.get());
			if ( mapper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(10, found.getPlotStart());
		assertEquals(200, found.getPlotSize());
		assertEquals(new Range<>(of("100.5"), of("145.15")), found.getValueRange());
	}
	
	@Test
	public void testConvertation_TestIncorrectCoordForNegativeRange() throws Exception {
		mapper = new ValueAxisDisplayMapperVUD(5, 10, new Range<>(of("-0.07"), of("-0.05")));
		
		assertEquals( 5, mapper.toDisplay(of("-0.050")));
		assertEquals( 5, mapper.toDisplay(of("-0.051")));
		assertEquals( 6, mapper.toDisplay(of("-0.052")));
		assertEquals( 6, mapper.toDisplay(of("-0.053")));
		assertEquals( 7, mapper.toDisplay(of("-0.054")));
		assertEquals( 7, mapper.toDisplay(of("-0.055")));
		assertEquals( 8, mapper.toDisplay(of("-0.056")));
		assertEquals( 8, mapper.toDisplay(of("-0.057")));
		assertEquals( 9, mapper.toDisplay(of("-0.058")));
		assertEquals( 9, mapper.toDisplay(of("-0.059")));
		assertEquals( 9, mapper.toDisplay(of("-0.060")));
		assertEquals(10, mapper.toDisplay(of("-0.061")));
		assertEquals(10, mapper.toDisplay(of("-0.062")));
		assertEquals(11, mapper.toDisplay(of("-0.063")));
		assertEquals(11, mapper.toDisplay(of("-0.064")));
		assertEquals(12, mapper.toDisplay(of("-0.065")));
		assertEquals(12, mapper.toDisplay(of("-0.066")));
		assertEquals(13, mapper.toDisplay(of("-0.067")));
		assertEquals(13, mapper.toDisplay(of("-0.068")));
		assertEquals(14, mapper.toDisplay(of("-0.069")));
		assertEquals(14, mapper.toDisplay(of("-0.070")));
		
		assertEquals(of("-0.05"), mapper.toValue( 5));
		assertEquals(of("-0.05"), mapper.toValue( 6)); // <-- cuz scale is 2, not 3!
		assertEquals(of("-0.05"), mapper.toValue( 7));
		assertEquals(of("-0.06"), mapper.toValue( 8));
		assertEquals(of("-0.06"), mapper.toValue( 9));
		assertEquals(of("-0.06"), mapper.toValue(10));
		assertEquals(of("-0.06"), mapper.toValue(11));
		assertEquals(of("-0.07"), mapper.toValue(12));
		assertEquals(of("-0.07"), mapper.toValue(13));
		assertEquals(of("-0.07"), mapper.toValue(14));
	}
	
	@Test
	public void testConvertation_TestIncorrectCoordForPositiveRange() throws Exception {
		mapper = new ValueAxisDisplayMapperVUD(5, 10, new Range<>(of("0.05"), of("0.07")));

		assertEquals( 5, mapper.toDisplay(of("0.070")));
		assertEquals( 5, mapper.toDisplay(of("0.069")));
		assertEquals( 6, mapper.toDisplay(of("0.068")));
		assertEquals( 6, mapper.toDisplay(of("0.067")));
		assertEquals( 7, mapper.toDisplay(of("0.066")));
		assertEquals( 7, mapper.toDisplay(of("0.065")));
		assertEquals( 8, mapper.toDisplay(of("0.064")));
		assertEquals( 8, mapper.toDisplay(of("0.063")));
		assertEquals( 9, mapper.toDisplay(of("0.062")));
		assertEquals( 9, mapper.toDisplay(of("0.061")));
		assertEquals( 9, mapper.toDisplay(of("0.060")));
		assertEquals(10, mapper.toDisplay(of("0.059")));
		assertEquals(10, mapper.toDisplay(of("0.058")));
		assertEquals(11, mapper.toDisplay(of("0.057")));
		assertEquals(11, mapper.toDisplay(of("0.056")));
		assertEquals(12, mapper.toDisplay(of("0.055")));
		assertEquals(12, mapper.toDisplay(of("0.054")));
		assertEquals(13, mapper.toDisplay(of("0.053")));
		assertEquals(13, mapper.toDisplay(of("0.052")));
		assertEquals(14, mapper.toDisplay(of("0.051")));
		assertEquals(14, mapper.toDisplay(of("0.050")));
		
		assertEquals(of("0.07"), mapper.toValue( 5));
		assertEquals(of("0.07"), mapper.toValue( 6));
		assertEquals(of("0.07"), mapper.toValue( 7));
		assertEquals(of("0.06"), mapper.toValue( 8));
		assertEquals(of("0.06"), mapper.toValue( 9));
		assertEquals(of("0.06"), mapper.toValue(10));
		assertEquals(of("0.06"), mapper.toValue(11));
		assertEquals(of("0.05"), mapper.toValue(12));
		assertEquals(of("0.05"), mapper.toValue(13));
		assertEquals(of("0.05"), mapper.toValue(14));
	}

}
