package ru.prolib.aquila.quik;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.CacheBuilder;

public class QUIKTerminalBuilderTest {
	private QUIKTerminalBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new QUIKTerminalBuilder();
	}
	
	@Test
	public void testCreateTerminal() throws Exception {
		// Этот тест всегда будет проваливаться под линуксом, так как для
		// работы инстанцирования клиента нужна бинарная виндовая либа.
		if ( ! System.getProperty("os.name").toLowerCase().startsWith("win") ) {
			System.err.println(getClass().getName() +
				"#testCreateTerminal skipped, for win only");
			return;
		}
		EventSystem es = new EventSystemImpl(new EventQueueImpl("foobar"));
		StarterQueue starter = new StarterQueue();
		starter.add(es.getEventQueue());
		EventDispatcher secDisp = es.createEventDispatcher("Securities");
		EventDispatcher portDisp = es.createEventDispatcher("Portfolios");
		EventDispatcher ordDisp = es.createEventDispatcher("Orders");
		EventDispatcher termDisp = es.createEventDispatcher("Terminal");
		Terminal expected = new QUIKTerminalImpl(es, starter,
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
				new CacheBuilder().createCache(es), new QUIKClient());

		assertEquals(expected, builder.createTerminal("foobar"));
	}


}
