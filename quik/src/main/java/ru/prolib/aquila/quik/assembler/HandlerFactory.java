package ru.prolib.aquila.quik.assembler;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Фабрика обработчиков.
 * <p>
 * Служебный класс.
 * Исключительно для разгрузки тестов процессора.
 */
class HandlerFactory {
	
	PlaceHandler createPlaceOrder(EditableOrder order) {
		return new PlaceHandler(order);
	}
	
	CancelHandler createCancelOrder(int transId, EditableOrder order) {
		return new CancelHandler(transId, order);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == HandlerFactory.class;
	}

}
