package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер направления заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetDirection.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetDirection implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetDirection() {
		super();
	}

	/**
	 * Установить направление заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == Direction.class ) {
			object.setDirection((Direction) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderSetDirection.class;
	}

}
