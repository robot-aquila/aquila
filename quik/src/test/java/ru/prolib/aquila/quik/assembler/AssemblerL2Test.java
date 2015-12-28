package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.data.row.RowException;
import ru.prolib.aquila.quik.QUIKTerminal;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;

public class AssemblerL2Test {
	private static Account account;
	private static QUIKSymbol symbol;
	private IMocksControl control;
	private QUIKTerminal terminal;
	private Cache cache;
	private AssemblerL2 asm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("TEST", "1", "2");
		symbol = new QUIKSymbol("SBER", "EQBR", ISO4217.RUB,
				SymbolType.STOCK, "SBER", "Сбербанк", "АО СБЕРБАНК");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		cache = control.createMock(Cache.class);
		asm = new AssemblerL2(terminal);
		
		expect(terminal.getDataCache()).andStubReturn(cache);
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

		assertEquals(1, Portfolio.VERSION);
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position_NoSymbol() throws Exception {
		PositionEntry entry = new PositionEntry(account, "LKOH", 1L, 0L, 12d);
		expect(cache.getSymbol(eq("LKOH"))).andReturn(null);
		control.replay();
		
		assertFalse(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Position_NoSecurity() throws Exception {
		PositionEntry entry = new PositionEntry(account, "LKOH", 1L, 0L, 12d);
		expect(cache.getSymbol(eq("LKOH"))).andReturn(symbol);
		expect(terminal.getSecurity(symbol))
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
		expect(cache.getSymbol(eq("LKOH"))).andReturn(symbol);
		expect(terminal.getSecurity(symbol)).andReturn(security);
		expect(terminal.getEditablePortfolio(eq(account))).andReturn(p);
		expect(p.getEditablePosition(security)).andReturn(pos);
		pos.setOpenQty(eq(1L));
		pos.setCurrQty(eq(0L));
		pos.setVarMargin(eq(12d));
		p.fireEvents(same(pos));
		control.replay();

		assertEquals(1, Position.VERSION);
		assertTrue(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Security() throws Exception {
		SecurityEntry entry = new SecurityEntry(1, 142820d, 132480d,
				6.48020d, 10d, 0, 137950d, 137000d, 136900d, "RTS-9.13",
				"RIU3", 138040d, 137990d, 138100d, 136050d, "RIU3", "SPBFUT",
				ISO4217.USD, SymbolType.FUTURE,
				132020d, 12450d);
		symbol = new QUIKSymbol("RTS-9.13", "SPBFUT", ISO4217.USD,
				SymbolType.FUTURE, "RIU3", "RIU3", "RTS-9.13");
		
		EditableSecurity security = control.createMock(EditableSecurity.class);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(security);
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
		security.setInitialPrice(eq(132020d));
		security.setInitialMargin(eq(12450d));
		terminal.fireEvents(same(security));
		control.replay();
		
		assertEquals(2, Security.VERSION);
		asm.tryAssemble(entry);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		QUIKTerminal t = control.createMock(QUIKTerminal.class);
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
		Trade trade1 = new Trade(terminal), trade2 = new Trade(terminal);
		trade1.setId(1L); trade1.setSymbol(symbol);
		trade2.setId(2L); trade2.setSymbol(symbol);
		
		expect(entry.access(same(terminal))).andReturn(trade1);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade1));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade2);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade2));
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
		Trade trade1 = new Trade(terminal), trade2 = new Trade(terminal);
		trade1.setId(1L); trade1.setSymbol(symbol);
		trade2.setId(2L); trade2.setSymbol(symbol);
		
