package ru.prolib.aquila.ChaosTheory;

public class PropsNotExistsException extends PropsException {
	private static final long serialVersionUID = 1L;

	public PropsNotExistsException(String propname) {
		super("Property not exists: " + propname);
	}

}
