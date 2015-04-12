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
	
	public TerminalBuilder() {
		
	}
	
	public EditableTerminal buildTerminal() {
		EventSystem es = new EventSystemImpl();
		TerminalImpl terminal = new TerminalImpl(new TerminalController(),
				new TerminalEventDispatcher(es),
				new Securities(new SecuritiesEventDispatcher(es)),
				new Portfolios(new PortfoliosEventDispatcher(es)),
				new OrdersImpl(new OrdersEventDispatcher(es),
						new OrderFactoryImpl(), new SimpleCounter()),
				new StarterQueue(),
				new SchedulerLocal(),
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
		return this;
	}

	/**
	 * Use a common event system facade with specified ID.
	 * <p>
	 * @param id - the thread identifier of event queue 
	 * @return the builder instance
	 */
	public TerminalBuilder withCommonEventSystemAndQueueId(String id) {
		return this;
	}
	
	public TerminalBuilder withSecurities(Securities repository) {
		return this;
	}
	
	public TerminalBuilder withPortfolios(Portfolios repository) {
		return this;
	}
	
	public TerminalBuilder withOrders(Orders repository) {
		return this;
	}
	
	public TerminalBuilder withStarter(StarterQueue starter) {
		return this;
	}
	
	public TerminalBuilder withScheduler(Scheduler scheduler) {
		return this;
	}
	
	public TerminalBuilder withOrderProcessor(OrderProcessor processor) {
		return this;
	}
	
	public TerminalBuilder withCommonSecuritiesAndFactory(SecurityFactory factory) {
		return this;
	}
	
	public TerminalBuilder withCommonPortfoliosAndFactory(PortfolioFactory factory) {
		return this;
	}

}
