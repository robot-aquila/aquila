package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определения неудачи при контроле заявки.
 */
public class OrderIsFailed implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsFailed() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(EditableOrder.STATUS_CHANGED)
			&& order.getStatus().isError();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsFailed.class;
	}

}
