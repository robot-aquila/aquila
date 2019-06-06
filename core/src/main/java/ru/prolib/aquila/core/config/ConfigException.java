package ru.prolib.aquila.core.config;

import ru.prolib.aquila.core.CoreException;

public class ConfigException extends CoreException {
	private static final long serialVersionUID = -6982416640822636932L;
	
	public ConfigException() {
		
	}
	
	public ConfigException(String msg) {
		super(msg);
	}
	
	public ConfigException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public ConfigException(Throwable t) {
		super(t);
	}

}
