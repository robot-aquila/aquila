package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;

public class PortfolioEntryTest {
	private PortfolioEntry row;

	@Before
	public void setUp() throws Exception {
		row = new PortfolioEntry(new Account("TEST"), 10.0d, 80.0d, -1.0d);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(row.equals(row));
		assertFalse(row.equals(null));
		assertFalse(row.equals(this));
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		long d = Math.abs(new Date().getTime() - row.getEntryTime().getTime());
		assertTrue(d <= 1000);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Account> vAcc = new Variant<Account>()
			.add(new Account("TEST"))
			.add(new Account("BEST"));
		Variant<Double> vBal = new Variant<Double>(vAcc)
			.add(10.0d)
			.add( 1000.0d);
		Variant<Double> vCash = new Variant<Double>(vBal)
			.add(80.0d)
			.add(0.0d);
		Variant<Double> vMrg = new Variant<Double>(vCash)
			.add(-1.0d)
			.add(200.0d);
		Variant<?> iterator = vMrg;
		int foundCnt = 0;
		PortfolioEntry x = null, found = null;
		do {
			x = new PortfolioEntry(vAcc.get(), vBal.get(),
					vCash.get(), vMrg.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Account("TEST"), found.getAccount());
		assertEquals(10.0d, found.getBalance(), 0.01d);
		assertEquals(80.0d, found.getCash(), 0.01d);
		assertEquals(-1.0d, found.getVarMargin(), 0.01d);
	}

}
