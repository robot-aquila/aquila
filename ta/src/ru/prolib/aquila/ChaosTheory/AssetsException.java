package ru.prolib.aquila.ChaosTheory;

public class AssetsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public AssetsException() {
		super();
	}

	public AssetsException(String msg) {
		super(msg);
	}

	public AssetsException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public AssetsException(Throwable t) {
		super(t);
	}

}
