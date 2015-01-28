package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер статуса заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetStatus.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetStatus implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetStatus() {
		super();
	}

	/**
	 * Установить статус заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == OrderStatus.class ) {
			object.setStatus((OrderStatus) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetStatus.class ) {
			return true;
		} else {
			return false;
		}
	}

}
