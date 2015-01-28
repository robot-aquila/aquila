package ru.prolib.aquila.core.BusinessEntities;

/**
 * Общее исключение терминала.
 */
public class TerminalException extends EditableObjectException {
	private static final long serialVersionUID = 1L;
	
	public TerminalException() {
		super();
	}
	
	public TerminalException(String msg) {
		super(msg);
	}
	
	public TerminalException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TerminalException(Throwable t) {
		super(t);
	}

}
