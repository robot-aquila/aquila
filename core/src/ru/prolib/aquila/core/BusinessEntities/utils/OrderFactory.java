package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Интерфейс фабрики заявок.
 * <p>
 * 2012-10-17<br>
 * $Id: OrderFactory.java 334 2012-12-11 03:45:38Z whirlwind $
 */
@Deprecated
public interface OrderFactory {
	
	/**
	 * Создать экземпляр заявки.
	 * <p>
	 * @return экземпляр заявки
	 */
	public EditableOrder createOrder();

}
