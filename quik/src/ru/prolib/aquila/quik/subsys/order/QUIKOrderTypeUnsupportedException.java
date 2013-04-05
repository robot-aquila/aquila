package ru.prolib.aquila.quik.subsys.order;

import ru.prolib.aquila.core.BusinessEntities.OrderType;

/**
 * Тип заявки не поддерживается.
 * <p>
 * 2013-01-30<br>
 * $Id: QUIKOrderTypeUnsupportedException.java 462 2013-01-30 17:37:31Z whirlwind $
 */
public class QUIKOrderTypeUnsupportedException extends RuntimeException {
	private static final long serialVersionUID = 9026412473601766466L;
	
	public QUIKOrderTypeUnsupportedException(OrderType type) {
		super("Order type unsupported: " + type);
	}

}
