package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер номера связанной заявки.
 * <p>
 * 2012-10-24<br>
 * $Id: OrderSetLinkedOrderId.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetLinkedOrderId implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetLinkedOrderId() {
		super();
	}

	/**
	 * Установить идентификатор связанной заявки.
	 * <p>
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Long.class ) {
			object.setLinkedOrderId((Long) value);
		}		
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null
		  && other.getClass() == OrderSetLinkedOrderId.class )
		{
			return true;
		} else {
			return false;
		}
	}

}
