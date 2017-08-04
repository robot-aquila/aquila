package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCEventImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;

/**
 * Observable state container implementation.
 * <p>
 * <b>Note:</b> If you want to override any update/consume methods use suppress/restore calls on event dispatcher
 * around the lock section.
 */
public class ObservableStateContainerImpl extends UpdatableStateContainerImpl implements ObservableStateContainer {
	protected final EventDispatcher dispatcher;
	private final EventType onUpdate;
	private final EventType onAvailable;
	private final EventType onClose;
	private OSCController controller;
	private boolean available = false;
	
	public ObservableStateContainerImpl(OSCParams params) {
		super(params.getID(), params.getLock());
		this.dispatcher = params.getEventDispatcher();
		this.controller = params.getController();
		final String id = params.getID();
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onAvailable = new EventTypeImpl(id + ".AVAILABLE");
		this.onClose = new EventTypeImpl(id + ".CLOSE");
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
		Instant time;
		lock.lock();
		try {
			if ( isClosed() ) {
				return;
			}
			time = controller.getCurrentTime(this);
			super.close();
			available = false;
			controller = null;
			onAvailable.removeAlternatesAndListeners();
			onUpdate.removeAlternatesAndListeners();
		} finally {
			lock.unlock();
		}
		dispatcher.dispatch(onClose, createEventFactory(time));
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
		dispatcher.suppressEvents();
		lock.lock();
		try {
			super.update(tokens); // inside the lock is OK in this case
			if ( hasChanged() ) {
				Instant time = controller.getCurrentTime(this);
				EventFactory factory = createEventFactory(time);
				dispatcher.dispatch(onUpdate, factory);
				controller.processUpdate(this, time);
				if ( ! available && controller.hasMinimalData(this, time) ) {
					available = true;
					dispatcher.dispatch(onAvailable, factory);
					controller.processAvailable(this, time);
				}
			}
		} finally {
			lock.unlock();
			dispatcher.restoreEvents();
		}
	}
	
	@Override
	public void suppressEvents() {
		dispatcher.suppressEvents();
	}

	@Override
	public void restoreEvents() {
		dispatcher.restoreEvents();
	}
	
	@Override
	public void purgeEvents() {
		dispatcher.purgeEvents();
	}

	/**
	 * Create event factory instance.
	 * <p>
	 * Override this method to produce specific events. Produced events must be
	 * derived of {@link OSCEventImpl} class.
	 * <p>
	 * @param time - time of event
	 * @return event factory
	 */
	protected EventFactory createEventFactory(Instant time) {
		return new OSCEventFactory(this, time);
	}

}
