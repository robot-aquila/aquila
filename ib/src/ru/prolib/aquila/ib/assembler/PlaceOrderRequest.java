package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.ib.client.*;

/**
 * Данные запроса на регистрацию заявки.
 * <p>
 * Объекты данного класса инкапсулируют атрибуты запроса на выставление заявки
 * через IB API. Используется в качестве объекта запроса в соответствующей
 * транзакции. Никаких действий, кроме хранения, не выполняет. 
 */
public class PlaceOrderRequest {
	private final Contract contract;
	private final Order order;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param contract дескриптор контракта
	 * @param order дескриптор заявки
	 */
	public PlaceOrderRequest(Contract contract, Order order) {
		super();
		this.contract = contract;
		this.order = order;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Дескриптор заявки создается автоматически.
	 * <p>
	 * @param contract дескриптор контракта
	 */
	public PlaceOrderRequest(Contract contract) {
		this(contract, new Order());
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public int getOrderId() {
		return order.m_orderId;
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
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PlaceOrderRequest.class ) {
			return false;
		}
		PlaceOrderRequest o = (PlaceOrderRequest) other;
		return new EqualsBuilder()
			.append(o.contract, contract)
			.append(o.order, order)
			.isEquals();
	}

}
