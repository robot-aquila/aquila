package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.EventQueueStarter;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.OrdersImpl;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Portfolios;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;

public class TerminalBuilderTest {
	private IMocksControl control;
	private TerminalBuilder builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		builder = new TerminalBuilder();
	}
	
	@Test
	public void testBuildTerminal_Default() throws Exception {
		TerminalImpl terminal = (TerminalImpl)builder.buildTerminal();
		assertNotNull(terminal);
		assertNotNull(terminal.getTerminalController());
		assertNotNull(terminal.getTerminalEventDispatcher());
		assertNotNull((Securities)terminal.getSecurityStorage());
		assertNotNull((Portfolios)terminal.getPortfolioStorage());
		assertNotNull((OrdersImpl)terminal.getOrderStorage());
		assertNotNull((SchedulerLocal) terminal.getScheduler());
		EventSystemImpl es = (EventSystemImpl) terminal.getEventSystem();
		assertNotNull(es);
		StarterQueue starter = terminal.getStarter();
		assertEquals(1, starter.count());
		assertSame(es.getEventQueue(),
				((EventQueueStarter)starter.get(0)).getEventQueue());
	}

	@Test
	@Ignore
	public void test() {
		fail("Not yet implemented");
	}

}
