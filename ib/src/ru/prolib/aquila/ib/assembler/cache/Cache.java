package ru.prolib.aquila.ib.assembler.cache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;

/**
 * Фасад кэша данных IB API.
 */
public class Cache {
	private final EventDispatcher dispatcher;
	private final EventType onContractUpdated;
	private final EventType onOrderUpdated;
	private final EventType onOrderStatusUpdated;
	private final EventType onPositionUpdated;
	private final EventType onExecUpdated;
	private final Map<Integer, ContractEntry> contracts;
	private final Map<Long, OrderEntry> orders;
	private final Map<Long, OrderStatusEntry> orderStatuses;
	private final Map<Long, Map<Long, ExecEntry>> execs;
	private final Map<String, PositionEntry> positions;
	
	public Cache(EventDispatcher dispatcher, EventType onContractUpdated,
			EventType onOrderUpdated, EventType onOrderStatusUpdated,
			EventType onPositionUpdated, EventType onExecUpdated)
	{
		super();
		this.dispatcher = dispatcher;
		this.onContractUpdated = onContractUpdated;
		this.onOrderUpdated = onOrderUpdated;
		this.onOrderStatusUpdated = onOrderStatusUpdated;
		this.onPositionUpdated = onPositionUpdated;
		this.onExecUpdated = onExecUpdated;
		contracts = new LinkedHashMap<Integer, ContractEntry>();
		orders = new LinkedHashMap<Long, OrderEntry>();
		orderStatuses = new LinkedHashMap<Long, OrderStatusEntry>();
		execs = new LinkedHashMap<Long, Map<Long, ExecEntry>>();
		positions = new LinkedHashMap<String, PositionEntry>();
	}
	
	/**
	 * Получить экземпляр диспетчера событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: при обновлении деталей контракта.
	 * <p>
	 * @return тип события
	 */
	public EventType OnContractUpdated() {
		return onContractUpdated;
	}
	
	/**
	 * Получить тип события: при обновлении информации о новой заявке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderUpdated() {
		return onOrderUpdated;
	}
	
	/**
	 * Получить тип события: при обновлении информации о статусе заявки.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderStatusUpdated() {
		return onOrderStatusUpdated;
	}
	
	/**
	 * Получить тип события: при обновлении позиции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionUpdated() {
		return onPositionUpdated;
	}
	
	/**
	 * Получить тип события: при обновлении информации о сделке.
	 * <p>
	 * @return тип события
	 */
	public EventType OnExecUpdated() {
		return onExecUpdated;
	}
	
	/**
	 * Получить кэш-запись контракта по идентификатору.
	 * <p>
	 * @param contractId идентификатор контракта
	 * @return кэш-запись или null, если нет соответствующей записи
	 */
	public synchronized ContractEntry getContract(int contractId) {
		return contracts.get(contractId);
	}
	
	/**
	 * Получить кэш-запись заявки.
	 * <p>
	 * @param id номер заявки
	 * @return кэш-запись заявки или null, если нет соответствующей записи
	 */
	public synchronized OrderEntry getOrder(Long id) {
		return orders.get(id);
	}
	
	/**
	 * Получить кэш-запись статуса заявки.
	 * <p>
	 * @param id номер заявки
	 * @return кэш-запись статуса заявки или null, если нет записи
	 */
	public synchronized OrderStatusEntry getOrderStatus(Long id) {
		return orderStatuses.get(id);
	}
	
	/**
	 * Получить кэш-запись позиции.
	 * <p>
	 * @param account торговый счет
	 * @param contractId идентификатор контракта
	 * @return кэш-запись или null, если нет соответствующей записи
	 */
	public synchronized
		PositionEntry getPosition(Account account, int contractId)
	{
		return positions.get(getPositionKey(account, contractId));
	}
	
	/**
	 * Сформировать ключ позиции.
	 * <p>
	 * @param account торговый счет
	 * @param contractId идентификатор контракта
	 * @return строковый ключ позиции
	 */
	private String getPositionKey(Account account, int contractId) {
		return account.toString() + ":" + Integer.toString(contractId);
	}
	
	/**
	 * Сформировать ключ позиции.
	 * <p>
	 * @param entry кэш-запись позиции
	 * @return строковый ключ позиции
	 */
	private String getPositionKey(PositionEntry entry) {
		return getPositionKey(entry.getAccount(), entry.getContractId());
	}
	
