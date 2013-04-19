package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер идентификатора транзакции заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetTransactionId.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetTransactionId implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetTransactionId() {
		super();
	}

	/**
	 * Установить идентификатор транзакции заявки.
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
				object.setTransactionId((Long) value);
			} else if ( valueClass == Integer.class ) {
				object.setTransactionId(((Integer) value).longValue());
			} else if ( valueClass == Double.class ) {
				object.setTransactionId(((Double) value).longValue());
			} else if ( valueClass == Float.class ) {
				object.setTransactionId(((Float) value).longValue());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderSetTransactionId.class;
	}

}
