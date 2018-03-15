package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class GridLinesSetupTest {
	private RulerRendererID rendererID1, rendererID2;
	private GridLinesSetup service;

	@Before
	public void setUp() throws Exception {
		rendererID1 = new RulerRendererID("foo", "bar");
		rendererID2 = new RulerRendererID("zoo", "buz");
		service = new GridLinesSetup(rendererID1, false, 10);
	}
	
	@Test
	public void testCtor1() {
		service = new GridLinesSetup(rendererID1);
		assertEquals(rendererID1, service.getRendererID());
		assertTrue(service.isVisible());
		assertEquals(0, service.getDisplayPriority());
	}
	
	@Test
	public void testCtor3() {
		assertEquals(rendererID1, service.getRendererID());
		assertFalse(service.isVisible());
		assertEquals(10, service.getDisplayPriority());
	}
	
	@Test
	public void testSetters() {
		assertSame(service, service.setVisible(false));
		assertFalse(service.isVisible());
		assertSame(service, service.setVisible(true));
		assertTrue(service.isVisible());
		assertSame(service, service.setDisplayPriority(80));
		assertEquals(80, service.getDisplayPriority());
		assertSame(service, service.setDisplayPriority(45));
		assertEquals(45, service.getDisplayPriority());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<RulerRendererID> vRID = new Variant<>(rendererID1, rendererID2);
		Variant<Boolean> vVis = new Variant<>(vRID, false, true);
		Variant<Integer> vPri = new Variant<>(vVis, 10, 25);
		Variant<?> iterator = vPri;
		int foundCnt = 0;
		GridLinesSetup x, found = null;
		do {
			x = new GridLinesSetup(vRID.get(), vVis.get(), vPri.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rendererID1, found.getRendererID());
		assertFalse(found.isVisible());
		assertEquals(10, found.getDisplayPriority());
	}

	@Test
	public void testToString() {
		String expected = "GridLinesSetup[rendererID=" + rendererID1 + ",visible=false,displayPriority=10]";
		assertEquals(expected, service.toString());

	}

}
