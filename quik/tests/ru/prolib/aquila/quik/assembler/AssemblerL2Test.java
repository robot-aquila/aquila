package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.quik.QUIKEditableTerminal;
import ru.prolib.aquila.quik.assembler.cache.*;

public class AssemblerL2Test {
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private QUIKEditableTerminal terminal;
	private EditableSecurities securities;
	private EditablePortfolios portfolios;
	private Cache cache;
	private AssemblerL2 asm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("TEST", "1", "2");
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKEditableTerminal.class);
		securities = control.createMock(EditableSecurities.class);
		portfolios = control.createMock(EditablePortfolios.class);
		cache = control.createMock(Cache.class);
		asm = new AssemblerL2(terminal);
		
		expect(terminal.getDataCache()).andStubReturn(cache);
		expect(terminal.getSecuritiesInstance()).andStubReturn(securities);
		expect(terminal.getPortfoliosInstance()).andStubReturn(portfolios);
	}
	
	@Test
	public void testTryAssemble_Portfolio_Ok() throws Exception {
		PortfolioEntry entry = new PortfolioEntry(account, 20d, 10d, -1d);
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		expect(terminal.getEditablePortfolio(eq(account))).andReturn(p);
		p.setBalance(eq(20d));
		p.setCash(eq(10d));
		p.setVariationMargin(eq(-1d));
		terminal.fireEvents(same(p));
		control.replay();

		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position_NoDescr() throws Exception {
		PositionEntry entry = new PositionEntry(account, "LKOH", 1L, 0L, 12d);
		expect(cache.getDescriptor(eq("LKOH"))).andReturn(null);
		control.replay();
		
		assertFalse(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position_NoSecurity() throws Exception {
		PositionEntry entry = new PositionEntry(account, "LKOH", 1L, 0L, 12d);
		expect(cache.getDescriptor(eq("LKOH"))).andReturn(descr);
		expect(terminal.getSecurity(descr))
			.andThrow(new SecurityException("test error"));
		control.replay();
		
		assertFalse(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position_Ok() throws Exception {
		PositionEntry entry = new PositionEntry(account, "LKOH", 1L, 0L, 12d);
		EditableSecurity security = control.createMock(EditableSecurity.class);
		EditablePortfolio p = control.createMock(EditablePortfolio.class);
		EditablePosition pos = control.createMock(EditablePosition.class);
		expect(cache.getDescriptor(eq("LKOH"))).andReturn(descr);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(terminal.getEditablePortfolio(eq(account))).andReturn(p);
		expect(p.getEditablePosition(security)).andReturn(pos);
		pos.setOpenQty(eq(1L));
		pos.setCurrQty(eq(0L));
		pos.setVarMargin(eq(12d));
		p.fireEvents(same(pos));
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Security() throws Exception {
		SecurityEntry entry = new SecurityEntry(1, 142820d, 132480d,
				6.48020d, 10d, 0, 137950d, 137000d, 136900d, "RTS-9.13",
				"RIU3", 138040d, 137990d, 138100d, 136050d, descr);
		EditableSecurity security = control.createMock(EditableSecurity.class);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(security);
		security.setLotSize(eq(1));
		security.setMaxPrice(eq(142820d));
		security.setMinPrice(eq(132480d));
		security.setMinStepPrice(eq(6.48020d));
		security.setMinStepSize(eq(10d));
		security.setPrecision(eq(0));
		security.setLastPrice(eq(137950d));
		security.setOpenPrice(eq(137000d));
		security.setClosePrice(eq(136900d));
		security.setDisplayName(eq("RTS-9.13"));
		security.setAskPrice(eq(138040d));
		security.setBidPrice(eq(137990d));
		security.setHighPrice(eq(138100d));
		security.setLowPrice(eq(136050d));
		terminal.fireEvents(same(security));
		control.replay();
		
		asm.tryAssemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		QUIKEditableTerminal t = control.createMock(QUIKEditableTerminal.class);
		assertTrue(asm.equals(asm));
		assertTrue(asm.equals(new AssemblerL2(terminal)));
		assertFalse(asm.equals(new AssemblerL2(t)));
		assertFalse(asm.equals(null));
		assertFalse(asm.equals(this));
	}
	
	@Test
	public void testAssemble_Trades_BlockFinished() throws Exception {
		// Две сделки в блоке и блок завершен
		EditableSecurity s = control.createMock(EditableSecurity.class);
		TradesEntry entry = control.createMock(TradesEntry.class);
		Trade trade = control.createMock(Trade.class);
		
		expect(entry.access(same(terminal))).andReturn(trade);
		expect(trade.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(s);
		s.fireTradeEvent(same(trade));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade);
		expect(trade.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(s);
		s.fireTradeEvent(same(trade));
		expect(entry.next()).andReturn(false);
		
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_BlockNotFinished() throws Exception {
		// Две сделки в блоке и блок не завершен
		EditableSecurity s = control.createMock(EditableSecurity.class);
		TradesEntry entry = control.createMock(TradesEntry.class);
		Trade trade = control.createMock(Trade.class);
		
		expect(entry.access(same(terminal))).andReturn(trade);
		expect(trade.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(s);
		s.fireTradeEvent(same(trade));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade);
		expect(trade.getSecurityDescriptor()).andReturn(descr);
		expect(terminal.getEditableSecurity(eq(descr))).andReturn(s);
		s.fireTradeEvent(same(trade));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(null);
		
		control.replay();
		
		assertFalse(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_AccessException() throws Exception {
		// Исключение при доступе к сделке
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(entry.access(same(terminal))).andThrow(new RowException("test"));
		expect(entry.count()).andReturn(1024);
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}

}
