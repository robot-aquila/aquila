package ru.prolib.aquila.quik.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.t2q.*;

public class CacheTest {
	private static Account account;
	private static QUIKSymbol symbol;
	private IMocksControl control;
	private SymbolsCache symbols;
	private PositionsCache positions;
	private OrdersCache orders;
	private OwnTradesCache ownTrades;
	private TradesCache trades;
	private EventType type;
	private Cache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		account = new Account("BCS", "LX01");
		symbol = new QUIKSymbol("GAZP", "EQBR", ISO4217.RUB,
				SymbolType.STK, "GAZP", "АО Газпром", "АО Газпром");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		symbols = control.createMock(SymbolsCache.class);
		positions = control.createMock(PositionsCache.class);
		orders = control.createMock(OrdersCache.class);
		ownTrades = control.createMock(OwnTradesCache.class);
		trades = control.createMock(TradesCache.class);
		type = control.createMock(EventType.class);
		cache = new Cache(symbols, positions, orders, ownTrades, trades);
	}
	
	@Test
	public void testGetSymbols0() throws Exception {
		List<QUIKSymbol> expected = new Vector<QUIKSymbol>();
		expect(symbols.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getSymbols());
		
		control.verify();
	}

	@Test
	public void testGetSymbol1() throws Exception {
		expect(symbols.get(eq("foo"))).andReturn(symbol);
		control.replay();
		
		assertSame(symbol, cache.getSymbol("foo"));
		
		control.verify();
	}
	
	@Test
	public void testGetSymbol2() throws Exception {
		expect(symbols.get(eq("foo"), eq("bar"))).andReturn(symbol);
		control.replay();
		
		assertSame(symbol, cache.getSymbol("foo", "bar"));
		
		control.verify();
	}
	
	@Test
	public void testOnSymbolsUpdate() throws Exception {
		expect(symbols.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnSymbolsUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_Symbol() throws Exception {
		expect(symbols.put(same(symbol))).andReturn(true);
		control.replay();
		
		assertTrue(cache.put(symbol));
		
		control.verify();
	}
	
	@Test
	public void testGetPositions0() throws Exception {
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expect(positions.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getPositions());
		
		control.verify();
	}
	
	@Test
	public void testGetPosition() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		expect(positions.get(same(account), eq("sber"))).andReturn(entry);
		control.replay();
		
		assertSame(entry, cache.getPosition(account, "sber"));
		
		control.verify();
	}
	
	@Test
	public void testGetPositions1() throws Exception {
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expect(positions.get(eq("gazp"))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getPositions("gazp"));
		
		control.verify();
	}
	
	@Test
	public void testOnPositionsUpdate() throws Exception {
		expect(positions.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnPositionsUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_PositionEntry() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		positions.put(same(entry));
		control.replay();
		
		cache.put(entry);
		
		control.verify();
	}
	
	@Test
	public void testPurge_PositionEntry() throws Exception {
		PositionEntry entry = control.createMock(PositionEntry.class);
		positions.purge(same(entry));
		control.replay();
		
		cache.purge(entry);
		
		control.verify();
	}
	
	@Test
	public void testGetOrders() throws Exception {
		List<T2QOrder> expected = new Vector<T2QOrder>();
		expect(orders.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrders());
		
		control.verify();
	}
	
	@Test
	public void testGetOrder_ByLocalId() throws Exception {
		T2QOrder expected = control.createMock(T2QOrder.class);
		expect(orders.get(eq(12))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrder(12));
		
		control.verify();
	}
	
	@Test
	public void testGetOrder_BySystemId() throws Exception {
		T2QOrder expected = control.createMock(T2QOrder.class);
		expect(orders.get(eq(524L))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOrder(524L));
		
		control.verify();
	}
	
	@Test
	public void testOnOrdersUpdate() throws Exception {
		expect(orders.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnOrdersUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_OrderEntry() throws Exception {
		T2QOrder entry = control.createMock(T2QOrder.class);
		orders.put(same(entry));
		control.replay();
		
		cache.put(entry);
		
		control.verify();
	}
	
	@Test
	public void testPurge_ByLocalOrderId() throws Exception {
		orders.purge(eq(815));
		control.replay();
		
		cache.purge(815);
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<SymbolsCache> vDsc = new Variant<SymbolsCache>()
			.add(symbols)
			.add(control.createMock(SymbolsCache.class));
		Variant<PositionsCache> vPos = new Variant<PositionsCache>(vDsc)
			.add(positions)
			.add(control.createMock(PositionsCache.class));
		Variant<OrdersCache> vOrd = new Variant<OrdersCache>(vPos)
			.add(orders)
			.add(control.createMock(OrdersCache.class));
		Variant<OwnTradesCache> vOwn = new Variant<OwnTradesCache>(vOrd)
			.add(ownTrades)
			.add(control.createMock(OwnTradesCache.class));
		Variant<TradesCache> vTrd = new Variant<TradesCache>(vOwn)
			.add(trades)
			.add(control.createMock(TradesCache.class));
		Variant<?> iterator = vTrd;
		int foundCnt = 0;
		Cache x, found = null;
		do {
			x = new Cache(vDsc.get(), vPos.get(), vOrd.get(), vOwn.get(),
					vTrd.get());
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(symbols, found.getSymbolsCache());
		assertSame(positions, found.getPositionsCache());
		assertSame(orders, found.getOrdersCache());
		assertSame(ownTrades, found.getOwnTradesCache());
		assertSame(trades, found.getTradesCache());
	}
	
	@Test
	public void testGetOwnTrades() throws Exception {
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expect(ownTrades.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOwnTrades());
		
		control.verify();
	}
	
	@Test
	public void testGetOwnTradesByOrder() throws Exception {
		List<T2QTrade> expected = new Vector<T2QTrade>();
		expect(ownTrades.getByOrder(eq(824L))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOwnTradesByOrder(824L));
		
		control.verify();
	}
	
	@Test
	public void testGetOwnTrade() throws Exception {
		T2QTrade expected = control.createMock(T2QTrade.class);
		expect(ownTrades.get(eq(7261L))).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getOwnTrade(7261L));
		
		control.verify();
	}
	
	@Test
	public void testOnOwnTradesUpdate() throws Exception {
		expect(ownTrades.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnOwnTradesUpdate());
		
		control.verify();
	}
	
	@Test
	public void testPut_OwnTrade() throws Exception {
		T2QTrade entry = control.createMock(T2QTrade.class);
		ownTrades.put(same(entry));
		control.replay();
		
		cache.put(entry);
		
		control.verify();
	}
	
	@Test
	public void testPurgeOwnTrades() throws Exception {
		ownTrades.purge(eq(4412L));
		control.replay();
		
		cache.purgeOwnTrades(4412L);
		
		control.verify();
	}
	
	@Test
	public void testGetTradesCache() throws Exception {
		assertSame(trades, cache.getTradesCache());
	}
	
	@Test
	public void testGetFirstTrades() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		expect(trades.getFirst()).andReturn(entry);
		control.replay();
		
		assertSame(entry, cache.getFirstTrades());
		
		control.verify();
	}
	
	@Test
	public void testPurgeFirstTrades() throws Exception {
		trades.purgeFirst();
		control.replay();
		
		cache.purgeFirstTrades();
		
		control.verify();
	}
	
	@Test
	public void testGetTrades() throws Exception {
		List<TradesEntry> expected = new Vector<TradesEntry>();
		expect(trades.get()).andReturn(expected);
		control.replay();
		
		assertSame(expected, cache.getTrades());
		
		control.verify();
	}
	
	@Test
	public void testOnTradesUpdate() throws Exception {
		expect(trades.OnUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnTradesUpdate());
		
		control.verify();
	}
	
	@Test
	public void testAdd() throws Exception {
		TradesEntry entry = control.createMock(TradesEntry.class);
		trades.add(same(entry));
		control.replay();
		
		cache.add(entry);
		
		control.verify();
	}

}
