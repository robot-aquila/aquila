package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.ValueAxisLabelGenerator;

public class ValueAxisLabelGeneratorTest {
	private ValueAxisDriver driver;
	private ValueAxisViewport viewport;
	private ValueAxisLabelGenerator generator;

	@Before
	public void setUp() throws Exception {
		generator = ValueAxisLabelGenerator.getInstance();
		driver = new ValueAxisDriverImpl("foo", AxisDirection.UP);
		viewport = new ValueAxisViewportImpl();
	}

	@Test
	public void testGetLabelValues_BasicCase_MultX2() {
		viewport.setValueRange(new Range<>(of("146.35"), of("246.36")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("0.01"), 20);
		
		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("160.00"));
		expected.add(of("180.00"));
		expected.add(of("200.00"));
		expected.add(of("220.00"));
		expected.add(of("240.00"));
		assertEquals(expected, actual);
		
		// Check that there is enough space between labels
		for ( int i = 1; i < expected.size(); i ++ ) {
			int y1 = mapper.toDisplay(expected.get(i - 1));
			int y2 = mapper.toDisplay(expected.get(i));
			assertTrue("At#" + i + "y1=" + y1 + " y2=" + y2, y1 - y2 >= 20);
		}
	}
	
	@Test
	public void testGetLabelValues_BaseCase2_MultX5() {
		// tick size=0.01
		// range=141.75-142.32
		// expected labels: from 141.75 to 142.30 with step 0.05
		viewport.setValueRange(new Range<>(of("141.75"), of("142.32")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("0.01"), 12);

		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("141.75"));
		expected.add(of("141.80"));
		expected.add(of("141.85"));
		expected.add(of("141.90"));
		expected.add(of("141.95"));
		expected.add(of("142.00"));
		expected.add(of("142.05"));
		expected.add(of("142.10"));
		expected.add(of("142.15"));
		expected.add(of("142.20"));
		expected.add(of("142.25"));
		expected.add(of("142.30"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_BaseCase3_MultX1() {
		viewport.setValueRange(new Range<>(of("32.75"), of("32.76")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("0.01"), 12);

		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("32.75"));
		expected.add(of("32.76"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_BaseCase4_MultX10() {
		viewport.setValueRange(new Range<>(of("100.02"), of("643.17")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("0.01"), 30);

		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("200.00"));
		expected.add(of("300.00"));
		expected.add(of("400.00"));
		expected.add(of("500.00"));
		expected.add(of("600.00"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_TickSize0_25() {
		viewport.setValueRange(new Range<>(of("100.02"), of("104.83")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("0.25"), 15);
		
		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("100.50"));
		expected.add(of("101.00"));
		expected.add(of("101.50"));
		expected.add(of("102.00"));
		expected.add(of("102.50"));
		expected.add(of("103.00"));
		expected.add(of("103.50"));
		expected.add(of("104.00"));
		expected.add(of("104.50"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_TickSize10() {
		viewport.setValueRange(new Range<>(of("124230"), of("124770")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("10"), 15);
		
		List<CDecimal> expected = new ArrayList<>();
		expected.add(of("124250"));
		expected.add(of("124300"));
		expected.add(of("124350"));
		expected.add(of("124400"));
		expected.add(of("124450"));
		expected.add(of("124500"));
		expected.add(of("124550"));
		expected.add(of("124600"));
		expected.add(of("124650"));
		expected.add(of("124700"));
		expected.add(of("124750"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_CaseWhenCannotDetermineStepSize() {
		// Этот кейс возможен, когда размер области рисования меньше размера метки
		viewport.setValueRange(new Range<>(of(250L), of(300L)));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 10), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of(10L), 12);
		
		List<CDecimal> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_BugTest1_NonAbstractRange() {
		viewport.setValueRange(new Range<>(ofRUB2("25.80"), ofRUB2("50.80")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, ofRUB2("1.00"), 12);
		
		List<CDecimal> expected = new ArrayList<>();
		// I'm not sure. Should be 24 here?
		expected.add(ofRUB2("26"));
		expected.add(ofRUB2("28"));
		expected.add(ofRUB2("30"));
		expected.add(ofRUB2("32"));
		expected.add(ofRUB2("34"));
		expected.add(ofRUB2("36"));
		expected.add(ofRUB2("38"));
		expected.add(ofRUB2("40"));
		expected.add(ofRUB2("42"));
		expected.add(ofRUB2("44"));
		expected.add(ofRUB2("46"));
		expected.add(ofRUB2("48"));
		expected.add(ofRUB2("50"));
		// Not sure. Should be 52 here?
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLabelValues_BugTest2_NonAbstractRangeAndUnitOfTickSizeDoesNotMatter() {
		viewport.setValueRange(new Range<>(ofRUB2("25.80"), ofRUB2("50.80")));
		ValueAxisDisplayMapper mapper = driver.createMapper(new Segment1D(0, 200), viewport);
		
		List<CDecimal> actual = generator.getLabelValues(mapper, of("1.00"), 12);

		List<CDecimal> expected = new ArrayList<>();
		// I'm not sure. Should be 24 here?
		expected.add(ofRUB2("26"));
		expected.add(ofRUB2("28"));
		expected.add(ofRUB2("30"));
		expected.add(ofRUB2("32"));
		expected.add(ofRUB2("34"));
		expected.add(ofRUB2("36"));
		expected.add(ofRUB2("38"));
		expected.add(ofRUB2("40"));
		expected.add(ofRUB2("42"));
		expected.add(ofRUB2("44"));
		expected.add(ofRUB2("46"));
		expected.add(ofRUB2("48"));
		expected.add(ofRUB2("50"));
		// Not sure. Should be 52 here?
		assertEquals(expected, actual);
	}

}