	/**
	 * Получить список сделок заявки.
	 * <p>
	 * @param id номер заявки
	 * @return кэш-запись или null, если нет соответствующей записи
	 */
	public synchronized List<ExecEntry> getOrderExecutions(Long id) {
		Map<Long, ExecEntry> map = execs.get(id);
		return map == null ? new Vector<ExecEntry>() :
			new Vector<ExecEntry>(map.values());
	}
	
	/**
	 * Получить список кэш-записей контрактов.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<ContractEntry> getContractEntries() {
		return new Vector<ContractEntry>(contracts.values());
	}
	
	/**
	 * Получить список кэш-записей заявок.
	 * <p> 
	 * @return список кэш-записей
	 */
	public synchronized List<OrderEntry> getOrderEntries() {
		return new Vector<OrderEntry>(orders.values());
	}
	
	/**
	 * Получить список кэш-записей статусов заявок.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<OrderStatusEntry> getOrderStatusEntries() {
		return new Vector<OrderStatusEntry>(orderStatuses.values());
	}
	
	/**
	 * Получить список кэш-записей позиций.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<PositionEntry> getPositionEntries() {
		return new Vector<PositionEntry>(positions.values());
	}
	
	/**
	 * Получить список кэш-записей сделок.
	 * <p>
	 * @return список кэш-записей
	 */
	public synchronized List<ExecEntry> getExecEntries() {
		List<ExecEntry> result = new Vector<ExecEntry>();
		for ( Map<Long, ExecEntry> map : execs.values() ) {
			result.addAll(map.values());
		}
		return result;
	}
	
	/**
	 * Обновить кэш контрактов.
	 * <p>
	 * @param entry кэш-запись контракта
	 */
	public synchronized void update(ContractEntry entry) {
		contracts.put(entry.getContractId(), entry);
		dispatcher.dispatch(new EventImpl(onContractUpdated));
	}
	
	/**
	 * Обновить кэш заявок.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public synchronized void update(OrderEntry entry) {
		orders.put(entry.getId(), entry);
		dispatcher.dispatch(new EventImpl(onOrderUpdated));
	}
	
	/**
	 * Обновить кэш статуса заявок.
	 * <p>
	 * @param entry кэш-запись статуса заявки
	 */
	public synchronized void update(OrderStatusEntry entry) {
		orderStatuses.put(entry.getId(), entry);
		dispatcher.dispatch(new EventImpl(onOrderStatusUpdated));
	}
	
	/**
	 * Обновить кэш позиции.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	public synchronized void update(PositionEntry entry) {
		positions.put(getPositionKey(entry), entry);
		dispatcher.dispatch(new EventImpl(onPositionUpdated));
	}
	
	/**
	 * Обновить кэш сделок.
	 * <p>
	 * @param entry кэш-запись сделки
	 */
	public synchronized void update(ExecEntry entry) {
		Long orderId = entry.getOrderId();
		Map<Long, ExecEntry> orderExecs = execs.get(orderId);
		if ( orderExecs == null ) {
			orderExecs = new LinkedHashMap<Long, ExecEntry>();
			execs.put(orderId, orderExecs);
		}
		orderExecs.put(entry.getId(), entry);
		dispatcher.dispatch(new EventImpl(onExecUpdated));
	}
	
	/**
	 * Удалить из кэша информацию о заявке.
	 * <p>
	 * Данный метод позволяет удалять информацию, после того, как заявка
	 * переводится в один из финальных статусов. 
	 * <p>
	 * @param id номер заявки
	 */
	public synchronized void purgeOrder(Long id) {
		orders.remove(id);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Cache.class ) {
			return false;
		}
		Cache o = (Cache) other;
		return new EqualsBuilder()
			.append(o.contracts, contracts)
			.append(o.dispatcher, dispatcher)
			.append(o.execs, execs)
			.append(o.onContractUpdated, onContractUpdated)
			.append(o.onExecUpdated, onExecUpdated)
			.append(o.onOrderStatusUpdated, onOrderStatusUpdated)
			.append(o.onOrderUpdated, onOrderUpdated)
			.append(o.onPositionUpdated, onPositionUpdated)
			.append(o.orders, orders)
			.append(o.orderStatuses, orderStatuses)
			.append(o.positions, positions)
			.isEquals();
	}

}
