package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;

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
	public boolean validate(Object object) throws ValidatorException {
		if ( object instanceof  EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED) &&
				order.getStatus().isFinal();
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsDone.class;
	}

}
