package ru.prolib.aquila.quik.dde;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * Кэш таблицы позиций по деривативам.
 */
public class PositionsFCache extends MirrorCache {
	private final Map<String, PositionFCache> cache;

	public PositionsFCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<String, PositionFCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}

	/**
	 * Получить кэш-запись позиции.
	 * <p>
	 * @param accountCode код торгового счета
	 * @param firmId код фирмы
	 * @param secShortName краткое наименование инструмента
	 * @return кэш запись или null, если такой позиции нет в кэше
	 */
	public synchronized PositionFCache get(String accountCode, String firmId,
			String secShortName)
	{
		return cache.get(accountCode + "#" + firmId + "#" + secShortName);
	}
	
	/**
	 * Сохранить или обновить кэш-запись позиции.
	 * <p>
	 * @param pos кэш-запись
	 */
	public synchronized void put(PositionFCache pos) {
		cache.put(pos.getAccountCode() + "#" + pos.getFirmId() + "#"
				+ pos.getSecurityShortName(), pos);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех записей
	 */
	public synchronized List<PositionFCache> getAll() {
		return new Vector<PositionFCache>(cache.values());
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != PositionsFCache.class ) {
			return false;
		}
		PositionsFCache o = (PositionsFCache) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(dispatcher, o.dispatcher)
			.append(onUpdate, o.onUpdate)
			.isEquals();
	}

}
