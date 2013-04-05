package ru.prolib.aquila.core;

import java.util.List;

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
	
	public SimpleEventQueue() {
		super();
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
		enqueue(event, dispatcher.getListeners(event.getType()));
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
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == SimpleEventQueue.class;
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

}
