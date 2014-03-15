package ru.prolib.aquila.core.sm;

import ru.prolib.aquila.core.*;

/**
 * Триггер по событию.
 */
public class SMEventTrigger implements SMTrigger, EventListener {
	private final EventType eventType;
	private final Runnable actor;
	private boolean active = false;
	
	public SMEventTrigger(EventType eventType, Runnable actor) {
		super();
		this.eventType = eventType;
		this.actor = actor;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( active ) {
			actor.run();
		}
	}

	@Override
	public synchronized void enable() {
		if ( ! active ) {
			eventType.addListener(this);
			active = true;
		}
	}

	@Override
	public synchronized void disable() {
		if ( active ) {
			eventType.removeListener(this);
			active = false;
		}
	}

	@Override
	public void input(Object object) {
		// TODO Auto-generated method stub
		
	}

}
