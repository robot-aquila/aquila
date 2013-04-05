package ru.prolib.aquila.ChaosTheory;

public class ServiceBuilderFormatException extends ServiceBuilderException {
	private static final long serialVersionUID = 1L;
	
	public ServiceBuilderFormatException(Throwable t) {
		super("Value format exception", t);
	}
	
	public ServiceBuilderFormatException(String attr, Throwable t) {
		super("Attribute format exception: " + attr, t);
	}
	
}
