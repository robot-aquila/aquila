package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер цены заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetPrice.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetPrice implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetPrice() {
		super();
	}

	/**
	 * Установить цену заявки.
	 * <p>
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == Double.class ) {
			object.setPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderSetPrice.class; 
	}

}
