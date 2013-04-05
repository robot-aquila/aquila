package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: получена информация о заявке.
 * <p>
 * @link <a href="http://www.interactivebrokers.com/en/software/api/apiguide/java/openorder.htm">openOrder</a>
 * 
 * <p>
 * 2012-12-11<br>
 * $Id: IBEventOpenOrder.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOpenOrder extends IBEventOrder {
	private final Contract contract;
	private final Order order;
	private final OrderState orderState;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param orderId номер заявки
	 * @param contract контракт
	 * @param order заявка
	 * @param orderState детали заявки
	 */
	public IBEventOpenOrder(EventType type, int orderId, Contract contract,
			Order order, OrderState orderState)
	{
		super(type, orderId);
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
	}
	
	/**
	 * Получить номер контракта.
	 * <p>
	 * @return номер контракта
	 */
	public int getContractId() {
		return contract.m_conId;
	}
	
	/**
	 * Получить контракт.
	 * <p>
	 * @return контракт
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Получить заявку.
	 * <p>
	 * @return заявка
	 */
	public Order getOrder() {
		return order;
	}
	
	/**
	 * Получить детали заявки.
	 * <p>
	 * @return детали заявки
	 */
	public OrderState getOrderState() {
		return orderState;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121211, 115527)
			.append(getType())
			.append(getOrderId())
			.append(contract)
			.append(order)
			.append(orderState)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBEventOpenOrder.class ?
				fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBEventOpenOrder o = (IBEventOpenOrder) other;
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(contract, o.contract)
			.append(order, o.order)
			.append(orderState, o.orderState)
			.isEquals();
	}

}
