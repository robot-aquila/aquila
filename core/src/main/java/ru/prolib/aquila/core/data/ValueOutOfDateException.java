package ru.prolib.aquila.core.data;


/**
 * Исключение, сигнализирующее о недоступности значения. Например, при доступе
 * к историческим значениям был указан индекс существующего значения, но в
 * результате оптимизации данных значение по указанному индексу было удалено.
 * 
 * 2012-04-17
 * $Id: ValueOutOfDateException.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class ValueOutOfDateException extends ValueException {
	private static final long serialVersionUID = 1L;

	public ValueOutOfDateException(String msg) {
		super(msg);
	}
		
	public ValueOutOfDateException(Throwable t) {
		super(t);
	}

	public ValueOutOfDateException() {
		super();
	}

}
