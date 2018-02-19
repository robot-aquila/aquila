package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerSpace;

public class ChartSpaceLayoutImplTest {
	private Segment1D dataSpace1, dataSpace2;
	private ChartRulerSpace ruler1, ruler2, ruler3, ruler4, ruler5, ruler6;
	private List<ChartRulerSpace> rulers1, rulers2;
	private ChartSpaceLayoutImpl service;

	@Before
	public void setUp() throws Exception {
		// Let's imagine case when space is horizontally oriented ->
		// rulers described as pairs of Y+height
		// all rulers linked to same axis
		// there are two rulers of categories axis: date which is outer
		// and time which is inner, next to data area.
		// Initially, display segment shifted 5 pixels right
		ruler1 = new ChartRulerSpace(new ChartRulerID("AXIS", "DATE", false), new Segment1D(  5, 15));
		ruler2 = new ChartRulerSpace(new ChartRulerID("AXIS", "TIME", false), new Segment1D( 20, 12));
		dataSpace1 = new Segment1D(32, 100);
		ruler3 = new ChartRulerSpace(new ChartRulerID("AXIS", "TIME", true),  new Segment1D(132, 12));
		ruler4 = new ChartRulerSpace(new ChartRulerID("AXIS", "DATE", true),  new Segment1D(144, 15));
		rulers1 = new ArrayList<>();
		rulers1.add(ruler1);
		rulers1.add(ruler2);
		rulers1.add(ruler3);
		rulers1.add(ruler4);
		service = new ChartSpaceLayoutImpl(dataSpace1, rulers1);
		// Data for additional tests
		ruler5 = new ChartRulerSpace(new ChartRulerID("foo", "bar", false), new Segment1D( 0, 10));
		dataSpace2 = new Segment1D(10, 50);
		ruler6 = new ChartRulerSpace(new ChartRulerID("foo", "bar", true),  new Segment1D(60, 10));
		rulers2 = new ArrayList<>();
		rulers2.add(ruler5);
		rulers2.add(ruler6);
	}
	
	@Test
	public void testGetLowerRulersTotalSpace() {
		Segment1D actual = service.getLowerRulersTotalSpace();
		
		Segment1D expected = new Segment1D(5, 27);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLowerRulersTotalSpace_WhenNoLowerRulers() {
		rulers1.clear();
		rulers1.add(ruler3);
		rulers1.add(ruler4);
		service = new ChartSpaceLayoutImpl(dataSpace1, rulers1);
		
		Segment1D actual = service.getLowerRulersTotalSpace();
		
		Segment1D expected = new Segment1D(0, 0);
		assertEquals(expected, actual);
		assertEquals(0, actual.getLength());
	}
	
	@Test
	public void testGetUpperRulersTotalSpace() {
		Segment1D actual = service.getUpperRulersTotalSpace();
		
		Segment1D expected = new Segment1D(132, 27);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetUpperRulersTotalSpace_WhenNoUpperRulers() {
		rulers1.clear();
		rulers1.add(ruler1);
		rulers1.add(ruler2);
		service = new ChartSpaceLayoutImpl(dataSpace1, rulers1);
		
		Segment1D actual = service.getUpperRulersTotalSpace();
		
		Segment1D expected = new Segment1D(0, 0);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDataSpace() {
		assertEquals(dataSpace1, service.getDataSpace());
	}
	
	@Test
	public void testGetRulers() {
		List<ChartRulerSpace> actual = service.getRulers();
		
		List<ChartRulerSpace> expected = new ArrayList<>();
		expected.add(ruler1);
		expected.add(ruler2);
		expected.add(ruler3);
		expected.add(ruler4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Segment1D> vDS = new Variant<>(dataSpace1, dataSpace2);
		Variant<List<ChartRulerSpace>> vRulers = new Variant<>(vDS);
		vRulers.add(rulers1);
		vRulers.add(rulers2);
		Variant<?> iterator = vRulers;
		int foundCnt = 0;
		ChartSpaceLayoutImpl x, found = null;
		do {
			x = new ChartSpaceLayoutImpl(vDS.get(), vRulers.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dataSpace1, found.getDataSpace());
		assertEquals(rulers1, found.getRulers());
	}
	
	@Test
	public void testToString() {
		String expected = new ToStringBuilder(service)
				.append("dataSpace", dataSpace1)
				.append("rulers", rulers1)
				.toString();
		assertEquals(expected, service.toString());
	}

}
