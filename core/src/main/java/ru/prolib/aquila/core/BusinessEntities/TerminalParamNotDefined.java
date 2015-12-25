package ru.prolib.aquila.core.BusinessEntities;

public class TerminalParamNotDefined extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TerminalParamNotDefined(String paramName) {
		super("Terminal parameter not defined: " + paramName);
	}

}
