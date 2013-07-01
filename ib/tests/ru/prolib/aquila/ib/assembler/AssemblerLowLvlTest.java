package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.BusinessEntities.*;
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
	private EditableOrder order;
	private AssemblerLowLvl asm;
	
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
		order = control.createMock(EditableOrder.class);
		asm = new AssemblerLowLvl(terminal);
		
		expect(terminal.getCache()).andStubReturn(cache);
		expect(terminal.getClient()).andStubReturn(client);
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
		expect(entry.getContractId()).andStubReturn(82413);
		expect(entry.getDefaultExchange()).andStubReturn("foobar");
		expect(client.nextReqId()).andReturn(180);
		client.setContractHandler(eq(180),
			eq(new IBRequestMarketDataHandler(terminal, security, 180, entry)));
		Contract expected = new Contract();
		expected.m_conId = 82413;
		expected.m_exchange = "foobar";
		client.reqMktData(eq(180), eq(expected), (String) isNull(), eq(false));
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
	
	@Test
	public void testFireStopOrderEvents_AvailNotChanged() throws Exception {
		expect(order.isAvailable()).andReturn(true);
		expect(order.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireStopOrderEvents(order);
		
		control.verify();
	}
	
	@Test
	public void testFireStopOrderEvents_AvailChanged() throws Exception {
		expect(order.isAvailable()).andReturn(true);
		expect(order.hasChanged()).andReturn(true);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		asm.fireStopOrderEvents(order);
		
		control.verify();
	}
	
	@Test
	public void testFireStopOrderEvents_NotAvailNotChanged() throws Exception {
		expect(order.isAvailable()).andReturn(false);
		order.setAvailable(eq(true));
		terminal.fireStopOrderAvailableEvent(same(order));
		expect(order.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireStopOrderEvents(order);
		
		control.verify();
	}

	@Test
	public void testFireStopOrderEvents_NotAvailChanged() throws Exception {
		expect(order.isAvailable()).andReturn(false);
		order.setAvailable(eq(true));
		terminal.fireStopOrderAvailableEvent(same(order));
		expect(order.hasChanged()).andReturn(true);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		asm.fireStopOrderEvents(order);
		
		control.verify();
	}

	
	@Test
	public void testUpdateStopOrder() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getAccount()).andStubReturn(new Account("TEST"));
		expect(entry.getContractId()).andStubReturn(11534);
		expect(entry.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(entry.getPrice()).andStubReturn(815.05d);
		expect(entry.getQty()).andStubReturn(1000L);
		expect(entry.getStopLimitPrice()).andStubReturn(820.01d);
		expect(entry.getType()).andStubReturn(OrderType.STOP_LIMIT);
		
		ContractEntry contrEntry = control.createMock(ContractEntry.class);
		expect(contrEntry.getSecurityDescriptor()).andStubReturn(descr);
		expect(cache.getContract(11534)).andStubReturn(contrEntry);
		
		order.setAccount(eq(new Account("TEST")));
		order.setDirection(eq(OrderDirection.SELL));
		order.setPrice(eq(815.05d));
		order.setQty(eq(1000L));
		order.setSecurityDescriptor(eq(descr));
		order.setStopLimitPrice(eq(820.01d));
		order.setType(eq(OrderType.STOP_LIMIT));
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		control.replay();
		
		asm.updateStopOrder(order, entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrderStatus_SkipNoStatusEntry() throws Exception {
		expect(order.getId()).andStubReturn(815L);
		expect(cache.getOrderStatus(eq(815L))).andReturn(null);
		control.replay();
		
		asm.adjustStopOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrderStatus_SkipNullStatus() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(order.getId()).andStubReturn(5815L);
		expect(cache.getOrderStatus(eq(5815L))).andReturn(entry);
		expect(entry.getStatus()).andReturn(null);
		control.replay();
		
		asm.adjustStopOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrderStatus_SkipActive() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(order.getId()).andStubReturn(1815L);
		expect(cache.getOrderStatus(eq(1815L))).andReturn(entry);
		expect(entry.getStatus()).andReturn(OrderStatus.ACTIVE);
		control.replay();
		
		asm.adjustStopOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrderStatus_Filled() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(order.getId()).andStubReturn(8115L);
		expect(cache.getOrderStatus(eq(8115L))).andReturn(entry);
		expect(entry.getStatus()).andReturn(OrderStatus.FILLED);
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		order.setStatus(eq(OrderStatus.FILLED));
		control.replay();
		
		asm.adjustStopOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustStopOrderStatus_Cancelled() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(order.getId()).andStubReturn(7216L);
		expect(cache.getOrderStatus(eq(7216L))).andReturn(entry);
		expect(entry.getStatus()).andReturn(OrderStatus.CANCELLED);
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		order.setStatus(eq(OrderStatus.CANCELLED));
		control.replay();
		
		asm.adjustStopOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testFireOrderEvents_AvailNotChanged() throws Exception {
		expect(order.isAvailable()).andReturn(true);
		expect(order.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireOrderEvents(order);
		
		control.verify();
	}
	
	@Test
	public void testFireOrderEvents_AvailChanged() throws Exception {
		expect(order.isAvailable()).andReturn(true);
		expect(order.hasChanged()).andReturn(true);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		asm.fireOrderEvents(order);
		
		control.verify();
	}

	@Test
	public void testFireOrderEvents_NotAvailNotChanged() throws Exception {
		expect(order.isAvailable()).andReturn(false);
		order.setAvailable(eq(true));
		terminal.fireOrderAvailableEvent(same(order));
		expect(order.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireOrderEvents(order);
		
		control.verify();
	}

	@Test
	public void testFireOrderEvents_NotAvailChanged() throws Exception {
		expect(order.isAvailable()).andReturn(false);
		order.setAvailable(eq(true));
		terminal.fireOrderAvailableEvent(same(order));
		expect(order.hasChanged()).andReturn(true);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		asm.fireOrderEvents(order);
		
		control.verify();
	}
	
	@Test
	public void testUpdateOrder_() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getAccount()).andStubReturn(new Account("BEST"));
		expect(entry.getContractId()).andStubReturn(445);
		expect(entry.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(entry.getPrice()).andStubReturn(212.15d);
		expect(entry.getQty()).andStubReturn(100L);
		expect(entry.getType()).andStubReturn(OrderType.LIMIT);
		
		ContractEntry contrEntry = control.createMock(ContractEntry.class);
		expect(contrEntry.getSecurityDescriptor()).andStubReturn(descr);
		expect(cache.getContract(445)).andStubReturn(contrEntry);
		
		order.setAccount(eq(new Account("BEST")));
		order.setDirection(eq(OrderDirection.BUY));
		order.setPrice(eq(212.15d));
		order.setQty(eq(100L));
		order.setQtyRest(eq(100L));
		order.setSecurityDescriptor(eq(descr));
		order.setType(eq(OrderType.LIMIT));
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		control.replay();
		
		asm.updateOrder(order, entry);
		
		control.verify();
	}
	
	@Test
	public void testUpdateOrder_SkipPriceForMarket() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getAccount()).andStubReturn(new Account("BEST"));
		expect(entry.getContractId()).andStubReturn(445);
		expect(entry.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(entry.getPrice()).andStubReturn(212.15d);
		expect(entry.getQty()).andStubReturn(100L);
		expect(entry.getType()).andStubReturn(OrderType.MARKET);
		
		ContractEntry contrEntry = control.createMock(ContractEntry.class);
		expect(contrEntry.getSecurityDescriptor()).andStubReturn(descr);
		expect(cache.getContract(445)).andStubReturn(contrEntry);
		
		order.setAccount(eq(new Account("BEST")));
		order.setDirection(eq(OrderDirection.BUY));
		order.setQty(eq(100L));
		order.setQtyRest(eq(100L));
		order.setSecurityDescriptor(eq(descr));
		order.setType(eq(OrderType.MARKET));
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setTime(same(time));
		control.replay();
		
		asm.updateOrder(order, entry);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderTrades() throws Exception {
		control.replay();
		
		asm.adjustOrderTrades(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderStatus_SkipNoStatusEntry() throws Exception {
		expect(order.getId()).andStubReturn(824L);
		expect(cache.getOrderStatus(eq(824L))).andReturn(null);
		control.replay();
		
		asm.adjustOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderStatus_SkipNullStatus() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getStatus()).andStubReturn(null);
		expect(order.getId()).andStubReturn(8224L);
		expect(cache.getOrderStatus(eq(8224L))).andReturn(entry);
		control.replay();
		
		asm.adjustOrderStatus(order);
		
		control.verify();
	}

	@Test
	public void testAdjustOrderStatus_Active() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		expect(entry.getAvgExecutedPrice()).andStubReturn(10.05d);
		expect(entry.getQtyRest()).andStubReturn(2L);
		expect(order.getId()).andStubReturn(1824L);
		expect(order.getQty()).andStubReturn(10L);
		expect(cache.getOrderStatus(eq(1824L))).andReturn(entry);
		
		order.setQtyRest(eq(2L));
		order.setAvgExecutedPrice(eq(10.05d));
		order.setExecutedVolume(eq(10.05d * 8));
		order.setStatus(eq(OrderStatus.ACTIVE));
		control.replay();
		
		asm.adjustOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderStatus_Filled() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getStatus()).andStubReturn(OrderStatus.FILLED);
		expect(entry.getAvgExecutedPrice()).andStubReturn(15.25d);
		expect(entry.getQtyRest()).andStubReturn(4L);
		expect(order.getId()).andStubReturn(881L);
		expect(order.getQty()).andStubReturn(8L);
		expect(cache.getOrderStatus(eq(881L))).andReturn(entry);
		
		order.setQtyRest(eq(4L));
		order.setAvgExecutedPrice(eq(15.25d));
		order.setExecutedVolume(eq(15.25d * 4));
		order.setStatus(eq(OrderStatus.FILLED));
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		control.replay();
		
		asm.adjustOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderStatus_Cancelled() throws Exception {
		OrderStatusEntry entry = control.createMock(OrderStatusEntry.class);
		expect(entry.getStatus()).andStubReturn(OrderStatus.CANCELLED);
		expect(entry.getAvgExecutedPrice()).andStubReturn(115.25d);
		expect(entry.getQtyRest()).andStubReturn(2L);
		expect(order.getId()).andStubReturn(2881L);
		expect(order.getQty()).andStubReturn(4L);
		expect(cache.getOrderStatus(eq(2881L))).andReturn(entry);
		
		order.setQtyRest(eq(2L));
		order.setAvgExecutedPrice(eq(115.25d));
		order.setExecutedVolume(eq(115.25d * 2));
		order.setStatus(eq(OrderStatus.CANCELLED));
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		control.replay();
		
		asm.adjustOrderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists_NoContract() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getContractId()).andStubReturn(9912);
		expect(cache.getContract(eq(9912))).andReturn(null);
		control.replay();
		
		assertFalse(asm.isSecurityExists(entry));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists_NoSecurity() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getContractId()).andStubReturn(19912);
		ContractEntry conEntry = control.createMock(ContractEntry.class);
		expect(cache.getContract(eq(19912))).andReturn(conEntry);
		expect(conEntry.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		control.replay();
		
		assertFalse(asm.isSecurityExists(entry));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityExists_Ok() throws Exception {
		OrderEntry entry = control.createMock(OrderEntry.class);
		expect(entry.getContractId()).andStubReturn(99121);
		ContractEntry conEntry = control.createMock(ContractEntry.class);
		expect(cache.getContract(eq(99121))).andReturn(conEntry);
		expect(conEntry.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		control.replay();
		
		assertFalse(asm.isSecurityExists(entry));
		
		control.verify();
	}

}
