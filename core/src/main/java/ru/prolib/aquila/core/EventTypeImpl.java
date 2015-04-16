package ru.prolib.aquila.core;

import java.util.*;

/**
 * Базовый тип события.
 * <p>
 * 2012-04-09<br>
 */
public class EventTypeImpl implements EventTypeSI {
	public static final String AUTO_ID_PREFIX = "EvtType";
	private static int autoId = 1;
	private final String id;
	private final List<EventListener> asyncListeners, syncListeners;
	private final boolean onlySync;
	
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
		this(id, false);
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создается объект с идентификатором по умолчанию. Идентификатор
	 * формируется по шаблону {@link #AUTO_ID_PREFIX} + autoId
	 * <p>
	 * @param onlySync разрешить только синхронную трансляцию
	 */
	public EventTypeImpl(boolean onlySync) {
		this(nextId(), onlySync);
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * @param id идентификатор типа события
	 * @param onlySync разрешить только синхронную трансляцию
	 */
	public EventTypeImpl(String id, boolean onlySync) {
		this.id = id;
		this.onlySync = onlySync;
		asyncListeners = new ArrayList<EventListener>();
		syncListeners = new ArrayList<EventListener>();
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
		if ( onlySync ) {
			addSyncListener(listener);
		} else {
			syncListeners.remove(listener);
			if ( ! isAsyncListener(listener) ) {
				asyncListeners.add(listener);
			}			
		}
	}
	
	@Override
	public synchronized void removeListener(EventListener listener) {
		asyncListeners.remove(listener);
		syncListeners.remove(listener);
	}

	@Override
	public synchronized boolean isListener(EventListener listener) {
		return isAsyncListener(listener) || isSyncListener(listener);
	}
	
	@Override
	public EventListener listenOnce(EventListener listener) {
		ListenOnce once = new ListenOnce(this, listener);
		once.start();
		return once;
	}

	@Override
	public synchronized void removeListeners() {
		asyncListeners.clear();
		syncListeners.clear();
	}

	@Override
	public synchronized int countListeners() {
		return asyncListeners.size() + syncListeners.size();
	}

	@Override
	public synchronized List<EventListener> getAsyncListeners() {
		return new ArrayList<EventListener>(asyncListeners);
	}

	@Override
	public synchronized List<EventListener> getSyncListeners() {
		return new ArrayList<EventListener>(syncListeners);
	}

	@Override
	public synchronized boolean isSyncListener(EventListener listener) {
		for ( EventListener l : syncListeners ) {
			if ( listener == l ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized boolean isAsyncListener(EventListener listener) {
		for ( EventListener l : asyncListeners ) {
			if ( listener == l ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void addSyncListener(EventListener listener) {
		asyncListeners.remove(listener);
		if ( ! isSyncListener(listener) ) {
			syncListeners.add(listener);
		}
	}

	@Override
	public boolean isOnlySyncMode() {
		return onlySync;
	}

}
