package ru.prolib.aquila.utils.experimental.chart.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class OEEntryImplTest {
	private OEEntryImpl service;

	@Before
	public void setUp() throws Exception {
		service = new OEEntryImpl(true, of("75.19"));
	}
	
	@Test
	public void testAccessors() {
		assertTrue(service.isBuy());
		assertFalse(service.isSell());
		assertEquals(of("75.19"), service.getPrice());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Boolean> vB = new Variant<>(true, false);
		Variant<CDecimal> vPr = new Variant<>(vB, of("75.19"));
		Variant<?> iterator = vPr;
		int foundCnt = 0;
		OEEntryImpl x, found = null;
		do {
			x = new OEEntryImpl(vB.get(), vPr.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(true, found.isBuy());
		assertEquals(of("75.19"), found.getPrice());
	}
	
	@Test
	public void testToString() {
		String expected = "OEEntryImpl[isBuy=true price=75.19]";
		assertEquals(expected, service.toString());
	}

}
