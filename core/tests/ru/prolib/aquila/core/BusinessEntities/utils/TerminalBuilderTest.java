package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

public class TerminalBuilderTest {
	private TerminalBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new TerminalBuilder();
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
		EditableTerminal expected = new TerminalImpl(es, starter,
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
				termDisp.createType("OnRequestSecurityError"));

		assertEquals(expected, builder.createTerminal("foobar"));
	}

}
