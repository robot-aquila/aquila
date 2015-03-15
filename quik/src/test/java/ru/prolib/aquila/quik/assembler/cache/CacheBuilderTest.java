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
		DescriptorsCache descrCache = actual.getDescriptorsCache();
		PositionsCache posCache = actual.getPositionsCache();
		OrdersCache ordersCache = actual.getOrdersCache();
		OwnTradesCache ownTradesCache = actual.getOwnTradesCache();
		TradesCache tradesCache = actual.getTradesCache();
		EventDispatcher d = descrCache.getEventDispatcher();
		Cache expected = new Cache(
				new DescriptorsCache(d, (EventTypeSI) descrCache.OnUpdate()),
				new PositionsCache(d, (EventTypeSI) posCache.OnUpdate()),
				new OrdersCache(d, (EventTypeSI) ordersCache.OnUpdate()),
				new OwnTradesCache(d, (EventTypeSI) ownTradesCache.OnUpdate()),
				new TradesCache(d, (EventTypeSI) tradesCache.OnUpdate()));
		assertEquals(expected, actual);
	}

}
