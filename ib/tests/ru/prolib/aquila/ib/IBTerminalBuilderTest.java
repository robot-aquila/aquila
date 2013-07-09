package ru.prolib.aquila.ib;


import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
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
		starter.add(es.getEventQueue());
		EventDispatcher secDisp = es.createEventDispatcher("Securities");
		EventDispatcher portDisp = es.createEventDispatcher("Portfolios");
		EventDispatcher ordDisp = es.createEventDispatcher("Orders");
		EventDispatcher termDisp = es.createEventDispatcher("Terminal");
		EventDispatcher cacheDisp = es.createEventDispatcher("Cache");
		Cache cache = new Cache(cacheDisp, cacheDisp.createType("Contract"),
				cacheDisp.createType("Order"),
				cacheDisp.createType("OrderStatus"),
				cacheDisp.createType("Position"),
				cacheDisp.createType("Exec"));
		IBClient client = new IBClient();
		IBEditableTerminal expected = new IBTerminalImpl(es, starter,
				new SecuritiesImpl(secDisp,
						secDisp.createType("OnAvailable"),
						secDisp.createType("OnChanged"),
						secDisp.createType("OnTrade")),
				new PortfoliosImpl(portDisp,
						portDisp.createType("OnAvailable"),
						portDisp.createType("OnChanged"),
						portDisp.createType("OnPositionAvailable"),
						portDisp.createType("OnPositionChanged")),
				new OrdersImpl(ordDisp,
						ordDisp.createType("OnAvailable"),
						ordDisp.createType("OnCancelFailed"),
						ordDisp.createType("OnCancelled"),
						ordDisp.createType("OnChanged"),
						ordDisp.createType("OnDone"),
						ordDisp.createType("OnFailed"),
						ordDisp.createType("OnFilled"),
						ordDisp.createType("OnPartiallyFilled"),
						ordDisp.createType("OnRegistered"),
						ordDisp.createType("OnRegisterFailed"),
						ordDisp.createType("OnTrade")),
				termDisp,
				termDisp.createType("OnConnected"),
				termDisp.createType("OnDisconnected"),
				termDisp.createType("OnStarted"),
				termDisp.createType("OnStopped"),
				termDisp.createType("OnPanic"),
				termDisp.createType("OnRequestSecurityError"),
				cache, client);

		assertEquals(expected, builder.createTerminal("foobar"));
	}

}
