package ru.prolib.aquila.ChaosTheory;

public class RiskManagerException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public RiskManagerException(String msg) {
		super(msg);
	}
	
	public RiskManagerException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public RiskManagerException(Throwable t) {
		super(t);
	}
	
	public RiskManagerException() {
		super();
	}
	
}
