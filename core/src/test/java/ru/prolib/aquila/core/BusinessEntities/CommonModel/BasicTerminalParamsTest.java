package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.OrderProcessor;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalEventDispatcher;

public class BasicTerminalParamsTest {
	private TerminalController controller;
	private TerminalEventDispatcher dispatcher;
	private Securities securities;
	private Portfolios portfolios;
	private Orders orders;
	private StarterQueue starter;
	private Scheduler scheduler;
	private EventSystem es;
	private OrderProcessor orderProcessor;
	private IMocksControl control;
	private BasicTerminalParams params;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = new EventSystemImpl();
		controller = control.createMock(TerminalController.class);
		dispatcher = control.createMock(TerminalEventDispatcher.class);
		securities = control.createMock(Securities.class);
		portfolios = control.createMock(Portfolios.class);
		orders = control.createMock(Orders.class);
		starter = control.createMock(StarterQueue.class);
		scheduler = control.createMock(Scheduler.class);
		orderProcessor = control.createMock(OrderProcessor.class);
		params = new BasicTerminalParams(controller, dispatcher, securities,
				portfolios, orders, starter, scheduler, es, orderProcessor);
	}

	@Test
	public void testAccessors() {
		assertSame(controller, params.getController());
		assertSame(dispatcher, params.getEventDispatcher());
		assertSame(securities, params.getSecurityStorage());
		assertSame(portfolios, params.getPortfolioStorage());
		assertSame(orders, params.getOrderStorage());
		assertSame(starter, params.getStarter());
		assertSame(scheduler, params.getScheduler());
		assertSame(es, params.getEventSystem());
		assertSame(orderProcessor, params.getOrderProcessor());
	}

}
