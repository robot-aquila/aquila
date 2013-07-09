package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.IBTerminalBuilder;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.*;

public class AssemblerLowLvlTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private Cache cache;
	private IBClient client;
	private EditablePortfolio port;
	private EditableSecurity security;
	private EditablePosition position;
	private AssemblerLowLvl asm;
	private Counter numerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		descr = new SecurityDescriptor("AAPL","LSE","USD",SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		cache = control.createMock(Cache.class);
		client = control.createMock(IBClient.class);
		port = control.createMock(EditablePortfolio.class);
		security = control.createMock(EditableSecurity.class);
		position = control.createMock(EditablePosition.class);
		numerator = control.createMock(Counter.class);
		asm = new AssemblerLowLvl(terminal);
		
		expect(terminal.getCache()).andStubReturn(cache);
		expect(terminal.getClient()).andStubReturn(client);
		expect(terminal.getOrderNumerator()).andStubReturn(numerator);
	}
	
	@Test
	public void testGetPortfolio_CreateNew() throws Exception {
		Account acc = new Account("TEST");
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		expect(terminal.createPortfolio(eq(acc))).andReturn(port);
		control.replay();
		
		assertSame(port, asm.getPortfolio(acc));
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio_ReturnExisting() throws Exception {
		Account acc = new Account("BEST");
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(port);
		control.replay();
		
		assertSame(port, asm.getPortfolio(acc));
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_SkipNoChanges() throws Exception {
		expect(port.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_ForAvailable() throws Exception {
		expect(port.hasChanged()).andReturn(true);
		expect(port.isAvailable()).andReturn(true);
		port.fireChangedEvent();
		port.resetChanges();
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_ForNew() throws Exception {
		expect(port.hasChanged()).andReturn(true);
		expect(port.isAvailable()).andReturn(false);
		port.setAvailable(true);
		terminal.firePortfolioAvailableEvent(same(port));
		port.resetChanges();
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_Cash() throws Exception {
		port.setCash(eq(813.12d));
		control.replay();
		
		asm.update(port, new PortfolioValueEntry("unused",
				"TotalCashBalance", "BASE", "813.12"));
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Portfolio_Balance() throws Exception {
		port.setBalance(eq(112.54d));
		control.replay();
		
		asm.update(port, new PortfolioValueEntry("unused",
				"NetLiquidationByCurrency", "BASE", "112.54"));
		
		control.verify();
	}
	
	@Test
	public void testIsAvailable() throws Exception {
		terminal = new IBTerminalBuilder().createTerminal("foo");
		Variant<Double> vCash = new Variant<Double>()
			.add(112.54d)
			.add(null);
		Variant<Double> vBal = new Variant<Double>(vCash)
			.add(7712.10d)
			.add(null);
		Variant<?> iterator = vBal;
		int foundCnt = 0, i = 0;
		Portfolio found = null;
		do {
			port = terminal.createPortfolio(new Account("TEST" + i));
			port.setCash(vCash.get());
			port.setBalance(vBal.get());
			if ( asm.isAvailable(port) ) {
				foundCnt ++;
				found = port;
			}
			i ++;
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(112.54d, found.getCash(), 0.01d);
		assertEquals(7712.10d, found.getBalance(), 0.01d);
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(asm.equals(asm));
		assertFalse(asm.equals(null));
		assertFalse(asm.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		IBTerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		asm = new AssemblerLowLvl(t1);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		AssemblerLowLvl x, found = null;
		do {
			x = new AssemblerLowLvl(vTerm.get());
			if ( asm.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
	}
	
	@Test
	public void testGetCache() throws Exception {
		control.replay();
		
		assertSame(cache, asm.getCache());
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity_ContrEntry_CreateNew() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(entry.getSecurityDescriptor()).andStubReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		expect(terminal.createSecurity(eq(descr))).andReturn(security);
		control.replay();
		
		assertSame(security, asm.getSecurity(entry));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity_ContrEntry_ReturnExisting() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(entry.getSecurityDescriptor()).andStubReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(true);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(security);
		control.replay();
		
		assertSame(security, asm.getSecurity(entry));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity_ContId_ContractNotFound() throws Exception {
		expect(cache.getContract(eq(815))).andReturn(null);
		control.replay();
		
		assertNull(asm.getSecurity(815));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity_ConId_CreateNew() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(cache.getContract(eq(314))).andReturn(entry);
		expect(entry.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		expect(terminal.createSecurity(eq(descr))).andReturn(security);
		control.replay();
		
		assertSame(security, asm.getSecurity(314));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurity_ConId_ReturnExisting() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		expect(cache.getContract(eq(314))).andReturn(entry);
		expect(entry.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(true);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(security);
		control.replay();
		
		assertSame(security, asm.getSecurity(314));
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Security() throws Exception {
		ContractDetails details = new ContractDetails();
		details.m_summary.m_symbol = "AAPL";
		details.m_summary.m_primaryExch = "LSE";
		details.m_summary.m_currency = "USD";
		details.m_summary.m_secType = "STK";
		details.m_minTick = 0.01d;
		details.m_longName = "Apple Inc.";
		ContractEntry entry = new ContractEntry(details);
		security.setDisplayName(eq("Apple Inc."));
		security.setLotSize(eq(1));
		security.setMinStepPrice(eq(0.01));
		security.setMinStepSize(eq(0.01));
		security.setPrecision(eq(2));
		control.replay();
		
		asm.update(security, entry);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Security_SkipNoChanges() throws Exception {
		expect(security.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireEvents(security);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Security_ForAvailable() throws Exception {
		expect(security.hasChanged()).andReturn(true);
		expect(security.isAvailable()).andReturn(true);
		security.fireChangedEvent();
		security.resetChanges();
		control.replay();
		
		asm.fireEvents(security);
		
		control.verify();
	}

	@Test
	public void testFireEvents_Security_ForNew() throws Exception {
		expect(security.hasChanged()).andReturn(true);
		expect(security.isAvailable()).andReturn(false);
		security.setAvailable(true);
		terminal.fireSecurityAvailableEvent(same(security));
		security.resetChanges();
		control.replay();
		
		asm.fireEvents(security);
		
		control.verify();
	}
	
	@Test
	public void testStartMktData() throws Exception {
		ContractEntry entry = control.createMock(ContractEntry.class);
		Contract expected = new Contract();
		expect(entry.getDefaultContract()).andStubReturn(expected);
		expect(numerator.incrementAndGet()).andReturn(180);
		client.setContractHandler(eq(180),
			eq(new IBRequestMarketDataHandler(terminal, security, 180, entry)));
		client.reqMktData(eq(180), same(expected), (String)isNull(), eq(false));
		control.replay();
		
		asm.startMktData(security, entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_Position() throws Exception {
		/**
		 * Конструктор.
		 * <p>
		 * @param contract контракт
		 * @param position текущая позиция
		 * @param marketValue рыночная стоимость позиции
		 * @param averageCost балансовая цена за единицу
		 * @param realizedPNL вариационка реализованной ранее позиции
		 * @param accountName код торгового счета
		 */
		PositionEntry entry = new PositionEntry(new Contract(), -15, -150.0d,
				9.0d, -12.0d, "BEST");
		position.setBookValue(eq(-135.0d));
		position.setCurrQty(eq(-15L));
		position.setMarketValue(eq(-150.0d));
		position.setVarMargin(eq(-27.0d));
		control.replay();
		
		asm.update(position, entry);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Position_SkipNoChanges() throws Exception {
		expect(position.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireEvents(position);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Position_ForAvailable() throws Exception {
		expect(position.hasChanged()).andReturn(true);
		expect(position.isAvailable()).andReturn(true);
		position.fireChangedEvent();
		position.resetChanges();
		control.replay();
		
		asm.fireEvents(position);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Position_ForNew() throws Exception {
		expect(position.hasChanged()).andReturn(true);
		expect(position.isAvailable()).andReturn(false);
		position.setAvailable(eq(true));
		expect(position.getPortfolio()).andReturn(port);
		port.firePositionAvailableEvent(same(position));
		position.resetChanges();
		control.replay();
		
		asm.fireEvents(position);
		
		control.verify();
	}

}
