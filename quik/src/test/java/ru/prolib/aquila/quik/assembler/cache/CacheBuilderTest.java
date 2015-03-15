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
		EventDispatcher d = es.createEventDispatcher("Cache");
		Cache expected = new Cache(
				new DescriptorsCache(d, d.createType("Descriptors")),
				new PositionsCache(d, d.createType("Positions")),
				new OrdersCache(d, d.createType("Orders")),
				new OwnTradesCache(d, d.createType("OwnTrades")),
				new TradesCache(d, d.createType("Trades")));
		assertEquals(expected, builder.createCache(es));
	}

}
