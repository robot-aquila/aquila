package ru.prolib.aquila.ChaosTheory;

public class ServiceBuilderAttributeNotExistsException extends
		ServiceBuilderException {
	private static final long serialVersionUID = 1L;
	
	public ServiceBuilderAttributeNotExistsException(String attr) {
		super("Attribute not exists: " + attr);
	}

}
