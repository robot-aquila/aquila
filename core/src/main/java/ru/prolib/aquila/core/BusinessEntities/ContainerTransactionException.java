package ru.prolib.aquila.core.BusinessEntities;

class ContainerTransactionException extends ContainerException {
	private static final long serialVersionUID = 1L;
	
	public ContainerTransactionException() {
		super();
	}
	
	public ContainerTransactionException(Throwable t) {
		super(t);
	}
	
	public ContainerTransactionException(String msg) {
		super(msg);
	}
	
	public ContainerTransactionException(String msg, Throwable t) {
		super(msg, t);
	}

}
