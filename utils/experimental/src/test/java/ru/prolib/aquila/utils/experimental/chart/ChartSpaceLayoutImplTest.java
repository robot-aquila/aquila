package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSpace;

public class ChartSpaceLayoutImplTest {
	private Segment1D dataSpace1, dataSpace2;
	private RulerSpace ruler1, ruler2, ruler3, ruler4, ruler5, ruler6;
	private List<RulerSpace> listRulers1, listRulers2;
	private GridLinesSetup glSetup1_1, glSetup1_2, glSetup2_1;
	private List<GridLinesSetup> listGLSetup1, listGLSetup2;
	private ChartSpaceLayoutImpl service;

	@Before
	public void setUp() throws Exception {
		// Let's imagine case when space is horizontally oriented ->
		// rulers described as pairs of Y+height
		// all rulers linked to same axis
		// there are two rulers of categories axis: date which is outer
		// and time which is inner, next to data area.
		// Initially, display segment shifted 5 pixels right
		ruler1 = new RulerSpace(new RulerID("AXIS", "DATE", false), new Segment1D(  5, 15));
		ruler2 = new RulerSpace(new RulerID("AXIS", "TIME", false), new Segment1D( 20, 12));
		dataSpace1 = new Segment1D(32, 100);
		ruler3 = new RulerSpace(new RulerID("AXIS", "TIME", true),  new Segment1D(132, 12));
		ruler4 = new RulerSpace(new RulerID("AXIS", "DATE", true),  new Segment1D(144, 15));
		listRulers1 = new ArrayList<>();
		listRulers1.add(ruler1);
		listRulers1.add(ruler2);
		listRulers1.add(ruler3);
		listRulers1.add(ruler4);
		// Prepare list of grid lines setup to display
		glSetup1_1 = new GridLinesSetup(new RulerRendererID("AXIS", "DATE"));
		glSetup1_2 = new GridLinesSetup(new RulerRendererID("AXIS", "TIME"));
		listGLSetup1 = new ArrayList<>();
		listGLSetup1.add(glSetup1_1);
		listGLSetup1.add(glSetup1_2);
		service = new ChartSpaceLayoutImpl(dataSpace1, listRulers1, listGLSetup1);
		
		// Data for additional tests
		ruler5 = new RulerSpace(new RulerID("foo", "bar", false), new Segment1D( 0, 10));
		dataSpace2 = new Segment1D(10, 50);
		ruler6 = new RulerSpace(new RulerID("foo", "bar", true),  new Segment1D(60, 10));
		listRulers2 = new ArrayList<>();
		listRulers2.add(ruler5);
		listRulers2.add(ruler6);
		glSetup2_1 = new GridLinesSetup(new RulerRendererID("foo", "bar"));
		listGLSetup2 = new ArrayList<>();
		listGLSetup2.add(glSetup2_1);
	}
	
	@Test
	public void testGetLowerRulersTotalSpace() {
		Segment1D actual = service.getLowerRulersTotalSpace();
		
		Segment1D expected = new Segment1D(5, 27);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLowerRulersTotalSpace_WhenNoLowerRulers() {
		listRulers1.clear();
		listRulers1.add(ruler3);
		listRulers1.add(ruler4);
		service = new ChartSpaceLayoutImpl(dataSpace1, listRulers1, listGLSetup1);
		
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
		listRulers1.clear();
		listRulers1.add(ruler1);
		listRulers1.add(ruler2);
		service = new ChartSpaceLayoutImpl(dataSpace1, listRulers1, listGLSetup1);
		
		Segment1D actual = service.getUpperRulersTotalSpace();
		
		Segment1D expected = new Segment1D(0, 0);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDataSpace() {
		assertEquals(dataSpace1, service.getDataSpace());
	}
	
	@Test
	public void testGetRulersToDisplay() {
		List<RulerSpace> actual = service.getRulersToDisplay();
		
		List<RulerSpace> expected = new ArrayList<>();
		expected.add(ruler1);
		expected.add(ruler2);
		expected.add(ruler3);
		expected.add(ruler4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetGridLinesToDisplay() {
		List<GridLinesSetup> actual = service.getGridLinesToDisplay();
		
		List<GridLinesSetup> expected = new ArrayList<>();
		expected.add(glSetup1_1);
		expected.add(glSetup1_2);
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
		Variant<List<RulerSpace>> vRulers = new Variant<>(vDS, listRulers1, listRulers2);
		Variant<List<GridLinesSetup>> vGridLines = new Variant<>(vRulers, listGLSetup1, listGLSetup2);
		Variant<?> iterator = vGridLines;
		int foundCnt = 0;
		ChartSpaceLayoutImpl x, found = null;
		do {
			x = new ChartSpaceLayoutImpl(vDS.get(), vRulers.get(), vGridLines.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dataSpace1, found.getDataSpace());
		assertEquals(listRulers1, found.getRulersToDisplay());
		assertEquals(listGLSetup1, found.getGridLinesToDisplay());
	}
	
	@Test
	public void testToString() {
		String expected = new ToStringBuilder(service, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("dataSpace", dataSpace1)
				.append("rulers", listRulers1)
				.append("gridLines", listGLSetup1)
				.toString();
		assertEquals(expected, service.toString());
	}

}
