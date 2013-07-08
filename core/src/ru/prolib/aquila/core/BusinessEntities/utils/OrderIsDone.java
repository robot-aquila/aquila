package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Определение момента финализации заявки.
 */
public class OrderIsDone implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsDone() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged(OrderImpl.STATUS_CHANGED)
			&& order.getStatus().isFinal();	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsDone.class;
	}

}
