package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class RendererIDTest {
	private RendererID service;

	@Before
	public void setUp() throws Exception {
		service = new RendererID("foo", "bar");
	}
	
	@Test
	public void testCtor2() {
		assertEquals("foo", service.getAxisID());
		assertEquals("bar", service.getRendererID());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<String> vAID = new Variant<>("foo", "zulu24");
		Variant<String> vRID = new Variant<>(vAID, "bar", "charlie");
		Variant<?> iterator = vRID;
		int foundCnt = 0;
		RendererID x, found = null;
		do {
			x = new RendererID(vAID.get(), vRID.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getAxisID());
		assertEquals("bar", found.getRendererID());
	}

	@Test
	public void testToString() {
		String expected = "RendererID[axisID=foo,rendererID=bar]";
		assertEquals(expected, service.toString());
	}

}
