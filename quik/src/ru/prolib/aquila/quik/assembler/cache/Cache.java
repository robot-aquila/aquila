package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.t2q.T2QOrder;

/**
 * Кэш данных QUIK.
 * <p>
 * Обеспечивает доступ к кэшу данных, который используется для согласования
 * объектов бизнес-модели. 
 */
public class Cache {
	private final DescriptorsCache descrs;
	private final PositionsCache positions;
	private final OrdersCache orders;

	public Cache(DescriptorsCache descr, PositionsCache positions,
			OrdersCache orders)
	{
		super();
		this.descrs = descr;
		this.positions = positions;
		this.orders = orders;
	}
	
	/**
	 * Получить кэш дескрипторов инструментов.
	 * <p>
	 * Данный метод позволяет получить экземпляр кэша дескрипторов для
	 * последующего доступа с блокировкой.
	 * <p>
	 * @return кэш дескрипторов
	 */
	public DescriptorsCache getDescriptorsCache() {
		return descrs;
	}

	/**
	 * Получить кэш позиций.
	 * <p>
	 * Данный метод позволяет получить экземпляр кэша позиций для последующего
	 * доступа с блокировкой.
	 * <p>
	 * @return кэш позиций
	 */
	public PositionsCache getPositionsCache() {
		return positions;
	}

	/**
	 * Получить кэш заявок.
	 * <p>
	 * Данный метод позволяет получить экземпляр кэша заявок для последующего
	 * доступа с блокировкой.
	 * <p>
	 * @return кэш заявок
	 */
	public OrdersCache getOrdersCache() {
		return orders;
	}
	
	/**
	 * Получить список всех зарегистрированных дескрипторов.
	 * <p>
	 * @return список дескрипторов
	 */
	public List<SecurityDescriptor> getDescriptors() {
		return descrs.get();
	}
	
	/**
	 * Получить дескриптор по краткому наименованию.
	 * <p>
	 * @param shortName краткое наименование инструмента
	 * @return дескриптор или null, если нет соответствующего дескриптора
	 */
	public SecurityDescriptor getDescriptor(String shortName) {
		return descrs.get(shortName);
	}
	
	/**
	 * Получить дескриптор по комбинации кодов инструмента и класса.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор или null, если нет соответствующего дескриптора
	 */
	public SecurityDescriptor getDescriptor(String code, String classCode) {
		return descrs.get(code, classCode);
	}
	
	/**
	 * Получить тип события: при обновлении кэша дескрипторов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnDescriptorsUpdate() {
		return descrs.OnUpdate();
	}
	
	/**
	 * Обновить кэш дескрипторов.
	 * <p>
	 * @param entry кэш-запись инструмента
	 * @return true - был добавлен новый дескриптор, false - без изменений
	 */
	public boolean put(SecurityEntry entry) {
		return descrs.put(entry);
	}
	
	/**
	 * Получить список кэш-записей позиций.
	 * <p>
	 * @return список кэш-записей
	 */
	public List<PositionEntry> getPositions() {
		return positions.get();
	}
	
	/**
	 * Получить кэш-запись позиции.
	 * <p>
	 * @param account торговый счет
	 * @param shortName краткое наименование инструмента
	 * @return кэш-запись позиции или null, если нет соответствующей позиции 
	 */
	public PositionEntry getPosition(Account account, String shortName) {
		return positions.get(account, shortName);
	}
	
	/**
	 * Получить кэш-записи позиций по инструменту.
	 * <p>
	 * @param securityShortName краткое наименование инструмента
	 * @return список кэш-записей (может быть пустым)
	 */
	public List<PositionEntry> getPositions(String securityShortName) {
		return positions.get(securityShortName);
	}
	
	/**
	 * Получить тип события: при изменении кэша позиций.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionsUpdate() {
		return positions.OnUpdate();
	}
	
	/**
	 * Обновить кэш позиций.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	public void put(PositionEntry entry) {
		positions.put(entry);
	}
	
	/**
	 * Очистить кэш позиции.
	 * <p>
	 * @param entry кэш-запись, идентифицирующая позицию
	 */
	public void purge(PositionEntry entry) {
		positions.purge(entry);
	}
	
	/**
	 * Получить список кэш-записей заявок.
	 * <p>
	 * @return список кэш-записей
	 */
	public List<T2QOrder> getOrders() {
		return orders.get();
	}
	
	/**
	 * Получить кэш-запись заявки по локальному номеру.
	 * <p>
	 * @param localOrderId локальный номер заявки
	 * @return кэш-запись или null, если нет соответствующей заявки
	 */
	public T2QOrder getOrder(int localOrderId) {
		return orders.get(localOrderId);
	}
	
	/**
	 * Получить кэш-запись заявки по системному номеру.
	 * <p>
	 * @param systemOrderId номер заявки в торговой системе
	 * @return кэш-запись или null, если нет соответствующей заявки
	 */
	public T2QOrder getOrder(long systemOrderId) {
		return orders.get(systemOrderId);
	}
	
	/**
	 * Получить тип события: при изменении кэша заявок.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrdersUpdate() {
		return orders.OnUpdate();
	}
	
	/**
	 * Обновить кэш заявок.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public void put(T2QOrder entry) {
		orders.put(entry);
	}
	
	/**
	 * Очистить кэш заявки.
	 * <p>
	 * @param localOrderId локальный номер заявки
	 */
	public void purge(int localOrderId) {
		orders.purge(localOrderId);
	}
		
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Cache.class ) {
			return false;
		}
		Cache o = (Cache) other;
		return new EqualsBuilder()
			.append(o.descrs, descrs)
			.append(o.orders, orders)
			.append(o.positions, positions)
			.isEquals();
	}

}
