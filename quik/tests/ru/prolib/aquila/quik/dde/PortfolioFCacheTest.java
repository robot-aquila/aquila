package ru.prolib.aquila.quik.dde;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class PortfolioFCacheTest {
	private PortfolioFCache row;

	@Before
	public void setUp() throws Exception {
		row = new PortfolioFCache("eqe01", "SPBFUT", 10000.0d, 8000.0d, -10.0d);
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
			.add("eqe01")
			.add("jmk00");
		Variant<String> vFrm = new Variant<String>(vAcc)
			.add("SPBFUT")
			.add("XXXXXX");
		Variant<Double> vBal = new Variant<Double>(vFrm)
			.add(10000.0d)
			.add( 1000.0d);
		Variant<Double> vCash = new Variant<Double>(vBal)
			.add(8000.0d)
			.add(0.0d);
		Variant<Double> vMrg = new Variant<Double>(vCash)
			.add(-10.0d)
			.add(200.0d);
		Variant<?> iterator = vMrg;
		int foundCnt = 0;
		PortfolioFCache x = null, found = null;
		do {
			x = new PortfolioFCache(vAcc.get(), vFrm.get(), vBal.get(),
					vCash.get(), vMrg.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("eqe01", found.getAccountCode());
		assertEquals("SPBFUT", found.getFirmId());
		assertEquals(10000.0d, found.getBalance(), 0.01d);
		assertEquals(8000.0d, found.getCash(), 0.01d);
		assertEquals(-10.0d, found.getVarMargin(), 0.01d);
	}

}
