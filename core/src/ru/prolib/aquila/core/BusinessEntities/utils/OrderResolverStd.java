package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
import ru.prolib.aquila.core.BusinessEntities.OrderException;

/**
 * Стандартный определитель заявки.
 * <p>
 * При наличии ожидающей заявки, перемещает заявку из очереди ожидания в список
 * зарегистрированных заявок. При наличии зарегистрированной заявки, возвращает
 * существующий экземпляр. Создает, регистрирует в хранилище и возвращает новый
 * экземпляр заявки в случае отсутствия. 
 * <p> 
 * 2012-12-14<br>
 * $Id: OrderResolverStd.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class OrderResolverStd implements OrderResolver {
	private final EditableOrders orders;
	private final OrderFactory factory;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param orders набор заявок
	 * @param factory фабрика заявок
	 */
	public OrderResolverStd(EditableOrders orders, OrderFactory factory) {
		super();
		this.orders = orders;
		this.factory = factory;
	}
	
	/**
	 * Получить хранилище заявок.
	 * <p>
	 * @return хранилище заявок
	 */
	public EditableOrders getOrders() {
		return orders;
	}
	
	/**
	 * Получить фабрику заявок.
	 * <p>
	 * @return фабрика заявок
	 */
	public OrderFactory getOrderFactory() {
		return factory;
	}
	
	@Override
	public EditableOrder resolveOrder(long orderId) {
		return resolveOrder(orderId, null);
	}

	@Override
	public EditableOrder resolveOrder(long orderId, Long transId) {
		try {
			EditableOrder order = null;
			if ( transId != null && orders.isPendingOrder(transId) ) {
				order = orders.getPendingOrder(transId);
				orders.purgePendingOrder(order);
				order.setId(orderId);
				orders.registerOrder(order);
			} else if ( ! orders.isOrderExists(orderId) ) {
				order = factory.createOrder();
				order.setId(orderId);
				orders.registerOrder(order);
			} else {
				order = orders.getEditableOrder(orderId);
			}
			return order;
		} catch ( OrderException e ) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderResolverStd.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		OrderResolverStd o = (OrderResolverStd) other;
		return new EqualsBuilder()
			.append(orders, o.orders)
			.append(factory, o.factory)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 3611)
			.append(orders)
			.append(factory)
			.toHashCode();
	}

}
