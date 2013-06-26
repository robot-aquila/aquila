package ru.prolib.aquila.ib.assembler.cache;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;

import com.ib.client.*;

/**
 * Кэш-запись основной части заявки.
 * <p>
 * Инкапсулирует данные, полученные через метод openOrder. 
 */
public class OrderEntry extends CacheEntry {
	private static final Map<String, OrderDirection> dirs;
	private static final Map<String, OrderType> types;
	private static final Map<String, OrderStatus> statuses;
	
	static {
		dirs = new Hashtable<String, OrderDirection>();
		dirs.put("BUY", OrderDirection.BUY);
		dirs.put("SELL", OrderDirection.SELL);
		types = new Hashtable<String, OrderType>();
		types.put("STP LMT", OrderType.STOP_LIMIT);
		types.put("MKT", OrderType.MARKET);
		types.put("LMT", OrderType.LIMIT);
		statuses = new Hashtable<String, OrderStatus>();
		//statuses.put("PendingSubmit", OrderStatus.PENDING);
		//statuses.put("PendingCancel", OrderStatus.ACTIVE);
		//statuses.put("PreSubmitted", OrderStatus.PENDING);
		statuses.put("Submitted", OrderStatus.ACTIVE);
		statuses.put("Cancelled", OrderStatus.CANCELLED);
		statuses.put("Filled", OrderStatus.FILLED);
		//statuses.put("Inactive", OrderStatus.PENDING);
	}
	
	private final Long id;
	private final Contract contract;
	private final Order order;
	private final OrderState state;
	
	public OrderEntry(int id, Contract contr, Order order, OrderState state) {
		super();
		this.id = new Long(id);
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
	public Long getId() {
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
	public OrderDirection getDirection() {
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
		OrderType type = types.get(order.m_orderType);
		return type == null ? OrderType.OTHER : type;
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

}
