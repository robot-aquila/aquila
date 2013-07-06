package ru.prolib.aquila.quik.dde;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;

/**
 * Кэш таблицы собственных сделок.
 */
public class TradesCache extends MirrorCache {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TradesCache.class);
	}
	
	private final Map<Long, TradeCache> cache;

	public TradesCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<Long, TradeCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись соответствующей сделки.
	 * <p>
	 * @param id номер сделки
	 * @return кэш-запись или null, если такой сделки нет в кэше
	 */
	public synchronized TradeCache get(Long id) {
		return cache.get(id);
	}
	
	/**
	 * Сохранить или обновить кэш-запись сделки.
	 * <p>
	 * @param trade кэш-запись
	 */
	public synchronized void put(TradeCache trade) {
		Long id = trade.getId();
		if ( ! cache.containsKey(id) ) {
			Object args[] = { id, trade.getOrderId() };
			logger.debug("First time trade cache id={}, orderId={}", args);
		}
		cache.put(id, trade);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех записей
	 */
	public synchronized List<TradeCache> getAll() {
		return new Vector<TradeCache>(cache.values());
	}
	
	/**
	 * Получить кэш-записи сделок по заявке с указанным номером.
	 * <p>
	 * Записи НЕ отсортированы. Порядок представляемых сделок НЕ определен.
	 * <p>
	 * @param orderId номер заявки
	 * @return список кэш-записей заявки или пустой список, если нет кэш-записей
	 */
	public synchronized List<TradeCache> getAllByOrderId(Long orderId) {
		List<TradeCache> entries = new Vector<TradeCache>();
		for ( TradeCache entry : cache.values() ) {
			if ( orderId.equals(entry.getOrderId()) ) {
				entries.add(entry);
			}
		}
		return entries;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != TradesCache.class ) {
			return false;
		}
		TradesCache o = (TradesCache) other;
		return new EqualsBuilder()
			.append(dispatcher, o.dispatcher)
			.append(onUpdate, o.onUpdate)
			.append(cache, o.cache)
			.isEquals();
	}

}
