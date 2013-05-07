package ru.prolib.aquila.quik.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Кэш таблицы стоп-заявок.
 */
public class StopOrdersCache extends MirrorCache {
	private final Map<Long, StopOrderCache> cache;

	public StopOrdersCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<Long, StopOrderCache>();
	}

	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись соответствующую стоп-заявке.
	 * <p>
	 * @param id номер заявки
	 * @return кэш запись или null, если стоп-заявки с таким номером нет в кэше
	 */
	public synchronized StopOrderCache get(Long id) {
		return cache.get(id);
	}
	
	/**
	 * Сохранить или обновить кэш-запись стоп-заявки.
	 * <p>
	 * @param order кэш-запись
	 */
	public synchronized void put(StopOrderCache order) {
		cache.put(order.getId(), order);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех кэш-записей
	 */
	public synchronized List<StopOrderCache> getAll() {
		return new Vector<StopOrderCache>(cache.values());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != StopOrdersCache.class ) {
			return false;
		}
		StopOrdersCache o = (StopOrdersCache) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(onUpdate, o.onUpdate)
			.append(dispatcher, o.dispatcher)
			.isEquals();
	}

}
