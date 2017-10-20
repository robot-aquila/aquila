package ru.prolib.aquila.core.sm;

/**
 * Исключение времени исполнения автомата.
 */
public class SMRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -4660340311756794198L;
	
	public SMRuntimeException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SMRuntimeException(String msg) {
		super(msg);
	}
	
	public SMRuntimeException(Throwable t) {
		super(t);
	}
	
	public SMRuntimeException() {
		super();
	}

}
