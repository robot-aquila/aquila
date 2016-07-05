package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

/**
 * Basic container implementation.
 */
public class ContainerImpl implements UpdatableContainer {
	protected final Lock lock;
	protected final EventQueue queue;
	private final Map<Integer, Object> values;
	private final Set<Integer> updated;
	private final EventType onUpdate, onAvailable;
	private final String id;
	private boolean available = false;
	private boolean closed = false;
	private Controller controller;
	
	public ContainerImpl(EventQueue queue, String id, Controller controller) {
		super();
		lock = new ReentrantLock();
		values = new HashMap<Integer, Object>();
		updated = new HashSet<Integer>();
		this.queue = queue;
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onAvailable = new EventTypeImpl(id + ".AVAILABLE");
		this.id = id;
		this.controller = controller;
	}
	
	public ContainerImpl(EventQueue queue, String id) {
		this(queue, id, new ControllerStub());
	}
	
	final public Controller getController() {
		return controller;
	}
	
	final public EventQueue getEventQueue() {
		return queue;
	}
	
	@Override
	public String getContainerID() {
		return id;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public void close() {
		lock.lock();
		try {
			available = false;
			closed = true;
			controller = null;
			onAvailable.removeListeners();
			onAvailable.removeAlternates();
			onUpdate.removeListeners();
			onUpdate.removeAlternates();
			values.clear();
			updated.clear();
		} finally {
			lock.unlock();
		}
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
	public String getString(int token) {
		return (String) getObject(token);
	}

	@Override
	public Integer getInteger(int token) {
		return (Integer) getObject(token);
	}

	@Override
	public Long getLong(int token) {
		return (Long) getObject(token);
	}

	@Override
	public Double getDouble(int token) {
		return (Double) getObject(token);
	}

	@Override
	public Boolean getBoolean(int token) {
		return (Boolean) getObject(token);
	}

	@Override
	public Instant getInstant(int token) {
		return (Instant) getObject(token);
	}

	@Override
	public Object getObject(int token) {
		lock.lock();
		try {
			return values.get(token);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isDefined(int[] tokens) {
		lock.lock();
		try {
			for ( int token : tokens ) {
				if ( values.get(token) == null ) {
					return false;
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean isDefined(int token) {
		lock.lock();
		try {
			return values.get(token) != null;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Integer> getUpdatedTokens() {
		lock.lock();
		try {
			return new HashSet<Integer>(updated);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean hasChanged(int token) {
		lock.lock();
		try {
			return updated.contains(token);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean hasChanged() {
		lock.lock();
		try {
			return updated.size() > 0;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean atLeastOneHasChanged(int[] tokens) {
		lock.lock();
		try {
			for ( int token : tokens ) {
				if ( updated.contains(token) ) {
					return true;
				}
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void update(Map<Integer, Object> tokens) {
		lock.lock();
		try {
			if ( closed ) {
				throw new IllegalStateException("Container is closed: " + id);
			}
			updated.clear();
			for ( Map.Entry<Integer, Object> entry : tokens.entrySet() ) {
				Integer key = entry.getKey();
				Object value = entry.getValue();
				Object current = values.get(key);
				if ( current == null ) {
					if ( value != null ) {
						updated.add(key);
						values.put(key, value);
					}
				} else {
					if ( ! current.equals(value) ) {
						updated.add(key);
						values.put(key, value);			
					}
				}
			}
			if ( updated.size() > 0 ) {
				EventFactory factory = createEventFactory();
				if ( available ) {
					queue.enqueue(onUpdate, factory);
					controller.processUpdate(this);
				} else if ( controller.hasMinimalData(this) ) {
					available = true;
					queue.enqueue(onAvailable, factory);
					controller.processAvailable(this);
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void resetChanges() {
		lock.lock();
		try {
			updated.clear();
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
	public boolean isClosed() {
		lock.lock();
		try {
			return closed;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Create event factory instance.
	 * <p>
	 * Override this method to produce specific events. Produced events must be
	 * derived of {@link ContainerEventImpl} class.
	 * <p>
	 * @return event factory
	 */
	protected EventFactory createEventFactory() {
		return new ContainerEventFactory(this);
	}

	/**
	 * Controller interface of container.
	 */
	public interface Controller {
		
		/**
		 * Check that container contains minimum required data.
		 * <p>
		 * This method called on every update until container in unavailable status.
		 * When this method returns true, then availability status switches on.
		 * Derived classes should use this method to check specific conditions based
		 * on container contents. 
		 * <p>
		 * @param container - the container
		 * @return true if minimum requirements are met, false otherwise
		 */
		public boolean hasMinimalData(Container container);
		
		/**
		 * Perform additional update processing.
		 * <p>
		 * This method called when an update event was enqueued. Derived classes
		 * should override this method to implement additional events.
		 * <p>
		 * @param container - the container
		 */
		public void processUpdate(Container container);
		
		/**
		 * Perform availability status switching.
		 * <p>
		 * This method called when container become available. Derived classes
		 * should override this method to implement additional events.
		 * <p>
		 * @param container - the container
		 */
		public void processAvailable(Container container);
		
	}
	
	public static class ControllerStub implements Controller {

		@Override
		public boolean hasMinimalData(Container container) {
			return true;
		}

		@Override
		public void processUpdate(Container container) {
			
		}

		@Override
		public void processAvailable(Container container) {
			
		}
		
	}
	
	static class ContainerEventFactory implements EventFactory {
		private final Container container;
		
		ContainerEventFactory(Container container) {
			super();
			this.container = container;
		}

		@Override
		public Event produceEvent(EventType type) {
			return new ContainerEventImpl(type, container);
		}
		
	}
	
	protected static class ContainerEventImpl extends EventImpl implements ContainerEvent {
		private final Container container;
		
		ContainerEventImpl(EventType type, Container container) {
			super(type);
			this.container = container;
		}

		@Override
		public Container getContainer() {
			return container;
		}
		
	}
	
}
