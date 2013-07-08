package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор определения неудачи процесса обработки заявки.
 * <p>
 * В настоящий момент не используется.
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
	public boolean validate(Object object) throws ValidatorException {
		if ( object instanceof  EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED) &&
				order.getStatus() == OrderStatus.REJECTED;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == OrderIsFailed.class;
	}

}
