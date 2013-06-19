package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.PositionEntry;

import com.ib.client.Contract;

public class PositionEntryTest {
	private Contract contract;
	private PositionEntry entry;

	@Before
	public void setUp() throws Exception {
		contract = new Contract();
		contract.m_conId = 124;
		contract.m_primaryExch = "ARCA";
		entry = new PositionEntry(contract, -5, -1000.0d, 180.5d, 130d, "TEST");
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		entry = new PositionEntry(contract, 0, 0d, 0d, 0d, "");
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetters() throws Exception {
		assertEquals(124, entry.getContractId());
		assertSame(contract, entry.getContract());
		assertEquals(new Long(-5), entry.getQty());
		assertEquals(-1000d, entry.getMarketValue(), 0.1d);
		assertEquals(-902.5d, entry.getBookValue(), 0.1d);
		assertEquals(32.5d, entry.getVarMargin(), 0.01d);
		assertEquals(new Account("TEST"), entry.getAccount());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Contract> vCont = new Variant<Contract>()
			.add(contract)
			.add(new Contract());
		Variant<Integer> vQty = new Variant<Integer>(vCont)
			.add(-5)
			.add(10);
		Variant<Double> vMktVal = new Variant<Double>(vQty)
			.add(-1000d)
			.add(829d);
		Variant<Double> vAvgCst = new Variant<Double>(vMktVal)
			.add(180.5d)
			.add(234d);
		Variant<Double> vRlzPnl = new Variant<Double>(vAvgCst)
			.add(130d)
			.add(390d);
		Variant<String> vAcc = new Variant<String>(vRlzPnl)
			.add("TEST")
			.add("BEST");
		Variant<?> iterator = vAcc;
		int foundCnt = 0;
		PositionEntry x = null, found = null;
		do {
			x = new PositionEntry(vCont.get(), vQty.get(), vMktVal.get(),
					vAvgCst.get(), vRlzPnl.get(), vAcc.get());
			if ( entry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(contract, found.getContract());
		assertEquals(new Long(-5), found.getQty());
		assertEquals(-1000d, entry.getMarketValue(), 0.1d);
		assertEquals(-902.5d, entry.getBookValue(), 0.1d);
		assertEquals(32.5d, entry.getVarMargin(), 0.01d);
		assertEquals(new Account("TEST"), entry.getAccount());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		Contract cont2 = new Contract();
		PositionEntry entry2 = new PositionEntry(cont2, 0, 0, 0, 0, "X");
		
		cont2.m_conId = 10;
		cont2.m_primaryExch = "ARCA";
		assertTrue(entry2.compareTo(entry) > 0);
		assertTrue(entry.compareTo(entry2) < 0);
		
		cont2.m_conId = 124;
		assertEquals(0, entry2.compareTo(entry));
		assertEquals(0, entry.compareTo(entry2));
		
		cont2.m_primaryExch = "ZORRO";
		assertTrue(entry2.compareTo(entry) < 0);
		assertTrue(entry.compareTo(entry2) > 0);
	}

}
