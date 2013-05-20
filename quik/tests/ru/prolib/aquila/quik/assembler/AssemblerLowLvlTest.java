package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerLowLvlTest {
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache;
	private OrderCache orderEntry;
	private TradeCache tradeEntry;
	private EditableOrder order;
	private AssemblerLowLvl low;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account = new Account("FIRM", "3644", "LX01");
		descr = new SecurityDescriptor("SBER", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		cache = control.createMock(Cache.class);
		orderEntry = control.createMock(OrderCache.class);
		tradeEntry = control.createMock(TradeCache.class);
		order = control.createMock(EditableOrder.class);
		low = new AssemblerLowLvl(terminal, cache);
		
		expect(orderEntry.getId()).andStubReturn(896L);
		expect(orderEntry.getAccountCode()).andStubReturn("LX01");
		expect(orderEntry.getClientCode()).andStubReturn("3644");
		expect(orderEntry.getSecurityCode()).andStubReturn("SBER");
		expect(orderEntry.getSecurityClassCode()).andStubReturn("EQBR");
		expect(tradeEntry.getId()).andStubReturn(815L);
		expect(tradeEntry.getOrderId()).andStubReturn(896L);
	}
	
	@Test
	public void getAccountByOrderCache_NotRegistered() throws Exception {
		expect(cache.isAccountRegistered(eq("3644"), eq("LX01")))
			.andReturn(false);
		control.replay();
		
		assertNull(low.getAccountByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void getAccountByOrderCache_PortfolioNA() throws Exception {
		expect(cache.isAccountRegistered(eq("3644"), eq("LX01")))
			.andReturn(true);
		expect(cache.getAccount(eq("3644"), eq("LX01"))).andReturn(account);
		expect(terminal.isPortfolioAvailable(eq(account))).andReturn(false);
		control.replay();
		
		assertNull(low.getAccountByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void getAccountByOrderCache() throws Exception {
		expect(cache.isAccountRegistered(eq("3644"), eq("LX01")))
			.andReturn(true);
		expect(cache.getAccount(eq("3644"), eq("LX01"))).andReturn(account);
		expect(terminal.isPortfolioAvailable(eq(account))).andReturn(true);
		control.replay();
		
		assertSame(account, low.getAccountByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByOrderCache_NotRegistered()
		throws Exception
	{
		expect(cache.isSecurityDescriptorRegistered(eq("SBER"), eq("EQBR")))
			.andReturn(false);
		control.replay();
		
		assertNull(low.getSecurityDescriptorByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByOrderCache_SecurityNA()
		throws Exception
	{
		expect(cache.isSecurityDescriptorRegistered(eq("SBER"), eq("EQBR")))
			.andReturn(true);
		expect(cache.getSecurityDescriptorByCodeAndClass(eq("SBER"),eq("EQBR")))
			.andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(false);
		control.replay();
		
		assertNull(low.getSecurityDescriptorByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByOrderCache() throws Exception {
		expect(cache.isSecurityDescriptorRegistered(eq("SBER"), eq("EQBR")))
			.andReturn(true);
		expect(cache.getSecurityDescriptorByCodeAndClass(eq("SBER"),eq("EQBR")))
			.andReturn(descr);
		expect(terminal.isSecurityExists(eq(descr))).andReturn(true);
		control.replay();
		
		assertSame(descr, low.getSecurityDescriptorByOrderCache(orderEntry));
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderState_Filled() throws Exception {
		expect(order.getQtyRest()).andReturn(0L);
		order.setStatus(OrderStatus.FILLED);
		Date time = new Date();
		expect(order.getLastTradeTime()).andReturn(time);
		order.setLastChangeTime(same(time));
		control.replay();
		
		low.adjustOrderStatus(orderEntry, order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderState_Cancelled() throws Exception {
		expect(order.getQtyRest()).andReturn(10L);
		expect(orderEntry.getStatus()).andReturn(OrderStatus.CANCELLED);
		expect(orderEntry.getQtyRest()).andReturn(10L);
		expect(order.getQtyRest()).andReturn(10L);
		order.setStatus(OrderStatus.CANCELLED);
		Date time = new Date();
		expect(orderEntry.getWithdrawTime()).andReturn(time);
		order.setLastChangeTime(time);
		control.replay();
		
		low.adjustOrderStatus(orderEntry, order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderState_CancelledNotAdjusted() throws Exception {
		expect(order.getQtyRest()).andReturn(10L);
		expect(orderEntry.getStatus()).andReturn(OrderStatus.CANCELLED);
		expect(orderEntry.getQtyRest()).andReturn(10L);
		expect(order.getQtyRest()).andReturn(15L);
		control.replay();
		
		low.adjustOrderStatus(orderEntry, order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderState_Active() throws Exception {
		expect(order.getQtyRest()).andReturn(10L);
		expect(orderEntry.getStatus()).andReturn(OrderStatus.ACTIVE);
		order.setStatus(OrderStatus.ACTIVE);
		control.replay();
		
		low.adjustOrderStatus(orderEntry, order);
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderTrade_ExistsingTrade() throws Exception {
		expect(order.hasTrade(eq(815L))).andReturn(true);
		control.replay();
		
		assertFalse(low.adjustOrderTrade(tradeEntry, order));
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderTrade_NewTradeForAvailableOrder()
		throws Exception
	{
		expect(order.hasTrade(eq(815L))).andStubReturn(false);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);
		expect(tradeEntry.getPrice()).andStubReturn(12.34d);
		expect(tradeEntry.getQty()).andStubReturn(200L);
		Date time = new Date();
		expect(tradeEntry.getTime()).andStubReturn(time);
		expect(tradeEntry.getVolume()).andStubReturn(240.25d);
		Trade expected = new Trade(terminal);
		expected.setDirection(OrderDirection.SELL);
		expected.setId(815L);
		expected.setOrderId(896L);
		expected.setSecurityDescriptor(descr);
		expected.setPrice(12.34d);
		expected.setQty(200L);
		expected.setTime(time);
		expected.setVolume(240.25d);
		order.addTrade(eq(expected));
		expect(order.isAvailable()).andReturn(true);
		order.fireTradeEvent(eq(expected));
		control.replay();

		assertTrue(low.adjustOrderTrade(tradeEntry, order));
		
		control.verify();
	}
	
	@Test
	public void testAdjustOrderTrade_NewTradeForNewOrder() throws Exception {
		expect(order.hasTrade(eq(815L))).andStubReturn(false);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getSecurityDescriptor()).andStubReturn(descr);
		expect(tradeEntry.getPrice()).andStubReturn(12.34d);
		expect(tradeEntry.getQty()).andStubReturn(200L);
		Date time = new Date();
		expect(tradeEntry.getTime()).andStubReturn(time);
		expect(tradeEntry.getVolume()).andStubReturn(240.25d);
		Trade expected = new Trade(terminal);
		expected.setDirection(OrderDirection.SELL);
		expected.setId(815L);
		expected.setOrderId(896L);
		expected.setSecurityDescriptor(descr);
		expected.setPrice(12.34d);
		expected.setQty(200L);
		expected.setTime(time);
		expected.setVolume(240.25d);
		order.addTrade(eq(expected));
		expect(order.isAvailable()).andReturn(false);
		control.replay();

		assertTrue(low.adjustOrderTrade(tradeEntry, order));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(low.equals(low));
		assertFalse(low.equals(null));
		assertFalse(low.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		TerminalBuilder tb = new TerminalBuilder();
		CacheBuilder cb = new CacheBuilder();
		EditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		Cache c1 = cb.createCache(t1), c2 = cb.createCache(t2);
		low = new AssemblerLowLvl(t1, c1);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Cache> vCache = new Variant<Cache>(vTerm)
			.add(c1)
			.add(c2);
		Variant<?> iterator = vCache;
		int foundCnt = 0;
		AssemblerLowLvl x = null, found = null;
		do {
			x = new AssemblerLowLvl(vTerm.get(), vCache.get());
			if ( low.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(c1, found.getCache());
	}

}
