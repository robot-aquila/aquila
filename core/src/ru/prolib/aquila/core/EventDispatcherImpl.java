package ru.prolib.aquila.core;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Диспетчер событий. 
 * <p>
 * Ведет списки наблюдателей для каждого типа события. 
 * Передает событие на обработку очереди событий.
 * <p>
 * 2013-02-10<br>
 * $Id: EventDispatcherImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventDispatcherImpl implements EventDispatcher {
	@SuppressWarnings("unused")
	private static final Logger logger;
	public static final String AUTO_ID_PREFIX = "EvtDisp";
	
	private static int autoId = 1;
	
	static {
		logger = LoggerFactory.getLogger(EventDispatcherImpl.class);
	}
	
	private final EventQueue queue;
	private final HashMap<EventType, Vector<EventListener>> listeners;
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
		listeners = new HashMap<EventType, Vector<EventListener>>();
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
	public synchronized
		void addListener(EventType type, EventListener listener)
	{
		Vector<EventListener> list = listeners.get(type);
		if ( list == null ) {
			list = new Vector<EventListener>();
			listeners.put(type, list);
		}
		if ( ! list.contains(listener) ) {
			list.add(listener);
		}
	}

	@Override
	public synchronized
		void removeListener(EventType type, EventListener listener)
	{
		Vector<EventListener> list = listeners.get(type);
		if ( list != null ) {
			list.remove(listener);
			if ( list.size() == 0 ) {
				listeners.remove(type);
			}
		}
	}

	@Override
	public void dispatch(Event event) {
		if ( countListeners(event.getType()) > 0 ) {
			queue.enqueue(event, this);
		}
	}

	@Override
	public synchronized void close() {
		listeners.clear();
	}

	@Override
	public synchronized int countListeners(EventType type) {
		Vector<EventListener> list = listeners.get(type);
		return list == null ? 0 : list.size();
	}

	@Override
	public synchronized List<EventListener> getListeners(EventType type) {
		Vector<EventListener> list = listeners.get(type);
		return list == null ?
				new Vector<EventListener>() : new Vector<EventListener>(list);
	}

	@Override
	public synchronized
		boolean isTypeListener(EventType type, EventListener listener)
	{
		Vector<EventListener> list = listeners.get(type);
		return list == null || ! list.contains(listener) ? false : true;
	}

	@Override
	public synchronized void removeListeners(EventType type) {
		listeners.remove(type);
	}

	@Override
	public synchronized void dispatchForCurrentList(Event event) {
		List<EventListener> list = getListeners(event.getType());
		if ( list.size() > 0 ) {
			queue.enqueue(event, list);
		}
	}

}
