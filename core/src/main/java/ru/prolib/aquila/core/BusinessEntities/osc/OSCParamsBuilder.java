package ru.prolib.aquila.core.BusinessEntities.osc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventQueue;

public class OSCParamsBuilder {
	private final EventQueue queue;
	private String id;
	private EventDispatcher dispatcher;
	private OSCController controller;
	private Lock lock;
	
	public OSCParamsBuilder(EventQueue queue) {
		this.queue = queue;
	}
	
	public OSCParamsBuilder() {
		this(null);
	}
	
	public OSCParamsBuilder withID(String id) {
		this.id = id;
		return this;
	}
	
	public OSCParamsBuilder withEventDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		return this;
	}
	
	public OSCParamsBuilder withController(OSCController controller) {
		this.controller = controller;
		return this;
	}
	
	public OSCParamsBuilder withLock(Lock lock) {
		this.lock = lock;
		return this;
	}
	
	public OSCParams buildParams() {
		OSCParamsImpl params = createParams();
		params.setController(getController());
		params.setEventDispatcher(getEventDispatcher());
		params.setID(getID());
		params.setLock(getLock());
		return params;
	}
	
	protected String getID() {
		return id == null ? getDefaultID() : id;
	}
	
	protected EventQueue getEventQueue() {
		if ( queue == null ) {
			throw new IllegalStateException("An event queue is undefined");
		}
		return queue;
	}
	
	protected EventDispatcher getEventDispatcher() {
		if ( dispatcher == null ) {
			return new EventDispatcherImpl(getEventQueue(), getID());
		}
		return dispatcher;
	}
	
	protected OSCController getController() {
		if ( controller == null ) {
			return getDefaultController();
		}
		return controller;
	}
	
	protected Lock getLock() {
		if ( lock == null ) {
			return getDefaultLock();
		}
		return lock;
	}
	
	/**
	 * Override this method to build default container ID.
	 * <p>
	 * @return default ID
	 */
	protected String getDefaultID() {
		return "OSC";
	}
	
	/**
	 * Override this method to build default container controller.
	 * <p>
	 * @return default controller
	 */
	protected OSCController getDefaultController() {
		return new OSCControllerStub();
	}
	
	protected Lock getDefaultLock() {
		return new ReentrantLock();
	}
	
	/**
	 * Override this method to build parameters instance.
	 * <p>
	 * @return new instance of parameters
	 */
	protected OSCParamsImpl createParams() {
		return new OSCParamsImpl();
	}

}
