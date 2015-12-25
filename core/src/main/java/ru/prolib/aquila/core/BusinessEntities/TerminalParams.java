package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.OrderFactoryImpl;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Orders;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.OrdersImpl;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Portfolios;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;
import ru.prolib.aquila.core.BusinessEntities.utils.OrdersEventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfoliosEventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.utils.SecuritiesEventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalEventDispatcher;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * Terminal parameters.
 * <p>
 * This class is used to pass to terminal's constructor to reduce coupling with
 * its implementation. It may be useful for derived classes or for testing
 * purposes.
 */
public class TerminalParams {
	private static int lastAssignedNum = 0;
	
	@Deprecated
	private TerminalController controller;
	private TerminalEventDispatcher dispatcher;
	private Securities securities;
	private Portfolios portfolios;
	private Orders orders;
	@Deprecated
	private StarterQueue starter;
	private Scheduler scheduler;
	@Deprecated
	private EventSystem eventSystem;
	@Deprecated
	private OrderProcessor orderProcessor;
	private String terminalID;
	private DataProvider dataProvider;
	
	public static synchronized int getCurrentGeneratedNumber() {
		return lastAssignedNum;
	}
	
	public static synchronized String generateNextGeneratedID() {
		return "Terminal#" + (++lastAssignedNum);
	}
	
	public TerminalParams(EventSystem es) {
		super();
		this.controller = new TerminalController();
		this.dispatcher = new TerminalEventDispatcher(es);
		this.securities = new Securities(new SecuritiesEventDispatcher(es));
		this.portfolios = new Portfolios(new PortfoliosEventDispatcher(es));
		this.orders = new OrdersImpl(new OrdersEventDispatcher(es),
				new OrderFactoryImpl(), new SimpleCounter());
		this.starter = new StarterQueue();
		this.scheduler = new SchedulerLocal();
		this.eventSystem = es;
	}
	
	public TerminalParams() {
		this(new EventSystemImpl());
	}
	
	@Deprecated
	public TerminalController getController() {
		return controller;
	}
	
	@Deprecated
	public TerminalEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Deprecated
	public Securities getSecurityRepository() {
		return securities;
	}
	
	@Deprecated
	public Portfolios getPortfolioRepository() {
		return portfolios;
	}
	
	@Deprecated
	public Orders getOrderRepository() {
		return orders;
	}
	
	@Deprecated
	public StarterQueue getStarter() {
		return starter;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	@Deprecated
	public EventSystem getEventSystem() {
		return eventSystem;
	}
	
	@Deprecated
	public OrderProcessor getOrderProcessor() {
		return orderProcessor;
	}
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public String getTerminalID() {
		return terminalID == null ? generateNextGeneratedID() : terminalID;
	}
	
	@Deprecated
	public void setTerminalController(TerminalController controller) {
		this.controller = controller;
	}
	
	@Deprecated
	public void setEventDispatcher(TerminalEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Deprecated
	public void setSecurityRepository(Securities repository) {
		this.securities = repository;
	}
	
	@Deprecated
	public void setPortfolioRepository(Portfolios repository) {
		this.portfolios = repository;
	}
	
	@Deprecated
	public void setOrderRepository(Orders repository) {
		this.orders = repository;
	}
	
	@Deprecated
	public void setStarter(StarterQueue starter) {
		this.starter = starter;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	@Deprecated
	public void setEventSystem(EventSystem eventSystem) {
		this.eventSystem = eventSystem;
	}
	
	@Deprecated
	public void setOrderProcessor(OrderProcessor processor) {
		this.orderProcessor = processor;
	}
	
	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	public void setTerminalID(String id) {
		this.terminalID = id;
	}

}
