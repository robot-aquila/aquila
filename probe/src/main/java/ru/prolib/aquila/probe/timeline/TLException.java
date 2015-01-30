package ru.prolib.aquila.probe.timeline;

/**
 * Базовое исключение временной шкалы.
 */
public class TLException extends Exception {
	private static final long serialVersionUID = 296435232118445068L;
	
	public TLException() {
		super();
	}
	
	public TLException(String msg) {
		super(msg);
	}
	
	public TLException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TLException(Throwable t) {
		super(t);
	}

}
