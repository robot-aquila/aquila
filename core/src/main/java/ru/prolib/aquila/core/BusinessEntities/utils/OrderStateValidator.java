package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Интерфейс проверки состояния заявки.
 */
public interface OrderStateValidator {
	
	/**
	 * Проверить состояние заявки.
	 * <p>
	 * @param order экземпляр заявки
	 * @return true - заявка удовлетворяет условиям, false - не удовлетворяет
	 */
	public boolean validate(EditableOrder order);

}
