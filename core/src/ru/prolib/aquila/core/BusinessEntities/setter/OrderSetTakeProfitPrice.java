package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер тэйк-профит цены заявки.
 * <p>
 * 2012-10-25<br>
 * $Id: OrderSetTakeProfitPrice.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetTakeProfitPrice implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetTakeProfitPrice() {
		super();
	}

	/**
	 * Установить тэйк-профит цену заявки.
	 * <p>
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null && value.getClass() == Double.class ) {
			object.setTakeProfitPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null
		  && other.getClass() == OrderSetTakeProfitPrice.class )
		{
			return true;
		} else {
			return false;
		}
	}

}
