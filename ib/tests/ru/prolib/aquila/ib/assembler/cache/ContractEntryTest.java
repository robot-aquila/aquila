package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;

import com.ib.client.*;

public class ContractEntryTest {
	private Contract contract;
	private ContractDetails details;
	private ContractEntry entry;

	@Before
	public void setUp() throws Exception {
		contract = new Contract();
		details = new ContractDetails();
		details.m_summary = contract;
		entry = new ContractEntry(details);
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		entry = new ContractEntry(details);
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetSecurityDescriptor_WithPrimaryExch() throws Exception {
		contract.m_currency = "USD";
		contract.m_primaryExch = "ARCA";
		contract.m_secType = "STK";
		contract.m_symbol = "SPXS";
		assertEquals(
			new SecurityDescriptor("SPXS", "ARCA", "USD", SecurityType.STK),
			entry.getSecurityDescriptor());
	}
	
	@Test
	public void testGetSecurityDescriptor_NoPrimaryExch() throws Exception {
		contract.m_currency = "USD";
		contract.m_exchange = "IDEALPRO";
		contract.m_secType = "CASH";
		contract.m_symbol = "JPY";
		assertEquals(
			new SecurityDescriptor("JPY", "IDEALPRO", "USD", SecurityType.CASH),
			entry.getSecurityDescriptor());
	}
	
	@Test
	public void testGetContract() throws Exception {
		assertSame(contract, entry.getContract());
	}
	
	@Test
	public void testGetContractId() throws Exception {
		contract.m_conId = 815;
		assertEquals(815, entry.getContractId());
	}
	
	@Test
	public void testGetContractDetails() throws Exception {
		entry = new ContractEntry(details);
		assertSame(details, entry.getContractDetails());
	}
	
	@Test
	public void testGetValidExchanges() throws Exception {
		details.m_validExchanges = "SMART,ISE,CHX";
		List<String> expected = new Vector<String>();
		expected.add("SMART");
		expected.add("ISE");
		expected.add("CHX");
	}
	
	@Test
	public void testIsSmart_Yes() throws Exception {
		details.m_validExchanges = "SMART,ISE,CHX";
		assertTrue(entry.isSmart());
	}

	@Test
	public void testIsSmart_No() throws Exception {
		details.m_validExchanges = "IDEALPRO";
		assertFalse(entry.isSmart());
	}
	
	@Test
	public void testGetType() throws Exception {
		Object fix[][] = {
				// IB type, local type
				{ "STK",  SecurityType.STK },
				{ "OPT",  SecurityType.OPT },
				{ "FUT",  SecurityType.FUT },
				{ "IND",  SecurityType.UNK },
				{ "FOP",  SecurityType.UNK },
				{ "CASH", SecurityType.CASH },
				{ "BAG",  SecurityType.UNK },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			contract.m_secType = (String) fix[i][0];
			assertEquals(msg, fix[i][1], entry.getType());
		}
	}
	
	@Test
	public void testGetPrecision() throws Exception {
		Object fix[][] = {
				// tick size, expected prec.
				{ 0.01d, 2 },
				{ 10.0d, 0 },
				{ 0.05d, 2 },
				{ 0.001d, 3 },
				{ 0.0000005d, 7 },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			details.m_minTick = (Double) fix[i][0];
			assertEquals(msg, fix[i][1], entry.getPrecision());
		}
	}
	
	@Test
	public void testGetMinStepPrice() throws Exception {
		details.m_minTick = 0.05d;
		assertEquals(0.05d, entry.getMinStepPrice(), 0.01d);
	}
	
	@Test
	public void testGetMinStepSize() throws Exception {
		details.m_minTick = 0.001d;
		assertEquals(0.001d, entry.getMinStepPrice(), 0.001d);
	}
	
	@Test
	public void testGetDisplayName() throws Exception {
		contract.m_localSymbol = "Apple Inc.";
		assertEquals("Apple Inc.", entry.getDisplayName());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<ContractDetails> vDtls = new Variant<ContractDetails>()
			.add(details)
			.add(new ContractDetails());
		Variant<?> iterator = vDtls;
		int foundCnt = 0;
		ContractEntry x = null, found = null;
		do {
			x = new ContractEntry(vDtls.get());
			if ( entry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(details, found.getContractDetails());
	}
	
}
