package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер стоп-лимит цены заявки.
 * <p>
 * 2012-10-24<br>
 * $Id: OrderSetStopLimitPrice.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetStopLimitPrice implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetStopLimitPrice() {
		super();
	}

	/**
	 * Установить стоп-лимит цену заявки.
	 * <p>
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Double.class ) {
			object.setStopLimitPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null
		  && other.getClass() == OrderSetStopLimitPrice.class )
		{
			return true;
		} else {
			return false;
		}
	}

}
