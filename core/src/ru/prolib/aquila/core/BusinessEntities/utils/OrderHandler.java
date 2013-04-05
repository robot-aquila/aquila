package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Интерфейс абстрактного обработчика заявки.
 * <p> 
 * 2012-09-25<br>
 * $Id: OrderHandler.java 283 2012-09-26 17:01:17Z whirlwind $
 */
public interface OrderHandler {

	/**
	 * Обработать заявку.
	 * <p>
	 * @param order заявка
	 */
	public void handle(EditableOrder order);

}