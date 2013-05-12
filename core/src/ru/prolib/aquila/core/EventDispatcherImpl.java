package ru.prolib.aquila.core;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;

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
	public static final String AUTO_ID_PREFIX = "EvtDisp";
	private static volatile int autoId = 1;
	
	private final EventQueue queue;
	private final Map<EventType, Vector<EventListener>> listeners;
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
		// ВАЖНО! Именно этот класс, иначе сравнение структур не будет работать!
		listeners = new LinkedHashMap<EventType, Vector<EventListener>>();
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

	@Override
	public EventType createType() {
		return new EventTypeImpl(this);
	}

	@Override
	public EventType createType(String typeId) {
		return new EventTypeImpl(this, typeId);
	}
	
	/**
	 * Сравнить составов диспетчеров.
	 * <p>
	 * Два разных экземпляра диспетчера всегда представляют разные стеки типов. 
	 * Метод сравнения сравнивает только структуру диспетчеров, которая включает
	 * в себя идентификатор диспетчера и набор типов событий вместе с их
	 * наборами обозревателей. Так как просто сравнить карты тип->наблюдатели
	 * нельзя (каждый экземпляр типа событий всегда представляет уникальный
	 * ключ), единственный способ сравнения - это сравнить структуру типов в
	 * порядке добавления типов в структуру диспетчера. Для сравнения стеков
	 * типов событий, из карты тип->наблюдатели каждого диспетчера извлекается
	 * список ключей, которые в последствии и сравнивается (в дополнение к
	 * идентификатору диспетчера). 
	 * <p>
	 * Данный метод предназначен исключительно для использования в тестах.
	 * Он значительно сокращает код теста и позволяет избежать моков для
	 * воспроизведения типа. Сравнение данным методом никогда не должно
	 * использоваться в рабочих процессах.
	 * <p> 
	 */
	@Override
	public final synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventDispatcherImpl.class ) {
			return false;
		}
		EventDispatcherImpl o = (EventDispatcherImpl) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.append(new Vector<EventType>(listeners.keySet()),
					new Vector<EventType>(o.listeners.keySet()))
			.isEquals();
	}

}
