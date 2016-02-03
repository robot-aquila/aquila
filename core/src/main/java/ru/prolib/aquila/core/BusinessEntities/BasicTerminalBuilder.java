package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.SimpleCounter;

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
		TerminalParams params = new TerminalParams();
		params.setScheduler(getScheduler());
		return params;
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
	
}
