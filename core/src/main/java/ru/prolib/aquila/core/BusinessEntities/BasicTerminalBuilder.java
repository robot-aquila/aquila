package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.DataProvider;

/**
 * The terminal builder.
 */
public class BasicTerminalBuilder extends TerminalParams {
	
	public BasicTerminalBuilder() {
		super();
	}
	
	public EditableTerminal buildTerminal() {
		return new TerminalImpl(buildParams());
	}
	
	public TerminalParams buildParams() {
		return this;
	}
	
	/**
	 * Use the event queue.
	 * <p>
	 * @param queue - the event queue
	 * @return the builder instance
	 */
	public BasicTerminalBuilder withEventQueue(EventQueue queue) {
		setEventQueue(queue);
		return this;
	}
	
	/**
	 * Use specified scheduler.
	 * <p>
	 * @param scheduler - scheduler to use
	 * @return the builder instance
	 */
	public BasicTerminalBuilder withScheduler(Scheduler scheduler) {
		setScheduler(scheduler);
		return this;
	}
	
	public BasicTerminalBuilder withDataProvider(DataProvider dataProvider) {
		setDataProvider(dataProvider);
		return this;
	}
	
	public BasicTerminalBuilder withTerminalID(String terminalID) {
		setTerminalID(terminalID);
		return this;
	}
	
	public BasicTerminalBuilder withObjectFactory(ObjectFactory factory) {
		setObjectFactory(factory);
		return this;
	}
	
}
