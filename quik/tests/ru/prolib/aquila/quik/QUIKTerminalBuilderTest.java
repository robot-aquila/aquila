package ru.prolib.aquila.quik;

import static org.junit.Assert.*;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
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
		starter.add(new EventQueueStarter(es.getEventQueue(), 30000));
		Terminal expected = new QUIKTerminalImpl(es, starter,
				new SecuritiesImpl(new SecuritiesEventDispatcher(es)),
				new PortfoliosImpl(new PortfoliosEventDispatcher(es)),
				new OrdersImpl(new OrdersEventDispatcher(es)),
				new TerminalEventDispatcher(es),
				new CacheBuilder().createCache(es), new QUIKClient());

		assertEquals(expected, builder.createTerminal("foobar"));
	}


}
