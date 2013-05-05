package ru.prolib.aquila.quik.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Кэш таблицы инструментов.
 */
public class SecuritiesCache extends MirrorCache {
	private final Map<SecurityDescriptor, SecurityCache> cache;

	public SecuritiesCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<SecurityDescriptor, SecurityCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись соответствующего инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return кэш-запись или null, если такого инструмента нет в кэше
	 */
	public synchronized SecurityCache get(SecurityDescriptor descr) {
		return cache.get(descr);
	}
	
	/**
	 * Сохранить или обновить кэш-запись инструмента.
	 * <p>
	 * @param security кэш-запись инструмента 
	 */
	public synchronized void put(SecurityCache security) {
		cache.put(security.getDescriptor(), security);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех записей
	 */
	public synchronized List<SecurityCache> getAll() {
		return new Vector<SecurityCache>(cache.values());
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != SecuritiesCache.class ) {
			return false;
		}
		SecuritiesCache o = (SecuritiesCache) other;
		return new EqualsBuilder()
			.append(dispatcher, o.dispatcher)
			.append(onUpdate, o.onUpdate)
			.append(cache, o.cache)
			.isEquals();
	}

}
