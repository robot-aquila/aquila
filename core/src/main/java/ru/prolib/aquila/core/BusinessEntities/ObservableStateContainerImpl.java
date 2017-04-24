package ru.prolib.aquila.core.BusinessEntities;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

/**
 * Observable state container implementation.
 */
public class ObservableStateContainerImpl extends UpdatableStateContainerImpl implements ObservableStateContainer {
	protected final EventQueue queue;
	private final EventType onUpdate;
	private final EventType onAvailable;
	private final EventType onClose;
	private Controller controller;
	private boolean available = false;
	
	public ObservableStateContainerImpl(EventQueue queue, String id, Controller controller) {
		super(id);
		this.queue = queue;
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onAvailable = new EventTypeImpl(id + ".AVAILABLE");
		this.onClose = new EventTypeImpl(id + ".CLOSE");
		this.controller = controller;
	}
	
	public ObservableStateContainerImpl(EventQueue queue, String id) {
		this(queue, id, new ControllerStub());
	}
	
	final public Controller getController() {
		return controller;
	}
	
	final public EventQueue getEventQueue() {
		return queue;
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
			queue.enqueue(onClose,  createEventFactory());
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
				queue.enqueue(onUpdate, factory);
				controller.processUpdate(this);
				if ( ! available && controller.hasMinimalData(this) ) {
					available = true;
					queue.enqueue(onAvailable, factory);
					controller.processAvailable(this);
				}
			}
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
		public boolean hasMinimalData(ObservableStateContainer container);
		
		/**
		 * Perform additional update processing.
		 * <p>
		 * This method called when an update event was enqueued. Derived classes
		 * should override this method to implement additional events.
		 * <p>
		 * @param container - the container
		 */
		public void processUpdate(ObservableStateContainer container);
		
		/**
		 * Perform availability status switching.
		 * <p>
		 * This method called when container become available. Derived classes
		 * should override this method to implement additional events.
		 * <p>
		 * @param container - the container
		 */
		public void processAvailable(ObservableStateContainer container);
		
	}
	
	public static class ControllerStub implements Controller {

		@Override
		public boolean hasMinimalData(ObservableStateContainer container) {
			return true;
		}

		@Override
		public void processUpdate(ObservableStateContainer container) {
			
		}

		@Override
		public void processAvailable(ObservableStateContainer container) {
			
		}
		
	}
	
	static class ContainerEventFactory implements EventFactory {
		private final ObservableStateContainer container;
		private final Set<Integer> updatedTokens;
		
		ContainerEventFactory(ObservableStateContainer container) {
			super();
			this.container = container;
			this.updatedTokens = new HashSet<>(container.getUpdatedTokens());
		}

		@Override
		public Event produceEvent(EventType type) {
			ContainerEventImpl e = new ContainerEventImpl(type, container);
			e.setUpdatedTokens(updatedTokens);
			return e;
		}
		
	}
	
	protected static class ContainerEventImpl extends EventImpl implements ContainerEvent {
		private final ObservableStateContainer container;
		private Set<Integer> updatedTokens;
		
		ContainerEventImpl(EventType type, ObservableStateContainer container) {
			super(type);
			this.container = container;
		}
		
		public void setUpdatedTokens(Set<Integer> updatedTokens) {
			this.updatedTokens = updatedTokens;
		}
		
		@Override
		public ObservableStateContainer getContainer() {
			return container;
		}

		@Override
		public boolean hasChanged(int token) {
			return updatedTokens != null && updatedTokens.contains(token);
		}

		@Override
		public Set<Integer> getUpdatedTokens() {
			return updatedTokens;
		}

	}
	
}
