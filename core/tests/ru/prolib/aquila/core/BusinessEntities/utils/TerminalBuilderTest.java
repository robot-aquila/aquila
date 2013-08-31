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
		starter.add(new EventQueueStarter(es.getEventQueue(), 30000));
		EditableTerminal expected = new TerminalImpl(es, starter,
				new SecuritiesImpl(new SecuritiesEventDispatcher(es)),
				new PortfoliosImpl(new PortfoliosEventDispatcher(es)),
				new OrdersImpl(new OrdersEventDispatcher(es)),
				new TerminalEventDispatcher(es));

		assertEquals(expected, builder.createTerminal("foobar"));
	}

}
