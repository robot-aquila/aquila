package ru.prolib.aquila.core;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Ассоциативный тип события.
 * <p>
 * Ассоциативные типы используются в тех случаях, когда однотипные события
 * приходят из одного источника и имеют отличительный признак, который может
 * служить для разделения обработчиков без введения дополнительной абстрактной
 * прослойки, которая ухудшает дизайн программы.
 * <p>
 * Например, при обработке транзакций события поступают из одного источника.
 * С точки зрения сервиса удобно рассматривать эти данные как события одного
 * типа. Но потребителям важно работать с конкретной транзакцией и им было бы
 * удобно подписываться только на конкретные транзакции, ассоциированные с
 * конкретным номером транзакции.
 * <p>
 * Данная реализация лишь немного изменяет поведение стандартного ассоциативного
 * массива, добавляя создание типа события при первом запросе и запрещая
 * операции перезаписи значений. 
 * <p>
 * @param <T> тип ассоциативного значения
 */
public class EventTypeMap<T> implements Map<T, EventTypeSI> {
	private final EventSystem es;
	private final EventDispatcher dispatcher;
	private final Map<T, EventTypeSI> storage;
	
	public EventTypeMap(EventSystem es, EventDispatcher dispatcher) {
		this(es, dispatcher, new Hashtable<T, EventTypeSI>());
	}
	
	public EventTypeMap(EventSystem es, EventDispatcher dispatcher,
			Map<T, EventTypeSI> storage)
	{
		super();
		this.es = es;
		this.dispatcher = dispatcher;
		this.storage = storage;
	}
	
	public Map<T, EventTypeSI> getUnderlyingMap() {
		return storage;
	}
	
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	public EventSystem getEventSystem() {
		return es;
	}

	@Override
	public void clear() {
		storage.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return storage.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return storage.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<T, EventTypeSI>> entrySet() {
		return storage.entrySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventTypeSI get(Object key) {
		EventTypeSI type = storage.get(key);
		if ( type == null ) {
			type = dispatcher.createType(key.toString());
			storage.put((T) key, type);
		}
		return type;
	}

	@Override
	public boolean isEmpty() {
		return storage.isEmpty();
	}

	@Override
	public Set<T> keySet() {
		return storage.keySet();
	}

	@Override
	public EventTypeSI put(T arg0, EventTypeSI arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends T, ? extends EventTypeSI> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventTypeSI remove(Object key) {
		EventTypeSI type = storage.remove(key);
		type.removeListeners();
		return type;
	}

	@Override
	public int size() {
		return storage.size();
	}

	@Override
	public Collection<EventTypeSI> values() {
		return storage.values();
	}
	
}
