package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.*;

public class CacheBuilderTest {
	private EventSystem es;
	private CacheBuilder builder;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		builder = new CacheBuilder();
	}
	
	@Test
	public void testCreateCache() throws Exception {
		Cache actual = builder.createCache(es);
		SymbolsCache symbols = actual.getSymbolsCache();
		PositionsCache posCache = actual.getPositionsCache();
		OrdersCache ordersCache = actual.getOrdersCache();
		OwnTradesCache ownTradesCache = actual.getOwnTradesCache();
		TradesCache tradesCache = actual.getTradesCache();
		EventDispatcher d = symbols.getEventDispatcher();
		Cache expected = new Cache(
				new SymbolsCache(d, symbols.OnUpdate()),
				new PositionsCache(d, posCache.OnUpdate()),
				new OrdersCache(d, ordersCache.OnUpdate()),
				new OwnTradesCache(d, ownTradesCache.OnUpdate()),
				new TradesCache(d, tradesCache.OnUpdate()));
		assertEquals(expected, actual);
	}

}
