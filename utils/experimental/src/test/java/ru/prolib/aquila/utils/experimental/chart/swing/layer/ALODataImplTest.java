package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;

public class ALODataImplTest {
	private ALODataImpl service;

	@Before
	public void setUp() throws Exception {
		service = new ALODataImpl(of("128.19"), of(100L), of(50L));
	}
	
	@Test
	public void testCtor3() {
		assertEquals(of("128.19"), service.getPrice());
		assertEquals(of(100L), service.getTotalBuyVolume());
		assertEquals(of(50L), service.getTotalSellVolume());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<CDecimal> vPr = new Variant<>(of("128.19"), of("200.94")),
				vBVol = new Variant<>(vPr, of(100L), of(999L)),
				vSVol = new Variant<>(vBVol, of(50L), of(200L));
		Variant<?> iterator = vSVol;
		int foundCnt = 0;
		ALODataImpl x, found = null;
		do {
			x = new ALODataImpl(vPr.get(), vBVol.get(), vSVol.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(of("128.19"), found.getPrice());
		assertEquals(of(100L), found.getTotalBuyVolume());
		assertEquals(of(50L), found.getTotalSellVolume());
	}

}
