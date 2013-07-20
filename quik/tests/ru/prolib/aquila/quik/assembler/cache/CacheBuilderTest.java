package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

public class CacheBuilderTest {
	private TerminalBuilder termBuilder;
	private CacheBuilder builder;

	@Before
	public void setUp() throws Exception {
		termBuilder = new TerminalBuilder();
		builder = new CacheBuilder();
	}
	
	@Test
	public void testCreateCache() throws Exception {
		EditableTerminal t1 = termBuilder.createTerminal("bar");
		EventDispatcher d = t1.getEventSystem().createEventDispatcher("Cache");
		Cache expected = new Cache(
				new DescriptorsCache(d, d.createType("Descriptors")),
				new PositionsCache(d, d.createType("Positions")),
				new OrdersCache(d, d.createType("Orders")),
				new OwnTradesCache(d, d.createType("OwnTrades")),
				new TradesCache(d, d.createType("Trades")));
		assertEquals(expected, builder.createCache(t1.getEventSystem()));
	}

}
