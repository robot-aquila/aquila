package ru.prolib.aquila.core;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import ru.prolib.aquila.core.eque.ESUtils;
import ru.prolib.aquila.core.eque.HierarchyOfAlternatesListener;
import ru.prolib.aquila.core.eque.HierarchyOfAlternatesObservable;

/**
 * Basic event type.
 * <p>
 * 2012-04-09<br>
 */
public class EventTypeImpl implements EventType, HierarchyOfAlternatesObservable, HierarchyOfAlternatesListener {
	public static final String AUTO_ID_PREFIX = "EvtType";
	private static int autoId = 1;
	private final String id;
	private final Set<EventListener> listeners;
	private final Set<EventType> alternates;
	
	// Hierarchy of Alternate Types
	private final Set<HierarchyOfAlternatesListener> hoaListeners;
	private final Set<EventType> hoaTypes;
	
	protected static Pair<Object, Object> getForLock(EventType one, EventType two) {
		int oh = one.hashCode(), th = two.hashCode();
		if ( oh < th ) {
			return Pair.of(one, two);
		} else if ( oh > th ) {
			return Pair.of(two, one);
		} else {
			int r = one.getId().compareTo(two.getId());
			if ( r < 0 ) {
				return Pair.of(one, two);
			} else {
				return Pair.of(two, one);
			}
		}
	}
	
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
		hoaListeners = new HashSet<>();
		hoaTypes = new HashSet<>();
		hoaTypes.add(this);
	}
	
	private void rebuildCache() {
		Set<EventType> x = ESUtils.getAllUniqueTypes(this);
		synchronized ( this ) {
			hoaTypes.clear();
			hoaTypes.addAll(x);
		}
	}
	
	private void notifyListeners(Set<HierarchyOfAlternatesListener> alreadyNotified) {
		Set<HierarchyOfAlternatesListener> hoals = null;
		synchronized ( this ) {
			hoals = new HashSet<>(hoaListeners);
		}
		for ( HierarchyOfAlternatesListener hoal : hoals ) {
			if ( ! alreadyNotified.contains(hoal) ) {
				alreadyNotified.add(hoal);
				hoal.onHierarchyOfAlternatesChange(alreadyNotified);
			}
		}
	}
	
	private void rebuildCacheAndNotifyListeners(Set<HierarchyOfAlternatesListener> alreadyNotified) {
		rebuildCache();
		notifyListeners(alreadyNotified);
	}
	
	private void rebuildCacheAndNotifyListeners() {
		rebuildCacheAndNotifyListeners(new HashSet<>());
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
	public void addAlternateType(EventType type) {
		if ( type == null ) {
			throw new NullPointerException();
		}
		if ( type == this ) {
			throw new IllegalArgumentException();
		}
		synchronized ( this ) {
			if ( ! alternates.contains(type) ) {
				alternates.add(type);
				type.addListener(this);
				// Do not call listeners here! It will cause deadlock!
			}
		}
		rebuildCacheAndNotifyListeners();
	}

	@Override
	public synchronized void removeAlternateType(EventType type) {
		if ( type == null ) {
			throw new NullPointerException();
		}
		synchronized ( this ) {
			if ( alternates.contains(type) ) {
				alternates.remove(type);
				type.removeListener(this);
				
				// Do not call listeners here! It will cause deadlocks!
			}
		}
		rebuildCacheAndNotifyListeners();
	}

	@Override
	public synchronized boolean isAlternateType(EventType type) {
		return alternates.contains(type);
	}

	@Override
	public synchronized Set<EventType> getAlternateTypes() {
		return new HashSet<>(alternates);
	}

	@Override
	public synchronized boolean hasAlternates() {
		return alternates.size() > 0;
	}

	@Override
	public void removeAlternates() {
		Set<EventType> types = null;
		synchronized ( this ) {
			types = new HashSet<>(alternates);
			alternates.clear();
		}
		for ( EventType t : types ) {
			t.removeListener(this);
		}
		rebuildCacheAndNotifyListeners();
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
		removeListeners();
		removeAlternates();
	}

	@Override
	public Set<EventType> getFullListOfRelatedTypes() {
		return new HashSet<>(hoaTypes);
	}
	
	public synchronized boolean isListener(HierarchyOfAlternatesListener listener) {
		return hoaListeners.contains(listener);
	}

	@Override
	public synchronized void addListener(HierarchyOfAlternatesListener listener) {
		hoaListeners.add(listener);
	}

	@Override
	public synchronized void removeListener(HierarchyOfAlternatesListener listener) {
		hoaListeners.remove(listener);
	}

	@Override
	public void onHierarchyOfAlternatesChange(Set<HierarchyOfAlternatesListener> alreadyNotified) {
		rebuildCacheAndNotifyListeners(alreadyNotified);
	}

}
