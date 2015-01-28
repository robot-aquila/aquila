package ru.prolib.aquila.core;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Базовый тип события.
 * <p>
 * 2012-04-09<br>
 */
public class EventTypeImpl implements EventType {
	public static final String AUTO_ID_PREFIX = "EvtType";
	private static int autoId = 1;
	private final String id;
	private final List<EventListener> listeners;
	
	/**
	 * Получить следующий идентификатор типа событий по-умолчанию.
	 * <p>
	 * @return идентификатор типа события
	 */
	public static synchronized final String nextId() {
		return AUTO_ID_PREFIX + (autoId ++);
	}
	
	/**
	 * Получить текущий идентификатор для автоназначения.
	 * <p>
	 * @return текущее значение идентификатора
	 */
	public static synchronized final int getAutoId() {
		return autoId;
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создается объект с идентификатором по умолчанию. Идентификатор
	 * формируется по шаблону {@link #AUTO_ID_PREFIX} + autoId;
	 * <p>
	 */
	public EventTypeImpl() {
		this(nextId());
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * @param id идентификатор типа события
	 */
	public EventTypeImpl(String id) {
		super();
		this.id = id;
		listeners = new ArrayList<EventListener>();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public synchronized void addListener(EventListener listener) {
		if ( ! isListener(listener) ) {
			listeners.add(listener);
		}
	}
	
	@Override
	public synchronized void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized boolean isListener(EventListener listener) {
		for ( EventListener l : listeners ) {
			if ( listener == l ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public EventListener once(EventListener listener) {
		ListenOnce once = new ListenOnce(this, listener);
		once.start();
		return once;
	}

	@Override
	public synchronized void removeListeners() {
		listeners.clear();
	}

	@Override
	public synchronized int countListeners() {
		return listeners.size();
	}

	@Override
	public synchronized List<EventListener> getListeners() {
		return new Vector<EventListener>(listeners);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventTypeImpl.class ) {
			return false;
		}
		EventTypeImpl o = (EventTypeImpl) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.isEquals();
	}

	@Override
	public synchronized boolean compareListeners(EventType other) {
		synchronized ( other ) {
			List<EventListener> otherListeners = other.getListeners();
			if ( listeners.size() != otherListeners.size() ) {
				return false;
			}
			for ( int i = 0; i < listeners.size(); i ++ ) {
				if ( listeners.get(i) != otherListeners.get(i) ) {
					return false;
				}
			}
			return true;
		}
	}

}
