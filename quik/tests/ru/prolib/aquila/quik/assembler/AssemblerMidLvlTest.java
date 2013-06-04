package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerMidLvlTest {
	private static SimpleDateFormat format;
	private static Account account, account2;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache;
	private AssemblerLowLvl low;
	private EditableOrder order;
	private OrderCache entryOrder;
	private StopOrderCache entryStopOrder;
	private EditablePortfolio portfolio;
	private PortfolioFCache entryPortF;
	private EditableSecurity security;
	private SecurityCache entrySec;
	private EditablePosition position;
	private PositionFCache entryPosF;
	private AssemblerMidLvl middle;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		account = new Account("FIRM", "3644", "LX01");
		account2 = new Account("SPBFUT", "eqe01", "eqe01");
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		cache = control.createMock(Cache.class);
		low = control.createMock(AssemblerLowLvl.class);
		order = control.createMock(EditableOrder.class);
		entryOrder = control.createMock(OrderCache.class);
		entryStopOrder = control.createMock(StopOrderCache.class);
		portfolio = control.createMock(EditablePortfolio.class);
		security = control.createMock(EditableSecurity.class);
		position = control.createMock(EditablePosition.class);
		entryPortF = new PortfolioFCache("eqe01","SPBFUT",100.0d,80.0d,-10.0d);
		entrySec = new SecurityCache(10, 150000.0d, 140000.0d,
				6.2188d, 10.0d, 0, 143870.0d, 151000.0d, // open
				150900.0d, "RTS-6.13", "RIM3", 143800.0d, 143900.0, // bid
				151500.0d, 143810.0d, descr);
		entryPosF = new PositionFCache("eqe01","SPBFUT","RIM3", 10L, 0L, 10.0d);
		middle = new AssemblerMidLvl(terminal, cache, low);
	}
	
	@Test
	public void testCheckIfOrderRemoved_NotActive() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.FILLED);
		control.replay();
		
		assertFalse(middle.checkIfOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCheckIfOrderRemoved_TooYoungOrder() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:00");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		control.replay();

		assertFalse(middle.checkIfOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCheckIfOrderRemoved_Removed() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:01");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getId()).andStubReturn(829L);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(cache.getOrderCache(829L)).andReturn(null);
		order.setStatus(OrderStatus.CANCELLED);
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		assertTrue(middle.checkIfOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCheckIfOrderRemoved_NotRemoved() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:01");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getId()).andStubReturn(829L);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(cache.getOrderCache(829L)).andReturn(entryOrder);
		control.replay();
		
		assertFalse(middle.checkIfOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCheckIfStopOrderRemoved_NotActive() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.FILLED);
		control.replay();
		
		assertFalse(middle.checkIfStopOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCheckIfStopOrderRemoved_TooYoungOrder() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:00");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		control.replay();

		assertFalse(middle.checkIfStopOrderRemoved(order));
		
		control.verify();
	}

	@Test
	public void testCheckIfStopOrderRemoved_Removed() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:01");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getId()).andStubReturn(792L);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(cache.getStopOrderCache(792L)).andReturn(null);
		order.setStatus(OrderStatus.CANCELLED);
		Date time = new Date();
		expect(terminal.getCurrentTime()).andReturn(time);
		order.setLastChangeTime(time);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		
		assertTrue(middle.checkIfStopOrderRemoved(order));
		
		control.verify();
	}

	@Test
	public void testCheckIfStopOrderRemoved_NotRemoved() throws Exception {
		Date currTime = format.parse("2013-05-21 20:00:01");
		Date orderTime = format.parse("2013-05-21 19:00:00");
		expect(terminal.getCurrentTime()).andStubReturn(currTime);
		expect(order.getTime()).andStubReturn(orderTime);
		expect(order.getId()).andStubReturn(792L);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(cache.getStopOrderCache(792L)).andReturn(entryStopOrder);
		control.replay();
		
		assertFalse(middle.checkIfStopOrderRemoved(order));
		
		control.verify();
	}
	
	@Test
	public void testCreateNewOrder_NoAccount() throws Exception {
		expect(low.getAccountByOrderCache(entryOrder)).andReturn(null);
		expect(low.getSecurityDescriptorByOrderCache(entryOrder))
			.andReturn(descr);
		control.replay();
		
		middle.createNewOrder(entryOrder);
		
		control.verify();
	}
	
	@Test
	public void testCreateNewOrder_NoSecurity() throws Exception {
		expect(low.getAccountByOrderCache(entryOrder)).andReturn(account);
		expect(low.getSecurityDescriptorByOrderCache(entryOrder))
			.andReturn(null);
		control.replay();
		
		middle.createNewOrder(entryOrder);
		
		control.verify();
	}

	@Test
	public void testCreateNewOrder() throws Exception {
		Date time = new Date();
		entryOrder = new OrderCache(360L, 800L, OrderStatus.ACTIVE,
				"SBER", "EQBR", "LX01", "3644", OrderDirection.BUY,
				250L, 100L, 148.12d, time, null, OrderType.LIMIT);
		expect(low.getAccountByOrderCache(entryOrder)).andReturn(account);
		expect(low.getSecurityDescriptorByOrderCache(entryOrder))
			.andReturn(descr);
		expect(terminal.createOrder()).andReturn(order);
		// stage 1:
		order.setAccount(account);
		order.setSecurityDescriptor(descr);
		low.initNewOrder(same(entryOrder), same(order));
		// stage 2:
		order.setStatus(eq(OrderStatus.ACTIVE));
		low.fireOrderChanges(same(order));
		// stage 3:		
		List<TradeCache> trades = new Vector<TradeCache>();
		trades.add(control.createMock(TradeCache.class));
		trades.add(control.createMock(TradeCache.class));
		expect(cache.getAllTradesByOrderId(eq(360L))).andReturn(trades);
		expect(low.adjustOrderTrade(trades.get(0), order)).andReturn(true);
		expect(low.adjustOrderTrade(trades.get(1), order)).andReturn(true);
		// stage 4:		
		low.adjustOrderStatus(entryOrder, order);
		// stage 5:
		low.fireOrderChanges(same(order));
		control.replay();
		
		middle.createNewOrder(entryOrder);
		
		control.verify();
	}
	
	@Test
	public void testUpdateExistingOrder_NotActive() throws Exception {
		expect(entryOrder.getId()).andStubReturn(891L);
		expect(terminal.getEditableOrder(891L)).andReturn(order);
		expect(order.getStatus()).andReturn(OrderStatus.FILLED);
		control.replay();
		
		middle.updateExistingOrder(entryOrder);
		
		control.verify();
	}
	
	@Test
	public void testUpdateExistingOrder() throws Exception {
		expect(entryOrder.getId()).andStubReturn(891L);
		expect(terminal.getEditableOrder(891L)).andReturn(order);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		List<TradeCache> trades = new Vector<TradeCache>();
		trades.add(control.createMock(TradeCache.class));
		trades.add(control.createMock(TradeCache.class));
		expect(cache.getAllTradesByOrderId(eq(891L))).andReturn(trades);
		expect(low.adjustOrderTrade(trades.get(0), order)).andReturn(false);
		expect(low.adjustOrderTrade(trades.get(1), order)).andReturn(true);
		low.adjustOrderStatus(entryOrder, order);
		low.fireOrderChanges(same(order));
		control.replay();
		
		middle.updateExistingOrder(entryOrder);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePortfolioFORTS_New() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(false);
		expect(terminal.createPortfolio(account2)).andReturn(portfolio);
		portfolio.setBalance(100.0d);
		portfolio.setCash(80.0d);
		portfolio.setVariationMargin(-10.0d);
		expect(portfolio.isAvailable()).andReturn(false);
		cache.registerAccount(account2);
		terminal.firePortfolioAvailableEvent(portfolio);
		portfolio.setAvailable(true);
		portfolio.resetChanges();
		control.replay();
		
		middle.updatePortfolioFORTS(entryPortF);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePortfolioFORTS_UpdateExisting() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(true);
		expect(terminal.getEditablePortfolio(account2)).andReturn(portfolio);
		portfolio.setBalance(100.0d);
		portfolio.setCash(80.0d);
		portfolio.setVariationMargin(-10.0d);
		expect(portfolio.isAvailable()).andReturn(true);
		portfolio.fireChangedEvent();
		portfolio.resetChanges();
		control.replay();
		
		middle.updatePortfolioFORTS(entryPortF);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurity_New() throws Exception {
		expect(terminal.isSecurityExists(same(descr))).andReturn(false);
		expect(terminal.createSecurity(same(descr))).andReturn(security);
		security.setAskPrice(eq(143800.0d));
		security.setBidPrice(eq(143900.0d));
		security.setClosePrice(eq(150900.0d));
		security.setDisplayName(eq("RTS-6.13"));
		security.setHighPrice(eq(151500.0d));
		security.setLastPrice(eq(143870.0d));
		security.setLotSize(eq(10));
		security.setLowPrice(eq(143810.0d));
		security.setMaxPrice(eq(150000.0d));
		security.setMinPrice(eq(140000.0d));
		security.setMinStepPrice(eq(6.2188d));
		security.setMinStepSize(eq(10.0d));
		security.setOpenPrice(eq(151000.0d));
		security.setPrecision(eq(0));
		expect(security.isAvailable()).andReturn(false);
		cache.registerSecurityDescriptor(same(descr), eq("RIM3"));
		terminal.fireSecurityAvailableEvent(same(security));
		security.setAvailable(eq(true));
		security.resetChanges();
		control.replay();
		
		middle.updateSecurity(entrySec);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurity_UpdateExisting() throws Exception {
		expect(terminal.isSecurityExists(same(descr))).andReturn(true);
		expect(terminal.getEditableSecurity(same(descr))).andReturn(security);
		security.setAskPrice(eq(143800.0d));
		security.setBidPrice(eq(143900.0d));
		security.setClosePrice(eq(150900.0d));
		security.setDisplayName(eq("RTS-6.13"));
		security.setHighPrice(eq(151500.0d));
		security.setLastPrice(eq(143870.0d));
		security.setLotSize(eq(10));
		security.setLowPrice(eq(143810.0d));
		security.setMaxPrice(eq(150000.0d));
		security.setMinPrice(eq(140000.0d));
		security.setMinStepPrice(eq(6.2188d));
		security.setMinStepSize(eq(10.0d));
		security.setOpenPrice(eq(151000.0d));
		security.setPrecision(eq(0));
		expect(security.isAvailable()).andReturn(true);
		security.fireChangedEvent();
		security.resetChanges();
		control.replay();
		
		middle.updateSecurity(entrySec);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePositionFORTS_PortfolioNA() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(false);
		control.replay();
		
		middle.updatePositionFORTS(entryPosF);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePositionFORTS_DescrNA() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(false);
		control.replay();
		
		middle.updatePositionFORTS(entryPosF);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePositionFORTS_SecurityNA() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(false);
		control.replay();
		
		middle.updatePositionFORTS(entryPosF);
		
		control.verify();
	}
	
	@Test
	public void testUpdatePositionFORTS_New() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(account2)).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		position.setCurrQty(0L);
		position.setOpenQty(10L);
		position.setVarMargin(10.0d);
		expect(position.isAvailable()).andReturn(false);
		portfolio.firePositionAvailableEvent(position);
		position.setAvailable(true);
		position.resetChanges();
		control.replay();
		
		middle.updatePositionFORTS(entryPosF);
		
		control.verify();
	}

	@Test
	public void testUpdatePositionFORTS_UpdateExisting() throws Exception {
		expect(terminal.isPortfolioAvailable(account2)).andReturn(true);
		expect(cache.isSecurityDescriptorRegistered("RIM3")).andReturn(true);
		expect(cache.getSecurityDescriptorByName("RIM3")).andReturn(descr);
		expect(terminal.isSecurityExists(descr)).andReturn(true);
		expect(terminal.getEditablePortfolio(account2)).andReturn(portfolio);
		expect(terminal.getSecurity(descr)).andReturn(security);
		expect(portfolio.getEditablePosition(security)).andReturn(position);
		position.setCurrQty(0L);
		position.setOpenQty(10L);
		position.setVarMargin(10.0d);
		expect(position.isAvailable()).andReturn(true);
		position.fireChangedEvent();
		position.resetChanges();
		control.replay();
		
		middle.updatePositionFORTS(entryPosF);
		
		control.verify();
	}

	@Test
	public void testEquals() throws Exception {
		TerminalBuilder tb = new TerminalBuilder();
		CacheBuilder cb = new CacheBuilder();
		EditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		Cache c1 = cb.createCache(t1), c2 = cb.createCache(t2);
		middle = new AssemblerMidLvl(t1, c1, low);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(c1)
			.add(c2);
		Variant<AssemblerLowLvl> vLow = new Variant<AssemblerLowLvl>(vCache)
			.add(low)
			.add(control.createMock(AssemblerLowLvl.class));
		Variant<?> iterator = vLow;
		int foundCnt = 0;
		AssemblerMidLvl x = null, found = null;
		do {
			x = new AssemblerMidLvl(vTerm.get(), vCache.get(), vLow.get());
			if ( middle.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(c1, found.getCache());
		assertSame(low, found.getAssemblerLowLevel());
	}
	
	@Test
	public void testUpdateExistingStopOrder_SkipNotActive() throws Exception {
		OrderStatus skip[] = {
				OrderStatus.CANCELLED,
				OrderStatus.FAILED,
				OrderStatus.FILLED,
				OrderStatus.PENDING,
		};
		for ( int i = 0; i < skip.length; i ++ ) {
			setUp();
			expect(entryStopOrder.getId()).andReturn(231L);
			expect(terminal.getEditableStopOrder(231L)).andReturn(order);
			expect(order.getStatus()).andReturn(skip[i]);
			control.replay();
			
			middle.updateExistingStopOrder(entryStopOrder);
			
			control.verify();
		}
	}

	@Test
	public void testUpdateExistingStopOrder_StopLimit() throws Exception {
		expect(entryStopOrder.getId()).andReturn(427L);
		expect(terminal.getEditableStopOrder(427L)).andReturn(order);
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(order.getType()).andReturn(OrderType.STOP_LIMIT);
		low.adjustStopOrderStatus(entryStopOrder, order);
		order.fireChangedEvent();
		expect(order.hasChanged()).andReturn(true);
		order.resetChanges();
		control.replay();
		
		middle.updateExistingStopOrder(entryStopOrder);

		control.verify();
	}

	@Test
	public void testUpdateExistingStopOrder_TpAndTpsl() throws Exception {
		OrderType proc[] = { OrderType.TAKE_PROFIT, OrderType.TPSL };
		for ( int i = 0; i < proc.length; i ++ ) {
			setUp();
			expect(entryStopOrder.getId()).andReturn(812L);
			expect(terminal.getEditableStopOrder(812L)).andReturn(order);
			expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
			expect(order.getType()).andReturn(proc[i]);
			expect(entryStopOrder.getTakeProfitPrice()).andStubReturn(12.34d);
			expect(entryStopOrder.getStopLimitPrice()).andStubReturn(18.24d);
			expect(entryStopOrder.getPrice()).andStubReturn(18.15d);
			order.setTakeProfitPrice(12.34d);
			order.setStopLimitPrice(18.24d);
			order.setPrice(18.15d);
			low.adjustStopOrderStatus(entryStopOrder, order);
			order.fireChangedEvent();
			expect(order.hasChanged()).andReturn(true);
			order.resetChanges();
			control.replay();
			
			middle.updateExistingStopOrder(entryStopOrder);

			control.verify();
		}
	}
	
	@Test
	public void testCreateNewStopOrder_NoAccount() throws Exception {
		Date time = new Date();
		entryStopOrder = new StopOrderCache(120L, 872L, OrderStatus.CANCELLED,
				"SBER", "EQBR", "LX01", "3644", OrderDirection.SELL,
				200L, 200.0d, 198.0d, 210.0d,
				new Price(PriceUnit.MONEY, 1.0d),
				new Price(PriceUnit.PERCENT, 0.5d),
				219L, time, null, OrderType.STOP_LIMIT);
		expect(low.getAccountByStopOrderCache(entryStopOrder)).andReturn(null);
		expect(low.getSecurityDescriptorByStopOrderCache(entryStopOrder))
			.andReturn(descr);
		control.replay();
		
		assertFalse(middle.createNewStopOrder(entryStopOrder));
		
		control.verify();
	}
	
	@Test
	public void testCreateNewStopOrder_NoSecurity() throws Exception {
		Date time = new Date();
		entryStopOrder = new StopOrderCache(120L, 872L, OrderStatus.CANCELLED,
				"SBER", "EQBR", "LX01", "3644", OrderDirection.SELL,
				200L, 200.0d, 198.0d, 210.0d,
				new Price(PriceUnit.MONEY, 1.0d),
				new Price(PriceUnit.PERCENT, 0.5d),
				219L, time, null, OrderType.STOP_LIMIT);
		expect(low.getAccountByStopOrderCache(entryStopOrder))
			.andReturn(account);
		expect(low.getSecurityDescriptorByStopOrderCache(entryStopOrder))
			.andReturn(null);
		control.replay();
		
		assertFalse(middle.createNewStopOrder(entryStopOrder));
		
		control.verify();
	}
	
	@Test
	public void testCreateNewStopOrder() throws Exception {
		Date time = new Date();
		entryStopOrder = new StopOrderCache(120L, 872L, OrderStatus.CANCELLED,
				"SBER", "EQBR", "LX01", "3644", OrderDirection.SELL,
				2000L, 200.0d, 198.0d, 210.0d,
				new Price(PriceUnit.MONEY, 1.0d),
				new Price(PriceUnit.PERCENT, 0.5d),
				219L, time, null, OrderType.TPSL);
		expect(low.getAccountByStopOrderCache(entryStopOrder))
			.andReturn(account);
		expect(low.getSecurityDescriptorByStopOrderCache(entryStopOrder))
			.andReturn(descr);
		expect(terminal.createStopOrder()).andReturn(order);
		order.setAccount(account);
		order.setDirection(OrderDirection.SELL);
		order.setOffset(new Price(PriceUnit.MONEY, 1.0d));
		order.setPrice(200.0d);
		order.setQty(2000L);
		order.setSecurityDescriptor(descr);
		order.setSpread(new Price(PriceUnit.PERCENT, 0.5d));
		order.setStopLimitPrice(198.0d);
		order.setTakeProfitPrice(210.0d);
		order.setTime(time);
		order.setTransactionId(872L);
		order.setType(OrderType.TPSL);
		low.adjustStopOrderStatus(entryStopOrder, order);
		terminal.registerStopOrder(120L, order);
		order.setAvailable(true);
		terminal.fireStopOrderAvailableEvent(order);		
		control.replay();
		
		assertTrue(middle.createNewStopOrder(entryStopOrder));
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		low.start();
		control.replay();
		
		middle.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		low.stop();
		control.replay();
		
		middle.stop();
		
		control.verify();
	}

}
