package ru.prolib.aquila.core;

import java.util.LinkedList;

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
	private final LinkedList<CachedEvent> cache;
	private int suppressCount = 0;
	
	/**
	 * Получить текущий идентификатор для автоназначения.
	 * <p>
	 * @return текущее значение идентификатора
	 */
	public static synchronized int getAutoId() {
		return autoId;
	}
	
	
	EventDispatcherImpl(EventQueue queue, String id, LinkedList<CachedEvent> eventCache) {
		this.queue = queue;
		this.id = id;
		this.cache = eventCache;
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
		this(queue, id, new LinkedList<>());
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
	public void dispatch(Event event) {
		dispatch(event.getType(), new EnqueueNewSigAdapter(event));
	}
	
	@Override
	public synchronized void dispatch(EventType type, EventFactory factory) {
		if ( suppressCount > 0 ) {
			cache.add(new CachedEvent(type, factory));
		} else {
			queue.enqueue(type, factory);
		}
	}

	@Override
	public void close() {
		
	}

	@Override
	public EventType createType() {
		return new EventTypeImpl(getId() + "." + EventTypeImpl.nextId());
	}

	@Override
	public EventType createType(String typeId) {
		return new EventTypeImpl(getId() + "." + typeId);
	}

	@Override
	public EventType createSyncType() {
		return new EventTypeImpl(getId() + "." + EventTypeImpl.nextId(), true);
	}

	@Override
	public EventType createSyncType(String typeId) {
		return new EventTypeImpl(getId() + "." + typeId, true);
	}

	@Override
	public synchronized void suppressEvents() {
		suppressCount ++;
	}

	@Override
	public synchronized void restoreEvents() {
		if ( suppressCount > 0 ) {
			suppressCount --;
			if ( suppressCount > 0 ) {
				return;
			}
		}
		for ( CachedEvent x : cache ) {
			queue.enqueue(x.type, x.factory);
		}
	}
	
	@Override
	public synchronized void purgeEvents() {
		if ( suppressCount > 0 ) {
			suppressCount --;
			cache.clear();
		}
	}
	
	static class CachedEvent {
		private final EventType type;
		private final EventFactory factory;
		
		CachedEvent(EventType type, EventFactory factory) {
			this.type = type;
			this.factory = factory;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != CachedEvent.class ) {
				return false;
			}
			CachedEvent o = (CachedEvent) other;
			return new EqualsBuilder()
					.append(type,  o.type)
					.append(factory, o.factory)
					.isEquals();
		}
		
	}
	
	static class EnqueueNewSigAdapter implements EventFactory {
		private final Event event;
		
		EnqueueNewSigAdapter(Event event) {
			this.event = event;
		}

		@Override
		public Event produceEvent(EventType type) {
			if ( event.isType(type) ) {
				return event;
			}
			throw new IllegalArgumentException("Modern consumer is incompatible with deprecated producer");
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != EnqueueNewSigAdapter.class ) {
				return false;
			}
			EnqueueNewSigAdapter o = (EnqueueNewSigAdapter) other;
			return o.event == event;
		}
		
	}

}
