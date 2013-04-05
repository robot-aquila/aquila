package ru.prolib.aquila.ChaosTheory;

public class PropsFormatException extends PropsException {
	private static final long serialVersionUID = 1L;

	public PropsFormatException(String propname, Throwable t) {
		super("Property format exception: " + propname, t);
	}
	
}
