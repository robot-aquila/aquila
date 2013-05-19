package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор определения момента частичного исполнения заявки.
 * <p>
 * Заявка считается частично исполненной при смене статуса на
 * {@link OrderStatus#CANCELLED} и ненулевом, отличном от начального
 * неисполненном остатке.  
 * <p>
 * 2012-09-25<br>
 * $Id: OrderIsPartiallyFilled.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsPartiallyFilled implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsPartiallyFilled() {
		super();
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		if ( object instanceof EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged(OrderImpl.STATUS_CHANGED)
				&& order.getQtyRest() != null && order.getQtyRest() > 0
				&& order.getQtyRest() < order.getQty()
				&& order.getStatus() == OrderStatus.CANCELLED;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass()==OrderIsPartiallyFilled.class;
	}

}
