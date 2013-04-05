package ru.prolib.aquila.core.BusinessEntities.validator;

import ru.prolib.aquila.core.utils.Validator;

/**
 * Валидатор определения неудачи процесса отмены заявки.
 * <p>
 * В настоящий момент не используется.
 * <p>
 * 2012-09-23<br>
 * $Id: OrderIsCancelFailed.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderIsCancelFailed implements Validator {
	
	/**
	 * Конструктор.
	 */
	public OrderIsCancelFailed() {
		super();
	}

	@Override
	public boolean validate(Object object) {
		return false;
	}

}
