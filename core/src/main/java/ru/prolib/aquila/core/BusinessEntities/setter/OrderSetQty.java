package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер количества заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetQty.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetQty implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetQty() {
		super();
	}

	/**
	 * Установить количество заявки.
	 * <p>
	 * Допустимые значение типа {@link java.lang.Long},
	 * {@link java.lang.Integer}, {@link java.lang.Double} и
	 * {@link java.lang.Float}. Вещественные значения приводятся к типу
	 * Long с потерей точности.   
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setQty((Long) value);
			} else if ( valueClass == Integer.class ) {
				object.setQty(((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				object.setQty(((Double) value).longValue());
			} else if ( valueClass == Float.class ) {
				object.setQty(((Float) value).longValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetQty.class ) {
			return true;
		} else {
			return false;
		}
	}

}
