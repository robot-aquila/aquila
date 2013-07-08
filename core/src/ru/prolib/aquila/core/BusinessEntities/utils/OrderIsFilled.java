package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение момента исполнения заявки.
 */
public class OrderIsFilled implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsFilled() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.FILLED;
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsFilled.class;
	}
	
}
