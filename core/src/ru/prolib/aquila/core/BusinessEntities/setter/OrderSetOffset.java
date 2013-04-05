package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер сдвиг цены тэйк-профита.
 * <p>
 * 2012-10-25<br>
 * $Id: OrderSetOffset.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetOffset implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetOffset() {
		super();
	}

	/**
	 * Установить сдвиг цены тэйк-профита.
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Price.class ) {
			object.setOffset((Price) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetOffset.class ) {
			return true;
		} else {
			return false;
		}
	}

}
