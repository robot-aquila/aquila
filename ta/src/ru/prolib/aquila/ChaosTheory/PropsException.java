package ru.prolib.aquila.ChaosTheory;

public class PropsException extends Exception {
	private static final long serialVersionUID = 1L;

	public PropsException() {
		super();
	}
	
	public PropsException(String msg) {
		super(msg);
	}
	
	public PropsException(Throwable t) {
		super(t);
	}
	
	public PropsException(String msg, Throwable t) {
		super(msg, t);
	}

}
