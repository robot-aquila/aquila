package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;

public class BasicTerminalBuilderTest {
	private IMocksControl control;
	private BasicTerminalBuilder builder;
	private EventSystem eventSystem1, eventSystem2;
	private Scheduler scheduler1, scheduler2;
	private OrderProcessor orderProcessor;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		builder = new BasicTerminalBuilder();
		eventSystem1 = new EventSystemImpl();
		eventSystem2 = new EventSystemImpl();
		scheduler1 = control.createMock(Scheduler.class);
		scheduler2 = control.createMock(Scheduler.class);
		orderProcessor = control.createMock(OrderProcessor.class);
	}
	
	@Test
	public void testDefaults() throws Exception {
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
		assertNull(terminal.getOrderProcessor());
		
		TerminalParams params = builder.buildParams();
		assertNotNull(params);
		assertNotNull(params.getController());
		assertNotSame(terminal.getTerminalController(), params.getController());
		assertNotNull(params.getEventDispatcher());
		assertNotSame(terminal.getTerminalEventDispatcher(), params.getEventDispatcher());
		assertNotNull(params.getSecurityRepository());
		assertNotSame(terminal.getSecurityStorage(), params.getSecurityRepository());
		assertNotNull(params.getPortfolioRepository());
		assertNotSame(terminal.getPortfolioStorage(), params.getPortfolioRepository());
		assertNotNull(params.getOrderRepository());
		assertNotSame(terminal.getOrderStorage(), params.getOrderRepository());
		assertNotNull(params.getScheduler());
		assertNotSame(terminal.getScheduler(), params.getScheduler());
		assertNotNull(params.getEventSystem());
		assertNotSame(terminal.getEventSystem(), params.getEventSystem());
		assertNotNull(params.getStarter());
		assertNotSame(terminal.getStarter(), params.getStarter());
		assertNull(params.getOrderProcessor());
	}
	
	@Test
	public void testWithEventSystem() throws Exception {
		builder.withEventSystem(eventSystem1)
			.withEventSystem(eventSystem2); // the last one will be used
		
		TerminalImpl terminal = (TerminalImpl) builder.buildTerminal();
		assertNotNull(terminal);
		assertSame(eventSystem2, terminal.getEventSystem());
		
		TerminalParams params = builder.buildParams();
		assertNotNull(params);
		assertSame(eventSystem2, params.getEventSystem());
	}
	
	@Test
	public void testWithCommonEventSystemAndQueueId() throws Exception {
		builder.withEventSystem(eventSystem1)
			.withCommonEventSystemAndQueueId("Jool");
		
		TerminalImpl terminal = (TerminalImpl) builder.buildTerminal();
		assertNotNull(terminal);
		EventSystemImpl x = (EventSystemImpl) terminal.getEventSystem();
		assertEquals("Jool", x.getEventQueue().getId());
		
		TerminalParams params = builder.buildParams();
		assertNotNull(params);
		assertSame(x, params.getEventSystem());
	}

	@Test
	public void testWithScheduler() throws Exception {
		builder.withScheduler(scheduler1)
			.withScheduler(scheduler2); // the last one will be used
		
		TerminalImpl terminal = (TerminalImpl) builder.buildTerminal();
		assertNotNull(terminal);
		assertSame(scheduler2, terminal.getScheduler());
		
		TerminalParams params = builder.buildParams();
		assertNotNull(params);
		assertSame(scheduler2, params.getScheduler());
	}
	
	@Test
	public void testWithOrderProcessor() throws Exception {
		builder.withOrderProcessor(orderProcessor);
		
		TerminalImpl terminal = (TerminalImpl) builder.buildTerminal();
		assertNotNull(terminal);
		assertSame(orderProcessor, terminal.getOrderProcessor());
		
		TerminalParams params = builder.buildParams();
		assertNotNull(params);
		assertSame(orderProcessor, params.getOrderProcessor());
	}

}
