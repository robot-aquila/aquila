package ru.prolib.aquila.quik.dde;

import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * Кэш таблицы заявок.
 */
public class OrdersCache extends MirrorCache {
	private final Map<Long, OrderCache> cache;
	
	public OrdersCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new Hashtable<Long, OrderCache>();
	}
	
	/**
	 * Очистить кэш.
	 */
	public void clear() {
		cache.clear();
	}
	
	/**
	 * Получить кэш-запись соответствующую заявке.
	 * <p>
	 * @param orderId номер заявки
	 * @return кэш-запись или null, если заявки с таким номером нет в кэше
	 */
	public OrderCache get(Long orderId) {
		return cache.get(orderId);
	}
	
	/**
	 * Сохранить или обновить кэш-запись заявки.
	 * <p>
	 * @param order кэш-запись
	 */
	public void put(OrderCache order) {
		cache.put(order.getId(), order);
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
