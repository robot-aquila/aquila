package ru.prolib.aquila.core.data;


/**
 * Исключение, сигнализирующее о запросе исторического значения, находящегося
 * за пределами доступного диапазона.
 * 
 * 2012-04-17
 * $Id: ValueOutOfRangeException.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class ValueOutOfRangeException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueOutOfRangeException(String msg) {
		super(msg);
	}
	
	public ValueOutOfRangeException(Throwable t) {
		super(t);
	}
	
	public ValueOutOfRangeException(String msg, Throwable t) {
		super(msg, t);
	}

	public ValueOutOfRangeException() {
		super();
	}

}
