package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class RulerIDTest {
	private RulerRendererID rendererID1;
	private RulerID service;

	@Before
	public void setUp() throws Exception {
		rendererID1 = new RulerRendererID("VALUE", "LABEL");
		service = new RulerID(rendererID1, false);
	}
	
	@Test
	public void testCtor3() {
		service = new RulerID("foo", "bar", true);
		assertEquals(new RulerRendererID("foo", "bar"), service.getRulerRendererID());
		assertEquals("foo", service.getAxisID());
		assertEquals("bar", service.getRendererID());
		assertFalse(service.isLowerPosition());
		assertTrue(service.isUpperPosition());
	}
	
	@Test
	public void testCtor2() {
		assertEquals(rendererID1, service.getRulerRendererID());
		assertEquals("VALUE", service.getAxisID());
		assertEquals("LABEL", service.getRendererID());
		assertTrue(service.isLowerPosition());
		assertFalse(service.isUpperPosition());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vAxID = new Variant<>("VALUE", "CATEGORY");
		Variant<String> vRndID = new Variant<>(vAxID, "LABEL", "TIME");
		Variant<Boolean> vIsUp = new Variant<>(vRndID, false, true);
		Variant<?> iterator = vIsUp;
		int foundCnt = 0;
		RulerID x, found = null;
		do {
			x = new RulerID(vAxID.get(), vRndID.get(), vIsUp.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new RulerRendererID("VALUE", "LABEL"), found.getRulerRendererID());
		assertTrue(found.isLowerPosition());
		assertFalse(found.isUpperPosition());
	}

	@Test
	public void testToString() {
		String expected = "RulerID[rulerRendererID=RulerRendererID[axisID=VALUE,rendererID=LABEL],isUpper=false]";
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(83921, 1412117)
				.append(new RulerRendererID("VALUE", "LABEL"))
				.append(false)
				.toHashCode();
		assertEquals(expected, service.hashCode());
	}
	
}
