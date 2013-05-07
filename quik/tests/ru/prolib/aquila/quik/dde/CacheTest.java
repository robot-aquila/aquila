package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;

public class CacheTest {
	private IMocksControl control;
	private EventSystem es;
	private OrdersCache orders;
	private TradesCache trades;
	private SecuritiesCache securities;
	private PortfoliosFCache ports_F;
	private PositionsFCache poss_F;
	private StopOrdersCache stopOrders;
	private Cache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		orders = control.createMock(OrdersCache.class);
		trades = control.createMock(TradesCache.class);
		securities = control.createMock(SecuritiesCache.class);
		ports_F = control.createMock(PortfoliosFCache.class);
		poss_F = control.createMock(PositionsFCache.class);
		stopOrders = control.createMock(StopOrdersCache.class);
		cache = new Cache(orders, trades, securities, ports_F, poss_F,
				stopOrders);
	}
	
	@Test
	public void testCreateCache() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onOrdersUpdate = control.createMock(EventType.class);
		EventType onTradesUpdate = control.createMock(EventType.class);
		EventType onSecuritiesUpdate = control.createMock(EventType.class);
		EventType onPortsFUpdate = control.createMock(EventType.class);
		EventType onPossFUpdate = control.createMock(EventType.class);
		EventType onStopOrdersUpdate = control.createMock(EventType.class);
		expect(es.createEventDispatcher("Cache")).andReturn(dispatcher);
		expect(es.createGenericType(dispatcher, "Orders"))
			.andReturn(onOrdersUpdate);
		expect(es.createGenericType(dispatcher, "MyTrades"))
			.andReturn(onTradesUpdate);
		expect(es.createGenericType(dispatcher, "Securities"))
			.andReturn(onSecuritiesUpdate);
		expect(es.createGenericType(dispatcher, "PortfoliosFORTS"))
			.andReturn(onPortsFUpdate);
		expect(es.createGenericType(dispatcher, "PositionsFORTS"))
			.andReturn(onPossFUpdate);
		expect(es.createGenericType(dispatcher, "StopOrders"))
			.andReturn(onStopOrdersUpdate);
		control.replay();
		
		cache = Cache.createCache(es);
		
		control.verify();
		assertNotNull(cache);
		Cache expected = new Cache(new OrdersCache(dispatcher, onOrdersUpdate),
				new TradesCache(dispatcher, onTradesUpdate),
				new SecuritiesCache(dispatcher, onSecuritiesUpdate),
				new PortfoliosFCache(dispatcher, onPortsFUpdate),
				new PositionsFCache(dispatcher, onPossFUpdate),
				new StopOrdersCache(dispatcher, onStopOrdersUpdate));
		assertEquals(expected, cache);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<OrdersCache> vOrders = new Variant<OrdersCache>()
			.add(orders)
			.add(control.createMock(OrdersCache.class));
		Variant<TradesCache> vTrades = new Variant<TradesCache>(vOrders)
			.add(trades)
			.add(control.createMock(TradesCache.class));
		Variant<SecuritiesCache> vSecs = new Variant<SecuritiesCache>(vTrades)
			.add(securities)
			.add(control.createMock(SecuritiesCache.class));
		Variant<PortfoliosFCache> vPrtF = new Variant<PortfoliosFCache>(vSecs)
			.add(ports_F)
			.add(control.createMock(PortfoliosFCache.class));
		Variant<PositionsFCache> vPosF = new Variant<PositionsFCache>(vPrtF)
			.add(poss_F)
			.add(control.createMock(PositionsFCache.class));
		Variant<StopOrdersCache> vStopOrders =
				new Variant<StopOrdersCache>(vPosF)
			.add(stopOrders)
			.add(control.createMock(StopOrdersCache.class));
		Variant<?> iterator = vStopOrders;
		int foundCnt = 0;
		Cache x = null, found = null;
		do {
			x = new Cache(vOrders.get(), vTrades.get(), vSecs.get(),
					vPrtF.get(), vPosF.get(), vStopOrders.get());
			if ( cache.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(orders, found.getOrdersCache());
		assertSame(trades, found.getTradesCache());
		assertSame(securities, found.getSecuritiesCache());
		assertSame(ports_F, found.getPortfoliosFCache());
		assertSame(poss_F, found.getPositionsFCache());
		assertSame(stopOrders, found.getStopOrdersCache());
	}

}
