package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;

public class RulerSetupTest {
	private RulerID rulerID1, rulerID2;
	private RulerSetup service;

	@Before
	public void setUp() throws Exception {
		rulerID1 = new RulerID("foo", "bar", true);
		rulerID2 = new RulerID("foo", "buz", false);
		service = new RulerSetup(rulerID1, false, 10);
	}
	
	@Test
	public void testCtor3() {
		assertEquals(rulerID1, service.getRulerID());
		assertEquals(false, service.isVisible());
		assertEquals(10, service.getDisplayPriority());
	}
	
	@Test
	public void testCtor1() {
		service = new RulerSetup(rulerID2);
		assertEquals(rulerID2, service.getRulerID());
		assertEquals(true, service.isVisible());
		assertEquals(0, service.getDisplayPriority());
	}
	
	@Test
	public void testSetVisible() {
		service.setVisible(true);
		assertTrue(service.isVisible());
		service.setVisible(false);
		assertFalse(service.isVisible());
	}
	
	@Test
	public void testSetDisplayPriority() {
		service.setDisplayPriority(100);
		assertEquals(100, service.getDisplayPriority());
		service.setDisplayPriority(200);
		assertEquals(200, service.getDisplayPriority());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<RulerID> vRID = new Variant<>(rulerID1, rulerID2);
		Variant<Boolean> vVis = new Variant<>(vRID, false, true);
		Variant<Integer> vPri = new Variant<>(vVis, 10, 50);
		Variant<?> iterator = vPri;
		int foundCnt = 0;
		RulerSetup x, found = null;
		do {
			x = new RulerSetup(vRID.get(), vVis.get(), vPri.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rulerID1, found.getRulerID());
		assertEquals(false, found.isVisible());
		assertEquals(10, found.getDisplayPriority());
	}

	@Test
	public void testToString() {
		String expected = "RulerSetup[rulerID=" + rulerID1 + ",visible=false,displayPriority=10]";
		assertEquals(expected, service.toString());
	}

}
