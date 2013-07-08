package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение момента регистрации заявки в торговой системе.
 */
public class OrderIsRegistered implements OrderStateValidator {
	
	/**
	 * Конструтор.
	 */
	public OrderIsRegistered() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.ACTIVE;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsRegistered.class;
	}

}
