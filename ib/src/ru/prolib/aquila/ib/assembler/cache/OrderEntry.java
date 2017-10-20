package ru.prolib.aquila.ib.assembler.cache;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import com.ib.client.*;
import com.ib.client.Order;

/**
 * Кэш-запись основной части заявки.
 * <p>
 * Инкапсулирует данные, полученные через метод openOrder. 
 */
public class OrderEntry extends CacheEntry {
	private static final Map<String, Direction> dirs;
	private static final Map<String, OrderType> types;
	private static final Map<String, OrderStatus> statuses;
	
	static {
		dirs = new Hashtable<String, Direction>();
		dirs.put("BUY", Direction.BUY);
		dirs.put("SELL", Direction.SELL);
		types = new Hashtable<String, OrderType>();
		types.put("MKT", OrderType.MARKET);
		types.put("LMT", OrderType.LIMIT);
		statuses = new Hashtable<String, OrderStatus>();
		// All the documented IB statuses
		statuses.put("PendingSubmit", OrderStatus.SENT);
		statuses.put("PendingCancel", OrderStatus.CANCEL_SENT);
		statuses.put("PreSubmitted", OrderStatus.ACTIVE);
		statuses.put("Submitted", OrderStatus.ACTIVE);
		statuses.put("Cancelled", OrderStatus.CANCELLED);
		statuses.put("Filled", OrderStatus.FILLED);
		statuses.put("Inactive", OrderStatus.ACTIVE);
	}
	
	private final int id;
	private final Contract contract;
	private final Order order;
	private final OrderState state;
	
	public OrderEntry(int id, Contract contr, Order order, OrderState state) {
		super();
		this.id = id;
		this.contract = contr;
		this.order = order;
		this.state = state;
	}
	
	/**
	 * Получить идентификатор контракта.
	 * <p>
	 * @return идентификатор контракта
	 */
	public int getContractId() {
		return contract.m_conId;
	}
	
	/**
	 * Получить дескриптор контракта.
	 * <p>
	 * @return дескриптор контракта
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Получить дескриптор заявки.
	 * <p>
	 * @return дескриптор заявки
	 */
	public Order getOrder() {
		return order;
	}
	
	/**
	 * Получить дескриптор статуса заявки.
	 * <p>
	 * @return дескриптор статуса заявки
	 */
	public OrderState getOrderState() {
		return state;
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Получить счет заявки.
	 * <p>
	 * @return счет
	 */
	public Account getAccount() {
		return new Account(order.m_account);
	}
	
	/**
	 * Получить направление заявки.
	 * <p>
	 * @return направление заявки
	 */
	public Direction getDirection() {
		return dirs.get(order.m_action);
	}
	
	/**
	 * Получить цену заявки.
	 * <p>
	 * @return цена заявки
	 */
	public Double getPrice() {
		return order.m_lmtPrice == 0d ? null : order.m_lmtPrice;
	}
	
	/**
	 * Получить стоп-лимит цену.
	 * <p>
	 * @return стоп-цена заявки
	 */
	public Double getStopLimitPrice() {
		return order.m_auxPrice == 0d ? null : order.m_auxPrice;
	}
	
	/**
	 * Получить количество заявки.
	 * <p>
	 * @return количество заявки
	 */
	public Long getQty() {
		return new Long(order.m_totalQuantity);
	}
	
	/**
	 * Получить тип заявки.
	 * <p>
	 * @return тип заявки
	 */
	public OrderType getType() {
		return types.get(order.m_orderType);
	}
	
	/**
	 * Получить статус заявки.
	 * <p>
	 * @return статус или null, если не удалось определить статус
	 */
	public OrderStatus getStatus() {
		return convertStatus(state.m_status);
	}
	
	/**
	 * Конвертировать IB статус в локальный статус заявки.
	 * <p>
	 * @param status строковый IB-статус заявки
	 * @return локальный статус заявки или null, если не удалось конвертировать
	 */
	public static OrderStatus convertStatus(String status) {
		return statuses.get(status);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderEntry.class ) {
			return false;
		}
		OrderEntry o = (OrderEntry) other;
		return new EqualsBuilder()
			.append(o.contract, contract)
			.append(o.id, id)
			.append(o.order, order)
			.append(o.state, state)
			.isEquals();
	}
	
	/**
	 * Проверить отношение кэш-записи к стоп-заявке.
	 * <p>
	 * @return true - стоп-заявка, false - обычная заявка
	 */
	public boolean isKnownType() {
		return getType() != null;
	}

}
