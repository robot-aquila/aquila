package ru.prolib.aquila.core.BusinessEntities;

import java.util.Map;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;

/**
 * Observable state container implementation.
 */
public class ObservableStateContainerImpl extends UpdatableStateContainerImpl implements ObservableStateContainer {
	protected final EventDispatcher dispatcher;
	private final EventType onUpdate;
	private final EventType onAvailable;
	private final EventType onClose;
	private OSCController controller;
	private boolean available = false;
	
	public ObservableStateContainerImpl(OSCParams params) {
		super(params.getID());
		this.dispatcher = params.getEventDispatcher();
		this.controller = params.getController();
		final String id = params.getID();
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onAvailable = new EventTypeImpl(id + ".AVAILABLE");
		this.onClose = new EventTypeImpl(id + ".CLOSE");
	}
	
	@Deprecated
	public ObservableStateContainerImpl(EventDispatcher dispatcher, String id, OSCController controller) {
		this(new OSCParamsBuilder()
				.withEventDispatcher(dispatcher)
				.withID(id)
				.withController(controller)
				.buildParams());
	}
	
	@Deprecated
	public ObservableStateContainerImpl(EventQueue queue, String id, OSCController controller) {
		this(new EventDispatcherImpl(queue, id), id, controller);
	}
	
	@Deprecated
	public ObservableStateContainerImpl(EventQueue queue, String id) {
		this(queue, id, new OSCControllerStub());
	}
	
	final public OSCController getController() {
		return controller;
	}
	
	final public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	public EventType onAvailable() {
		return onAvailable;
	}

	@Override
	public EventType onUpdate() {
		return onUpdate;
	}
	
	@Override
	public EventType onClose() {
		return onClose;
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			super.close();
			available = false;
			controller = null;
			onAvailable.removeAlternatesAndListeners();
			onUpdate.removeAlternatesAndListeners();
			dispatcher.dispatch(onClose, createEventFactory());
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean isAvailable() {
		lock.lock();
		try {
			return available;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void update(Map<Integer, Object> tokens) {
		lock.lock();
		try {
			super.update(tokens); // inside the lock is OK in this case
			if ( hasChanged() ) {
				EventFactory factory = createEventFactory();
				dispatcher.dispatch(onUpdate, factory);
				controller.processUpdate(this);
				if ( ! available && controller.hasMinimalData(this) ) {
					available = true;
					dispatcher.dispatch(onAvailable, factory);
					controller.processAvailable(this);
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void suppressEvents() {
		lock.lock();
		try {
			dispatcher.suppressEvents();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void restoreEvents() {
		lock.lock();
		try {
			dispatcher.restoreEvents();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Create event factory instance.
	 * <p>
	 * Override this method to produce specific events. Produced events must be
	 * derived of {@link OSCEventImpl} class.
	 * <p>
	 * @return event factory
	 */
	protected EventFactory createEventFactory() {
		return new OSCEventFactory(this);
	}

}
