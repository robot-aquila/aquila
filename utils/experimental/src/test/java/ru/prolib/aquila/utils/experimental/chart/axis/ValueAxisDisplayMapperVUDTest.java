package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.core.utils.Variant;

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
		new ValueAxisDisplayMapperVUD(0, 10, new Range<>(of(0L), of(11L)));
	}
	
	@Test
	public void testCtor() {
		mapper = new ValueAxisDisplayMapperVUD(2, 6, new Range<>(of(0L), of(2L)));
		assertEquals(2, mapper.getPlotStart());
		assertEquals(6, mapper.getPlotSize());
		assertEquals(new Range<CDecimal>(of(0L), of(2L)), mapper.getValueRange());
		assertEquals(AxisDirection.UP, mapper.getAxisDirection());
	}
	
	@Test
	public void testConvertation_SimpleCase1() {
		mapper = new ValueAxisDisplayMapperVUD(2, 6, new Range<>(of(0L), of(2L)));
		
		// ratio=(6-1)/(2-0)=2.5
		assertEquals(of("2.50000"), mapper.getRatio());
		
		assertEquals(2, mapper.toDisplay(of(2L)));
		assertEquals(4, mapper.toDisplay(of(1L)));
		assertEquals(7, mapper.toDisplay(of(0L)));
	}
	
	@Test
	public void testConvertation_SimpleCase2() {
		mapper = new ValueAxisDisplayMapperVUD(2, 7, new Range<>(of(0L), of(2L)));
		
		// ratio=(7-1)/(2-0)=2
		assertEquals(of("3.00000"), mapper.getRatio());
		
		assertEquals(2, mapper.toDisplay(of(2L)));
		assertEquals(5, mapper.toDisplay(of(1L)));
		assertEquals(8, mapper.toDisplay(of(0L)));
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
		assertEquals(of("4.4568869"), mapper.getRatio()); // 4.4568868980963045912653975363942
		
		assertEquals(209, mapper.toDisplay(of("100.5")));
		assertEquals( 10, mapper.toDisplay(of("145.15")));
		assertEquals(122, mapper.toDisplay(of("120.03")));	// 120.03-100.5=19.53*4.4568869= 87.0429992
															// 10+200-1- 87=122
		assertEquals( 11, mapper.toDisplay(of("145.00")));	// 145.00-100.5=44.5 *4.4568869=198.3314671
															// 10+200-1-198= 11
		assertEquals( 20, mapper.toDisplay(of("142.91")));	// 142.91-100.5=42.41*4.4568869=189.0165734
															// 10+200-1-189= 20
		assertEquals( 48, mapper.toDisplay(of("136.57")));	// 136.57-100.5=36.07*4.4568869=160.7599105
															// 10+200-1-161= 48

		assertEquals(of("100.50"), mapper.toValue(209));
		assertEquals(of("100.72"), mapper.toValue(208));	// (200-1-(208-10))/4.4568869+100.5=100.72
		assertEquals(208, mapper.toDisplay(of("100.72")));	// additional test
		assertEquals(of("102.52"), mapper.toValue(200));	// (200-1-(200-10))/4.4568869+100.5=102.52
		assertEquals(of("108.80"), mapper.toValue(172));	// (200-1-(172-10))/4.4568869+100.5=108.80
		assertEquals(of("125.63"), mapper.toValue( 97));	// (200-1-( 97-10))/4.4568869+100.5=125.63
		assertEquals(of("144.93"), mapper.toValue( 11));	// (200-1-( 11-10))/4.4568869+100.5=144.93
		assertEquals(of("145.15"), mapper.toValue( 10));
	}
	
	@Test
	public void testConvertation_Case2() {
		mapper = new ValueAxisDisplayMapperVUD(45, 55,
				new Range<>(of("48.213"), of("97.42")));
		assertEquals(of("48.213"), mapper.getMinValue());
		assertEquals(of("97.42"), mapper.getMaxValue()); // height 49.207
		assertEquals(45, mapper.getPlotStart());
		assertEquals(55, mapper.getPlotSize());
		assertEquals(of("1.09740485"), mapper.getRatio()); // 1,097404840774686528339463897413

		assertEquals( 99, mapper.toDisplay(of("48.213")));
		assertEquals( 45, mapper.toDisplay(of("97.42")));
		assertEquals( 97, mapper.toDisplay(of("50")));		// 50.000-48.213= 1.787*1.09740485= 1.96106247
															// 45+55-1- 2=97
		assertEquals( 60, mapper.toDisplay(of("84.001")));	// 84.001-48.213=35.788*1.09740485=39.27392477
															// 45+55-1-39=60

		assertEquals(of("48.213"), mapper.toValue(99));
		assertEquals(of("97.420"), mapper.toValue(45));
		assertEquals(of("50.035"), mapper.toValue(97));		// (55-1-(97-45))/1.09740485+48.213=50.035
		assertEquals(of("83.751"), mapper.toValue(60));		// (55-1-(60-45))/1.09740485+48.213=83.751
	}
	
	@Test
	public void testConvertation_Case3() {
		mapper = new ValueAxisDisplayMapperVUD(100, 400,
				new Range<>(of(90L), of(450L)));
		assertEquals( of(90L), mapper.getMinValue());
		assertEquals(of(450L), mapper.getMaxValue()); // height 360
		assertEquals(100, mapper.getPlotStart());
		assertEquals(400, mapper.getPlotSize());
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
		mapper = new ValueAxisDisplayMapperVUD(10, 200,
				new Range<>(of("100.5"), of("145.15")));
		assertTrue(mapper.equals(mapper));
		assertFalse(mapper.equals(null));
		assertFalse(mapper.equals(this));
	}

	@Test
	public void testEquals() {
		mapper = new ValueAxisDisplayMapperVUD(10, 200,
				new Range<>(of("100.5"), of("145.15")));
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

}
