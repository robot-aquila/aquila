package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;

public class SWTimeAxisRulerSetupTest {
	private RulerID rulerID1, rulerID2;
	private SWTimeAxisRulerSetup service;

	@Before
	public void setUp() throws Exception {
		rulerID1 = new RulerID("foo", "bar", true);
		rulerID2 = new RulerID("bar", "buz", false);
		service = new SWTimeAxisRulerSetup(rulerID1);
	}
	
	@Test
	public void testCtor1() {
		assertEquals(rulerID1, service.getRulerID());
		assertTrue(service.isVisible());
		assertEquals(0, service.getDisplayPriority());
		assertTrue(service.isShowInnerLine());
		assertTrue(service.isShowOuterLine());
	}
	
	@Test
	public void testSetters() {
		SWTimeAxisRulerSetup expected = service.setDisplayPriority(50);
		assertSame(expected, service);
		assertEquals(50, service.getDisplayPriority());
		
		expected = service.setVisible(false);
		assertSame(expected, service);
		assertFalse(service.isVisible());
		
		expected = service.setShowInnerLine(false);
		assertSame(expected, service);
		assertFalse(service.isShowInnerLine());
		
		expected = service.setShowOuterLine(false);
		assertSame(expected, service);
		assertFalse(service.isShowOuterLine());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		service.setDisplayPriority(50)
			.setVisible(false)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		
		Variant<RulerID> vRID = new Variant<>(rulerID1, rulerID2);
		Variant<Integer> vPri = new Variant<>(vRID, 50, 80);
		Variant<Boolean> vVis = new Variant<>(vPri, false, true),
			vSIL = new Variant<>(vVis, true, false),
			vSOL = new Variant<>(vSIL, false, true);
		Variant<?> iterator = vSOL;
		int foundCnt = 0;
		SWTimeAxisRulerSetup x, found = null;
		do {
			x = new SWTimeAxisRulerSetup(vRID.get())
					.setDisplayPriority(vPri.get())
					.setVisible(vVis.get())
					.setShowInnerLine(vSIL.get())
					.setShowOuterLine(vSOL.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rulerID1, found.getRulerID());
		assertEquals(50, found.getDisplayPriority());
		assertFalse(found.isVisible());
		assertTrue(found.isShowInnerLine());
		assertFalse(found.isShowOuterLine());
	}
	
	@Test
	public void testToString() {
		service.setDisplayPriority(50)
			.setVisible(false)
			.setShowInnerLine(true)
			.setShowOuterLine(false);
		String expected = "SWTimeAxisRulerSetup[rulerID=" + rulerID1
				+ ",visible=false"
				+ ",displayPriority=50"
				+ ",showInnerLine=true"
				+ ",showOuterLine=false"
				+ "]";
		assertEquals(expected, service.toString());
	}

}
