package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Кэш сделок.
 * <p>
 * Обеспечивает хранение информации о всех сделках до момента обработки.
 */
public class TradesCache {
	private final EventDispatcher dispatcher;
	private final EventType onUpdate;
	private final LinkedList<TradesEntry> data;
	
	public TradesCache(EventDispatcher dispatcher, EventType onUpdate) {
		super();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
		data = new LinkedList<TradesEntry>();
	}
	
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить запись из начала очереди.
	 * <p>
	 * @return элемент кэша или null, если кэш пуст
	 */
	public synchronized TradesEntry getFirst() {
		return data.size() > 0 ? data.getFirst() : null;
	}
	
	/**
	 * Удалить запись из начала очереди.
	 * <p>
	 * Если кэш пуст, то никаких действий не выполняется.
	 */
	public synchronized void purgeFirst() {
		if ( data.size() > 0 ) {
			data.removeFirst();
			dispatcher.dispatch(new CacheEvent(onUpdate, false));
		}
	}
	
	/**
	 * Получить содержимое очереди.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<TradesEntry> get() {
		return new Vector<TradesEntry>(data);
	}
	
	/**
	 * Получить тип события: при изменении кэша.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdate() {
		return onUpdate;
	}
	
	/**
	 * Добавить запись в конец очереди.
	 * <p>
	 * @param entry кэш-запись
	 */
	public synchronized void add(TradesEntry entry) {
		addEntry(entry);
		dispatcher.dispatch(new CacheEvent(onUpdate, true));
	}
	
	/**
	 * Добавить запись в конец очереди.
	 * <p>
	 * Служебный метод. Не генерирует событий.
	 * <p>
	 * @param entry кэш запись
	 */
	synchronized void addEntry(TradesEntry entry) {
		data.add(entry);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradesCache.class ) {
			return false;
		}
		TradesCache o = (TradesCache) other;
		return new EqualsBuilder()
			.append(o.data, data)
			.append(o.dispatcher, dispatcher)
			.append(o.onUpdate, onUpdate)
			.isEquals();
	}

}
