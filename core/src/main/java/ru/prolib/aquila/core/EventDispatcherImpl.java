package ru.prolib.aquila.core;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Диспетчер событий. 
 * <p>
 * Передает событие на обработку очереди событий.
 * <p>
 * 2013-02-10<br>
 * $Id: EventDispatcherImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventDispatcherImpl implements EventDispatcher {
	public static final String AUTO_ID_PREFIX = "EvtDisp";
	private static volatile int autoId = 1;
	
	private final EventQueue queue;
	private final String id;
	
	/**
	 * Получить текущий идентификатор для автоназначения.
	 * <p>
	 * @return текущее значение идентификатора
	 */
	public static synchronized int getAutoId() {
		return autoId;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создается объект с идентификатором по умолчанию. Идентификатор
	 * формируется по шаблону {@link #AUTO_ID_PREFIX} + autoId;
	 * <p>
	 * @param queue очередь обработки событий
	 */
	public EventDispatcherImpl(EventQueue queue) {
		this(queue, AUTO_ID_PREFIX + (autoId ++));
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queue очередь обработки событий
	 * @param id идентификатор диспетчера (фактически владельца)
	 */
	public EventDispatcherImpl(EventQueue queue, String id) {
		super();
		if ( queue == null ) {
			throw new NullPointerException("Queue cannot be null");
		}
		this.queue = queue;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return asString();
	}
	
	@Override
	public String asString() {
		return id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * Получить очередь событий.
	 * <p>
	 * @return очередь событий
	 */
	public EventQueue getEventQueue() {
		return queue;
	}

	@Override
	public void addListener(EventType type, EventListener listener) {
		type.addListener(listener);
	}

	@Override
	public void removeListener(EventType type, EventListener listener) {
		type.removeListener(listener);
	}

	@Override
	public void dispatch(Event event) {
		if ( countListeners(event.getType()) > 0 ) {
			queue.enqueue(event, this);
		}
	}

	@Override
	public void close() {
		
	}

	@Override
	public int countListeners(EventType type) {
		return type.countListeners();
	}

	@Override
	public List<EventListener> getListeners(EventType type) {
		return type.getListeners();
	}

	@Override
	public boolean isTypeListener(EventType type, EventListener listener) {
		return type.isListener(listener);
	}

	@Override
	public void removeListeners(EventType type) {
		type.removeListeners();
	}

	@Override
	public void dispatchForCurrentList(Event event) {
		EventType type = event.getType();
		if ( type.countListeners() > 0 ) {
			queue.enqueue(event, type.getListeners());
		}
	}

	@Override
	public EventType createType() {
		return new EventTypeImpl(getId() + "." + EventTypeImpl.nextId());
	}

	@Override
	public EventType createType(String typeId) {
		return new EventTypeImpl(getId() + "." + typeId);
	}
	
	/**
	 * Сравнивает иерархию идентификаторов.
	 */
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventDispatcherImpl.class ) {
			return false;
		}
		EventDispatcherImpl o = (EventDispatcherImpl) other;
		return new EqualsBuilder()
			.append(o.queue, queue)
			.append(o.id, id)
			.isEquals();
	}

}
