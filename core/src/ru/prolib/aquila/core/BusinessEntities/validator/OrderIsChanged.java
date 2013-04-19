package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор определения момента изменения заявки.
 * <p>
 * 2012-09-24<br>
 * $Id: OrderIsChanged.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsChanged implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsChanged() {
		super();
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		if ( object instanceof EditableOrder ) {
			EditableOrder order = (EditableOrder) object;
			return order.hasChanged();
		}
		return false;
	}

}
