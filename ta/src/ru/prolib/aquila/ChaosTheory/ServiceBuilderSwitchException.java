package ru.prolib.aquila.ChaosTheory;

public class ServiceBuilderSwitchException extends ServiceBuilderException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Конструктор.
	 * @param variant
	 */
	public ServiceBuilderSwitchException(String variant) {
		super("Unsupported variant: " + variant);
	}

}
