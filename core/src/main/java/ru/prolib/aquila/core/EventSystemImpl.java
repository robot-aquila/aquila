package ru.prolib.aquila.core;

import org.apache.commons.lang3.builder.*;

/**
 * Фабрика системы событий.
 * <p> 
 * Реализует конструкцию типовых объектов системы.
 * <p>
 * 2012-04-22<br>
 * $Id: EventSystemImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventSystemImpl implements EventSystem {
	private final EventQueue queue;
	
	/**
	 * Создать объект.
	 * <p>
	 * @param queue использовать очередь событий
	 */
	public EventSystemImpl(EventQueue queue) {
		super();
		if ( queue == null ) {
			throw new NullPointerException("Queue cannot be null");
		}
		this.queue = queue;
	}
	
	/**
	 * Создать объект.
	 * <p>
	 * При создании инстанцируется новый экземпляр очереди событий
	 * типа {@link EventQueueImpl}.
	 */
	public EventSystemImpl() {
		this(new EventQueueImpl());
	}
	
	/**
	 * Создать объект.
	 * <p>
	 * @param queueId идентификатор потока очереди событий
	 */
	public EventSystemImpl(String queueId) {
		this(new EventQueueImpl(queueId));
	}

	@Override
	public EventQueue getEventQueue() {
		return queue;
	}

	@Override
	public EventDispatcher createEventDispatcher() {
		return new EventDispatcherImpl(queue);
	}
	
	@Override
	public EventDispatcher createEventDispatcher(String id) {
		return new EventDispatcherImpl(queue, id);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 60523)
			.append(queue)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == EventSystemImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		EventSystemImpl o = (EventSystemImpl) other;
		return new EqualsBuilder()
			.append(queue, o.queue)
			.isEquals();
	}

}
