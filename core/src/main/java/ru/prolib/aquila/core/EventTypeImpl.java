package ru.prolib.aquila.core;

import java.util.*;

/**
 * Basic event type.
 * <p>
 * 2012-04-09<br>
 */
public class EventTypeImpl implements EventType {
	public static final String AUTO_ID_PREFIX = "EvtType";
	private static int autoId = 1;
	private final String id;
	private final Set<EventListener> listeners;
	private final Set<EventType> alternates;
	
	/**
	 * Получить следующий идентификатор типа событий по-умолчанию.
	 * <p>
	 * @return идентификатор типа события
	 */
	public static synchronized String nextId() {
		return AUTO_ID_PREFIX + (autoId ++);
	}
	
	/**
	 * Получить текущий идентификатор для автоназначения.
	 * <p>
	 * @return текущее значение идентификатора
	 */
	public static synchronized int getAutoId() {
		return autoId;
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создается объект с идентификатором по умолчанию. Идентификатор
	 * формируется по шаблону {@link #AUTO_ID_PREFIX} + autoId
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
		this.id = id;
		listeners = new HashSet<>();
		alternates = new HashSet<>();
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
		listeners.add(listener);
	}
	
	@Override
	public synchronized void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized boolean isListener(EventListener listener) {
		return listeners.contains(listener);
	}
	
	@Override
	public EventListener listenOnce(EventListener listener) {
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
	public synchronized Set<EventListener> getListeners() {
		return new HashSet<>(listeners);
	}

	@Override
	public synchronized void addAlternateType(EventType type) {
		if ( type == null ) {
			throw new NullPointerException();
		}
		alternates.add(type);
	}

	@Override
	public synchronized void removeAlternateType(EventType type) {
		alternates.remove(type);
	}

	@Override
	public synchronized boolean isAlternateType(EventType type) {
		return alternates.contains(type);
	}

	@Override
	public synchronized Set<EventType> getAlternateTypes() {
		return alternates;
	}

	@Override
	public synchronized boolean hasAlternates() {
		return alternates.size() > 0;
	}

	@Override
	public synchronized void removeAlternates() {
		alternates.clear();
	}

	@Override
	public synchronized boolean hasListeners() {
		return countListeners() > 0;
	}

	@Override
	public synchronized int countAlternates() {
		return alternates.size();
	}

	@Override
	public void removeAlternatesAndListeners() {
		removeAlternates();
		removeListeners();
	}

}
