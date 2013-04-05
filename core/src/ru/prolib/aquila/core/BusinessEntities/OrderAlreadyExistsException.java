package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если заявка с таким идентификатором уже
 * существует.
 * <p>
 * 2012-10-17<br>
 * $Id: OrderAlreadyExistsException.java 562 2013-03-06 15:22:54Z whirlwind $
 */
public class OrderAlreadyExistsException extends OrderException {
	private static final long serialVersionUID = 1L;

	public OrderAlreadyExistsException(long orderId) {
		super("Order id# " + orderId);
	}

}
