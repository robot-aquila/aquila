package ru.prolib.aquila.quik.dde;

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
	public void testCreateTerminal() throws Exception {
		EditableTerminal t1 = termBuilder.createTerminal("bar");
		EventDispatcher d = t1.getEventSystem().createEventDispatcher("Cache");
		Cache expected = new Cache(new PartiallyKnownObjects(t1),
				new OrdersCache(d, d.createType("Orders")),
				new TradesCache(d, d.createType("MyTrades")),
				new SecuritiesCache(d, d.createType("Securities")),
				new PortfoliosFCache(d, d.createType("PortfoliosFORTS")),
				new PositionsFCache(d, d.createType("PositionsFORTS")),
				new StopOrdersCache(d, d.createType("StopOrders")));
		assertEquals(expected, builder.createCache(t1));
	}
	
	@Test
	public void testCreateTerminal_EqualityResult() throws Exception {
		// Разные экземпляра кэша при прочих равных равны, но при условии,
		// что они созданы для одного экземпляра терминала. Даже при равной
		// структуре кэша, два кэша различны, если они для разных терминалов
		EditableTerminal t1 = termBuilder.createTerminal("foo"),
			t2 = termBuilder.createTerminal("foo");
		Cache c1 = builder.createCache(t1);
		Cache c2 = builder.createCache(t1);
		Cache c3 = builder.createCache(t2);
		assertTrue(c1.equals(c2));
		assertTrue(c2.equals(c1));
		assertFalse(c1.equals(c3));
		assertFalse(c2.equals(c3));
	}

}
