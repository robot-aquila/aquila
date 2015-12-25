package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.OrderProcessor;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TerminalParams;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Portfolios;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalEventDispatcher;
import ru.prolib.aquila.core.data.DataProvider;

public class TerminalParamsTest {
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
	private DataProvider dataProvider;
	private TerminalParams params;

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
		dataProvider = control.createMock(DataProvider.class);
		params = new TerminalParams();
	}

	@Test
	public void testGettersAndSetters() {
		params.setTerminalController(controller);
		params.setEventDispatcher(dispatcher);
		params.setSecurityRepository(securities);
		params.setPortfolioRepository(portfolios);
		params.setOrderRepository(orders);
		params.setStarter(starter);
		params.setScheduler(scheduler);
		params.setEventSystem(es);
		params.setOrderProcessor(orderProcessor);
		params.setDataProvider(dataProvider);
		params.setTerminalID("DummyTerminal");
		
		assertSame(controller, params.getController());
		assertSame(dispatcher, params.getEventDispatcher());
		assertSame(securities, params.getSecurityRepository());
		assertSame(portfolios, params.getPortfolioRepository());
		assertSame(orders, params.getOrderRepository());
		assertSame(starter, params.getStarter());
		assertSame(scheduler, params.getScheduler());
		assertSame(es, params.getEventSystem());
		assertSame(orderProcessor, params.getOrderProcessor());
		assertSame(dataProvider, params.getDataProvider());
		assertEquals("DummyTerminal", params.getTerminalID());
	}
	
	@Test
	public void testGetTerminalID_Auto() throws Exception {
		int baseNumber = TerminalParams.getCurrentGeneratedNumber();
		String expected1 = "Terminal#" + (baseNumber + 1),
				expected2 = "Terminal#" + (baseNumber + 2);
		
		assertEquals(expected1, params.getTerminalID());
		assertEquals(expected2, params.getTerminalID());
	}
	
	@Ignore
	@Test
	public void testGetters_StrictMode() throws Exception {
		// TODO: 
	}

}
