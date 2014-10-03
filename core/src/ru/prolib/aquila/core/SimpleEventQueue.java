package ru.prolib.aquila.core;

import java.util.LinkedList;
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

	private static class CachedEvent {
		private final Event event;
		private final List<EventListener> listeners;
		
		CachedEvent(Event event, List<EventListener> listeners) {
			super();
			this.event = event;
			this.listeners = listeners;
		}
		
		void fire() {
			for ( EventListener listener : listeners ) {
				listener.onEvent(event);
			}
		}
		
	}
	
	private final LinkedList<CachedEvent> cache = new LinkedList<CachedEvent>();

	@Override
	public void enqueue(Event event, List<EventListener> listeners) {
		// Кэшированние событий используется для соблюдения требования
		// диспетчеризации событий в порядке их поступления. Если этого не
		// сделать, то при генерации событий из обработчика другого события
		// нарушение неизбежно.
		cache.addLast(new CachedEvent(event, listeners));
		if ( cache.size() == 1 ) {
			// Только в этом случае мы можем начинать диспетчеризацию.
			// Более одного элемента в кэше означает, что выше по стеку
			// очередь уже обрабатывается.
			do {
				cache.getFirst().fire(); // Сразу удалять нельзя!
				cache.pollFirst();
			} while ( cache.size() > 0 );
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
