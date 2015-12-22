package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.*;

/**
 * Кэш собственных сделок.
 * <p>
 * Обеспечивает доступ к системной информации о сделках, полученных через
 * QUIK API. Информация по сделкам используется для согласования заявок.
 */
public class OwnTradesCache {
	private final EventDispatcher dispatcher;
	private final EventType onUpdate;
	private final Map<Long, T2QTrade> data;
	
	public OwnTradesCache(EventDispatcher dispatcher, EventType onUpdate) {
		super();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
		data = new LinkedHashMap<Long, T2QTrade>();
	}
	
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить список всех собственных сделок.
	 * <p>
	 * @return список сделок
	 */
	public synchronized List<T2QTrade> get() {
		return new Vector<T2QTrade>(data.values());
	}
	
	/**
	 * Получить список сделок заявки.
	 * <p>
	 * @param systemOrderId номер заявки в торговой системе
	 * @return список сделок
	 */
	public synchronized List<T2QTrade> getByOrder(long systemOrderId) {
		List<T2QTrade> list = new Vector<T2QTrade>();
		for ( T2QTrade entry : data.values() ) {
			if ( entry.getOrderId() == systemOrderId ) {
				list.add(entry);
			}
		}
		return list;
	}
	
	/**
	 * Получить сделку по номеру сделки в торговой системе.
	 * <p>
	 * @param systemTradeId номер сделки
	 * @return сделка или null, если нет такой сделки
	 */
	public synchronized T2QTrade get(long systemTradeId) {
		return data.get(systemTradeId);
	}
	
	/**
	 * Получить тип события: при обновлении кэша.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdate() {
		return onUpdate;
	}
	
	/**
	 * Добавить информацию о сделки в кэш.
	 * <p>
	 * @param entry кэш-запись сделки
	 */
	public synchronized void put(T2QTrade entry) {
		set(entry);
		dispatcher.dispatch(new EventImpl(onUpdate));
	}
	
	/**
	 * Удалить сделки по системному номеру заявки.
	 * <p>
	 * @param systemOrderId номер заявки в торговой системе
	 */
	public synchronized void purge(long systemOrderId) {
		int removed = 0;
		for ( T2QTrade entry : getByOrder(systemOrderId) ) {
			removed ++;
			data.remove(entry.getId());
		}
		if ( removed > 0 ) {
			dispatcher.dispatch(new EventImpl(onUpdate));
		}
	}
	
	/**
	 * Установить кэш-запись.
	 * <p>
	 * Служебный метод. Не генерирует события.
	 * <p>
	 * @param entry кэш-запись
	 */
	synchronized void set(T2QTrade entry) {
		data.put(entry.getId(), entry);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OwnTradesCache.class ) {
			return false;
		}
		OwnTradesCache o = (OwnTradesCache) other;
		return new EqualsBuilder()
			.append(o.data, data)
			.append(o.dispatcher, dispatcher)
			.append(o.onUpdate, onUpdate)
			.isEquals();
	}

}
