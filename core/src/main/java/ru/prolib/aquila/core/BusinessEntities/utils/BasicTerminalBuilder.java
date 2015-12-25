package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * The terminal builder.
 */
public class BasicTerminalBuilder {
	private EventSystem eventSystem;
	private Scheduler scheduler;
	private OrderProcessor orderProcessor;
	
	public BasicTerminalBuilder() {
		
	}
	
	public EditableTerminal buildTerminal() {
		return new TerminalImpl(buildParams());
	}
	
	public TerminalParams buildParams() {
		EventSystem es = getEventSystem();
		StarterQueue starter = new StarterQueue();
		starter.add(new EventQueueStarter(es.getEventQueue(), 3000));
		TerminalParams params = new TerminalParams();
		params.setStarter(starter);
		params.setScheduler(getScheduler());
		params.setEventSystem(es);
		params.setOrderProcessor(getOrderProcessor());
		return params;
	}
	
	/**
	 * Use the event system facade.
	 * <p>
	 * @param facade - the specified event system facade to use in terminal
	 * @return the builder instance
	 */
	public BasicTerminalBuilder withEventSystem(EventSystem facade) {
		this.eventSystem = facade;
		return this;
	}

	/**
	 * Use a common event system facade with the specified ID.
	 * <p>
	 * @param id - the thread identifier of event queue 
	 * @return the builder instance
	 */
	public BasicTerminalBuilder withCommonEventSystemAndQueueId(String id) {
		this.eventSystem = new EventSystemImpl(id);
		return this;
	}
	
	/**
	 * Use specified scheduler.
	 * <p>
	 * @param scheduler - scheduler to use
	 * @return the builder instance
	 */
	public BasicTerminalBuilder withScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return this;
	}

	/*
	public TerminalBuilder withSecurities(Securities repository) {
		return this;
	}
	
	public TerminalBuilder
		withCommonSecuritiesAndFactory(SecurityFactory factory)
	{
		return this;
	}
	
	public TerminalBuilder withPortfolios(Portfolios repository) {
		return this;
	}
	
	public TerminalBuilder
		withCommonPortfoliosAndFactory(PortfolioFactory factory)
	{
		return this;
	}
	
	public TerminalBuilder withOrders(Orders repository) {
		return this;
	}
	
	public TerminalBuilder withStarter(StarterQueue starter) {
		return this;
	}
	*/
	
	public BasicTerminalBuilder withOrderProcessor(OrderProcessor processor) {
		this.orderProcessor = processor;
		return this;
	}
	
	
	private EventSystem getEventSystem() {
		return eventSystem == null ? new EventSystemImpl() : eventSystem;
	}
	
	private Scheduler getScheduler() {
		return scheduler == null ? new SchedulerLocal() : scheduler;
	}
	
	private OrderProcessor getOrderProcessor() {
		return orderProcessor;
	}
	
}
