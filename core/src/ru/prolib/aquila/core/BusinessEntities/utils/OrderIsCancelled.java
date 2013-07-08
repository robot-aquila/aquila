package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение момента отмены заявки.
 */
public class OrderIsCancelled implements OrderStateValidator {
	
	/**
	 * Создать валидатор.
	 */
	public OrderIsCancelled() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.CANCELLED;			
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsCancelled.class;
	}

}
