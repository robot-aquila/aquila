package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;

public class CacheTest {
	private static Account acc;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private EventSystem es;
	private PartiallyKnownObjects partiallyKnown;
	private OrdersCache orders;
	private TradesCache trades;
	private SecuritiesCache securities;
	private PortfoliosFCache ports_F;
	private PositionsFCache poss_F;
	private StopOrdersCache stopOrders;
	private Cache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		acc = new Account("BCS", "LX01");
		descr = new SecurityDescriptor("GAZP", "EQBR", "SUR", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		partiallyKnown = control.createMock(PartiallyKnownObjects.class);
		orders = control.createMock(OrdersCache.class);
		trades = control.createMock(TradesCache.class);
		securities = control.createMock(SecuritiesCache.class);
		ports_F = control.createMock(PortfoliosFCache.class);
		poss_F = control.createMock(PositionsFCache.class);
		stopOrders = control.createMock(StopOrdersCache.class);
		cache = new Cache(partiallyKnown, orders, trades, securities, ports_F,
				poss_F, stopOrders);
	}
	
	@Test
	public void testCreateCache() throws Exception {
		FirePanicEvent firePanic = control.createMock(FirePanicEvent.class);
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
		
		cache = Cache.createCache(es, firePanic);
		
		control.verify();
		assertNotNull(cache);
		Cache expected = new Cache(new PartiallyKnownObjects(firePanic),
				new OrdersCache(dispatcher, onOrdersUpdate),
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
		Variant<PartiallyKnownObjects> vPrtKnwn =
				new Variant<PartiallyKnownObjects>()
			.add(partiallyKnown)
			.add(control.createMock(PartiallyKnownObjects.class));
		Variant<OrdersCache> vOrders = new Variant<OrdersCache>(vPrtKnwn)
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
			x = new Cache(vPrtKnwn.get(), vOrders.get(), vTrades.get(),
					vSecs.get(), vPrtF.get(), vPosF.get(), vStopOrders.get());
			if ( cache.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(partiallyKnown, found.getPartiallyKnownObjects());
		assertSame(orders, found.getOrdersCache());
		assertSame(trades, found.getTradesCache());
		assertSame(securities, found.getSecuritiesCache());
		assertSame(ports_F, found.getPortfoliosFCache());
		assertSame(poss_F, found.getPositionsFCache());
		assertSame(stopOrders, found.getStopOrdersCache());
	}
	
	@Test
	public void testRegisterAccount() throws Exception {
		partiallyKnown.registerAccount(same(acc));
		control.replay();
		
		cache.registerAccount(acc);
		
		control.verify();
	}
	
	@Test
	public void testRegisterSecurityDescriptor() throws Exception {
		partiallyKnown.registerSecurityDescriptor(same(descr), eq("Газпром"));
		control.replay();
		
		cache.registerSecurityDescriptor(descr, "Газпром");
		
		control.verify();
	}
	
	@Test
	public void testGetAccount1() throws Exception {
		expect(partiallyKnown.getAccount(eq("LX01"))).andReturn(acc);
		control.replay();
		
		assertSame(acc, cache.getAccount("LX01"));

		control.verify();
	}
	
	@Test
	public void testGetAccount2() throws Exception {
		expect(partiallyKnown.getAccount(eq("BCS"), eq("LX01"))).andReturn(acc);
		control.replay();
		
		assertSame(acc, cache.getAccount("BCS", "LX01"));

		control.verify();
	}

	@Test
	public void testGetSecurityDescriptorByName() throws Exception {
		expect(partiallyKnown.getSecurityDescriptorByName(eq("Газпром")))
			.andReturn(descr);
		control.replay();
		
		assertSame(descr, cache.getSecurityDescriptorByName("Газпром"));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDescriptorByCodeAndClass() throws Exception {
		expect(partiallyKnown.getSecurityDescriptorByCodeAndClass(eq("GAZP"),
				eq("EQBR"))).andReturn(descr);
		control.replay();
		
		assertSame(descr,
				cache.getSecurityDescriptorByCodeAndClass("GAZP", "EQBR"));
		
		control.verify();
	}
	
	@Test
	public void testIsAccountRegistered1() throws Exception {
		expect(partiallyKnown.isAccountRegistered(eq("LX01"))).andReturn(true);
		expect(partiallyKnown.isAccountRegistered(eq("LX01"))).andReturn(false);
		control.replay();
		
		assertTrue(cache.isAccountRegistered("LX01"));
		assertFalse(cache.isAccountRegistered("LX01"));
		
		control.verify();
	}

	@Test
	public void testIsAccountRegistered2() throws Exception {
		expect(partiallyKnown.isAccountRegistered(eq("3466"), eq("LX01")))
			.andReturn(true);
		expect(partiallyKnown.isAccountRegistered(eq("3466"), eq("LX01")))
			.andReturn(false);
		control.replay();
		
		assertTrue(cache.isAccountRegistered("3466", "LX01"));
		assertFalse(cache.isAccountRegistered("3466", "LX01"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered1() throws Exception {
		expect(partiallyKnown.isSecurityDescriptorRegistered(eq("Газпром")))
			.andReturn(true);
		expect(partiallyKnown.isSecurityDescriptorRegistered(eq("Газпром")))
			.andReturn(false);
		control.replay();
		
		assertTrue(cache.isSecurityDescriptorRegistered("Газпром"));
		assertFalse(cache.isSecurityDescriptorRegistered("Газпром"));
		
		control.verify();
	}
	
	@Test
	public void testIsSecurityDescriptorRegistered2() throws Exception {
		expect(partiallyKnown.isSecurityDescriptorRegistered(eq("GAZP"),
				eq("EQBR"))).andReturn(true);
		expect(partiallyKnown.isSecurityDescriptorRegistered(eq("GAZP"),
				eq("EQBR"))).andReturn(false);
		control.replay();
		
		assertTrue(cache.isSecurityDescriptorRegistered("GAZP", "EQBR"));
		assertFalse(cache.isSecurityDescriptorRegistered("GAZP", "EQBR"));
		
		control.verify();
	}
	
	@Test
	public void testGetAllSecurities() throws Exception {
		List<SecurityCache> list = new Vector<SecurityCache>();
		expect(securities.getAll()).andReturn(list);
		control.replay();
		
		assertSame(list, cache.getAllSecurities());
		
		control.verify();
	}
	
	@Test
	public void testOnSecuritiesCacheUpdate() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(securities.OnCacheUpdate()).andReturn(type);
		control.replay();
		
		assertSame(type, cache.OnSecuritiesCacheUpdate());
		
		control.verify();
	}

}
