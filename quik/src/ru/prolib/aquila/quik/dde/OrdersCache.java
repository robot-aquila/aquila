package ru.prolib.aquila.quik.dde;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;

/**
 * Кэш таблицы заявок.
 */
public class OrdersCache extends MirrorCache {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrderCache.class);
	}
	
	private final Map<Long, OrderCache> cache;
	
	public OrdersCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<Long, OrderCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public synchronized void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись соответствующую заявке.
	 * <p>
	 * @param orderId номер заявки
	 * @return кэш-запись или null, если заявки с таким номером нет в кэше
	 */
	public synchronized OrderCache get(Long orderId) {
		return cache.get(orderId);
	}
	
	/**
	 * Получить все кэш-записи.
	 * <p>
	 * @return список всех записей
	 */
	public synchronized List<OrderCache> getAll() {
		return new LinkedList<OrderCache>(cache.values());
	}
	
	/**
	 * Сохранить или обновить кэш-запись заявки.
	 * <p>
	 * @param order кэш-запись
	 */
	public synchronized void put(OrderCache order) {
		Long id = order.getId();
		if ( ! cache.containsKey(id) ) {
			Object args[] = { id, order.getTransId() };
			logger.debug("First time order cache id={}, transId={}", args);
		}
		cache.put(id, order);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == OrdersCache.class ) {
			OrdersCache o = (OrdersCache) other;
			return new EqualsBuilder()
				.append(cache, o.cache)
				.append(onUpdate, o.onUpdate)
				.append(dispatcher, o.dispatcher)
				.isEquals();
		} else {
			return false;
		}
	}

}
