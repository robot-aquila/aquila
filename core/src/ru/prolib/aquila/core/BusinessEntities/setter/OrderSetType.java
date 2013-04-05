package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер типа заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetType.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetType implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetType() {
		super();
	}

	/**
	 * Установить тип заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == OrderType.class ) {
			object.setType((OrderType) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderSetType.class;
	}

}
