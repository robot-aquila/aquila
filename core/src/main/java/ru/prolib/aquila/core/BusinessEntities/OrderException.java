package ru.prolib.aquila.core.BusinessEntities;

/**
 * Generic order exception.
 * <p>
 * 2012-10-16<br>
 * $Id: OrderException.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class OrderException extends EditableObjectException {
	private static final long serialVersionUID = 1L;
	
	public OrderException() {
		super();
	}
	
	public OrderException(String msg) {
		super(msg);
	}
	
	public OrderException(Throwable t) {
		super(t);
	}
	
	public OrderException(String msg, Throwable t) {
		super(msg, t);
	}

}
