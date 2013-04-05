package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер неисполненного количества заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetQtyRest.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetQtyRest implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetQtyRest() {
		super();
	}

	/**
	 * Установить неисполненное количество заявки.
	 * <p>
	 * Допустимые значение типа {@link java.lang.Long},
	 * {@link java.lang.Integer}, {@link java.lang.Double} и
	 * {@link java.lang.Float}. Вещественные значения приводятся к типу
	 * Long с потерей точности.   
	 */
	@Override
	public void set(EditableOrder object, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass();
			if ( valueClass == Long.class ) {
				object.setQtyRest((Long) value);
			} else if ( valueClass == Integer.class ) {
				object.setQtyRest(((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				object.setQtyRest(((Double) value).longValue());
			} else if ( valueClass == Float.class ) {
				object.setQtyRest(((Float) value).longValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderSetQtyRest.class;
	}

}
