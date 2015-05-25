package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.OrderProcessor;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalEventDispatcher;

public class BasicTerminalParams {
	private final TerminalController controller;
	private final TerminalEventDispatcher dispatcher;
	private final Securities securities;
	private final Portfolios portfolios;
	private final Orders orders;
	private final StarterQueue starter;
	private final Scheduler scheduler;
	private final EventSystem eventSystem;
	private final OrderProcessor orderProcessor;
	
	public BasicTerminalParams(TerminalController controller,
			TerminalEventDispatcher dispatcher,
			Securities securities,
			Portfolios portfolios,
			Orders orders,
			StarterQueue starter,
			Scheduler scheduler,
			EventSystem eventSystem,
			OrderProcessor orderProcessor)
	{
		super();
		this.controller = controller;
		this.dispatcher = dispatcher;
		this.securities = securities;
		this.portfolios = portfolios;
		this.orders = orders;
		this.starter = starter;
		this.scheduler = scheduler;
		this.eventSystem = eventSystem;
		this.orderProcessor = orderProcessor;
	}
	
	
	public TerminalController getController() {
		return controller;
	}
	
	public TerminalEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	public Securities getSecurityStorage() {
		return securities;
	}
	
	public Portfolios getPortfolioStorage() {
		return portfolios;
	}
	
	public Orders getOrderStorage() {
		return orders;
	}
	
	public StarterQueue getStarter() {
		return starter;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public EventSystem getEventSystem() {
		return eventSystem;
	}
	
	public OrderProcessor getOrderProcessor() {
		return orderProcessor;
	}

}
