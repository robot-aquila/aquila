package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.CoreException;

public class QFTransactionException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	public QFTransactionException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public QFTransactionException(String msg) {
		super(msg);
	}
	
	public QFTransactionException(Throwable t) {
		super(t);
	}
	
	public QFTransactionException() {
		
	}

}
