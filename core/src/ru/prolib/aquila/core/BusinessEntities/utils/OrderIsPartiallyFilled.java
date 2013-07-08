package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение момента частичного исполнения заявки.
 */
public class OrderIsPartiallyFilled implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsPartiallyFilled() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.CANCELLED
			&& order.getQtyRest() > 0;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass()==OrderIsPartiallyFilled.class;
	}

}
