package ru.prolib.aquila.core;

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
	public void dispatch(Event event) {
		queue.enqueue(event);
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

}
