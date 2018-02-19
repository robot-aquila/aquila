package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ChartRulerSpaceTest {
	private ChartRulerSpace service;
	private ChartRulerID rulerID1, rulerID2;

	@Before
	public void setUp() throws Exception {
		rulerID1 = new ChartRulerID("VALUE", "LABEL", false);
		rulerID2 = new ChartRulerID("CATEGORY", "TIME", true);
		service = new ChartRulerSpace(rulerID1, new Segment1D(15, 30));
	}
	
	@Test
	public void testCtor() {
		assertEquals(rulerID1, service.getRulerID());
		assertEquals(new Segment1D(15, 30), service.getSpace());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<ChartRulerID> vRulerID = new Variant<>(rulerID1, rulerID2);
		Variant<Segment1D> vSpace = new Variant<>(vRulerID);
		vSpace.add(new Segment1D(15, 30));
		vSpace.add(new Segment1D(10, 50));
		Variant<?> iterator = vSpace;
		int foundCnt = 0;
		ChartRulerSpace x, found = null;
		do {
			x = new ChartRulerSpace(vRulerID.get(), vSpace.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rulerID1, found.getRulerID());
		assertEquals(new Segment1D(15, 30), found.getSpace());
	}

	@Test
	public void testToString() {
		String expected = "ChartRulerSpace[" + rulerID1 + ", " + new Segment1D(15, 30) +  "]";
		assertEquals(expected, service.toString());
	}

}
