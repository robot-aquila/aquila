package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение отклонения регистрации заявки.
 */
public class OrderIsRegisterFailed implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsRegisterFailed() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.REJECTED;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsRegisterFailed.class;
	}

}
