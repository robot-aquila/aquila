package ru.prolib.aquila.quik.dde;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class PositionFCacheTest {
	private PositionFCache row;

	@Before
	public void setUp() throws Exception {
		row = new PositionFCache("eqe02", "BCS01", "RIM3", 10L, 1L, -12.34d);
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		assertEquals(new Date(), row.getEntryTime());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vAcc = new Variant<String>()
			.add("eqe02")
			.add("jmk03");
		Variant<String> vFrm = new Variant<String>(vAcc)
			.add("BCS01")
			.add("XXXXX");
		Variant<String> vName = new Variant<String>(vFrm)
			.add("RIM3")
			.add("GAZP");
		Variant<Long> vOpnQty = new Variant<Long>(vName)
			.add(10L)
			.add(120L);
		Variant<Long> vCurQty = new Variant<Long>(vOpnQty)
			.add(1L)
			.add(5L);
		Variant<Double> vMrg = new Variant<Double>(vCurQty)
			.add(-12.34d)
			.add(15.98d);
		Variant<?> iterator = vMrg;
		int foundCnt = 0;
		PositionFCache x = null, found = null;
		do {
			x = new PositionFCache(vAcc.get(), vFrm.get(), vName.get(),
					vOpnQty.get(), vCurQty.get(), vMrg.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("eqe02", found.getAccountCode());
		assertEquals("BCS01", found.getFirmId());
		assertEquals("RIM3", found.getSecurityShortName());
		assertEquals(new Long(10L), found.getOpenQty());
		assertEquals(new Long(1L), found.getCurrentQty());
		assertEquals(-12.34d, found.getVarMargin(), 0.001d);
	}

}
