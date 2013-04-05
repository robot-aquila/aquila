package ru.prolib.aquila.ta;

/**
 * Исключение, возбуждающееся в случае запроса значения, находящегося за
 * пределами доступного диапазона.
 */
public class ValueOutOfRangeException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueOutOfRangeException(String msg) {
		super(msg);
	}
	
	public ValueOutOfRangeException(Throwable t) {
		super(t);
	}

	public ValueOutOfRangeException() {
		super();
	}

}
