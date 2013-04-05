package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор определения момента отмены заявки.
 * <p>
 * 2012-09-24<br>
 * $Id: OrderIsCancelled.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsCancelled implements Validator {
	
	/**
	 * Создать валидатор.
	 */
	public OrderIsCancelled() {
		super();
	}

	@Override
	public boolean validate(Object object) {
		if ( object instanceof EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED)
				&& order.getStatus() == OrderStatus.CANCELLED;			
		}
		return false;
	}

}
