package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;

/**
 * Интерфейс определителя заявки.
 * <p>
 * 2012-12-14<br>
 * $Id: OrderResolver.java 338 2012-12-15 10:20:43Z whirlwind $
 */
public interface OrderResolver {
	
	/**
	 * Определить экземпляр заявки.
	 * <p>
	 * @param id номер заявки
	 * @param transId номер транзакции
	 * @return экземпляр заявки
	 */
	public EditableOrder resolveOrder(long id, Long transId);
	
	/**
	 * Определить экземпляр заявки.
	 * <p>
	 * @param id номер заявки
	 * @return экземпляр заявки
	 */
	public EditableOrder resolveOrder(long id);

}
