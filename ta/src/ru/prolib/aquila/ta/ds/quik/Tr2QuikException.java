package ru.prolib.aquila.ta.ds.quik;

/**
 * Базовое исключение драйвера.
 */
public class Tr2QuikException extends Exception {
	private static final long serialVersionUID = 7928288815194515307L;

	public Tr2QuikException() {
		super();
	}
	
	public Tr2QuikException(String msg) {
		super(msg);
	}
	
	public Tr2QuikException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public Tr2QuikException(Throwable t) {
		super(t);
	}
	
}