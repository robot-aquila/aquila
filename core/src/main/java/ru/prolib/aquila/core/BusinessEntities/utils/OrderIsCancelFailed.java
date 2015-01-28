package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение неудачи процесса отмены заявки.
 */
public class OrderIsCancelFailed implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsCancelFailed() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(EditableOrder.STATUS_CHANGED)
			&& order.getStatus() == OrderStatus.CANCEL_FAILED;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsCancelFailed.class;
	}

}
