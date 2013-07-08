package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор определения неудачи процесса обработки заявки.
 * <p>
 * 2012-09-23<br>
 * $Id: OrderIsFailed.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class OrderIsFailed implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsFailed() {
		super();
	}

	@Override
	public boolean validate(Object object) {
		if ( object instanceof  EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED) &&
				order.getStatus().isError();
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsFailed.class;
	}

}
