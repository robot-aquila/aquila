package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.*;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * A terminal builder.
 * <p>
 */
public class TerminalBuilder {
	private EventSystem eventSystem;
	private Scheduler scheduler;
	
	public TerminalBuilder() {
		
	}
	
	public EditableTerminal buildTerminal() {
		EventSystem es = getEventSystem();
		TerminalImpl terminal = new TerminalImpl(new TerminalController(),
				new TerminalEventDispatcher(es),
				new Securities(new SecuritiesEventDispatcher(es)),
				new Portfolios(new PortfoliosEventDispatcher(es)),
				new OrdersImpl(new OrdersEventDispatcher(es),
						new OrderFactoryImpl(), new SimpleCounter()),
				new StarterQueue(),
				getScheduler(),
				es);
		StarterQueue starter = terminal.getStarter(); 
		starter.add(new EventQueueStarter(es.getEventQueue(), 3000));
		return terminal;
	}
	
	/**
	 * Use the event system facade.
	 * <p>
	 * @param facade - the specified event system facade to use in terminal
	 * @return the builder instance
	 */
	public TerminalBuilder withEventSystem(EventSystem facade) {
		this.eventSystem = facade;
		return this;
	}

	/**
	 * Use a common event system facade with the specified ID.
	 * <p>
	 * @param id - the thread identifier of event queue 
	 * @return the builder instance
	 */
	public TerminalBuilder withCommonEventSystemAndQueueId(String id) {
		this.eventSystem = new EventSystemImpl(id);
		return this;
	}
	
	/**
	 * Use specified scheduler.
	 * <p>
	 * @param scheduler - scheduler to use
	 * @return the builder instance
	 */
	public TerminalBuilder withScheduler(Scheduler scheduler) {
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
	
	public TerminalBuilder withOrderProcessor(OrderProcessor processor) {
		return this;
	}
	*/
	
	private EventSystem getEventSystem() {
		return eventSystem == null ? new EventSystemImpl() : eventSystem;
	}
	
	private Scheduler getScheduler() {
		return scheduler == null ? new SchedulerLocal() : scheduler;
	}
	
}
