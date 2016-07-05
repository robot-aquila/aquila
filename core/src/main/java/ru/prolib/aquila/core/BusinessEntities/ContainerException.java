package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.CoreException;

public class ContainerException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	public ContainerException() {
		super();
	}
	
	public ContainerException(Throwable t) {
		super(t);
	}
	
	public ContainerException(String msg) {
		super(msg);
	}
	
	public ContainerException(String msg, Throwable t) {
		super(msg, t);
	}

}
