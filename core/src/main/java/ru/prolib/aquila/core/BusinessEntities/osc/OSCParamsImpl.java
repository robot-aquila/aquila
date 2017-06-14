package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.concurrent.locks.Lock;

import ru.prolib.aquila.core.EventDispatcher;

public class OSCParamsImpl implements OSCParams {
	protected String id;
	protected EventDispatcher dispatcher;
	protected OSCController controller;
	protected Lock lock;
	
	@Override
	public String getID() {
		if ( id == null ) {
			throw new IllegalStateException("Undefined ID");
		}
		return id;
	}

	@Override
	public EventDispatcher getEventDispatcher() {
		if ( dispatcher == null ) {
			throw new IllegalStateException("Undefined event dispatcher");
		}
		return dispatcher;
	}

	@Override
	public OSCController getController() {
		if ( controller == null ) {
			throw new IllegalStateException("Undefined controller");
		}
		return controller;
	}
	
	@Override
	public Lock getLock() {
		if ( lock == null ) {
			throw new IllegalStateException("Undefined lock");
		}
		return lock;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setEventDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void setController(OSCController controller) {
		this.controller = controller;
	}
	
	public void setLock(Lock lock) {
		this.lock = lock;
	}

}
