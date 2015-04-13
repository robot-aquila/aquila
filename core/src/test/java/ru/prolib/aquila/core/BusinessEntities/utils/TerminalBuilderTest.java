package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;

public class TerminalBuilderTest {
	private IMocksControl control;
	private TerminalBuilder builder;
	private EventSystem eventSystem1, eventSystem2;
	private Scheduler scheduler1, scheduler2;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		builder = new TerminalBuilder();
		eventSystem1 = new EventSystemImpl();
		eventSystem2 = new EventSystemImpl();
		scheduler1 = control.createMock(Scheduler.class);
		scheduler2 = control.createMock(Scheduler.class);
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
	public void testBuildTerminal_WithEventSystem() throws Exception {
		TerminalImpl terminal =
			(TerminalImpl) builder.withEventSystem(eventSystem1)
			.withEventSystem(eventSystem2) // the last one will be used
			.buildTerminal();
		assertNotNull(terminal);
		assertSame(eventSystem2, terminal.getEventSystem());
	}
	
	@Test
	public void testBuildTerminal_WithCommonEventSystemAndQueueId()
			throws Exception
	{
		TerminalImpl terminal =
			(TerminalImpl) builder.withEventSystem(eventSystem1)
			.withCommonEventSystemAndQueueId("Jool")
			.buildTerminal();
		assertNotNull(terminal);
		EventSystemImpl x = (EventSystemImpl) terminal.getEventSystem();
		assertEquals("Jool", x.getEventQueue().getId());
	}

	@Test
	public void testBuildTerminal_WithScheduler() throws Exception {
		TerminalImpl terminal =
			(TerminalImpl) builder.withScheduler(scheduler1)
			.withScheduler(scheduler2) // the last one will be used
			.buildTerminal();
		assertNotNull(terminal);
		assertSame(scheduler2, terminal.getScheduler());
	}

}
