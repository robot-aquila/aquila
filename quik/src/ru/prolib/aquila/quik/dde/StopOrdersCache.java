package ru.prolib.aquila.quik.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;

/**
 * Кэш таблицы стоп-заявок.
 */
public class StopOrdersCache extends MirrorCache {
	private final Map<Long, StopOrderCache> cache;
	private final Map<Long, StopOrderCache> unadjusted;

	public StopOrdersCache(EventDispatcher dispatcher, EventType onUpdate) {
		super(dispatcher, onUpdate);
		cache = new LinkedHashMap<Long, StopOrderCache>();
		unadjusted = new Hashtable<Long, StopOrderCache>();
	}
	
	/**
	 * Проверить наличие несогласованных записей.
	 * <p>
	 * Проверяется наличие записей стоп-заявок в статусе FILLED но без
	 * номера связанной заявки. При наличии таких записей согласование
	 * заявок выполнять нельзя, так как может привести к ситуации временного
	 * отсутствия связи между заявками, что критично для обработчиков событий.
	 * <p>
	 * @return true - имеются несогласованные записи, false - несогласованных
	 * записей нет
	 */
	public synchronized boolean hasFilledWithoutLinkedId() {
		return unadjusted.size() > 0;
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
		Long id = order.getId();
		cache.put(id, order);
		if ( order.getStatus() == OrderStatus.FILLED ) {
			if ( order.getLinkedOrderId() == null ) {
				unadjusted.put(id, order);
			} else {
				unadjusted.remove(id);
			}
		}
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
	public synchronized boolean equals(Object other) {
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