		expect(entry.access(same(terminal))).andReturn(trade1);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade1));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade2);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade2));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(null);
		
		control.replay();
		
		assertFalse(asm.tryAssemble(entry));
		
		control.verify();
	}
	
	@Test
	public void testAssemble_Trades_SkipDuplicatesById() throws Exception {
		EditableSecurity s = control.createMock(EditableSecurity.class);
		TradesEntry entry = control.createMock(TradesEntry.class);
		Trade trade1, trade2, trade3;
		trade1 = new Trade(terminal);
		trade2 = new Trade(terminal);
		trade3 = new Trade(terminal);
		trade1.setId(98L); trade1.setSymbol(symbol);
		trade2.setId(99L); trade2.setSymbol(symbol);
		trade3.setId(98L); trade3.setSymbol(symbol); // duplicate

		expect(entry.access(same(terminal))).andReturn(trade1);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade1));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade2);
		expect(terminal.getEditableSecurity(eq(symbol))).andReturn(s);
		s.fireTradeEvent(same(trade2));
		expect(entry.next()).andReturn(true);
		
		expect(entry.access(same(terminal))).andReturn(trade3);
		expect(entry.next()).andReturn(false);
		control.replay();
		
		assertTrue(asm.tryAssemble(entry));

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
	
	@Test
	public void testTryGetOrder_LocalOrderNotExists() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getTransId()).andReturn(123L);
		expect(terminal.isOrderExists(eq(123))).andReturn(false);
		control.replay();
		
		assertNull(asm.tryGetOrder(entry));
		
		control.verify();
	}

	@Test
	public void testTryGetOrder_GetOrderException() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getTransId()).andReturn(123L);
		expect(terminal.isOrderExists(eq(123))).andReturn(true);
		terminal.getEditableOrder(eq(123));
		expectLastCall().andThrow(new OrderNotExistsException(123L));
		control.replay();
		
		assertNull(asm.tryGetOrder(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryGetOrder_FinalStatus() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(entry.getTransId()).andReturn(123L);
		expect(terminal.isOrderExists(eq(123))).andReturn(true);
		expect(terminal.getEditableOrder(eq(123))).andReturn(order);
		expect(order.getStatus()).andReturn(OrderStatus.CANCELLED);
		control.replay();
		
		assertNull(asm.tryGetOrder(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryGetOrder_Ok() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(entry.getTransId()).andReturn(123L);
		expect(terminal.isOrderExists(eq(123))).andReturn(true);
		expect(terminal.getEditableOrder(eq(123))).andReturn(order);
		expect(order.getStatus()).andReturn(OrderStatus.SENT);
		control.replay();
		
		assertEquals(1, Order.VERSION);
		assertSame(order, asm.tryGetOrder(entry));
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Trade_SkipExisting() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		T2QTrade entry = control.createMock(T2QTrade.class);
		expect(entry.getId()).andStubReturn(814L);
		expect(order.hasTrade(eq(814L))).andReturn(true);
		control.replay();
		
		asm.tryAssemble(order, entry);
		
		control.verify();
	}
	
	@Test
	public void testTryAssemble_Trade_Ok() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(order.getDirection()).andStubReturn(Direction.SELL);
		expect(order.getId()).andStubReturn(832);
		expect(order.getSymbol()).andStubReturn(symbol);
		
		T2QTrade entry = control.createMock(T2QTrade.class);
		expect(entry.getId()).andStubReturn(814L);
		expect(entry.getPrice()).andStubReturn(12.34d);
		expect(entry.getQty()).andStubReturn(1000L);
		expect(entry.getDate()).andStubReturn(20130722L);
		expect(entry.getTime()).andStubReturn(93317L);
		expect(entry.getValue()).andStubReturn(123400d);
		
		Trade expected = new Trade(terminal);
		expected.setDirection(Direction.SELL);
		expected.setId(814L);
		expected.setOrderId(832L);
		expected.setPrice(12.34d);
		expected.setQty(1000L);
		expected.setSymbol(symbol);
		expected.setTime(LocalDateTime.of(2013, 7, 22, 9, 33, 17));
		expected.setVolume(123400d);

		expect(order.hasTrade(eq(814L))).andReturn(false);
		order.addTrade(eq(expected));
		order.fireTradeEvent(eq(expected));
		control.replay();
		
		asm.tryAssemble(order, entry);
		
		control.verify();
	}
	
	@Test
	public void testTryActivate_NotRequired() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.CONDITION,
				OrderStatus.FILLED,
				OrderStatus.PENDING,
				OrderStatus.REJECTED,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			EditableOrder order = control.createMock(EditableOrder.class);
			expect(order.getStatus()).andReturn(expected[i]);
			control.replay();
			
			asm.tryActivate(order);
			
			control.verify();
		}
	}
	
	@Test
	public void testTryActivate_Activate() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		expect(order.getId()).andStubReturn(512);
		expect(order.getStatus()).andReturn(OrderStatus.SENT);
		order.setStatus(eq(OrderStatus.ACTIVE));
		terminal.fireEvents(same(order));
		control.replay();
		
		asm.tryActivate(order);
		
		control.verify();
	}
	
	@Test
	public void testTryFinalize_FilledOk() throws Exception {
		EditableOrder order = control.createMock(EditableOrder.class);
		T2QOrder entry = control.createMock(T2QOrder.class);
		LocalDateTime time = LocalDateTime.now();
		expect(order.getId()).andStubReturn(829);
		expect(order.getQtyRest()).andReturn(0L);
		order.setStatus(eq(OrderStatus.FILLED));
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		terminal.fireEvents(same(order));
		control.replay();
		
		assertTrue(asm.tryFinalize(order, entry));
		
		control.verify();
	}
	
	@Test
	public void testTryFinalize_KilledOk() throws Exception {
		LocalDateTime time = LocalDateTime.now();
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getBalance()).andStubReturn(5L);
		expect(entry.getStatus()).andStubReturn(2);

		EditableOrder order = control.createMock(EditableOrder.class);
		expect(order.getId()).andStubReturn(394);
		expect(order.getQtyRest()).andReturn(5L);
		order.setStatus(eq(OrderStatus.CANCELLED));
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		terminal.fireEvents(same(order));
		control.replay();
		
		assertTrue(asm.tryFinalize(order, entry));
		
		control.verify();
	}
	
	@Test
	public void testTryFinalize_SkipKilledButNotAdjusted() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getBalance()).andStubReturn(5L);
		expect(entry.getStatus()).andStubReturn(2);

		EditableOrder order = control.createMock(EditableOrder.class);
		expect(order.getQtyRest()).andReturn(6L);
		control.replay();
		
		assertFalse(asm.tryFinalize(order, entry));
		
		control.verify();
	}
	
	@Test
	public void testTryFinalize_SkipActive() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		expect(entry.getBalance()).andStubReturn(5L);
		expect(entry.getStatus()).andStubReturn(1);

		EditableOrder order = control.createMock(EditableOrder.class);
		expect(order.getQtyRest()).andReturn(5L);
		control.replay();
		
		assertFalse(asm.tryFinalize(order, entry));
		
		control.verify();
	}

}
