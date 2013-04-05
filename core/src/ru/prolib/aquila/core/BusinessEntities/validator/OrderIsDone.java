package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор определения момента завершения заявки.
 * <p>
 * 2012-09-24<br>
 * $Id: OrderIsDone.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsDone implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsDone() {
		super();
	}

	@Override
	public boolean validate(Object object) {
		if ( object instanceof  EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED) &&
				(order.getStatus() == OrderStatus.CANCELLED
			  || order.getStatus() == OrderStatus.FILLED);
		}
		return false;
	}

}
