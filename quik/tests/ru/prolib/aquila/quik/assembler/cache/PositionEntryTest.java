package ru.prolib.aquila.quik.assembler.cache;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;

public class PositionEntryTest {
	private PositionEntry row;

	@Before
	public void setUp() throws Exception {
		row = new PositionEntry(new Account("TEST"), "RIM", 5L, 1L, -1.3d);
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
		Variant<Account> vAcc = new Variant<Account>()
			.add(new Account("TEST"))
			.add(new Account("BEST"));
		Variant<String> vName = new Variant<String>(vAcc)
			.add("RIM")
			.add("GAZP");
		Variant<Long> vOpnQty = new Variant<Long>(vName)
			.add(5L)
			.add(120L);
		Variant<Long> vCurQty = new Variant<Long>(vOpnQty)
			.add(1L)
			.add(5L);
		Variant<Double> vMrg = new Variant<Double>(vCurQty)
			.add(-1.3d)
			.add(15.98d);
		Variant<?> iterator = vMrg;
		int foundCnt = 0;
		PositionEntry x = null, found = null;
		do {
			x = new PositionEntry(vAcc.get(), vName.get(),
					vOpnQty.get(), vCurQty.get(), vMrg.get());
			if ( row.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new Account("TEST"), found.getAccount());
		assertEquals("RIM", found.getSecurityShortName());
		assertEquals(new Long(5L), found.getOpenQty());
		assertEquals(new Long(1L), found.getCurrentQty());
		assertEquals(-1.3d, found.getVarMargin(), 0.001d);
	}

}
