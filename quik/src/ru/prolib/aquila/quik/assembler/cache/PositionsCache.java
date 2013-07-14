package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;

/**
 * Кэш позиций.
 * <p>
 * Обеспечивает хранение и доступ к данным позиции в период отсуствия
 * инструмента. Позволяет отслеживать изменения содержимого кэша, выборочно
 * запрашивать список записей, связанных с определенным инструментом и удалять
 * записи из кэша. 
 */
public class PositionsCache {
	private static final String SEP = "#";
	private final EventDispatcher dispatcher;
	private final EventType onUpdate;
	private final Map<String, PositionEntry> data;
	
	public PositionsCache(EventDispatcher dispatcher, EventType onUpdate) {
		super();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
		this.data = new LinkedHashMap<String, PositionEntry>();
	}
	
	EventDispatcher getEventDispatcher() {
		return dispatcher;
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
	 * Обновить кэш-запись позиции.
	 * <p>
	 * @param entry кэш-запись
	 */
	public synchronized void put(PositionEntry entry) {
		set(entry);
		dispatcher.dispatch(new EventImpl(onUpdate));
	}
	
	/**
	 * Получить данные позиции.
	 * <p>
	 * Возвращает кэш-запись позиции, идентифицируя ее по комбинации счета и
	 * краткого наименования инструмента.
	 * <p>
	 * @param account торговый счет
	 * @param shortName краткое наименование инструмента
	 * @return кэш-запись или null, если нет соответствующей записи
	 */
	public synchronized PositionEntry get(Account account, String shortName) {
		return data.get(getKey(account, shortName));
	}
	
	/**
	 * Получить все позиции по инструменту.
	 * <p>
	 * @param securityShortName краткое наименование инструмента
	 * @return список кэш-записей позиций
	 */
	public synchronized List<PositionEntry> get(String securityShortName) {
		List<PositionEntry> list = new Vector<PositionEntry>();
		for ( PositionEntry entry : get() ) {
			if ( entry.getSecurityShortName().equals(securityShortName) ) {
				list.add(entry);
			}
		}
		return list;
	}
	
	/**
	 * Получить список всех записей.
	 * <p>
	 * @return список кэш-записей позиций
	 */
	public synchronized List<PositionEntry> get() {
		return new Vector<PositionEntry>(data.values());
	}
	
	/**
	 * Удалить данные позиции из кэша.
	 * <p>
	 * @param entry кэш-запись, идентифицирующая позицию
	 */
	public synchronized void purge(PositionEntry entry) {
		data.remove(getKey(entry));
	}
	
	/**
	 * Сохранить кэш-запись (без генерации событий).
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	void set(PositionEntry entry) {
		 data.put(getKey(entry), entry);
	}
	
	/**
	 * Определить ключ кэш-записи.
	 * <p>
	 * @param entry кэш-запись позиции
	 * @return ключ
	 */
	private String getKey(PositionEntry entry) {
		return getKey(entry.getAccount(), entry.getSecurityShortName());
	}
	
	/**
	 * Определить ключ кэш-записи.
	 * <p>
	 * @param account торговый счет
	 * @param shortName краткое наименование инструмента
	 * @return ключ
	 */
	private String getKey(Account account, String shortName) {
		return account.toString() + SEP + shortName;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionsCache.class ) {
			return false;
		}
		PositionsCache o = (PositionsCache) other;
		return new EqualsBuilder()
			.append(o.data, data)
			.append(o.dispatcher, dispatcher)
			.append(o.onUpdate, onUpdate)
			.isEquals();
	}

}
