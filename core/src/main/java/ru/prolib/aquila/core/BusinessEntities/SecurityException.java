package ru.prolib.aquila.core.BusinessEntities;

/**
 * Общее исключение торгового инструмента.
 */
public class SecurityException extends EditableObjectException {
	private static final long serialVersionUID = 1L;
	
	public SecurityException() {
		super();
	}
	
	public SecurityException(String msg) {
		super(msg);
	}
	
	public SecurityException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SecurityException(Throwable t) {
		super(t);
	}

}
