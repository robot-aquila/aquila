package ru.prolib.aquila.core;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Простая очередь событий.
 * <p>
 * Данная реализация подразумевает диспетчеризацию событий немедленно при
 * получении в пределах одного потока.
 * <p>
 * 2013-03-10<br>
 * $Id: SimpleEventQueue.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class SimpleEventQueue implements EventQueue {
	private final String id;
	
	public SimpleEventQueue(String id) {
		super();
		this.id = id;
	}
	
	public SimpleEventQueue() {
		this("default");
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void enqueue(Event event, List<EventListener> listeners) {
		for ( EventListener listener : listeners ) {
			listener.onEvent(event);
		}
	}

	@Override
	public void enqueue(Event event, EventDispatcher dispatcher) {
		enqueue(event, event.getType().getListeners());
	}

	@Override
	public boolean started() {
		return true;
	}

	@Override
	public boolean isDispatchThread() {
		return true;
	}

	@Override
	public boolean join(long timeout) throws InterruptedException {
		return true;
	}

	@Override
	public void join() throws InterruptedException {

	}
	
	/**
	 * Сравнивает только идентификаторы очередей одного класса.
	 * <p>
	 * Только для тестов.
	 */
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SimpleEventQueue.class ) {
			return false;
		}
		SimpleEventQueue o = (SimpleEventQueue) other;
		return new EqualsBuilder()
			.append(o.id, id)
			.isEquals();
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public String getId() {
		return id;
	}

}
