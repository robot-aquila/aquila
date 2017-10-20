package ru.prolib.aquila.ib;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.Cache;

public class IBTerminalBuilderTest {
	private IBTerminalBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new IBTerminalBuilder();
	}
	
	@Test
	public void testCreateTerminal() throws Exception {
		EventSystem es = new EventSystemImpl(new EventQueueImpl("foobar"));
		StarterQueue starter = new StarterQueue();
		starter.add(new EventQueueStarter(es.getEventQueue(), 30000));
		EventDispatcher cacheDisp = es.createEventDispatcher("Cache");
		Cache cache = new Cache(cacheDisp, cacheDisp.createType("Contract"),
				cacheDisp.createType("Order"),
				cacheDisp.createType("OrderStatus"),
				cacheDisp.createType("Position"),
				cacheDisp.createType("Exec"));
		IBClient client = new IBClient();
		IBEditableTerminal expected = new IBTerminalImpl(es, starter,
				new SecuritiesImpl(new SecuritiesEventDispatcher(es)),
				new PortfoliosImpl(new PortfoliosEventDispatcher(es)),
				new OrdersImpl(new OrdersEventDispatcher(es)),
				new TerminalEventDispatcher(es),
				cache, client);

		assertEquals(expected, builder.createTerminal("foobar"));
	}

}
