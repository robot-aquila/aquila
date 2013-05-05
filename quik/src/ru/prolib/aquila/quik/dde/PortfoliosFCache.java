package ru.prolib.aquila.quik.dde;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * Кэш таблицы портфелей ФОРТС.
 */
public class PortfoliosFCache extends MirrorCache {
	private final Map<String, PortfolioFCache> cache;

	public PortfoliosFCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<String, PortfolioFCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись портфеля.
	 * <p>
	 * @param accountCode код торгового счета
	 * @param firmId код фирмы
	 * @return кэш-запись или null, если такого портфеля нет в кэше
	 */
	public synchronized PortfolioFCache get(String accountCode, String firmId) {
		return cache.get(accountCode + "#" + firmId);
	}
	
	/**
	 * Сохранить или обновить кэш-запись портфеля.
	 * <p>
	 * @param port кэш-запись
	 */
	public synchronized void put(PortfolioFCache port) {
		cache.put(port.getAccountCode() + "#" + port.getFirmId(), port);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех записей
	 */
	public List<PortfolioFCache> getAll() {
		return new Vector<PortfolioFCache>(cache.values());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != PortfoliosFCache.class ) {
			return false;
		}
		PortfoliosFCache o = (PortfoliosFCache) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(dispatcher, o.dispatcher)
			.append(onUpdate, o.onUpdate)
			.isEquals();
	}

}
