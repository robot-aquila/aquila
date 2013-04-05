package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Price;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер защитного спрэда тэйк-профита.
 * <p>
 * 2012-10-25<br>
 * $Id: OrderSetSpread.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetSpread implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetSpread() {
		super();
	}

	/**
	 * Установить защитный спрэд тэйк-профита.
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Price.class ) {
			object.setSpread((Price) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetSpread.class ) {
			return true;
		} else {
			return false;
		}
	}

}
