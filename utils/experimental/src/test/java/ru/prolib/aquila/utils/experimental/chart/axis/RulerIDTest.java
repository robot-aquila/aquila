package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class RulerIDTest {
	private RulerID service;

	@Before
	public void setUp() throws Exception {
		service = new RulerID("VALUE", "LABEL", false);
	}
	
	@Test
	public void testCtor() {
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
		assertEquals("VALUE", found.getAxisID());
		assertEquals("LABEL", found.getRendererID());
		assertTrue(found.isLowerPosition());
		assertFalse(found.isUpperPosition());
	}

	@Test
	public void testToString() {
		String expected = "RulerID[axisID=VALUE, rendererID=LABEL, isUpper=false]";
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(83921, 1412117)
				.append("VALUE")
				.append("LABEL")
				.append(false)
				.toHashCode();
		assertEquals(expected, service.hashCode());
	}
	
}
