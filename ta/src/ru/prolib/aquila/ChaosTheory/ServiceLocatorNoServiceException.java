package ru.prolib.aquila.ChaosTheory;

public class ServiceLocatorNoServiceException extends
		ServiceLocatorException {
	private static final long serialVersionUID = 1L;
	
	public ServiceLocatorNoServiceException(String serviceName) {
		super("Service not available: " + serviceName);
	}

}
