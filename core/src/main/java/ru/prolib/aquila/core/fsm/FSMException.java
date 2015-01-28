package ru.prolib.aquila.core.fsm;

/**
 * Базовое исключение КА.
 */
public class FSMException extends Exception {
	private static final long serialVersionUID = -7946963366350084947L;
	
	public FSMException() {
		super();
	}
	
	public FSMException(String msg) {
		super(msg);
	}
	
	public FSMException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public FSMException(Throwable t) {
		super(t);
	}

}
