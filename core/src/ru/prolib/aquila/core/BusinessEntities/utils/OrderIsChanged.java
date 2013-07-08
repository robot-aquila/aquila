package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Определение момента изменения заявки.
 */
public class OrderIsChanged implements OrderStateValidator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsChanged() {
		super();
	}

	@Override
	public boolean validate(EditableOrder order) {
		return order.hasChanged();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsChanged.class;
	}

}
