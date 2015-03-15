package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.T2QOrder;

/**
 * Кэш заявок.
 * <p>
 * Обеспечивает доступ к системному состоянию заявки, полученному через
 * QUIK API. Системное состояние заявки необходимо для согласования заявки по
 * сделкам. При отмене заявки финализация ее локального отражения выполняется
 * при совпадении неисполненного остатка по системному состоянию и по сумме
 * сделок. Данное состояние сохраняется до момента финализации заявки, после
 * чего удаляется из кэша. Так же данный объект позволяет отслеживать изменения
 * кэша.
 */
public class OrdersCache {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onUpdate;
	private final Map<Long, T2QOrder> data;
	
	public OrdersCache(EventDispatcher dispatcher, EventTypeSI onUpdate) {
		super();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
		data = new LinkedHashMap<Long, T2QOrder>();
	}
	
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить список всех записей.
	 * <p>
	 * @return список записей
	 */
	public synchronized List<T2QOrder> get() {
		return new Vector<T2QOrder>(data.values());
	}
	
	/**
	 * Получить состояние заявки по локальному номеру.
	 * <p>
	 * Локальный номер заявки соответствует номеру транзакции в торговой
	 * системе. Данный метод выполняет поиск записи с соответствующим номером
	 * транзакции.
	 * <p>
	 * @param localOrderId номер заявки
	 * @return кэш-запись или null, если нет соответствующей кэш-записи
	 */
	public synchronized T2QOrder get(int localOrderId) {
		long id = localOrderId;
		for ( T2QOrder entry : data.values() ) {
			if ( id == entry.getTransId() ) {
				return entry;
			}
		}
		return null;
	}
	
	/**
	 * Получить состояние заявки по биржевому номеру.
	 * <p>
	 * Каждая зарегистрированная заявка имеет свой уникальный номер в торговой
	 * системе. Этот номер назначается биржей и в случае формирования кэша
	 * из одного источника этот номер гарантирует однозначную идентификацию
	 * заявки. Данный метод осуществляет поиск записи с соответствующим
	 * биржевым номером заявки.
	 * <p>
	 * @param systemOrderId номер заявки в торговой системе
	 * @return кэш-запись или null, если нет заявки, соответствующей номеру
	 */
	public synchronized T2QOrder get(long systemOrderId) {
		return data.get(systemOrderId);
	}
	
	/**
	 * Получить тип события: при изменении кэша.
	 * <p>
	 * Данный тип события позволяет отслеживать добавление новых записей,
	 * изменения существующих и удаления записей из кэша заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdate() {
		return onUpdate;
	}

	/**
	 * Сохранить состояние заявки.
	 * <p>
	 * Независимо от того, была ли добавлена новая запись или обновлена
	 * существующая, вызов данного метода всегда приводит к генерации
	 * события об обновлении кэша заявок.
	 * <p>
	 * @param entry кэш-запись состояния заявки
	 */
	public synchronized void put(T2QOrder entry) {
		data.put(entry.getOrderId(), entry);
		dispatcher.dispatch(new EventImpl(onUpdate));
	}
	
	/**
	 * Удалить кэш-запись состояния заявки.
	 * <p>
	 * Локальный номер заявки соответствует номеру транзакции в торговой
	 * системе.  Данный метод удаляет кэш-запись, соответствующую указанному
	 * локальному номеру заявки. Если запись (или записи) существовала и была
	 * удалена, то вызов приводит к генерации события об изменении кэша заявок. 
	 * <p>
	 * @param localOrderId локальный номер заявки
	 */
	public synchronized void purge(int localOrderId) {
		long id = localOrderId;
		List<Long> remove = new Vector<Long>();
		for ( T2QOrder entry : data.values() ) {
			if ( id == entry.getTransId() ) {
				remove.add(entry.getOrderId());
			}
		}
		if ( remove.size() > 0 ) {
			for ( Long removeId : remove ) data.remove(removeId);
			dispatcher.dispatch(new EventImpl(onUpdate));
		}
	}
	
	/**
	 * Установить кэш-запись (без генерации событий).
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param entry кэш-запись
	 */
	void set(T2QOrder entry) {
		data.put(entry.getOrderId(), entry);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrdersCache.class ) {
			return false;
		}
		OrdersCache o = (OrdersCache) other;
		return new EqualsBuilder()
			.append(o.data, data)
			.append(o.dispatcher, dispatcher)
			.append(o.onUpdate, onUpdate)
			.isEquals();
	}

}
