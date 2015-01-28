package ru.prolib.aquila.core.BusinessEntities;

/**
 * Исключение, выбрасываемое в случае, если указанная заявка не найдена.
 * <p>
 * 2012-10-17<br>
 * $Id: OrderNotExistsException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class OrderNotExistsException extends OrderException {
	private static final long serialVersionUID = 1L;

	public OrderNotExistsException(long id) {
		super("Order not exists: " + id);
	}
	
}
