package ru.prolib.aquila.ib.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.ExecEntry;
import ru.prolib.aquila.ib.assembler.cache.ExecIdCache;

import com.ib.client.*;

public class ExecEntryTest {
	private IMocksControl control;
	private ExecIdCache idCache;
	private Contract contract;
	private Execution execution;
	private ExecEntry entry;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		idCache = control.createMock(ExecIdCache.class);
		contract = new Contract();
		contract.m_conId = 654;
		execution = new Execution();
		execution.m_execId = "213belka";
		entry = new ExecEntry(contract, execution);
		ExecEntry.setIdCache(new ExecIdCache());
	}
	
	@Test
	public void testDefaultIdCache() throws Exception {
		assertEquals(new ExecIdCache(), ExecEntry.getIdCache());
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		entry = new ExecEntry(contract, execution);
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetAccount() throws Exception {
		execution.m_acctNumber = "TEST";
		assertEquals(new Account("TEST"), entry.getAccount());
	}
	
	@Test
	public void testGetContract() throws Exception {
		assertSame(contract, entry.getContract());
	}
	
	@Test
	public void testGetDirection() throws Exception {
		execution.m_side = "BOT";
		assertEquals(OrderDirection.BUY, entry.getDirection());
		execution.m_side = "SLD";
		assertEquals(OrderDirection.SELL, entry.getDirection());
	}
	
	@Test
	public void testGetExchange() throws Exception {
		execution.m_exchange = "IDEALPRO";
		assertEquals("IDEALPRO", entry.getExchange());
	}
	
	@Test
	public void testGetOrderId() throws Exception {
		execution.m_orderId = 248;
		assertEquals(new Long(248), entry.getOrderId());
	}
	
	@Test
	public void testGetExecution() throws Exception {
		assertSame(execution, entry.getExecution());
	}
	
	@Test
	public void testGetId() throws Exception {
		ExecEntry.setIdCache(idCache);
		expect(idCache.getId(eq("213belka"))).andReturn(213L);
		control.replay();
		
		assertEquals(new Long(213), entry.getId());
		
		control.verify();
	}
	
	@Test
	public void testGetNativeId() throws Exception {
		assertEquals("213belka", entry.getNativeId());
	}
	
	@Test
	public void testGetPrice() throws Exception {
		execution.m_price = 34.19d;
		assertEquals(34.19d, entry.getPrice(), 0.001d);
	}
	
	@Test
	public void testGetQty() throws Exception {
		execution.m_shares = 1000;
		assertEquals(new Long(1000), entry.getQty());
	}
	
	@Test
	public void testGetTime() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		execution.m_time = "19980512 14:44:29";
		assertEquals(fmt.parse("1998-05-12 14:44:29"), entry.getTime());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Execution exec2 = new Execution();
		exec2.m_execId = "100500alka";
		Variant<Contract> vCont = new Variant<Contract>()
			.add(contract)
			.add(new Contract());
		Variant<Execution> vExec = new Variant<Execution>(vCont)
			.add(execution)
			.add(exec2);
		Variant<?> iterator = vExec;
		int foundCnt = 0;
		ExecEntry x = null, found = null;
		do {
			x = new ExecEntry(vCont.get(), vExec.get());
			if ( entry.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(contract, found.getContract());
		assertSame(execution, found.getExecution());
	}
	
	@Test
	public void testGetContractId() throws Exception {
		assertEquals(654, entry.getContractId());
	}

}
